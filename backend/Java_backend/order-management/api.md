          
# Order Management API 文档

## 概述

订单管理模块提供完整的订单生命周期管理功能，包括订单创建、查询、状态更新、取消和统计等操作。所有API接口采用RESTful设计，使用JSON格式进行数据交换。

**基础URL**: `/api/orders`

**运行端口8095**: `/api/orders`

**支持的HTTP方法**: POST

**响应格式**: JSON

## 通用响应格式

### 成功响应
```json
{
  "success": true,
  "message": "操作成功",
  "code": 200,
  "data": {},
  "total": 0  // 仅在列表查询时包含
}
```

### 错误响应
```json
{
  "success": false,
  "message": "错误信息",
  "code": 400
}
```

## 订单状态说明

| 状态码 | 状态名称 | 描述 |
|--------|----------|------|
| `pending_payment` | 待付款 | 订单已创建，等待买家付款 |
| `pending_transaction` | 待交易 | 买家已付款，等待线下交易 |
| `completed` | 已完成 | 交易已完成 |

## API接口详情

### 1. 创建订单

**接口**: `POST /api/orders/create`

**描述**: 创建一个新的订单，订单初始状态为待付款

**请求体**:
```json
{
  "commodityId": "string",     // 商品ID，必填，最大36位
  "buyerId": "string",        // 买家ID，必填，最大9位
  "sellerId": "string",       // 卖家ID，必填，最大9位
  "money": "decimal",         // 交易金额，必填，最小0.01
  "saleLocation": "string",   // 交易地址，可选，最大250位
  "buyQuantity": "integer"    // 购买数量，必填，最小1
}
```

**响应**:
- **201**: 订单创建成功
- **400**: 请求参数错误或库存不足
- **500**: 服务器内部错误

**成功响应示例**:
```json
{
  "success": true,
  "message": "订单创建成功",
  "code": 201,
  "data": {
    "orderId": "ORD-20241216-001",
    "commodityId": "COMM-001",
    "buyerId": "USER001",
    "sellerId": "USER002",
    "orderStatus": "pending_payment",
    "orderStatusDescription": "待付款",
    "saleTime": "2024-12-16 10:30:00",
    "money": 99.99,
    "saleLocation": "校园咖啡厅",
    "buyQuantity": 1
  }
}
```

### 2. 查询订单详情

**接口**: `POST /api/orders/query/by-id`

**描述**: 根据订单ID查询订单详细信息

**请求体**:
```json
{
  "orderId": "string"  // 订单ID，必填
}
```

**响应**:
- **200**: 查询成功
- **404**: 订单不存在
- **400**: 请求参数错误

### 3. 查询买家订单

**接口**: `POST /api/orders/query/by-buyer`

**描述**: 根据买家ID查询订单列表

**请求体**:
```json
{
  "buyerId": "string"  // 买家ID，必填
}
```

**响应**:
- **200**: 查询成功
- **400**: 请求参数错误

### 4. 查询卖家订单

**接口**: `POST /api/orders/query/by-seller`

**描述**: 根据卖家ID查询订单列表

**请求体**:
```json
{
  "sellerId": "string"  // 卖家ID，必填
}
```

**响应**:
- **200**: 查询成功
- **400**: 请求参数错误

### 5. 查询商品订单

**接口**: `POST /api/orders/query/by-commodity`

**描述**: 根据商品ID查询订单列表

**请求体**:
```json
{
  "commodityId": "string"  // 商品ID，必填
}
```

**响应**:
- **200**: 查询成功
- **400**: 请求参数错误

### 6. 按状态查询订单

**接口**: `POST /api/orders/query/by-status`

**描述**: 根据订单状态查询订单列表

**请求体**:
```json
{
  "status": "string"  // 订单状态，必填，可选值：pending_payment, pending_transaction, completed
}
```

**响应**:
- **200**: 查询成功
- **400**: 请求参数错误

### 7. 查询用户参与的订单

**接口**: `POST /api/orders/query/by-user`

**描述**: 根据用户ID查询用户作为买家或卖家参与的所有订单

**请求体**:
```json
{
  "userId": "string"  // 用户ID，必填
}
```

**响应**:
- **200**: 查询成功
- **400**: 请求参数错误

### 8. 分页查询所有订单

**接口**: `POST /api/orders/query/all-paged`

**描述**: 分页查询所有订单，支持按创建时间倒序排列，适用于管理员查看所有订单或用户浏览订单列表

**请求体**:
```json
{
  "page": 1,        // 页码，必填，最小值为1
  "pageSize": 10    // 每页大小，必填，范围1-100
}
```

**响应**:
- **200**: 查询成功
- **400**: 请求参数错误

