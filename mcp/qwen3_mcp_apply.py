# qwen3_mcp_multi.py
from qwen_agent.agents.assistant import Assistant
from qwen_agent.utils.output_beautify import typewriter_print
import os
from dotenv import load_dotenv
BOT_IMAGE_PATH = "./asset/user_avatar.jpg"
USER_IMAGE_PATH = "./asset/robot.jpg"

def init_agent_service():
    load_dotenv()
    api_key = os.getenv("sk-api")
    llm_cfg = {
        'model': 'Qwen/Qwen3-30B-A3B',
        'model_server': 'https://api.siliconflow.cn/v1',
        'api_key': api_key,
        # 'model': 'Qwen3:1.7B',
        # 'model_server': 'http://localhost:11434/v1',
        # 'api_key': 'ollama',
    }

    tools = [{
       "mcpServers": {
            "sql-database": {
                "command": "python",
                "args": ["./server/sql-mcp-server/main.py"]
            },
       }
    },
        # "code_interpreter"
    ]

    system = """
                    你是一个商品推销小助手 \
        你可以调用数据库工具查询数据库商品，并为用户合理推荐商品\
        如果有学生找你询问他本学期(他讲的可能是例如大一上这种)要购买的书本，你可以通过books数据表并根据他提供的信息进行查找，然后告诉他\
        如果它的想法是找商品，你就去commodities表里查找结果，告诉他\
        在回答时不要透露任何数据库字段和表结构信息，只告诉用户结果即可
    """

    bot = Assistant(
        llm=llm_cfg,
        name='性感助手范佳琪',
        description='具备商品查询和推荐能力',
        system_message=system,
        function_list=tools,
    )

    return bot


def run_query(query=None):
    # 定义数据库助手
    bot = init_agent_service()

    # from qwen_agent.gui import WebUI
    from webUI import WebUI

    chatbot_config = {
        'prompt.suggestions': [
            '请为我推荐电子产品',
            '价格在2000元以下的手机推荐',
            '我喜欢文学，有什么推荐商品'
        ]
    }
    WebUI(
        bot,
        chatbot_config=chatbot_config,
    ).run()


if __name__ == '__main__':
    run_query()
