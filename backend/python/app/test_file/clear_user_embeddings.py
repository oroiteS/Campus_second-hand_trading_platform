import chromadb
import os
from dotenv import load_dotenv

# 加载 .env 文件中的环境变量
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), '..', '.env'))

folder = "./embedding_folder"
collection_name = "user_embeddings"

# 连接到ChromaDB
chromadb_client = chromadb.PersistentClient(path=folder)

try:
    # 获取集合
    collection = chromadb_client.get_collection(collection_name)
    
    # 获取所有数据的ID
    all_data = collection.get()
    all_ids = all_data['ids']
    
    if all_ids:
        print(f"找到 {len(all_ids)} 条记录，开始删除...")
        
        # 删除所有数据
        collection.delete(ids=all_ids)
        
        print(f"成功删除了 {len(all_ids)} 条记录")
        
        # 验证删除结果
        remaining_data = collection.get()
        print(f"删除后剩余记录数: {len(remaining_data['ids'])}")
    else:
        print("集合中没有数据需要删除")
        
except Exception as e:
    print(f"删除数据时出错: {e}")
    
    # 如果集合不存在，尝试创建一个空集合
    try:
        collection = chromadb_client.get_or_create_collection(collection_name)
        print(f"集合 '{collection_name}' 已重新创建为空集合")
    except Exception as create_error:
        print(f"创建集合时出错: {create_error}")

print("操作完成")