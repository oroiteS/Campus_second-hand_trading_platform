#!/usr/bin/env python3
"""
SQL MCP Server - 主入口文件
直接运行此文件启动 MCP 服务器
"""

import sys
import os

# 添加当前目录到Python路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from server import run_server

if __name__ == "__main__":
    print("启动 SQL MCP Server...")
    print("服务器正在运行，按 Ctrl+C 停止")
    try:
        run_server()
    except KeyboardInterrupt:
        print("\n服务器已停止")
    except Exception as e:
        print(f"服务器启动失败: {e}")