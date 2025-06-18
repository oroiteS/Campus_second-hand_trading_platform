import os
from dotenv import load_dotenv
import requests
import numpy as np
import chromadb
# 加载 .env 文件中的环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '..', '.env'))

OPENAI_API_BASE = os.getenv("OPENAI_API_BASE")
OPENAI_EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")
headers = {"Content-Type": "application/json"}

def get_embedding(text):
    """获取给定文本的嵌入向量"""
    payload = {"prompt": text, "model": OPENAI_EMBEDDING_MODEL_NAME}
    try:
        response = requests.post(f"{OPENAI_API_BASE}/embeddings", headers=headers, json=payload, timeout=60) # 增加超时时间
        response.raise_for_status()  # 如果请求失败则抛出HTTPError
        return np.array(response.json()['embedding'])
    except requests.exceptions.RequestException as e:
        print(f"获取嵌入向量失败: {text}, 错误: {e}")
        return None
    except KeyError:
        print(f"获取嵌入向量失败: {text}, 响应中没有 'embedding' 键. 响应: {response.text}")
        return None
def get_embedding_tag_id(tag_id):
    client = chromadb.PersistentClient("app/embedding_folder")
    collection = client.get_or_create_collection("tags")
    query = collection.get(
        where={"tag_id":tag_id},
        include=["embeddings"],
        limit=1
    )
    if query["ids"]:
        return query["embeddings"][0]
    else:
        return np.zeros(2560)

def get_embedding_category_id(category_id):
    client = chromadb.PersistentClient("app/embedding_folder")
    collection = client.get_or_create_collection("categories")
    query = collection.get(
        where={"category_id":category_id},
        include=["embeddings"],
        limit=1
    )
    if query["ids"]:
        return query["embeddings"][0]
    else:
        return np.zeros(2560)
def get_embedding_commodity_id(commodity_id):
    client = chromadb.PersistentClient("app/embedding_folder")
    collection = client.get_or_create_collection("commodities")
    query = collection.get(
        where={"commodity_id":commodity_id},
        include=["embeddings"],
        limit=1
    )
    if query["ids"]:
        return query["embeddings"][0]
    else:
        return np.zeros(2560)
def update_embedding_commodity_id(commodity_id,embedding,seller_id):
    client = chromadb.PersistentClient("app/embedding_folder")
    collection = client.get_or_create_collection("commodities")
    try:
        collection.upsert(
            ids=[commodity_id],
            embeddings=[embedding.tolist()],
            metadatas= [
                {
                    'seller_id':seller_id
                }
            ]
        )
    except Exception as e:
        return 1
    return 0
def delete_embedding_commodity_id(commodity_id):
    client = chromadb.PersistentClient("app/embedding_folder")
    collection = client.get_or_create_collection("commodities")
    try:
        collection.delete(
            ids=[commodity_id]
        )
    except Exception as e:
        return 1
    return 0