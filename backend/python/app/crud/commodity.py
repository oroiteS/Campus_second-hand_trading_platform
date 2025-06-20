from typing import List
from sqlalchemy.orm import Session
import os
from dotenv import load_dotenv
import pymongo
import datetime
import numpy as np

# Load environment variables from .env file
load_dotenv(os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))), '.env'))
from app.models.commodity import Commodity
from app.models.user import User
from app.schemas.commodity import Commodity_username,Commodity_id,Commodity_username_avatar
from sqlalchemy import select, or_, func
from app.lib.Tokenization import Tokenization
from app.schemas.SearchCommodityRequest import SearchCommodityRequest
from app.schemas.register_Request import RegisterRequest
from app.lib.text_embedding import get_embedding,get_embedding_tag_id,get_embedding_category_id,get_embedding_commodity_id,get_embedding_commodity_id,update_embedding_commodity_id,delete_embedding_commodity_id
from app.schemas.BuyCommodityRequest import BuyCommodityRequest
from app.schemas.ClickCommodityRequest import ClickCommodityRequest
from app.schemas.AddCartRequest import AddCartRequest
from app.schemas.UploadCommodityRequest import UploadCommodityRequest

from app.lib.get_embedding import Embedder
import random
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
            Commodity.tags_Id,
            Commodity.current_price,
            Commodity.commodity_status,
            Commodity.seller_id,
            Commodity.main_image_url,
            Commodity.image_list,
            Commodity.created_at,
            Commodity.updated_at,
            Commodity.newness,
            Commodity.quantity,
            users.user_name
        )
        .join(users, Commodity.seller_id == users.user_id)
    )
    results = db.execute(stmt).all() # SQLAlchemy 2.0 风格的执行

    # 将结果转换为 Commodity_username 对象列表
    # Pydantic 的 orm_mode 会尝试从每个 Row 对象中提取数据
    # Row 对象可以像字典一样通过列名访问，也可以像元组一样通过索引访问
    # commodities_with_username = [
    #     Commodity_username.model_validate(row._asdict()) for row in results
    # ]
    return results
def get_commodity_recommendation(db: Session,user_id: str) -> List[Commodity_username_avatar]:
    """获取推荐的商品list"""
    #部分1-按照用户的购买行为返回list
    embedder = Embedder()
    results_commendation_buy_cid = embedder.recommendation_by_buy(user_id= user_id)
    #部分2-按照用户的喜欢行为返回list
    results_commendation_like_cid = embedder.recommendation_by_like(user_id=user_id,limit=10)

    #合并两个list
    results_commendation_cid = results_commendation_buy_cid + results_commendation_like_cid
    #去重
    results_commendation_cid = list(set(results_commendation_cid))
    #返回
    query = select(Commodity, User.user_name, User.avatar_url).join(User, Commodity.seller_id == User.user_id).where(Commodity.commodity_id.in_(results_commendation_cid))
    results = db.execute(query).all()
    print(results)
    # 将元组结果转换为Commodity_username_avatar对象列表
    results_commendation = []
    for row in results:
        commodity, user_name, avatar_url = row
        commodity_data = commodity.__dict__
        commodity_data['user_name'] = user_name
        commodity_data['avatar_url'] = avatar_url
        results_commendation.append(Commodity_username_avatar(**commodity_data))
    random.shuffle(results_commendation)
    return results_commendation


