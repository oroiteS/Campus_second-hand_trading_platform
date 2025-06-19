from sqlalchemy.ext.declarative import declarative_base

# 创建 SQLAlchemy Base 类
Base = declarative_base()

# 导入所有模型以便 Alembic 发现
# 这样 Alembic 就能自动检测到所有的模型变更
from .commodity import Commodity  # noqa: E402