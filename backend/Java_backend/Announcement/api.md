
---

## 📘 公告查询接口

### 1. 接口地址

```
GET /announcements
```

---

### 2. 接口说明

返回符合条件的公告列表。支持按管理员筛选、按数量限制、可见性过滤。默认返回所有可见公告，按时间倒序排列。

---

### 3. 请求参数（Query Parameters）

| 参数名      | 类型     | 是否必填 | 说明                     |
| -------- | ------ | ---- | ---------------------- |
| `rootId` | string | 否    | 管理员ID，若传入，仅返回该管理员发布的公告 |
| `n`      | int    | 否    | 返回最近的前 n 条公告           |

📌 说明：

* 两个参数都不填 → 返回所有可见公告
* 只填 `n` → 返回最近 n 条公告
* 只填 `rootId` → 返回该管理员的全部可见公告
* 同时填 → 返回该管理员的最近 n 条可见公告

---

### 4. 成功响应

#### HTTP 状态码：

```
200 OK
```

#### 响应体格式（JSON）：

```json
[
  {
    "announcementId": "A0001",
    "rootId": "R2023001",
    "createdAt": "2025-06-17T14:30:00",
    "content": "本周六进行系统维护，请注意。",
    "visibleStatus": false
  }
]
```

---

### 5. 失败响应（示例）

| 状态码 | 场景     | 示例响应                                     |
| --- | ------ | ---------------------------------------- |
| 500 | 后端服务异常 | `{ "message": "Internal Server Error" }` |

---

### 6. 示例调用

#### 6.1 获取所有公告（默认）：

```http
GET /announcements
```

#### 6.2 获取最近 5 条公告：

```http
GET /announcements?n=5
```

#### 6.3 获取某管理员的所有公告：

```http
GET /announcements?rootId=R20230123
```

#### 6.4 获取某管理员最近 3 条公告：

```http
GET /announcements?rootId=R20230123&n=3
```

---

### 7. 注意事项

* 所有公告结果默认按 `createdAt` 字段倒序排列；
* 返回的公告数据已自动过滤不可见（`visible_status = 1`）的数据；
* 请确保管理员 `rootId` 在系统中存在。

---
以下是“新建公告”接口的 **API 文档**，格式清晰，适用于对接前端或生成 Swagger/OpenAPI 说明文档。

---

## 📝 新建公告接口

### 📌 接口地址

```
POST /announcements
```

---

### 🧾 功能描述

管理员创建一条新的公告信息。

---

### 🧑‍💼 权限要求

仅允许管理员用户调用（需在控制器中添加权限校验，若使用）。

---

### 📥 请求参数（JSON 格式）

| 字段名              | 类型        | 是否必填 | 说明                 |
| ---------------- | --------- | ---- | ------------------ |
| `announcementId` | `string`  | 否    | 公告 ID，建议由后端自动生成    |
| `rootId`         | `string`  | ✅ 是  | 管理员 ID，对应 Root 表主键 |
| `content`        | `string`  | ✅ 是  | 公告内容               |
| `visibleStatus`  | `boolean` | 否    | 公告是否可见，默认 `false`  |

> ⚠️ 提示：如果不传 `visibleStatus`，系统默认公告为“可见”。

---

### 📤 响应参数

#### ✅ 成功响应（HTTP 200）

```json
{
  "code": 200,
  "message": "公告创建成功"
}
```

#### ❌ 失败响应（HTTP 500）

```json
{
  "code": 500,
  "message": "公告创建失败"
}
```

---

### 🧪 请求示例

#### ✅ 正确示例

```http
POST /announcements
Content-Type: application/json

{
  "rootId": "R20231234",
  "content": "下周起进入期末考试阶段，请大家注意考试安排。",
  "visibleStatus": false
}
```

---

### 📘 补充说明

* `announcementId` 通常由系统生成（推荐），如使用 UUID 或雪花算法，避免前端生成重复 ID。
  
---

## 🧾 更新公告信息

### 📌 请求地址

```
PUT /announcements
```

---

### 📋 功能描述

> 管理员更新已发布的公告信息（内容、可见状态）。

---

### 🛠️ 请求方式

```
PUT
```

---

### 🔐 权限要求（可选）

* 仅允许管理员修改自己发布的公告（若有 RootId 校验逻辑）。
* 建议使用 Token 或 Session 校验用户身份（未实现）。

---

### 📥 请求参数（RequestBody）

```json
{
  "announcementId": "A123456789",
  "content": "更新后的公告内容",
  "visibleStatus": true
}
```

| 字段名            | 类型        | 是否必填 | 说明                        |
| -------------- | --------- | ---- | ------------------------- |
| announcementId | `string`  | 是    | 公告主键 ID                   |
| content        | `string`  | 是    | 新的公告内容                    |
| visibleStatus  | `boolean` | 是    | 是否可见（true: 不可见，false: 可见） |

---

### 📤 成功响应

```json
{
  "code": 200,
  "message": "公告更新成功"
}
```

---

### ❌ 失败响应

#### 情况一：数据库更新失败或公告不存在

```json
{
  "code": 500,
  "message": "公告更新失败"
}
```

---

### 🔍 示例调用（cURL）

```bash
curl -X PUT http://localhost:8092/announcements \
  -H "Content-Type: application/json" \
  -d '{
        "announcementId": "A123456789",
        "content": "系统维护将在今晚11点进行",
        "visibleStatus": false
      }'
```

---

### 🧩 备注

* `createdAt` 和 `rootId` 无需修改，因此无需出现在请求体中。
