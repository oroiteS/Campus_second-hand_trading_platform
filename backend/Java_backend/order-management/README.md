# 订单管理模块 (Order Management)

## 项目概述

订单管理模块是校园二手交易平台的核心业务模块之一，负责处理用户之间的商品交易订单。该模块提供完整的订单生命周期管理，包括订单创建、状态跟踪、支付确认、交易完成等功能。

## 技术栈

- **框架**: Spring Boot 3.0.2
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis Plus 3.5.3.1
- **文档**: Swagger/OpenAPI 3
- **构建工具**: Maven
- **Java版本**: 17+

## 项目结构

```
order-management/
├── src/main/java/com/campus/ordermanagement/
│   ├── OrderManagementApplication.java     # 主启动类
│   ├── controller/                         # 控制器层
│   │   ├── OrderController.java           # 原有控制器
│   │   └── OrderApiController.java        # 新的REST API控制器
│   ├── service/                           # 服务层
│   │   ├── OrderService.java              # 服务接口
│   │   └── impl/
│   │       └── OrderServiceImpl.java      # 服务实现
│   ├── dao/                               # 数据访问层
│   │   └── OrderRepository.java           # 订单数据访问接口
│   ├── pojo/                              # 实体类
│   │   └── Order.java                     # 订单实体
│   ├── dto/                               # 数据传输对象
│   │   ├── CreateOrderRequest.java        # 创建订单请求
│   │   ├── OrderResponse.java             # 订单响应
│   │   └── UpdateOrderStatusRequest.java  # 更新状态请求
│   ├── config/                            # 配置类
│   │   └── SwaggerConfig.java             # Swagger配置
│   ├── exception/                         # 异常处理
│   │   ├── GlobalExceptionHandler.java    # 全局异常处理器
│   │   └── OrderBusinessException.java    # 业务异常类
│   └── util/                              # 工具类
│       └── UUIDGenerator.java             # UUID生成工具
├── src/main/resources/
│   └── application.yml                    # 应用配置
├── test-order-api.md                      # API测试文档
├── pom.xml                                # Maven配置
└── README.md                              # 项目说明
```

## 核心功能

### 1. 订单管理
- 创建订单
- 查询订单（支持多种查询条件）
- 更新订单状态
- 取消订单

### 2. 订单状态流转
```
PENDING_PAYMENT (待付款)
       ↓
PENDING_TRANSACTION (待交易)
       ↓
COMPLETED (已完成)
```

### 3. 查询功能
- 根据订单ID查询
- 根据买家ID查询
- 根据卖家ID查询
- 根据商品ID查询
- 根据订单状态查询
- 订单统计信息

### 4. 业务操作
- 确认付款
- 完成交易
- 取消订单
- 状态验证

## 数据模型

### Order 实体
```java
public class Order {
    private String orderId;        // 订单ID (UUIDv7)
    private String commodityId;    // 商品ID
    private String buyerId;        // 买家ID
    private String sellerId;       // 卖家ID
    private OrderStatus orderStatus; // 订单状态
    private LocalDateTime saleTime;  // 交易时间
    private BigDecimal money;        // 交易金额
    private String saleLocation;     // 交易地点
    private LocalDateTime createTime; // 创建时间
}
```

### 订单状态枚举
```java
public enum OrderStatus {
    PENDING_PAYMENT,      // 待付款
    PENDING_TRANSACTION,  // 待交易
    COMPLETED            // 已完成
}
```

## API接口

### 主要端点
- `POST /api/orders` - 创建订单
- `GET /api/orders/{orderId}` - 查询订单
- `GET /api/orders/buyer/{buyerId}` - 查询买家订单
- `GET /api/orders/seller/{sellerId}` - 查询卖家订单
- `PUT /api/orders/{orderId}/status` - 更新订单状态
- `PUT /api/orders/{orderId}/confirm-payment` - 确认付款
- `PUT /api/orders/{orderId}/complete` - 完成订单
- `PUT /api/orders/{orderId}/cancel` - 取消订单
- `GET /api/orders/statistics/user/{userId}` - 用户订单统计

详细的API文档请参考 [test-order-api.md](./test-order-api.md)

## 配置说明

### 应用配置 (application.yml)
```yaml
server:
  port: 8081
  servlet:
    context-path: /order-management

spring:
  application:
    name: order-management
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trading
    username: campus_test
    password: campus_suep

order:
  config:
    timeout-minutes: 30
    max-orders-per-user: 100
    enable-statistics: true
```

### 数据库配置
确保MySQL数据库中存在 `orders` 表，表结构参考 `backend/sql/create_order_table.sql`

## 启动指南

### 1. 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE campus_trading;

-- 创建订单表
USE campus_trading;
source backend/sql/create_order_table.sql;
```

### 3. 启动应用
```bash
# 进入项目目录
cd order-management

# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 4. 验证启动
- 应用地址: http://localhost:8081/order-management
- Swagger文档: http://localhost:8081/order-management/swagger-ui.html
- 健康检查: http://localhost:8081/order-management/actuator/health

## 开发指南

### 1. 添加新功能
1. 在 `OrderService` 接口中定义新方法
2. 在 `OrderServiceImpl` 中实现业务逻辑
3. 在 `OrderRepository` 中添加数据访问方法
4. 在 `OrderApiController` 中添加REST端点
5. 更新API文档

### 2. 异常处理
- 使用 `OrderBusinessException` 处理业务异常
- 全局异常处理器会自动处理并返回统一格式

### 3. 参数验证
- 使用 `@Valid` 和 `@Validated` 注解
- 在DTO类中使用Bean Validation注解

### 4. 日志记录
- 使用 `@Slf4j` 注解
- 重要操作记录INFO级别日志
- 异常记录ERROR级别日志

## 测试

### 1. 单元测试
```bash
mvn test
```

### 2. API测试
使用Postman或curl工具，参考 `test-order-api.md` 中的示例

### 3. 集成测试
确保数据库连接正常，所有API端点响应正确

## 监控和运维

### 1. 健康检查
- `/actuator/health` - 应用健康状态
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 性能指标

### 2. 日志文件
- 位置: `logs/order-management.log`
- 滚动策略: 10MB/文件，保留30天

### 3. 性能优化
- 数据库连接池配置
- 查询优化
- 缓存策略（可选）

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库连接配置
- 确认用户权限

### 2. 端口冲突
- 修改 `application.yml` 中的端口配置
- 检查其他服务是否占用8081端口

### 3. UUID生成问题
- 确保使用UUIDv7格式
- 检查时间戳是否正确

## 版本历史

- **v1.0.0** - 初始版本，包含基础订单管理功能

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

本项目采用MIT许可证，详情请参考LICENSE文件。