**成功响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "code": 200,
  "data": {
    "orders": [
      {
        "orderId": "ORD-20241216-001",
        "commodityId": "COMM-001",
        "buyerId": "USER001",
        "sellerId": "USER002",
        "orderStatus": "pending_payment",
        "orderStatusDescription": "待付款",
        "saleTime": "2024-12-16 10:30:00",
        "money": 99.99,
        "saleLocation": "校园咖啡厅",
        "buyQuantity": 1,
        "commodityInfo": {
          "commodityName": "二手教材",
          "commodityDescription": "高等数学教材"
        },
        "buyerInfo": {
          "buyerName": "张三",
          "buyerContact": "138****1234"
        },
        "sellerInfo": {
          "sellerName": "李四",
          "sellerContact": "139****5678"
        }
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 25,
      "totalPages": 3,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

**特性说明**:
- 支持Redis缓存，提高查询性能
- 订单按创建时间倒序排列（最新的在前）
- 包含商品信息、买家信息、卖家信息的详细数据
- 分页信息包含总数、总页数、是否有下一页/上一页等
- 参数验证：页码最小为1，页面大小范围1-100

### 9. 更新订单状态

**接口**: `POST /api/orders/update-status`

**描述**: 更新订单的状态

**请求体**:
```json
{
  "orderId": "string",        // 订单ID，必填，最大36位
  "orderStatus": "string",    // 新的订单状态，必填，可选值：pending_payment, pending_transaction, completed
  "operatorId": "string",     // 操作用户ID，可选
  "remark": "string"          // 备注信息，可选，最大500位
}
```

**响应**:
- **200**: 状态更新成功
- **400**: 请求参数错误

### 10. 取消订单

**接口**: `POST /api/orders/cancel`

**描述**: 取消指定的订单，只有待付款状态的订单才能取消

**请求体**:
```json
{
  "orderId": "string"  // 订单ID，必填
}
```

**响应**:
- **200**: 订单取消成功
- **400**: 请求参数错误或订单状态不允许取消
- **404**: 订单不存在
- **500**: 服务器内部错误

**成功响应示例**:
```json
{
  "success": true,
  "message": "订单取消成功",
  "code": 200,
  "data": {
    "orderId": "ORD-20241216-001",
    "status": "cancelled"
  }
}
```

### 11. 获取订单统计

**接口**: `POST /api/orders/statistics`

**描述**: 获取用户的订单统计信息

**请求体**:
```json
{
  "userId": "string",  // 用户ID，必填
  "role": "string"     // 角色，必填，可选值：buyer, seller
}
```

**响应**:
- **200**: 统计信息获取成功
- **400**: 请求参数错误

**成功响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "code": 200,
  "data": {
    "totalOrders": 10,
    "pendingPaymentOrders": 2,
    "pendingTransactionOrders": 3,
    "completedOrders": 5,
    "role": "buyer"
  }
}
```

## 数据模型

### OrderResponse (订单响应对象)

```json
{
  "orderId": "string",                    // 订单ID
  "commodityId": "string",               // 商品ID
  "buyerId": "string",                   // 买家ID
  "sellerId": "string",                  // 卖家ID
  "orderStatus": "string",               // 订单状态码
  "orderStatusDescription": "string",    // 订单状态描述
  "saleTime": "2024-12-16 10:30:00",     // 交易时间
  "money": 99.99,                        // 交易金额
  "saleLocation": "string",              // 交易地址
  "buyQuantity": 1,                      // 购买数量
  "commodityInfo": {                     // 商品信息（仅在分页查询中包含）
    "commodityName": "string",           // 商品名称
    "commodityDescription": "string"     // 商品描述
  },
  "buyerInfo": {                        // 买家信息（仅在分页查询中包含）
    "buyerName": "string",              // 买家姓名
    "buyerContact": "string"             // 买家联系方式
  },
  "sellerInfo": {                       // 卖家信息（仅在分页查询中包含）
    "sellerName": "string",             // 卖家姓名
    "sellerContact": "string"            // 卖家联系方式
  }
}
```

### PagedOrderResponse (分页订单响应对象)

```json
{
  "orders": [                           // 订单列表
    // OrderResponse 对象数组
  ],
  "pagination": {                       // 分页信息
    "page": 1,                          // 当前页码
    "pageSize": 10,                     // 每页大小
    "total": 25,                        // 总记录数
    "totalPages": 3,                    // 总页数
    "hasNext": true,                    // 是否有下一页
    "hasPrevious": false                // 是否有上一页
  }
}
```

### QueryAllOrdersRequest (分页查询请求对象)

```json
{
  "page": 1,        // 页码，必填，最小值为1
  "pageSize": 10    // 每页大小，必填，范围1-100
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. **库存检查**: 创建订单时会自动检查商品库存，如果购买数量超过库存，将返回错误
2. **库存更新**: 订单创建成功后会自动减少商品库存
3. **状态管理**: 商品库存为0时会自动更新状态为"已售出"
4. **编码安全**: 所有字符串响应都经过UTF-8编码处理，确保字符安全
5. **事务安全**: 订单创建和库存更新在同一事务中执行，保证数据一致性
6. **并发安全**: 使用条件更新确保并发环境下的数据安全

## 使用示例

### 创建订单流程

1. 调用创建订单接口
2. 系统检查商品库存
3. 库存充足则创建订单并减少库存
4. 返回订单详情

### 订单状态流转

```
待付款 (pending_payment) 
    ↓ (买家付款)
待交易 (pending_transaction)
    ↓ (线下交易完成)
已完成 (completed)
```

### 取消订单

只有处于"待付款"状态的订单才能被取消，取消后需要恢复商品库存。
        