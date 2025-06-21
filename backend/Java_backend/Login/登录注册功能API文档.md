# 校园二手交易平台 - 登录注册功能API文档

## 项目概述

- **项目名称**: 校园二手交易平台 (Campus Second-hand Trading Platform)
- **模块**: 登录注册服务 (Login & Register Service)
- **文档版本**: v1.2
- **更新时间**: 2024年12月16日
- **技术栈**: Java 17, Spring Boot 3.3.4, MySQL 8.0, JWT
- **服务端口**: 8080
- **基础路径**: `/api-8080/user`

## API接口列表

### 1. 用户注册接口

#### 接口信息
- **接口路径**: `POST /api-8080/user/register`
- **接口描述**: 用户注册功能，支持新用户创建账户
- **Content-Type**: `application/json`

#### 请求参数
```json
{
  "userId": "848089153",
  "userName": "testuser",
  "password": "password123",
  "telephone": "13800138001",
  "realName": "测试用户",
  "idCard": "310101199901010001",
  "longitude": 121.506377,
  "latitude": 31.245417
}
```

#### 参数说明
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | String | 是 | 用户ID，9位数字 |
| userName | String | 是 | 用户名，唯一标识 |
| password | String | 是 | 密码 |
| telephone | String | 是 | 手机号码，11位数字 |
| realName | String | 是 | 真实姓名 |
| idCard | String | 是 | 身份证号码，18位 |
| longitude | Double | 默认 | 经度坐标 |
| latitude | Double | 默认 | 纬度坐标 |

#### 响应示例
**成功响应 (200)**
```json
{
  "message": "注册成功！用户ID: 848089153",
  "userId": "848089153"
}
```

**失败响应 (400)**
```json
{
  "error": "用户名已存在"
}
```

### 2. 用户登录接口

#### 接口信息
- **接口路径**: `POST /api-8080/user/login`
- **接口描述**: 用户登录功能，返回JWT Token
- **Content-Type**: `application/json`

#### 请求参数
```json
{
  "username": "testuser",
  "password": "password123"
}
```

#### 参数说明
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

#### 响应示例
**成功响应 (200)**
```json
{
  "message": "登录成功！",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**失败响应 (401)**
```json
{
  "error": "用户名或密码错误"
}
```

### 3. 获取用户信息接口

#### 接口信息
- **接口路径**: `GET /api-8080/user/info`
- **接口描述**: 获取当前登录用户的基本信息
- **认证方式**: Bearer Token (JWT)

#### 请求头
```
Authorization: Bearer {token}
```

#### 响应示例
**成功响应 (200)**
```json
{
  "userId": "848089153",
  "userName": "testuser",
  "telephone": "13800138001",
  "realName": "测试用户"
}
```

### 4. 获取用户头像接口

#### 接口信息
- **接口路径**: `GET /api-8080/user/avatar`
- **接口描述**: 获取当前登录用户的头像URL
- **认证方式**: Bearer Token (JWT)

#### 请求头
```
Authorization: Bearer {token}
```

#### 响应示例
**成功响应 (200)**
```json
{
  "avatarUrl": "http://example.com/avatar/user123.jpg"
}
```

### 5. 检查用户名可用性

#### 接口信息
- **接口路径**: `GET /api-8080/user/check-username?username={username}`
- **接口描述**: 检查用户名是否已被使用

#### 响应示例
```json
{
  "available": true,
  "message": "用户名可用"
}
```

### 6. 检查手机号可用性

#### 接口信息
- **接口路径**: `GET /api-8080/user/check-telephone?telephone={telephone}`
- **接口描述**: 检查手机号是否已被使用

#### 响应示例
```json
{
  "available": false,
  "message": "手机号已被使用"
}
```

### 7. 检查身份证号可用性

#### 接口信息
- **接口路径**: `GET /api-8080/user/check-idcard?idCard={idCard}`
- **接口描述**: 检查身份证号是否已被使用

#### 响应示例
```json
{
  "available": true,
  "message": "身份证号可用"
}
```

### 8. 检查用户ID可用性

#### 接口信息
- **接口路径**: `GET /api-8080/user/check-userid?userId={userId}`
- **接口描述**: 检查用户ID是否已被使用

#### 响应示例
```json
{
  "available": true,
  "message": "用户ID可用"
}
```

### 9. Token验证接口

#### 接口信息
- **接口路径**: `POST /api-8080/user/validate-token`
- **接口描述**: 验证JWT Token的有效性
- **认证方式**: Bearer Token (JWT)

#### 请求头
```
Authorization: Bearer {token}
```

#### 响应示例
**成功响应 (200)**
```json
{
  "valid": true,
  "userId": "848089153"
}
```

## 数据模型

### User 用户实体
```json
{
  "userId": "String",           // 用户ID，9位数字
  "userName": "String",         // 用户名，唯一
  "password": "String",         // 密码（加密存储）
  "telephone": "String",        // 手机号码，11位
  "realName": "String",         // 真实姓名
  "idCard": "String",           // 身份证号码，18位
  "avatarUrl": "String",        // 头像URL
  "userLocLongitude": "Double", // 经度坐标
  "userLocLatitude": "Double",  // 纬度坐标
  "userSta": "Byte",            // 用户状态 (1:正常, 0:禁用)
  "createAt": "DateTime",       // 创建时间
  "updatedAt": "DateTime"       // 更新时间
}
```

## 错误码说明

### HTTP状态码
| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或Token无效 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 业务错误码
| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 1001 | 用户名已存在 | 注册时用户名重复 |
| 1002 | 手机号已存在 | 注册时手机号重复 |
| 1003 | 身份证号已存在 | 注册时身份证号重复 |
| 1004 | 用户ID已存在 | 注册时用户ID重复 |
| 1005 | 用户名或密码错误 | 登录失败 |
| 1006 | Token无效或已过期 | JWT验证失败 |
| 1007 | 用户不存在 | 查询用户信息失败 |
| 1008 | 参数验证失败 | 请求参数格式错误 |

## 使用示例

### PowerShell 测试命令

#### 1. 用户注册
```powershell
$body = @'
{
  "userId": "848089153",
  "userName": "testuser",
  "password": "password123",
  "telephone": "13800138001",
  "realName": "测试用户",
  "idCard": "310101199901010001",
  "longitude": 121.506377,
  "latitude": 31.245417
}
'@

