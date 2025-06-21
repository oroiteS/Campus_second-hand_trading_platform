#!/usr/bin/env python3
"""
配置文件 - SQL MCP Server 配置项
"""

# 配置允许查询的表列表（安全控制）
ALLOWED_TABLES = {
    'commodities',
    'books'
}

# MySQL 云端数据库连接配置
DB_CONFIG = {
    'host': 'rm-cn-smw4b7vtl0001ho.rwlb.rds.aliyuncs.com',  # 服务器地址
    'user': 'campus_test',            # 用户名
    'password': 'Campus_suep2022',        # 密码
    'database': 'campus',   # 数据库名
    'port': 3306,                       # 端口，MySQL默认3306
    'charset': 'utf8mb4',               # 字符集
    'autocommit': True             # 自动提交
}

# 数据库连接池配置（可选）
CONNECTION_POOL_CONFIG = {
    'mincached': 2,        # 启动时开启的空连接数量
    'maxcached': 5,        # 连接池最大可用连接数量
    'maxshared': 3,        # 连接池最大可共享连接数量
    'maxconnections': 6,   # 连接池允许的最大连接数
    'blocking': True,      # 连接池中如果没有可用连接后，是否阻塞等待
    'maxusage': None,      # 一个连接最多被重复使用的次数
    'setsession': [],      # 开始会话前执行的命令列表
    'ping': 0,             # ping MySQL服务端，检查是否服务可用
}

# 查询限制配置
MAX_QUERY_LIMIT = 1000
DEFAULT_QUERY_LIMIT = 100

# SQL安全检查配置
DANGEROUS_KEYWORDS = [
    'drop', 'delete', 'insert', 'update', 'alter', 
    'create', 'truncate', 'exec', 'execute'
]