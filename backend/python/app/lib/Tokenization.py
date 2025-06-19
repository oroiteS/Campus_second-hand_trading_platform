import jieba
import re
class Tokenization:
    def __init__(self, stopwords_file):
        self.stopwords = self.load_stopwords(stopwords_file)
        jieba.load_userdict("app/stopwords/customed_dict.txt")
    def load_stopwords(self,stopwords_file):
        stopwords = set()
        try:
            with open(stopwords_file, 'r',encoding='utf-8') as f:
                for line in f:
                    word = line.strip()
                    if word:#忽略空行
                        stopwords.add(word)
        except FileNotFoundError:
            print("停用词文件不存在")
            return '1'
        except Exception as e:
            print(f"读取停用词文件时出错: {e}")
            return '1'
        return stopwords
    def preprocess_text(self,text, stopwords):
        """文本预处理：去除停用词"""
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
    def segment_jieba(self,text, stopwords_file):
        """停用词处理+jieba分词"""
        #1.加载停用词
        stopwords = self.load_stopwords(stopwords_file)

        # 2. 预处理文本(去除停用词)
        cleaned_text = self.preprocess_text(text,stopwords)

        #3.使用jieba进行最终分词
        final_words = jieba.lcut(cleaned_text)

        #4.再次过滤
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