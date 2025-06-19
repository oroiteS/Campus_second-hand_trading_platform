from pydantic import BaseModel
class UploadCommodityRequest(BaseModel):
    seller_id:str
    commodity_id:str
    category_id:int
    tags:list[int]