# 商品信息查看功能 API 文档

## 概述

本API提供校园二手交易平台的商品信息查看功能，支持根据商品ID获取商品详细信息。

## 基础信息

- **服务名称**: View Product Information Service
- **基础路径**: `/api/commodity`
- **协议**: HTTP/HTTPS
- **数据格式**: JSON

## API 接口

### 1. 查看商品详情

#### 接口描述
根据商品ID获取商品的详细信息，包括商品基本信息和类别名称。

#### 请求信息
- **URL**: `/api/commodity/detail/{commodityId}`
- **方法**: `GET`
- **Content-Type**: `application/json`

#### 路径参数

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| commodityId | String | 是 | 商品唯一标识符 |

#### 请求示例

```http
GET /api/commodity/detail/01234567-89ab-cdef-0123-456789abcdef
Content-Type: application/json
```

#### 响应格式

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "commodityId": "01234567-89ab-cdef-0123-456789abcdef",
    "commodityName": "苹果iPhone 13",
    "commodityDescription": "95新，国行，无拆修，功能正常",
    "categoryId": 1,
    "categoryName": "数码产品",
    "tags": "[\"95新\",\"国行\"]",
    "currentPrice": 3500.00,
    "commodityStatus": "在售",
    "sellerId": "user123",
    "mainImageUrl": "https://example.com/images/iphone13_main.jpg",
    "imageList": [
      "https://example.com/images/iphone13_1.jpg",
      "https://example.com/images/iphone13_2.jpg"
    ],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**商品不存在 (404)**

```json
{
  "code": 404,
  "message": "商品不存在",
  "data": null
}
```

**服务器错误 (500)**

```json
{
  "code": 500,
  "message": "查询商品详情失败: 数据库连接异常",
  "data": null
}
```

#### 响应字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| commodityId | String | 商品唯一标识符 |
| commodityName | String | 商品标题 |
| commodityDescription | String | 商品详细描述 |
| categoryId | Integer | 类别ID |
| categoryName | String | 类别名称 |
| tags | String | 商品标签（JSON字符串格式） |
| currentPrice | BigDecimal | 商品价格 |
| commodityStatus | String | 商品状态（在售/已售/下架） |
| sellerId | String | 卖家ID |
| mainImageUrl | String | 商品主图链接 |
| imageList | Array<String> | 多图链接数组 |
| createdAt | DateTime | 商品发布时间 |
| updatedAt | DateTime | 信息更新时间 |

### 2. 健康检查

#### 接口描述
检查商品信息服务是否正常运行。

#### 请求信息
- **URL**: `/api/commodity/health`
- **方法**: `GET`
- **Content-Type**: `application/json`

#### 请求示例

```http
GET /api/commodity/health
Content-Type: application/json
```

#### 响应格式

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "商品信息服务运行正常"
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 200 | 操作成功 | - |
| 404 | 商品不存在 | 检查商品ID是否正确 |
| 500 | 服务器内部错误 | 联系技术支持 |

## 数据库设计

### 商品表 (commodities)

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| commodity_id | VARCHAR(36) | PRIMARY KEY | 商品唯一标识符（UUIDv7） |
| commodity_name | VARCHAR(255) | NOT NULL | 商品标题 |
| commodity_description | TEXT | | 详细描述 |
| category_id | INT | FOREIGN KEY | 关联类别表的外键 |
| tags | JSON | | 存储标签数组 |
| current_price | DECIMAL(10,2) | NOT NULL | 商品售价 |
| commodity_status | VARCHAR(20) | NOT NULL | 商品状态 |
| seller_id | VARCHAR(36) | FOREIGN KEY | 关联用户表的外键 |
| main_image_url | VARCHAR(500) | | 商品主图链接 |
| image_list | JSON | | 多图链接数组 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 商品发布时间 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 信息更新时间 |

### 类别表 (categories)

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| category_id | INT | PRIMARY KEY AUTO_INCREMENT | 类别ID |
| category | VARCHAR(100) | NOT NULL | 类别名称 |

## 技术栈

- **框架**: Spring Boot 2.x
- **ORM**: MyBatis Plus
- **数据库**: MySQL
- **文档**: Swagger 3 (OpenAPI)
- **日志**: SLF4J + Logback

## 注意事项

1. 商品ID采用UUIDv7格式，确保全局唯一性
2. 图片链接支持多种格式，建议使用HTTPS协议
3. 标签字段存储为JSON字符串格式
4. 所有时间字段采用ISO 8601格式
5. 价格字段使用BigDecimal类型，精确到分
6. 服务包含详细的日志记录，便于问题排查

## 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2024-01-15 | 初始版本，支持商品详情查看功能 |

## 联系方式

如有问题或建议，请联系开发团队。