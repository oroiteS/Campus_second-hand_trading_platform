# FastAPI + SQLAlchemy 商品表CRUD实现指南

## 1. 项目结构规划

```
app/
├── __init__.py
├── main.py                 # FastAPI应用入口
├── core/
│   ├── __init__.py
│   ├── config.py          # 配置文件（数据库连接等）
│   └── database.py        # 数据库连接和会话管理
├── models/
│   ├── __init__.py
│   ├── base.py           # SQLAlchemy Base类
│   └── commodity.py      # 商品模型
├── schemas/
│   ├── __init__.py
│   └── commodity.py      # Pydantic模型（请求/响应）
├── crud/
│   ├── __init__.py
│   └── commodity.py      # 数据库操作函数
├── api/
│   ├── __init__.py
│   ├── deps.py           # 依赖注入（数据库会话等）
│   └── v1/
│       ├── __init__.py
│       ├── api.py        # 路由汇总
│       └── endpoints/
│           ├── __init__.py
│           └── commodities.py  # 商品相关API端点
└── utils/
    ├── __init__.py
    └── helpers.py        # 工具函数
```

## 2. 核心配置步骤

### 2.1 数据库配置 (`core/config.py`)
- 定义数据库连接URL
- 配置环境变量
- 设置应用基本信息

**主要内容：**
```python
class Settings(BaseSettings):
    PROJECT_NAME: str = "Campus Trading Platform"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api/v1"
    
    # 数据库配置
    DATABASE_URL: str = "mysql+pymysql://user:password@localhost:3306/campus"
    
    # 其他配置...
```

### 2.2 数据库连接 (`core/database.py`)
- 创建SQLAlchemy引擎
- 配置会话工厂
- 定义数据库依赖注入函数

**主要功能：**
- `create_engine()`: 创建数据库引擎
- `SessionLocal`: 会话工厂
- `get_db()`: 依赖注入函数

### 2.3 基础模型 (`models/base.py`)
- 定义SQLAlchemy Base类
- 导入所有模型以便Alembic发现

## 3. 模型层实现

### 3.1 SQLAlchemy模型 (`models/commodity.py`)

**需要定义的字段：**
- `commodity_id`: 主键，CHAR(36) UUID类型
- `commodity_name`: VARCHAR(100) 商品名称
- `commodity_description`: TEXT 商品描述
- `category_id`: INT UNSIGNED 分类ID（外键）
- `tags`: JSON 标签数组
- `current_price`: DECIMAL(10,2) 价格
- `commodity_status`: ENUM 商品状态
- `seller_id`: CHAR(9) 卖家ID（外键）
- `main_image_url`: VARCHAR(255) 主图URL
- `image_list`: JSON 图片列表
- `created_at`: DATETIME 创建时间
- `updated_at`: DATETIME 更新时间

**关键配置：**
- 使用`__tablename__ = "commodities"`指定表名
- 配置索引：`Index('idx_commodity_name', 'commodity_name')`
- 外键约束：`ForeignKey('categories.category_id')`
- JSON字段处理：`Column(JSON)`
- 枚举类型：`Column(Enum(CommodityStatus))`
- 默认值和检查约束

### 3.2 Pydantic模型 (`schemas/commodity.py`)

**需要定义的模式：**

1. **CommodityBase**: 基础字段
   ```python
   class CommodityBase(BaseModel):
       commodity_name: str
       commodity_description: Optional[str] = None
       category_id: int
       tags: Optional[List[str]] = None
       current_price: Decimal
       # ...
   ```

2. **CommodityCreate**: 创建商品请求
   - 继承CommodityBase
   - 包含创建时必需的字段

3. **CommodityUpdate**: 更新商品请求
   - 所有字段都是可选的
   - 用于PATCH操作

4. **CommodityInDB**: 数据库中的完整模型
   - 包含所有数据库字段
   - 包含ID和时间戳

5. **Commodity**: 响应模型
   - 继承CommodityInDB
   - 配置`orm_mode = True`

## 4. 数据操作层 (`crud/commodity.py`)

**需要实现的CRUD函数：**

### 4.1 基础CRUD操作
```python
def get_commodity(db: Session, commodity_id: str) -> Optional[Commodity]
def get_commodities(db: Session, skip: int = 0, limit: int = 100) -> List[Commodity]
def create_commodity(db: Session, commodity: CommodityCreate) -> Commodity
def update_commodity(db: Session, commodity_id: str, commodity_update: CommodityUpdate) -> Optional[Commodity]
def delete_commodity(db: Session, commodity_id: str) -> bool
```

### 4.2 业务查询函数
```python
def get_commodities_by_seller(db: Session, seller_id: str) -> List[Commodity]
def get_commodities_by_category(db: Session, category_id: int) -> List[Commodity]
def get_commodities_by_status(db: Session, status: CommodityStatus) -> List[Commodity]
def search_commodities(db: Session, keyword: str) -> List[Commodity]
```

### 4.3 高级查询功能
- 分页查询
- 多条件筛选
- 价格区间查询
- 按时间排序
- 标签搜索

## 5. API层实现 (`api/v1/endpoints/commodities.py`)

**需要实现的端点：**

### 5.1 基础CRUD端点
```python
@router.get("/", response_model=List[schemas.Commodity])
def read_commodities(skip: int = 0, limit: int = 100, db: Session = Depends(get_db))

@router.get("/{commodity_id}", response_model=schemas.Commodity)
def read_commodity(commodity_id: str, db: Session = Depends(get_db))

@router.post("/", response_model=schemas.Commodity)
def create_commodity(commodity: schemas.CommodityCreate, db: Session = Depends(get_db))

@router.put("/{commodity_id}", response_model=schemas.Commodity)
def update_commodity(commodity_id: str, commodity_update: schemas.CommodityUpdate, db: Session = Depends(get_db))

@router.delete("/{commodity_id}")
def delete_commodity(commodity_id: str, db: Session = Depends(get_db))
```

