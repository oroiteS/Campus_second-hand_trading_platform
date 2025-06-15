<template>
  <div class="admin-product-detail">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="back-button" @click="goBack">
          <span class="back-icon">←</span> 返回
        </div>
        <h1 class="page-title">商品详情管理</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <div class="detail-container" v-if="product">
      <!-- 商品基本信息 -->
      <div class="product-info-card">
        <div class="product-header">
          <div class="product-image">
            <img :src="product.image" :alt="product.name" class="main-image" />
          </div>
          <div class="product-basic-info">
            <h2 class="product-title">{{ product.name }}</h2>
            <div class="product-price-section">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
            </div>
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
          </div>
        </div>
      </div>

      <!-- 商品详细信息 -->
      <div class="product-details-card">
        <h3 class="section-title">商品信息</h3>
        <div class="details-grid">
          <div class="detail-item">
            <span class="detail-label">商品ID:</span>
            <span class="detail-value">{{ product.id }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">商品状态:</span>
            <span class="detail-value">{{ product.condition }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">发布时间:</span>
            <span class="detail-value">{{ product.publishTime }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">所在位置:</span>
            <span class="detail-value">{{ product.location }}</span>
          </div>
        </div>
      </div>

      <!-- 卖家信息 -->
      <div class="seller-card">
        <h3 class="section-title">卖家信息</h3>
        <div class="seller-info">
          <img :src="product.sellerAvatar" class="seller-avatar" />
          <div class="seller-details">
            <div class="seller-name">{{ product.sellerName }}</div>
            <div class="seller-school">{{ product.sellerSchool }}</div>
            <div class="seller-contact">联系方式: {{ product.sellerContact || '未提供' }}</div>
          </div>
        </div>
      </div>

      <!-- 商品描述 -->
      <div class="description-card">
        <h3 class="section-title">商品描述</h3>
        <p class="description-text">
          {{ product.description || '暂无描述' }}
        </p>
      </div>

      <!-- 审核记录 -->
      <div class="audit-card" v-if="product.auditHistory && product.auditHistory.length > 0">
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
    </div>

    <!-- 底部操作栏 -->
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
        <button class="action-btn contact-btn" @click="contactSeller">
          联系卖家
        </button>
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
import productService from '../api/productService'

export default {
  name: 'AdminProductDetail',
  data() {
    return {
      product: null
    }
  },
  created() {
    this.fetchProductData()
  },
  methods: {
    fetchProductData() {
      const productId = parseInt(this.$route.params.id)
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 模拟数据库中的商品数据
        const productsDatabase = [
          {
            id: 1,
            name: 'iPhone 13 Pro 128G',
            price: 4999,
            originalPrice: 6999,
            condition: '9成新',
            location: '东校区',
            image: 'https://via.placeholder.com/400x300/F0F0F0/666666?text=iPhone',
            status: 'pending',
            sellerName: '张同学',
            sellerSchool: '计算机学院',
            sellerAvatar: 'https://via.placeholder.com/50x50/4CAF50/FFFFFF?text=张',
            sellerContact: '13800138000',
            publishTime: '2023-12-20 10:30',
            description: 'iPhone 13 Pro 128G，购买时间不到半年，无划痕，电池健康度95%，配件齐全，因为换了新手机所以出售。',
            auditHistory: [
              {
                id: 1,
                time: '2023-12-20 10:35',
                action: '提交审核',
                operator: '系统',
                reason: '用户提交商品审核'
              }
            ]
          },
          {
            id: 2,
            name: '高等数学教材（第七版）',
            price: 25,
            originalPrice: 45,
            condition: '8成新',
            location: '西校区',
            image: 'https://via.placeholder.com/400x300/F0F0F0/666666?text=教材',
            status: 'approved',
            sellerName: '李同学',
            sellerSchool: '数学学院',
            sellerAvatar: 'https://via.placeholder.com/50x50/2196F3/FFFFFF?text=李',
            sellerContact: '13900139000',
            publishTime: '2023-12-19 14:20',
            description: '高等数学第七版，只用了一个学期，里面有少量笔记，对学习很有帮助。',
            auditHistory: [
              {
                id: 1,
                time: '2023-12-19 14:25',
                action: '提交审核',
                operator: '系统',
                reason: '用户提交商品审核'
              },
              {
                id: 2,
                time: '2023-12-19 15:10',
                action: '审核通过',
                operator: '管理员',
                reason: '商品信息完整，符合平台规范'
              }
            ]
          }
        ]
        
        // 查找对应ID的商品
        this.product = productsDatabase.find(p => p.id === productId) || productsDatabase[0]
      }, 500)
    },
    
    goBack() {
      this.$router.go(-1)
    },
    
    approveProduct() {
      if (confirm('确定要上架这个商品吗？')) {
        productService.updateProductStatus(this.product.id, { status: 'approved' })
          .then(() => {
            this.product.status = 'approved'
            // 添加审核记录
            if (!this.product.auditHistory) {
              this.product.auditHistory = []
            }
            this.product.auditHistory.push({
              id: this.product.auditHistory.length + 1,
              time: new Date().toLocaleString(),
              action: '审核通过',
              operator: '管理员',
              reason: '商品审核通过，允许上架'
            })
            alert('商品已成功上架')
          })
          .catch(error => {
            console.error('更新商品状态失败:', error)
            alert('操作失败，请重试')
          })
      }
    },
    
    rejectProduct() {
      const reason = prompt('请输入下架原因:')
      if (reason !== null) {
        productService.updateProductStatus(this.product.id, { status: 'rejected', reason })
          .then(() => {
            this.product.status = 'rejected'
            // 添加审核记录
            if (!this.product.auditHistory) {
              this.product.auditHistory = []
            }
            this.product.auditHistory.push({
              id: this.product.auditHistory.length + 1,
              time: new Date().toLocaleString(),
              action: '审核拒绝',
              operator: '管理员',
              reason: reason || '不符合平台规范'
            })
            alert('商品已下架')
          })
          .catch(error => {
            console.error('更新商品状态失败:', error)
            alert('操作失败，请重试')
          })
      }
    },
    
    contactSeller() {
      alert(`联系卖家: ${this.product.sellerContact || '联系方式未提供'}`)
    }
  }
}
</script>

<style scoped>
.admin-product-detail {
  background-color: #f8f9fa;
  min-height: 100vh;
  padding-bottom: 80px;
}

.header {
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

.back-button {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-weight: 500;
}

.back-icon {
  margin-right: 5px;
  font-size: 18px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}

.placeholder {
  width: 60px;
}

.detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.product-info-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.product-header {
  display: flex;
  gap: 20px;
}

.product-image {
  flex-shrink: 0;
}

.main-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.product-basic-info {
  flex-grow: 1;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 15px;
}

.product-price-section {
  margin-bottom: 15px;
}

.product-price {
  font-size: 28px;
  font-weight: bold;
  color: #ff4757;
}

.product-original-price {
  font-size: 18px;
  color: #a4b0be;
  text-decoration: line-through;
  margin-left: 15px;
}

.product-status {
  margin-top: 10px;
}

.status-badge {
  padding: 6px 12px;
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

.product-details-card,
.seller-card,
.description-card,
.audit-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f1f2f6;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f1f2f6;
}

.detail-label {
  color: #747d8c;
  font-weight: 500;
}

.detail-value {
  color: #2f3542;
  font-weight: 600;
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
}

.seller-details {
  flex-grow: 1;
}

.seller-name {
  font-size: 18px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 5px;
}

.seller-school,
.seller-contact {
  font-size: 14px;
  color: #747d8c;
  margin-bottom: 3px;
}

.description-text {
  color: #57606f;
  line-height: 1.8;
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
  max-width: 1000px;
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
  .product-header {
    flex-direction: column;
  }
  
  .main-image {
    width: 100%;
    height: 250px;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>