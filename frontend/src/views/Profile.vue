<template>
  <div class="profile-page">
    <!-- 顶部导航 -->
    <header class="profile-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
            返回
        </button>
        <h1 class="page-title">个人资料</h1>
        <button class="header-btn edit-btn">编辑资料</button>
      </div>
    </header>

    <div class="profile-container">
      <!-- 用户基本信息 -->
      <div class="user-card">
        <div class="user-avatar-section">
          <img :src="userInfo.avatar" alt="用户头像" class="large-avatar" />
          <div class="avatar-badge">{{ userInfo.badge }}</div>
        </div>
        <div class="user-basic-info">
          <h2 class="username">{{ userInfo.username }}</h2>
          <p class="user-school">{{ userInfo.school }}</p>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-number">{{ userInfo.creditScore }}</span>
              <span class="stat-label">信用评分</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ userInfo.dealCount }}</span>
              <span class="stat-label">成功交易</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ userInfo.favoriteCount }}</span>
              <span class="stat-label">收藏商品</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="menu-section">
        <h3 class="section-title">我的功能</h3>
        <div class="menu-grid">
          <div class="menu-item" v-for="item in menuItems" :key="item.id" @click="handleMenuClick(item.action)">
            <div class="menu-icon" :style="{backgroundColor: item.color}">
              {{ item.icon }}
            </div>
            <span class="menu-name">{{ item.name }}</span>
            <span class="menu-arrow">→</span>
          </div>
        </div>
      </div>

      <!-- 我的商品 -->
      <div class="products-section">
        <div class="section-header">
          <h3 class="section-title">我的商品</h3>
          <div class="tab-buttons">
            <button 
              v-for="tab in productTabs" 
              :key="tab.id"
              class="tab-btn"
              :class="{active: activeTab === tab.id}"
              @click="activeTab = tab.id"
            >
              {{ tab.name }}
            </button>
          </div>
        </div>
        
        <div class="products-grid" v-if="currentProducts.length > 0">
          <div class="product-card" v-for="product in currentProducts" :key="product.id">
            <img :src="product.image" :alt="product.name" class="product-image" />
            <div class="product-info">
              <h4 class="product-title">{{ product.name }}</h4>
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-status">{{ product.status }}</p>
            </div>
          </div>
        </div>
        
        <div class="empty-state" v-else>
          <div class="empty-icon">📦</div>
          <p class="empty-text">暂无商品</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProfilePage',
  data() {
    return {
      activeTab: 'selling',
      userInfo: {
        username: 'xy21675070351',
        school: '计算机学院',
        avatar: '/测试图片.jpg',
        badge: '认证用户',
        creditScore: '98%',
        dealCount: 47,
        favoriteCount: 23
      },
      menuItems: [
        { id: 1, name: '我的收藏', icon: '❤️', color: '#FF6B6B', action: 'favorites' },
        { id: 2, name: '交易记录', icon: '📋', color: '#4ECDC4', action: 'orders' },
        { id: 3, name: '账号设置', icon: '⚙️', color: '#45B7D1', action: 'settings' },
        { id: 4, name: '安全中心', icon: '🔒', color: '#96CEB4', action: 'security' },
        { id: 5, name: '帮助中心', icon: '❓', color: '#FFEAA7', action: 'help' },
        { id: 6, name: '意见反馈', icon: '💬', color: '#DDA0DD', action: 'feedback' }
      ],
      productTabs: [
        { id: 'selling', name: '在售' },
        { id: 'sold', name: '已售' },
        { id: 'draft', name: '草稿' }
      ],
      products: {
        selling: [
          {
            id: 1,
            name: 'iPhone 13 Pro',
            price: 4999,
            image: 'https://via.placeholder.com/150x150/FF6B35/FFFFFF?text=手机',
            status: '在售中'
          }
        ],
        sold: [
          {
            id: 2,
            name: '高等数学教材',
            price: 25,
            image: 'https://via.placeholder.com/150x150/4CAF50/FFFFFF?text=教材',
            status: '已售出'
          }
        ],
        draft: []
      }
    }
  },
  computed: {
    currentProducts() {
      return this.products[this.activeTab] || []
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    handleMenuClick(action) {
      console.log('点击菜单:', action)
      // 根据不同的action跳转到不同的页面
      switch(action) {
        case 'favorites':
          this.$router.push('/favorites')
          break
        case 'orders':
          // 可以添加交易记录页面
          console.log('跳转到交易记录')
          break
        case 'settings':
          // 跳转到UserProfile.vue页面
          this.$router.push('/userprofile')
          break
        case 'security':
          // 可以添加安全中心页面
          console.log('跳转到安全中心')
          break
        case 'help':
          // 可以添加帮助中心页面
          console.log('跳转到帮助中心')
          break
        case 'feedback':
          // 可以添加意见反馈页面
          console.log('跳转到意见反馈')
          break
        default:
          console.log('未知操作:', action)
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 顶部导航 */
.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  padding: 15px 20px;
}

.header-btn {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  font-size: 14px;
  cursor: pointer;
  padding: 10px 20px;
  border-radius: 20px;
  transition: all 0.3s;
  font-weight: 500;
}

.header-btn:hover {
  background: rgba(255,255,255,0.3);
  transform: translateY(-1px);
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: white;
  margin: 0;
}

/* 主要内容 */
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 用户卡片 */
.user-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 30px;
}

.user-avatar-section {
  position: relative;
}

.large-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #e0e0e0;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.avatar-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #4CAF50;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
}

.user-basic-info {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.user-school {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

/* 功能菜单 */
.menu-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 3px solid #667eea;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.menu-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.menu-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

.menu-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.menu-name {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.menu-arrow {
  color: #999;
  font-size: 18px;
}

/* 商品区域 */
.products-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tab-buttons {
  display: flex;
  gap: 10px;
}

.tab-btn {
  padding: 8px 20px;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.tab-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.tab-btn:hover {
  border-color: #667eea;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #FF6B35;
  margin-bottom: 5px;
}

.product-status {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.empty-text {
  font-size: 16px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .user-stats {
    justify-content: center;
    gap: 20px;
  }
  
  .menu-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>