Invoke-RestMethod -Uri "http://localhost:8080/api-8080/user/register" -Method POST -ContentType "application/json" -Body $body
```

#### 2. 用户登录
```powershell
$loginBody = @'
{
  "username": "testuser",
  "password": "password123"
}
'@

$response = Invoke-RestMethod -Uri "http://localhost:8080/api-8080/user/login" -Method POST -ContentType "application/json" -Body $loginBody
$token = $response.token
```

#### 3. 获取用户信息
```powershell
$headers = @{"Authorization" = "Bearer $token"}
Invoke-RestMethod -Uri "http://localhost:8080/api-8080/user/info" -Method GET -Headers $headers
```

#### 4. 获取用户头像
```powershell
$headers = @{"Authorization" = "Bearer $token"}
Invoke-RestMethod -Uri "http://localhost:8080/api-8080/user/avatar" -Method GET -Headers $headers
```

### cURL 测试命令

#### 1. 用户注册
```bash
curl -X POST http://localhost:8080/api-8080/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "848089153",
    "userName": "testuser",
    "password": "password123",
    "telephone": "13800138001",
    "realName": "测试用户",
    "idCard": "310101199901010001",
    "longitude": 121.506377,
    "latitude": 31.245417
  }'
```

#### 2. 用户登录
```bash
curl -X POST http://localhost:8080/api-8080/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

#### 3. 获取用户信息
```bash
curl -X GET http://localhost:8080/api-8080/user/info \
  -H "Authorization: Bearer {your_token_here}"
```

## 技术说明

### 认证机制
- **JWT Token**: 使用JSON Web Token进行用户身份验证
- **Token过期时间**: 1小时（可配置）
- **加密算法**: HS256
- **密码加密**: BCrypt算法

### 数据验证
- **用户ID**: 必须为9位数字
- **用户名**: 2-20个字符，支持中英文
- **密码**: 6-50个字符
- **手机号**: 11位数字，符合中国手机号格式
- **身份证号**: 18位，符合中国身份证号格式

### 数据库设计
- **字符集**: UTF8MB4
- **存储引擎**: InnoDB
- **索引**: 用户名、手机号、身份证号、用户ID均建立唯一索引
- **时间字段**: 使用DATETIME(6)支持微秒精度

## 部署指南

### 环境要求
- **Java**: JDK 17+
- **数据库**: MySQL 8.0+
- **内存**: 最小512MB
- **端口**: 8080（可配置）

### 配置文件
```yaml
# application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trading?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: your_jwt_secret_key
  expiration: 3600000  # 1小时
```

