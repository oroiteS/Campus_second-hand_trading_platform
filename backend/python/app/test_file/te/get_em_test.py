import chromadb 
import numpy as np
client = chromadb.PersistentClient(path='./embedding_folder')
collection_category = client.get_collection('commodities')
results = collection_category.get(
    include=['embeddings','documents','metadatas']
)
print(results)
