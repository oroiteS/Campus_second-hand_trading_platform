# 商品查询服务 API 文档

## 概述

商品查询服务专门提供商品分类查询功能，支持按分类查询、价格筛选、新旧度筛选、排序和分页功能。该服务自动过滤在售商品，为用户提供精准的商品查询体验。

**服务端口**: 8082  
**上下文路径**: /product-query  
**基础URL**: http://localhost:8082/product-query/api/commodity-query

## API 接口列表

### 1. 分类查询商品

**接口**: `POST /api/commodity-query/category`

**描述**: 根据商品分类查询商品，支持价格筛选、新旧度筛选、排序和分页功能

**请求体**:
```json
{
  "categoryId": 1,
  "priceRange": "50-200",
  "newness": "全新",
  "sortBy": "time_desc",
  "pageNum": 1,
  "pageSize": 20
}
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| categoryId | Integer | 是 | 商品分类ID（1-8） | 1 |
| priceRange | String | 否 | 价格区间筛选 | "50-200" |
| newness | String | 否 | 新旧度筛选 | "全新" |
| sortBy | String | 否 | 排序方式 | "time_desc" |
| pageNum | Integer | 否 | 页码（默认1） | 1 |
| pageSize | Integer | 否 | 每页大小（默认10，最大100） | 20 |

**价格区间选项**:
- `"0-50"`: 0-50元
- `"50-200"`: 50-200元
- `"200-500"`: 200-500元
- `"500-1000"`: 500-1000元
- `"1000+"`: 1000元以上

**新旧度选项**:
- `"全新"`: 全新商品
- `"95新"`: 95新商品
- `"9新"`: 9新商品

**排序方式选项**:
- `"price_asc"`: 价格从低到高
- `"price_desc"`: 价格从高到低
- `"time_desc"`: 发布时间从新到旧（默认）
- `"time_asc"`: 发布时间从旧到新

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "commodityId": "01234567-89ab-cdef-0123-456789abcdef",
        "commodityName": "苹果iPhone 13",
        "commodityPrice": 3500.00,
        "commodityDescription": "9成新，无划痕，原装充电器",
        "commodityNewness": "9新",
        "commodityStatus": "on_sale",
        "categoryId": 1,
        "sellerId": "202301001",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "total": 150,
    "size": 20,
    "current": 1,
    "pages": 8
  },
  "timestamp": 1705123456789
}
```

## 响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功消息",
  "data": {}, // 具体数据
  "timestamp": 1705123456789
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误消息",
  "data": null,
  "timestamp": 1705123456789
}
```

## 错误码说明

- `200`: 请求成功
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

## 数据模型

### CategoryQueryRequest（分类查询请求）

| 字段名 | 类型 | 必填 | 描述 | 验证规则 |
|--------|------|------|------|----------|
| categoryId | Integer | 是 | 商品分类ID | 1-8之间 |
| priceRange | String | 否 | 价格区间 | 枚举值："0-50", "50-200", "200-500", "500-1000", "1000+" |
| newness | String | 否 | 新旧度 | 枚举值："全新", "95新", "9新" |
| sortBy | String | 否 | 排序方式 | 枚举值："price_asc", "price_desc", "time_desc", "time_asc" |
| pageNum | Integer | 否 | 页码 | 最小值：1 |
| pageSize | Integer | 否 | 每页大小 | 1-100之间 |

### Commodity（商品）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| commodityId | String | 商品唯一标识符（UUID） |
| commodityName | String | 商品标题 |
| commodityPrice | BigDecimal | 商品售价 |
| commodityDescription | String | 详细描述 |
| commodityNewness | String | 商品新旧度 |
| commodityStatus | String | 商品状态（固定为on_sale） |
| categoryId | Integer | 商品分类ID |
| sellerId | String | 卖家ID |
| createdAt | String | 商品发布时间 |
| updatedAt | String | 信息更新时间 |

### PagedCommodityResponse（分页商品响应）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| records | List<Commodity> | 商品列表 |
| total | Long | 总记录数 |
| size | Integer | 当前页大小 |
| current | Integer | 当前页码 |
| pages | Integer | 总页数 |

## 使用示例

### 只按分类查询（获取所有数据）

```bash
curl -X POST "http://localhost:8082/product-query/api/commodity-query/category" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "pageSize": 100
  }'
```

### 完整筛选查询

```bash
curl -X POST "http://localhost:8082/product-query/api/commodity-query/category" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "priceRange": "50-200",
    "newness": "全新",
    "sortBy": "price_asc",
    "pageNum": 1,
    "pageSize": 20
  }'
```

### 价格筛选查询

```bash
curl -X POST "http://localhost:8082/product-query/api/commodity-query/category" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 2,
    "priceRange": "0-50",
    "sortBy": "time_desc"
  }'
```

### 新旧度筛选查询

```bash
curl -X POST "http://localhost:8082/product-query/api/commodity-query/category" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 3,
    "newness": "95新",
    "sortBy": "price_desc",
    "pageNum": 1,
    "pageSize": 50
  }'
```

## Swagger 文档

启动服务后，可以通过以下地址访问 Swagger UI：

**Swagger UI**: http://localhost:8082/product-query/swagger-ui.html

**API Docs**: http://localhost:8082/product-query/api-docs

## 分页说明

1. **默认分页**: 每页10条记录
2. **最大分页**: 每页最多100条记录
3. **获取全部数据**: 设置 `pageSize` 为100，根据返回的 `pages` 字段进行多次请求
4. **分页信息**:
   - `total`: 总记录数
   - `size`: 当前页大小
   - `current`: 当前页码
   - `pages`: 总页数
   - `records`: 当前页数据列表

## 注意事项

1. **接口使用 POST 方法**，请求体为 JSON 格式
2. 所有请求都需要设置 `Content-Type: application/json`
3. 分页查询的页码从 1 开始
4. 价格字段使用 BigDecimal 类型，支持精确的小数计算
5. 时间格式统一使用 ISO 8601 格式：`yyyy-MM-ddTHH:mm:ss`
6. 商品状态自动过滤为 `on_sale`（在售）
7. `categoryId` 为必填参数，其他筛选条件均为可选
8. 当不提供筛选条件时，返回该分类下的所有在售商品
9. 排序默认按发布时间倒序（最新发布的在前）
10. 建议使用合理的 `pageSize` 值以获得最佳性能