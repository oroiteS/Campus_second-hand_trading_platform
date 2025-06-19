from pydantic import BaseModel
class ClickCommodityRequest(BaseModel):
    user_id: str
    commodity_id: str
