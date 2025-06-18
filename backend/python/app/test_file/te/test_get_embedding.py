import pymongo
import os
from dotenv import load_dotenv
from datetime import datetime

# 加载环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

# MongoDB配置
mongo_uri = os.getenv("MONGODB_URI", "mongodb://localhost:27017")
mongo_db_name = "user_embeddings_db"
mongo_collection_name = "user_embeddings"

def get_recent_records(limit=2):
    """
    查询并返回按时间戳倒序排列的前N条记录
    
    Args:
        limit (int): 需要返回的记录数量，默认为2
    
    Returns:
        list: 包含前N条记录的列表，每条记录是一个字典
    """
    try:
        # 连接到MongoDB
        client = pymongo.MongoClient(mongo_uri)
        db = client[mongo_db_name]
        collection = db[mongo_collection_name]
        
        # 查询并按time字段倒序排列，取前N条
        cursor = collection.find().sort("time", pymongo.DESCENDING).limit(limit)
        
        # 转换结果为列表
        results = list(cursor)
        
        # 关闭连接
        client.close()
        
        return results
        
    except Exception as e:
        print(f"查询出错: {e}")
        return []

def format_record(record):
    """格式化记录以便打印"""
    # 将毫秒时间戳转换为可读的日期时间格式
    timestamp = record.get("time", 0)
    date_time = datetime.fromtimestamp(timestamp / 1000).strftime('%Y-%m-%d %H:%M:%S')
    
    # 提取关键信息
    return {
        "id": record.get("_id"),
        "user_id": record.get("user_id"),
        "category": record.get("category"),
        "tags": record.get("tags"),
        "time": date_time,
        "action": record.get("action")
    }

if __name__ == "__main__":
    # 获取最近的两条记录
    recent_records = get_recent_records(limit=2)
    
    if recent_records:
        print(f"找到 {len(recent_records)} 条最近的记录:")
        for i, record in enumerate(recent_records, 1):
            formatted = format_record(record)
            print(f"\n记录 {i}:")
            print(f"  ID: {formatted['id']}")
            print(f"  用户ID: {formatted['user_id']}")
            print(f"  分类: {formatted['category']}")
            print(f"  标签: {', '.join(formatted['tags'])}")
            print(f"  时间: {formatted['time']}")
            print(f"  操作: {formatted['action']}")
    else:
        print("未找到任何记录")