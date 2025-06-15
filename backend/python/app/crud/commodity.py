from typing import List
from sqlalchemy.orm import Session
from app.models.commodity import Commodity
from app.models.user import User
from app.schemas.commodity import Commodity_username
from sqlalchemy import select

def update_commodity_status(db: Session,commodity_id:str,new_status:str) -> int:
     """更新商品状态"""
     commodity = db.query(Commodity).filter(Commodity.commodity_id == commodity_id).first()
     if commodity:
        commodity.commodity_status = new_status
        db.commit()
        return 0
     else:
        return 1

def get_commodities(db: Session) -> List[Commodity]:
    """获取所有商品"""
    return db.query(Commodity).all()
def get_commodities_username(db: Session) -> List[Commodity_username]:
    """获取所有商品(带用户名)"""
    stmt = (
        select(
            Commodity.commodity_id,
            Commodity.commodity_name,
            Commodity.commodity_description,
            Commodity.category_id,
            Commodity.tags,
            Commodity.current_price,
            Commodity.commodity_status,
            Commodity.seller_id,
            Commodity.main_image_url,
            Commodity.image_list,
            Commodity.created_at,
            Commodity.updated_at,
            User.user_name
        )
        .join(User, Commodity.seller_id == User.user_id)
    )
    results = db.execute(stmt).all() # SQLAlchemy 2.0 风格的执行

    # 将结果转换为 Commodity_username 对象列表
    # Pydantic 的 orm_mode 会尝试从每个 Row 对象中提取数据
    # Row 对象可以像字典一样通过列名访问，也可以像元组一样通过索引访问
    # commodities_with_username = [
    #     Commodity_username.model_validate(row._asdict()) for row in results
    # ]
    return results
def get_commodity_id(db: Session,user_id: str) -> List[Commodity_id]:
    """获取推荐的商品id"""
    
