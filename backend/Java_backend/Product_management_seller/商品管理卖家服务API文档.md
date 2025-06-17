# 商品管理卖家服务 API 文档

## 项目简介

这是校园二手交易平台的商品管理卖家服务模块，基于Spring Boot 3.3.4和Java 17开发。该服务提供商品的上架、下架、修改描述、查询等功能，专门为卖家用户设计。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.3.4
- **Spring Data JPA**: 数据持久化
- **MySQL**: 数据库
- **Maven**: 项目管理
- **SpringDoc OpenAPI**: API文档生成
- **Bean Validation**: 参数验证

## 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

## 数据库配置

### 数据库连接信息

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: campus_test
    password: campus_suep
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 数据库初始化

请先执行 `backend/sql/init_database.sql` 脚本来创建数据库和表结构。

## 快速开始

1. **克隆项目**

```bash
cd e:\本科\大三\软件工程\Campus_second-hand_trading_platform\backend\Java_backend\Product_management_seller
```

2. **编译项目**

```bash
mvn clean compile
```

3. **运行项目**

```bash
mvn spring-boot:run
```

4. **验证服务**

访问健康检查接口：

```
GET http://localhost:8084/api/commodity/health
```

5. **查看API文档**

访问Swagger UI：

```
http://localhost:8084/swagger-ui.html
```

## API接口文档

### 基础信息

- **服务端口**: 8084
- **基础路径**: `/api/commodity`
- **响应格式**: JSON
- **时区**: Asia/Shanghai

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

- **URL**: `/api/commodity/put-on-sale`
- **方法**: `POST`
- **描述**: 将商品状态设置为 `on_sale`（在售）。
- **Content-Type**: `application/json`

**请求参数**:

```json
{
  "commodityId": "string",
  "sellerId": "string",
  "status": "on_sale"
}
```

| 参数名      | 类型   | 必填 | 描述     | 验证规则 |
|-------------|--------|------|----------|----------|
| commodityId | string | 是   | 商品ID   | 不能为空 |
| sellerId    | string | 是   | 卖家ID   | 不能为空 |
| status      | string | 是   | 商品状态 | 必须为 "on_sale" |

**响应示例**:

```json
{
  "success": true,
  "message": "商品上架成功"
}
```

#### 2. 商品下架

- **URL**: `/api/commodity/put-off-sale`
- **方法**: `POST`
- **描述**: 将商品状态设置为 `off_sale`（下架）。
- **Content-Type**: `application/json`

**请求参数**:

```json
{
  "commodityId": "string",
  "sellerId": "string",
  "status": "off_sale"
}
```

| 参数名      | 类型   | 必填 | 描述     | 验证规则 |
|-------------|--------|------|----------|----------|
| commodityId | string | 是   | 商品ID   | 不能为空 |
| sellerId    | string | 是   | 卖家ID   | 不能为空 |
| status      | string | 是   | 商品状态 | 必须为 "off_sale" |

**响应示例**:

```json
{
  "success": true,
  "message": "商品下架成功"
}
```

#### 3. 修改商品描述

- **URL**: `/api/commodity/update-description`
- **方法**: `POST`
- **描述**: 更新商品的描述信息。
- **Content-Type**: `application/json`

**请求参数**:

```json
{
  "commodityId": "string",
  "sellerId": "string",
  "description": "string"
}
```

| 参数名      | 类型   | 必填 | 描述     | 验证规则 |
|-------------|--------|------|----------|----------|
| commodityId | string | 是   | 商品ID   | 不能为空 |
| sellerId    | string | 是   | 卖家ID   | 不能为空 |
| description | string | 否   | 新的商品描述 | 允许为空（清空描述） |

**响应示例**:

```json
{
  "success": true,
  "message": "商品描述修改成功"
}
```

#### 4. 获取卖家商品列表

- **URL**: `/api/commodity/list/{sellerId}`
- **方法**: `GET`
- **描述**: 获取指定卖家的所有商品列表。

**路径参数**:

| 参数名   | 类型   | 必填 | 描述     |
|----------|--------|------|----------|
| sellerId | string | 是   | 卖家ID   |

**响应示例**:

```json
{
  "success": true,
  "message": "获取商品列表成功",
  "data": [
    {
      "commodityId": "12345678-1234-1234-1234-123456789012",
      "commodityName": "二手iPhone 13",
      "commodityDescription": "9成新，无划痕，配件齐全",
      "categoryId": 1,
      "currentPrice": 4500.00,
      "commodityStatus": "ON_SALE",
      "sellerId": "202100001",
      "mainImageUrl": "http://example.com/image.jpg",
      "createdAt": "2023-10-27T10:00:00",
      "updatedAt": "2023-10-27T10:00:00"
    }
  ]
}
```

#### 5. 根据状态获取商品列表

- **URL**: `/api/commodity/list/{sellerId}/status/{status}`
- **方法**: `GET`
- **描述**: 获取指定卖家特定状态的商品列表。

**路径参数**:

