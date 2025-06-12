import torch
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from typing import List, Tuple


class Qwen3EmbeddingRecommendation:
    """基于 Qwen3 Embedding 模型的智能推荐系统"""
    
    def __init__(self, model_name: str = "Qwen/Qwen3-Embedding-0.6B"):
        """
        初始化推荐系统
        
        Args:
            model_name: Qwen3 embedding 模型名称
        """
        print(f"正在加载 {model_name} 模型...")
        try:
            # 加载 Qwen3 embedding 模型
            self.model = SentenceTransformer(model_name)
            print("模型加载成功！")
        except Exception as e:
            print(f"模型加载失败: {e}")
            print("请确保已安装所需依赖并有网络连接")
            raise
    
    def encode_texts(self, texts: List[str], prompt_name: str = None) -> np.ndarray:
        """
        将文本编码为向量
        
        Args:
            texts: 待编码的文本列表
            prompt_name: 提示词名称（如 'query' 用于查询）
            
        Returns:
            文本向量数组
        """
        if prompt_name:
            embeddings = self.model.encode(texts, prompt_name=prompt_name)
        else:
            embeddings = self.model.encode(texts)
        return embeddings
    
    def find_similar_items(self, query: str, documents: List[str], top_k: int = 5) -> List[Tuple[str, float]]:
        """
        根据查询找到最相似的文档
        
        Args:
            query: 查询文本
            documents: 文档列表
            top_k: 返回前 k 个最相似的结果
            
        Returns:
            (文档, 相似度分数) 的列表
        """
        # 编码查询（使用 query 提示词）
        query_embedding = self.encode_texts([query], prompt_name="query")
        
        # 编码文档
        doc_embeddings = self.encode_texts(documents)
        
        # 计算相似度
        similarities = cosine_similarity(query_embedding, doc_embeddings)[0]
        
        # 获取最相似的文档
        top_indices = np.argsort(similarities)[::-1][:top_k]
        
        results = []
        for idx in top_indices:
            results.append((documents[idx], float(similarities[idx])))
        
        return results
    
    def recommend_items(self, user_preferences: List[str], candidate_items: List[str], top_k: int = 5) -> List[Tuple[str, float]]:
        """
        基于用户偏好推荐物品
        
        Args:
            user_preferences: 用户偏好描述列表
            candidate_items: 候选物品描述列表
            top_k: 推荐数量
            
        Returns:
            推荐结果列表
        """
        # 将用户偏好合并为一个查询
        combined_preferences = " ".join(user_preferences)
        
        # 找到最相似的物品
        recommendations = self.find_similar_items(combined_preferences, candidate_items, top_k)
        
        return recommendations


def demo_text_similarity():
    """演示文本相似度功能"""
    print("\n=== 文本相似度演示 ===")
    
    # 初始化推荐系统
    recommender = Qwen3EmbeddingRecommendation()
    
    # 示例查询和文档
    queries = [
        "什么是人工智能？",
        "如何学习机器学习？"
    ]
    
    documents = [
        "人工智能是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。",
        "机器学习是人工智能的一个子领域，通过算法让计算机从数据中学习模式。",
        "深度学习使用神经网络来模拟人脑的学习过程。",
        "自然语言处理帮助计算机理解和生成人类语言。",
        "计算机视觉让机器能够识别和理解图像内容。"
    ]
    
    for query in queries:
        print(f"\n查询: {query}")
        results = recommender.find_similar_items(query, documents, top_k=3)
        
        print("最相似的文档:")
        for i, (doc, score) in enumerate(results, 1):
            print(f"{i}. 相似度: {score:.4f}")
            print(f"   文档: {doc}")


def demo_recommendation():
    """演示推荐系统功能"""
    print("\n=== 智能推荐演示 ===")
    
    # 初始化推荐系统
    recommender = Qwen3EmbeddingRecommendation()
    
    # 用户偏好
    user_preferences = [
        "喜欢科幻电影",
        "对人工智能感兴趣",
        "喜欢悬疑剧情"
    ]
    
    # 候选物品（电影）
    candidate_movies = [
        "《黑客帝国》- 一部关于虚拟现实和人工智能的科幻电影",
        "《星际穿越》- 探索时间和空间的科幻巨作",
        "《机械姬》- 关于人工智能和人性的悬疑科幻片",
        "《盗梦空间》- 在梦境中展开的悬疑科幻电影",
        "《泰坦尼克号》- 经典爱情电影",
        "《阿凡达》- 外星世界的科幻冒险电影",
        "《终结者》- 人工智能与人类的对抗",
        "《喜剧之王》- 经典喜剧电影"
    ]
    
    print(f"用户偏好: {', '.join(user_preferences)}")
    
    recommendations = recommender.recommend_items(user_preferences, candidate_movies, top_k=5)
    
    print("\n推荐结果:")
    for i, (movie, score) in enumerate(recommendations, 1):
        print(f"{i}. 匹配度: {score:.4f}")
        print(f"   电影: {movie}")


def main():
    """主函数"""
    print("Qwen3 Embedding 智能推荐系统")
    print("=" * 50)
    
    try:
        # 运行演示
        demo_text_similarity()
        demo_recommendation()
        
        print("\n=== 演示完成 ===")
        print("\n使用说明:")
        print("1. 该系统使用 Qwen3-Embedding-0.6B 模型")
        print("2. 支持中英文等 100+ 种语言")
        print("3. 可用于文本相似度计算和智能推荐")
        print("4. 首次运行需要下载模型，请确保网络连接正常")
        
    except Exception as e:
        print(f"\n运行出错: {e}")
        print("\n可能的解决方案:")
        print("1. 运行 'uv sync' 安装依赖")
        print("2. 确保网络连接正常以下载模型")
        print("3. 检查 Python 版本是否 >= 3.12")


if __name__ == "__main__":
    main()
