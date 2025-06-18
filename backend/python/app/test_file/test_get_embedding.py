import chromadb
client = chromadb.PersistentClient('./embedding_folder')
collection = client.get_or_create_collection('user_embeddings')
# 获取所有数据并过滤指定 metadata
try:
    all_data = collection.get(include=['metadatas'])
    print(f"数据库中总共有 {len(all_data['ids'])} 条记录")
    print("\n查找 user_id 为 U10000002 的数据:")
    
    found_count = 0
    for i, metadata in enumerate(all_data['metadatas']):
        if metadata.get('user_id') == 'U10000002':
            found_count += 1
            print(f"\n记录 {found_count}:")
            print(f"ID: {all_data['ids'][i]}")
            print(f"Category: {metadata.get('category', 'N/A')}")
            print(f"Tags: {metadata.get('tags', 'N/A')}")
            print(f"User ID: {metadata.get('user_id', 'N/A')}")
            print(f"Time: {metadata.get('time', 'N/A')}")
            print("-" * 40)
    
    if found_count == 0:
        print("未找到 user_id 为 U10000002 的数据")
    else:
        print(f"\n总共找到 {found_count} 条匹配记录")
        
except Exception as e:
    print(f"获取数据时出错: {e}")