def get_commodities_by_search(db: Session,request: SearchCommodityRequest) -> List[Commodity_username_avatar]:
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
    query = select(Commodity, User.user_name, User.avatar_url).join(User, Commodity.seller_id == User.user_id).filter(
        # 核心条件：任意一个token满足条件即可（OR组合）
        or_(*token_conditions),
        # 必选条件：商品状态为"在售"（与上述条件用AND连接）
        Commodity.commodity_status == "on_sale",
        # 排除条件：不显示用户自己发布的商品
        Commodity.seller_id != user_id
    ).order_by(func.random()).limit(40)#随机取40个
    # 执行查询
    results_search_raw = db.execute(query).all()
    
    #部分3-桶2:返回个性化推荐内容
    embedder = Embedder()
    results_commendation_like_cid = embedder.recommendation_by_like(user_id=user_id)
    query = select(Commodity, User.user_name, User.avatar_url).join(User, Commodity.seller_id == User.user_id).where(Commodity.commodity_id.in_(results_commendation_like_cid))
    results_commendation_like_raw = db.execute(query).all()
    
    # 桶3 查询结果与特征匹配
    results_commendation_token_cid = embedder.recommendation_by_token(token_list,user_id=user_id)
    query = select(Commodity, User.user_name, User.avatar_url).join(User, Commodity.seller_id == User.user_id).where(Commodity.commodity_id.in_(results_commendation_token_cid))
    results_commendation_token_raw = db.execute(query).all()

    all_results_raw = results_search_raw + results_commendation_like_raw + results_commendation_token_raw

    # 使用字典去重，保持第一次出现的商品
    seen_ids = set()
    results = []
    for row in all_results_raw:
        commodity, user_name, avatar_url = row
        if commodity.commodity_id not in seen_ids:
            seen_ids.add(commodity.commodity_id)
            commodity_data = commodity.__dict__
            commodity_data['user_name'] = user_name
            commodity_data['avatar_url'] = avatar_url
            results.append(Commodity_username_avatar(**commodity_data))
            
    return results

def register_user(db: Session,request:RegisterRequest):
    like_tags = request.like_tags
    user_id = request.user_id
    mongo_uri = os.getenv("MONGODB_URI")
    mongo_db_name = os.getenv("MONGODB_DB_NAME")
    mongo_collection_name = "user_embeddings"
    mongo_client = pymongo.MongoClient(mongo_uri)
    mongo_db = mongo_client[mongo_db_name]
    mongo_collection = mongo_db[mongo_collection_name]
    # 计算标签嵌入向量（累加）
    embedding_tags = np.zeros(2560)
    for tag_id in like_tags:
        tag_embedding = get_embedding_tag_id(tag_id)
        embedding_tags += tag_embedding
    # 构建文档
    now = datetime.datetime.now()
    milliseconds = int(now.timestamp() * 1000)
    record_id = f"{user_id}_like_{milliseconds}"

    doc = {
        "_id": record_id,
        "user_id": user_id,
        "action": "like",
        "embedding": embedding_tags.tolist(),
        "created_at": now,
        "updated_at": now
    }
    # 检查是否已存在相同的记录
    existing_document = mongo_collection.find_one({"user_id": user_id, "action": "like"})
    if existing_document:
        # 如果已存在，可以选择更新或直接返回，这里我们直接返回0表示操作完成（未插入新数据）
        # 或者可以根据需求返回特定代码，例如 2 表示已存在
        return 1
    # 插入文档
    try:
        mongo_collection.insert_one(doc) # 注意这里之前代码用的是 document，但定义的是 doc
    except Exception as e:
        # 考虑打印日志 e
        return 1
    return 0

def buy_commodity(db:Session,request:BuyCommodityRequest):
    '''既要更新购买记录，又要更新用户图像'''
    user_id = request.user_id
    category_id = request.category_id
    tags = request.tags
    mongo_uri = os.getenv("MONGODB_URI")
    mongo_db_name = os.getenv("MONGODB_DB_NAME")
    mongo_collection_name = "user_embeddings"
    mongo_client = pymongo.MongoClient(mongo_uri)
    mongo_db = mongo_client[mongo_db_name]
    mongo_collection = mongo_db[mongo_collection_name]
    #对商品标签求和
    embedding_category = get_embedding_category_id(category_id)
    embedding_tags = np.zeros(2560)
    for tag_id in tags:
        tag_embedding = get_embedding_tag_id(tag_id)
        embedding_tags += tag_embedding
    embedding_sum = embedding_category + embedding_tags
    # 1-更新购买记录
    now = datetime.datetime.now()
    milliseconds = int(now.timestamp() * 1000) 
    record_id = f"{user_id}_buy_{milliseconds}"
    doc = {
        "_id": record_id,
        "user_id": user_id,
        "time": milliseconds,
        "action": "buy",
        "embedding": embedding_sum.tolist(),
        "created_at": now,
        "updated_at": now
    }
    try:
        mongo_collection.insert_one(doc)
    except Exception as e:
        return 1
    #2.更新用户图像
    #查找用户的画像
    user_doc = mongo_collection.find_one({"user_id": user_id,"action":"like"})
    if user_doc:
        # 计算新的嵌入向量(利用0.7与0.3的权重更新)
        new_embedding = 0.7*np.array(user_doc["embedding"]) + 0.3*np.array(embedding_sum)
        # 更新用户文档
        mongo_collection.update_one(
            {"_id": user_doc["_id"]},
            {"$set": {"embedding": new_embedding.tolist(), "updated_at": now}}
        )
    else:
        return 1
    return 0

