<template>
  <div class="admin-product-detail">
    <!-- 顶部导航 -->
    <header class="detail-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
          ← 返回
        </button>
        <h1 class="page-title">商品详情管理</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <div class="detail-container" v-if="product">
      <!-- 左侧：商品图片 -->
      <div class="product-images">
        <div class="main-image">
          <img :src="currentImage" :alt="product.name" class="main-img" />
        </div>
        <div class="thumbnail-list">
          <div 
            v-for="(image, index) in product.images" 
            :key="index"
            class="thumbnail"
            :class="{ active: currentImageIndex === index }"
            @click="selectImage(index)"
          >
            <img :src="image" :alt="`商品图片${index + 1}`" />
          </div>
        </div>
      </div>

      <!-- 右侧：商品信息 -->
      <div class="product-info">
        <!-- 价格和标题 -->
        <div class="price-section">
          <div class="price-main">
            <span class="currency">¥</span>
            <span class="price">{{ product.price }}</span>
          </div>
          <div class="price-original" v-if="product.originalPrice">
            原价：¥{{ product.originalPrice }}
          </div>
        </div>

        <h2 class="product-title">{{ product.name }}</h2>
        
        <!-- 商品状态 -->
        <div class="product-status">
          <span 
            class="status-badge" 
            :class="{
              'status-pending': product.status === 'pending',
              'status-approved': product.status === 'approved',
              'status-rejected': product.status === 'rejected'
            }"
          >
            {{ 
              product.status === 'pending' ? '待审核' : 
              product.status === 'approved' ? '已上架' : '已下架' 
            }}
          </span>
        </div>
        
        <!-- 商品描述 -->
        <div class="product-description">
          <p>{{ product.description }}</p>
        </div>

        <!-- 商品详细信息 -->
        <div class="product-details">
          <div class="detail-item">
            <span class="label">商品ID：</span>
            <span class="value">{{ product.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">成色：</span>
            <span class="value condition">{{ product.condition }}</span>
          </div>
          <div class="detail-item">
            <span class="label">品牌：</span>
            <span class="value">{{ product.brand }}</span>
          </div>
          <div class="detail-item">
            <span class="label">交易地点：</span>
            <span class="value location">📍 {{ product.location }}</span>
          </div>
          <div class="detail-item">
            <span class="label">发布时间：</span>
            <span class="value">{{ product.publishTime }}</span>
          </div>
          <div class="detail-item">
            <span class="label">浏览次数：</span>
            <span class="value">{{ product.viewCount }} 次</span>
          </div>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section">
          <div class="seller-header">
            <h3>卖家信息</h3>
          </div>
          <div class="seller-info">
            <img :src="product.sellerAvatar || product.seller?.avatar" class="seller-avatar" />
            <div class="seller-details">
              <div class="seller-name">{{ product.sellerName || product.seller?.name }}</div>
              <div class="seller-school">{{ product.sellerSchool || product.seller?.school }}</div>
              <div class="seller-contact" v-if="product.sellerContact">
                联系方式：{{ product.sellerContact }}
              </div>
              <div class="seller-stats" v-if="product.seller">
                <span>信用评分：{{ product.seller.creditScore }}</span>
                <span>成交：{{ product.seller.dealCount }}笔</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详细描述 -->
    <div class="description-section" v-if="product">
      <h3 class="section-title">商品详情</h3>
      <div class="description-content">
        <p v-for="(paragraph, index) in product.detailDescription" :key="index">
          {{ paragraph }}
        </p>
      </div>
    </div>

    <!-- 审核记录 -->
    <div class="audit-card" v-if="product && product.auditHistory && product.auditHistory.length > 0">
      <h3 class="section-title">审核记录</h3>
      <div class="audit-list">
        <div class="audit-item" v-for="audit in product.auditHistory" :key="audit.id">
          <div class="audit-time">{{ audit.time }}</div>
          <div class="audit-action">{{ audit.action }}</div>
          <div class="audit-operator">操作人: {{ audit.operator }}</div>
          <div class="audit-reason" v-if="audit.reason">原因: {{ audit.reason }}</div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 - 管理员审核按钮 -->
    <div class="bottom-action-bar" v-if="product">
      <div class="action-buttons">
        <button 
          v-if="product.status === 'pending' || product.status === 'rejected'"
          class="action-btn approve-btn" 
          @click="approveProduct"
        >
          上架商品
        </button>
        <button 
          v-if="product.status === 'approved' || product.status === 'pending'" 
          class="action-btn reject-btn" 
          @click="rejectProduct"
        >
          下架商品
        </button>
        <!-- 已删除联系卖家按钮 -->
      </div>
    </div>

    <!-- 加载中状态 -->
    <div class="loading-state" v-if="!product">
      <div class="loading-spinner"></div>
      <p>加载商品信息中...</p>
    </div>
  </div>
</template>

<script>
import { commodityService } from '../api/commodityService'
import { productService } from '../api/ProductService'

export default {
  name: 'AdminProductDetail',
  data() {
    return {
      product: null,
      currentImageIndex: 0
    }
  },
  computed: {
    currentImage() {
      return this.product?.images?.[this.currentImageIndex] || this.product?.image
    }
  },
  created() {
    this.fetchProductData()
  },
  methods: {
    async fetchProductData() {
      const productId = this.$route.params.id
      
      try {
        const result = await productService.getCommodityDetail(productId)
        if (result.success && result.data.code === 200) {
          const commodityData = result.data.data
          
          // 安全处理图片列表
          let imageList = []
          try {
            imageList = commodityData.imageList ? JSON.parse(commodityData.imageList) : []
          } catch (e) {
            console.warn('解析图片列表失败:', e)
            imageList = []
          }
          
          // 确保至少有一张图片
          if (imageList.length === 0 && commodityData.mainImageUrl) {
            imageList = [commodityData.mainImageUrl]
          }
          
          this.product = {
            id: commodityData.commodityId,
            name: commodityData.commodityName,
            description: commodityData.commodityDescription,
            price: commodityData.currentPrice,
            status: this.mapCommodityStatus(commodityData.commodityStatus),
            condition: commodityData.newness,
            category: commodityData.categoryName,
            sellerId: commodityData.sellerId,
            image: commodityData.mainImageUrl || imageList[0] || '/default-product.png',
            images: imageList,
            publishTime: new Date(commodityData.createdAt).toLocaleString(),
            viewCount: 0,
            quantity: commodityData.quantity,
            sellerName: '卖家信息',
            sellerAvatar: '/default-avatar.png',
            sellerSchool: '未知学校',
            detailDescription: [commodityData.commodityDescription]
          }
        } else {
          console.error('获取商品详情失败:', result.message)
          alert('获取商品详情失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('获取商品详情出错:', error)
        alert('获取商品详情失败，请检查网络连接')
      }
    },
    
    // 映射商品状态
    mapCommodityStatus(status) {
      const statusMap = {
        'to_sale': 'pending',
        'on_sale': 'approved', 
        'sold': 'sold',
        'off_sale': 'rejected'
      }
      return statusMap[status] || 'pending'
    },
    
    goBack() {
      // 修正路径为正确的 AdminDashboard 路由
      this.$router.push({ 
        path: '/AdminDashboard',
        query: { activeMenu: 'products' }
      })
    },
    
    selectImage(index) {
      this.currentImageIndex = index
    },
    
    async approveProduct() {
      console.log('=== 开始审核通过操作 ===');
      console.log('商品ID:', this.product.id);
      
      if (confirm('确定要通过审核并上架这个商品吗？')) {
        try {
          console.log('准备调用 updateCommodityStatus，参数:', {
            productId: this.product.id,
            newStatus: 'on_sale'  // 修正：使用正确的状态值
          });
          
          const result = await commodityService.updateCommodityStatus(this.product.id, 'on_sale');
          console.log('API调用成功，返回结果:', result);
          
          this.product.status = 'approved'; // 前端显示状态保持不变
          // 添加审核记录
          if (!this.product.auditHistory) {
            this.product.auditHistory = [];
          }
          this.product.auditHistory.push({
            id: this.product.auditHistory.length + 1,
            time: new Date().toLocaleString(),
            action: '审核通过',
            operator: '管理员',
            reason: '商品审核通过，允许上架'
          });
          alert('商品审核通过，已上架！');
          
        } catch (error) {
          console.error('=== 审核通过失败 ===');
          console.error('错误详情:', error);
          console.error('错误响应:', error.response);
          console.error('错误状态码:', error.response?.status);
          console.error('错误数据:', error.response?.data);
          
          alert(`操作失败：${error.response?.data?.message || error.message || '请稍后重试'}`);
        }
      }
    },
    
    async rejectProduct() {
      console.log('=== 开始下架操作 ===');
      console.log('商品ID:', this.product.id);
      
      if (confirm('确定要下架这个商品吗？')) {
        try {
          console.log('准备调用 updateCommodityStatus，参数:', {
            productId: this.product.id,
            newStatus: 'off_sale'
          });
          
          const result = await commodityService.updateCommodityStatus(this.product.id, 'off_sale');
          console.log('API调用成功，返回结果:', result);
          
          this.product.status = 'rejected'; // 前端显示状态保持不变
          // 添加审核记录
          if (!this.product.auditHistory) {
            this.product.auditHistory = [];
          }
          this.product.auditHistory.push({
            id: this.product.auditHistory.length + 1,
            time: new Date().toLocaleString(),
            action: '审核拒绝',
            operator: '管理员',
            reason: '不符合平台规范'
          });
          alert('商品已下架！');
          
        } catch (error) {
          console.error('=== 下架失败 ===');
          console.error('错误详情:', error);
          console.error('错误响应:', error.response);
          console.error('错误状态码:', error.response?.status);
          console.error('错误数据:', error.response?.data);
          
          alert(`操作失败：${error.response?.data?.message || error.message || '请稍后重试'}`);
        }
      }
    },
    
    // 删除了contactSeller方法
  }
}
</script>

<style scoped>
.admin-product-detail {
  background-color: #f8f9fa;
  min-height: 100vh;
  padding-bottom: 80px;
}

.detail-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  color: white;
}

.header-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.header-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.placeholder {
  width: 60px;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  gap: 30px;
}

.product-images {
  flex: 0 0 400px;
}

.main-image {
  margin-bottom: 15px;
}

.main-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s;
}

.thumbnail.active {
  border-color: #667eea;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.price-section {
  margin-bottom: 20px;
}

.price-main {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.currency {
  font-size: 24px;
  color: #ff4757;
  font-weight: 600;
  margin-right: 5px;
}

.price {
  font-size: 36px;
  color: #ff4757;
  font-weight: bold;
}

.price-original {
  color: #a4b0be;
  text-decoration: line-through;
  font-size: 16px;
}

.product-title {
  font-size: 28px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 20px;
  line-height: 1.3;
}

.product-status {
  margin-bottom: 20px;
}

.status-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  background-color: #ffeaa7;
  color: #d63031;
}

.status-approved {
  background-color: #d1f2eb;
  color: #00b894;
}

.status-rejected {
  background-color: #fab1a0;
  color: #e17055;
}

.product-description {
  margin-bottom: 25px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.product-description p {
  color: #57606f;
  line-height: 1.6;
  margin: 0;
  font-size: 16px;
}

.product-details {
  margin-bottom: 30px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f1f2f6;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  color: #747d8c;
  font-weight: 500;
  font-size: 14px;
}

.value {
  color: #2f3542;
  font-weight: 600;
  font-size: 14px;
}

.condition {
  background-color: #00b894;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.location {
  color: #5352ed;
}

.seller-section {
  border-top: 2px solid #f1f2f6;
  padding-top: 25px;
}

.seller-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.seller-header h3 {
  margin: 0;
  color: #2f3542;
  font-size: 18px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.seller-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 18px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 5px;
}

.seller-school,
.seller-contact {
  color: #747d8c;
  font-size: 14px;
  margin-bottom: 3px;
}

.seller-stats {
  display: flex;
  gap: 15px;
  margin-top: 8px;
}

.seller-stats span {
  color: #5352ed;
  font-size: 13px;
  font-weight: 500;
}

.description-section,
.audit-card {
  max-width: 1200px;
  margin: 20px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f1f2f6;
}

.description-content p {
  color: #57606f;
  line-height: 1.8;
  margin-bottom: 15px;
  font-size: 16px;
}

.audit-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.audit-item {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #5352ed;
}

.audit-time {
  font-size: 12px;
  color: #747d8c;
  margin-bottom: 5px;
}

.audit-action {
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 5px;
}

.audit-operator,
.audit-reason {
  font-size: 14px;
  color: #57606f;
  margin-bottom: 3px;
}

.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  padding: 15px 20px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  z-index: 99;
}

.action-buttons {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 15px;
}

.action-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.approve-btn {
  background-color: #00b894;
  color: white;
}

.approve-btn:hover {
  background-color: #00a085;
}

.reject-btn {
  background-color: #e17055;
  color: white;
}

.reject-btn:hover {
  background-color: #d63031;
}

.contact-btn {
  background-color: #5352ed;
  color: white;
}

.contact-btn:hover {
  background-color: #4742d8;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 50vh;
  color: #57606f;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #5352ed;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .detail-container {
    flex-direction: column;
    gap: 20px;
  }
  
  .product-images {
    flex: none;
  }
  
  .main-img {
    height: 300px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>