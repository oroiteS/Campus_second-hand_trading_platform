# 商品查询服务 Swagger UI 测试指南

## 快速启动

### 方法一：使用启动脚本
```bash
# 在 product-query 目录下运行
./start-swagger-test.bat
```

### 方法二：手动启动
```bash
# 在 product-query 目录下运行
mvn spring-boot:run
```

## 访问地址

启动成功后，访问以下地址：

- **Swagger UI**: http://localhost:8082/product-query/swagger-ui.html
- **API文档**: http://localhost:8082/product-query/v3/api-docs

## 新增API测试

### 按类别查询商品（支持筛选）

**接口路径**: `POST /api/v1/commodities/search/category-filter`

#### 测试用例1：基础类别查询
```json
{
  "categoryId": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

#### 测试用例2：价格区间筛选
```json
{
  "categoryId": 1,
  "priceRange": "50-200",
  "pageNum": 1,
  "pageSize": 10
}
```

#### 测试用例3：新旧度筛选
```json
{
  "categoryId": 1,
  "newness": "全新",
  "pageNum": 1,
  "pageSize": 10
}
```

#### 测试用例4：综合筛选 + 排序
```json
{
  "categoryId": 1,
  "priceRange": "100-500",
  "newness": "全新",
  "sortBy": "price_asc",
  "pageNum": 1,
  "pageSize": 10
}
```

#### 测试用例5：完整参数测试
```json
{
  "categoryId": 1,
  "commodityName": "手机",
  "priceRange": "200-500",
  "newness": "95新",
  "commodityStatus": "on_sale",
  "sortBy": "price_desc",
  "pageNum": 1,
  "pageSize": 20
}
```

## 参数说明

### 必填参数
- `categoryId`: 类别ID（1-8，对应不同商品类别）

### 可选参数

#### 价格区间 (priceRange)
- `"0-50"`: 0到50元
- `"50-200"`: 50到200元
- `"200-500"`: 200到500元
- `"500-1000"`: 500到1000元
- `"1000+"`: 1000元以上

#### 新旧度 (newness)
- `"全新"`: 全新商品
- `"95新"`: 95新商品
- `"9新"`: 9新商品
- `"8新"`: 8新商品
- `"7新"`: 7新商品

#### 排序方式 (sortBy)
- `"price_asc"`: 价格从低到高
- `"price_desc"`: 价格从高到低
- `"time_desc"`: 发布时间从新到旧（默认）
- `"time_asc"`: 发布时间从旧到新

#### 商品状态 (commodityStatus)
- `"on_sale"`: 在售（默认）
- `"sold"`: 已售出
- `"off_sale"`: 下架

#### 分页参数
- `pageNum`: 页码（默认1）
- `pageSize`: 每页大小（默认10，最大100）

## 响应格式

成功响应示例：
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "commodities": [
      {
        "commodityId": "COMM001",
        "commodityName": "iPhone 14",
        "currentPrice": 4999.00,
        "categoryId": 1,
        "newness": "全新",
        "commodityStatus": "on_sale",
        "sellerId": "USER001",
        "createdAt": "2024-01-15T10:30:00"
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "totalCount": 25
  }
}
```

## 常见问题

### 1. 服务启动失败
- 检查端口8082是否被占用
- 确认数据库连接配置正确
- 查看控制台错误日志

### 2. Swagger UI无法访问
- 确认服务已完全启动
- 检查防火墙设置
- 尝试清除浏览器缓存

### 3. API测试返回错误
- 检查请求参数格式是否正确
- 确认categoryId是否存在
- 查看服务端日志获取详细错误信息

## 开发调试

### 查看日志
```bash
# 查看应用日志
tail -f logs/product-query.log

# 查看SQL执行日志（如果开启了SQL日志）
tail -f logs/sql.log
```

### 数据库查询验证
```sql
-- 查看类别1的商品数量
SELECT COUNT(*) FROM commodity WHERE category_id = 1;

-- 查看价格区间分布
SELECT 
  CASE 
    WHEN current_price <= 50 THEN '0-50'
WHEN current_price <= 200 THEN '50-200'
WHEN current_price <= 500 THEN '200-500'
WHEN current_price <= 1000 THEN '500-1000'
    ELSE '1000+'
  END as price_range,
  COUNT(*) as count
FROM commodity 
WHERE category_id = 1 
GROUP BY price_range;
```

## 性能测试

可以使用Swagger UI的批量测试功能，或者结合JMeter等工具进行性能测试：

1. 测试不同分页大小的响应时间
2. 测试复杂筛选条件的查询性能
3. 测试并发请求的处理能力

---

**注意**: 测试时请使用真实的测试数据，确保数据库中有足够的商品数据来验证筛选功能的正确性。