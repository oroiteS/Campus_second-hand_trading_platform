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

**接口描述**: 获取最新6个发布的在售商品的完整信息，包含所有字段

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
      "commodityId": "01234567-89ab-cdef-0123-456789abcdef",
      "commodityName": "苹果iPhone 13",
      "commodityDescription": "9成新，无划痕，原装充电器",
      "categoryId": 1,
      "tagsId": "[1,2,3]",
      "currentPrice": 3500.00,
      "commodityStatus": "on_sale",
      "sellerId": "202112345",
      "mainImageUrl": "https://example.com/images/iphone13.jpg",
      "imageList": "[\"https://example.com/images/1.jpg\",\"https://example.com/images/2.jpg\"]",
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-01T12:00:00",
      "quantity": 1,
      "newness": "九成新"
    }
  ]
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| commodityId | String | 商品唯一标识符（UUIDv7） |
| commodityName | String | 商品标题（如"苹果iPhone 13"） |
| commodityDescription | String | 详细描述（颜色、瑕疵等） |
| categoryId | Integer | 关联类别表的外键 |
| tagsId | String | 存储标签ID数组（如[1,2]） |
| currentPrice | BigDecimal | 商品售价（如3500.00） |
| commodityStatus | String | 商品状态：on_sale(在售)/sold(已售)/off_sale(下架) |
| sellerId | String | 关联用户表的外键 |
| mainImageUrl | String | 商品主图链接 |
| imageList | String | 多图链接数组（可选） |
| createdAt | LocalDateTime | 商品发布时间 |
| updatedAt | LocalDateTime | 信息更新时间 |
| quantity | Integer | 商品数量 |
| newness | String | 商品新旧度 |

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
| commodity_id | CHAR(36) | 商品唯一标识符（UUIDv7）（主键） |
| commodity_name | VARCHAR(100) | 商品标题（如"苹果iPhone 13"） |
| commodity_description | TEXT | 详细描述（颜色、瑕疵等） |
| category_id | INT UNSIGNED | 关联类别表的外键 |
| tags_Id | JSON | 存储标签ID数组（如[1,2]） |
| current_price | DECIMAL(10,2) | 商品售价（如3500.00） |
| commodity_status | ENUM | 商品状态：on_sale/sold/off_sale |
| seller_id | CHAR(9) | 关联用户表的外键 |
| main_image_url | VARCHAR(255) | 商品主图链接 |
| image_list | JSON | 多图链接数组（可选） |
| created_at | DATETIME | 商品发布时间 |
| updated_at | DATETIME | 信息更新时间 |
| quantity | INT UNSIGNED | 商品数量 |
| newness | VARCHAR(50) | 商品新旧度 |

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