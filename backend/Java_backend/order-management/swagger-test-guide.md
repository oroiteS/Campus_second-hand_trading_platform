# Swagger UI 接口测试指南

## 访问地址

启动订单管理服务后，可以通过以下地址访问Swagger UI：

- **Swagger UI**: http://localhost:8081/order-management/swagger-ui.html
- **API文档**: http://localhost:8081/order-management/api-docs

## 测试前准备

### 1. 启动服务
```bash
cd order-management
mvn spring-boot:run
```

### 2. 验证服务状态
访问健康检查端点：http://localhost:8081/order-management/actuator/health

应该返回：
```json
{
  "status": "UP"
}
```

## 测试数据示例

### UUID格式说明
本系统使用UUIDv7格式，具有时间排序特性。测试时可以使用以下示例UUID：

```
订单ID示例: 01234567-89ab-7def-0123-456789abcdef
商品ID示例: 11111111-2222-7333-4444-555555555555
买家ID示例: aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee
卖家ID示例: ffffffff-0000-7111-2222-333333333333
```

### 创建订单测试数据

#### 示例1：电子产品订单
```json
{
  "commodityId": "11111111-2222-7333-4444-555555555555",
  "buyerId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
  "sellerId": "ffffffff-0000-7111-2222-333333333333",
  "money": 1299.99,
  "saleLocation": "上海理工大学南校区"
}
```

#### 示例2：书籍订单
```json
{
  "commodityId": "22222222-3333-7444-5555-666666666666",
  "buyerId": "bbbbbbbb-cccc-7ddd-eeee-ffffffffffff",
  "sellerId": "00000000-1111-7222-3333-444444444444",
  "money": 45.50,
  "saleLocation": "上海理工大学图书馆"
}
```

#### 示例3：生活用品订单
```json
{
  "commodityId": "33333333-4444-7555-6666-777777777777",
  "buyerId": "cccccccc-dddd-7eee-ffff-000000000000",
  "sellerId": "11111111-2222-7333-4444-555555555555",
  "money": 89.00,
  "saleLocation": "上海理工大学宿舍楼下"
}
```

### 更新订单状态测试数据

#### 确认付款
```json
{
  "newStatus": "PENDING_TRANSACTION",
  "operatorUserId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
  "remark": "买家已完成付款，等待交易"
}
```

#### 完成交易
```json
{
  "newStatus": "COMPLETED",
  "operatorUserId": "ffffffff-0000-7111-2222-333333333333",
  "remark": "交易已完成，商品已交付"
}
```

## 完整测试流程

### 1. 创建订单测试

1. 打开Swagger UI
2. 找到 `POST /api/orders` 接口
3. 点击 "Try it out"
4. 在请求体中输入上述示例数据
5. 点击 "Execute"
6. 查看响应，记录返回的 `orderId`

**预期响应**：
```json
{
  "success": true,
  "message": "订单创建成功",
  "statusCode": 201,
  "data": {
    "orderId": "01234567-89ab-7def-0123-456789abcdef",
    "commodityId": "11111111-2222-7333-4444-555555555555",
    "buyerId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
    "sellerId": "ffffffff-0000-7111-2222-333333333333",
    "orderStatus": "PENDING_PAYMENT",
    "orderStatusDescription": "待付款",
    "saleTime": null,
    "money": 1299.99,
    "saleLocation": "上海理工大学南校区",
    "createTime": "2024-01-15T10:30:00"
  },
  "total": 1
}
```

### 2. 查询订单测试

#### 根据订单ID查询
1. 使用 `GET /api/orders/{orderId}` 接口
2. 在路径参数中输入上一步获得的 `orderId`
3. 执行查询

#### 根据买家ID查询
1. 使用 `GET /api/orders/buyer/{buyerId}` 接口
2. 输入买家ID：`aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee`
3. 可选参数：
   - `page`: 0
   - `size`: 10
   - `status`: PENDING_PAYMENT

### 3. 订单状态流转测试

#### 步骤1：确认付款
1. 使用 `PUT /api/orders/{orderId}/confirm-payment` 接口
2. 请求体：
```json
{
  "operatorUserId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee"
}
```

#### 步骤2：完成订单
1. 使用 `PUT /api/orders/{orderId}/complete` 接口
2. 请求体：
```json
{
  "operatorUserId": "ffffffff-0000-7111-2222-333333333333"
}
```

