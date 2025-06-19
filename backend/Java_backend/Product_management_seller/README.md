# 商品管理卖家服务 (Product Management Seller)

## 项目简介

这是校园二手交易平台的商品管理卖家服务模块，基于Spring Boot 3.3.4和Java 17开发。该服务提供商品的上架、下架和修改描述功能。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.3.4
- **Spring Data JPA**: 数据持久化
- **MySQL**: 数据库
- **Maven**: 项目管理

## 功能特性

- ✅ 商品上架 (只需提供商品ID和卖家ID)
- ✅ 商品下架 (只需提供商品ID和卖家ID)
- ✅ 修改商品描述
- ✅ 查询卖家商品列表
- ✅ 根据状态查询商品
- ✅ 获取商品详情
- ✅ CORS跨域支持
- ✅ 全局异常处理
- ✅ 参数验证

## 环境要求

- JDK 17 (路径: `C:\Users\ASUS\.jdks\jbr-17.0.9`)
- MySQL 8.0+
- Maven 3.6+

## 数据库配置

### 数据库连接信息
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus
    username: campus_test
    password: campus_suep
```

### 数据库初始化
请先执行 `backend/sql/init_database.sql` 脚本来创建数据库和表结构。

## 快速开始

### 1. 克隆项目
```bash
cd e:\本科\大三\软件工程\Campus_second-hand_trading_platform\backend\Java_backend\Product_management_seller
```

### 2. 编译项目
```bash
mvn clean compile
```

### 3. 运行项目
```bash
mvn spring-boot:run
```

### 4. 验证服务
访问健康检查接口：
```
GET http://localhost:8084/api/commodity/health
```

## API接口文档

### 基础信息
- **服务端口**: 8084
- **基础路径**: `/api/commodity`
- **响应格式**: JSON

### 统一响应格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": {},
  "errorCode": null
}
```

### 接口列表

#### 1. 商品上架
```http
POST /api/commodity/put-on-sale
Content-Type: application/json

{
  "commodityId": "商品ID",
  "sellerId": "卖家ID"
}
```

#### 2. 商品下架
```http
POST /api/commodity/put-off-sale
Content-Type: application/json

{
  "commodityId": "商品ID",
  "sellerId": "卖家ID"
}
```

#### 3. 修改商品描述
```http
POST /api/commodity/update-description
Content-Type: application/json

{
  "commodityId": "商品ID",
  "sellerId": "卖家ID",
  "description": "新的商品描述"
}
```

#### 4. 获取卖家商品列表
```http
GET /api/commodity/list/{sellerId}
```

#### 5. 根据状态获取商品列表
```http
GET /api/commodity/list/{sellerId}/status/{status}
```
支持的状态: `on_sale`, `off_sale`, `sold`

#### 6. 获取商品详情
```http
GET /api/commodity/detail/{commodityId}/seller/{sellerId}
```

#### 7. 健康检查
```http
GET /api/commodity/health
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| VALIDATION_ERROR | 参数验证失败 |
| INVALID_STATUS | 无效的商品状态 |
| COMMODITY_NOT_FOUND | 商品不存在或无权限 |
| INVALID_SELLER_ID | 无效的卖家ID |
| INVALID_COMMODITY_ID | 无效的商品ID |
| INTERNAL_ERROR | 服务器内部错误 |
| DATABASE_ERROR | 数据库操作失败 |

## 项目结构

```
src/main/java/com/example/product_management_seller/
├── ProductManagementSellerApplication.java  # 主启动类
├── config/
│   └── CorsConfig.java                      # CORS跨域配置
├── controller/
│   └── CommodityController.java             # 控制器层
├── dto/
│   ├── ApiResponse.java                     # 统一响应格式
│   ├── CommodityStatusUpdateRequest.java    # 状态更新请求
│   └── CommodityDescriptionUpdateRequest.java # 描述更新请求
├── entity/
│   └── Commodity.java                       # 商品实体类
├── exception/
│   └── GlobalExceptionHandler.java          # 全局异常处理
├── repository/
│   └── CommodityRepository.java             # 数据访问层
└── service/
    └── CommodityService.java                # 业务逻辑层
```

## 开发说明

### 数据库表结构
主要使用 `commodities` 表，包含以下关键字段：
- `commodity_id`: 商品ID (主键)
- `commodity_status`: 商品状态 (`on_sale`, `off_sale`, `sold`)
- `commodity_description`: 商品描述
- `seller_id`: 卖家ID

### 业务逻辑
1. **上架**: 系统自动将 `commodity_status` 设置为 `on_sale`
2. **下架**: 系统自动将 `commodity_status` 设置为 `off_sale`
3. **已售**: 系统自动将 `commodity_status` 设置为 `sold`
4. **修改描述**: 更新 `commodity_description` 字段
5. **权限控制**: 只能操作属于自己的商品 (通过 `seller_id` 验证)

### 商品状态管理

#### 上架商品
```bash
curl -X POST http://localhost:8084/api/commodity/put-on-sale \
  -H "Content-Type: application/json" \
  -d '{
    "commodityId": "your-commodity-id",
    "sellerId": "your-seller-id"
  }'
```

#### 下架商品
```bash
curl -X POST http://localhost:8084/api/commodity/put-off-sale \
  -H "Content-Type: application/json" \
  -d '{
    "commodityId": "your-commodity-id",
    "sellerId": "your-seller-id"
  }'
```

#### 标记商品为已售
```bash
curl -X POST http://localhost:8084/api/commodity/mark-as-sold \
  -H "Content-Type: application/json" \
  -d '{
    "commodityId": "your-commodity-id",
    "sellerId": "your-seller-id"
  }'
```

### 日志配置
项目配置了详细的日志记录，包括：
- 请求日志
- 业务操作日志
- 异常日志
- SQL执行日志

## 注意事项

1. 确保数据库服务正在运行
2. 确保数据库连接配置正确
3. 所有API都支持CORS跨域访问
4. 参数验证严格，请按照接口文档传递参数
5. 只能操作属于自己的商品，系统会验证 `seller_id`

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **端口占用**
   - 检查8084端口是否被占用
   - 可在 `application.yml` 中修改端口

3. **JDK版本问题**
   - 确保使用JDK 17
   - 检查JAVA_HOME环境变量

## 联系方式

如有问题，请联系开发团队。