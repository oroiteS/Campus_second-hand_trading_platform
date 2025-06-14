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
from sklearn.metrics.pairwise import cosine_similarity
import jieba
from collections import Counter

# 加载环境变量
load_dotenv()

class RerankerModel:
    """重排模型类，用于对搜索结果进行二次排序"""
    
    def __init__(self):
        # 初始化中文分词器
        jieba.initialize()
    
    def calculate_text_similarity(self, query, document):
        """计算文本相似度（基于词汇重叠和语义相似度）"""
        # 分词
        query_words = set(jieba.cut(query.lower()))
        doc_words = set(jieba.cut(document.lower()))
        
        # 计算词汇重叠度
        intersection = query_words.intersection(doc_words)
        union = query_words.union(doc_words)
        
        if len(union) == 0:
            jaccard_similarity = 0
        else:
            jaccard_similarity = len(intersection) / len(union)
        
        # 计算字符级别相似度
        query_chars = set(query.lower())
        doc_chars = set(document.lower())
        char_intersection = query_chars.intersection(doc_chars)
        char_union = query_chars.union(doc_chars)
        
        if len(char_union) == 0:
            char_similarity = 0
        else:
            char_similarity = len(char_intersection) / len(char_union)
        
        # 计算长度相似度
        len_similarity = 1 - abs(len(query) - len(document)) / max(len(query), len(document), 1)
        
        # 综合相似度分数
        combined_score = 0.4 * jaccard_similarity + 0.3 * char_similarity + 0.3 * len_similarity
        
        return combined_score
    
    def rerank_results(self, query, results, embedding_similarities):
        """重排搜索结果"""
        if not results['documents'] or not results['documents'][0]:
            return results
        
        documents = results['documents'][0]
        distances = results['distances'][0]
        
        # 计算重排分数
        reranked_items = []
        for i, (doc, distance) in enumerate(zip(documents, distances)):
            # 原始嵌入相似度（距离转换为相似度）
            embedding_sim = 1 - distance
            
            # 文本相似度
            text_sim = self.calculate_text_similarity(query, doc)
            
            # 综合分数（可以调整权重）
            final_score = 0.65 * embedding_sim + 0.35 * text_sim
            
            reranked_items.append({
                'document': doc,
                'distance': distance,
                'embedding_similarity': embedding_sim,
                'text_similarity': text_sim,
                'final_score': final_score,
                'original_index': i
            })
        
        # 按最终分数排序
        reranked_items.sort(key=lambda x: x['final_score'], reverse=True)
        
        # 重新构建结果
        reranked_results = {
            'documents': [[item['document'] for item in reranked_items]],
            'distances': [[1 - item['final_score'] for item in reranked_items]],
            'metadatas': results.get('metadatas', [[]]),
            'ids': results.get('ids', [[]])
        }
        
        return reranked_results, reranked_items

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
    
    # 创建嵌入函数和重排模型
    embedding_func = OllamaEmbeddingFunction(api_base, model_name)
    reranker = RerankerModel()
    
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
                    
                    # 使用重排模型重新排序
                    reranked_results, reranked_items = reranker.rerank_results(query, results, results['distances'][0])
                    
                    print("\n重排后结果:")
                    for i, item in enumerate(reranked_items[:5]):
                        print(f"  {i+1}. {item['document']}")
                        print(f"     嵌入相似度: {item['embedding_similarity']:.3f}")
                        print(f"     文本相似度: {item['text_similarity']:.3f}")
                        print(f"     综合分数: {item['final_score']:.3f}")
                        print()
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
    reranker = RerankerModel()
    
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
            
            # 重排结果
            reranked_results, reranked_items = reranker.rerank_results(query, results, results['distances'][0])
            
            print("\n🎯 重排模型优化结果:")
            for i, item in enumerate(reranked_items[:5]):
                improvement = "📈" if item['final_score'] > item['embedding_similarity'] else "📉" if item['final_score'] < item['embedding_similarity'] else "➡️"
                print(f"  {i+1}. {item['document']} {improvement}")
                print(f"     综合分数: {item['final_score']:.3f} (嵌入: {item['embedding_similarity']:.3f}, 文本: {item['text_similarity']:.3f})")
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
                
                # 创建嵌入函数和重排模型
                embedding_func = OllamaEmbeddingFunction(api_base, model_name)
                reranker = RerankerModel()
                
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
                        
                        # 使用重排模型
                        reranked_results, reranked_items = reranker.rerank_results(query, results, results['distances'][0])
                        
                        print("\n=== 重排后结果 ===")
                        for i, item in enumerate(reranked_items[:8]):
                            print(f"  {i+1}. {item['document']}")
                            print(f"     嵌入相似度: {item['embedding_similarity']:.3f}")
                            print(f"     文本相似度: {item['text_similarity']:.3f}")
                            print(f"     综合分数: {item['final_score']:.3f}")
                            if i < 7:  # 不在最后一个项目后添加空行
                                print()
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

if __name__ == "__main__":
    main()