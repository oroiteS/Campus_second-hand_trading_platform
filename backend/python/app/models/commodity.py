from sqlalchemy import Column, String, Text, Integer, DECIMAL, Enum, DateTime, JSON
from sqlalchemy.sql import func
from app.db.database import Base
import enum

class CommodityStatus(str, enum.Enum):
    on_sale = "on_sale"
    sold = "sold"
    off_sale = "off_sale"

class Commodity(Base):
    __tablename__ = "commodities"
    
    commodity_id = Column(String(36), primary_key=True)
    commodity_name = Column(String(100), nullable=False)
    commodity_description = Column(Text, nullable=False)
    category_id = Column(Integer, nullable=False)
    tags = Column(JSON)
    current_price = Column(DECIMAL(10, 2), nullable=False)
    commodity_status = Column(Enum(CommodityStatus), nullable=False)
    seller_id = Column(String(9), nullable=False)
    main_image_url = Column(String(255), nullable=False)
    image_list = Column(JSON)
    created_at = Column(DateTime, nullable=False, server_default=func.now())
    updated_at = Column(DateTime, nullable=False, server_default=func.now(), onupdate=func.now())