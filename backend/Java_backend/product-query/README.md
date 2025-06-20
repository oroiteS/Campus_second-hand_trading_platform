# 商品查询服务 (Product Query Service)

## 项目简介

商品查询服务是校园二手交易平台的核心微服务之一，专门负责提供商品分类查询功能。该服务采用 Spring Boot + MyBatis Plus 架构，提供 RESTful API 接口，支持按分类查询、价格筛选、新旧度筛选、排序和分页功能。

## 技术栈

- **框架**: Spring Boot 2.7.x
- **数据访问**: MyBatis Plus 3.5.x
- **数据库**: MySQL 8.0+
- **连接池**: HikariCP
- **API文档**: SpringDoc OpenAPI 3 (Swagger)
- **数据验证**: Spring Boot Validation
- **JSON处理**: Jackson
- **构建工具**: Maven

## 项目结构

```
product-query/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/campus/productquery/
│   │   │       ├── ProductQueryApplication.java          # 启动类
│   │   │       ├── config/
│   │   │       │   └── MyBatisPlusConfig.java           # MyBatis Plus配置
│   │   │       ├── controller/
│   │   │       │   └── CommodityQueryController.java    # 控制器层
│   │   │       ├── service/
│   │   │       │   ├── CommodityQueryService.java       # 服务接口
│   │   │       │   └── impl/
│   │   │       │       └── CommodityQueryServiceImpl.java # 服务实现
│   │   │       ├── dao/
│   │   │       │   └── CommodityRepository.java         # 数据访问层
│   │   │       ├── pojo/
│   │   │       │   └── Commodity.java                   # 实体类
│   │   │       └── dto/
│   │   │           ├── CommodityQueryRequest.java       # 查询请求DTO
│   │   │           ├── CommodityResponse.java           # 商品响应DTO
│   │   │           └── PagedCommodityResponse.java      # 分页响应DTO
│   │   └── resources/
│   │       ├── application.yml                          # 配置文件
│   │       └── mapper/
│   │           └── CommodityMapper.xml                  # MyBatis映射文件
│   └── test/
├── pom.xml                                              # Maven配置
├── README.md                                            # 项目说明
└── API.md                                               # API文档
```

## 功能特性

### 核心功能

1. **分类查询**
   - 支持8个商品分类（1-8）的查询
   - 自动过滤在售商品（commodity_status = 'on_sale'）

2. **筛选功能**
   - **价格区间筛选**：0-50元、50-200元、200-500元、500-1000元、1000元以上
   - **新旧度筛选**：全新、95新、9新
   - 筛选条件可选，支持只按分类查询

3. **排序功能**
   - 价格从低到高（price_asc）
   - 价格从高到低（price_desc）
   - 发布时间从新到旧（time_desc，默认）
   - 发布时间从旧到新（time_asc）

4. **分页功能**
   - 支持自定义页码和每页大小
   - 默认每页10条记录，最大支持100条
   - 返回完整的分页信息

### 技术特性

- **RESTful API**: 遵循REST设计原则
- **分页支持**: 支持灵活的分页查询
- **参数验证**: 完整的请求参数验证
- **异常处理**: 统一的异常处理机制
- **API文档**: 集成Swagger自动生成文档
- **日志记录**: 完整的日志记录
- **配置管理**: 灵活的配置管理

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+

### 数据库准备

1. 创建数据库：
```sql
CREATE DATABASE campus_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 创建 `commodities` 表（参考项目根目录的建表语句）

### 配置修改

修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trading?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 启动服务

1. **使用Maven启动**：
```bash
cd product-query
mvn spring-boot:run
```

2. **使用IDE启动**：
   - 导入项目到IDE
   - 运行 `ProductQueryApplication.java`

3. **打包运行**：
```bash
mvn clean package
java -jar target/product-query-1.0.0.jar
```

### 验证服务

服务启动后，访问以下地址验证：

- **健康检查**: http://localhost:8082/product-query/actuator/health
- **Swagger UI**: http://localhost:8082/product-query/swagger-ui.html
- **API文档**: http://localhost:8082/product-query/api-docs

## API 使用示例

### 分类查询商品 API

**接口地址**: `POST /api/commodity-query/category`

**请求方法**: POST

**Content-Type**: application/json

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| categoryId | Integer | 是 | 商品分类ID（1-8） | 1 |
| priceRange | String | 否 | 价格区间 | "0-50" |
| newness | String | 否 | 新旧度 | "全新" |
| sortBy | String | 否 | 排序方式 | "time_desc" |
| pageNum | Integer | 否 | 页码（默认1） | 1 |
| pageSize | Integer | 否 | 每页大小（默认10，最大100） | 20 |

#### 价格区间选项
- `"0-50"`: 0-50元
- `"50-200"`: 50-200元
- `"200-500"`: 200-500元
- `"500-1000"`: 500-1000元
- `"1000+"`: 1000元以上

#### 新旧度选项
- `"全新"`: 全新商品
- `"95新"`: 95新商品
- `"9新"`: 9新商品

#### 排序方式选项
- `"price_asc"`: 价格从低到高
- `"price_desc"`: 价格从高到低
- `"time_desc"`: 发布时间从新到旧（默认）
- `"time_asc"`: 发布时间从旧到新

#### 请求示例

**1. 只按分类查询（获取所有数据）**
```bash
curl -X POST "http://localhost:8082/product-query/api/commodity-query/category" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "pageSize": 100
  }'
