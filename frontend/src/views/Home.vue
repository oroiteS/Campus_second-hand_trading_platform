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
          <router-link to="/login" class="login-link">登录</router-link>
        </div>
      </div>
    </header>

    <!-- 主导航菜单 -->
    <nav class="main-nav">
      <div class="nav-content">
        <a href="#" class="nav-item active">首页</a>
        <a href="#" class="nav-item">数码电子</a>
        <a href="#" class="nav-item">教材书籍</a>
        <a href="#" class="nav-item">生活用品</a>
        <a href="#" class="nav-item">服装配饰</a>
        <a href="#" class="nav-item">运动器材</a>
        <a href="#" class="nav-item">学习用品</a>
        <a href="#" class="nav-item">其他</a>
      </div>
    </nav>

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
          image: 'https://via.placeholder.com/200x150/F0F0F0/666666?text=iPhone',
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
  methods: {
    searchProducts() {
      console.log('搜索:', this.searchQuery)
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home {
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* 顶部导航栏 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  flex-direction: column;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: white;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

.logo-subtitle {
  font-size: 12px;
  color: rgba(255,255,255,0.8);
  margin-top: 2px;
}

.search-container {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-bar {
  display: flex;
  background: white;
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.search-input {
  flex: 1;
  padding: 12px 20px;
  border: none;
  outline: none;
  font-size: 14px;
}

.search-btn {
  padding: 12px 20px;
  background: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  transition: background 0.3s;
}

.search-btn:hover {
  background: #45a049;
}

.search-icon {
  font-size: 16px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.publish-btn {
  background: #FF6B35;
  color: white;
  padding: 10px 20px;
  border-radius: 20px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s;
  box-shadow: 0 2px 5px rgba(255,107,53,0.3);
}

.publish-btn:hover {
  background: #E55A2B;
  transform: translateY(-1px);
}

.login-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 15px;
  transition: all 0.3s;
}

.login-link:hover {
  background: rgba(255,255,255,0.1);
}

/* 主导航菜单 */
.main-nav {
  background: white;
  border-bottom: 1px solid #e0e0e0;
  padding: 0;
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  padding: 0 20px;
}

.nav-item {
  padding: 15px 20px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}

.nav-item:hover, .nav-item.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

/* 内容区域 */
.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  padding: 20px;
}

/* 侧边栏 */
.sidebar {
  width: 250px;
  flex-shrink: 0;
}

.category-menu, .notice-board {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.category-title, .notice-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
  margin-bottom: 5px;
}

.category-item:hover {
  background: #f8f9fa;
  padding-left: 10px;
}

.category-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 25px;
}

.category-name {
  font-size: 14px;
  color: #666;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-text {
  font-size: 13px;
  color: #666;
}

.notice-date {
  font-size: 12px;
  color: #999;
}

/* 主内容区 */
.main-content {
  flex: 1;
}

/* 欢迎横幅 */
.featured-section {
  margin-bottom: 30px;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  padding: 30px;
  color: white;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="2" fill="rgba(255,255,255,0.1)"/></svg>') repeat;
  animation: float 20s infinite linear;
}

@keyframes float {
  0% { transform: translateX(0) translateY(0); }
  100% { transform: translateX(-100px) translateY(-100px); }
}

.banner-content h2 {
  font-size: 28px;
  margin-bottom: 10px;
  position: relative;
  z-index: 1;
}

.banner-content p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.stats {
  display: flex;
  gap: 30px;
  position: relative;
  z-index: 1;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

/* 快速分类 */
.quick-categories {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.quick-category {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s;
  cursor: pointer;
}

.quick-category:hover {
  transform: translateY(-5px);
}

.quick-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin: 0 auto 10px;
  color: white;
}

.quick-name {
  display: block;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.quick-count {
  font-size: 12px;
  color: #999;
}

/* 区块标题 */
.section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.more-link {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.more-link:hover {
  color: #5a6fd8;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.product-image-container {
  position: relative;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #FF6B35;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.time-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0,0,0,0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  line-height: 1.4;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #FF6B35;
}

.product-original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
}

.product-condition {
  background: #E8F5E8;
  color: #4CAF50;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.product-location {
  color: #666;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.seller-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.seller-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.seller-school {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .quick-categories {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
  
  .stats {
    gap: 15px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-container {
    margin: 0;
    max-width: 100%;
  }
}
</style>