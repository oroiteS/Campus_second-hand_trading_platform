from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    # 项目基本信息
    PROJECT_NAME: str = "Campus Trading Platform"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api/v1"
    DESCRIPTION: str = "校园二手交易平台API"
    
    # 数据库配置
    DATABASE_URL: str = "mysql+pymysql://campus_test:campus_suep@localhost:3306/campus"
    

    # 安全配置
    # SECRET_KEY: str = "your-secret-key-here-change-in-production"
    # ALGORITHM: str = "HS256"
    # ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    
    # CORS配置
    BACKEND_CORS_ORIGINS: list = [
        "http://localhost:3000",  # Vue.js 开发服务器
        "http://localhost:8080",  # 备用前端端口
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080",
    ]
    
    # 文件上传配置
    UPLOAD_DIR: str = "uploads"
    MAX_FILE_SIZE: int = 10 * 1024 * 1024  # 10MB
    ALLOWED_IMAGE_EXTENSIONS: set = {".jpg", ".jpeg", ".png", ".gif", ".webp"}
    
    # 分页配置
    # DEFAULT_PAGE_SIZE: int = 20
    # MAX_PAGE_SIZE: int = 100
    

    
    class Config:
        env_file = ".env"
        case_sensitive = True
        extra="allow" 

# 创建全局设置实例
settings = Settings()