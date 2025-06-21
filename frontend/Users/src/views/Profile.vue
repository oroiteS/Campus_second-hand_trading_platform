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
              @click="switchTab(tab.id)"
            >
              {{ tab.name }}
            </button>
          </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="isLoadingProducts" class="loading-container">
          <div class="loading-spinner"></div>
          <p>正在加载商品...</p>
        </div>
        
        <!-- 商品列表 -->
        <div class="profile-products-grid" v-else-if="currentProducts.length > 0">
          <div class="profile-product-card" v-for="product in currentProducts" :key="product.commodityId">
            <img :src="product.mainImageUrl || '/测试图片.jpg'" :alt="product.commodityName" class="profile-product-image" @click="goToProductDetail(product.commodityId)" />
            <div class="profile-product-info" @click="goToProductDetail(product.commodityId)">
              <h4 class="profile-product-title">{{ product.commodityName }}</h4>
              <p class="profile-product-price">¥{{ product.currentPrice }}</p>
              <p class="profile-product-status">{{ getStatusText(product.commodityStatus) }}</p>
            </div>
            <!-- 商品操作按钮 -->
            <div class="profile-product-actions">
              <div class="profile-status-buttons">
                <button 
                  class="profile-status-btn" 
                  :class="{ active: product.commodityStatus === 'ON_SALE' }"
                  @click.stop="changeProductStatus(product, 'on_sale')"
                  title="设为在售"
                >
                  在售
                </button>
                <button 
                  class="profile-status-btn" 
                  :class="{ active: product.commodityStatus === 'SOLD' }"
                  @click.stop="changeProductStatus(product, 'sold')"
                  title="设为已售"
                >
                  已售
                </button>
                <button 
                  class="profile-status-btn" 
                  :class="{ active: product.commodityStatus === 'OFF_SALE' }"
                  @click.stop="changeProductStatus(product, 'off_sale')"
                  title="设为已下架"
                >
                  下架
                </button>
              </div>

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
      isLoadingProducts: false, // 添加商品加载状态
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
        { id: 'selling', name: '在售', apiStatus: 'on_sale' },
        { id: 'sold', name: '已售', apiStatus: 'sold' },
        { id: 'offline', name: '已下架', apiStatus: 'off_sale' }
      ],
      products: {
        selling: [],
        sold: [],
        offline: []
      }
    }
  },
  computed: {
    currentProducts() {
      return this.products[this.activeTab] || []
    }
  },
  async created() {
    // 从路由参数中获取userId和name
    if (this.$route.query.userId) {
      console.log('Profile页面获取到的userId:', this.$route.query.userId);
      // 保存userId以便后续使用
      this.userId = this.$route.query.userId;
      
      // 获取用户头像URL
      this.fetchUserAvatar();
      await this.fetchFavoriteCount();
          // 获取成功交易数量
      await this.fetchCompletedDealsCount();
      // 加载商品数据
      await this.loadAllProducts();
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
    
    // 加载所有状态的商品
    async loadAllProducts() {
      if (!this.userId) {
        console.error('无法加载商品：用户ID不存在');
        return;
      }
      
      this.isLoadingProducts = true;
      
      try {
        // 并行加载所有状态的商品
        const promises = this.productTabs.map(tab => 
          this.fetchProductsByStatus(tab.apiStatus, tab.id)
        );
        
        await Promise.all(promises);
        console.log('所有商品加载完成:', this.products);
      } catch (error) {
        console.error('加载商品失败:', error);
        this.$message?.error('加载商品失败，请重试');
      } finally {
        this.isLoadingProducts = false;
      }
    },
    
    // 根据状态获取商品列表
    async fetchProductsByStatus(apiStatus, tabId) {
      try {
        const response = await axios.get(
          `http://localhost:8084/api/commodity/list/${this.userId}/status/${apiStatus}`
        );
        
        if (response.data.success) {
          this.products[tabId] = response.data.data || [];
          console.log(`${apiStatus}状态商品加载成功:`, response.data.data);
        } else {
          console.error(`获取${apiStatus}状态商品失败:`, response.data.message);
          this.products[tabId] = [];
        }
      } catch (error) {
        console.error(`获取${apiStatus}状态商品请求失败:`, error);
        this.products[tabId] = [];
      }
    },
    
    // 切换标签页
    async switchTab(tabId) {
      this.activeTab = tabId;
      // 如果当前标签页的商品为空，重新加载
      if (this.products[tabId].length === 0) {
        const tab = this.productTabs.find(t => t.id === tabId);
        if (tab) {
          this.isLoadingProducts = true;
          await this.fetchProductsByStatus(tab.apiStatus, tabId);
          this.isLoadingProducts = false;
        }
      }
    },
    
    // 获取状态文本
    getStatusText(commodityStatus) {
      const statusMap = {
        'ON_SALE': '在售中',
        'SOLD': '已售出',
        'OFF_SALE': '已下架'
      };
      return statusMap[commodityStatus] || '未知状态';
    },
    
    // 修改商品状态
    async changeProductStatus(product, newApiStatus) {
      try {
        let apiUrl = '';
        let successMessage = '';
        
        // 根据新状态选择对应的API接口
        switch(newApiStatus) {
          case 'on_sale':
            apiUrl = 'http://localhost:8084/api/commodity/put-on-sale';
            successMessage = '商品上架成功';
            break;
          case 'off_sale':
            apiUrl = 'http://localhost:8084/api/commodity/put-off-sale';
            successMessage = '商品下架成功';
            break;
          case 'sold':
            apiUrl = 'http://localhost:8084/api/commodity/mark-as-sold';
            successMessage = '商品已标记为已售';
            break;
          default:
            this.$message?.error('无效的商品状态');
            return;
        }
        
        // 调用对应的API接口
        const response = await axios.post(apiUrl, {
          commodityId: product.commodityId,
          sellerId: this.userId
        });
        
        if (response.data.success) {
          // 重新加载所有商品数据
          await this.loadAllProducts();
          this.$message?.success(successMessage);
        } else {
          this.$message?.error(response.data.message || '商品状态更新失败');
        }
      } catch (error) {
        console.error('更新商品状态失败:', error);
        this.$message?.error('商品状态更新失败，请重试');
      }
    },
    
    // 删除商品
    async deleteProduct(product) {
      if (confirm(`确定要删除商品"${product.commodityName}"吗？此操作不可撤销。`)) {
        try {
          // 这里需要调用删除商品的API
          const response = await axios.delete(
            `http://localhost:8084/api/commodity/${product.commodityId}`
          );
          
          if (response.data.success) {
            // 重新加载所有商品数据
            await this.loadAllProducts();
            this.$message?.success('商品删除成功');
          } else {
            this.$message?.error('商品删除失败');
          }
        } catch (error) {
          console.error('删除商品失败:', error);
          this.$message?.error('商品删除失败，请重试');
        }
      }
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
    },
    async fetchFavoriteCount() {
  try {
    const response = await axios.get('/cart/commodities', {
      params: {
        userId: this.userId,
        category: '全部'
      }
    });
    
    // 更新收藏商品数量
    this.userInfo.favoriteCount = response.data.length;
    console.log('收藏商品数量:', this.userInfo.favoriteCount);
    
  } catch (error) {
    console.error('获取收藏商品数量失败:', error);
    // 如果获取失败，保持默认值0
    this.userInfo.favoriteCount = 0;
  }
},
async fetchCompletedDealsCount() {
  try {
    console.log('开始获取用户成功交易数量...');
    const response = await axios.post('http://localhost:8095/api/orders/query/by-status', {
      status: 'completed'
    });
    
    console.log('用户交易API响应:', response.data);
    
    if (response.data && response.data.code === 200) {
      const completedOrders = response.data.data;
      console.log('用户成功交易订单:', completedOrders);
      
      // 过滤出当前用户相关的订单（作为买家或卖家）
      const userOrders = completedOrders.filter(order => 
        order.buyerId === this.userId || order.sellerId === this.userId
      );
      
      this.userInfo.dealCount = userOrders.length;
      console.log('更新后的用户交易数量:', this.userInfo.dealCount);
    }
  } catch (error) {
    console.error('获取用户成功交易数量失败:', error);
    // 如果获取失败，保持默认值0
    this.userInfo.dealCount = 0;
  }
},
  }
}
</script>

<style scoped>
@import '../styles/Profile.css';

/* 添加加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>