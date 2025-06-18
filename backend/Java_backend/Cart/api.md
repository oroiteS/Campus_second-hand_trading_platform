---

## 🛒 购物车接口文档（CartController）

###  1. 获取用户购物车中的商品列表

* **接口路径**：`GET /cart/commodities`
* **功能描述**：获取指定用户购物车中的商品，可按商品类别筛选。
* **请求参数**：

| 参数名        | 类型     | 是否必须 | 说明                        |
| ---------- | ------ | ---- | ------------------------- |
| `userId`   | string | 是    | 用户 ID（外键）                 |
| `category` | string | 否    | 商品类别，如 `"运动器材"`，默认 `"全部"` |

* **响应示例**（HTTP 200 OK）：

```json
[
  {
    "commodityId": "e2f48d9b-f332-11ee-a905-0242ac110002",
    "commodityName": "运动手环",
    "commodityDescription": "几乎全新",
    "categoryId": 4,
    "currentPrice": 129.99,
    "commodityStatus": "on_sale",
    "sellerId": "202201001",
    "mainImageUrl": "http://example.com/image.jpg",
    "imageList": "[\"url1\",\"url2\"]",
    "createdAt": "2025-06-01T10:00:00",
    "updatedAt": "2025-06-02T12:00:00",
    "quantity": 1
  }
]
```

---

###  2. 添加商品到购物车

* **接口路径**：`POST /cart/add`

* **功能描述**：将指定商品添加到指定用户的购物车中。

* **请求参数（Query）**：

| 参数名           | 类型     | 是否必须 | 说明               |
| ------------- | ------ | ---- | ---------------- |
| `userId`      | string | 是    | 用户 ID（外键）        |
| `commodityId` | string | 是    | 商品 ID（UUIDv7，外键） |

* **响应示例**（HTTP 200 OK）：

```json
{
  "success": true
}
```

---

###  3. 从购物车中移除商品

* **接口路径**：`POST /cart/remove`

* **功能描述**：将指定商品从用户的购物车中移除。

* **请求参数（Query）**：

| 参数名           | 类型     | 是否必须 | 说明               |
| ------------- | ------ | ---- | ---------------- |
| `userId`      | string | 是    | 用户 ID（外键）        |
| `commodityId` | string | 是    | 商品 ID（UUIDv7，外键） |

* **响应示例**：

```json
{
  "success": true
}
```

---

##  错误响应通用格式

* **HTTP 状态码**：`400 Bad Request` / `500 Internal Server Error`
* **错误响应示例**：

```json
{
  "success": false,
  "error": "商品不存在或外键冲突"
}
```

---
