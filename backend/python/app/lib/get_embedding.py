import pymongo
from dotenv import load_dotenv
import os
from datetime import datetime
import chromadb
from app.lib.text_embedding import get_embedding
import numpy as np

class Embedder:
    def __init__(self):
        load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))
        
        # MongoDB连接配置
        self.mongo_uri = os.getenv("MONGODB_URI", "mongodb://localhost:27017")
        self.mongo_db_name = "user_embeddings_db"
        self.mongo_client = pymongo.MongoClient(self.mongo_uri)
        self.collection_name = "user_embeddings"
        self.mongo_collection = self.mongo_client[self.mongo_db_name][self.collection_name]

        self.folder = 'app/embedding_folder'
        self.collection_tag = 'tags'
        self.collection_category = 'categories'
        self.collection_commodity = 'commodities'
        self.client = chromadb.PersistentClient(path=self.folder)
        self.chroma_collection_tag = self.client.get_or_create_collection(self.collection_tag)
        self.chroma_collection_category = self.client.get_or_create_collection(self.collection_category)
        self.chroma_collection_commodity = self.client.get_or_create_collection(self.collection_commodity)
    def recommendation_by_buy(self, user_id, limit=3):
        """
        从MongoDB获取用户最近的购买记录，按时间戳倒序排列
        
        Args:
            user_id (str): 用户ID
            limit (int): 返回记录数，默认3条
        """
        result_id = []
        try:
            # 构建查询条件
            query = {"user_id": user_id, "action": "buy"}
            
            # 按时间倒序查询，并限制结果数量
            cursor = self.mongo_collection.find(query).sort("time", pymongo.DESCENDING).limit(limit)
            results_data = list(cursor)
            if cursor is None:
                return result_id
            for data in results_data:
                results_buy_recommendation = self.chroma_collection_commodity.query(
                    query_embeddings=[data['embedding']],
                    n_results=2,
                    where={"seller_id": {"$ne": user_id}},
                    include=['documents','metadatas']
                )
                # ChromaDB返回的ids是嵌套列表，需要展平
                if results_buy_recommendation['ids']:
                    for id_list in results_buy_recommendation['ids']:
                        for id in id_list:
                            result_id.append(id)
        except Exception as e:
            print(f"查询错误: {e}")
        return result_id
        
    def recommendation_by_like(self, user_id,limit = 8):
        query = {"user_id": user_id, "action": "like"}
        cursor = self.mongo_collection.find(query)    
        # 获取所有用户喜欢的商品的embedding
        embeddings = []
        for doc in cursor:
            if 'embedding' in doc:
                embeddings.append(doc['embedding'])
        
        if not embeddings:
            return []
            
        results = self.chroma_collection_commodity.query(
            query_embeddings=embeddings,
            n_results=limit,
            where={"seller_id": {"$ne": user_id}},
            include=['documents','metadatas']
        )
        # ChromaDB返回的ids是嵌套列表，需要展平
        if results['ids'] and len(results['ids']) > 0:
            return results['ids'][0]  # 取第一个查询结果的ids列表
        return []
    def recommendation_by_token(self,token_list,user_id,limit = 15):
        embedding_sum = np.zeros(2560)
        for token in token_list:
            token_embedding = get_embedding(token)
            if token_embedding is not None:
                embedding_sum += token_embedding
        results = self.chroma_collection_commodity.query(
            query_embeddings=embedding_sum,
            n_results=limit,
            where={"seller_id": {"$ne": user_id}},
            include=['documents','metadatas']
        )
        if results['ids'] and len(results['ids']) > 0:
            return results['ids'][0]  # 取第一个查询结果的ids列表
        return []
    def close(self):
        """关闭MongoDB连接"""
        if hasattr(self, 'mongo_client'):
            self.mongo_client.close()