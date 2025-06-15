<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-text">校园二手交易</span>
          <span class="logo-subtitle">Campus Market</span>
        </div>
        <div class="search-container">
          <div class="search-bar">
            <input 
              type="text" 
              placeholder="搜索校园好物..." 
              v-model="searchQuery" 
              class="search-input"
            />
            <button @click="searchProducts" class="search-btn">
              <i class="search-icon">🔍</i>
            </button>
          </div>
        </div>
        <div class="nav-actions">
          <a href="#" class="publish-btn">+ 发布闲置</a>
          <!-- 根据登录状态显示不同内容 -->
          <div v-if="isLoggedIn" class="user-info">
            <img :src="userInfo.avatar" :alt="userInfo.name + '的头像'" class="user-avatar" @click="goToProfile" />
            <div class="user-details" @click="goToProfile">
              <span class="user-name">{{ userInfo.name }}</span>
              <span class="user-status">{{ userInfo.status }}</span>
            </div>
            <!-- 悬浮菜单 -->
            <div class="user-dropdown">
              <button @click.stop="logout" class="logout-btn">退出登录</button>
            </div>
          </div>
          <div v-else class="auth-buttons">
            <router-link to="/login" class="login-link">登录</router-link>
            <router-link to="/register" class="register-link">注册</router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <!-- 侧边分类菜单 -->
      <aside class="sidebar">
        <div class="category-menu">
          <h3 class="category-title">商品分类</h3>
          <div class="category-item" v-for="category in categories" :key="category.id">
            <span class="category-icon">{{ category.icon }}</span>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
        
        <!-- 校园公告 -->
        <div class="notice-board">
          <h3 class="notice-title">📢 校园公告</h3>
          <div class="notice-item" v-for="notice in notices" :key="notice.id">
            <span class="notice-text">{{ notice.text }}</span>
            <span class="notice-date">{{ notice.date }}</span>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="main-content">
        <!-- 推荐区域 -->
        <div class="featured-section">
          <div class="welcome-banner">
            <div class="banner-content">
              <h2>🎓 欢迎来到校园二手市场</h2>
              <p>让闲置物品重新焕发价值，让校园生活更加便利</p>
              <div class="stats">
                <div class="stat-item">
                  <span class="stat-number">1,234</span>
                  <span class="stat-label">在售商品</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">567</span>
                  <span class="stat-label">活跃用户</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">890</span>
                  <span class="stat-label">成功交易</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="quick-categories">
            <div class="quick-category" v-for="quickCat in quickCategories" :key="quickCat.id">
              <div class="quick-icon" :style="{backgroundColor: quickCat.color}">
                {{ quickCat.icon }}
              </div>
              <span class="quick-name">{{ quickCat.name }}</span>
              <span class="quick-count">{{ quickCat.count }}件</span>
            </div>
          </div>
        </div>

        <!-- 热门推荐 -->
        <section class="section">
          <div class="section-header">
            <h3 class="section-title">🔥 热门推荐</h3>
            <a href="#" class="more-link">查看更多 →</a>
          </div>
          <div class="products-grid">
            <div class="product-card" v-for="product in hotProducts" :key="product.id">
              <div class="product-image-container">
                <img :src="product.image" :alt="product.name" class="product-image" />
                <div class="product-badge" v-if="product.badge">{{ product.badge }}</div>
              </div>
              <div class="product-info">
                <h4 class="product-title">{{ product.name }}</h4>
                <div class="product-meta">
                  <span class="product-price">¥{{ product.price }}</span>
                  <span class="product-original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
                <div class="product-details">
                  <span class="product-condition">{{ product.condition }}</span>
                  <span class="product-location">📍 {{ product.location }}</span>
                </div>
                <div class="seller-info">
                  <img :src="product.sellerAvatar" class="seller-avatar" />
                  <span class="seller-name">{{ product.sellerName }}</span>
                  <span class="seller-school">{{ product.sellerSchool }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 最新发布 -->
        <section class="section">
          <div class="section-header">
            <h3 class="section-title">🆕 最新发布</h3>
            <a href="#" class="more-link">查看更多 →</a>
          </div>
          <div class="products-grid">
            <div class="product-card" v-for="product in newProducts" :key="product.id">
              <div class="product-image-container">
                <img :src="product.image" :alt="product.name" class="product-image" />
                <div class="time-badge">{{ product.timeAgo }}</div>
              </div>
              <div class="product-info">
                <h4 class="product-title">{{ product.name }}</h4>
                <div class="product-meta">
                  <span class="product-price">¥{{ product.price }}</span>
                </div>
                <div class="product-details">
                  <span class="product-condition">{{ product.condition }}</span>
                  <span class="product-location">📍 {{ product.location }}</span>
                </div>
                <div class="seller-info">
                  <img :src="product.sellerAvatar" class="seller-avatar" />
                  <span class="seller-name">{{ product.sellerName }}</span>
                  <span class="seller-school">{{ product.sellerSchool }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      searchQuery: '',
      isLoggedIn: false,
      userInfo: {
        name: 'xy21675070351',
        avatar: '/测试图片.jpg',
        status: '在线'
      },
      categories: [
        { id: 1, name: '数码电子', icon: '📱' },
        { id: 2, name: '教材书籍', icon: '📚' },
        { id: 3, name: '生活用品', icon: '🏠' },
        { id: 4, name: '服装配饰', icon: '👕' },
        { id: 5, name: '运动器材', icon: '⚽' },
        { id: 6, name: '学习用品', icon: '✏️' },
        { id: 7, name: '美妆护肤', icon: '💄' },
        { id: 8, name: '其他物品', icon: '📦' }
      ],
      notices: [
        { id: 1, text: '新用户注册送积分', date: '12-20' },
        { id: 2, text: '期末教材回收活动', date: '12-18' },
        { id: 3, text: '诚信交易倡议书', date: '12-15' }
      ],
      quickCategories: [
        { id: 1, name: '教材', icon: '📖', count: 156, color: '#4CAF50' },
        { id: 2, name: '手机', icon: '📱', count: 89, color: '#2196F3' },
        { id: 3, name: '电脑', icon: '💻', count: 67, color: '#FF9800' },
        { id: 4, name: '服装', icon: '👔', count: 234, color: '#E91E63' }
      ],
      hotProducts: [
        {
          id: 1,
          name: 'iPhone 13 Pro 128G',
          price: 4999,
          originalPrice: 6999,
          condition: '9成新',
          location: '东校区',
          image: '/测试图片.jpg',
          badge: '热销',
          sellerName: '张同学',
          sellerSchool: '计算机学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/4CAF50/FFFFFF?text=张'
        },
        {
          id: 2,
          name: '高等数学教材（第七版）',
          price: 25,
          originalPrice: 45,
          condition: '8成新',
          location: '西校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=教材',
          badge: '推荐',
          sellerName: '李同学',
          sellerSchool: '数学学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/2196F3/FFFFFF?text=李'
        },
        {
          id: 3,
          name: 'MacBook Air M1',
          price: 6500,
          originalPrice: 8999,
          condition: '9成新',
          location: '南校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=MacBook',
          sellerName: '王同学',
          sellerSchool: '设计学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/FF9800/FFFFFF?text=王'
        },
        {
          id: 4,
          name: '小米台灯护眼版',
          price: 89,
          originalPrice: 129,
          condition: '全新',
          location: '北校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=台灯',
          sellerName: '赵同学',
          sellerSchool: '物理学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/E91E63/FFFFFF?text=赵'
        }
      ],
      newProducts: [
        {
          id: 5,
          name: 'AirPods Pro 2代',
          price: 1299,
          condition: '9成新',
          location: '东校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=AirPods',
          timeAgo: '5分钟前',
          sellerName: '陈同学',
          sellerSchool: '音乐学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/9C27B0/FFFFFF?text=陈'
        },
        {
          id: 6,
          name: '英语四级真题集',
          price: 15,
          condition: '8成新',
          location: '西校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=四级',
          timeAgo: '10分钟前',
          sellerName: '刘同学',
          sellerSchool: '外语学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/607D8B/FFFFFF?text=刘'
        },
        {
          id: 7,
          name: '宿舍小冰箱',
          price: 299,
          condition: '9成新',
          location: '南校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=冰箱',
          timeAgo: '15分钟前',
          sellerName: '周同学',
          sellerSchool: '生活学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/795548/FFFFFF?text=周'
        },
        {
          id: 8,
          name: '篮球鞋 Nike Air',
          price: 399,
          condition: '7成新',
          location: '北校区',
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=球鞋',
          timeAgo: '20分钟前',
          sellerName: '吴同学',
          sellerSchool: '体育学院',
          sellerAvatar: 'https://via.placeholder.com/30x30/FF5722/FFFFFF?text=吴'
        }
      ]
    }
  },
  mounted() {
    // 检查用户登录状态
    this.checkLoginStatus();
    // 添加存储监听器，当localStorage发生变化时更新状态
    window.addEventListener('storage', this.handleStorageChange);
  },
  beforeUnmount() {
    // 清理事件监听器
    window.removeEventListener('storage', this.handleStorageChange);
  },
  watch: {
    // 监听路由变化，每次进入页面都检查登录状态
    '$route'() {
      this.checkLoginStatus();
    }
  },
  methods: {
    searchProducts() {
      console.log('搜索:', this.searchQuery)
    },
    // 检查登录状态
    checkLoginStatus() {
      // 检查token
      const token = localStorage.getItem('userToken');
      this.isLoggedIn = !!token;
      
      // 如果已登录，获取用户信息
      if (this.isLoggedIn) {
        const userInfoStr = localStorage.getItem('userInfo');
        if (userInfoStr) {
          try {
            this.userInfo = JSON.parse(userInfoStr);
          } catch (e) {
            console.error('解析用户信息失败:', e);
            // 使用默认用户信息
            this.userInfo = {
              name: localStorage.getItem('username') || 'xy21675070351',
              avatar: '/测试图片.jpg',
              status: '在线'
            };
          }
        }
      }
      
      // 调试信息
      console.log('登录状态检查:', {
        token: token,
        isLoggedIn: this.isLoggedIn,
        userInfo: this.userInfo
      });
    },
    // 处理存储变化
    handleStorageChange(e) {
      if (e.key === 'userToken' || e.key === 'userInfo') {
        this.checkLoginStatus();
      }
    },
    // 跳转到个人资料页面
    goToProfile() {
      this.$router.push('/profile');
    },
    // 手动更新登录状态
    updateLoginStatus() {
      this.checkLoginStatus();
    },
    // 退出登录
    logout() {
      // 清除本地存储的用户信息
      localStorage.removeItem('userToken');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      
      // 更新登录状态
      this.isLoggedIn = false;
      this.userInfo = {
        name: 'xy21675070351',
        avatar: '/测试图片.jpg',
        status: '在线'
      };
      
      // 跳转到首页
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
@import '../styles/Home.css';
</style>