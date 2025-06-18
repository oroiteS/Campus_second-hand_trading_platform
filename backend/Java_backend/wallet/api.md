# 钱包模块 API 文档

## 基础信息

- **基础路径**: `/user/account`
- **认证方式**: Spring Security (需要用户登录)
- **内容类型**: `application/json`
- **运行端口**: `8081`

## 通用响应格式

所有接口都返回统一的响应格式：

```json
{
  "success": true,
  "message": "操作成功",
  "code": 200,
  "data": null
}
```

### 状态码说明

- `200`: 操作成功
- `400`: 请求参数错误（如余额不足、金额无效等）
- `404`: 资源不存在（如用户不存在、订单不存在）
- `500`: 服务器内部错误（如数据库更新失败）

## API 接口

### 1. 确认收货

**接口地址**: `POST /user/account/confirmReceipt`

**功能描述**: 买家确认收货，将订单金额转入卖家账户

**请求参数**:
```json
{
  "userId": "string",
  "orderID": "string"
}
```

**参数说明**:
- `userId`: 用户ID（必填）
- `orderID`: 订单ID（必填）

**成功响应**:
```json
{
  "success": true,
  "message": "确认收货成功",
  "code": 200
}
```

**错误响应**:
- `404`: 订单不存在 / 用户不存在
- `400`: 订单状态错误
- `500`: 更新账户余额失败 / 更新订单状态失败

---

### 2. 提现

**接口地址**: `POST /user/account/withdraw`

**功能描述**: 用户从账户余额中提现

**请求参数**:
```json
{
  "userId": "string",
  "amount": "decimal"
}
```

**参数说明**:
- `userId`: 用户ID（必填）
- `amount`: 提现金额（必填，必须大于0）

**成功响应**:
```json
{
  "success": true,
  "message": "提现成功",
  "code": 200
}
```

**错误响应**:
- `404`: 用户不存在或账户余额不足
- `400`: 提现金额必须大于0 / 余额不足
- `500`: 更新账户余额失败

---

### 3. 充值

**接口地址**: `POST /user/account/recharge`

**功能描述**: 用户向账户充值

**请求参数**:
```json
{
  "userId": "string",
  "amount": "decimal"
}
```

**参数说明**:
- `userId`: 用户ID（必填）
- `amount`: 充值金额（必填，必须大于0）

**成功响应**:
```json
{
  "success": true,
  "message": "充值成功",
  "code": 200
}
```

**错误响应**:
- `404`: 用户不存在
- `400`: 充值金额必须大于0
- `500`: 更新账户余额失败

---

### 4. 卖家退款

**接口地址**: `POST /user/account/sellerRefund`

**功能描述**: 卖家主动退款给买家

**请求参数**:
```json
{
  "userId": "string",
  "orderID": "string"
}
```

**参数说明**:
- `userId`: 用户ID（必填）
- `orderID`: 订单ID（必填）

**成功响应**:
```json
{
  "success": true,
  "message": "退款成功",
  "code": 200
}
```

**错误响应**:
- `404`: 订单不存在 / 用户不存在
- `500`: 更新账户余额失败

---

### 5. 支付

**接口地址**: `POST /user/account/pay`

**功能描述**: 用户支付订单

**请求参数**:
```json
{
  "userId": "string",
  "orderID": "string"
}
```

**参数说明**:
- `userId`: 用户ID（必填）
- `orderID`: 订单ID（必填）

**成功响应**:
```json
{
  "success": true,
  "message": "支付成功",
  "code": 200
}
```

**错误响应**:
- `404`: 订单不存在 / 用户不存在
- `400`: 余额不足
- `500`: 更新账户余额失败

## 数据模型

### 请求对象

#### ConfirmReceiptRequest (确认收货请求)
```json
{
  "userId": "string",
  "orderID": "string"
}
```

#### WithdrawRequest (提现请求)
```json
{
  "userId": "string",
  "amount": "decimal"
}
```

#### RechargeRequest (充值请求)
```json
{
  "userId": "string",
  "amount": "decimal"
}
```

#### SellerRefundRequest (卖家退款请求)
```json
{
  "userId": "string",
  "orderID": "string"
}
```

#### PayRequest (支付请求)
```json
{
  "userId": "string",
  "orderID": "string"
}
```

## 注意事项

1. 所有接口都需要用户登录认证
2. 请求体中的用户ID和订单ID都是必填参数
3. 金额字段使用BigDecimal类型，支持高精度计算
4. 所有操作都有事务保护，确保数据一致性
5. 错误信息会以中文形式返回，便于前端展示
6. 响应格式统一为包含success、message、code字段的JSON对象

## 业务流程说明

### 支付流程
1. 用户创建订单后，调用支付接口
2. 系统检查用户账户余额是否充足
3. 余额充足则扣除相应金额，订单状态更新为已支付
4. 余额不足则返回错误信息

### 确认收货流程
1. 买家收到商品后，调用确认收货接口
2. 系统将订单金额转入卖家账户
3. 订单状态更新为已完成

### 退款流程
1. 卖家主动发起退款或买家申请退款
2. 系统将订单金额退回买家账户
3. 订单状态更新为已退款

### 充值提现流程
1. 充值：用户向平台账户充值，增加账户余额
2. 提现：用户从平台账户提现，减少账户余额
```
        