| 参数名   | 类型   | 必填 | 描述     | 可选值 |
|----------|--------|------|----------|--------|
| sellerId | string | 是   | 卖家ID   | - |
| status   | string | 是   | 商品状态 | "on_sale", "off_sale", "sold" |

**响应示例**: (同上)

#### 6. 获取商品详情

- **URL**: `/api/commodity/detail/{commodityId}/seller/{sellerId}`
- **方法**: `GET`
- **描述**: 获取指定商品的详细信息。

**路径参数**:

| 参数名      | 类型   | 必填 | 描述     |
|-------------|--------|------|----------|
| commodityId | string | 是   | 商品ID   |
| sellerId    | string | 是   | 卖家ID   |

**响应示例**:

```json
{
  "success": true,
  "message": "获取商品详情成功",
  "data": {
    "commodityId": "12345678-1234-1234-1234-123456789012",
    "commodityName": "二手iPhone 13",
    "commodityDescription": "9成新，无划痕，配件齐全",
    "categoryId": 1,
    "tagsId": "[1,2,3]",
    "currentPrice": 4500.00,
    "commodityStatus": "ON_SALE",
    "sellerId": "202100001",
    "mainImageUrl": "http://example.com/image.jpg",
    "imageList": "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]",
    "createdAt": "2023-10-27T10:00:00",
    "updatedAt": "2023-10-27T10:00:00"
  }
}
```

#### 7. 健康检查

- **URL**: `/api/commodity/health`
- **方法**: `GET`
- **描述**: 检查服务是否正常运行。

**响应示例**:

```json
{
  "success": true,
  "message": "商品管理服务运行正常",
  "data": "OK"
}
```

## 数据模型

### Commodity 实体

```java
public class Commodity {
    private String commodityId;          // 商品ID (主键)
    private String commodityName;        // 商品名称
    private String commodityDescription; // 商品描述
    private Integer categoryId;          // 商品类别ID
    private String tagsId;               // 标签ID (JSON格式)
    private BigDecimal currentPrice;     // 当前价格
    private CommodityStatus commodityStatus; // 商品状态
    private String sellerId;             // 卖家ID
    private String mainImageUrl;         // 主图URL
    private String imageList;            // 图片列表 (JSON格式)
    private LocalDateTime createdAt;     // 创建时间
    private LocalDateTime updatedAt;     // 更新时间
}
```

### 商品状态枚举

```java
public enum CommodityStatus {
    ON_SALE("on_sale"),   // 在售
    SOLD("sold"),         // 已售出
    OFF_SALE("off_sale") // 下架
}
```

## 错误码说明

| 错误码              | 说明                     | HTTP状态码 |
|---------------------|--------------------------|------------|
| VALIDATION_ERROR    | 参数验证失败             | 400        |
| INVALID_STATUS      | 无效的商品状态           | 400        |
| COMMODITY_NOT_FOUND | 商品不存在或无权限       | 404        |
| INVALID_SELLER_ID   | 无效的卖家ID             | 400        |
| INVALID_COMMODITY_ID| 无效的商品ID             | 400        |
| INTERNAL_ERROR      | 服务器内部错误           | 500        |
| DATABASE_ERROR      | 数据库操作失败           | 500        |

## 安全说明

1. **权限验证**: 所有操作都会验证 `sellerId`，确保只能操作属于自己的商品。
2. **参数验证**: 使用Bean Validation进行严格的参数验证。
3. **SQL注入防护**: 使用JPA预编译语句防止SQL注入。
4. **跨域支持**: 配置了CORS支持前端跨域访问。

## 注意事项

1. **数据库连接**: 确保MySQL服务正在运行，数据库连接配置正确。
2. **字符编码**: 数据库和应用都使用UTF-8编码。
3. **时区设置**: 统一使用Asia/Shanghai时区。
4. **日志级别**: 开发环境开启了详细的SQL日志。
5. **商品状态**: 状态变更有严格的业务逻辑验证。
6. **图片存储**: 图片URL和列表以JSON格式存储。

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
│   ├── CommodityStatusUpdateRequest.java    # 状态更新请求DTO
│   └── CommodityDescriptionUpdateRequest.java # 描述更新请求DTO
├── entity/
│   └── Commodity.java                       # 商品实体类
├── exception/
│   └── GlobalExceptionHandler.java          # 全局异常处理
├── repository/
│   └── CommodityRepository.java             # 数据访问层
├── service/
│   └── CommodityService.java                # 业务逻辑层
└── converter/
    └── CommodityStatusConverter.java        # 状态转换器
```

## 开发指南

### 添加新接口

1. 在 `CommodityController` 中添加新的端点
2. 在 `CommodityService` 中实现业务逻辑
3. 如需要，创建对应的DTO类
4. 更新API文档

### 数据库迁移

1. 修改实体类
2. 更新数据库脚本
3. 测试数据迁移

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 版本信息

- **当前版本**: 0.0.1-SNAPSHOT
- **最后更新**: 2023年10月
- **维护状态**: 活跃开发中

## 联系方式

如有问题，请联系开发团队。

---

*本文档随代码更新而更新，请以最新版本为准。*