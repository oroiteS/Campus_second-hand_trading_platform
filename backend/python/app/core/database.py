from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from typing import Generator

from .config import settings

# 创建数据库引擎
engine = create_engine(
    settings.DATABASE_URL,
    # MySQL连接池配置
    pool_pre_ping=True,  # 连接前检查连接是否有效
    pool_recycle=300,    # 连接回收时间（秒）
    pool_size=5,         # 连接池大小
    max_overflow=10,     # 最大溢出连接数
    echo=True          # 是否打印SQL语句（开发时可设为True）
)

# 创建会话工厂
SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)

# 创建Base类，所有模型都将继承这个类
Base = declarative_base()


# 数据库依赖注入函数
def get_db() -> Generator:
    """
    数据库会话依赖注入函数
    用于FastAPI的Depends()中，自动管理数据库会话的创建和关闭
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# 创建所有表（在应用启动时调用）
# def create_tables():
#     """
#     创建所有数据库表
#     注意：在生产环境中建议使用Alembic进行数据库迁移
#     """
#     Base.metadata.create_all(bind=engine)


# 删除所有表（仅用于测试或重置）
# def drop_tables():
#     """
#     删除所有数据库表
#     警告：这将删除所有数据，仅用于开发和测试
#     """
#     Base.metadata.drop_all(bind=engine)