### 启动命令
```bash
# 编译打包
mvn clean package -DskipTests

# 启动应用
java -jar target/Login-0.0.1-SNAPSHOT.jar

# 后台启动
nohup java -jar target/Login-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### 健康检查
```bash
# 检查应用状态
curl http://localhost:8080/actuator/health

# 检查数据库连接
curl http://localhost:8080/api-8080/user/check-username?username=test
```

## 数据库表结构

### users表结构
```sql
CREATE TABLE users (
    User_ID            CHAR(9)         NOT NULL PRIMARY KEY COMMENT '用户ID，9位数字',
    User_name          VARCHAR(20)     NOT NULL UNIQUE COMMENT '用户名，唯一标识',
    password           VARCHAR(255)    NOT NULL COMMENT '密码（BCrypt加密）',
    telephone          VARCHAR(11)     NOT NULL UNIQUE COMMENT '手机号码，11位数字',
    real_name          VARCHAR(10)     NULL COMMENT '真实姓名',
    avatar_url         VARCHAR(255)    NULL COMMENT '头像URL',
    User_Loc_longitude DECIMAL(10,7)   NULL COMMENT '经度坐标',
    User_Loc_latitude  DECIMAL(10,7)   NULL COMMENT '纬度坐标',
    User_sta           TINYINT(1)      DEFAULT 1 COMMENT '用户状态 (1:正常, 0:禁用)',
    Create_at          DATETIME(6)     DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at         DATETIME(6)     NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    ID                 VARCHAR(18)     NULL UNIQUE COMMENT '身份证号码，18位'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 创建索引
CREATE INDEX idx_user_name ON users(User_name);
CREATE INDEX idx_telephone ON users(telephone);
CREATE INDEX idx_id_card ON users(ID);
CREATE INDEX idx_user_id ON users(User_ID);
CREATE INDEX idx_create_time ON users(Create_at);
```

## 安全说明

### 数据安全
- **密码存储**: 使用BCrypt算法加密，不存储明文密码
- **SQL注入防护**: 使用MyBatis-Plus参数化查询
- **XSS防护**: 对用户输入进行验证和转义
- **CSRF防护**: 使用JWT Token进行状态验证

### 接口安全
- **认证机制**: JWT Token认证，支持Token过期
- **参数验证**: 严格的输入参数验证
- **错误处理**: 统一的错误响应格式，不泄露敏感信息
- **日志记录**: 记录关键操作日志，便于审计

## 性能优化

### 数据库优化
- **索引策略**: 为常用查询字段建立索引
- **连接池**: 使用HikariCP连接池
- **查询优化**: 避免N+1查询问题
- **缓存策略**: 可集成Redis进行数据缓存

### 应用优化
- **异步处理**: 非关键操作使用异步处理
- **资源管理**: 合理配置JVM参数
- **监控告警**: 集成Actuator进行健康检查

## 版本信息

### 当前版本: v1.2
- **发布日期**: 2024年12月16日
- **主要功能**: 用户注册、登录、信息查询、头像获取
- **技术栈**: Spring Boot 3.3.4, MyBatis-Plus, JWT, BCrypt
- **数据库**: MySQL 8.0
- **Java版本**: JDK 17

### 更新日志
- **v1.2** (2024-12-16)
  - 调整JWT Token过期时间从30分钟改为1小时
  - 进一步优化用户体验，减少频繁登录需求
  - 更新配置文档和技术说明

- **v1.1** (2024-12-16)
  - 调整JWT Token过期时间从24小时改为30分钟
  - 优化用户体验，平衡安全性与便利性
  - 更新配置文档和技术说明

- **v1.0** (2024-12-15)
  - 初始版本发布
  - 实现用户注册功能（支持用户自定义userId）
  - 实现用户登录功能（JWT Token认证）
  - 实现用户信息查询功能
  - 实现用户头像获取功能
  - 实现各种可用性检查接口
  - 完善数据验证和错误处理
  - 优化数据库表结构和索引

## 联系信息

### 技术支持
- **项目地址**: Campus Second-hand Trading Platform
- **文档维护**: 开发团队
- **更新频率**: 根据需求定期更新

### 开发团队
- **后端开发**: Java Spring Boot团队
- **数据库设计**: 数据架构团队
- **API设计**: 接口设计团队

---

**文档版本**: v1.2  
**最后更新**: 2024年12月16日  
**文档状态**: ✅ 完整可用  
**API状态**: 🚀 生产就绪