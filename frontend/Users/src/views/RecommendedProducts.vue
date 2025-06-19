<template>
  <div class="recommended-products-container">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="home-header-content">
        <div class="home-logo">
          <span class="home-logo-text">校园二手交易</span>
          <span class="home-logo-subtitle">Campus Market</span>
        </div>
        <div class="home-nav-actions">
          <a @click="goBack" class="home-publish-btn">&larr; 返回首页</a>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="home-main-content">
      <!-- 热门推荐 -->
      <section class="home-section">
        <div class="home-section-header">
          <h3 class="home-section-title">🔥 热门推荐</h3>
        </div>
        <div v-if="isLoading" class="loading-container">
          <p>正在加载推荐商品...</p>
        </div>
        <div v-else-if="products.length === 0" class="empty-state">
            <p>暂无推荐商品</p>
        </div>
        <div v-else class="home-products-grid">
          <div class="home-product-card" v-for="product in products" :key="product.id" @click="goToProductDetail(product.id)">
            <div class="home-product-image-container">
              <img :src="product.main_image_url || '/测试图片.jpg'" :alt="product.commodity_name" class="home-product-image" />
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
                <img :src="product.avatar_url || 'https://via.placeholder.com/30x30/4CAF50/FFFFFF?text=U'" class="home-seller-avatar" />
                <span class="home-seller-name">{{ product.user_name }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import { get_commodities_recommendation } from '../api/commodity.js';

export default {
  name: 'RecommendedProducts',
  data() {
    return {
      products: [],
      isLoading: false,
    };
  },
  async created() {
    await this.loadProducts();
  },
  methods: {
    async loadProducts() {
      this.isLoading = true;
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.log('用户未登录，无法加载推荐商品');
        this.isLoading = false;
        return;
      }

      try {
        const recommendedData = await get_commodities_recommendation(userId);
        if (recommendedData && recommendedData.length > 0) {
          this.products = recommendedData.map(product => ({
            ...product,
            id: product.commodity_id, // 确保有唯一的key
          }));
          console.log('加载所有推荐商品成功:', this.products);
        } else {
          this.products = [];
        }
      } catch (error) {
        console.error('加载所有推荐商品失败:', error);
        this.products = [];
      } finally {
        this.isLoading = false;
      }
    },
    goToProductDetail(productId) {
      this.$router.push(`/product/${productId}`);
    },
    goBack() {
        this.$router.push('/');
    }
  },
};
</script>

<style scoped>
@import '../styles/Home.css';

.recommended-products-container {
  background-color: #f4f5f7;
}

.home-main-content {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
}

.empty-state {
    text-align: center;
    padding: 50px;
    color: #888;
}
</style>