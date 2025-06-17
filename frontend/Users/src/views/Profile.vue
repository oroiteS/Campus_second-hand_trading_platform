<template>
  <div class="profile-page">
    <!-- 顶部导航 -->
    <header class="profile-header">
      <div class="profile-header-content">
        <button @click="goBack" class="profile-header-btn back-btn">
            返回
        </button>
        <h1 class="profile-page-title">个人资料</h1>
        <div class="header-placeholder"></div>
      </div>
    </header>

    <div class="profile-container">
      <!-- 用户基本信息 -->
      <div class="profile-user-card">
        <div class="profile-user-avatar-section">
          <img :src="userInfo.avatar" alt="用户头像" class="profile-large-avatar" />
          <div class="profile-avatar-badge">{{ userInfo.badge }}</div>
        </div>
        <div class="profile-user-basic-info">
          <h2 class="profile-username">{{ userInfo.username }}</h2>
          <p class="profile-user-school">{{ userInfo.school }}</p>
          <div class="profile-user-stats">
            <div class="profile-stat-item">
              <span class="profile-stat-number">{{ userInfo.dealCount }}</span>
              <span class="profile-stat-label">成功交易</span>
            </div>
            <div class="profile-stat-item">
              <span class="profile-stat-number">{{ userInfo.favoriteCount }}</span>
              <span class="profile-stat-label">收藏商品</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="profile-menu-section">
        <h3 class="profile-section-title">我的功能</h3>
        <div class="profile-menu-grid">
          <div class="profile-menu-item" v-for="item in menuItems" :key="item.id" @click="handleMenuClick(item.action)">
            <div class="profile-menu-icon" :style="{backgroundColor: item.color}">
              {{ item.icon }}
            </div>
            <span class="profile-menu-name">{{ item.name }}</span>
            <span class="profile-menu-arrow">→</span>
          </div>
        </div>
      </div>

      <!-- 我的商品 -->
      <div class="profile-products-section">
        <div class="profile-section-header">
          <h3 class="profile-section-title">我的商品</h3>
          <div class="profile-tab-buttons">
            <button 
              v-for="tab in productTabs" 
              :key="tab.id"
              class="profile-tab-btn"
              :class="{active: activeTab === tab.id}"
              @click="activeTab = tab.id"
            >
              {{ tab.name }}
            </button>
          </div>
        </div>
        
        <div class="profile-products-grid" v-if="currentProducts.length > 0">
          <div class="profile-product-card" v-for="product in currentProducts" :key="product.id">
            <img :src="product.image" :alt="product.name" class="profile-product-image" @click="goToProductDetail(product.id)" />
            <div class="profile-product-info" @click="goToProductDetail(product.id)">
              <h4 class="profile-product-title">{{ product.name }}</h4>
              <p class="profile-product-price">¥{{ product.price }}</p>
              <p class="profile-product-status">{{ product.status }}</p>
            </div>
            <!-- 商品操作按钮 -->
            <div class="profile-product-actions">
              <div class="profile-status-buttons">
                <button 
                  class="profile-status-btn" 
                  :class="{ active: getProductStatus(product) === 'selling' }"
                  @click.stop="changeProductStatus(product, 'selling')"
                  title="设为在售"
                >
                  在售
                </button>
                <button 
                  class="profile-status-btn" 
                  :class="{ active: getProductStatus(product) === 'sold' }"
                  @click.stop="changeProductStatus(product, 'sold')"
                  title="设为已售"
                >
                  已售
                </button>
                <button 
                  class="profile-status-btn" 
                  :class="{ active: getProductStatus(product) === 'offline' }"
                  @click.stop="changeProductStatus(product, 'offline')"
                  title="设为已下架"
                >
                  下架
                </button>
              </div>
              <button 
                class="profile-delete-btn" 
                @click.stop="deleteProduct(product)"
                title="删除商品"
              >
                🗑️
              </button>
            </div>
          </div>
        </div>
        
        <div class="profile-empty-state" v-else>
          <div class="profile-empty-icon">📦</div>
          <p class="profile-empty-text">暂无商品</p>
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
        { id: 4, name: '钱包管理', icon: '💰', color: '#FFA726', action: 'wallet' },
        { id: 5, name: '附近的人', icon: '❓', color: '#FFEAA7', action: 'help' }
      ],
      productTabs: [
        { id: 'selling', name: '在售' },
        { id: 'sold', name: '已售' },
        { id: 'offline', name: '已下架' }
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
        offline: []
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
    // 跳转到商品详情页
    goToProductDetail(productId) {
      this.$router.push({
        path: `/product/${productId}`,
        query: {
          from: 'profile', // 添加来源标识
          editable: 'true' // 标识可编辑
        }
      })
    },
    handleMenuClick(action) {
      console.log('点击菜单:', action)
      // 根据不同的action跳转到不同的页面
      switch(action) {
        case 'favorites':
          this.$router.push('/favorites')
          break
        case 'orders':
          this.$router.push('/orders')
          break
        case 'settings':
          console.log('跳转到个人信息')
          this.$router.push('/userprofile')
          break
        case 'wallet':
          console.log('跳转到钱包管理')
          this.$router.push('/wallet')
          break
        case 'help':
          console.log('跳转到附近的人')
          this.$router.push('/nearbyusers')
          break
        default:
          console.log('未知操作:', action)
      }
    },
    // 获取商品当前状态
    getProductStatus(product) {
      if (this.products.selling.find(p => p.id === product.id)) return 'selling'
      if (this.products.sold.find(p => p.id === product.id)) return 'sold'
      if (this.products.offline.find(p => p.id === product.id)) return 'offline'
      return 'selling'
    },
    // 修改商品状态
    changeProductStatus(product, newStatus) {
      // 从所有状态数组中移除该商品
      this.removeProductFromAllArrays(product.id)
      
      // 更新商品状态文本
      const statusText = {
        'selling': '在售中',
        'sold': '已售出',
        'offline': '已下架'
      }
      product.status = statusText[newStatus]
      
      // 添加到新的状态数组
      this.products[newStatus].push(product)
      
      // 显示成功提示
      this.$message?.success(`商品状态已更新为：${statusText[newStatus]}`)
    },
    // 从所有状态数组中移除商品
    removeProductFromAllArrays(productId) {
      Object.keys(this.products).forEach(status => {
        this.products[status] = this.products[status].filter(p => p.id !== productId)
      })
    },
    // 删除商品
    deleteProduct(product) {
      if (confirm(`确定要删除商品"${product.name}"吗？此操作不可撤销。`)) {
        this.removeProductFromAllArrays(product.id)
        this.$message?.success('商品已删除')
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/Profile.css';
</style>