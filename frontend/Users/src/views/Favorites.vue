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
                <!-- <span class="product-school">{{ product.school }}</span> -->
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
import {ax1} from '@/api/axios'

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
        { id: 'electronics', name: '电子设备' },
        { id: 'books', name: '教材书籍' },
        { id: 'daily', name: '生活用品' },
        { id: 'clothing', name: '服装配饰' },
        { id: 'sports', name: '运动器材' },
        { id: 'study', name: '学习用品' },
        { id: 'beauty', name: '美妆护肤' },
        { id: 'others', name: '其它物品' }
      ],
      favoriteProducts: []
    }
  },
  mounted() {
    this.fetchCartCommodities()
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
    
    
    async fetchCartCommodities() {
      try {
        const res = await ax1.get('/api-8085/cart/commodities', {
          params: {
            userId: localStorage.getItem('userId'),
            category: this.activeCategory === 'all' ? '全部' : this.getCategoryNameById(this.activeCategory)
          }
        })
        this.favoriteProducts = res.data.map(item => ({
          id: item.commodityId,
          name: item.commodityName,
          price: item.currentPrice,
          image: item.mainImageUrl || 'https://via.placeholder.com/200',
          seller: item.sellerId,
          status: this.mapStatus(item.commodityStatus),
          statusClass: item.commodityStatus === 'sold' ? 'sold' : 'available',
          category: this.mapCategoryIdToFrontend(item.categoryId),
          favoriteTime: item.createdAt?.substring(0, 10) || '未知时间'
        }))
      } catch (err) {
        console.error('获取购物车商品失败', err)
      }
    },
    async addToCart(commodityId) {
      try {
        const res = await ax1.post('/api-8085/cart/add', null, {
          params: {
            userId: localStorage.getItem('userId'),
            commodityId
          }
        })
        if (res.data.success) {
          this.$message?.success('加入购物车成功')
          this.fetchCartCommodities()
        } else {
          this.$message?.error('加入失败')
        }
      } catch (err) {
        console.error('添加失败', err)
      }
    },
    async removeFavorite(commodityId) {
    try {
      const res = await ax1.post('/api-8085/cart/remove', null, {
        params: {
          userId: localStorage.getItem('userId'),
          commodityId
        }
      })
      if (res.data.success) {
        this.$message?.success('已取消收藏')
        this.fetchCartCommodities()
      }
    } catch (err) {
      console.error('移除失败', err)
    }
  },
  async batchRemoveFavorites() {
    const removed = []
    for (const id of this.selectedProducts) {
      const res = await ax1.post('/api-8085/cart/remove', null, {
        params: {
          userId:localStorage.getItem('userId'),
          commodityId: id
        }
      })
      if (res.data.success) {
        removed.push(id)
      }
    }
    this.selectedProducts = []
    this.isSelectMode = false
    this.$message?.success(`已取消收藏 ${removed.length} 件商品`)
    this.fetchCartCommodities()
  },
  mapStatus(status) {
    switch (status) {
      case 'on_sale': return '在售中'
      case 'sold': return '已售出'
      case 'off_sale': return '已下架'
      default: return '未知状态'
    }
  },
  getCategoryNameById(id) {
    const cat = this.categories.find(c => c.id === id)
    return cat ? cat.name : '全部'
  },
  mapCategoryIdToFrontend(categoryId) {
    // 建议从后端返回时直接携带分类名
    const map = {
      1: 'electronics',
      2: 'books',
      3: 'daily',
      4: 'sports',
      5: 'clothing',
      6: 'beauty',
      7: 'foods',
      8: 'furniture',
      9: 'stationery',
      10: 'others'
    }
    return map[categoryId] || 'others'
  }

  }
}
</script>

<style scoped>
@import '../styles/Favorites.css';
</style>@/api/axios1