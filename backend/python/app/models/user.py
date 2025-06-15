from sqlalchemy import Column,String,DECIMAL,Integer,TIMESTAMP
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.sql import func

Base = declarative_base()

#定义用户数据模型
class User(Base):
    __tablename__ = 'users'
    #主键字段
    user_id = Column(String(9),primary_key=True,comment='主键，用户ID')

    #基本信息字段
    user_name = Column(String(20),nullable=False,comment='存储用户名')
    password = Column(String(64),nullable=False,comment='sha256加密后的密码')
    telephone = Column(String(11),nullable=False,comment='用户手机号')
    real_name = Column(String(20),nullable=False,comment='用户真实姓名')
    avatar_url = Column(String(255),comment='用户头像url')
    User_Loc_longitude = Column(DECIMAL(9,6),comment='存储经度')
    User_Loc_latitude = Column(DECIMAL(9,6),comment='存储纬度')
    User_sta = Column(Integer,nullable=False,default=0,comment='是否封号，0表示未封号，1表示封号')
    Create_at = Column(TIMESTAMP, nullable=False, server_default=func.now(), comment='创建时间')
    ID = Column(String(18), nullable=False, comment='身份证号')

    def __repr__(self):
        return f"<User(User_ID='{self.User_ID}', User_name='{self.User_name}')>"