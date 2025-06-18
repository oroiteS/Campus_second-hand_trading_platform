from typing import List
from sqlalchemy.orm import Session
from app.models.commodity import Commodity
from app.models.user import User
from app.schemas.commodity import Commodity_username
from sqlalchemy import select, or_, func
from app.lib.Tokenization import Tokenization
from app.schemas.SearchCommodityRequest import SearchCommodityRequest
from app.lib.get_embedding import Embedder
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
def get_commodity_id(db: Session,user_id: str) -> List[Commodity]:
    """获取推荐的商品list"""
    #部分1-按照用户的购买行为返回list
    embedder = Embedder()
    results_commendation_buy_cid = embedder.recommendation_by_buy(user_id= user_id)
    #部分2-按照用户的喜欢行为返回list
    results_commendation_like_cid = embedder.recommendation_by_like(user_id=user_id,limit=10)
    #合并两个list
    results_commendation_cid = results_commendation_buy_cid + results_commendation_like_cid
    print('合并前',len(results_commendation_cid))
    #去重
    results_commendation_cid = list(set(results_commendation_cid))
    print('合并后',len(results_commendation_cid))
    #返回
    query = select(Commodity).where(Commodity.commodity_id.in_(results_commendation_cid))
    results_commendation = db.execute(query).scalars().all()
    return results_commendation


def get_commodities_by_search(db: Session,request: SearchCommodityRequest):
    """通过搜索获取商品信息"""
    #部分1-模糊查询
    stopwords_file = "app/stopwords/stopwords.txt"
    
    tokenizer = Tokenization(stopwords_file)
    #获取分词list
    token_list = tokenizer.segment_jieba(request.search_content,stopwords_file)
    user_id = request.user_id
    #部分2-按照sql语句进行模糊查询
    # 构建所有token的OR条件列表
    token_conditions = []
    for token in token_list:
        # 只检查商品名称包含token的条件
        token_condition = func.lower(Commodity.commodity_name).like(func.lower(f"%{token}%"))  # 商品名称包含token（不区分大小写）
        # func.lower(token).like(func.lower(f"%{Commodity.commodity_name}%"))  # 方向2：token包含商品名称（注释掉）
        token_conditions.append(token_condition)
    
    query = select(Commodity).filter(
        # 核心条件：任意一个token满足条件即可（OR组合）
        or_(*token_conditions),
        # 必选条件：商品状态为"在售"（与上述条件用AND连接）
        Commodity.commodity_status == "on_sale",
        # 排除条件：不显示用户自己发布的商品
        Commodity.seller_id != user_id
    ).order_by(func.random()).limit(40)#随机取40个
    # 执行查询
    results_search = db.execute(query).scalars().all()
    # 直接返回 SQLAlchemy 对象列表
    #部分3-桶2:返回个性化推荐内容
    embedder = Embedder()
    results_commendation_like_cid = embedder.recommendation_by_like(user_id=user_id)
    print(results_commendation_like_cid)
    query = select(Commodity).where(Commodity.commodity_id.in_(results_commendation_like_cid))
    results_commendation_like = db.execute(query).scalars().all()
    print(results_commendation_like)
    # 合并结果并基于commodity_id去重
    all_results = results_search + results_commendation_like
    
    # 使用字典去重，保持第一次出现的商品
    seen_ids = set()
    results = []
    for commodity in all_results:
        if commodity.commodity_id not in seen_ids:
            seen_ids.add(commodity.commodity_id)
            results.append(commodity)
    
    return results