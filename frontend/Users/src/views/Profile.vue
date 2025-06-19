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
              <!-- 删除按钮已移除 -->
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
  

  <!-- 定位加载状态提示 -->
  <div v-if="isLocating" class="location-loading">
    <div class="loading-spinner"></div>
    <p>正在获取您的位置...</p>
  </div>

  <!-- 定位错误提示 -->
  <div v-if="locationError" class="location-error">
    <p>⚠️ {{ locationError }}</p>
    <button @click="retryLocation">重试</button>
  </div>
</template>

<script>
import axios from 'axios'
import AMapLoader from '@amap/amap-jsapi-loader'
export default {
  name: 'ProfilePage',
  
  data() {
    return {
      activeTab: 'selling',
      userId: '',
      userInfo: {
        username: '默认用户',
        avatar: '/测试图片.jpg',
        dealCount: 47,
        favoriteCount: 23
      },
      menuItems: [
        { id: 1, name: '我的收藏', icon: '❤️', color: '#FF6B6B', action: 'favorites' },
        { id: 2, name: '订单管理', icon: '📋', color: '#4ECDC4', action: 'orders' },
        { id: 3, name: '个人信息', icon: '⚙️', color: '#45B7D1', action: 'settings' },
        { id: 4, name: '附近的人', icon: '❓', color: '#FFEAA7', action: 'nearby' },
        { id: 5, name: '钱包管理', icon: '💰', color: '#FFA500', action: 'wallet' },
        { id: 6, name: '密码修改', icon: '🔒', color: '#9370DB', action: 'password' }
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
  created() {
    // 从路由参数中获取userId和name
    if (this.$route.query.userId) {
      console.log('Profile页面获取到的userId:', this.$route.query.userId);
      // 保存userId以便后续使用
      this.userId = this.$route.query.userId;
      
      // 获取用户头像URL
      this.fetchUserAvatar();
    }
    
    // 如果传递了name参数，更新userInfo中的username
    if (this.$route.query.name) {
      console.log('Profile页面获取到的name:', this.$route.query.name);
      this.userInfo.username = this.$route.query.name;
    }
  },
  methods: {
    // 获取用户头像URL
    fetchUserAvatar() {
      if (!this.userId) {
        console.error('获取头像失败：用户ID不存在');
        return;
      }
      
      axios.post('http://localhost:8089/api/user/avatar/url', {
        userId: this.userId
      })
      .then(response => {
        if (response.data.success && response.data.data) {
          // 更新用户头像URL
          this.userInfo.avatar = response.data.data.avatarUrl;
          console.log('成功获取用户头像URL:', this.userInfo.avatar);
        } else {
          console.error('获取头像URL失败:', response.data.message);
        }
      })
      .catch(error => {
        console.error('获取头像URL请求出错:', error);
      });
    },
    
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
    async handleMenuClick(action) {
      console.log('点击菜单:', action)
      // 根据不同的action跳转到不同的页面
      switch(action) {
        case 'favorites':
          this.$router.push('/favorites')
          break
        case 'orders':
          // 跳转到订单管理页面
          this.$router.push('/orders')
          break
        case 'settings':
          // 跳转到UserProfile.vue页面
          console.log('跳转到个人信息')
          this.$router.push('/userprofile')
          break
        case 'nearby':
          // 可以添加帮助中心页面
          await this.handleNearbyClick()
          console.log('跳转到附近的人')
          break
          case 'wallet':
          // 跳转到钱包管理页面，并传递userId
          console.log('跳转到钱包管理，userId:', this.userId)
          this.$router.push({
            path: '/wallet',
            query: {
              userId: this.userId
            }
          })
          break
        case 'password':
          // 跳转到密码修改页面
          console.log('跳转到密码修改')
          this.$router.push({
            path: '/password-reset',
            query: {
              userId: this.userId
            }
          })
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
    },
    async handleNearbyClick() {
      this.isLocating = true
      this.locationError = null
      
      try {
        // 1. 初始化高德地图
        window._AMapSecurityConfig = {
          securityJsCode: "cde9e988223d78ba64124400dbef252a",
        }
        
        const AMap = await AMapLoader.load({
          key: "514d185bcae5fedf73ec30184c598996",
          version: "2.0",
          plugins: ["AMap.Geolocation"],
        })
        
        // 2. 获取当前位置
        const position = await new Promise((resolve, reject) => {
          const geolocation = new AMap.Geolocation({
            enableHighAccuracy: true,
            timeout: 10000,
            showButton: false,
          })
          
          geolocation.getCurrentPosition((status, result) => {
            if (status === 'complete') {
              console.log('定位成功，位置信息:', result) // 详细定位信息
              console.log('经度:', result.position.lng.toFixed(6)) // 经度
              console.log('纬度:', result.position.lat.toFixed(6)) // 纬度
              
          
              resolve({
                longitude: result.position.lng.toFixed(6),
                latitude: result.position.lat.toFixed(6),
              })
            } else {
              reject(new Error(result.message || '定位失败'))
            }
          }, { showMarker: false })
        })
        
        // 3. 跳转并传递所有信息
        this.$router.push({
          path: '/nearbyusers',
          query: {
            userId: this.$route.query.userId, // 用户ID
            lon: position.longitude,      // 经度
            lat: position.latitude,       // 纬度
            
          }
        })
        
      } catch (error) {
        console.error('定位失败:', error)
        this.locationError = error.message || '获取位置信息失败'
      } finally {
        this.isLocating = false
      }
    },
    retryLocation() {
      this.locationError = null
      this.handleNearbyClick()
    }
  }
}
</script>

<style scoped>
@import '../styles/Profile.css';
</style>