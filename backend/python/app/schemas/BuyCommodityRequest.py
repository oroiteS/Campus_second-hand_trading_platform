from pydantic import BaseModel
class BuyCommodityRequest(BaseModel):
    user_id: str
    category_id: int
    tags:list[int]
    