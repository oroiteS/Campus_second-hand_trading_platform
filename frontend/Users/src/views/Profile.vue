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
        { id: 2, name: '订单管理', icon: '📋', color: '#4ECDC4', action: 'orders' },
        { id: 3, name: '个人信息', icon: '⚙️', color: '#45B7D1', action: 'settings' },
        { id: 4, name: '安全中心', icon: '🔒', color: '#96CEB4', action: 'security' },
        { id: 5, name: '附近的人', icon: '❓', color: '#FFEAA7', action: 'help' },
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
          console.log('跳转到订单管理')
          break
        case 'settings':
          // 跳转到UserProfile.vue页面
          console.log('跳转到个人信息')
          this.$router.push('/userprofile')
          break
        case 'security':
          // 可以添加安全中心页面
          console.log('跳转到安全中心')
          break
        case 'help':
          // 可以添加帮助中心页面
          console.log('跳转到附近的人')
          this.$router.push('/nearbyusers')
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
@import '../styles/Profile.css';
</style>