def click_commodity(request:ClickCommodityRequest,db: Session):
    user_id = request.user_id
    commodity_id = request.commodity_id
    #1-更新用户画像
    mongo_uri = os.getenv("MONGODB_URI")
    mongo_db_name = os.getenv("MONGODB_DB_NAME")
    mongo_collection_name = "user_embeddings"
    mongo_client = pymongo.MongoClient(mongo_uri)
    mongo_db = mongo_client[mongo_db_name]
    mongo_collection = mongo_db[mongo_collection_name]
    #1.1-获取商品嵌入向量
    commodity_embedding = get_embedding_commodity_id(commodity_id)
    #1.2-更新用户画像
    user_doc = mongo_collection.find_one({"user_id": user_id,"action":"like"})
    if user_doc:
        # 计算新的嵌入向量(利用0.92与0.08的权重更新)
        new_embedding = 0.92*np.array(user_doc["embedding"]) + 0.08*np.array(commodity_embedding)
        # 更新用户文档
        now = datetime.datetime.now()
        mongo_collection.update_one(
            {"_id": user_doc["_id"]},
            {"$set": {"embedding": new_embedding.tolist(), "updated_at": now}}
        )
    else:
        return 1
    return 0

def add_cart(request:AddCartRequest,db: Session):#add与click可以考虑优化合并
    user_id = request.user_id
    commodity_id = request.commodity_id
        #1-更新用户画像
    mongo_uri = os.getenv("MONGODB_URI")
    mongo_db_name = os.getenv("MONGODB_DB_NAME")
    mongo_collection_name = "user_embeddings"
    mongo_client = pymongo.MongoClient(mongo_uri)
    mongo_db = mongo_client[mongo_db_name]
    mongo_collection = mongo_db[mongo_collection_name]
    #1.1-获取商品嵌入向量
    commodity_embedding = get_embedding_commodity_id(commodity_id)
    #1.2-更新用户画像
    user_doc = mongo_collection.find_one({"user_id": user_id,"action":"like"})
    if user_doc:
        # 计算新的嵌入向量(利用0.7与0.3的权重更新)
        new_embedding = 0.85*np.array(user_doc["embedding"]) + 0.15*np.array(commodity_embedding)
        # 更新用户文档
        now = datetime.datetime.now()
        mongo_collection.update_one(
            {"_id": user_doc["_id"]},
            {"$set": {"embedding": new_embedding.tolist(), "updated_at": now}}
        )
    else:
        return 1
    return 0

def upload_commodity(request:UploadCommodityRequest,db:Session):
    seller_id = request.seller_id
    commodity_id = request.commodity_id
    category_id = request.category_id
    tags = request.tags
    #更新商品根基向量库
    embedding_category = get_embedding_category_id(category_id=category_id)
    embedding_tags = np.zeros(2560)
    for tag_id in tags:
        tag_embedding = get_embedding_tag_id(tag_id=tag_id)
        embedding_tags += tag_embedding 
    embedding_sum = embedding_category + embedding_tags
    #更新商品向量库
    code = update_embedding_commodity_id(commodity_id,embedding_sum,seller_id)
    return code

def delete_commodity(request:Commodity_id,db:Session):
    commodity_id = request.commodity_id
    #删除商品向量库
    code = delete_embedding_commodity_id(commodity_id)
    return code

def get_username(user_id,db:Session):
    username = db.query(users).filter(users.user_id == user_id).first().username
    return username
