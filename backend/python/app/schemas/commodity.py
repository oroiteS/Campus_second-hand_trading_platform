from pydantic import BaseModel
from typing import List,Optional
from datetime import datetime
from decimal import Decimal

class CommodityBase(BaseModel):
    commodity_name:str
    commodity_description:str
    category_id:int
    tags: Optional[List[str]] = None
    current_price:Decimal
    commodity_status:str
    main_image_url:str
    image_list: Optional[List[str]] = None
class CommodityResponse(CommodityBase):
    commodity_id: str
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True