### 4. 订单统计测试

1. 使用 `GET /api/orders/statistics/user/{userId}` 接口
2. 输入用户ID（买家或卖家ID）
3. 查看统计结果

**预期响应**：
```json
{
  "success": true,
  "message": "查询成功",
  "statusCode": 200,
  "data": {
    "totalOrders": 3,
    "pendingPaymentOrders": 1,
    "pendingTransactionOrders": 1,
    "completedOrders": 1,
    "totalAmount": 1434.49,
    "averageAmount": 478.16
  },
  "total": 1
}
```

## 错误测试场景

### 1. 参数验证错误

#### 测试无效金额
```json
{
  "commodityId": "11111111-2222-7333-4444-555555555555",
  "buyerId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
  "sellerId": "ffffffff-0000-7111-2222-333333333333",
  "money": -10.00,
  "saleLocation": "上海理工大学"
}
```

**预期响应**：400 Bad Request

#### 测试空字段
```json
{
  "commodityId": "",
  "buyerId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
  "sellerId": "ffffffff-0000-7111-2222-333333333333",
  "money": 100.00,
  "saleLocation": "上海理工大学"
}
```

**预期响应**：400 Bad Request

### 2. 资源不存在错误

#### 查询不存在的订单
使用不存在的订单ID：`99999999-9999-7999-9999-999999999999`

**预期响应**：404 Not Found

### 3. 状态流转错误

#### 尝试对已完成订单再次确认付款
1. 先创建订单并完成整个流程
2. 再次调用确认付款接口

**预期响应**：409 Conflict

## 高级测试功能

### 1. 批量数据测试

可以使用以下脚本创建多个测试订单：

```bash
# 创建多个订单用于测试分页和统计功能
for i in {1..20}; do
  curl -X POST "http://localhost:8081/order-management/api/orders" \
    -H "Content-Type: application/json" \
    -d "{
      \"commodityId\": \"$(uuidgen)\",
      \"buyerId\": \"aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee\",
      \"sellerId\": \"ffffffff-0000-7111-2222-333333333333\",
      \"money\": $((RANDOM % 1000 + 10)).99,
      \"saleLocation\": \"测试地点 $i\"
    }"
done
```

### 2. 分页测试

测试不同的分页参数：
- `page=0&size=5` - 第一页，每页5条
- `page=1&size=10` - 第二页，每页10条
- `page=0&size=100` - 大页面测试

### 3. 过滤测试

测试不同的状态过滤：
- `status=PENDING_PAYMENT`
- `status=PENDING_TRANSACTION`
- `status=COMPLETED`

## 性能测试建议

### 1. 并发测试
使用多个浏览器标签页同时创建订单，测试并发处理能力。

### 2. 大数据量测试
创建大量订单后测试查询性能：
- 创建1000+订单
- 测试分页查询响应时间
- 测试统计接口性能

## 常见问题排查

### 1. 服务无法访问
- 检查服务是否启动：`curl http://localhost:8081/order-management/actuator/health`
- 检查端口是否被占用：`netstat -an | grep 8081`

### 2. 数据库连接问题
- 检查MySQL服务状态
- 验证数据库连接配置
- 查看应用日志：`tail -f logs/order-management.log`

### 3. UUID格式错误
确保使用标准UUID格式：`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`

### 4. 时间格式问题
系统使用ISO 8601格式：`2024-01-15T10:30:00`

## 测试报告模板

```markdown
# 订单管理API测试报告

## 测试环境
- 服务地址：http://localhost:8081/order-management
- 测试时间：2024-01-15
- 测试人员：[姓名]

## 测试结果

### 功能测试
- [ ] 创建订单
- [ ] 查询订单
- [ ] 更新订单状态
- [ ] 订单统计

### 异常测试
- [ ] 参数验证
- [ ] 资源不存在
- [ ] 状态流转错误

### 性能测试
- [ ] 并发处理
- [ ] 大数据量查询

## 问题记录
[记录发现的问题]

## 改进建议
[提出改进建议]
```

## 总结

通过Swagger UI可以方便地测试所有订单管理API接口。建议按照上述流程进行系统性测试，确保所有功能正常工作。测试过程中如发现问题，请及时记录并反馈给开发团队。