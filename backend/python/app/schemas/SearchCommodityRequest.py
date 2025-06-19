from pydantic import BaseModel
class SearchCommodityRequest(BaseModel):
    user_id:str
    search_content:str