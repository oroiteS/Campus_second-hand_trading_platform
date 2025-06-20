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
- **阿里云OSS**: 图片存储服务
- **OSS Java SDK**: OSS客户端

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
GET http://localhost:8084/api-8084/commodity/health
```

5. **查看API文档**

访问Swagger UI：

```
http://localhost:8084/swagger-ui.html
```

## API接口文档

### 基础信息

- **服务端口**: 8084
- **基础路径**: `/api-8084/commodity`
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

#### 1. 创建商品（待审核）

- **URL**: `/api-8084/commodity/create-and-put-on-sale`
- **方法**: `POST`
- **描述**: 创建新商品并设置为待审核状态(to_sale)，需要管理员审核后才能正式上架。支持同时上传商品图片到OSS。
- **Content-Type**: `multipart/form-data`

**请求参数**:

| 参数名               | 类型    | 必填 | 描述         | 验证规则                    |
|---------------------|---------|------|--------------|-----------------------------|  
| commodityName       | string  | 是   | 商品名称     | 不能为空                    |
| commodityDescription| string  | 是   | 商品描述     | 不能为空                    |
| categoryId          | integer | 是   | 商品类别ID   | 不能为空                    |
| tagsId              | List<Integer> | 是   | 标签ID列表   | 整数数组格式，如[1,2,3]     |
| currentPrice        | decimal | 是   | 商品价格     | 必须大于0                   |
| quantity            | integer | 是   | 商品数量     | 必须大于0                   |
| sellerId            | string  | 是   | 卖家ID       | 不能为空                    |
| newness             | string  | 是   | 商品新旧度   | 不能为空                    |
| images              | file[]  | 是   | 商品图片文件 | 支持多张图片，格式：jpg/jpeg/png/gif/webp，单个文件不超过10MB |

**图片上传说明**:
- 支持的图片格式：JPG、JPEG、PNG、GIF、WEBP
- 单个图片文件大小限制：10MB
- 支持多张图片同时上传
- 第一张图片将自动设为主图
- 图片将上传到OSS并生成带https前缀的完整访问URL

**响应示例**:

```json
{
  "success": true,
  "message": "商品创建成功，等待管理员审核",
  "data": {
    "commodityId": "12345678-1234-1234-1234-123456789012",
    "commodityName": "二手iPhone 13",
    "commodityDescription": "9成新，无划痕，配件齐全",
    "categoryId": 1,
    "tagsId": [1,2,3],
    "currentPrice": 4500.00,
    "quantity": 1,
    "newness": "九成新",
    "commodityStatus": "TO_SALE",
    "sellerId": "202100001",
    "mainImageUrl": "https://your-oss-domain.com/commodities/202100001/20241217_220000_abc123.jpg",
    "imageList": "[\"https://your-oss-domain.com/commodities/202100001/20241217_220000_abc123.jpg\", \"https://your-oss-domain.com/commodities/202100001/20241217_220001_def456.jpg\"]",
    "createdAt": "2024-12-17T22:00:00",
    "updatedAt": "2024-12-17T22:00:00"
  }
}
```

#### 2. 申请上架商品（设置状态为待审核）

- **URL**: `/api-8084/commodity/put-on-sale`
- **方法**: `POST`
- **描述**: 申请上架商品，将商品状态设置为 `to_sale`（待审核），等待管理员审核。
- **Content-Type**: `application/json`

**请求参数**:

```json
{
  "commodityId": "string",
  "sellerId": "string"
}
```

| 参数名      | 类型   | 必填 | 描述     | 验证规则 |
|-------------|--------|------|----------|----------|
| commodityId | string | 是   | 商品ID   | 不能为空 |
| sellerId    | string | 是   | 卖家ID   | 不能为空 |

**响应示例**:

```json
{
  "success": true,
  "message": "商品申请上架成功，状态已设为待审核"
}
```

#### 3. 商品下架

- **URL**: `/api-8084/commodity/put-off-sale`
- **方法**: `POST`
- **描述**: 将商品状态设置为 `off_sale`（下架）。
- **Content-Type**: `application/json`

**请求参数**:

```json
{
  "commodityId": "string",
  "sellerId": "string"
}
```

| 参数名      | 类型   | 必填 | 描述     | 验证规则 |
|-------------|--------|------|----------|----------|
| commodityId | string | 是   | 商品ID   | 不能为空 |
| sellerId    | string | 是   | 卖家ID   | 不能为空 |

**响应示例**:

```json
{
  "success": true,
  "message": "商品下架成功"
}
```

#### 3.1. 标记商品为已售

- **URL**: `/api-8084/commodity/mark-as-sold`
- **方法**: `POST`
- **描述**: 将商品状态设置为 `sold`（已售）。
- **Content-Type**: `application/json`

**请求参数**:

```json
{
  "commodityId": "string",
  "sellerId": "string"
}
```

| 参数名      | 类型   | 必填 | 描述     | 验证规则 |
|-------------|--------|------|----------|----------|
| commodityId | string | 是   | 商品ID   | 不能为空 |
| sellerId    | string | 是   | 卖家ID   | 不能为空 |

**响应示例**:

```json
{
  "success": true,
  "message": "商品已标记为已售"
}
```

#### 4. 更新商品信息

- **URL**: `/api-8084/commodity/update-info`
- **方法**: `POST`
- **描述**: 更新商品的详细信息，包括名称、描述、价格、新旧度、数量和图片。支持部分字段更新和图片文件直接上传。
- **Content-Type**: `multipart/form-data`

**请求参数**:

| 参数名               | 类型    | 必填 | 描述         | 验证规则                    |
|---------------------|---------|------|--------------|-----------------------------|
| commodityId         | string  | 是   | 商品ID       | 不能为空                    |
| sellerId            | string  | 是   | 卖家ID       | 不能为空                    |
| commodityName       | string  | 否   | 商品名称     | 可选，提供时不能为空字符串   |
| commodityDescription| string  | 否   | 商品描述     | 可选，允许为空（清空描述）   |
| currentPrice        | decimal | 否   | 当前价格     | 可选，必须为正数            |
| newness             | string  | 否   | 商品新旧度   | 可选，提供时不能为空字符串，支持：全新、95新、9成新、8成新、7成新 |
| quantity            | integer | 否   | 商品数量     | 可选，必须为正整数          |

| images              | file[]  | 否   | 商品图片文件 | 可选，支持多张图片上传，格式：jpg/jpeg/png/gif/webp，单个文件不超过10MB |

**图片更新逻辑说明**:

1. **提供新图片** (提供 `images` 参数):
   - 直接使用新提供的图片文件替换原有的所有图片
   - 上传新图片文件到服务器
   - 更新商品的图片列表和主图片URL

2. **不提供图片** (不提供 `images` 参数):
   - 保持原有图片不变
   - 不对图片进行任何修改

**响应示例**:

```json
{
  "success": true,
  "message": "商品信息更新成功"
}
```

#### 5. 修改商品描述（已废弃）

- **URL**: `/api-8084/commodity/update-description`
- **方法**: `POST`
- **描述**: ⚠️ **此接口已废弃，请使用 `/update-info` 接口进行商品信息更新**
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

#### 6. 获取卖家商品列表

- **URL**: `/api-8084/commodity/list/{sellerId}`
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
      "newness": "九成新",
      "commodityStatus": "ON_SALE",
      "sellerId": "202100001",
      "mainImageUrl": "https://your-oss-domain.com/commodities/202100001/20241217_100000_xyz789.jpg",
      "createdAt": "2023-10-27T10:00:00",
      "updatedAt": "2023-10-27T10:00:00"
    }
  ]
}
```

