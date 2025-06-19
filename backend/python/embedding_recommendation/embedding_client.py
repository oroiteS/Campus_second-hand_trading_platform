import os
import numpy as np
from openai import OpenAI
from dotenv import load_dotenv
from typing import List, Dict, Any, Optional
import json
import chromadb
import requests
import time
import hashlib
import logging

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 加载环境变量
load_dotenv()

class EmbeddingClient:
    """基于OpenAI格式的LM Studio embedding客户端，并使用ChromaDB进行向量存储和检索"""
    
    def __init__(self, collection_name="default_collection"):
        self.client = OpenAI(
            api_key=os.getenv("OPENAI_API_KEY"),
            base_url=os.getenv("OPENAI_API_BASE")
        )
        self.model_name = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")
        
        # 初始化ChromaDB客户端
        # 使用内存模式，数据不会持久化
        self.chroma_client = chromadb.Client()
        # 或者使用持久化存储
        # self.chroma_client = chromadb.PersistentClient(path="./chroma_db")
        
        # 获取或创建集合
        try:
            self.collection = self.chroma_client.get_collection(name=collection_name)
            print(f"使用已存在的ChromaDB集合: {collection_name}")
        except:
            self.collection = self.chroma_client.create_collection(name=collection_name)
            print(f"创建新的ChromaDB集合: {collection_name}")
        
    def get_embedding(self, text: str) -> List[float]:
        """获取单个文本的embedding向量"""
        try:
            response = self.client.embeddings.create(
                input=text,
                model=self.model_name
            )
            return response.data[0].embedding
        except Exception as e:
            print(f"获取embedding失败: {e}")
            return []
    
    def get_embeddings_batch(self, texts: List[str]) -> List[List[float]]:
        """批量获取多个文本的embedding向量"""
        embeddings = []
        for text in texts:
            embedding = self.get_embedding(text)
            if embedding:
                embeddings.append(embedding)
        return embeddings
    
    def cosine_similarity(self, vec1: List[float], vec2: List[float]) -> float:
        """计算两个向量的余弦相似度"""
        if not vec1 or not vec2:
            return 0.0
        
        vec1 = np.array(vec1)
        vec2 = np.array(vec2)
        
        # 计算余弦相似度
        dot_product = np.dot(vec1, vec2)
        norm1 = np.linalg.norm(vec1)
        norm2 = np.linalg.norm(vec2)
        
        if norm1 == 0 or norm2 == 0:
            return 0.0
        
        return dot_product / (norm1 * norm2)
    
    def add_documents_to_collection(self, documents: List[str], metadatas: List[Dict[str, Any]] = None, ids: List[str] = None):
        """将文档及其embeddings添加到ChromaDB集合中"""
        if not documents:
            print("没有文档可添加")
            return

        embeddings = self.get_embeddings_batch(documents)
        if not embeddings or len(embeddings) != len(documents):
            print("获取embeddings失败或数量不匹配")
            return

        if ids is None:
            ids = [f"doc_{i}" for i in range(len(documents))]
        
        if metadatas is None:
            metadatas = [{"source": "unknown"} for _ in range(len(documents))]
        elif len(metadatas) != len(documents):
            print("元数据数量与文档数量不匹配")
            # 使用默认元数据填充
            metadatas = [{"source": "unknown"} for _ in range(len(documents))]

        try:
            self.collection.add(
                embeddings=embeddings,
                documents=documents,
                metadatas=metadatas,
                ids=ids
            )
            print(f"成功添加 {len(documents)} 个文档到ChromaDB集合")
        except Exception as e:
            print(f"添加到ChromaDB失败: {e}")

    def find_most_similar_chroma(self, query_text: str, n_results: int = 3) -> Dict[str, Any]:
        """使用ChromaDB找到与查询文本最相似的文档"""
        query_embedding = self.get_embedding(query_text)
        if not query_embedding:
            return {"error": "无法获取查询文本的embedding"}

        try:
            results = self.collection.query(
                query_embeddings=[query_embedding],
                n_results=n_results
            )
            
            # ChromaDB返回的distances是L2距离的平方，如果需要余弦相似度，需要转换
            # 或者在创建collection时指定 hnsw:space: "cosine"
            # 这里我们直接使用ChromaDB的距离作为排序依据（越小越相似）
            
            similar_items = []
            if results and results.get('documents') and results.get('distances') and results.get('ids'):
                for i in range(len(results['ids'][0])):
                    similar_items.append({
                        "text": results['documents'][0][i],
                        "distance": results['distances'][0][i], # L2 distance, smaller is better
                        "id": results['ids'][0][i]
                    })
            
            # 按距离排序 (升序)
            similar_items.sort(key=lambda x: x["distance"])

            return {
                "query": query_text,
                "results": similar_items,
                "most_similar": similar_items[0] if similar_items else None
            }
        except Exception as e:
            print(f"ChromaDB查询失败: {e}")
            return {"error": f"ChromaDB查询失败: {e}"}

