#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单的标签搜索测试程序
用于测试ChromaDB向量存储和搜索功能
"""

import os
import chromadb
from dotenv import load_dotenv
import requests
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity as sklearn_cosine_similarity # Renamed to avoid conflict
from collections import Counter
from dotenv import load_dotenv
# LM Studio API Configuration
LM_STUDIO_API_URL = "http://127.0.0.1:1234/v1/embeddings"
LM_STUDIO_MODEL_NAME = "text-embedding-qwen3-reranker-4b"

# 加载环境变量
load_dotenv()

class OllamaEmbeddingFunction:
    def __init__(self, api_base, model_name):
        self.api_base = api_base
        self.model_name = model_name

    def __call__(self, input):
        if not input:
            return []
        
        # 确保input是列表格式
        if isinstance(input, str):
            input = [input]
        
        texts = input
        
        embeddings = []
        
        # 逐个处理每个文本
        for text in texts:
            headers = {"Content-Type": "application/json"}
            payload = {"prompt": text, "model": self.model_name}
            
            try:
                response = requests.post(f"{self.api_base}/embeddings", headers=headers, json=payload)
                response.raise_for_status()
                
                json_response = response.json()
                if 'embedding' in json_response:
                    embeddings.append(json_response['embedding'])
                else:
                    print(f"警告：未找到嵌入数据，响应: {json_response}")
                    # 返回一个默认的嵌入向量（全零向量）
                    embeddings.append([0.0] * 384)  # 假设384维
            except Exception as e:
                print(f"调用Ollama API失败: {e}")
                # 返回一个默认的嵌入向量
                embeddings.append([0.0] * 384)
        
        return embeddings

def test_search():
    """测试搜索功能（带重排模型）"""
    print("=== 开始测试标签搜索功能（使用重排模型）===")
    
    # 初始化ChromaDB客户端
    client = chromadb.PersistentClient(path="tags_vector_db/")
    
    # 从环境变量获取配置
    api_base = os.getenv('OPENAI_API_BASE', 'http://localhost:11434/api')
    model_name = os.getenv('OPENAI_EMBEDDING_MODEL_NAME', 'qwen3-embedding-0.6b:latest')
    
    # 创建嵌入函数
    embedding_func = OllamaEmbeddingFunction(api_base, model_name)
    # LM Studio Reranker (via embeddings) is used directly in the search loop
    
    try:
        # 获取已存在的集合
        collection = client.get_collection(name="tags_collection", embedding_function=embedding_func)
        print(f"成功连接到集合，包含 {collection.count()} 个标签")
        
        # 测试查询列表
        test_queries = [
            "电脑",
            "手机",
            "数学",
            "书籍",
            "运动",
            "音乐",
            "游戏",
            "学习"
        ]
        
        print("\n=== 开始搜索测试 ===")
        for query in test_queries:
            print(f"\n搜索查询: '{query}'")
            try:
                # 执行相似性搜索（获取更多结果用于重排）
                results = collection.query(
                    query_texts=[query],
                    n_results=10  # 获取更多结果用于重排
                )
                
                if results['documents'] and results['documents'][0]:
                    print("\n原始搜索结果:")
                    for i, (doc, distance) in enumerate(zip(results['documents'][0][:3], results['distances'][0][:3])):
                        print(f"  {i+1}. {doc} (嵌入相似度: {1-distance:.3f})")
                    
                    # 使用 LM Studio Embeddings API 进行重排
                    docs_to_rerank = results['documents'][0]
                    query_embedding_for_rerank = get_lm_studio_embedding(query, LM_STUDIO_MODEL_NAME, LM_STUDIO_API_URL)
                    rerank_scores = []
                    if query_embedding_for_rerank:
                        for doc_text in docs_to_rerank:
                            doc_embedding_for_rerank = get_lm_studio_embedding(doc_text, LM_STUDIO_MODEL_NAME, LM_STUDIO_API_URL)
                            if doc_embedding_for_rerank:
                                score = cosine_similarity_local(query_embedding_for_rerank, doc_embedding_for_rerank)
                                rerank_scores.append(score)
                            else:
                                rerank_scores.append(0.0) # Assign low score if embedding fails
                    else:
                        print("  无法获取查询的 LM Studio 嵌入，跳过重排。")
                        rerank_scores = [0.0] * len(docs_to_rerank) # Assign all zero if query embedding fails
                    
                    if rerank_scores:
                        # 将分数与文档关联并排序
                        reranked_items = []
                        for doc, score, original_distance in zip(docs_to_rerank, rerank_scores, results['distances'][0]):
                            reranked_items.append({
                                'document': doc,
                                'original_embedding_similarity': 1 - original_distance, # 原始嵌入相似度 (来自ChromaDB)
                                'rerank_score': score, # 新的重排分数 (来自LM Studio)
                                # 'final_score' will be replaced by RRF score later
                                'doc_id': doc # Keep track of document for RRF
                            })
                        
                        # Calculate RRF scores
                        # First, get ranks for original similarity
                        original_ranked_docs = sorted(reranked_items, key=lambda x: x['original_embedding_similarity'], reverse=True)
                        original_ranks = {item['doc_id']: i + 1 for i, item in enumerate(original_ranked_docs)}

                        # Second, get ranks for rerank scores
                        rerank_model_ranked_docs = sorted(reranked_items, key=lambda x: x['rerank_score'], reverse=True)
                        rerank_model_ranks = {item['doc_id']: i + 1 for i, item in enumerate(rerank_model_ranked_docs)}

                        k_rrf = 20 # RRF k parameter
                        for item in reranked_items:
                            rank1 = original_ranks.get(item['doc_id'], len(reranked_items) + 1) # Default to a large rank if not found
                            rank2 = rerank_model_ranks.get(item['doc_id'], len(reranked_items) + 1)
                            item['rrf_score'] = (1 / (k_rrf + rank1)) + (1 / (k_rrf + rank2))
                        
                        # Sort by RRF score
                        reranked_items.sort(key=lambda x: x['rrf_score'], reverse=True)
                        
                        print("\n重排后结果 (使用 RRF 融合):")
                        for i, item in enumerate(reranked_items[:5]):
                            print(f"  {i+1}. {item['document']}")
                            print(f"     原始嵌入相似度: {item['original_embedding_similarity']:.3f} (排名: {original_ranks.get(item['doc_id'], 'N/A')})")
                            print(f"     重排模型分数: {item['rerank_score']:.3f} (排名: {rerank_model_ranks.get(item['doc_id'], 'N/A')})")
                            print(f"     RRF 融合得分: {item['rrf_score']:.6f}")
                            print()
                    else:
                        print("  重排失败或没有返回分数。")
                else:
                    print("  未找到相似标签")
                    
            except Exception as e:
                print(f"  搜索失败: {e}")
        
        print("\n=== 测试完成 ===")
        
    except Exception as e:
        print(f"无法连接到集合: {e}")
        print("请先运行 simple_tags_vector.py 来创建向量数据库")

def show_all_tags():
    """显示所有存储的标签"""
    print("\n=== 显示所有存储的标签 ===")
    
    client = chromadb.PersistentClient(path="tags_vector_db/")
    
    try:
        collection = client.get_collection(name="tags_collection")
        
        # 获取所有文档
        all_docs = collection.get()
        
        if all_docs['documents']:
            print(f"总共存储了 {len(all_docs['documents'])} 个标签:")
            for i, doc in enumerate(all_docs['documents'][:20]):  # 只显示前20个
                print(f"  {i+1}. {doc}")
            
            if len(all_docs['documents']) > 20:
                print(f"  ... 还有 {len(all_docs['documents']) - 20} 个标签")
        else:
            print("没有找到任何标签")
            
    except Exception as e:
        print(f"无法获取标签列表: {e}")

def compare_search_methods(query):
    """比较原始搜索和重排搜索的效果"""
    print(f"\n=== 搜索方法对比: '{query}' ===")
    
    client = chromadb.PersistentClient(path="tags_vector_db/")
    
    # 从环境变量获取配置
    api_base = os.getenv('OPENAI_API_BASE', 'http://localhost:11434/api')
    model_name = os.getenv('OPENAI_EMBEDDING_MODEL_NAME', 'qwen3-embedding-0.6b:latest')
    
    # 创建嵌入函数和重排模型
    embedding_func = OllamaEmbeddingFunction(api_base, model_name)
    try:
        reranker = RerankerModel(model_dir='./model/Qwen3-4B-Base')
    except FileNotFoundError as e:
        print(f"无法加载重排模型: {e}")
        print("请检查模型路径是否正确，以及模型文件是否存在。")
        return
    except Exception as e:
        print(f"加载重排模型时发生未知错误: {e}")
        return
    
    try:
        collection = client.get_collection(name="tags_collection", embedding_function=embedding_func)
        results = collection.query(
            query_texts=[query],
            n_results=10
        )
        
        if results['documents'] and results['documents'][0]:
            print("\n📊 原始嵌入搜索结果:")
            for i, (doc, distance) in enumerate(zip(results['documents'][0][:5], results['distances'][0][:5])):
                print(f"  {i+1}. {doc} (相似度: {1-distance:.3f})")
            
            # 使用 LM Studio Embeddings API 进行重排
            docs_to_rerank = results['documents'][0]
            query_embedding_for_rerank = get_lm_studio_embedding(query, LM_STUDIO_MODEL_NAME, LM_STUDIO_API_URL)
            rerank_scores = []
            if query_embedding_for_rerank:
                for doc_text in docs_to_rerank:
                    doc_embedding_for_rerank = get_lm_studio_embedding(doc_text, LM_STUDIO_MODEL_NAME, LM_STUDIO_API_URL)
                    if doc_embedding_for_rerank:
                        score = cosine_similarity_local(query_embedding_for_rerank, doc_embedding_for_rerank)
                        rerank_scores.append(score)
                    else:
                        rerank_scores.append(0.0) # Assign low score if embedding fails
            else:
                print("  无法获取查询的 LM Studio 嵌入，跳过重排。")
                rerank_scores = [0.0] * len(docs_to_rerank)

            if rerank_scores:
                reranked_items = []
                for doc, score, original_distance in zip(docs_to_rerank, rerank_scores, results['distances'][0]):
                    original_sim = 1 - original_distance
                    reranked_items.append({
                        'document': doc,
                        'original_embedding_similarity': original_sim, # 原始嵌入相似度 (来自ChromaDB)
                        'rerank_score': score, # 新的重排分数 (来自LM Studio)
                        # 'final_score' will be replaced by RRF score later
                        'doc_id': doc # Keep track of document for RRF
                    })
                # Calculate RRF scores
                original_ranked_docs_comp = sorted(reranked_items, key=lambda x: x['original_embedding_similarity'], reverse=True)
                original_ranks_comp = {item['doc_id']: i + 1 for i, item in enumerate(original_ranked_docs_comp)}

                rerank_model_ranked_docs_comp = sorted(reranked_items, key=lambda x: x['rerank_score'], reverse=True)
                rerank_model_ranks_comp = {item['doc_id']: i + 1 for i, item in enumerate(rerank_model_ranked_docs_comp)}

                k_rrf = 20 # RRF k parameter
                for item in reranked_items:
                    rank1_comp = original_ranks_comp.get(item['doc_id'], len(reranked_items) + 1)
                    rank2_comp = rerank_model_ranks_comp.get(item['doc_id'], len(reranked_items) + 1)
                    item['rrf_score'] = (1 / (k_rrf + rank1_comp)) + (1 / (k_rrf + rank2_comp))
                
                reranked_items.sort(key=lambda x: x['rrf_score'], reverse=True)

                print("\n🎯 RRF 融合结果:")
                for i, item in enumerate(reranked_items[:5]):
                    print(f"  {i+1}. {item['document']}")
                    print(f"     原始嵌入相似度: {item['original_embedding_similarity']:.3f} (排名: {original_ranks_comp.get(item['doc_id'], 'N/A')})")
                    print(f"     重排模型分数: {item['rerank_score']:.3f} (排名: {rerank_model_ranks_comp.get(item['doc_id'], 'N/A')})")
                    print(f"     RRF 融合得分: {item['rrf_score']:.6f}")
            else:
                print("  重排失败或没有返回分数。")
        else:
            print("  未找到相似标签")
            
    except Exception as e:
        print(f"搜索失败: {e}")

def main():
    """主函数"""
    print("ChromaDB 标签搜索测试程序 (带重排模型)")
    print("=" * 50)
    
    while True:
        print("\n请选择操作:")
        print("1. 测试搜索功能 (使用重排模型)")
        print("2. 显示所有标签")
        print("3. 自定义搜索 (对比重排效果)")
        print("4. 搜索方法效果对比")
        print("5. 退出")
        
        choice = input("\n请输入选择 (1-5): ").strip()
        
        if choice == '1':
            test_search()
        elif choice == '2':
            show_all_tags()
        elif choice == '3':
            query = input("请输入搜索关键词: ").strip()
            if query:
                print(f"\n搜索查询: '{query}'")
                client = chromadb.PersistentClient(path="tags_vector_db/")
                
                # 从环境变量获取配置
                api_base = os.getenv('OPENAI_API_BASE', 'http://localhost:11434/api')
                model_name = os.getenv('OPENAI_EMBEDDING_MODEL_NAME', 'qwen3-embedding-0.6b:latest')
                
                # 创建嵌入函数
                embedding_func = OllamaEmbeddingFunction(api_base, model_name)
                # LM Studio Reranker (via embeddings) is used directly in the search loop
                
                try:
                    collection = client.get_collection(name="tags_collection", embedding_function=embedding_func)
                    results = collection.query(
                        query_texts=[query],
                        n_results=15
                    )
                    
                    if results['documents'] and results['documents'][0]:
                        print("\n=== 原始搜索结果 ===")
                        for i, (doc, distance) in enumerate(zip(results['documents'][0][:8], results['distances'][0][:8])):
                            print(f"  {i+1}. {doc} (嵌入相似度: {1-distance:.3f})")
                        
                        # 使用 LM Studio Embeddings API 进行重排
                        docs_to_rerank = results['documents'][0]
                        query_embedding_for_rerank = get_lm_studio_embedding(query, LM_STUDIO_MODEL_NAME, LM_STUDIO_API_URL)
                        rerank_scores = []
                        if query_embedding_for_rerank:
                            for doc_text in docs_to_rerank:
                                doc_embedding_for_rerank = get_lm_studio_embedding(doc_text, LM_STUDIO_MODEL_NAME, LM_STUDIO_API_URL)
                                if doc_embedding_for_rerank:
                                    score = cosine_similarity_local(query_embedding_for_rerank, doc_embedding_for_rerank)
                                    rerank_scores.append(score)
                                else:
                                    rerank_scores.append(0.0) # Assign low score if embedding fails
                        else:
                            print("  无法获取查询的 LM Studio 嵌入，跳过重排。")
                            rerank_scores = [0.0] * len(docs_to_rerank) # Assign all zero if query embedding fails


                        if rerank_scores:
                            reranked_items = []
                            for doc, score, original_distance in zip(docs_to_rerank, rerank_scores, results['distances'][0]):
                                reranked_items.append({
                                    'document': doc,
                                    'original_embedding_similarity': 1 - original_distance, # 原始嵌入相似度 (来自ChromaDB)
                                    'rerank_score': score, # 新的重排分数 (来自LM Studio)
                                    # 'final_score' will be replaced by RRF score later
                                    'doc_id': doc # Keep track of document for RRF
                                })
                            # Calculate RRF scores
                            original_ranked_docs_main = sorted(reranked_items, key=lambda x: x['original_embedding_similarity'], reverse=True)
                            original_ranks_main = {item['doc_id']: i + 1 for i, item in enumerate(original_ranked_docs_main)}

                            rerank_model_ranked_docs_main = sorted(reranked_items, key=lambda x: x['rerank_score'], reverse=True)
                            rerank_model_ranks_main = {item['doc_id']: i + 1 for i, item in enumerate(rerank_model_ranked_docs_main)}

                            k_rrf = 20 # RRF k parameter
                            for item in reranked_items:
                                rank1_main = original_ranks_main.get(item['doc_id'], len(reranked_items) + 1)
                                rank2_main = rerank_model_ranks_main.get(item['doc_id'], len(reranked_items) + 1)
                                item['rrf_score'] = (1 / (k_rrf + rank1_main)) + (1 / (k_rrf + rank2_main))
                            
                            reranked_items.sort(key=lambda x: x['rrf_score'], reverse=True)

                            print("\n=== 重排后结果 (使用 RRF 融合) ===")
                            for i, item in enumerate(reranked_items[:8]):
                                print(f"  {i+1}. {item['document']}")
                                print(f"     原始嵌入相似度: {item['original_embedding_similarity']:.3f} (排名: {original_ranks_main.get(item['doc_id'], 'N/A')})")
                                print(f"     重排模型分数: {item['rerank_score']:.3f} (排名: {rerank_model_ranks_main.get(item['doc_id'], 'N/A')})")
                                print(f"     RRF 融合得分: {item['rrf_score']:.6f}")
                                if i < 7:
                                    print()
                        else:
                            print("  重排失败或没有返回分数。")
                    else:
                        print("  未找到相似标签")
                except Exception as e:
                    print(f"搜索失败: {e}")
        elif choice == '4':
            query = input("请输入要对比的搜索关键词: ").strip()
            if query:
                compare_search_methods(query)
        elif choice == '5':
            print("退出程序")
            break
        else:
            print("无效选择，请重新输入")

def get_lm_studio_embedding(text: str, model_name: str, api_url: str):
    """Fetches an embedding for the given text using the LM Studio API."""
    headers = {"Content-Type": "application/json"}
    payload = {
        "input": text,
        "model": model_name
    }
    try:
        response = requests.post(api_url, headers=headers, json=payload)
        response.raise_for_status()
        json_response = response.json()
        if json_response.get("data") and isinstance(json_response["data"], list) and len(json_response["data"]) > 0:
            embedding_data = json_response["data"][0]
            if isinstance(embedding_data, dict) and "embedding" in embedding_data:
                return embedding_data["embedding"]
    except requests.exceptions.RequestException as e:
        print(f"Error calling LM Studio API for embedding: {e}")
    except Exception as e:
        print(f"An unexpected error occurred during LM Studio embedding: {e}")
    return None

def cosine_similarity_local(vec1, vec2):
    """Computes the cosine similarity between two vectors."""
    if vec1 is None or vec2 is None:
        return 0.0
    vec1 = np.array(vec1)
    vec2 = np.array(vec2)
    if vec1.shape != vec2.shape:
        return 0.0
    if np.linalg.norm(vec1) == 0 or np.linalg.norm(vec2) == 0:
        return 0.0
    return np.dot(vec1, vec2) / (np.linalg.norm(vec1) * np.linalg.norm(vec2))

if __name__ == "__main__":
    main()