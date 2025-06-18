# API
- 基础url：http://localhost:8091/api/v1
- Content-Type : application/json
- 所有时间格式 : ISO 8601 格式 (例: 2025-06-18T14:37:36Z )

## api具体内容

1. 创建评论/回复

   - 请求

       ```http
       POST /api/v1/comments
       Content-Type: application/json
       ```

   - 请求体

       ```http
       {
         "commodity_id": "01234567-89ab-7def-8123-456789abcde0",
         "user_id": "S20210001",
         "message": "这本书质量很好，内容清晰，推荐购买！",
         "reply_to_message_id": "可选，回复时填写被回复评论的message_id,为空则代表是对商品的直接评论"
       }
       ```

   - 成功响应(200)

       ```http
       {
         "message": "评论创建成功",
         "data": {
           "id": 1,
           "message_id": "550e8400-e29b-41d4-a716-446655440000",
           "commodity_id": "01234567-89ab-7def-8123-456789abcde0",
           "user_id": "S20210001",
           "message": "这本书质量很好，内容清晰，推荐购买！",
           "reply_to_message_id": null,
           "message_type": "comment",
           "created_at": "2025-06-18T14:37:36Z",
           "replies": []
         }
       }
       ```
    - 错误响应(400/500)

       ```http
       {
         "error": "参数错误",
         "message": "具体错误信息"
       }
       ```

2. 获取评论列表

    - 请求

        ```http
        GET /api/v1/comments?commodity_id=01234567-89ab-7def-8123-456789abcde0&page=1&page_size=10&sort_by=created_at&order=desc
        ```

    - 查询参数

        ```http
        {
          "commodity_id": " (必填),商品ID",
          "page": "(可选)页码，默认1",
          "page_size": "(可选),每页数量，默认10，最大100",
          "sort_by": "可选，排序字段，可选值: created_at , id ，默认 created_at",
          "order": (可选)排序方式，可选值: asc , desc ，默认 desc
        }
        ```

    - 成功响应(200)

        ```http
        {
          "message": "获取评论列表成功",
          "data": {
            "comments": [
              {
                "id": 1,
                "message_id": "550e8400-e29b-41d4-a716-446655440000",
                "commodity_id": "01234567-89ab-7def-8123-456789abcde0",
                "user_id": "S20210001",
                "message": "这本书质量很好，内容清晰，推荐购买！",
                "reply_to_message_id": null,
                "message_type": "comment",
                "created_at": "2025-06-18T14:37:36Z",
                "replies": [
                  {
                    "id": 2,
                    "message_id": "660e8400-e29b-41d4-a716-446655440001",
                    "commodity_id": "01234567-89ab-7def-8123-456789abcde0",
                    "user_id": "T20210003",
                    "message": "谢谢推荐，我也觉得这本书很不错！",
                    "reply_to_message_id": "550e8400-e29b-41d4-a716-446655440000",
                    "message_type": "reply",
                    "created_at": "2025-06-18T14:38:36Z",
                    "replies": []
                  }
                ]
              }
            ],
            "total": 1,
            "page": 1,
            "page_size": 10,
            "total_pages": 1,
            "has_next": false,
            "has_prev": false
          }
        }
        ```

3. 获取评论详情

    - 请求

        ```http
        GET /api/v1/comments/{message_id}
        ```

    - 成功响应(200)

        ```http
        {
          "message": "获取评论详情成功",
          "data": {
            "id": 1,
            "message_id": "550e8400-e29b-41d4-a716-446655440000",
            "commodity_id": "01234567-89ab-7def-8123-456789abcde0",
            "user_id": "S20210001",
            "message": "这本书质量很好，内容清晰，推荐购买！",
            "reply_to_message_id": null,
            "message_type": "comment",
            "created_at": "2025-06-18T14:37:36Z",
            "replies": []
          }
        }
        ```

4. 删除评论

    - 请求

        ```http
        DELETE /api/v1/comments/{message_id}?user_id=S20210001
        ```

    - 查询参数

        ```http
        user_id (必填): 用户ID，用于验证权限
        ```

    - 成功响应(200)

        ```http
        {
          "message": "评论删除成功"
        }
        ```

5. 健康检查

    - 请求

        ```http
        GET /health
        ```

    - 成功响应(200)

        ```http
        {
          "status": "ok",
          "service": "comment-service"
        }
        ```
# 数据字段说明

```http
id number 评论ID（数据库主键）
message_id string 消息ID（UUID格式） 
commodity_id string 商品ID 
user_id string 用户ID 
message string 评论内容（最大2000字符） 
reply_to_message_id string|null 被回复的消息ID，顶级评论为null 
message_type string 消息类型："comment"（评论）或"reply"（回复） 
created_at string 创建时间（ISO 8601格式） 
replies array 回复列表（CommentResponse数组）
```

# 分页信息

```
字段 类型 说明 
total number 总评论数 
page number 当前页码 
page_size number 每页数量 
total_pages number 总页数 
has_next boolean 是否有下一页 
has_prev boolean 是否有上一页
```

