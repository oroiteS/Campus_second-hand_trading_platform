import chromadb
from dotenv import load_dotenv
import requests
import numpy as np

persist_dir = "./embedding_folder"
collection_name = "test"

load_dotenv

api_base="http://localhost:11434/api"
OPENAI_EMBEDDING_MODEL_NAME="modelscope.cn/Qwen/Qwen3-Embedding-4B-GGUF:Q6_K"
OPENAI_RERANKER_MODEL_NAME="qwen3-reranker-4b:latest"

prompt=["华为","OPPO","vivo","索尼","阿迪达斯","一加手机","洁厕灵","手机","电子产品","我想买一台电脑"]
headers = {"Content-Type":"application/json"}
payload_tag = {"prompt":"vivo", "model": OPENAI_EMBEDDING_MODEL_NAME}
payload_category = {"prompt":"电子产品","model": OPENAI_EMBEDDING_MODEL_NAME}
payload_combine = {"prompt":"电子产品 vivo","model": OPENAI_EMBEDDING_MODEL_NAME}
payload_test = {"prompt":"OPPO","model": OPENAI_EMBEDDING_MODEL_NAME}
response_tag = requests.post(f"{api_base}/embeddings", headers=headers, json=payload_tag)
response_tag.raise_for_status()
json_response_tag = response_tag.json()
response_category = requests.post(f"{api_base}/embeddings", headers=headers, json=payload_category)
response_category.raise_for_status()
json_response_category = response_category.json()
response_combine = requests.post(f"{api_base}/embeddings", headers=headers, json=payload_combine)
response_combine.raise_for_status()
json_response_combine = response_combine.json()

# 计算余弦相似度
def cosine_similarity(vec1, vec2):
    dot_product = np.dot(vec1, vec2)
    norm_a = np.linalg.norm(vec1)
    norm_b = np.linalg.norm(vec2)
    return dot_product / (norm_a * norm_b)
response_test = requests.post(f"{api_base}/embeddings",headers=headers,json=payload_test)
response_test.raise_for_status()
json_response_test = response_test.json()

# 确保将嵌入向量转换为 numpy 数组再进行操作
category_embedding_np = np.array(json_response_category['embedding'])
tag_embedding_np = np.array(json_response_tag['embedding'])
combine_embedding_np = np.array(json_response_combine['embedding'])
test_embedding_np = np.array(json_response_test['embedding'])
e1 = category_embedding_np + tag_embedding_np
e2 = combine_embedding_np

for p in prompt:
    payload_test = {"prompt":p, "model": OPENAI_EMBEDDING_MODEL_NAME}
    response_test = requests.post(f"{api_base}/embeddings",headers=headers,json=payload_test)
    response_test.raise_for_status()
    json_response_test = response_test.json()
    test_embedding_np = np.array(json_response_test['embedding'])

    print(f"{p}与 电子产品+vivo的相似度为",cosine_similarity(e1,test_embedding_np))
    print(f"{p}与 电子产品 vivo的相似度为",cosine_similarity(e2,test_embedding_np))
