# 订单管理API测试文档

## 概述
本文档提供了订单管理模块的API测试指南和示例。

## 服务配置
- **服务端口**: 8081
- **上下文路径**: /order-management
- **基础URL**: http://localhost:8081/order-management

## API端点

### 1. 创建订单
**POST** `/api/orders`

```json
{
  "commodityId": "commodity-uuid-123",
  "buyerId": "buyer-uuid-456",
  "sellerId": "seller-uuid-789",
  "money": 99.99,
  "saleLocation": "上海市浦东新区"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "订单创建成功",
  "statusCode": 200,
  "data": {
    "orderId": "01234567-89ab-7def-0123-456789abcdef",
    "commodityId": "commodity-uuid-123",
    "buyerId": "buyer-uuid-456",
    "sellerId": "seller-uuid-789",
    "orderStatus": "PENDING_PAYMENT",
    "orderStatusDescription": "待付款",
    "saleTime": null,
    "money": 99.99,
    "saleLocation": "上海市浦东新区",
    "createTime": "2024-01-15T10:30:00"
  },
  "total": 1
}
```

### 2. 根据订单ID查询订单
**GET** `/api/orders/{orderId}`

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "statusCode": 200,
  "data": {
    "orderId": "01234567-89ab-7def-0123-456789abcdef",
    "commodityId": "commodity-uuid-123",
    "buyerId": "buyer-uuid-456",
    "sellerId": "seller-uuid-789",
    "orderStatus": "PENDING_PAYMENT",
    "orderStatusDescription": "待付款",
    "saleTime": null,
    "money": 99.99,
    "saleLocation": "上海市浦东新区",
    "createTime": "2024-01-15T10:30:00"
  },
  "total": 1
}
```

### 3. 根据买家ID查询订单列表
**GET** `/api/orders/buyer/{buyerId}`

**查询参数**:
- `page`: 页码（默认0）
- `size`: 每页大小（默认10）
- `status`: 订单状态过滤（可选）

### 4. 根据卖家ID查询订单列表
**GET** `/api/orders/seller/{sellerId}`

### 5. 根据商品ID查询订单列表
**GET** `/api/orders/commodity/{commodityId}`

### 6. 根据订单状态查询订单列表
**GET** `/api/orders/status/{status}`

**状态值**:
- `PENDING_PAYMENT`: 待付款
- `PENDING_TRANSACTION`: 待交易
- `COMPLETED`: 已完成

### 7. 更新订单状态
**PUT** `/api/orders/{orderId}/status`

```json
{
  "newStatus": "PENDING_TRANSACTION",
  "operatorUserId": "user-uuid-123",
  "remark": "买家已付款，等待交易"
}
```

### 8. 确认付款
**PUT** `/api/orders/{orderId}/confirm-payment`

```json
{
  "operatorUserId": "buyer-uuid-456"
}
```

### 9. 完成订单
**PUT** `/api/orders/{orderId}/complete`

```json
{
  "operatorUserId": "seller-uuid-789"
}
```

### 10. 取消订单
**PUT** `/api/orders/{orderId}/cancel`

```json
{
  "operatorUserId": "buyer-uuid-456",
  "remark": "买家取消订单"
}
```

### 11. 获取用户订单统计
**GET** `/api/orders/statistics/user/{userId}`

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "statusCode": 200,
  "data": {
    "totalOrders": 25,
    "pendingPaymentOrders": 3,
    "pendingTransactionOrders": 5,
    "completedOrders": 17,
    "totalAmount": 2599.99,
    "averageAmount": 103.99
  },
  "total": 1
}
```

## Swagger文档
启动服务后，可以通过以下URL访问API文档：
- Swagger UI: http://localhost:8081/order-management/swagger-ui.html
- API文档: http://localhost:8081/order-management/api-docs

## 健康检查
- 健康状态: http://localhost:8081/order-management/actuator/health
- 应用信息: http://localhost:8081/order-management/actuator/info
- 指标信息: http://localhost:8081/order-management/actuator/metrics

## 错误响应格式
```json
{
  "success": false,
  "message": "订单不存在",
  "statusCode": 404,
  "data": null,
  "total": 0
}
```

## 常见错误码
- 400: 请求参数错误
- 404: 订单不存在
- 409: 订单状态冲突
- 500: 服务器内部错误

## 测试建议
1. 首先创建订单
2. 测试各种查询接口
3. 测试订单状态流转：PENDING_PAYMENT → PENDING_TRANSACTION → COMPLETED
4. 测试异常情况和错误处理
5. 验证参数校验功能

## 数据库表结构
订单数据存储在`orders`表中，主要字段：
- `order_id`: 订单ID（UUIDv7，主键）
- `commodity_id`: 商品ID
- `buyer_id`: 买家ID
- `seller_id`: 卖家ID
- `order_status`: 订单状态
- `sale_time`: 交易时间
- `money`: 交易金额
- `sale_location`: 交易地点
- `create_time`: 创建时间