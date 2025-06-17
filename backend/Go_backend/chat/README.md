# 校园二手交易平台聊天系统

基于Go语言开发的实时聊天系统，支持WebSocket实时通信，使用MySQL存储聊天数据，Redis缓存未读消息计数。

## 功能特性

- ✅ WebSocket实时聊天
- ✅ 聊天会话管理
- ✅ 消息发送与接收
- ✅ 未读消息计数
- ✅ 消息已读状态
- ✅ Redis缓存优化
- ✅ Swagger API文档
- ✅ CORS跨域支持

## 技术栈

- **语言**: Go 1.24.4
- **Web框架**: Gin
- **WebSocket**: Gorilla WebSocket
- **数据库**: MySQL (GORM)
- **缓存**: Redis
- **API文档**: Swagger

## 项目结构

```
go_chat/
├── main.go                 # 应用入口
├── go.mod                  # Go模块文件
├── config/
│   └── config.go          # 配置管理
├── database/
│   ├── mysql.go           # MySQL连接
│   └── redis.go           # Redis连接
├── models/
│   └── models.go          # 数据模型
├── services/
│   ├── chat_service.go    # 聊天业务逻辑
│   └── websocket.go       # WebSocket服务
├── handlers/
│   └── chat_handler.go    # HTTP处理器
├── middleware/
│   └── cors.go            # CORS中间件
└── docs/
    └── docs.go            # Swagger文档
```

## 数据库配置

### MySQL配置
- 主机: localhost:3306
- 数据库: campus
- 用户: campus_test
- 密码: campus_suep

### Redis配置
- 主机: localhost:6379
- 密码: 123456
- 数据库: 0

## 安装和运行

### 1. 安装依赖

```bash
cd go_chat
go mod tidy
```

### 2. 确保数据库运行

确保MySQL和Redis服务正在运行，并且已经执行了数据库初始化脚本。

### 3. 运行应用

```bash
go run main.go
```

应用将在 `http://localhost:8080` 启动。

## API文档

启动应用后，访问 `http://localhost:8080/swagger/index.html` 查看完整的API文档。

## API端点

### WebSocket
- `GET /api/v1/ws/{user_id}` - 建立WebSocket连接

### 聊天会话
- `POST /api/v1/chat/sessions` - 创建聊天会话
- `GET /api/v1/chat/sessions/{user_id}` - 获取用户会话列表

### 消息管理
- `GET /api/v1/chat/sessions/{session_id}/messages` - 获取会话消息
- `POST /api/v1/chat/messages` - 发送消息
- `PUT /api/v1/chat/messages/{message_id}/read` - 标记消息为已读
- `GET /api/v1/chat/unread/{user_id}` - 获取未读消息数量

### 健康检查
- `GET /health` - 健康检查

## WebSocket消息格式

### 客户端发送消息格式
```json
{
  "type": "message",
  "data": {
    "session_id": "会话ID",
    "content": "消息内容",
    "message_type": "text"
  },
  "user_id": "发送者ID",
  "time": "2024-01-01T00:00:00Z"
}
```

### 服务器推送消息格式
```json
{
  "type": "message",
  "data": {
    "message_id": "消息ID",
    "session_id": "会话ID",
    "sender_id": "发送者ID",
    "receiver_id": "接收者ID",
    "content": "消息内容",
    "message_type": "text",
    "sent_at": "2024-01-01T00:00:00Z"
  },
  "user_id": "发送者ID",
  "time": "2024-01-01T00:00:00Z"
}
```

## 环境变量

可以通过环境变量覆盖默认配置：

```bash
# 服务器配置
SERVER_PORT=8080

# MySQL配置
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=campus_test
MYSQL_PASSWORD=campus_suep
MYSQL_DATABASE=campus

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=123456
```

## 使用示例

### 1. 创建聊天会话

```bash
curl -X POST http://localhost:8080/api/v1/chat/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "buyer_id": "123456789",
    "seller_id": "987654321"
  }'
```

### 2. 发送消息

```bash
curl -X POST http://localhost:8080/api/v1/chat/messages \
  -H "Content-Type: application/json" \
  -d '{
    "session_id": "会话ID",
    "sender_id": "123456789",
    "receiver_id": "987654321",
    "message_type": "text",
    "content": "Hello, World!"
  }'
```

### 3. WebSocket连接示例（JavaScript）

```javascript
const ws = new WebSocket('ws://localhost:8080/api/v1/ws/123456789');

ws.onopen = function() {
    console.log('WebSocket连接已建立');
};

ws.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log('收到消息:', message);
};

ws.onerror = function(error) {
    console.error('WebSocket错误:', error);
};

ws.onclose = function() {
    console.log('WebSocket连接已关闭');
};

// 发送心跳
setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({
            type: 'ping',
            data: 'ping',
            user_id: '123456789',
            time: new Date().toISOString()
        }));
    }
}, 30000);
```

## 注意事项

1. 确保MySQL和Redis服务正在运行
2. 数据库表结构需要与现有的SQL脚本匹配
3. WebSocket连接需要提供有效的用户ID
4. 生产环境中应该添加身份验证和授权机制
5. 建议在生产环境中使用HTTPS和WSS

## 开发和调试

### 生成Swagger文档

如果修改了API注释，需要重新生成Swagger文档：

```bash
# 安装swag工具
go install github.com/swaggo/swag/cmd/swag@latest

# 生成文档
swag init
```

### 日志级别

应用使用标准的Go log包，可以通过修改代码调整日志级别。

## 许可证

Apache 2.0