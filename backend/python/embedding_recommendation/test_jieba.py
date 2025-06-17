import jieba
import re

def load_stopwords(stopwords_file):
    """
    加载停用词文件
    """
    stopwords = set()
    try:
        with open(stopwords_file, 'r', encoding='utf-8') as f:
            for line in f:
                word = line.strip()
                if word:  # 忽略空行
                    stopwords.add(word)
        print(f"成功加载 {len(stopwords)} 个停用词")
    except FileNotFoundError:
        print(f"停用词文件 {stopwords_file} 未找到")
    except Exception as e:
        print(f"加载停用词文件时出错: {e}")
    
    return stopwords

def preprocess_text(text, stopwords):
    """
    文本预处理：去除停用词
    """
    # 1. 基本清理：去除多余空格和换行
    text = re.sub(r'\s+', ' ', text).strip()
    
    # 2. 分割成词（这里简单按空格和标点分割，也可以先用jieba粗分）
    # 方法1：简单分割
    words = re.findall(r'\b\w+\b', text)
    
    # 3. 去除停用词
    filtered_words = [word for word in words if word.lower() not in stopwords and word not in stopwords]
    
    # 4. 重新组合成文本
    cleaned_text = ' '.join(filtered_words)
    
    return cleaned_text

def preprocess_text_advanced(text, stopwords):
    """
    高级文本预处理：先用jieba粗分，再去停用词
    """
    # 1. 基本清理
    text = re.sub(r'\s+', ' ', text).strip()
    
    # 2. 使用jieba进行粗分词
    rough_words = jieba.lcut(text)
    
    # 3. 去除停用词和标点符号
    filtered_words = []
    for word in rough_words:
        word = word.strip()
        # 跳过空字符串、纯标点符号、停用词
        if (word and 
            not re.match(r'^[\s\W]+$', word) and  # 不是纯标点或空白
            word.lower() not in stopwords and 
            word not in stopwords):
            filtered_words.append(word)
    
    # 4. 重新组合
    cleaned_text = ' '.join(filtered_words)
    
    return cleaned_text

def jieba_segment_with_stopwords(text, stopwords_file):
    """
    完整的文本处理流程：停用词预处理 + jieba分词
    """
    # 1. 加载停用词
    stopwords = load_stopwords(stopwords_file)
    
    # 2. 预处理文本（去除停用词）
    cleaned_text = preprocess_text_advanced(text, stopwords)
    print(f"预处理后的文本: {cleaned_text}")
    
    # 3. 使用jieba进行最终分词
    final_words = jieba.lcut(cleaned_text)
    
    # 4. 再次过滤（确保没有遗漏的停用词和空字符）
    final_filtered_words = []
    for word in final_words:
        word = word.strip()
        if (word and 
            len(word) > 0 and
            not word.isspace() and  # 过滤纯空格
            not re.match(r'^[\s\W]+$', word) and
            word.lower() not in stopwords and 
            word not in stopwords):
            final_filtered_words.append(word)
    
    return final_filtered_words

if __name__ == "__main__":
    # 测试文本
    test_text = "圣罗兰口红110ml"
    
    # 停用词文件路径
    stopwords_file = "./stopwords/stopwords.txt"
    customed_dict = "./stopwords/customed_dict.txt"
    jieba.load_userdict(customed_dict)
    print("原始文本:", test_text)
    print("=" * 50)
    
    # 方法1：简单预处理
    print("\n=== 方法1：简单预处理 ===")
    stopwords = load_stopwords(stopwords_file)
    cleaned_simple = preprocess_text(test_text, stopwords)
    print(f"简单预处理后: {cleaned_simple}")
    jieba_result_simple = jieba.lcut(cleaned_simple)
    # 过滤空字符串和空格
    jieba_result_simple = [word.strip() for word in jieba_result_simple if word.strip() and not word.isspace()]
    print(f"jieba分词结果: {jieba_result_simple}")
    
    # 方法2：高级预处理
    print("\n=== 方法2：高级预处理 ===")
    cleaned_advanced = preprocess_text_advanced(test_text, stopwords)
    print(f"高级预处理后: {cleaned_advanced}")
    jieba_result_advanced = jieba.lcut(cleaned_advanced)
    # 过滤空字符串和空格
    jieba_result_advanced = [word.strip() for word in jieba_result_advanced if word.strip() and not word.isspace()]
    print(f"jieba分词结果: {jieba_result_advanced}")
    
    # 方法3：完整流程
    print("\n=== 方法3：完整流程 ===")
    final_result = jieba_segment_with_stopwords(test_text, stopwords_file)
    print(f"最终分词结果: {final_result}")
    
    # 对比原始jieba分词
    print("\n=== 对比：原始jieba分词 ===")
    original_jieba = jieba.lcut(test_text)
    print(f"原始jieba分词: {original_jieba}")
    
    print("\n=== 总结 ===")
    print(f"原始词数: {len(original_jieba)}")
    print(f"去停用词后词数: {len(final_result)}")
    print(f"过滤掉的词数: {len(original_jieba) - len(final_result)}")