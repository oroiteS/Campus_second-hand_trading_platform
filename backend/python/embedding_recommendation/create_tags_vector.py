import chromadb
import requests
import os
from dotenv import load_dotenv
from chromadb.utils import embedding_functions

# 加载环境变量
load_dotenv()

class OllamaEmbeddingFunction(embedding_functions.EmbeddingFunction):
    def __init__(self, api_base: str, model_name: str):
        self.api_base = api_base
        self.model_name = model_name

    def __call__(self, input):
        if not input:
            return []
        
        # 确保input是列表格式
        if isinstance(input, str):
            input = [input]
        
        texts = input
        
        embeddings = []
        
        # 逐个处理每个文本，因为Ollama可能不支持批量处理
        for text in texts:
            headers = {"Content-Type": "application/json"}
            payload = {"prompt": text, "model": self.model_name}
            
            try:
                response = requests.post(f"{self.api_base}/embeddings", headers=headers, json=payload)
                response.raise_for_status()
                
                json_response = response.json()
                if 'embedding' in json_response:
                    embeddings.append(json_response['embedding'])
                else:
                    print(f"警告：未找到嵌入数据，响应: {json_response}")
                    # 返回一个默认的嵌入向量（全零向量）
                    embeddings.append([0.0] * 384)  # 假设384维
            except Exception as e:
                print(f"调用Ollama API失败: {e}")
                # 返回一个默认的嵌入向量
                embeddings.append([0.0] * 384)
        
        return embeddings

def main():
    # 读取标签文件
    tags_file = "tags.txt"
    with open(tags_file, 'r', encoding='utf-8') as f:
        tags = [line.strip() for line in f if line.strip()]
    
    print(f"读取到 {len(tags)} 个标签: {tags}")
    
    # 获取配置
    api_base = os.getenv("OPENAI_API_BASE")
    model_name = os.getenv("OPENAI_EMBEDDING_MODEL_NAME")
    
    if not api_base or not model_name:
        print("错误：请在.env文件中配置OPENAI_API_BASE和OPENAI_EMBEDDING_MODEL_NAME")
        return
    
    print(f"使用模型: {model_name} @ {api_base}")
    
    # 创建嵌入函数
    embedding_func = OllamaEmbeddingFunction(api_base, model_name)
    
    # 初始化ChromaDB
    client = chromadb.PersistentClient(path="tags_vector_db/")
    
    # 创建集合
    collection = client.get_or_create_collection(
        name="tags_collection",
        embedding_function=embedding_func,
        metadata={"hnsw:space": "cosine"}
    )
    
    # 清空现有数据（如果有）
    try:
        collection.delete()
    except:
        pass
    
    # 添加标签到向量数据库
    ids = [f"tag_{i}" for i in range(len(tags))]
    collection.add(
        documents=tags,
        ids=ids
    )
    
    print(f"成功添加 {collection.count()} 个标签到向量数据库")
    
    # 测试查询
    query = "学习用品"
    results = collection.query(
        query_texts=[query],
        n_results=3
    )
    
    print(f"\n查询 '{query}' 的结果:")
    for i, doc in enumerate(results['documents'][0]):
        distance = results['distances'][0][i]
        print(f"  {i+1}. {doc} (距离: {distance:.4f})")

if __name__ == "__main__":
    main()