def main():
    """主函数 - 演示embedding匹配和ChromaDB功能"""
    print("=== LM Studio Embedding 与 ChromaDB 匹配测试 ===")
    
    # 初始化客户端，指定集合名称
    client = EmbeddingClient(collection_name="campus_items")
    
    # 创建测试数据
    print("\n1. 创建并添加测试数据到ChromaDB...")
    
    # 校园二手交易平台相关的测试数据
    candidate_texts = [
        "出售二手笔记本电脑，联想ThinkPad，9成新，价格面议",
        "转让闲置自行车，山地车，骑行感受良好，适合校园代步",
        "出售教材书籍，高等数学、大学物理等，价格便宜",
        "转让电子产品，iPhone手机，功能正常，外观有轻微磨损",
        "出售生活用品，台灯、书架、收纳盒等宿舍必备",
        "转让运动器材，哑铃、瑜伽垫，适合宿舍健身",
        "出售数码配件，充电器、数据线、耳机等",
        "转让学习用品，计算器、文具盒、笔记本等"
    ]
    
    print(f"候选商品数量: {len(candidate_texts)}")
    # 为每个文档创建元数据和ID
    metadatas = [{"category": "electronics" if "电脑" in text or "手机" in text or "电子" in text or "数码" in text else 
                  "books" if "教材" in text or "书" in text else
                  "vehicle" if "自行车" in text else
                  "household" if "生活用品" in text or "台灯" in text or "书架" in text or "收纳盒" in text else
                  "sports" if "运动器材" in text or "哑铃" in text or "瑜伽垫" in text else
                  "stationery" if "学习用品" in text or "计算器" in text or "文具盒" in text or "笔记本" in text else
                  "other"} for text in candidate_texts]
    ids = [f"item_{i}" for i in range(len(candidate_texts))]
    
    # 将文档添加到ChromaDB
    client.add_documents_to_collection(candidate_texts, metadatas=metadatas, ids=ids)
    
    # 测试查询
    test_queries = [
        "我想买一台笔记本电脑",
        "需要购买自行车代步",
        "寻找数学教材",
        "想要买个手机",
        "需要一些宿舍用品"
    ]
    
    print("\n2. 开始使用ChromaDB进行匹配测试...")
    
    for i, query in enumerate(test_queries):
        print(f"\n--- 测试 {i+1}: {query} ---")
        
        result = client.find_most_similar_chroma(query, n_results=3)
        
        if "error" in result:
            print(f"错误: {result['error']}")
            continue
        
        if result['most_similar']:
            print(f"最匹配的商品: {result['most_similar']['text']}")
            print(f"距离 (越小越好): {result['most_similar']['distance']:.4f}")
        else:
            print("未找到匹配结果")
        
        print("\n匹配结果:")
        if result['results']:
            for j, item in enumerate(result['results']):
                print(f"  {j+1}. [距离: {item['distance']:.4f}] {item['text']} (ID: {item['id']})")
        else:
            print("  无匹配结果")
    
    # 测试单个embedding获取
    print("\n3. 测试单个文本embedding获取...")
    test_text = "出售二手笔记本电脑"
    embedding = client.get_embedding(test_text)
    if embedding:
        print(f"文本: {test_text}")
        print(f"Embedding维度: {len(embedding)}")
        print(f"前5个维度值: {embedding[:5]}")
    else:
        print("获取embedding失败")
    
    print("\n=== 测试完成 ===")

if __name__ == "__main__":
    main()