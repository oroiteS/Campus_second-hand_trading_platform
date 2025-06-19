import chromadb

# # 初始化客户端
# client = chromadb.PersistentClient(path = "m_test_db")
# collection = client.create_collection("my_vectors")


persist_directory = "./m_test_db"
collection_name = "my_vectors"
client = chromadb.PersistentClient(path= persist_directory)
collection = client.get_or_create_collection(name=collection_name)


# 1. 插入向量（支持批量）
# collection.add(
#     ids=["id1", "id2"],
#     embeddings=[
#         [0.1, 0.2, 0.3],  # 向量1
#         [0.4, 0.5, 0.6]   # 向量2
#     ],
#     metadatas=[{"name": "item1"}, {"name": "item2"}]  # 可选元数据
# )
# # 2. 查询相似向量
# results = collection.query(
#     query_embeddings=[[0.15, 0.25, 0.35]],  # 查询向量
#     n_results=1  # 返回最相似的1个
# )

# print("相似结果:", results)  # 包含ID、距离、元数据等

# print("\n--- 通过 ID 查询向量 ---")
# retrieved_data = collection.get(ids=["id1", "id2"],include=['embeddings','metadatas'])
# print("通过 ID 查询到的数据:", retrieved_data)
# print(retrieved_data['embeddings'])