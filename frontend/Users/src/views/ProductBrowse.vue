<template>
  <div class="product-browse">
    <!-- 顶部导航 -->
    <header class="browse-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
          ← 返回
        </button>
        <h1 class="page-title">{{ categoryName }}</h1>
        <div class="header-actions">
          <button class="header-btn filter-btn" @click="toggleFilter">
            筛选
          </button>
        </div>
      </div>
    </header>

    <div class="browse-container">
      <!-- 筛选栏 -->
      <div class="filter-section" v-show="showFilter">
        <div class="filter-row">
          <div class="filter-group">
            <label>价格范围：</label>
            <select v-model="priceRange" @change="filterProducts">
              <option value="all">不限</option>
              <option value="0-50">0-50元</option>
              <option value="50-200">50-200元</option>
              <option value="200-500">200-500元</option>
              <option value="500-1000">500-1000元</option>
              <option value="1000+">1000元以上</option>
            </select>
          </div>
          <div class="filter-group">
            <label>商品状态：</label>
            <select v-model="condition" @change="filterProducts">
              <option value="all">不限</option>
              <option value="全新">全新</option>
              <option value="9成新">9成新</option>
              <option value="8成新">8成新</option>
              <option value="7成新">7成新</option>
            </select>
          </div>
          <div class="filter-group">
            <label>排序方式：</label>
            <select v-model="sortBy" @change="sortProducts">
              <option value="time">发布时间</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
              <option value="popularity">热度</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-section">
        <div class="products-header">
          <span class="products-count">共找到 {{ filteredProducts.length }} 件商品</span>
        </div>
        
        <div class="products-grid" v-if="filteredProducts.length > 0">
          <div 
            class="product-card" 
            v-for="product in filteredProducts" 
            :key="product.id"
            @click="goToProductDetail(product.id)"
          >
            <div class="product-image-container">
              <img :src="product.image" :alt="product.name" class="product-image" />
              <div class="product-badge" v-if="product.badge">{{ product.badge }}</div>
            </div>
            <div class="product-info">
              <h4 class="product-title">{{ product.name }}</h4>
              <div class="product-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
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
        
        <div class="empty-state" v-else>
          <div class="empty-icon">📦</div>
          <p class="empty-text">暂无相关商品</p>
          <p class="empty-subtext">试试调整筛选条件或浏览其他分类</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductBrowse',
  data() {
    return {
      categoryId: null,
      categoryName: '商品浏览',
      showFilter: false,
      priceRange: 'all',
      condition: 'all',
      sortBy: 'time',
      allProducts: [],
      filteredProducts: []
    }
  },
  mounted() {
    this.categoryId = this.$route.params.categoryId
    this.loadCategoryData()
    this.loadProducts()
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    toggleFilter() {
      this.showFilter = !this.showFilter
    },
    loadCategoryData() {
      // 根据分类ID设置分类名称
      const categoryMap = {
        '1': '数码电子',
        '2': '教材书籍', 
        '3': '生活用品',
        '4': '服装配饰',
        '5': '运动器材',
        '6': '学习用品',
        '7': '美妆护肤',
        '8': '其他物品'
      }
      this.categoryName = categoryMap[this.categoryId] || '商品浏览'
    },
    loadProducts() {
      // 模拟加载商品数据，实际项目中应该从API获取
      this.allProducts = [
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
          sellerAvatar: 'https://via.placeholder.com/30x30/4CAF50/FFFFFF?text=张',
          category: '1',
          publishTime: new Date('2024-01-15')
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
          sellerAvatar: 'https://via.placeholder.com/30x30/2196F3/FFFFFF?text=李',
          category: '2',
          publishTime: new Date('2024-01-14')
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
          sellerAvatar: 'https://via.placeholder.com/30x30/FF9800/FFFFFF?text=王',
          category: '1',
          publishTime: new Date('2024-01-13')
        }
        // 可以添加更多商品数据
      ]
      this.filterProducts()
    },
    filterProducts() {
      let products = this.allProducts
      
      // 按分类筛选
      if (this.categoryId && this.categoryId !== 'all') {
        products = products.filter(product => product.category === this.categoryId)
      }
      
      // 按价格筛选
      if (this.priceRange !== 'all') {
        const [min, max] = this.priceRange.split('-').map(p => p.replace('+', ''))
        products = products.filter(product => {
          if (this.priceRange === '1000+') {
            return product.price >= 1000
          }
          return product.price >= parseInt(min) && product.price <= parseInt(max)
        })
      }
      
      // 按状态筛选
      if (this.condition !== 'all') {
        products = products.filter(product => product.condition === this.condition)
      }
      
      this.filteredProducts = products
      this.sortProducts()
    },
    sortProducts() {
      switch (this.sortBy) {
        case 'price-asc':
          this.filteredProducts.sort((a, b) => a.price - b.price)
          break
        case 'price-desc':
          this.filteredProducts.sort((a, b) => b.price - a.price)
          break
        case 'popularity':
          // 可以根据浏览量、收藏量等排序
          this.filteredProducts.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
          break
        case 'time':
        default:
          this.filteredProducts.sort((a, b) => new Date(b.publishTime) - new Date(a.publishTime))
          break
      }
    },
    goToProductDetail(productId) {
      this.$router.push(`/product/${productId}`)
    }
  },
  watch: {
    '$route'() {
      this.categoryId = this.$route.params.categoryId
      this.loadCategoryData()
      this.filterProducts()
    }
  }
}
</script>

<style scoped>
@import '../styles/ProductBrowse.css';
</style>