from pydantic import BaseModel
class RegisterRequest(BaseModel):
    user_id: str
    like_tags: list[int]