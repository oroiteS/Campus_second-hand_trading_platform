import chromadb
from dotenv import load_dotenv
import os
import requests
import numpy as np
# from sentence_transformers import CrossEncoder # 删除 reranker 相关的导入

# 加载 .env 文件中的环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

OPENAI_API_BASE = os.getenv("OPENAI_API_BASE")
OPENAI_EMBEDDING_MODEL_NAME = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")

persist_dir = "./embedding_folder"
collection_name = "tags"
# reranker_model_path = os.path.join(os.path.dirname(__file__), 'Qwen3-reranker') # 删除 reranker 模型路径

headers = {"Content-Type": "application/json"}

def get_embedding(text, model_name):
    """获取给定文本的嵌入向量"""
    payload = {"prompt": text, "model": model_name}
    try:
        response = requests.post(f"{OPENAI_API_BASE}/embeddings", headers=headers, json=payload, timeout=60)
        response.raise_for_status()  # 如果请求失败则抛出HTTPError
        return np.array(response.json()['embedding'])
    except requests.exceptions.RequestException as e:
        print(f"获取嵌入向量失败: {text}, 错误: {e}")
        return None
    except KeyError:
        print(f"获取嵌入向量失败: {text}, 响应中没有 'embedding' 键. 响应: {response.text}")
        return None

# 初始化ChromaDB客户端并获取集合
client = chromadb.PersistentClient(path=persist_dir)
try:
    collection = client.get_collection(name=collection_name)
except Exception as e:
    print(f"无法获取集合 '{collection_name}': {e}")
    print("请确保 create_embeddings.py 已成功运行并创建了向量数据库。")
    exit()

# # 初始化Reranker模型 # 删除 reranker 初始化
# try:
#     reranker = CrossEncoder(reranker_model_path, max_length=512)
#     print(f"Reranker模型 '{reranker_model_path}' 加载成功。")
# except Exception as e:
#     print(f"加载Reranker模型失败: {e}")
#     reranker = None

# 定义测试用例
# 每个测试用例是一个元组 (查询文本, 预期类别)
test_cases = [
    ("二手手机", "电子产品"),
    ("九成新笔记本电脑", "电子产品"),
    ("考研数学资料", "图书教材"),
    ("线性代数课本", "图书教材"),
    ("几乎没用过的台灯", "生活用品"),
    ("宿舍小风扇", "生活用品"),
    ("篮球", "运动器材"),
    ("跑步鞋", "运动器材"),
    ("夏季T恤", "服装鞋帽"),
    ("冬季外套", "服装鞋帽"),
    ("补水面膜", "美妆护肤"),
    ("口红", "美妆护肤"),
    ("游戏键盘", "电子产品"), # 跨类别但相关的查询
    ("学习桌", "生活用品") # 另一个生活用品查询
]

print(f"\n--- 开始测试推荐效果 (元数据过滤 + 向量搜索) ---")
for query_text, expected_category in test_cases:
    print(f"\n查询: '{query_text}' (预期类别: {expected_category})")
    query_embedding = get_embedding(query_text, OPENAI_EMBEDDING_MODEL_NAME)

    if query_embedding is not None:
        try:
            # 使用元数据过滤 (where子句) 进行查询，获取更多结果以便去重
            results = collection.query(
                query_embeddings=[query_embedding.tolist()],
                n_results=20, # 获取更多结果以便去重
                where={"category": expected_category}, # 按预期类别过滤
                include=["metadatas", "documents", "distances"]
            )

            print("推荐结果 (先按类别过滤，然后向量搜索):")
            if results and results.get('ids') and results['ids'][0]:
                # 去重处理：使用字典来跟踪已见过的标签
                seen_tags = set()
                unique_results = []
                
                for i in range(len(results['ids'][0])):
                    doc_id = results['ids'][0][i]
                    distance = results['distances'][0][i]
                    metadata = results['metadatas'][0][i]
                    document = results['documents'][0][i]
                    recommended_category = metadata.get('category', '未知类别')
                    original_tag = metadata.get('original_tag', '未知标签')
                    
                    # 只保留未见过的标签
                    if original_tag not in seen_tags:
                        seen_tags.add(original_tag)
                        unique_results.append({
                            'doc_id': doc_id,
                            'distance': distance,
                            'metadata': metadata,
                            'document': document,
                            'recommended_category': recommended_category,
                            'original_tag': original_tag
                        })
                        
                        # 只显示前5个唯一结果
                        if len(unique_results) >= 5:
                            break
                
                # 显示去重后的结果
                for result in unique_results:
                    print(f"  - 推荐标签: '{result['original_tag']}' (文档: '{result['document']}'), 类别: '{result['recommended_category']}', 距离: {result['distance']:.4f}")
                    
                if len(unique_results) == 0:
                    print(f"  在类别 '{expected_category}' 中未找到与 '{query_text}' 相关的推荐结果。")
            else:
                print(f"  在类别 '{expected_category}' 中未找到与 '{query_text}' 相关的推荐结果。")
        except Exception as e:
            print(f"  处理查询时出错: {e}")
    else:
        print(f"  无法为查询 '{query_text}' 获取嵌入向量，跳过此测试用例。")

print("\n--- 测试结束 ---")

# 提示用户如何运行脚本
print("\n要运行此测试脚本，请在终端中导航到此文件所在的目录，然后执行:")
print("python test_embedding_recommendations.py")