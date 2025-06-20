#!/usr/bin/env python3
"""
MCP 工具函数模块 - 实现所有数据库查询工具
"""

from typing import List, Dict, Any
from database import (
    get_db_connection, 
    validate_table_name, 
    sanitize_sql, 
    get_allowed_tables
)
from config import MAX_QUERY_LIMIT, DEFAULT_QUERY_LIMIT


def list_allowed_tables() -> List[str]:
    """列出所有允许查询的表名"""
    return get_allowed_tables()


def get_table_schema(table_name: str) -> Dict[str, Any]:
    """
    获取指定表的结构信息
    
    Args:
        table_name: 表名
        
    Returns:
        包含表结构信息的字典
    """
    if not validate_table_name(table_name):
        raise ValueError(f"表 '{table_name}' 不在允许访问的列表中。允许的表: {get_allowed_tables()}")
    
    try:
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # 获取表结构 - 使用MySQL的DESCRIBE语句
            cursor.execute(f"DESCRIBE {table_name}")
            columns = cursor.fetchall()
            
            if not columns:
                raise ValueError(f"表 '{table_name}' 不存在")
            
            schema = {
                'table_name': table_name,
                'columns': []
            }
            
            # MySQL DESCRIBE返回格式: (Field, Type, Null, Key, Default, Extra)
            for col in columns:
                schema['columns'].append({
                    'name': col[0],           # Field
                    'type': col[1],           # Type
                    'not_null': col[2] == 'NO',  # Null (YES/NO)
                    'default_value': col[4],  # Default
                    'primary_key': col[3] == 'PRI',  # Key (PRI/UNI/MUL)
                    'extra': col[5] if len(col) > 5 else None  # Extra (auto_increment等)
                })
            
            return schema
            
    except Exception as e:
        raise RuntimeError(f"获取表结构失败: {str(e)}")


def query_table(table_name: str, limit: int = DEFAULT_QUERY_LIMIT, where_clause: str = "") -> Dict[str, Any]:
    """
    查询指定表的数据
    
    Args:
        table_name: 表名
        limit: 返回记录数限制 (默认100，最大1000)
        where_clause: WHERE条件子句 (可选，不包含WHERE关键字)
        
    Returns:
        包含查询结果的字典
    """
    if not validate_table_name(table_name):
        raise ValueError(f"表 '{table_name}' 不在允许访问的列表中。允许的表: {get_allowed_tables()}")
    
    # 限制查询数量
    limit = min(max(1, limit), MAX_QUERY_LIMIT)
    
    try:
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # 构建查询语句
            query = f"SELECT * FROM {table_name}"
            
            if where_clause:
                # 基本的安全检查
                if not sanitize_sql(where_clause):
                    raise ValueError("WHERE子句包含不安全的SQL关键字")
                query += f" WHERE {where_clause}"
            
            query += f" LIMIT {limit}"
            
            # 执行查询
            cursor.execute(query)
            rows = cursor.fetchall()
            
            # 获取列名
            column_names = [description[0] for description in cursor.description]
            
            # 转换为字典格式
            result = {
                'table_name': table_name,
                'columns': column_names,
                'rows': [dict(zip(column_names, row)) for row in rows],
                'total_rows': len(rows),
                'query': query
            }
            
            return result
            
    except Exception as e:
        raise RuntimeError(f"查询失败: {str(e)}")


def count_table_rows(table_name: str, where_clause: str = "") -> Dict[str, Any]:
    """
    统计表中的记录数
    
    Args:
        table_name: 表名
        where_clause: WHERE条件子句 (可选，不包含WHERE关键字)
        
    Returns:
        包含统计结果的字典
    """
    if not validate_table_name(table_name):
        raise ValueError(f"表 '{table_name}' 不在允许访问的列表中。允许的表: {get_allowed_tables()}")
    
    try:
        with get_db_connection() as conn:
            cursor = conn.cursor()
            
            # 构建查询语句
            query = f"SELECT COUNT(*) FROM {table_name}"
            
            if where_clause:
                # 基本的安全检查
                if not sanitize_sql(where_clause):
                    raise ValueError("WHERE子句包含不安全的SQL关键字")
                query += f" WHERE {where_clause}"
            
            # 执行查询
            cursor.execute(query)
            count = cursor.fetchone()[0]
            
            return {
                'table_name': table_name,
                'count': count,
                'query': query
            }
            
    except Exception as e:
        raise RuntimeError(f"统计失败: {str(e)}")