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
            <input type="text" placeholder="搜索校园好物..." v-model="searchQuery" class="home-search-input" />
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
              <span class="home-user-status">{{ userInfo.status }}</span>
            </div>
            <!-- 消息通知按钮 -->
            <div class="home-notification-btn" @click="showNotifications">
              <span class="notification-icon">🔔</span>
              <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
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
          <div class="home-category-item" v-for="category in categories" :key="category.id"
            @click="goToCategoryBrowse(category.id)">
            <span class="home-category-icon">{{ category.icon }}</span>
            <span class="home-category-name">{{ category.name }}</span>
          </div>
        </div>

        <!-- 校园公告 -->
        <div class="home-notice-board">
          <h3 class="home-notice-title">📢 校园公告</h3>
          <div class="home-notice-item" v-for="notice in notices" :key="notice.announcementId"
            @click="goToNoticeDetail(notice.announcementId)">
            <span class="home-notice-text">{{ notice.content.substring(0, 10) }}{{ notice.content.length > 10 ? '...' :
              '' }}</span>
            <span class="home-notice-date">{{ new Date(notice.createdAt).toLocaleDateString().substring(5) }}</span>
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
              <div class="home-quick-icon" :style="{ backgroundColor: quickCat.color }">
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
            <router-link to="/recommended" class="home-more-link">查看更多 →</router-link>
          </div>
          <div class="home-products-grid">
            <div class="home-product-card" v-for="product in recommendedProducts" :key="product.id"
              @click="goToProductDetail(product.id)">
              <div class="home-product-image-container">
                <img :src="product.main_image_url || '/测试图片.jpg'" :alt="product.commodity_name"
                  class="home-product-image" />
                <div class="home-product-badge" v-if="product.badge">{{ product.badge }}</div>
              </div>
              <div class="home-product-info">
                <h4 class="home-product-title">{{ product.commodity_name }}</h4>
                <div class="home-product-meta">
                  <span class="home-product-price">¥{{ product.current_price }}</span>

                </div>
                <div class="home-product-details">
                  <span class="home-product-condition">{{ product.newness }}</span>
                </div>
                <div class="home-seller-info">
                  <img :src="product.avatar_url || 'https://via.placeholder.com/30x30/4CAF50/FFFFFF?text=U'"
                    class="home-seller-avatar" />
                  <span class="home-seller-name">{{ product.user_name }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 最新发布 -->
        <section class="home-section">
          <div class="home-section-header">
            <h3 class="home-section-title">🆕 最新发布</h3>
            <a href="#" class="home-more-link" @click="refreshLatestProducts">刷新数据 →</a>
          </div>

          <!-- 加载状态 -->
          <div v-if="isLoadingNewProducts" class="loading-container">
            <p>正在加载最新商品...</p>
          </div>

          <!-- 商品列表 -->
          <div v-else class="home-products-grid">
            <div class="home-product-card" v-for="product in newProducts" :key="product.id"
              @click="goToProductDetail(product.id)">
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
                </div>
                <div class="home-seller-info">
                  <img :src="product.sellerAvatar" class="home-seller-avatar" />
                  <span class="home-seller-name">{{ product.sellerName }}</span>
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
// 导入API函数
import { getLatestCommodities, getAllUsers, transformCommodityData, get_commodities_recommendation } from '../api/commodity.js';

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
      unreadCount: 0, // 初始化未读消息数量
      quickCategories: [], // 初始化快速分类
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
      notices: [],
      recommendedProducts: [],
      isLoadingHotProducts: false,
      // 将newProducts改为从API获取
      newProducts: [],
      // 添加加载状态
      isLoadingNewProducts: false,
      // 添加用户数据缓存
      usersCache: [],
      usersCacheTime: null // 添加缓存时间戳
    }
  },

  async mounted() {
    // 检查用户登录状态
    this.checkLoginStatus();
    // 自动加载最新商品数据
    await this.loadLatestProducts();
    // 获取校园公告
    await this.fetchAnnouncements();
    // 加载推荐商品
    await this.loadRecommendedProducts();
    // 获取未读消息数量
    await this.fetchUnreadCount();
    // 添加存储监听器，当localStorage发生变化时更新状态
    window.addEventListener('storage', this.handleStorageChange);
  },
  beforeUnmount() {
    // 清理事件监听器
    window.removeEventListener('storage', this.handleStorageChange);
  },
  watch: {
    // 监听路由变化，每次进入页面都检查登录状态并刷新商品数据
    async '$route'(to) {
      this.checkLoginStatus();
      // 如果是从其他页面回到首页，自动刷新最新商品
      if (to.path === '/' || to.name === 'HomePage') {
        await this.loadLatestProducts();
      }
    }
  },
  methods: {
    searchProducts() {
      console.log('搜索:', this.searchQuery)
    },
    // 跳转到分类浏览页面
    goToCategoryBrowse(categoryId) {
      this.$router.push(`/browse/${categoryId}`);
    },
    // 检查登录状态
    checkLoginStatus() {
      // 检查token
      const token = localStorage.getItem('userToken');

      if (!token) {
        this.isLoggedIn = false;
        this.unreadCount = 0;// 未登录时清零未读消息数量
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
              // 通过API请求获取最新的用户信息
              this.fetchUserInfo(userId);
              // 通过API请求获取未读消息数量
              this.fetchUnreadCount();
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
              name: response.data.data.userName || '未知用户',
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
      this.unreadCount = 0;// 未读消息数量清零
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
      // 找到对应的公告对象
      const notice = this.notices.find(item => item.announcementId === noticeId);

      if (notice) {
        // 使用query参数传递公告信息
        this.$router.push({
          path: '/notice',
          query: {
            id: notice.announcementId,
            content: notice.content,
            createdAt: notice.createdAt,
            rootName: '平台管理员'
          }
        });
      } else {
        console.error('未找到对应的公告信息');
        this.$router.push('/notice?error=notfound');
      }
    },
    showNotifications() {
      // 获取当前用户ID
      const userId = localStorage.getItem('userId');

      if (!userId) {
        // 如果用户未登录，提示登录
        alert('请先登录后查看消息');
        this.$router.push('/login');
        return;
      }

      // 跳转到聊天列表页面
      this.$router.push(`/chat-list/${userId}`);

      // 可选：清除未读消息数量
      this.unreadCount = 0;
    },

    async loadRecommendedProducts() {
      this.isLoadingHotProducts = true;
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.log('用户未登录，无法加载推荐商品');
        this.isLoadingHotProducts = false;
        return;
      }

      try {
        const recommendedData = await get_commodities_recommendation(userId);
        if (recommendedData && recommendedData.length > 0) {
          this.recommendedProducts = recommendedData.slice(0, 12).map(product => ({
            ...product,
            id: product.commodity_id, // 确保有唯一的key
          }));
          console.log('加载推荐商品成功:', this.recommendedProducts);
        } else {
          this.recommendedProducts = []; // 清空或使用默认
        }
      } catch (error) {
        console.error('加载推荐商品失败:', error);
        this.recommendedProducts = []; // 清空或使用默认
      } finally {
        this.isLoadingHotProducts = false;
      }
    },

    /**
     * 加载最新商品数据
     */
    async loadLatestProducts() {
      this.isLoadingNewProducts = true;

      try {
        // 并行获取商品数据和用户数据
        const [commodities, users] = await Promise.all([
          getLatestCommodities(),
          this.getUsersData()
        ]);

        // 转换数据格式
        this.newProducts = transformCommodityData(commodities, users);

        console.log('成功加载最新商品:', this.newProducts);

      } catch (error) {
        console.error('加载最新商品失败:', error);

        // 显示错误提示
        this.showErrorMessage('加载最新商品失败，请稍后重试');

        // 使用默认数据作为后备
        this.newProducts = this.getDefaultNewProducts();

      } finally {
        this.isLoadingNewProducts = false;
      }
    },

    /**
     * 获取用户数据（带缓存）
     */
    async getUsersData() {
      // 如果已有缓存且不超过5分钟，直接使用缓存
      if (this.usersCache.length > 0 && this.usersCacheTime &&
        (Date.now() - this.usersCacheTime) < 5 * 60 * 1000) {
        return this.usersCache;
      }

      try {
        const users = await getAllUsers();
        this.usersCache = users;
        this.usersCacheTime = Date.now();
        return users;
      } catch (error) {
        console.warn('获取用户数据失败，使用空数组:', error);
        return [];
      }
    },

    /**
     * 显示错误消息
     */
    showErrorMessage(message) {
      // 这里可以使用更好的提示组件，比如Element UI的Message
      alert(message);
    },

    /**
     * 获取默认的新商品数据（作为后备）
     */
    getDefaultNewProducts() {
      return [
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
      ];
    },

    /**
     * 刷新最新商品数据
     */
    async refreshLatestProducts() {
      await this.loadLatestProducts();
    },

    /**
     * 获取校园公告
     * @param {Number} n - 获取公告的数量，默认为5
     * @param {String} rootId - 可选的管理员ID过滤
     */
    async fetchAnnouncements(n = 5, rootId = null) {
      try {
        const params = { n };

        // 如果提供了rootId，则添加到请求参数中
        if (rootId) {
          params.rootId = rootId;
        }

        const response = await axios.get('http://localhost:8092/api/announcements', { params });

        // 过滤公告，只显示visibleStatus为false的公告
        this.notices = response.data.filter(announcement => announcement.visibleStatus === true);
        console.log('获取校园公告成功:', this.notices);
      } catch (error) {
        console.error('获取校园公告失败:', error);
        // 使用空数组作为后备
        this.notices = [];
      }
    },
    async fetchUnreadCount() {
      const userId = localStorage.getItem("userId");
      // 如果用户未登录，直接返回
      if (!userId || !this.isLoggedIn) {
        this.unreadCount = 0;
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8088/api/v1/chat/user/${userId}/unread`);

        if (response.data && typeof response.data.unread_count === 'number') {
          this.unreadCount = response.data.unread_count;
          console.log('获取未读消息数量成功:', this.unreadCount);
        } else {
          console.warn('未读消息数量响应格式异常:', response.data);
          console.warn('期望的字段: unread_count, 实际响应字段:', Object.keys(response.data || {}));
          this.unreadCount = 0;
        }
      } catch (error) {
        console.error('获取未读消息数量失败:', error);
        // 发生错误时不改变当前的未读数量，避免误导用户
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/Home.css';
</style>