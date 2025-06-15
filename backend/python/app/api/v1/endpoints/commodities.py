from typing import List
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.crud import commodity as crud_commodity
from app.schemas.commodity import Commodity,Commodity_username,Commodity_id
from app.api.deps import get_db

router = APIRouter()

@router.get("/", response_model=List[Commodity])
def read_commodities(
    db: Session = Depends(get_db)
):
    """
    获取所有商品列表
    """
    commodities = crud_commodity.get_commodities(db)
    return commodities
@router.get("/with_username",response_model=List[Commodity_username])
def read_commodities_username(
    db: Session = Depends(get_db)
):
    """
    获取所有商品列表(带用户名)
    """
    commodities = crud_commodity.get_commodities_username(db)
    return commodities
@router.post("/update_status")
def update_commodity_status(
    commodity_id: str,
    new_status: str,
    db: Session = Depends(get_db)
):
    """
    更新商品状态
    """
    result = crud_commodity.update_commodity_status(db, commodity_id, new_status)
    if result == 0:
        return {"message": "商品状态更新成功"}
    else:
        return {"message": "商品状态更新失败"}
@router.get("/recommendation/{user_id}",response_model=List[Commodity_id])
def get_recommendation_commodity_id(user_id:str,db: Session= Depends(get_db)):
    """给指定用户推送推荐的商品id"""
    

