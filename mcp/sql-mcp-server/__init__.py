#!/usr/bin/env python3
"""
SQL MCP Server Package
使用 FastMCP 实现的数据库查询服务包
"""

from .server import mcp, run_server
from .tools import (
    list_allowed_tables,
    get_table_schema,
    query_table,
    count_table_rows
)
from .database import (
    get_db_connection,
    validate_table_name,
    sanitize_sql,
    get_allowed_tables
)
from .config import (
    ALLOWED_TABLES,
    DB_PATH,
    MAX_QUERY_LIMIT,
    DEFAULT_QUERY_LIMIT
)

__version__ = "1.0.0"
__author__ = "SQL MCP Server"
__description__ = "使用 FastMCP 实现的安全数据库查询服务"

__all__ = [
    'mcp',
    'run_server',
    'list_allowed_tables',
    'get_table_schema', 
    'query_table',
    'count_table_rows',
    'get_db_connection',
    'validate_table_name',
    'sanitize_sql',
    'get_allowed_tables',
    'ALLOWED_TABLES',
    'DB_PATH',
    'MAX_QUERY_LIMIT',
    'DEFAULT_QUERY_LIMIT'
]