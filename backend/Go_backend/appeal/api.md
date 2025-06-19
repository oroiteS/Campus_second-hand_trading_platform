# 申诉模块 (Appeal Users) API 文档

## 基础路径

`/api/v1/appeals`

---

## 1. 发起申诉

- **Endpoint:** `POST /`
- **描述:** 用户发起一个新的申诉请求。
- **请求体 (JSON):**
  ```json
  {
    "argue1_id": "string", // 申诉发起者ID (必填)
    "argue2_id": "string", // 被申诉者ID (可选)
    "order_id": "string",  // 关联的订单ID (可选)
    "reason": "string"      // 申诉理由 (必填)
  }
  ```
- **成功响应 (201 Created):**
  ```json
  {
    "argument_id": "...",
    "argue1_id": "...",
    "argue2_id": "...",
    "order_id": "...",
    "reason": "...",
    "created_at": "...",
    "root_id": null,
    "status": "process"
  }
  ```

---

## 2. 取消申诉

- **Endpoint:** `PUT /:argumentId/cancel`
- **描述:** 申诉发起者取消自己的申诉，状态将变为 `finish`。
- **URL 参数:**
  - `argumentId`: 申诉ID (必填)
- **Header:**
  - `User-ID`: 当前操作的用户ID (必填)
- **成功响应 (200 OK):**
  ```json
  {
    "message": "申诉已取消"
  }
  ```

---

## 3. 查看单个订单的申诉状态

- **Endpoint:** `GET /order/:orderId/status`
- **描述:** 根据订单ID查询该订单的申诉状态。
- **URL 参数:**
  - `orderId`: 订单ID (必填)
- **成功响应 (200 OK):**
  - 如果存在申诉:
    ```json
    {
      "status": "process" // 或 'finish', 'refuse'
    }
    ```
  - 如果不存在申诉:
    ```json
    {
      "status": null
    }
    ```

---

## 4. 批量查询订单的申诉状态

- **Endpoint:** `POST /batch-status`
- **描述:** 批量查询多个订单的申诉状态。
- **请求体 (JSON):**
  ```json
  {
    "order_ids": [
      "order_id_1",
      "order_id_2"
    ]
  }
  ```
- **成功响应 (200 OK):**
  ```json
  {
    "order_id_1": "process",
    "order_id_2": null
  }
  ```

---

## 5. 管理员更新申诉状态

- **Endpoint:** `PUT /:argumentId/admin-update`
- **描述:** 管理员更新指定申诉的状态，并记录操作的管理员ID。
- **URL 参数:**
  - `argumentId`: 申诉ID (必填)
- **请求体 (JSON):**
  ```json
  {
    "root_id": "string", // 管理员ID (必填)
    "status": "string"  // 新的状态 (必填, 'finish', 'refuse', 或 'process')
  }
  ```
- **成功响应 (200 OK):**
  ```json
  {
    "message": "申诉状态更新成功",
    "appeal": {
      "argument_id": "...",
      "status": "finish",
      "root_id": "...",
      // ... 其他申诉详情
    }
  }
  ```
