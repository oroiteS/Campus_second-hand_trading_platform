#!/usr/bin/env python3
"""
数据库连接和安全验证模块
"""

import pymysql
from typing import List
from dbutils.pooled_db import PooledDB
from config import DB_CONFIG, CONNECTION_POOL_CONFIG, ALLOWED_TABLES, DANGEROUS_KEYWORDS

# 全局连接池实例
_connection_pool = None

def _get_connection_pool():
    """获取连接池实例（单例模式）"""
    global _connection_pool
    if _connection_pool is None:
        _connection_pool = PooledDB(
            creator=pymysql,
            **CONNECTION_POOL_CONFIG,
            **DB_CONFIG
        )
    return _connection_pool

def get_db_connection():
    """从连接池获取数据库连接"""
    pool = _get_connection_pool()
    return pool.connection()


def validate_table_name(table_name: str) -> bool:
    """验证表名是否在允许列表中"""
    return table_name.lower() in ALLOWED_TABLES


def sanitize_sql(query: str) -> bool:
    """基本的SQL注入防护检查"""
    query_lower = query.lower()
    return not any(keyword in query_lower for keyword in DANGEROUS_KEYWORDS)


def get_allowed_tables() -> List[str]:
    """获取允许访问的表列表"""
    return list(ALLOWED_TABLES)