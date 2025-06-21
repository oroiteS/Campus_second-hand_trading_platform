# SQL MCP Server

使用 FastMCP 实现的安全数据库查询服务，提供限制访问的数据库查询功能。

## 项目结构

```
sql-mcp-server/
├── __init__.py          # 包初始化文件
├── main.py              # 主入口文件
├── server.py            # MCP 服务器主文件
├── tools.py             # MCP 工具函数实现
├── database.py          # 数据库连接和安全验证
├── config.py            # 配置文件
└── README.md            # 项目说明文档
```

## 模块说明

### config.py
- 配置允许查询的表列表
- 数据库文件路径配置
- 查询限制和安全配置

### database.py
- 数据库连接管理
- 表名验证功能
- SQL 注入防护
- 安全检查函数

### tools.py
- 实现所有 MCP 工具函数
- `list_allowed_tables()` - 列出允许的表
- `get_table_schema()` - 获取表结构
- `query_table()` - 查询表数据
- `count_table_rows()` - 统计表行数

### server.py
- FastMCP 应用初始化
- 工具函数注册
- 服务器启动逻辑

### main.py
- 主入口文件
- 直接运行启动服务器

## 安装依赖

```bash
pip install fastmcp pymysql DBUtils
```

## 使用方法

### 1. 直接运行

```bash
python -m sql-mcp-server.main
```

### 2. 作为模块导入

```python
from sql_mcp_server import run_server

# 启动服务器
run_server()
```

### 3. 自定义配置

修改 `config.py` 文件中的配置项：

```python
# 修改允许访问的表
ALLOWED_TABLES = {
    'your_table1',
    'your_table2'
}

# 修改MySQL数据库连接配置
DB_CONFIG = {
    'host': 'your-mysql-host.com',     # 云端MySQL主机地址
    'port': 3306,                      # MySQL端口
    'user': 'your_username',           # 数据库用户名
    'password': 'your_password',       # 数据库密码
    'database': 'your_database',       # 数据库名
    'charset': 'utf8mb4',             # 字符集
    'autocommit': True                # 自动提交
}

# 连接池配置（可选）
CONNECTION_POOL_CONFIG = {
    'pool_name': 'sql_mcp_pool',
    'pool_size': 5,                   # 连接池大小
    'pool_reset_session': True
}
```

## 安全特性

1. **表访问限制**: 只能查询配置中允许的表
2. **SQL 注入防护**: 基本的危险关键字检查
3. **查询数量限制**: 限制单次查询返回的记录数
4. **只读访问**: 只允许 SELECT 和 COUNT 操作

## 可用工具

- `list_allowed_tables()` - 获取允许查询的表列表
- `get_table_schema(table_name)` - 获取表结构信息
- `query_table(table_name, limit, where_clause)` - 查询表数据
- `count_table_rows(table_name, where_clause)` - 统计表行数

## 注意事项

1. 确保MySQL数据库服务可访问
2. 根据实际需求修改 `ALLOWED_TABLES` 配置
3. **生产环境安全建议**：
   - 使用环境变量存储数据库密码
   - 启用SSL连接
   - 限制数据库用户权限（只读权限）
   - 定期更换数据库密码
4. 定期检查和更新安全配置
5. 建议使用连接池提高性能

## 环境变量配置（推荐）

为了提高安全性，建议使用环境变量配置敏感信息：

```python
# 在config.py中使用环境变量
import os

DB_CONFIG = {
    'host': os.getenv('MYSQL_HOST', 'localhost'),
    'port': int(os.getenv('MYSQL_PORT', 3306)),
    'user': os.getenv('MYSQL_USER', 'root'),
    'password': os.getenv('MYSQL_PASSWORD', ''),
    'database': os.getenv('MYSQL_DATABASE', 'test'),
    'charset': 'utf8mb4',
    'autocommit': True
}
```