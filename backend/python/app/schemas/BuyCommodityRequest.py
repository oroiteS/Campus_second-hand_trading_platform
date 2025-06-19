from pydantic import BaseModel
class BuyCommodityRequest(BaseModel):#其实好像也可以直接传入commodity_id效果相同，但是当时没想到，效率影响不大
    user_id: str
    category_id: int
    tags:list[int]
    