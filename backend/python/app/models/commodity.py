from sqlalchemy import Column, String, Text, Integer, DECIMAL, DateTime, JSON
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.sql import func

Base = declarative_base()

# 定义商品数据模型
class Commodity(Base):
    __tablename__ = 'commodities'
    
    # 主键字段
    commodity_id = Column(String(36), primary_key=True)
    
    # 基本信息字段
    commodity_name = Column(String(100))
    commodity_description = Column(Text)
    category_id = Column(Integer)
    tags = Column(JSON)
    
    # 价格和状态字段
    current_price = Column(DECIMAL(10, 2))
    commodity_status = Column(String(50))
    
    # 关联字段
    seller_id = Column(String(9))
    
    # 图片字段
    main_image_url = Column(String(255))
    image_list = Column(JSON)
    
    # 时间戳字段
    created_at = Column(DateTime, default=func.now())
    updated_at = Column(DateTime, default=func.now(), onupdate=func.now())
    
    def __repr__(self):
        return f"<Commodity(id={self.commodity_id}, name={self.commodity_name}, price={self.current_price})>"

    