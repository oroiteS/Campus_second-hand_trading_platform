import os
import chromadb
from dotenv import load_dotenv
import requests
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import jieba
from collections import Counter



#加载环境变量
load_dotenv()
class recommendation:
    def __init__(self, user_id):
        #初始化ChromaDB客户端
        api_base = os.getenv('OPENAI_API_BASE', 'http://localhost:11434/api')
        model_name = os.getenv('OPENAI_EMBEDDING_MODEL_NAME', 'qwen3-embedding-0.6b:latest')

        client = chromadb.PersistentClient(path="tags_vector_db/")

        embedding_model = EmbeddingModel()
        reranker = RerankerModel()

    def get_tags(self,tags):
        """获取匹配的数据"""
        target_tags = []
        for tag in tags:
            try:
                results = collection.query(
                    query_texts=[tag],
                    n_results = 10 #获取更多结果以用于重排序
                )
                if results['documents'] and results['documents'][0]:
                    reranker_results,reranked_items = reranker.rerank_results(tag,results,results['distances'][0])
                    for i ,item in enumerate(reranked_items[:3]):
                        target_tags.append(item)
            except Exception as e:
                print(f"搜索失败:{e}")
                return []
        return target_tags
            
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

class EmbeddingModel:
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