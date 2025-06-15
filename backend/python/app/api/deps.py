from typing import Generator

from app.core.database import SessionLocal

def get_db() -> Generator:
    """
    获取数据库会话的依赖注入函数
    """
    try:
        db = SessionLocal()
        yield db
    finally:
        db.close()