```

**2. 完整筛选查询**
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

**3. 价格筛选查询**
```bash
curl -X POST "http://localhost:8082/product-query/api/commodity-query/category" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 2,
    "priceRange": "0-50",
    "sortBy": "time_desc"
  }'
```

#### 返回数据格式

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "commodityId": "uuid-string",
        "commodityName": "商品名称",
        "commodityPrice": 299.99,
        "commodityDescription": "商品描述",
        "commodityNewness": "全新",
        "commodityStatus": "on_sale",
        "categoryId": 1,
        "sellerId": "seller-uuid",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "total": 150,
    "size": 20,
    "current": 1,
    "pages": 8
  }
}
```

#### 分页说明

- **默认分页**: 每页10条记录
- **最大分页**: 每页最多100条记录
- **获取全部数据**: 设置 `pageSize` 为100，根据返回的 `pages` 字段进行多次请求
- **分页信息**:
  - `total`: 总记录数
  - `size`: 当前页大小
  - `current`: 当前页码
  - `pages`: 总页数
  - `records`: 当前页数据列表

## 配置说明

### 应用配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| server.port | 8082 | 服务端口 |
| server.servlet.context-path | /product-query | 上下文路径 |
| spring.application.name | product-query-service | 应用名称 |

### 数据库配置

| 配置项 | 说明 |
|--------|------|
| spring.datasource.url | 数据库连接URL |
| spring.datasource.username | 数据库用户名 |
| spring.datasource.password | 数据库密码 |
| spring.datasource.hikari.* | HikariCP连接池配置 |

### MyBatis Plus配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| mybatis-plus.configuration.map-underscore-to-camel-case | true | 驼峰命名转换 |
| mybatis-plus.configuration.log-impl | StdOutImpl | 日志实现 |
| mybatis-plus.global-config.db-config.id-type | assign_uuid | 主键类型 |

## 开发指南

### 项目架构

本项目采用分层架构设计：

1. **Controller层** (`CommodityQueryController`): 处理HTTP请求，参数验证
2. **Service层** (`CommodityQueryService`): 业务逻辑处理
3. **Repository层** (`CommodityRepository`): 数据访问，基于MyBatis-Plus的BaseMapper
4. **Entity层** (`Commodity`): 数据实体映射
5. **DTO层** (`CategoryQueryRequest`, `PagedCommodityResponse`): 数据传输对象

### 核心实现

- **查询构建**: 使用MyBatis-Plus的`QueryWrapper`动态构建查询条件
- **分页处理**: 集成MyBatis-Plus分页插件，自动处理分页逻辑
- **参数验证**: 使用Spring Validation进行请求参数校验
- **异常处理**: 统一异常处理和响应格式

### 代码规范

- 遵循阿里巴巴Java开发手册
- 使用统一的命名规范
- 添加必要的注释和文档
- 进行充分的参数验证
- 处理异常情况

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn integration-test

# 生成测试报告
mvn surefire-report:report
```

## 部署说明

### Docker 部署

1. **构建镜像**：
```bash
docker build -t product-query:1.0.0 .
```

2. **运行容器**：
```bash
docker run -d -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/campus_trading \
  -e SPRING_DATASOURCE_USERNAME=username \
  -e SPRING_DATASOURCE_PASSWORD=password \
  product-query:1.0.0
```

### 生产环境配置

1. **修改日志级别**
2. **配置外部配置文件**
3. **设置JVM参数**
4. **配置监控和告警**

## 监控和运维

### 健康检查

- **应用健康**: `/actuator/health`
- **数据库连接**: 自动检查数据库连接状态
- **内存使用**: `/actuator/metrics`

### 日志管理

- **日志文件**: `logs/product-query.log`
- **日志级别**: 可通过配置文件调整
- **日志轮转**: 支持按大小和时间轮转

## 常见问题

### Q: 服务启动失败

A: 检查以下几点：
1. 数据库连接配置是否正确
2. 数据库是否已创建
3. 端口是否被占用
4. JDK版本是否符合要求

### Q: 查询结果为空

A: 检查以下几点：
1. 数据库中是否有数据
2. 查询条件是否正确
3. 字段映射是否正确

### Q: 分页查询异常

A: 检查以下几点：
1. 页码和页大小是否合理
2. MyBatis Plus分页插件是否正确配置
3. 排序字段是否存在

## 版本历史

- **v1.0.0**: 初始版本，实现基础查询功能

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队：
- 邮箱: dev@campus-trading.com
- 项目地址: https://github.com/campus-trading/product-query