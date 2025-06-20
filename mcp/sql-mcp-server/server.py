#!/usr/bin/env python3
"""
SQL MCP Server - 主服务器文件
使用 FastMCP 实现的数据库查询服务
提供安全的数据库查询功能，限制可访问的表
"""

from fastmcp import FastMCP
import tools

# 创建 FastMCP 应用
mcp = FastMCP("SQL Database Server")


# 注册工具函数
@mcp.tool()
def list_allowed_tables():
    """列出所有允许查询的表名"""
    return tools.list_allowed_tables()


@mcp.tool()
def get_table_schema(table_name: str):
    """
    获取指定表的结构信息
    
    Args:
        table_name: 表名
        
    Returns:
        包含表结构信息的字典
    """
    return tools.get_table_schema(table_name)


@mcp.tool()
def query_table(table_name: str, limit: int = 100, where_clause: str = ""):
    """
    查询指定表的数据
    
    Args:
        table_name: 表名
        limit: 返回记录数限制 (默认100，最大1000)
        where_clause: WHERE条件子句 (可选，不包含WHERE关键字)
        
    Returns:
        包含查询结果的字典
    """
    return tools.query_table(table_name, limit, where_clause)


@mcp.tool()
def count_table_rows(table_name: str, where_clause: str = ""):
    """
    统计表中的记录数
    
    Args:
        table_name: 表名
        where_clause: WHERE条件子句 (可选，不包含WHERE关键字)
        
    Returns:
        包含统计结果的字典
    """
    return tools.count_table_rows(table_name, where_clause)


def run_server():
    """启动MCP服务器"""
    mcp.run()


if __name__ == "__main__":
    run_server()