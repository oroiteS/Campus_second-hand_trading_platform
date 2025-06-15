<template>
  <div class="favorites-page">
    <!-- 顶部导航 -->
    <header class="favorites-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
          ← 返回
        </button>
        <h1 class="page-title">我的收藏</h1>
        <div class="header-actions">
          <button class="header-btn" @click="toggleSelectMode">
            {{ isSelectMode ? '取消' : '管理' }}
          </button>
        </div>
      </div>
    </header>

    <div class="favorites-container">
      <!-- 收藏统计 -->
      <div class="stats-section">
        <div class="stats-card">
          <span class="stats-number">{{ favoriteProducts.length }}</span>
          <span class="stats-label">件收藏商品</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-section">
        <div class="section-header" v-if="favoriteProducts.length > 0">
          <div class="filter-tabs">
            <button 
              v-for="category in categories" 
              :key="category.id"
              class="filter-tab"
              :class="{active: activeCategory === category.id}"
              @click="activeCategory = category.id"
            >
              {{ category.name }}
            </button>
          </div>
          <div class="sort-options">
            <select v-model="sortBy" class="sort-select">
              <option value="time">收藏时间</option>
              <option value="price">价格</option>
              <option value="name">商品名称</option>
            </select>
          </div>
        </div>
        
        <div class="products-grid" v-if="filteredProducts.length > 0">
          <div 
            class="product-card" 
            v-for="product in filteredProducts" 
            :key="product.id"
            :class="{selected: selectedProducts.includes(product.id)}"
            @click="handleProductClick(product)"
          >
            <div class="product-image-container">
              <img :src="product.image" :alt="product.name" class="product-image" />
              <div class="product-status" :class="product.statusClass">{{ product.status }}</div>
              <div v-if="isSelectMode" class="select-checkbox" @click.stop="toggleSelect(product.id)">
                <span v-if="selectedProducts.includes(product.id)">✓</span>
              </div>
            </div>
            <div class="product-info">
              <h4 class="product-title">{{ product.name }}</h4>
              <p class="product-price">¥{{ product.price }}</p>
              <div class="product-meta">
                <span class="product-seller">{{ product.seller }}</span>
                <span class="product-school">{{ product.school }}</span>
              </div>
              <p class="favorite-time">{{ product.favoriteTime }}</p>
            </div>
            <button v-if="!isSelectMode" class="unfavorite-btn" @click.stop="removeFavorite(product.id)">
              ❤️
            </button>
          </div>
        </div>
        
        <div class="empty-state" v-else>
          <div class="empty-icon">💝</div>
          <p class="empty-text">还没有收藏任何商品</p>
          <p class="empty-subtext">去首页看看有什么好东西吧</p>
          <button class="goto-home-btn" @click="goToHome">去首页逛逛</button>
        </div>
      </div>

      <!-- 批量操作栏 -->
      <div class="batch-actions" v-if="isSelectMode && selectedProducts.length > 0">
        <div class="batch-info">
          已选择 {{ selectedProducts.length }} 件商品
        </div>
        <div class="batch-buttons">
          <button class="batch-btn remove-btn" @click="batchRemoveFavorites">
            取消收藏
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FavoritesPage',
  data() {
    return {
      isSelectMode: false,
      selectedProducts: [],
      activeCategory: 'all',
      sortBy: 'time',
      categories: [
        { id: 'all', name: '全部' },
        { id: 'electronics', name: '数码' },
        { id: 'books', name: '书籍' },
        { id: 'clothing', name: '服装' },
        { id: 'daily', name: '日用' }
      ],
      favoriteProducts: [
        {
          id: 1,
          name: 'iPhone 13 Pro 128GB',
          price: 4999,
          image: 'https://via.placeholder.com/200x200/FF6B35/FFFFFF?text=手机',
          seller: '张同学',
          school: '计算机学院',
          status: '在售中',
          statusClass: 'available',
          category: 'electronics',
          favoriteTime: '2024年1月15日收藏'
        },
        {
          id: 2,
          name: '高等数学教材（第七版）',
          price: 25,
          image: 'https://via.placeholder.com/200x200/4CAF50/FFFFFF?text=教材',
          seller: '李同学',
          school: '数学学院',
          status: '在售中',
          statusClass: 'available',
          category: 'books',
          favoriteTime: '2024年1月10日收藏'
        },
        {
          id: 3,
          name: 'Nike运动鞋 42码',
          price: 299,
          image: 'https://via.placeholder.com/200x200/2196F3/FFFFFF?text=鞋子',
          seller: '王同学',
          school: '体育学院',
          status: '已售出',
          statusClass: 'sold',
          category: 'clothing',
          favoriteTime: '2024年1月8日收藏'
        },
        {
          id: 4,
          name: '小米台灯护眼版',
          price: 89,
          image: 'https://via.placeholder.com/200x200/9C27B0/FFFFFF?text=台灯',
          seller: '赵同学',
          school: '设计学院',
          status: '在售中',
          statusClass: 'available',
          category: 'daily',
          favoriteTime: '2024年1月5日收藏'
        }
      ]
    }
  },
  computed: {
    filteredProducts() {
      let products = this.favoriteProducts
      
      // 按分类筛选
      if (this.activeCategory !== 'all') {
        products = products.filter(product => product.category === this.activeCategory)
      }
      
      // 排序
      products.sort((a, b) => {
        switch (this.sortBy) {
          case 'price':
            return a.price - b.price
          case 'name':
            return a.name.localeCompare(b.name)
          case 'time':
          default:
            return new Date(b.favoriteTime) - new Date(a.favoriteTime)
        }
      })
      
      return products
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    goToHome() {
      this.$router.push('/')
    },
    toggleSelectMode() {
      this.isSelectMode = !this.isSelectMode
      if (!this.isSelectMode) {
        this.selectedProducts = []
      }
    },
    handleProductClick(product) {
      if (this.isSelectMode) {
        this.toggleSelect(product.id)
      } else {
        // 跳转到商品详情页
        this.$router.push(`/product/${product.id}`)
      }
    },
    toggleSelect(productId) {
      const index = this.selectedProducts.indexOf(productId)
      if (index > -1) {
        this.selectedProducts.splice(index, 1)
      } else {
        this.selectedProducts.push(productId)
      }
    },
    removeFavorite(productId) {
      const index = this.favoriteProducts.findIndex(p => p.id === productId)
      if (index > -1) {
        this.favoriteProducts.splice(index, 1)
        this.$message?.success('已取消收藏')
      }
    },
    batchRemoveFavorites() {
      this.favoriteProducts = this.favoriteProducts.filter(
        product => !this.selectedProducts.includes(product.id)
      )
      this.selectedProducts = []
      this.isSelectMode = false
      this.$message?.success(`已取消收藏 ${this.selectedProducts.length} 件商品`)
    }
  }
}
</script>

