import requests
import numpy as np

# LM Studio API Configuration
API_URL = "http://127.0.0.1:1234/v1/embeddings"
MODEL_NAME = "text-embedding-qweb3-reranker-4b" # From the screenshot

def get_embedding(text: str, model_name: str, api_url: str):
    """Fetches an embedding for the given text using the LM Studio API."""
    headers = {"Content-Type": "application/json"}
    payload = {
        "input": text,
        "model": model_name
    }
    
    try:
        response = requests.post(api_url, headers=headers, json=payload)
        response.raise_for_status()  # Raise an exception for HTTP errors
        json_response = response.json()
        
        # The response structure for embeddings in OpenAI-compatible APIs is usually:
        # {
        #   "object": "list",
        #   "data": [
        #     {
        #       "object": "embedding",
        #       "index": 0,
        #       "embedding": [0.0023064255, -0.009327292, ...]
        #     }
        #   ],
        #   "model": "text-embedding-qweb3-reranker-4b",
        #   "usage": {...}
        # }
        # We need to extract the actual embedding vector.
        if json_response.get("data") and isinstance(json_response["data"], list) and len(json_response["data"]) > 0:
            embedding_data = json_response["data"][0]
            if isinstance(embedding_data, dict) and "embedding" in embedding_data:
                return embedding_data["embedding"]
            else:
                print(f"Warning: 'embedding' key not found in data item: {embedding_data}")
                return None
        else:
            print(f"Warning: Embedding data not found or in unexpected format in response: {json_response}")
            return None
            
    except requests.exceptions.RequestException as e:
        print(f"Error calling LM Studio API: {e}")
        return None
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return None

def cosine_similarity(vec1, vec2):
    """Computes the cosine similarity between two vectors."""
    if vec1 is None or vec2 is None:
        return 0.0
    vec1 = np.array(vec1)
    vec2 = np.array(vec2)
    if vec1.shape != vec2.shape:
        print(f"Warning: Vectors have different shapes: {vec1.shape} vs {vec2.shape}")
        return 0.0
    if np.linalg.norm(vec1) == 0 or np.linalg.norm(vec2) == 0:
        return 0.0 # Avoid division by zero if a vector is all zeros
    return np.dot(vec1, vec2) / (np.linalg.norm(vec1) * np.linalg.norm(vec2))

def main():
    print(f"Testing Reranker Model: {MODEL_NAME} via Embeddings API at {API_URL}\n")

    query = "高性能笔记本电脑"
    documents = [
        "一款轻薄便携的笔记本，适合日常办公和学习。",
        "专为游戏玩家打造的顶级配置笔记本，显卡性能强劲。",
        "这款笔记本配备了最新的多核处理器和大容量内存，运行大型软件毫无压力，非常适合专业人士。",
        "经济实惠型笔记本，满足基本浏览和文档处理需求。",
        "超长续航笔记本，出差旅行无忧。"
    ]

    print(f"Query: {query}\n")
    print("Documents to rerank:")
    for i, doc in enumerate(documents):
        print(f"  {i+1}. {doc}")
    print("-"*30)

    query_embedding = get_embedding(query, MODEL_NAME, API_URL)

    if query_embedding is None:
        print("Failed to get embedding for the query. Exiting.")
        return

    print(f"Successfully fetched query embedding (dimension: {len(query_embedding)}).\n")

    reranked_results = []

    for doc in documents:
        doc_embedding = get_embedding(doc, MODEL_NAME, API_URL)
        if doc_embedding is not None:
            similarity = cosine_similarity(query_embedding, doc_embedding)
            reranked_results.append({"document": doc, "score": similarity})
            print(f"  Processed document: \"{doc[:30]}...\" - Score: {similarity:.4f}")
        else:
            print(f"  Failed to get embedding for document: \"{doc[:30]}...\"")
            reranked_results.append({"document": doc, "score": 0.0}) # Assign a low score if embedding fails
    
    # Sort documents by score in descending order
    reranked_results.sort(key=lambda x: x["score"], reverse=True)

    print("\n" + "="*10 + " Reranked Results " + "="*10)
    for i, item in enumerate(reranked_results):
        print(f"  {i+1}. Document: {item['document']}")
        print(f"     Score: {item['score']:.4f}\n")

if __name__ == "__main__":
    main()