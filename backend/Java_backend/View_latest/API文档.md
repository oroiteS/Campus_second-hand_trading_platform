# View_latest 模块 API 文档

## 项目概述

**项目名称**: View_latest 服务  
**端口**: 8087  
**功能**: 提供最新商品查询和用户信息查询服务  
**API文档地址**: http://localhost:8087/swagger-ui.html  
**跨域策略**: 允许所有来源  

## 技术栈

- **Java版本**: 17
- **Spring Boot版本**: 3.3.4
- **数据库**: MySQL
- **ORM框架**: MyBatis Plus 3.5.8
- **API文档**: SpringDoc OpenAPI (Swagger)
- **构建工具**: Maven

## 数据库配置

- **数据库名**: campus
- **用户名**: campus_test
- **密码**: campus_suep
- **连接URL**: jdbc:mysql://localhost:3306/campus?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true

## API 接口列表

### 1. 商品管理 API

#### 1.1 获取最新在售商品

**接口描述**: 获取最新6个发布的在售商品信息

- **请求方式**: `GET`
- **请求路径**: `/api/commodities/latest`
- **完整URL**: `http://localhost:8087/api/commodities/latest`

**请求参数**: 无

**响应格式**:
```json
{
  "code": 200,
  "message": "获取最新商品成功",
  "data": [
    {
      "commodityId": "商品ID",
      "commodityName": "商品名称",
      "commodityDescription": "商品描述",
      "commodityPrice": 99.99,
      "commodityStatus": "on_sale",
      "commodityCategory": "商品分类",
      "commodityLocation": "商品位置",
      "sellerId": "卖家ID",
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-01T12:00:00",
      "commodityImageUrl": "商品图片URL"
    }
  ]
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| commodityId | String | 商品唯一标识 |
| commodityName | String | 商品名称 |
| commodityDescription | String | 商品详细描述 |
| commodityPrice | BigDecimal | 商品价格 |
| commodityStatus | String | 商品状态（on_sale: 在售） |
| commodityCategory | String | 商品分类 |
| commodityLocation | String | 商品所在位置 |
| sellerId | String | 卖家用户ID |
| createdAt | LocalDateTime | 商品创建时间 |
| updatedAt | LocalDateTime | 商品更新时间 |
| commodityImageUrl | String | 商品图片链接 |

**错误响应**:
```json
{
  "code": 500,
  "message": "获取商品信息失败: 具体错误信息",
  "data": null
}
```

### 2. 用户管理 API

#### 2.1 获取所有用户信息

**接口描述**: 获取所有用户的指定字段信息（User_ID、User_name、telephone、Create_at、User_sta、avatar_url）

- **请求方式**: `GET`
- **请求路径**: `/api/users/all`
- **完整URL**: `http://localhost:8087/api/users/all`

**请求参数**: 无

**响应格式**:
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": [
    {
      "userId": "用户ID",
      "userName": "用户名",
      "telephone": "手机号码",
      "createAt": "2024-01-01T12:00:00",
      "userSta": true,
      "avatarUrl": "头像URL"
    }
  ]
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| userId | String | 用户唯一标识 |
| userName | String | 用户名 |
| telephone | String | 用户手机号码 |
| createAt | LocalDateTime | 用户创建时间 |
| userSta | Boolean | 用户状态（true: 正常, false: 禁用） |
| avatarUrl | String | 用户头像链接 |

**错误响应**:
```json
{
  "code": 500,
  "message": "获取用户信息失败: 具体错误信息",
  "data": null
}
```

## 统一响应格式

所有API接口都遵循统一的响应格式：

```json
{
  "code": "响应状态码",
  "message": "响应消息",
  "data": "响应数据（可为null）"
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 数据库表结构

### 商品表 (commodities)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| commodity_id | VARCHAR | 商品ID（主键） |
| commodity_name | VARCHAR | 商品名称 |
| commodity_description | TEXT | 商品描述 |
| commodity_price | DECIMAL | 商品价格 |
| commodity_status | VARCHAR | 商品状态 |
| commodity_category | VARCHAR | 商品分类 |
| commodity_location | VARCHAR | 商品位置 |
| seller_id | VARCHAR | 卖家ID |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| commodity_image_url | VARCHAR | 商品图片URL |

### 用户表 (users)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| User_ID | VARCHAR | 用户ID（主键） |
| User_name | VARCHAR | 用户名 |
| telephone | VARCHAR | 手机号码 |
| Create_at | DATETIME | 创建时间 |
| User_sta | BOOLEAN | 用户状态 |
| avatar_url | VARCHAR | 头像URL |

## 启动说明

1. **环境要求**:
   - Java 17
   - MySQL 数据库
   - Maven 3.6+

2. **启动步骤**:
   ```bash
   # 编译项目
   mvn clean compile
   
   # 运行项目
   mvn spring-boot:run
   ```

3. **访问地址**:
   - 服务地址: http://localhost:8087
   - API文档: http://localhost:8087/swagger-ui.html
   - 最新商品API: http://localhost:8087/api/commodities/latest
   - 所有用户API: http://localhost:8087/api/users/all

## 注意事项

1. **跨域配置**: 所有API接口都已配置允许跨域访问
2. **数据库连接**: 确保MySQL服务正在运行，且数据库连接配置正确
3. **日志级别**: 开发环境下已配置详细的调试日志
4. **错误处理**: 所有接口都包含完善的异常处理机制
5. **数据查询**: 商品查询仅返回状态为'on_sale'的商品，按创建时间倒序排列

## 开发者信息

- **模块名称**: View_latest
- **主类**: com.campus.view_latest.ViewLatestApplication
- **包结构**:
  - `controller`: 控制器层
  - `service`: 业务逻辑层
  - `mapper`: 数据访问层
  - `entity`: 实体类
  - `common`: 公共类（如统一响应格式）

## 更新日志

- **v1.0.0**: 初始版本，实现基础的商品查询和用户信息查询功能