<style scoped>
.favorites-page {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 顶部导航 */
.favorites-header {
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
.favorites-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 统计区域 */
.stats-section {
  margin-bottom: 30px;
}

.stats-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  display: inline-block;
}

.stats-number {
  display: block;
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.stats-label {
  font-size: 14px;
  color: #666;
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
  flex-wrap: wrap;
  gap: 15px;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.filter-tab.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.filter-tab:hover {
  border-color: #667eea;
}

.sort-select {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.product-card.selected {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.product-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-status {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.product-status.available {
  background: #4CAF50;
}

.product-status.sold {
  background: #999;
}

.select-checkbox {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  border: 2px solid white;
  border-radius: 50%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #FF6B35;
  margin-bottom: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.product-seller, .product-school {
  font-size: 12px;
  color: #666;
}

.favorite-time {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.unfavorite-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(255,255,255,0.9);
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
}

.unfavorite-btn:hover {
  background: white;
  transform: scale(1.1);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 18px;
  color: #666;
  margin-bottom: 10px;
}

.empty-subtext {
  font-size: 14px;
  color: #999;
  margin-bottom: 30px;
}

.goto-home-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.goto-home-btn:hover {
  background: #5a6fd8;
  transform: translateY(-1px);
}

/* 批量操作栏 */
.batch-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #e0e0e0;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  z-index: 50;
}

.batch-info {
  font-size: 14px;
  color: #666;
}

.batch-buttons {
  display: flex;
  gap: 10px;
}

.batch-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.remove-btn {
  background: #FF6B6B;
  color: white;
}

.remove-btn:hover {
  background: #ff5252;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-tabs {
    justify-content: center;
  }
}
</style>