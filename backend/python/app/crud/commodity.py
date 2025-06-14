from sqlalchemy.orm import Session
from app.models.commodity import Commodity
from typing import List

class CommodityCRUD:
    def get_all_commodities(self,db:Session)->List[Commodity]:
        """获取所有商品信息"""
        return db.query(Commodity).all()
commodity_crud = CommodityCRUD()