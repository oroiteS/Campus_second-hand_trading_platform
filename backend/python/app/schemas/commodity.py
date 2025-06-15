from typing import Optional, List
from decimal import Decimal
from datetime import datetime
from pydantic import BaseModel
from enum import Enum


class CommodityStatus(str, Enum):
    """商品状态枚举"""
    ON_SALE = "on_sale"
    SOLD = "sold"
    OFF_SALE = "off_sale"


class CommodityBase(BaseModel):
    """商品基础字段"""
    commodity_name: str
    commodity_description: Optional[str] = None
    category_id: int
    tags: Optional[List[str]] = None
    current_price: Decimal
    commodity_status: str # Was CommodityStatus = CommodityStatus.ON_SALE
    seller_id: str
    main_image_url: Optional[str] = None
    image_list: Optional[List[str]] = None


class CommodityCreate(CommodityBase):
    """创建商品请求模型"""
    pass


class CommodityUpdate(BaseModel):
    """更新商品请求模型 - 所有字段都是可选的"""
    commodity_name: Optional[str] = None
    commodity_description: Optional[str] = None
    category_id: Optional[int] = None
    tags: Optional[List[str]] = None
    current_price: Optional[Decimal] = None
    commodity_status: Optional[CommodityStatus] = None
    seller_id: Optional[str] = None
    main_image_url: Optional[str] = None
    image_list: Optional[List[str]] = None


class CommodityInDB(CommodityBase):
    """数据库中的完整商品模型"""
    commodity_id: str
    created_at: datetime
    updated_at: datetime

    class Config:
        orm_mode = True


class Commodity(CommodityInDB):
    """响应模型"""
    pass
class Commodity_username(CommodityInDB):
    """有用户名字段的商品模型"""
    user_name:str