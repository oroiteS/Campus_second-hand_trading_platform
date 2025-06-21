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
        </div>
      </div>
    </header>

    <div class="browse-container">
      <!-- 筛选栏 -->
      <div class="filter-section">
        <div class="filter-row">
          <div class="filter-group">
            <label>价格范围：</label>
            <select v-model="priceRange" @change="resetAndLoadProducts">
              <option value="">不限</option>
              <option value="0-50">0-50元</option>
              <option value="50-200">50-200元</option>
              <option value="200-500">200-500元</option>
              <option value="500-1000">500-1000元</option>
              <option value="1000+">1000元以上</option>
            </select>
          </div>
          <div class="filter-group">
            <label>商品状态：</label>
            <select v-model="newness" @change="resetAndLoadProducts">
              <option value="">不限</option>
              <option value="全新">全新</option>
              <option value="95新">95新</option>
              <option value="9新">9新</option>
            </select>
          </div>
          <div class="filter-group">
            <label>排序方式：</label>
            <select v-model="sortBy" @change="resetAndLoadProducts">
              <option value="time_desc">发布时间（新到旧）</option>
              <option value="time_asc">发布时间（旧到新）</option>
              <option value="price_asc">价格从低到高</option>
              <option value="price_desc">价格从高到低</option>
            </select>
          </div>
          <div class="filter-group">
            <label>每页显示：</label>
            <select v-model="pageSize" @change="resetAndLoadProducts">
              <option value="10">10条</option>
              <option value="20">20条</option>
              <option value="50">50条</option>
              <option value="100">100条</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-section">
        <div class="products-header">
          <span class="products-count">共找到 {{ totalCount }} 件商品，当前第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <p>正在加载商品数据...</p>
        </div>

        <div class="products-grid" v-else-if="products.length > 0">
          <div class="product-card" v-for="product in products" :key="product.commodityId"
            @click="goToProductDetail(product.commodityId)">
            <div class="product-image-container">
              <img :src="product.mainImageUrl || '/测试图片.jpg'" :alt="product.commodityName" class="product-image" />
              <div class="product-badge" v-if="product.commodityStatus != 'ON_SALE'">在售</div>
            </div>
            <div class="product-info">
              <h4 class="product-title">{{ product.commodityName }}</h4>
              <div class="product-price">
                <span class="current-price">¥{{ product.currentPrice }}</span>
              </div>
              <div class="product-details">
                <span class="product-condition">{{ product.newness }}</span>
              </div>
              <div class="product-meta">
                <span class="product-time">{{ formatTime(product.createdAt) }}</span>
                <span class="product-quantity" v-if="product.quantity > 1">数量: {{ product.quantity }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="empty-state" v-else>
          <div class="empty-icon">📦</div>
          <p class="empty-text">暂无相关商品</p>
          <p class="empty-subtext">试试调整筛选条件或浏览其他分类</p>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <button @click="changePage(1)" :disabled="currentPage === 1" class="page-btn">
            首页
          </button>
          <button @click="changePage(currentPage - 1)" :disabled="!hasPrevious" class="page-btn">
            上一页
          </button>

          <!-- 页码显示 -->
          <div class="page-numbers">
            <span v-for="page in getPageNumbers()" :key="page" @click="changePage(page)"
              :class="['page-number', { active: page === currentPage, disabled: page === '...' }]">
              {{ page }}
            </span>
          </div>

          <button @click="changePage(currentPage + 1)" :disabled="!hasNext" class="page-btn">
            下一页
          </button>
          <button @click="changePage(totalPages)" :disabled="currentPage === totalPages" class="page-btn">
            末页
          </button>
        </div>

        <!-- 调试信息 -->
        <div class="debug-info" v-if="showDebug">
          <p>调试信息：</p>
          <p>当前页: {{ currentPage }}</p>
          <p>每页大小: {{ pageSize }}</p>
          <p>总数量: {{ totalCount }}</p>
          <p>总页数: {{ totalPages }}</p>
          <p>有下一页: {{ hasNext }}</p>
          <p>有上一页: {{ hasPrevious }}</p>
          <p>商品数量: {{ products.length }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ProductBrowse',
  data() {
    return {
      categoryId: null,
      categoryName: '商品浏览',
      priceRange: '',
      newness: '',
      sortBy: 'time_desc',
      products: [],
      loading: false,
      showDebug: false, // 可以设置为true来显示调试信息
      // 分页相关
      currentPage: 1,
      pageSize: 20,
      totalCount: 0,
      totalPages: 0,
      hasNext: false,
      hasPrevious: false
    }
  },
  mounted() {
    this.categoryId = this.$route.params.categoryId || this.$route.query.category
    this.loadCategoryData()
    this.loadProducts()
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    loadCategoryData() {
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
    // 重置页码并加载商品
    resetAndLoadProducts() {
      this.currentPage = 1
      this.loadProducts()
    },
    async loadProducts() {
      if (!this.categoryId) {
        console.error('分类ID不能为空')
        return
      }

      this.loading = true

      try {
        // 构建请求参数
        const requestData = {
          categoryId: parseInt(this.categoryId),
          pageNum: this.currentPage,
          pageSize: parseInt(this.pageSize), // 确保是数字类型
          sortBy: this.sortBy
        }

        // 添加可选的筛选条件
        if (this.priceRange) {
          requestData.priceRange = this.priceRange
        }
        if (this.newness) {
          requestData.newness = this.newness
        }

        console.log('请求参数:', requestData)

        // 调用后端API
        const response = await axios.post(
          'http://localhost:8096/product-query/api/v1/commodities/category',
          requestData,
          {
            headers: {
              'Content-Type': 'application/json'
            }
          }
        )

        console.log('API响应:', response.data)

        if (response.data.success) {
          const data = response.data.data
          this.products = data.commodities || []
          this.currentPage = data.currentPage || 1
          this.totalCount = data.totalCount || 0
          this.totalPages = data.totalPages || 0
          this.hasNext = data.hasNext || false
          this.hasPrevious = data.hasPrevious || false

          console.log('分页信息更新:', {
            currentPage: this.currentPage,
            totalPages: this.totalPages,
            totalCount: this.totalCount,
            productsLength: this.products.length
          })
        } else {
          console.error('API返回错误:', response.data.message)
          this.showError('加载商品失败: ' + response.data.message)
        }
      } catch (error) {
        console.error('请求失败:', error)
        this.showError('网络请求失败，请检查后端服务是否启动')
      } finally {
        this.loading = false
      }
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages && page !== '...') {
        this.currentPage = page
        this.loadProducts()
        // 滚动到顶部
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
    },
    // 获取页码数组（用于分页显示）
    getPageNumbers() {
      const pages = []
      const total = this.totalPages
      const current = this.currentPage

      if (total <= 7) {
        // 如果总页数小于等于7，显示所有页码
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        // 复杂分页逻辑
        if (current <= 4) {
          for (let i = 1; i <= 5; i++) {
            pages.push(i)
          }
          pages.push('...')
          pages.push(total)
        } else if (current >= total - 3) {
          pages.push(1)
          pages.push('...')
          for (let i = total - 4; i <= total; i++) {
            pages.push(i)
          }
        } else {
          pages.push(1)
          pages.push('...')
          for (let i = current - 1; i <= current + 1; i++) {
            pages.push(i)
          }
          pages.push('...')
          pages.push(total)
        }
      }

      return pages
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) {
        return '刚刚'
      } else if (diff < 3600000) {
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) {
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return Math.floor(diff / 86400000) + '天前'
      }
    },
    showError(message) {
      alert(message)
    },
    async goToProductDetail(productId) {
      this.$router.push(`/product/${productId}`)
      await this.recordClickBehavior(productId);
    },
        // 记录用户点击商品行为
        async recordClickBehavior(commodityId) {
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          await axios.post('http://localhost:8000/api/v1/commodities/click_commodity', {
            user_id: userId,
            commodity_id: commodityId
          });
          console.log('点击行为已记录:', { userId, commodityId });
        }
      } catch (error) {
        console.error('记录点击行为失败:', error);
        // 不影响正常跳转，只记录错误
      }
    }
  },
  watch: {
    '$route'() {
      this.categoryId = this.$route.params.categoryId || this.$route.query.category
      this.currentPage = 1 // 重置页码
      this.loadCategoryData()
      this.loadProducts()
    }
  }
}
</script>

<style scoped>
@import '../styles/ProductBrowse.css';
</style>