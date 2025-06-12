#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Qwen3 Embedding 推荐系统使用示例

本文件包含了各种实际应用场景的示例代码，展示如何使用 Qwen3 Embedding 模型
进行智能推荐、文本检索、内容分类等任务。
"""

import json
import numpy as np
from typing import List, Dict, Tuple
from main import Qwen3EmbeddingRecommendation


def example_ecommerce_recommendation():
    """电商推荐系统示例"""
    print("\n" + "=" * 60)
    print("电商推荐系统示例")
    print("=" * 60)
    
    recommender = Qwen3EmbeddingRecommendation()
    
    # 用户画像数据
    user_profiles = {
        "用户A": ["喜欢户外运动", "关注健康生活", "经常购买运动装备"],
        "用户B": ["热爱阅读", "喜欢科技产品", "关注人工智能发展"],
        "用户C": ["美食爱好者", "喜欢烹饪", "追求生活品质"]
    }
    
    # 商品库
    products = [
        "专业跑步鞋 - 轻量化设计，适合长距离跑步，透气舒适",
        "智能手环 - 健康监测，运动追踪，心率检测功能",
        "编程入门书籍 - Python从零开始，适合初学者",
        "无线蓝牙耳机 - 高音质，降噪功能，适合运动使用",
        "厨房料理机 - 多功能切菜，榨汁，搅拌一体机",
        "瑜伽垫 - 环保材质，防滑设计，适合家庭健身",
        "机器学习实战 - 深度学习算法详解与应用",
        "不锈钢炒锅 - 不粘涂层，导热均匀，易清洗",
        "智能音箱 - AI语音助手，智能家居控制",
        "户外背包 - 大容量，防水设计，适合徒步旅行"
    ]
    
    # 为每个用户生成推荐
    for user_name, preferences in user_profiles.items():
        print(f"\n{user_name} 的推荐结果:")
        print(f"用户偏好: {', '.join(preferences)}")
        
        recommendations = recommender.recommend_items(preferences, products, top_k=3)
        
        print("推荐商品:")
        for i, (product, score) in enumerate(recommendations, 1):
            print(f"{i}. 匹配度: {score:.4f}")
            print(f"   商品: {product}")


def example_content_recommendation():
    """内容推荐系统示例"""
    print("\n" + "=" * 60)
    print("内容推荐系统示例")
    print("=" * 60)
    
    recommender = Qwen3EmbeddingRecommendation()
    
    # 用户阅读历史
    reading_history = [
        "深度学习在计算机视觉中的应用",
        "自然语言处理技术发展趋势",
        "机器学习算法优化方法"
    ]
    
    # 文章库
    articles = [
        "Transformer架构详解 - 注意力机制的革命性突破",
        "强化学习在游戏AI中的应用 - AlphaGo背后的技术",
        "计算机视觉目标检测算法对比 - YOLO vs R-CNN",
        "大语言模型的训练技巧 - 从GPT到ChatGPT",
        "推荐系统中的协同过滤算法 - 用户行为分析",
        "区块链技术原理与应用 - 去中心化的未来",
        "量子计算基础概念 - 量子比特与量子纠缠",
        "边缘计算在物联网中的作用 - 降低延迟的关键",
        "神经网络可解释性研究 - 黑盒模型的透明化",
        "联邦学习保护隐私 - 分布式机器学习新范式"
    ]
    
    print(f"用户阅读历史: {', '.join(reading_history)}")
    
    # 基于阅读历史推荐新文章
    user_profile = " ".join(reading_history)
    recommendations = recommender.find_similar_items(user_profile, articles, top_k=5)
    
    print("\n推荐文章:")
    for i, (article, score) in enumerate(recommendations, 1):
        print(f"{i}. 相关度: {score:.4f}")
        print(f"   文章: {article}")


def example_qa_system():
    """问答系统示例"""
    print("\n" + "=" * 60)
    print("智能问答系统示例")
    print("=" * 60)
    
    recommender = Qwen3EmbeddingRecommendation()
    
    # 知识库
    knowledge_base = [
        "Python是一种高级编程语言，语法简洁，易于学习，广泛应用于数据科学、Web开发、人工智能等领域。",
        "机器学习是人工智能的一个分支，通过算法让计算机从数据中学习模式，无需明确编程。",
        "深度学习是机器学习的子集，使用多层神经网络来模拟人脑的学习过程。",
        "自然语言处理(NLP)是计算机科学和人工智能的一个分支，帮助计算机理解、解释和生成人类语言。",
        "计算机视觉是人工智能的一个领域，致力于让机器能够识别和理解图像和视频内容。",
        "数据科学结合了统计学、计算机科学和领域专业知识，从数据中提取有价值的洞察。",
        "云计算是通过互联网提供计算服务，包括服务器、存储、数据库、网络、软件等。",
        "区块链是一种分布式账本技术，通过密码学确保数据的安全性和不可篡改性。",
        "物联网(IoT)是指通过互联网连接的物理设备网络，能够收集和交换数据。",
        "大数据是指传统数据处理应用软件不足以处理的大型或复杂的数据集。"
    ]
    
    # 用户问题
    questions = [
        "什么是人工智能？",
        "如何开始学习编程？",
        "深度学习和机器学习有什么区别？",
        "什么是云计算的优势？",
        "区块链技术有哪些应用？"
    ]
    
    for question in questions:
        print(f"\n问题: {question}")
        
        # 找到最相关的知识
        answers = recommender.find_similar_items(question, knowledge_base, top_k=2)
        
        print("相关答案:")
        for i, (answer, score) in enumerate(answers, 1):
            print(f"{i}. 相关度: {score:.4f}")
            print(f"   答案: {answer}")


def example_document_classification():
    """文档分类示例"""
    print("\n" + "=" * 60)
    print("文档分类系统示例")
    print("=" * 60)
    
    recommender = Qwen3EmbeddingRecommendation()
    
    # 类别定义
    categories = {
        "科技": "科技新闻，人工智能，计算机技术，软件开发，硬件设备",
        "体育": "体育赛事，运动员，足球篮球，健身运动，体育新闻",
        "娱乐": "电影电视，音乐娱乐，明星八卦，综艺节目，娱乐新闻",
        "财经": "股票市场，经济新闻，投资理财，商业资讯，金融政策",
        "健康": "医疗健康，养生保健，疾病预防，营养饮食，心理健康"
    }
    
    # 待分类文档
    documents = [
        "苹果公司发布了最新的iPhone，搭载了更强大的AI芯片，性能提升显著。",
        "NBA总决赛激战正酣，湖人队在主场以108:102击败对手，取得系列赛领先。",
        "新上映的科幻电影《星际穿越2》票房破亿，观众反响热烈。",
        "央行宣布降准0.5个百分点，释放流动性约8000亿元，股市应声上涨。",
        "研究发现，每天坚持30分钟有氧运动可以显著降低心血管疾病风险。",
        "谷歌推出新一代量子计算机，计算能力比传统计算机快数百万倍。",
        "世界杯足球赛进入淘汰赛阶段，各支强队展开激烈角逐。"
    ]
    
    print("文档分类结果:")
    
    for i, doc in enumerate(documents, 1):
        print(f"\n文档 {i}: {doc}")
        
        # 计算与各类别的相似度
        category_scores = []
        for category, description in categories.items():
            similarity = recommender.find_similar_items(doc, [description], top_k=1)[0][1]
            category_scores.append((category, similarity))
        
        # 按相似度排序
        category_scores.sort(key=lambda x: x[1], reverse=True)
        
        print("分类结果:")
        for category, score in category_scores[:3]:
            print(f"  {category}: {score:.4f}")
        
        predicted_category = category_scores[0][0]
        print(f"预测类别: {predicted_category}")


def example_multilingual_support():
    """多语言支持示例"""
    print("\n" + "=" * 60)
    print("多语言支持示例")
    print("=" * 60)
    
    recommender = Qwen3EmbeddingRecommendation()
    
    # 多语言文档库
    multilingual_docs = [
        "人工智能是计算机科学的一个分支，致力于创建智能机器。",  # 中文
        "Artificial intelligence is a branch of computer science that aims to create intelligent machines.",  # 英文
        "L'intelligence artificielle est une branche de l'informatique qui vise à créer des machines intelligentes.",  # 法文
        "人工知能は、知能的な機械を作ることを目的とするコンピュータサイエンスの分野です。",  # 日文
        "Künstliche Intelligenz ist ein Bereich der Informatik, der darauf abzielt, intelligente Maschinen zu schaffen.",  # 德文
    ]
    
    # 多语言查询
    queries = [
        "什么是AI？",  # 中文查询
        "What is machine learning?",  # 英文查询
        "Qu'est-ce que l'apprentissage automatique?",  # 法文查询
    ]
    
    for query in queries:
        print(f"\n查询: {query}")
        results = recommender.find_similar_items(query, multilingual_docs, top_k=3)
        
        print("最相关文档:")
        for i, (doc, score) in enumerate(results, 1):
            print(f"{i}. 相似度: {score:.4f}")
            print(f"   文档: {doc[:50]}...")


def example_batch_processing():
    """批量处理示例"""
    print("\n" + "=" * 60)
    print("批量处理示例")
    print("=" * 60)
    
    recommender = Qwen3EmbeddingRecommendation()
    
    # 大量文本数据
    texts = [
        f"这是第{i}个测试文档，内容关于人工智能和机器学习的应用。"
        for i in range(1, 101)
    ]
    
    print(f"批量处理 {len(texts)} 个文档...")
    
    # 批量编码
    embeddings = recommender.encode_texts(texts)
    
    print(f"编码完成，生成了 {embeddings.shape[0]} 个向量，每个向量维度为 {embeddings.shape[1]}")
    
    # 计算文档间相似度矩阵
    from sklearn.metrics.pairwise import cosine_similarity
    similarity_matrix = cosine_similarity(embeddings)
    
    print(f"相似度矩阵形状: {similarity_matrix.shape}")
    
    # 找到最相似的文档对
    max_similarity = 0
    most_similar_pair = None
    
    for i in range(len(texts)):
        for j in range(i+1, len(texts)):
            if similarity_matrix[i][j] > max_similarity:
                max_similarity = similarity_matrix[i][j]
                most_similar_pair = (i, j)
    
    if most_similar_pair:
        i, j = most_similar_pair
        print(f"\n最相似的文档对 (相似度: {max_similarity:.4f}):")
        print(f"文档 {i+1}: {texts[i]}")
        print(f"文档 {j+1}: {texts[j]}")


def main():
    """运行所有示例"""
    print("Qwen3 Embedding 推荐系统 - 应用示例集合")
    print("=" * 80)
    
    examples = [
        ("1", "电商推荐系统", example_ecommerce_recommendation),
        ("2", "内容推荐系统", example_content_recommendation),
        ("3", "智能问答系统", example_qa_system),
        ("4", "文档分类系统", example_document_classification),
        ("5", "多语言支持", example_multilingual_support),
        ("6", "批量处理", example_batch_processing),
    ]
    
    print("\n可用示例:")
    for num, name, _ in examples:
        print(f"{num}. {name}")
    print("0. 运行所有示例")
    
    try:
        choice = input("\n请选择要运行的示例 (0-6): ").strip()
        
        if choice == "0":
            # 运行所有示例
            for _, name, func in examples:
                print(f"\n正在运行: {name}")
                func()
        elif choice in ["1", "2", "3", "4", "5", "6"]:
            # 运行指定示例
            idx = int(choice) - 1
            _, name, func = examples[idx]
            print(f"\n正在运行: {name}")
            func()
        else:
            print("无效选择，请输入 0-6 之间的数字")
            
    except KeyboardInterrupt:
        print("\n\n程序被用户中断")
    except Exception as e:
        print(f"\n运行出错: {e}")
        print("\n可能的解决方案:")
        print("1. 确保已安装所有依赖: uv sync")
        print("2. 检查网络连接以下载模型")
        print("3. 确保有足够的内存运行模型")
    
    print("\n示例运行完成！")


if __name__ == "__main__":
    main()