### 5.2 业务端点
```python
@router.get("/seller/{seller_id}", response_model=List[schemas.Commodity])
def read_commodities_by_seller(seller_id: str, db: Session = Depends(get_db))

@router.get("/category/{category_id}", response_model=List[schemas.Commodity])
def read_commodities_by_category(category_id: int, db: Session = Depends(get_db))

@router.get("/search/", response_model=List[schemas.Commodity])
def search_commodities(q: str, db: Session = Depends(get_db))
```

### 5.3 查询参数支持
- 分页：`skip`, `limit`
- 筛选：`status`, `min_price`, `max_price`
- 排序：`sort_by`, `order`
- 搜索：`keyword`, `tags`

## 6. 依赖注入 (`api/deps.py`)

**配置内容：**

### 6.1 数据库会话依赖
```python
def get_db() -> Generator:
    try:
        db = SessionLocal()
        yield db
    finally:
        db.close()
```

### 6.2 其他依赖（可选）
- 用户认证依赖
- 权限检查依赖
- 请求验证依赖

## 7. 数据库迁移

**使用Alembic进行数据库版本管理：**

### 7.1 初始化Alembic
```bash
alembic init alembic
```

### 7.2 配置alembic.ini
- 设置数据库URL
- 配置迁移脚本位置

### 7.3 创建迁移脚本
```bash
alembic revision --autogenerate -m "Create commodities table"
```

### 7.4 应用迁移
```bash
alembic upgrade head
```

## 8. 实现顺序建议

### 第一阶段：基础配置
1. **配置数据库连接** (`core/config.py`, `core/database.py`)
2. **创建基础模型** (`models/base.py`)
3. **设置项目结构** (创建所有必要的目录和`__init__.py`文件)

### 第二阶段：模型定义
4. **定义SQLAlchemy模型** (`models/commodity.py`)
5. **定义Pydantic模式** (`schemas/commodity.py`)
6. **配置数据库迁移** (Alembic设置)

### 第三阶段：业务逻辑
7. **实现CRUD操作** (`crud/commodity.py`)
8. **创建API端点** (`api/v1/endpoints/commodities.py`)
9. **配置路由** (`api/v1/api.py`, `main.py`)

### 第四阶段：测试和优化
10. **编写单元测试**
11. **集成测试**
12. **性能优化**
13. **添加日志和监控**

## 9. 关键技术点

### 9.1 JSON字段处理
```python
# SQLAlchemy模型中
tags = Column(JSON)
image_list = Column(JSON)

# Pydantic模式中
tags: Optional[List[str]] = None
image_list: Optional[List[str]] = None
```

### 9.2 枚举类型处理
```python
# 定义枚举
class CommodityStatus(str, Enum):
    ON_SALE = "on_sale"
    SOLD = "sold"
    OFF_SALE = "off_sale"

# SQLAlchemy中使用
commodity_status = Column(Enum(CommodityStatus), default=CommodityStatus.ON_SALE)
```

### 9.3 UUID处理
```python
import uuid
from sqlalchemy.dialects.mysql import CHAR

# 生成UUID
def generate_uuid():
    return str(uuid.uuid4())

# 模型中使用
commodity_id = Column(CHAR(36), primary_key=True, default=generate_uuid)
```

### 9.4 分页查询实现
```python
def get_commodities_paginated(db: Session, skip: int = 0, limit: int = 100):
    return db.query(Commodity).offset(skip).limit(limit).all()
```

### 9.5 复杂查询示例
```python
def search_commodities_advanced(db: Session, filters: dict):
    query = db.query(Commodity)
    
    if filters.get('keyword'):
        query = query.filter(Commodity.commodity_name.contains(filters['keyword']))
    
    if filters.get('min_price'):
        query = query.filter(Commodity.current_price >= filters['min_price'])
    
    if filters.get('max_price'):
        query = query.filter(Commodity.current_price <= filters['max_price'])
    
    if filters.get('status'):
        query = query.filter(Commodity.commodity_status == filters['status'])
    
    return query.all()
```

### 9.6 错误处理
```python
from fastapi import HTTPException, status

def get_commodity_or_404(db: Session, commodity_id: str):
    commodity = crud.get_commodity(db, commodity_id)
    if not commodity:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Commodity not found"
        )
    return commodity
```

## 10. 性能优化建议

### 10.1 数据库优化
- 合理使用索引（已在建表语句中定义）
- 使用复合索引优化常用查询
- 考虑读写分离

### 10.2 查询优化
- 使用`select_related`和`joinedload`减少N+1查询
- 实现查询缓存
- 分页查询避免大量数据传输

### 10.3 API优化
- 实现响应缓存
- 使用异步处理
- 添加请求限流

## 11. 安全考虑

### 11.1 数据验证
- 使用Pydantic进行输入验证
- 添加业务规则验证
- 防止SQL注入

### 11.2 权限控制
- 实现用户认证
- 添加操作权限检查
- 数据访问控制

### 11.3 数据保护
- 敏感数据加密
- 日志脱敏
- 定期备份

---

这个架构遵循了FastAPI和SQLAlchemy的最佳实践，将关注点分离，便于维护和扩展。按照这个指南逐步实现，可以构建一个健壮、可扩展的商品管理系统。