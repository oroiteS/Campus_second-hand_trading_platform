# 商品管理API使用说明

## 创建并上架商品API（支持多图片上传）

### 接口信息
- **URL**: `/api/commodity/create-and-put-on-sale`
- **方法**: POST
- **Content-Type**: `multipart/form-data`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| commodityName | String | 是 | 商品名称 |
| commodityDescription | String | 是 | 商品描述 |
| categoryId | Integer | 是 | 分类ID |
| tagsId | String | 是 | 标签ID（逗号分隔） |
| currentPrice | BigDecimal | 是 | 当前价格 |
| quantity | Integer | 是 | 商品数量 |
| sellerId | String | 是 | 卖家ID |
| newness | String | 是 | 新旧度 |
| images | MultipartFile[] | 是 | 商品图片文件数组 |

### 前端请求示例

#### JavaScript (使用FormData)

```javascript
const formData = new FormData();

// 添加文本参数
formData.append('commodityName', '商品名称');
formData.append('commodityDescription', '商品描述');
formData.append('categoryId', '1');
formData.append('tagsId', '1,2,3');
formData.append('currentPrice', '99.99');
formData.append('quantity', '10');
formData.append('sellerId', 'seller123');
formData.append('newness', '九成新');

// 添加图片文件
const fileInput = document.getElementById('imageFiles');
for (let i = 0; i < fileInput.files.length; i++) {
    formData.append('images', fileInput.files[i]);
}

// 发送请求
fetch('/api/commodity/create-and-put-on-sale', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => {
    console.log('商品创建成功:', data);
})
.catch(error => {
    console.error('创建失败:', error);
});
```

#### HTML表单示例

```html
<form action="/api/commodity/create-and-put-on-sale" method="post" enctype="multipart/form-data">
    <input type="text" name="commodityName" placeholder="商品名称" required>
    <textarea name="commodityDescription" placeholder="商品描述" required></textarea>
    <input type="number" name="categoryId" placeholder="分类ID" required>
    <input type="text" name="tagsId" placeholder="标签ID（逗号分隔）" required>
    <input type="number" step="0.01" name="currentPrice" placeholder="价格" required>
    <input type="number" name="quantity" placeholder="数量" required>
    <input type="text" name="sellerId" placeholder="卖家ID" required>
    <input type="text" name="newness" placeholder="新旧度" required>
    <input type="file" name="images" multiple accept="image/*" required>
    <button type="submit">创建商品</button>
</form>
```

### 响应格式

#### 成功响应
```json
{
    "success": true,
    "message": "商品创建并上架成功",
    "data": {
        "commodityId": "uuid-string",
        "commodityName": "商品名称",
        "commodityDescription": "商品描述",
        "categoryId": 1,
        "tagsId": "[\"1\",\"2\",\"3\"]",
        "currentPrice": 99.99,
        "mainImageUrl": "https://oss-url/commodity/sellerId_timestamp_random.jpg",
        "imageList": "[\"https://oss-url/image1.jpg\",\"https://oss-url/image2.jpg\"]",
        "quantity": 10,
        "sellerId": "seller123",
        "newness": "九成新",
        "commodityStatus": "ON_SALE",
        "createdAt": "2024-01-01T12:00:00",
        "updatedAt": "2024-01-01T12:00:00"
    }
}
```

#### 失败响应
```json
{
    "success": false,
    "message": "错误信息",
    "data": null
}
```

### 图片处理说明

1. **支持的图片格式**: JPG, PNG, GIF, WEBP
2. **文件大小限制**: 每个文件最大5MB
3. **图片存储**: 自动上传到阿里云OSS
4. **主图设置**: 第一张上传的图片自动设为主图
5. **图片URL**: 所有图片URL以JSON数组格式存储在`imageList`字段
6. **文件命名**: `commodity/用户ID_时间戳_随机字符串.扩展名`

### 注意事项

1. 请求必须使用`multipart/form-data`格式
2. 至少需要上传一张图片
3. 图片会自动上传到OSS并生成公共访问URL
4. 所有图片URL都会保存到数据库的`imageList`字段
5. 第一张图片的URL同时保存到`mainImageUrl`字段作为主图