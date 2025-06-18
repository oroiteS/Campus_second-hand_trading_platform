<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="home-header-content">
        <div class="home-logo">
          <span class="home-logo-text">校园二手交易</span>
          <span class="home-logo-subtitle">Campus Market</span>
        </div>
        <div class="home-search-container">
          <div class="home-search-bar">
            <input 
              type="text" 
              placeholder="搜索校园好物..." 
              v-model="searchQuery" 
              class="home-search-input"
            />
            <button @click="searchProducts" class="home-search-btn">
              <i class="home-search-icon">🔍</i>
            </button>
          </div>
        </div>
        <div class="home-nav-actions">
          <a @click="goToPublish" class="home-publish-btn">+ 发布闲置</a>
          <!-- 根据登录状态显示不同内容 -->
          <div v-if="isLoggedIn" class="home-user-info">
            <img :src="userInfo.avatar" :alt="userInfo.name + '的头像'" class="home-user-avatar" @click="goToProfile" />
            <div class="home-user-details" @click="goToProfile">
              <span class="home-user-name">{{ userInfo.name }}</span>
            </div>
            <!-- 悬浮菜单 -->
            <div class="home-user-dropdown">
              <button @click.stop="logout" class="home-logout-btn">退出登录</button>
            </div>
          </div>
          <div v-else class="home-auth-buttons">
            <router-link to="/login" class="home-login-link">登录</router-link>
            <router-link to="/register" class="home-register-link">注册</router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- 内容区域 -->
    <div class="home-content-wrapper">
      <!-- 侧边分类菜单 -->
      <aside class="home-sidebar">
        <div class="home-category-menu">
          <h3 class="home-category-title">商品分类</h3>
          <div class="home-category-item" v-for="category in categories" :key="category.id">
            <span class="home-category-icon">{{ category.icon }}</span>
            <span class="home-category-name">{{ category.name }}</span>
          </div>
        </div>
        
        <!-- 校园公告 -->
        <div class="home-notice-board">
          <h3 class="home-notice-title">📢 校园公告</h3>
          <div class="home-notice-item" v-for="notice in notices" :key="notice.id" @click="goToNoticeDetail(notice.id)">
            <span class="home-notice-text">{{ notice.text }}</span>
            <span class="home-notice-date">{{ notice.date }}</span>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="home-main-content">
        <!-- 推荐区域 -->
        <div class="home-featured-section">
          <div class="home-welcome-banner">
            <div class="home-banner-content">
              <h2>🎓 欢迎来到校园二手市场</h2>
              <p>让闲置物品重新焕发价值，让校园生活更加便利</p>
              <div class="home-stats">
                <div class="home-stat-item">
                  <span class="home-stat-number">1,234</span>
                  <span class="home-stat-label">在售商品</span>
                </div>
                <div class="home-stat-item">
                  <span class="home-stat-number">567</span>
                  <span class="home-stat-label">活跃用户</span>
                </div>
                <div class="home-stat-item">
                  <span class="home-stat-number">890</span>
                  <span class="home-stat-label">成功交易</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="home-quick-categories">
            <div class="home-quick-category" v-for="quickCat in quickCategories" :key="quickCat.id">
              <div class="home-quick-icon" :style="{backgroundColor: quickCat.color}">
                {{ quickCat.icon }}
              </div>
              <span class="home-quick-name">{{ quickCat.name }}</span>
              <span class="home-quick-count">{{ quickCat.count }}件</span>
            </div>
          </div>
        </div>

        <!-- 热门推荐 -->
        <section class="home-section">
          <div class="home-section-header">
            <h3 class="home-section-title">🔥 热门推荐</h3>
            <a href="#" class="home-more-link">查看更多 →</a>
          </div>
          <div class="home-products-grid">
            <div class="home-product-card" v-for="product in hotProducts" :key="product.id" @click="goToProductDetail(product.id)">
              <div class="home-product-image-container">
                <img :src="product.image" :alt="product.name" class="home-product-image" />
                <div class="home-product-badge" v-if="product.badge">{{ product.badge }}</div>
              </div>
              <div class="home-product-info">
                <h4 class="home-product-title">{{ product.name }}</h4>
                <div class="home-product-meta">
                  <span class="home-product-price">¥{{ product.price }}</span>
                  <span class="home-product-original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
                <div class="home-product-details">
                  <span class="home-product-condition">{{ product.condition }}</span>
                  <span class="home-product-location">📍 {{ product.location }}</span>
                </div>
                <div class="home-seller-info">
                  <img :src="product.sellerAvatar" class="home-seller-avatar" />
                  <span class="home-seller-name">{{ product.sellerName }}</span>
                  <span class="home-seller-school">{{ product.sellerSchool }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 最新发布 -->
        <section class="home-section">
          <div class="home-section-header">
            <h3 class="home-section-title">🆕 最新发布</h3>
            <a href="#" class="home-more-link">查看更多 →</a>
          </div>
          <div class="home-products-grid">
            <div class="home-product-card" v-for="product in newProducts" :key="product.id" @click="goToProductDetail(product.id)">
              <div class="home-product-image-container">
                <img :src="product.image" :alt="product.name" class="home-product-image" />
                <div class="home-time-badge">{{ product.timeAgo }}</div>
              </div>
              <div class="home-product-info">
                <h4 class="home-product-title">{{ product.name }}</h4>
                <div class="home-product-meta">
                  <span class="home-product-price">¥{{ product.price }}</span>
                </div>
                <div class="home-product-details">
                  <span class="home-product-condition">{{ product.condition }}</span>
                  <span class="home-product-location">📍 {{ product.location }}</span>
                </div>
                <div class="home-seller-info">
                  <img :src="product.sellerAvatar" class="home-seller-avatar" />
                  <span class="home-seller-name">{{ product.sellerName }}</span>
                  <span class="home-seller-school">{{ product.sellerSchool }}</span>
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
import axios from 'axios';

export default {
  name: 'HomePage',
  data() {
    return {
      searchQuery: '',
      isLoggedIn: false,
      userInfo: {
        name: '未知用户',
        avatar: '/测试图片.jpg',
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
      
      if (!token) {
        this.isLoggedIn = false;
        this.userInfo = {
          name: '未知用户',
          avatar: '/测试图片.jpg',
        };
        return;
      }
      
      // 验证token有效性
      axios.post('http://localhost:8080/api/user/validate-token', {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        const result = response.data;
        if (result.code === 200 && result.data === true) {
          // token有效，设置登录状态
          this.isLoggedIn = true;
          
          // 获取用户ID并获取用户信息
          const userId = localStorage.getItem('userId');
          if (userId) {
            console.log('登录成功，token有效!!!');
            // 移除这里的日志，改为在fetchUserInfo完成后输出
            this.fetchUserInfo(userId);
            // 通过API请求获取最新的用户信息
          }
        } else {
          // token无效，清除登录信息
          console.log('token无效，退出登录');
          this.logout();
        }
      })
      .catch(error => {
        console.error('验证token失败:', error);
        // 验证失败，清除登录信息
        this.logout();
      });
    },
    // 处理存储变化
    handleStorageChange(e) {
      if (e.key === 'userToken' || e.key === 'userId' || e.key === 'isLoggedIn') {
        this.checkLoginStatus();
      }
    },
    // 跳转到个人资料页面
    goToProfile() {
      // 从localStorage获取userId
      const userId = localStorage.getItem('userId');
      if (!userId) {
        alert('请先登录');
        return;
      }
      
      this.$router.push({
        path: '/profile',
        query: {
          userId: userId,
          name: this.userInfo.name
        }
      });
    },
    // 手动更新登录状态
    updateLoginStatus() {
      this.checkLoginStatus();
    },
    
    // 从后端获取用户信息
    fetchUserInfo(userId) {
      // 使用axios发送请求获取用户信息
      axios.post('http://localhost:8089/api/user/info', {
        userId: userId
      })
      .then(response => {
        if (response.data.success && response.data.code === 200) {
          // 只获取realName进行展示
          this.userInfo = {
            name: response.data.data.realName || '未知用户',
            avatar: response.data.data.avatarUrl || '/测试图片.jpg',
          };
          console.log('获取用户信息成功:', this.userInfo);
          
          // 在获取用户信息成功后输出完整的登录状态
          console.log('登录成功状态检查:', {
            token: localStorage.getItem('userToken'),
            isLoggedIn: this.isLoggedIn,
            userInfo: this.userInfo
          });
        } else {
          console.error('获取用户信息失败:', response.data.message);
          // 使用默认值
          this.userInfo = {
            name: '未知用户',
            avatar: '/测试图片.jpg',
          };
        }
      })
      .catch(error => {
        console.error('获取用户信息请求失败:', error);
        // 使用默认值
        this.userInfo = {
          name: '未知用户',
          avatar: '/测试图片.jpg',
        };
      });
    },
    // 跳转到商品详情页
    goToProductDetail(productId) {
      this.$router.push(`/product/${productId}`);
    },
    // 退出登录
    logout() {
      // 清除登录信息
      localStorage.removeItem('userToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('isLoggedIn');
      
      // 更新登录状态
      this.isLoggedIn = false;
      this.userInfo = {
        name: '未知用户',
        avatar: '/测试图片.jpg',
      };
      
      // 跳转到首页
      this.$router.push('/');
    },
    // 跳转到发布页面
    goToPublish() {
      // 检查是否已登录
      if (!this.isLoggedIn) {
        alert('请先登录后再发布商品');
        this.$router.push('/login');
        return;
      }
      this.$router.push('/publish');
    },
    // 跳转到公告详情页面
    goToNoticeDetail(noticeId) {
      this.$router.push(`/notice/${noticeId}`);
    }
  }
}
</script>

<style scoped>
@import '../styles/Home.css';
</style>