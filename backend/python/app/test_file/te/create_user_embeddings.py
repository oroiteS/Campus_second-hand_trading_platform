import pymongo
import os
from dotenv import load_dotenv
import datetime
import requests
import numpy as np

# 加载环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

# MongoDB配置
mongo_uri = os.getenv("MONGODB_URI", "mongodb://localhost:27017")
mongo_db_name = "user_embeddings_db"
mongo_collection_name = "user_embeddings"

# 初始化MongoDB客户端
mongo_client = pymongo.MongoClient(mongo_uri)
mongo_db = mongo_client[mongo_db_name]
mongo_collection = mongo_db[mongo_collection_name]

# 接口配置
headers = {"Content-Type": "application/json"}
OPENAI_API_BASE = os.getenv("OPENAI_API_BASE")
OPENAI_EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")

def get_embedding(text, model_name):
    """获取文本的嵌入向量（与ChromaDB逻辑一致）"""
    if not OPENAI_API_BASE or not model_name:
        print("OpenAI配置缺失，无法获取嵌入向量")
        return np.zeros(2560)
    
    payload = {"prompt": text, "model": model_name}
    try:
        response = requests.post(
            f"{OPENAI_API_BASE}/embeddings", 
            headers=headers, 
            json=payload, 
            timeout=60
        )
        response.raise_for_status()
        return np.array(response.json()["embedding"])
    except Exception as e:
        print(f"获取嵌入向量失败: {text}, 错误: {e}")
        return np.zeros(2560)

def add_records_to_mongodb():
    """批量向MongoDB添加记录（完全复刻ChromaDB逻辑）"""
    user_id = "U10000002"
    categories = ["电子产品", "电子产品", "图书教材"]
    tags = [
        ["vivo", "95新", "手机"],
        ["华硕ROG", "电脑", "笔记本电脑"],
        ["考研", "数学"]
    ]
    
    for i in range(len(categories)):
        # 1. 生成时间戳（毫秒级）
        now = datetime.datetime.now()
        milliseconds = int(now.timestamp() * 1000)
        
        # 2. 获取分类嵌入向量
        category_embedding = get_embedding(categories[i], OPENAI_EMBEDDING_MODEL_NAME)
        
        # 3. 获取标签嵌入向量（累加）
        embedding_tags = np.zeros(2560)
        for tag in tags[i]:
            tag_embedding = get_embedding(tag, OPENAI_EMBEDDING_MODEL_NAME)
            embedding_tags += tag_embedding if tag_embedding is not None else np.zeros(2560)
        
        # 4. 合并向量
        final_embedding = (category_embedding + embedding_tags).tolist()
        
        # 5. 构建文档
        doc = {
            "_id": f"{user_id}_{i}_{milliseconds}",  # 保持ID格式与ChromaDB一致
            "user_id": user_id,
            "category": categories[i],
            "tags": tags[i],  # 保留列表格式（ChromaDB存储为逗号字符串，此处优化为数组）
            "time": milliseconds,
            "action": "buy",
            "embedding": final_embedding,
            "created_at": now,
            "updated_at": now
        }
        
        # 6. 插入MongoDB
        try:
            result = mongo_collection.insert_one(doc)
            if result.inserted_id:
                print(f"成功插入记录: {doc['_id']}")
            else:
                print(f"插入失败: {doc['_id']}")
        except pymongo.errors.DuplicateKeyError:
            print(f"记录已存在: {doc['_id']}")
        except Exception as e:
            print(f"插入出错: {doc['_id']}, 错误: {e}")

# 执行插入
if __name__ == "__main__":
    add_records_to_mongodb()