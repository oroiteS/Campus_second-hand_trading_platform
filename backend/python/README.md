# Qwen3 Embedding 智能推荐系统

基于 Qwen3 Embedding 模型的智能推荐系统，支持文本相似度计算和个性化推荐功能。

## 功能特性

- 🚀 **高性能嵌入模型**: 使用 Qwen3-Embedding-0.6B 模型，在 MTEB 多语言排行榜表现优异
- 🌍 **多语言支持**: 支持中文、英文等 100+ 种语言
- 🎯 **智能推荐**: 基于用户偏好进行个性化推荐
- 📊 **文本相似度**: 高精度的文本语义相似度计算
- 🔧 **易于集成**: 简洁的 API 设计，易于集成到现有项目

## 安装依赖

确保你的 Python 版本 >= 3.12，然后安装依赖：

```bash
# 使用 uv（推荐）
uv sync

# 或使用 pip
pip install torch>=2.0.0 transformers>=4.51.0 sentence-transformers>=2.7.0 numpy>=1.24.0 scikit-learn>=1.3.0
```

## 快速开始

### 运行演示

```bash
python main.py
```

首次运行会自动下载 Qwen3-Embedding-0.6B 模型（约 1.2GB），请确保网络连接正常。

### 基本使用

```python
from main import Qwen3EmbeddingRecommendation

# 初始化推荐系统
recommender = Qwen3EmbeddingRecommendation()

# 文本相似度计算
query = "什么是人工智能？"
documents = [
    "人工智能是计算机科学的一个分支",
    "机器学习是AI的子领域",
    "深度学习使用神经网络"
]

results = recommender.find_similar_items(query, documents, top_k=2)
for doc, score in results:
    print(f"相似度: {score:.4f} - {doc}")

# 个性化推荐
user_preferences = ["喜欢科幻电影", "对AI感兴趣"]
candidate_items = [
    "《黑客帝国》- 关于AI的科幻电影",
    "《泰坦尼克号》- 经典爱情电影"
]

recommendations = recommender.recommend_items(user_preferences, candidate_items)
for item, score in recommendations:
    print(f"推荐度: {score:.4f} - {item}")
```

## API 文档

### Qwen3EmbeddingRecommendation 类

#### 初始化

```python
recommender = Qwen3EmbeddingRecommendation(model_name="Qwen/Qwen3-Embedding-0.6B")
```

**参数:**
- `model_name` (str): Qwen3 embedding 模型名称，可选：
  - `Qwen/Qwen3-Embedding-0.6B` (默认，轻量级)
  - `Qwen/Qwen3-Embedding-4B` (平衡性能)
  - `Qwen/Qwen3-Embedding-8B` (最高性能)

#### 主要方法

##### encode_texts(texts, prompt_name=None)

将文本编码为向量表示。

**参数:**
- `texts` (List[str]): 待编码的文本列表
- `prompt_name` (str, 可选): 提示词名称，如 "query" 用于查询文本

**返回:**
- `np.ndarray`: 文本向量数组

##### find_similar_items(query, documents, top_k=5)

根据查询找到最相似的文档。

**参数:**
- `query` (str): 查询文本
- `documents` (List[str]): 文档列表
- `top_k` (int): 返回前 k 个最相似的结果

**返回:**
- `List[Tuple[str, float]]`: (文档, 相似度分数) 的列表

##### recommend_items(user_preferences, candidate_items, top_k=5)

基于用户偏好推荐物品。

**参数:**
- `user_preferences` (List[str]): 用户偏好描述列表
- `candidate_items` (List[str]): 候选物品描述列表
- `top_k` (int): 推荐数量

**返回:**
- `List[Tuple[str, float]]`: 推荐结果列表

## 应用场景

### 1. 电商推荐

```python
# 用户偏好
user_preferences = ["喜欢户外运动", "关注健康生活"]

# 商品列表
products = [
    "专业跑步鞋 - 适合长距离跑步的轻量化设计",
    "瑜伽垫 - 环保材质，适合家庭健身",
    "游戏手柄 - 高精度控制，游戏体验升级"
]

recommendations = recommender.recommend_items(user_preferences, products)
```

### 2. 内容推荐

```python
# 用户阅读历史
reading_history = ["机器学习算法详解", "深度学习实战"]

# 文章库
articles = [
    "神经网络优化技巧",
    "计算机视觉入门",
    "美食制作指南"
]

user_profile = " ".join(reading_history)
recommendations = recommender.find_similar_items(user_profile, articles)
```

### 3. 问答系统

```python
# 知识库
knowledge_base = [
    "Python 是一种高级编程语言",
    "机器学习需要大量数据训练",
    "深度学习是机器学习的子集"
]

# 用户问题
user_question = "如何开始学习编程？"

answers = recommender.find_similar_items(user_question, knowledge_base, top_k=1)
```

## 性能优化

### 模型选择

根据你的需求选择合适的模型：

- **Qwen3-Embedding-0.6B**: 轻量级，适合资源受限环境
- **Qwen3-Embedding-4B**: 平衡性能和资源消耗
- **Qwen3-Embedding-8B**: 最高精度，适合对准确性要求极高的场景

### 批量处理

```python
# 批量编码以提高效率
texts = ["文本1", "文本2", "文本3"]
embeddings = recommender.encode_texts(texts)
```

### 缓存机制

对于频繁查询的文本，建议实现缓存机制：

```python
import pickle

# 保存编码结果
embeddings = recommender.encode_texts(documents)
with open('embeddings_cache.pkl', 'wb') as f:
    pickle.dump(embeddings, f)

# 加载缓存
with open('embeddings_cache.pkl', 'rb') as f:
    cached_embeddings = pickle.load(f)
```

## 注意事项

1. **首次运行**: 需要下载模型文件，请确保网络连接稳定
2. **内存需求**: 0.6B 模型约需 2GB 内存，4B 模型约需 8GB 内存
3. **GPU 加速**: 如有 GPU，模型会自动使用 GPU 加速
4. **文本长度**: 支持最大 32K tokens 的文本长度

## 故障排除

### 常见问题

**Q: 模型下载失败**
A: 检查网络连接，或尝试使用代理。也可以手动下载模型到本地。

**Q: 内存不足**
A: 尝试使用更小的模型（0.6B），或增加系统内存。

**Q: 运行速度慢**
A: 确保安装了 GPU 版本的 PyTorch，或使用批量处理提高效率。

**Q: transformers 版本错误**
A: 确保 transformers >= 4.51.0，运行 `pip install transformers>=4.51.0` 更新。

## 许可证

本项目基于 Apache 2.0 许可证开源。Qwen3 模型遵循其官方许可证条款。

## 参考资源

- [Qwen3 官方文档](https://qwenlm.github.io/blog/qwen3/) <mcreference link="https://qwenlm.github.io/blog/qwen3/" index="4">4</mcreference>
- [Qwen3-Embedding GitHub](https://github.com/QwenLM/Qwen3-Embedding) <mcreference link="https://github.com/QwenLM/Qwen3-Embedding" index="1">1</mcreference>
- [Hugging Face 模型页面](https://huggingface.co/Qwen/Qwen3-Embedding-0.6B) <mcreference link="https://huggingface.co/Qwen/Qwen3-Embedding-0.6B" index="3">3</mcreference>