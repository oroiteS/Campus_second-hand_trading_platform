          
# Profile模块API文档

## 概述
Profile模块提供用户信息管理相关的API接口，包括用户信息查询、更新和头像管理功能。

## 基础信息
- **模块名称**: Profile用户信息管理模块
- **基础路径**: `/api/user`
- **运行端口**: `8089
- **技术栈**: Spring Boot + MyBatis Plus
- **文档标准**: OpenAPI 3.0 (Swagger)

## API接口列表

### 1. 获取用户信息

**接口描述**: 根据用户ID获取用户信息（脱敏处理）

- **请求方式**: `POST`
- **请求路径**: `/api/user/info`
- **请求头**: `Content-Type: application/json`

**请求参数**:
```json
{
  "userId": "U10000001"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取用户信息成功",
  "code": 200,
  "data": {
    "userId": "U10000001",
    "userName": "张三",
    "telephone": "138****5678",
    "realName": "张三",
    "avatarUrl": "/uploads/avatars/avatar.jpg",
    "userLocLongitude": 116.397128,
    "userLocLatitude": 39.916527,
    "userStatus": false,
    "createAt": "2024-01-01T10:00:00",
    "idCard": "110***********1234",
    "isBanned": false
  }
}
```

**状态码**:
- `200`: 成功获取用户信息
- `404`: 用户不存在
- `500`: 服务器内部错误

### 2. 获取用户完整信息（管理员接口）

**接口描述**: 管理员接口，获取包含敏感信息的完整用户数据

- **请求方式**: `POST`
- **请求路径**: `/api/user/admin/info`
- **请求头**: `Content-Type: application/json`

**请求参数**:
```json
{
  "userId": "U10000001"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取用户完整信息成功",
  "code": 200,
  "data": {
    "userId": "U10000001",
    "userName": "张三",
    "telephone": "13812345678",
    "realName": "张三",
    "avatarUrl": "/uploads/avatars/avatar.jpg",
    "userLocLongitude": 116.397128,
    "userLocLatitude": 39.916527,
    "userStatus": false,
    "createAt": "2024-01-01T10:00:00",
    "idCard": "110101199001011234",
    "isBanned": false
  }
}
```

### 3. 获取用户基本信息（公开接口）

**接口描述**: 公开接口，获取用户基本信息（仅包含非敏感数据）

- **请求方式**: `POST`
- **请求路径**: `/api/user/basic`
- **请求头**: `Content-Type: application/json`

**请求参数**:
```json
{
  "userId": "U10000001"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取用户基本信息成功",
  "code": 200,
  "data": {
    "userId": "U10000001",
    "userName": "张三",
    "avatarUrl": "/uploads/avatars/avatar.jpg",
    "createAt": "2024-01-01T10:00:00",
    "isBanned": false
  }
}
```

### 4. 更新用户信息

**接口描述**: 更新用户的用户名和电话号码

- **请求方式**: `POST`
- **请求路径**: `/api/user/update`
- **请求头**: `Content-Type: application/json`

**请求参数**:
```json
{
  "userId": "U10000001",
  "userName": "李四",
  "telephone": "13987654321"
}
```

**参数验证规则**:
- `userId`: 必填，不能为空
- `userName`: 必填，不能为空
- `telephone`: 必填，必须符合手机号格式（^1[3-9]\d{9}$）

**响应示例**:
```json
{
  "success": true,
  "message": "用户信息更新成功",
  "code": 200,
  "data": {
    "userId": "U10000001",
    "userName": "李四",
    "telephone": "139****4321"
  }
}
```

**状态码**:
- `200`: 用户信息更新成功
- `400`: 请求参数错误
- `404`: 用户不存在
- `409`: 用户名或电话号码已被使用
- `500`: 服务器内部错误

### 5. 上传用户头像

**接口描述**: 上传用户头像文件，支持JPG、PNG、GIF、WEBP格式，最大5MB

- **请求方式**: `POST`
- **请求路径**: `/api/user/avatar/upload`
- **请求头**: `Content-Type: multipart/form-data`

**请求参数**:
- `file`: 头像文件（multipart/form-data）
- `userId`: 用户ID（form-data）

**文件限制**:
- 支持格式: JPG、PNG、GIF、WEBP
- 最大大小: 5MB

**响应示例**:
```json
{
  "success": true,
  "message": "头像上传成功",
  "code": 200,
  "data": {
    "userId": "U10000001",
    "avatarUrl": "/uploads/avatars/U10000001_avatar_20240101.jpg"
  }
}
```

**状态码**:
- `200`: 头像上传成功
- `400`: 请求参数错误或文件格式不支持
- `404`: 用户不存在
- `500`: 服务器内部错误

### 6. 更新用户头像URL

**接口描述**: 直接更新用户头像URL（不上传文件）

- **请求方式**: `POST`
- **请求路径**: `/api/user/avatar/update`
- **请求头**: `Content-Type: application/json`

**请求参数**:
```json
{
  "userId": "U10000001",
  "avatarUrl": "/uploads/avatars/new_avatar.jpg"
}
```

**参数验证规则**:
- `userId`: 必填，不能为空，最大长度9位
- `avatarUrl`: 可选，最大长度255位

**响应示例**:
```json
{
  "success": true,
  "message": "头像URL更新成功",
  "code": 200,
  "data": {
    "userId": "U10000001",
    "userName": "张三",
    "avatarUrl": "/uploads/avatars/new_avatar.jpg"
  }
}
```

## 数据模型

### UserInfoRequest
```json
{
  "userId": "string (必填，用户ID)"
}
```

### UpdateUserRequest
```json
{
  "userId": "string (必填，用户ID)",
  "userName": "string (必填，用户名)",
  "telephone": "string (必填，电话号码，格式：1[3-9]\\d{9})"
}
```

### UpdateAvatarRequest
```json
{
  "userId": "string (必填，用户ID，最大9位)",
  "avatarUrl": "string (可选，头像URL，最大255位)"
}
```

### User实体（部分字段）
```json
{
  "userId": "string (用户ID，主键)",
  "userName": "string (用户名，最大20位)",
  "telephone": "string (电话号码)",
  "realName": "string (真实姓名，最大50位)",
  "avatarUrl": "string (头像URL，最大255位)",
  "userLocLongitude": "decimal (经度，-180到180)",
  "userLocLatitude": "decimal (纬度，-90到90)",
  "userStatus": "boolean (用户状态)",
  "createAt": "datetime (创建时间)",
  "isBanned": "boolean (是否被封禁)"
}
```

## 错误响应格式

所有接口的错误响应都遵循统一格式：

```json
{
  "success": false,
  "message": "错误描述信息",
  "code": 错误状态码
}
```

## 注意事项

1. **数据脱敏**: 普通用户接口会对敏感信息进行脱敏处理（电话号码、身份证号）
2. **管理员接口**: `/admin/info` 接口返回完整信息，需要管理员权限
3. **文件上传**: 头像上传支持多种格式，有大小限制
4. **参数验证**: 所有接口都有严格的参数验证
5. **跨域支持**: 所有接口支持跨域访问（@CrossOrigin）
6. **Swagger文档**: 集成了Swagger，可通过 `/swagger-ui.html` 访问在线文档
        