<template>
  <div class="search-container">
    <div class="search-header">
      <router-link to="/" class="back-button">← 返回首页</router-link>
      <h1>搜索页面</h1>
    </div>
    
    <div class="search-content">
      <div class="search-box">
        <input 
          type="text" 
          placeholder="请输入搜索内容..." 
          v-model="searchQuery"
          class="search-input"
        />
        <button @click="handleSearch" class="search-button" :disabled="isLoading">
          <span v-if="!isLoading">搜索</span>
          <span v-else>搜索中...</span>
        </button>
      </div>
      
      <!-- 加载动画 -->
      <div class="loading-container" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p class="loading-text">正在搜索商品...</p>
      </div>
      
      <div class="search-results" v-if="searchResults.length > 0 && !isLoading">
        <div class="results-header">
          <h3>搜索结果 (共 {{ totalResults }} 件商品)</h3>
        </div>
        
        <!-- 商品网格展示 -->
        <div class="products-grid">
          <div class="product-card" v-for="product in currentPageProducts" :key="product.commodity_id" @click="goToProductDetail(product.commodity_id)" style="cursor: pointer;">
            <div class="product-image">
              <img :src="product.main_image_url" :alt="product.commodity_name" />
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.commodity_name }}</h4>
              <p class="product-description">{{ product.commodity_description }}</p>
              <div class="product-price">
                <span class="current-price">¥{{ product.current_price }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 分页组件 -->
        <div class="pagination" v-if="totalPages > 1">
          <button 
            class="page-btn" 
            :class="{ disabled: currentPage === 1 }"
            @click="goToPage(currentPage - 1)"
            :disabled="currentPage === 1"
          >
            上一页
          </button>
          
          <button 
            class="page-btn" 
            v-for="page in visiblePages" 
            :key="page"
            :class="{ active: page === currentPage }"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>
          
          <button 
            class="page-btn" 
            :class="{ disabled: currentPage === totalPages }"
            @click="goToPage(currentPage + 1)"
            :disabled="currentPage === totalPages"
          >
            下一页
          </button>
        </div>
      </div>
      
      <div class="no-results" v-else-if="searchQuery && hasSearched && !isLoading">
        <p>未找到相关商品</p>
      </div>
    </div>
  </div>
</template>

<script>
import {instance} from '@/api/axios';

export default {
  name: 'SearchResult',
  data() {
    return {
      searchQuery: '',
      searchResults: [],
      hasSearched: false,
      currentPage: 1,
      pageSize: 12, // 每页显示12个商品
      totalResults: 0,
      isLoading: false // 添加加载状态
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalResults / this.pageSize);
    },
    currentPageProducts() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.searchResults.slice(start, end);
    },
    visiblePages() {
      const pages = [];
      const maxVisible = 5;
      let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
      let end = Math.min(this.totalPages, start + maxVisible - 1);
      
      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    }
  },
  mounted() {
    // 获取URL参数中的搜索关键词
    const query = this.$route.query.q;
    if (query) {
      this.searchQuery = query;
      // 自动执行搜索
      this.handleSearch();
    }
  },
  methods: {
    async handleSearch() {
      if (this.searchQuery.trim() && !this.isLoading) {
        this.hasSearched = true;
        this.currentPage = 1;
        this.isLoading = true; // 开始加载
        
        try {
          // 从localStorage获取用户ID
          const userId = localStorage.getItem('userId');
          
          // 调用后端搜索API
          const response = await instance.post('/api/v1/commodities/search', {
            user_id: userId,
            search_content: this.searchQuery
          });
          console.log('userID:', userId);
          console.log('searchContent:', this.searchQuery);
          
          this.searchResults = response.data;
          this.totalResults = response.data.length;
        } catch (error) {
          console.error('搜索请求错误:', error);
          this.searchResults = [];
          this.totalResults = 0;
        } finally {
          this.isLoading = false; // 结束加载
        }
      }
    },
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
        // 滚动到顶部
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },
    async goToProductDetail(productId){
      // 记录用户点击商品行为
      await this.recordClickBehavior(productId);
      
      this.$router.push({
        path: `/product/${productId}`
      });
    },
    // 记录用户点击商品行为
    async recordClickBehavior(commodityId) {
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          await instance.post('/api/v1/commodities/click_commodity', {
            user_id: userId,
            commodity_id: commodityId
          });
          console.log('点击行为已记录:', { userId, commodityId });
        }
      } catch (error) {
        console.error('记录点击行为失败:', error);
        // 不影响正常跳转，只记录错误
      }
    },

  }
}
</script>

<style scoped>
@import '../styles/SearchResult.css';

.search-header {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative; /* 为返回按钮定位 */
}

.search-header h1 {
  flex-grow: 1;
  text-align: center;
  margin: 0;
}

.back-button {
  padding: 10px 15px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 20px;
  text-decoration: none;
  color: #333;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.back-button:hover {
  background-color: #f5f5f5;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}
</style>@/api/axios1