#### 7. 根据状态获取商品列表

- **URL**: `/api-8084/commodity/list/{sellerId}/status/{status}`
- **方法**: `GET`
- **描述**: 获取指定卖家特定状态的商品列表。

**路径参数**:

| 参数名   | 类型   | 必填 | 描述     | 可选值 |
|----------|--------|------|----------|--------|
| sellerId | string | 是   | 卖家ID   | - |
| status   | string | 是   | 商品状态 | "to_sale", "on_sale", "off_sale", "sold" |

**响应示例**: (同上)

#### 8. 获取商品详情

- **URL**: `/api-8084/commodity/detail/{commodityId}/seller/{sellerId}`
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
    "tagsId": [1,2,3],
    "currentPrice": 4500.00,
    "newness": "九成新",
    "commodityStatus": "ON_SALE",
    "sellerId": "202100001",
    "mainImageUrl": "https://your-oss-domain.com/commodities/202100001/20241217_100000_xyz789.jpg",
    "imageList": "[\"https://your-oss-domain.com/commodities/202100001/20241217_100000_xyz789.jpg\", \"https://your-oss-domain.com/commodities/202100001/20241217_100001_abc456.jpg\"]",
    "createdAt": "2023-10-27T10:00:00",
    "updatedAt": "2023-10-27T10:00:00"
  }
}
```

#### 9. 获取标签列表

- **URL**: `/api-8084/commodity/tags`
- **方法**: `GET`
- **描述**: 根据商品类别ID获取该类别下的所有标签列表。

**查询参数**:

| 参数名      | 类型    | 必填 | 描述         | 验证规则 |
|-------------|---------|------|--------------|----------|
| category_id | integer | 是   | 商品类别ID   | 必须大于0 |

**请求示例**:

```
GET /api-8084/commodity/tags?category_id=1
```

**响应示例**:

```json
{
  "success": true,
  "message": "获取标签列表成功",
  "data": [
    {
      "tid": 1,
      "tagName": "苹果"
    },
    {
      "tid": 2,
      "tagName": "安卓"
    },
    {
      "tid": 3,
      "tagName": "华为"
    }
  ]
}
```

**错误响应示例**:

```json
{
  "success": false,
  "message": "category_id 参数无效，必须为正整数",
  "data": null,
  "errorCode": "VALIDATION_ERROR"
}
```

#### 10. 健康检查

- **URL**: `/api-8084/commodity/health`
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
    private Integer quantity;            // 商品数量
    private String newness;              // 商品新旧度
    private CommodityStatus commodityStatus; // 商品状态
    private String sellerId;             // 卖家ID
    private String mainImageUrl;         // 主图URL (带https前缀的完整OSS访问地址)
    private String imageList;            // 图片列表 (JSON格式，包含带https前缀的完整OSS访问地址)
    private LocalDateTime createdAt;     // 创建时间
    private LocalDateTime updatedAt;     // 更新时间
}
```

