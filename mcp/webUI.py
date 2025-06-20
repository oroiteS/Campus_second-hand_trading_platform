import gradio as gr
import asyncio
import os
import inspect
import time
from qwen_agent.gui import WebUI as QwenWebUI


class WebUI(QwenWebUI):
    def __init__(self, bot, chatbot_config=None, chatbot=None, **kwargs):
        self.bot = bot
        self.custom_chatbot_config = chatbot_config or {}
        self.custom_chatbot_component = chatbot
        self.custom_kwargs = kwargs

        # 默认头像
        self.robot_image_path = "./asset/ChatGPT-Logo.jpg"
        self.user_image_path = "./asset/user.jpg"

        if hasattr(chatbot, 'avatar_images') and chatbot.avatar_images:
            self.user_image_path, self.robot_image_path = chatbot.avatar_images

        if not os.path.exists(self.user_image_path):
            self.user_image_path = None
        if not os.path.exists(self.robot_image_path):
            self.robot_image_path = None

        try:
            super().__init__(bot, **kwargs)
        except Exception as e:
            print(f"[Warning] 父类初始化失败，将使用自定义界面: {e}")

        self.demo = self._create_custom_interface()

    def validate_messages_format(self, messages):
        """验证messages格式是否正确"""
        if not isinstance(messages, list) or not all(isinstance(m, dict) for m in messages):
            raise ValueError("输入不是 messages 格式，请确保 chatbot_type='messages' 设置正确")

    async def _chat_function(self, user_input: str, chat_history: list):
        """
        先捕获完整响应，过滤深度思考内容，然后流式输出最终结果
        """
        if not isinstance(chat_history, list):
            chat_history = []

        # 清理和规范化消息历史，确保符合OpenAI格式
        cleaned_history = self._clean_message_history(chat_history)
        current_messages_for_bot = cleaned_history + [{"role": "user", "content": user_input}]

        # 记录开始时间
        start_time = time.time()
        
        # 先显示思考状态
        thinking_message = "🤔 正在思考中..."
        # 只返回当前对话，不包含历史记录
        current_conversation = [
            # {"role": "user", "content": user_input},
            {"role": "assistant", "content": thinking_message}
        ]
        yield current_conversation
        
        try:
            # 捕获完整响应
            full_response = await self._capture_full_response(current_messages_for_bot)
            
            # 计算思考时间
            thinking_time = time.time() - start_time
            
            # 过滤深度思考内容，只保留最终结果
            final_content = self._filter_reasoning_content(full_response)
            
            # 添加思考时间信息
            time_info = f"💭 思考了 {thinking_time:.1f} 秒\n\n"
            final_content_with_time = time_info + final_content
            
            # 流式输出最终结果
            assistant_response_content = ""
            for char in final_content_with_time:
                assistant_response_content += char
                await asyncio.sleep(0.02)  # 稍微慢一点的打字效果
                # 只返回当前对话，不包含历史记录
                current_conversation = [
                    # {"role": "user", "content": user_input},
                    {"role": "assistant", "content": assistant_response_content}
                ]
                yield current_conversation
                
        except Exception as e:
            error_message = f"出错: {e}"
            pass  # Error logged for debugging
            
            # 流式输出错误信息
            error_to_stream = ""
            for char in error_message:
                error_to_stream += char
                await asyncio.sleep(0.01)
                # 只返回当前对话，不包含历史记录
                current_conversation = [
                    # {"role": "user", "content": user_input},
                    {"role": "assistant", "content": error_to_stream}
                ]
                yield current_conversation
    
    async def _capture_full_response(self, messages):
        """捕获完整的模型响应，只获取最终结果"""
        full_response = ""
        all_chunks = []
        
        try:
            # 尝试使用父类方法
            response_generator = super()._chat_fn(messages[-1]["content"], messages[:-1])
            
            if inspect.isgenerator(response_generator):
                # 收集所有响应块
                for chunk in response_generator:
                    all_chunks.append(chunk)
                
                # 只处理最后一个有效的响应块
                if all_chunks:
                    # 从后往前找到最后一个有内容的响应
                    for chunk in reversed(all_chunks):
                        chunk_content = self._extract_content(chunk)
                        if chunk_content and chunk_content.strip():
                            full_response = chunk_content
                            break
            else:
                full_response = self._extract_content(response_generator)
                
        except AttributeError:
            # 回退到bot.run方法
            try:
                bot_response = self.bot.run(messages)
                
                if inspect.isgenerator(bot_response):
                    # 收集所有响应块
                    for chunk in bot_response:
                        all_chunks.append(chunk)
                    
                    # 只处理最后一个有效的响应块
                    if all_chunks:
                        for chunk in reversed(all_chunks):
                            chunk_content = self._extract_content(chunk)
                            if chunk_content and chunk_content.strip():
                                full_response = chunk_content
                                break
                else:
                    full_response = self._extract_content(bot_response)
                    
            except Exception as e:
                raise Exception(f"模型调用失败: {e}")
        
        return full_response
    
    def _filter_reasoning_content(self, content):
        """基于模型输出格式的智能内容过滤"""
        if not content:
            return content
        
        # 检测明确的思考内容特征标识（更严格的判断）
        strong_thinking_indicators = [
            '好的，用户想要', '我需要分析', '让我想想如何', '我应该先',
            '分析用户的需求', '考虑到用户', '首先我需要', '我来帮助用户'
        ]
        
        # 检测自我介绍或正常回复的特征（不应被过滤）
        normal_content_indicators = [
            '我是', '您好', '欢迎', '专注于', '核心功能', '帮助您',
            '需要我帮您', '可以直接告诉我', '想了解的'
        ]
        
        # 检查内容特征
        content_start = content[:300].strip()  # 增加检查范围
        has_strong_thinking = any(indicator in content_start for indicator in strong_thinking_indicators)
        has_normal_content = any(indicator in content_start for indicator in normal_content_indicators)
        
        # 如果包含正常内容标识，直接返回（不过滤）
        if has_normal_content:
            return content
        
        # 只有在明确检测到思考内容时才进行过滤
        is_thinking_content = has_strong_thinking
        
        if is_thinking_content:
            # 尝试提取引号内的最终回复
            import re
            
            # 匹配各种引号格式的内容
            quote_patterns = [
                r'[""](.*?)[""]',  # 中英文双引号
                r'"(.*?)"',         # 标准双引号
                r'「(.*?)」',        # 日式引号
                r'『(.*?)』'         # 另一种日式引号
            ]
            
            for pattern in quote_patterns:
                quoted_matches = re.findall(pattern, content, re.DOTALL)
                if quoted_matches:
                    # 取最后一个匹配的引用内容
                    final_reply = quoted_matches[-1].strip()
                    if final_reply and len(final_reply) > 10:  # 确保不是太短的内容
                        return final_reply
            
            # 如果没有找到引号内容，尝试分隔符方法
            separators = ['\n\n---\n\n', '\n\n', '\n---\n', '---']
            
            for separator in separators:
                if separator in content:
                    parts = content.split(separator)
                    
                    # 返回最后一个非空且不包含思考标识的部分
                    for part in reversed(parts):
                        part_stripped = part.strip()
                        if part_stripped and len(part_stripped) > 20:
                            # 检查这部分是否还包含强思考标识
                            part_start = part_stripped[:100]
                            has_strong_thinking_in_part = any(indicator in part_start for indicator in strong_thinking_indicators)
                            has_normal_content_in_part = any(indicator in part_start for indicator in normal_content_indicators)
                            
                            # 如果包含正常内容标识或不包含强思考标识，则选择这部分
                            if has_normal_content_in_part or not has_strong_thinking_in_part:
                                return part_stripped
            
            # 如果都没找到合适的内容，返回一个默认回复
            return "您好！有什么我可以帮助您的吗？"
        
        # 如果不是思考内容，直接返回
        return content
    
    def _clean_message_history(self, history):
        """清理和规范化消息历史"""
        if not history:
            return []
        
        cleaned_history = []
        for msg in history:
            if isinstance(msg, dict):
                # 确保消息有必要的字段
                if 'role' in msg and 'content' in msg:
                    role = msg['role']
                    content = str(msg['content']).strip()
                    
                    if content:  # 只添加非空内容
                        cleaned_history.append({
                            'role': role,
                            'content': content
                        })
        
        return cleaned_history

    def _extract_content(self, chunk):
        """优化的内容提取方法，智能识别最终输出内容"""
        
        if isinstance(chunk, list) and chunk:
            # 取最后一个元素，通常包含最完整的内容
            item = chunk[-1]
            
            if isinstance(item, dict):
                # 优先提取content字段（最终输出），如果为空则使用reasoning_content
                final_content = item.get('content', '').strip()
                reasoning_content = item.get('reasoning_content', '').strip()
                
                if final_content:
                    return final_content
                elif reasoning_content:
                    return reasoning_content
                    
        elif isinstance(chunk, dict):
            # 直接处理字典类型
            final_content = chunk.get('content', '').strip()
            reasoning_content = chunk.get('reasoning_content', '').strip()
            
            if final_content:
                return final_content
            elif reasoning_content:
                return reasoning_content
                
        elif isinstance(chunk, str):
            return chunk.strip()
            
        elif hasattr(chunk, "content"):
            content = getattr(chunk, "content", "")
            return str(content).strip() if content else ""
        
        # 最后的回退处理
        return ""

    def _create_custom_interface(self):
        suggestions = self.custom_chatbot_config.get("prompt.suggestions", [])
        bot_name = getattr(self.bot, "name", "AI助手")
        bot_description = getattr(self.bot, "description", "您的AI助手")

        with gr.Blocks(title=bot_name, theme="soft") as demo:
            gr.ChatInterface(
                fn=self._chat_function,
                chatbot=gr.Chatbot(
                    label=bot_name,
                    avatar_images=(self.user_image_path, self.robot_image_path),
                    placeholder=f"<strong>{bot_description}</strong><br>欢迎提问。",
                    bubble_full_width=getattr(self.custom_chatbot_component, 'bubble_full_width', False) if self.custom_chatbot_component else False,
                    render_markdown=True,
                    type='messages'  # ✅ Chatbot 使用 OpenAI 格式
                ),
                
                title=bot_name,
                textbox=gr.Textbox(
                    placeholder="请输入您的问题...",
                    show_label=False
                ),
                examples=suggestions if suggestions else None,
                fill_height=True
            )

        return demo

    def run(self, **launch_kwargs):
        default_launch_kwargs = {
            'server_name': '0.0.0.0',
            'server_port': 7860,
            'share': False,
            'inbrowser': True
        }
        default_launch_kwargs.update(launch_kwargs)

        print(f"✅ 启动 {getattr(self.bot, 'name', 'AI')} WebUI")
        print(f"📎 访问地址: http://localhost:{default_launch_kwargs['server_port']}")
        return self.demo.launch(**default_launch_kwargs)
