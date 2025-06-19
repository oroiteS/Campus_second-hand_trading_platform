import chromadb
from dotenv import load_dotenv
import os
import requests
import numpy as np
import time # 引入time模块用于添加延迟

# 加载 .env 文件中的环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

OPENAI_API_BASE = os.getenv("OPENAI_API_BASE")
OPENAI_EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")

num = [94, 164, 312, 384, 546, 681,688,689]  # 每个类别最后一个tag的行号（1-based）
category_classes = ["电子产品", "图书教材", "生活用品", "运动器材","学习用品", "美妆护肤","食品饮料","其他"]
keywords_file_path = os.path.join(os.path.dirname(__file__), 'stopwords', 'keywords_only.txt')

persist_dir = "./embedding_folder"
collection_name = "tags"

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

# 初始化ChromaDB
client = chromadb.PersistentClient(path=persist_dir)
collection = client.get_or_create_collection(name=collection_name)

current_category_index = 0
all_tags = []

print(f"从 {keywords_file_path} 读取关键词...")
with open(keywords_file_path, 'r', encoding='utf-8') as f:
    all_tags = [line.strip() for line in f if line.strip()] # 读取所有非空行

print(f"总共读取到 {len(all_tags)} 个关键词.")

for i, tag_text in enumerate(all_tags):
    line_number = i + 1 # 当前行号 (1-based)

    #确定当前tag的类别
    if line_number > num[current_category_index]:
        if current_category_index < len(category_classes) - 1:
            current_category_index += 1
        # 如果超出最后一个类别定义，则保持在最后一个类别

    category = category_classes[current_category_index]

    print(f"处理中: 行 {line_number}/{len(all_tags)}, 类别: {category}, 标签: {tag_text}")

    # 1. 获取类别的嵌入向量
    category_embedding = get_embedding(category, OPENAI_EMBEDDING_MODEL_NAME)
    if category_embedding is None:
        print(f"跳过标签 {tag_text} 因为无法获取类别 '{category}' 的嵌入.")
        time.sleep(1) # 发生错误时也稍作等待
        continue

    # 2. 获取标签的嵌入向量
    tag_embedding = get_embedding(tag_text, OPENAI_EMBEDDING_MODEL_NAME)
    if tag_embedding is None:
        print(f"跳过标签 {tag_text} 因为无法获取其自身的嵌入.")
        time.sleep(1) # 发生错误时也稍作等待
        continue

    # 3. 向量求和
    combined_embedding = category_embedding + tag_embedding

    # 4. 准备文档和元数据
    # document_content = f"{category} {tag_text}" # 可以考虑是否需要这样的组合文档
    document_content = tag_text # 或者只用tag作为文档内容，元数据中包含类别
    metadata = {"category": category, "original_tag": tag_text,"tag_id": line_number}
    unique_id = f"{category}_{tag_text}_{line_number}" # 创建一个唯一的ID

    # 5. 添加到ChromaDB集合
    try:
        collection.add(
            documents=[document_content],
            embeddings=[combined_embedding.tolist()], # embedding需要是list of list of floats
            metadatas=[metadata],
            ids=[unique_id]
        )
        print(f"成功添加: {unique_id}")
    except Exception as e:
        print(f"添加到ChromaDB失败: {unique_id}, 错误: {e}")

    # API调用之间添加短暂延迟，避免过于频繁请求导致服务器拒绝
    # time.sleep(0.5) # 0.5秒延迟

print("所有标签处理完毕并已尝试存入向量库。")

# 验证 (可选)
# results = collection.query(
# query_texts=["苹果手机"],
# n_results=2
# )
# print("查询 '苹果手机' 的结果:", results)

# 清理 (如果需要)
# client.delete_collection(name=collection_name)
# print(f"集合 {collection_name} 已删除.")