### Tag 实体

```java
public class Tag {
    private Integer tid;                 // 标签ID (主键)
    private Integer categoryId;          // 所属类别ID (外键)
    private String tagName;              // 标签名称
}
```

### TagDTO 数据传输对象

```java
public class TagDTO {
    private Integer tid;                 // 标签ID
    private String tagName;              // 标签名称
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
| IMAGES_REQUIRED     | 商品图片不能为空         | 400        |
| NO_VALID_IMAGES     | 没有有效的图片文件       | 400        |
| INVALID_IMAGE_FORMAT| 图片格式不正确           | 400        |
| IMAGE_TOO_LARGE     | 图片文件过大             | 400        |
| OSS_UPLOAD_FAILED   | OSS上传失败              | 500        |
| INTERNAL_ERROR      | 服务器内部错误           | 500        |
| DATABASE_ERROR      | 数据库操作失败           | 500        |
| CREATE_FAILED       | 商品创建失败             | 500        |

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
6. **图片存储**: 图片URL和列表以JSON格式存储，所有图片URL都带有https前缀。
7. **OSS配置**: 需要正确配置OSS的endpoint、accessKeyId和accessKeySecret。
8. **图片上传**: 支持JPG、JPEG、PNG、GIF、WEBP格式，单个文件不超过10MB。
9. **外键约束**: 创建商品时需要确保sellerId在users表中存在。

## 项目结构

```
src/main/java/com/campus/product_management_seller/
├── ProductManagementSellerApplication.java  # 主启动类
├── config/
│   ├── CorsConfig.java                      # CORS跨域配置
│   └── OssConfig.java                       # OSS配置类
├── controller/
│   └── CommodityController.java             # 控制器层
├── dto/
│   ├── ApiResponse.java                     # 统一响应格式
│   ├── CommodityCreateRequest.java          # 商品创建请求DTO
│   ├── CommodityUpdateRequest.java          # 商品信息更新请求DTO
│   ├── CommodityStatusUpdateRequest.java    # 状态更新请求DTO
│   ├── CommodityDescriptionUpdateRequest.java # 描述更新请求DTO（已废弃）
│   └── TagDTO.java                          # 标签数据传输对象
├── entity/
│   ├── Commodity.java                       # 商品实体类
│   └── Tag.java                             # 标签实体类
├── exception/
│   └── GlobalExceptionHandler.java          # 全局异常处理
├── repository/
│   ├── CommodityRepository.java             # 商品数据访问层
│   └── TagRepository.java                   # 标签数据访问层
├── service/
│   ├── CommodityService.java                # 商品业务逻辑层
│   └── TagService.java                      # 标签业务逻辑层
├── util/
│   └── OssUtil.java                         # OSS工具类
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
- **最后更新**: 2024年12月
- **维护状态**: 活跃开发中

## 更新日志

### v0.0.1-SNAPSHOT (2024年12月)

#### 最新更新：商品状态管理重构
- **重要变更**: 修改商品创建和上架逻辑，引入审核机制
- **状态变更**: 
  - 新增 `to_sale`（待审核）状态
  - 商品创建后默认状态为 `to_sale`，需要管理员审核
  - 管理员审核通过后状态变为 `on_sale`（在售）
