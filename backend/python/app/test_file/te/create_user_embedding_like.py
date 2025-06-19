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
    """获取文本的嵌入向量"""
    if not OPENAI_API_BASE or not model_name:
        print("OpenAI配置缺失，使用默认零向量")
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

def add_like_records_to_mongodb():
    """为like行为添加记录到MongoDB（移除time和category字段）"""
    user_id = "U10000002"  # 用户ID
    like_tags = ["手机", "耳机", "相机", "运动手表", "笔记本电脑"]  # like标签列表
    
    # 1. 生成唯一ID
    now = datetime.datetime.now()
    milliseconds = int(now.timestamp() * 1000)
    record_id = f"{user_id}_like_{milliseconds}"
    
    # 2. 计算标签嵌入向量（累加）
    embedding_tags = np.zeros(2560)
    for tag in like_tags:
        tag_embedding = get_embedding(tag, OPENAI_EMBEDDING_MODEL_NAME)
        embedding_tags += tag_embedding
    
    # 3. 构建文档（移除time和category）
    doc = {
        "_id": record_id,
        "user_id": user_id,
        "tags": like_tags,
        "action": "like",
        "embedding": embedding_tags.tolist(),
        "created_at": now,
        "updated_at": now
    }
    
    # 4. 插入MongoDB
    try:
        result = mongo_collection.insert_one(doc)
        if result.inserted_id:
            print(f"成功插入like记录: {record_id}")
        else:
            print(f"插入失败: {record_id}")
    except pymongo.errors.DuplicateKeyError:
        print(f"like记录已存在: {record_id}")
    except Exception as e:
        print(f"插入like记录出错: {record_id}, 错误: {e}")

# 执行插入
if __name__ == "__main__":
    add_like_records_to_mongodb()