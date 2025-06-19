import chromadb
import os
from dotenv import load_dotenv
import datetime 
import requests
import numpy as np
# 加载 .env 文件中的环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

folder = "./embedding_folder"
collection_name = "user_embeddings"

headers = {"Content-Type": "application/json"}

OPENAI_API_BASE = os.getenv("OPENAI_API_BASE")
OPENAI_EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")
user_id = "U10000002"
chromadb_client = chromadb.PersistentClient(path=folder)
collection = chromadb_client.get_or_create_collection(collection_name)
categories = ['电子产品','电子产品','图书教材']
tags = [['vivo','95新','手机'],['华硕ROG','电脑','笔记本电脑'],['考研','数学']]
def get_embedding(text, model_name):
    """获取给定文本的嵌入向量"""
    payload = {"prompt": text, "model": model_name}
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
for i in range(len(categories)):
    now = datetime.datetime.now()
    milliseconds = int(now.timestamp() * 1000)
    category_embedding = get_embedding(categories[i], OPENAI_EMBEDDING_MODEL_NAME)
    embedding_tags = np.zeros(2560)
    for tag in tags[i]:
        embedding_tags += get_embedding(tag, OPENAI_EMBEDDING_MODEL_NAME)
    collection.add(
        embeddings=embedding_tags+category_embedding,
        metadatas=[{"category": categories[i], "tags": ",".join(tags[i]),"user_id":user_id,"time":milliseconds}],
        ids=[f"{user_id}_{i}_{milliseconds}"]
    )