- **影响接口**:
  - 创建商品接口：商品创建后状态为 `to_sale`
  - 上架接口：现在用于管理员审核通过，将状态从 `to_sale` 改为 `on_sale`
  - 状态查询接口：支持查询 `to_sale` 状态的商品
- **业务价值**: 增强平台管理能力，确保商品质量，防止违规商品直接上架

#### 新增商品已售状态管理
- **新增功能**: 添加商品标记为已售接口 `POST /api-8084/commodity/mark-as-sold`
- **功能描述**: 将商品状态设置为 `sold`（已售）
- **参数要求**: 只需要传入 `commodityId` 和 `sellerId`
- **实现层级**: Service层新增 `markAsSold` 方法，Controller层新增对应接口
- **复用设计**: 使用现有的 `CommodityStatusUpdateRequest` DTO和数据库方法
- **优势**: 提供完整的商品状态管理功能，支持上架、下架、已售三种状态操作

#### 商品上架下架接口简化
- **修改内容**: 简化商品上架和下架接口，移除 `status` 参数
- **影响范围**: 
  - 接口参数：只需要传入 `commodityId` 和 `sellerId`
  - 业务逻辑：系统自动判断上架或下架操作
- **涉及接口**:
  - `POST /api-8084/commodity/put-on-sale` - 商品上架
- `POST /api-8084/commodity/put-off-sale` - 商品下架
- **DTO更新**: `CommodityStatusUpdateRequest.java` 移除 `status` 字段
- **优势**: 简化接口调用，减少参数验证，提高易用性

#### 重要更新：tagsId字段类型优化
- **修改内容**: 将 `tagsId` 字段类型从 `String` 改为 `List<Integer>`
- **影响范围**: 
  - 前端传入格式：从字符串格式改为整数数组格式
  - 后端存储方式：保持JSON格式不变
- **涉及代码层级**:
  - DTO层：`CommodityCreateRequest.java` 中 `tagsId` 字段类型更新
  - Service层：`CommodityService.java` 中 `processTagsId` 方法重构
  - Controller层：`CommodityController.java` 中参数类型调整
- **技术实现**: 使用 `ObjectMapper` 将 `List<Integer>` 转换为JSON字符串存储
- **优势**: 提高类型安全性，简化前端处理逻辑，保持数据一致性
- **前端使用示例**: `"tagsId": [1, 2, 3]` 替代 `"tagsId": "1,2,3"`

#### 商品信息更新功能增强
- **功能扩展**: 更新商品信息接口 `/update-info` 支持更多字段
- **新增字段**: 
  - `quantity`（商品数量）：支持更新商品库存数量
  - `mainImageUrl`（主图片URL）：支持更新商品主要展示图片
  - `imageList`（图片列表）：支持更新商品图片列表，JSON格式存储
- **完整支持字段**: 商品名称、描述、价格、新旧度、数量、图片
- **新旧度选项**: 支持全新、95新、9成新、8成新、7成新五个等级
- **技术实现**: 
  - DTO层：`CommodityUpdateRequest.java` 新增数量和图片相关字段
  - Service层：`updateCommodityInfo` 方法增加对新字段的处理逻辑
  - Controller层：更新接口注释说明支持的字段范围
- **业务价值**: 提供完整的商品信息维护功能，满足卖家对商品全方位管理需求

#### 其他功能更新
- 废弃原有的 `/update-description` 接口，统一使用 `/update-info`
- 支持商品名称、描述、价格、新旧度的统一更新
- **新增OSS图片上传功能**：
  - 集成阿里云OSS服务，支持商品图片直接上传
  - 创建商品接口改为multipart/form-data格式，支持同时上传图片
  - 支持多张图片上传，第一张自动设为主图
  - 图片URL统一使用带https前缀的完整访问地址
  - 添加图片格式和大小验证（支持JPG/JPEG/PNG/GIF/WEBP，单个文件不超过10MB）
  - 新增商品数量(quantity)字段支持
- **新增标签管理功能**：
  - 新增 `Tag` 实体类，对应数据库 `tags` 表
  - 新增 `TagRepository` 数据访问层，支持按类别查询标签
  - 新增 `TagService` 业务逻辑层，提供标签查询服务
  - 新增 `TagDTO` 数据传输对象，用于API响应
  - 新增 `/api-8084/commodity/tags` 接口，根据类别ID获取标签列表
  - 支持标签与商品类别的关联查询
  - 完善了商品创建时的标签ID验证和存储

## 联系方式

如有问题，请联系开发团队。

---

*本文档随代码更新而更新，请以最新版本为准。*