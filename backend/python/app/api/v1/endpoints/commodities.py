from typing import List
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.crud import commodity as crud_commodity
from app.schemas.commodity import Commodity,Commodity_username,Commodity_id
from app.schemas.SearchCommodityRequest import SearchCommodityRequest
from app.schemas.Response import ResponseBase
from app.schemas.register_Request import RegisterRequest
from app.schemas.BuyCommodityRequest import BuyCommodityRequest
from app.schemas.ClickCommodityRequest import ClickCommodityRequest

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

@router.post("/update_status",response_model=ResponseBase)
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
        return {"code": "0"}
    else:
        return {"code": "1"}

@router.get("/recommendation/{user_id}",response_model=List[Commodity])
def get_recommendation_commodity_id(user_id:str,db: Session= Depends(get_db)):
    """给指定用户推送推荐的商品id"""
    commodity_list = crud_commodity.get_commodity_id(db,user_id)
    return commodity_list
    

@router.post("/search",response_model=List[Commodity])
def get_commodities_by_search(request: SearchCommodityRequest,db: Session = Depends(get_db)):
    """根据用户搜索的商品进行模糊查询、相似度查询与兴趣推荐"""
    results = crud_commodity.get_commodities_by_search(db,request)
    return results  # 直接返回列表，空列表也是有效的

@router.post("/register",response_model=ResponseBase)
def register_user(request:RegisterRequest,db: Session = Depends(get_db)):
    """注册用户时，为用户注册兴趣行为"""

    result = crud_commodity.register_user(db, request)
    if result == 0:
        return {"code": 0}
    else:
        return {"code": 1}

@router.post("/buy",response_model=ResponseBase)
def buy_commodity(request:BuyCommodityRequest,db: Session = Depends(get_db)):
    """用户购买商品时，更新用户兴趣行为"""
    result = crud_commodity.buy_commodity(db, request)
    if result == 0:
        return {"code": 0}
    else:
        return {"code": 1}

@router.post("/click_commodity",response_model=ResponseBase)
def click_commodity(request:ClickCommodityRequest,db: Session = Depends(get_db)):
    """用户点击商品时，更新用户兴趣行为"""
    result = crud_commodity.click_commodity(request,db)
    if result == 0:
        return {"code": 0}
    else:
        return {"code": 1}