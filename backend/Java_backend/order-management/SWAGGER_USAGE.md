# Swagger UI 使用指南

## 🚀 快速开始

### 1. 启动服务

#### 方式一：使用启动脚本（推荐）
```bash
# Windows
.\start-swagger-test.bat

# Linux/Mac
chmod +x start-swagger-test.sh
./start-swagger-test.sh
```

#### 方式二：手动启动
```bash
# 编译项目
mvn clean compile

# 启动服务（开发环境）
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. 访问 Swagger UI

服务启动成功后，访问以下地址：

- **Swagger UI**: http://localhost:8081/order-management/swagger-ui.html
- **API文档**: http://localhost:8081/order-management/api-docs
- **健康检查**: http://localhost:8081/order-management/actuator/health

## 📋 接口测试指南

### 测试数据准备

系统已预置测试数据，包括：

#### 测试用户ID（UUIDv7格式）
```
买家ID:
- aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee
- bbbbbbbb-cccc-7ddd-eeee-ffffffffffff
- cccccccc-dddd-7eee-ffff-000000000000
- dddddddd-eeee-7fff-0000-111111111111

卖家ID:
- ffffffff-0000-7111-2222-333333333333
- 00000000-1111-7222-3333-444444444444
- 11111111-2222-7333-4444-555555555555
- 22222222-3333-7444-5555-666666666666
```

#### 测试商品ID
```
- 11111111-2222-7333-4444-555555555555
- 22222222-3333-7444-5555-666666666666
- 33333333-4444-7555-6666-777777777777
- 44444444-5555-7666-7777-888888888888
- 55555555-6666-7777-8888-999999999999
```

### 完整测试流程

#### 1. 创建订单

**接口**: `POST /api/orders`

**请求体示例**:
```json
{
  "commodityId": "11111111-2222-7333-4444-555555555555",
  "buyerId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
  "sellerId": "ffffffff-0000-7111-2222-333333333333",
  "money": 299.99,
  "saleLocation": "上海理工大学南校区"
}
```

**操作步骤**:
1. 点击 `POST /api/orders` 接口
2. 点击 "Try it out" 按钮
3. 在请求体中输入上述JSON数据
4. 点击 "Execute" 执行
5. 查看响应，记录返回的 `orderId`

#### 2. 查询订单

**接口**: `GET /api/orders/{orderId}`

**操作步骤**:
1. 点击 `GET /api/orders/{orderId}` 接口
2. 点击 "Try it out" 按钮
3. 在 `orderId` 参数中输入步骤1中获得的订单ID
4. 点击 "Execute" 执行
5. 验证订单信息是否正确

#### 3. 确认付款

**接口**: `PUT /api/orders/{orderId}/confirm-payment`

**请求体示例**:
```json
{
  "operatorUserId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee"
}
```

**操作步骤**:
1. 点击对应接口
2. 输入订单ID和操作用户ID
3. 执行后订单状态应变为 `PENDING_TRANSACTION`

#### 4. 完成订单

**接口**: `PUT /api/orders/{orderId}/complete`

**请求体示例**:
```json
{
  "operatorUserId": "ffffffff-0000-7111-2222-333333333333"
}
```

**操作步骤**:
1. 点击对应接口
2. 输入订单ID和卖家ID
3. 执行后订单状态应变为 `COMPLETED`

### 高级测试场景

#### 批量查询测试

1. **按买家查询**: `GET /api/orders/buyer/{buyerId}`
2. **按卖家查询**: `GET /api/orders/seller/{sellerId}`
3. **按状态查询**: `GET /api/orders/status/{status}`
4. **按商品查询**: `GET /api/orders/commodity/{commodityId}`

#### 统计接口测试

**接口**: `GET /api/orders/user/{userId}/statistics`

测试不同用户的订单统计信息。

#### 错误场景测试

1. **无效UUID格式**:
   ```
   使用: "invalid-uuid-format"
   预期: 400 Bad Request
   ```

2. **订单不存在**:
   ```
   使用: "00000000-0000-7000-0000-000000000000"
   预期: 404 Not Found
   ```

3. **状态流转错误**:
   ```
   对已完成订单执行付款确认
   预期: 400 Bad Request
   ```

4. **权限验证**:
   ```
   使用错误的操作用户ID
   预期: 403 Forbidden
   ```

## 🔧 Swagger UI 功能说明

### 界面元素

1. **接口分组**: 按功能模块分组显示
2. **请求方法**: GET、POST、PUT、DELETE 用不同颜色标识
3. **参数说明**: 详细的参数类型、是否必填、示例值
4. **响应示例**: 各种状态码的响应格式
5. **模型定义**: 数据传输对象的结构说明

### 测试功能

1. **Try it out**: 启用接口测试
2. **参数输入**: 支持路径参数、查询参数、请求体
3. **执行测试**: 发送真实HTTP请求
4. **响应查看**: 查看状态码、响应头、响应体
5. **代码生成**: 生成各种语言的调用代码

### 认证配置

如果接口需要认证，可以在页面顶部的 "Authorize" 按钮中配置：
- API Key
- Bearer Token
- Basic Auth

## 📊 性能测试建议

### 并发测试

1. 使用多个浏览器标签页同时测试
2. 测试创建订单的并发性能
3. 验证数据一致性

### 大数据量测试

1. 创建大量测试订单
2. 测试分页查询性能
3. 验证查询响应时间

## 🐛 常见问题排查

### 服务启动失败

1. **端口占用**:
   ```bash
   netstat -ano | findstr :8081
   taskkill /PID <PID> /F
   ```

2. **数据库连接失败**:
   - 检查数据库服务是否启动
   - 验证连接配置是否正确

3. **依赖缺失**:
   ```bash
   mvn dependency:resolve
   ```

### Swagger UI 无法访问

1. **检查服务状态**:
   ```
   访问: http://localhost:8081/order-management/actuator/health
   ```

2. **检查配置**:
   - 确认 `spring.profiles.active=dev`
   - 验证 Swagger 配置是否正确

3. **清除缓存**:
   - 清除浏览器缓存
   - 尝试无痕模式

### 接口测试失败

1. **参数格式错误**:
   - 检查UUID格式是否正确
   - 验证JSON格式是否有效

2. **业务逻辑错误**:
   - 检查订单状态流转是否合法
   - 验证用户权限是否正确

3. **数据库问题**:
   - 检查数据库表是否存在
   - 验证数据完整性

## 📝 测试报告模板

### 基础功能测试

- [ ] 创建订单
- [ ] 查询订单（按ID）
- [ ] 查询订单（按买家）
- [ ] 查询订单（按卖家）
- [ ] 查询订单（按状态）
- [ ] 查询订单（按商品）
- [ ] 更新订单状态
- [ ] 确认付款
- [ ] 完成订单
- [ ] 取消订单
- [ ] 获取用户统计

### 异常场景测试

- [ ] 无效参数格式
- [ ] 资源不存在
- [ ] 状态流转错误
- [ ] 权限验证失败

### 性能测试

- [ ] 并发创建订单
- [ ] 大量数据查询
- [ ] 响应时间测试

## 🎯 下一步

完成Swagger UI测试后，建议：

1. **集成测试**: 与前端应用集成测试
2. **自动化测试**: 编写单元测试和集成测试
3. **性能优化**: 根据测试结果优化性能
4. **文档完善**: 补充API文档和使用说明
5. **部署测试**: 在测试环境部署验证

---

**技术支持**: 如有问题，请查看项目README.md或联系开发团队。