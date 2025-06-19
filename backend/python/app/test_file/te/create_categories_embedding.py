import chromadb
import numpy as np
from dotenv import load_dotenv
import requests
import os
# 加载环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

OPENAI_API_BASE = os.getenv("OPENAI_API_BASE")
OPENAI_EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")
categories = ['电子设备','教材书籍','生活用品','运动器材','文具办公','美妆护肤','食品饮料','家居用品']
client = chromadb.PersistentClient('./embedding_folder')
collection_category = client.get_or_create_collection('categories')
i = 1
headers = {"Content-Type": "application/json"}
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
for category in categories:
    category_embedding = get_embedding(category, OPENAI_EMBEDDING_MODEL_NAME)
    print(category_embedding)
    if category_embedding is not None:
        collection_category.add(
            embeddings=category_embedding.tolist(),
            documents=[category],
            ids=[f"category_{i}"],
            metadatas=[{'category_id': i}]
        )
        i += 1
