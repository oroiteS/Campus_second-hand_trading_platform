


# 附近的人功能API文档

## 1. 概述

提供用户位置更新和附近用户查询功能，基于Haversine算法计算距离，返回指定半径范围内的用户列表。

---

## 2. 端点信息

| 方法 | 路径               | 说明                     |
|------|--------------------|--------------------------|
| POST | `/api/user/nearby` | 更新位置并获取附近用户   |

---

## 3. 请求参数

| 参数名  | 类型       | 是否必填 | 说明                 |
|---------|------------|----------|----------------------|
| userId  | String     | 是       | 用户唯一标识         |
| lat     | BigDecimal | 是       | 纬度（范围：-90~90） |
| lon     | BigDecimal | 是       | 经度（范围：-180~180） |

**注意：**  
- 坐标需使用WGS84坐标系  
- 纬度(lon)参数顺序：纬度在前，经度在后  
- 查询半径：1000米

---

## 4. 请求示例

```bash
curl -X POST "http://localhost:8080/api/user/nearby?userId=U123&lat=31.230416&lon=121.473701"
```

---

## 5. 响应说明

### 5.1 成功响应

**状态码：** 200 OK  
**响应格式：** JSON数组

| 字段名             | 类型       | 说明                     |
|--------------------|------------|--------------------------|
| userId             | String     | 用户ID                   |
| userName           | String     | 用户名                   |
| password           | String     | 密码（需加密存储）       |
| telephone          | String     | 手机号                   |
| realName           | String     | 真实姓名                 |
| avatarUrl          | String     | 头像URL                  |
| userLocLatitude    | BigDecimal | 用户纬度                 |
| userLocLongitude   | BigDecimal | 用户经度                 |
| userSta            | Integer    | 用户状态（0-正常）       |
| createAt           | Date       | 创建时间                 |
| id                 | Long       | 数据库记录ID             |

**排序规则：**  
返回结果按距离升序排列（最近的用户排在最前）

### 5.2 示例响应

```json
[
  {
    "userId": "U456",
    "userName": "john_doe",
    "password": "encrypted_pwd",
    "telephone": "13800138000",
    "realName": "张三",
    "avatarUrl": "https://example.com/avatar.jpg",
    "userLocLatitude": 31.230416,
    "userLocLongitude": 121.473701,
    "userSta": 0,
    "createAt": "2024-01-01T00:00:00.000Z",
    "id": 123
  }
]
```

---

## 6. 错误处理

| 错误码 | 状态码 | 说明                   |
|--------|--------|------------------------|
| 50001  | 500    | 数据库位置更新失败     |
| 50002  | 500    | 附近用户查询失败       |
| 400    | 400    | 参数格式错误           |

---

## 7. 其他说明

1. **跨域支持**  
   允许来自 `http://localhost:8079` 的跨域请求

2. **坐标过滤优化**  
   采用二级过滤策略：
    - 一级：使用经纬度包围盒快速筛选候选集
    - 二级：精确计算Haversine距离验证

3. **距离计算**  
   使用WGS84地球模型（地球半径=6371km）
```
