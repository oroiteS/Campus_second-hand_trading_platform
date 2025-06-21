<template>
  <div class="seller-profile-container">
    <div class="seller-profile-box">
      <div class="seller-profile-header">
        <button @click="goBack" class="seller-profile-back-btn">← 返回</button>
        <h2>卖家信息</h2>
      </div>
      
      <!-- 头像和基本信息 -->
      <div class="seller-profile-avatar-section">
        <img :src="sellerInfo.avatarUrl || '/public/测试图片.jpg'" class="seller-profile-avatar" />
        <div class="seller-profile-basic-info">
          <h3>{{ sellerInfo.userName || '未知用户' }}</h3>
        </div>
      </div>
      
      <!-- 详细信息 -->
      <div class="seller-profile-details">
        <div class="seller-profile-info-group">
          <label>用户ID</label>
          <div class="seller-profile-info-value">{{ sellerInfo.userId || '未知' }}</div>
        </div>
        
        <div class="seller-profile-info-group">
          <label>联系电话</label>
          <div class="seller-profile-info-value">{{ sellerInfo.telephone || '未公开' }}</div>
        </div>
        
        <div class="seller-profile-info-group">
          <label>注册时间</label>
          <div class="seller-profile-info-value">{{ formatDate(sellerInfo.registerTime) }}</div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="seller-profile-actions">
        <button @click="contactSeller" class="seller-profile-contact-btn">
          联系卖家
        </button>
      </div>
      
      <!-- 卖家商品列表 -->
      <div class="seller-products-section">
        <h3>卖家商品</h3>
        <div v-if="productsLoading" class="products-loading">
          正在加载商品...
        </div>
        <div v-else-if="products.length === 0" class="no-products">
          该卖家暂无商品
        </div>
        <!-- 优化商品卡片显示 -->
        <div v-else>
          <div class="products-grid">
            <div 
              v-for="product in products" 
              :key="product.id" 
              class="product-card"
              @click="viewProductDetail(product.id)"
            >
              <div class="product-image-container">
                <img :src="product.imageUrl || '/public/测试图片.jpg'" class="product-image" />
                <div class="product-status-badge condition">
                  {{ product.condition || '成色良好' }}
                </div>
              </div>
              <div class="product-info">
                <h4 class="product-title" :title="product.title">{{ product.title }}</h4>
                <p class="product-price">¥{{ product.price }}</p>
              </div>
            </div>
          </div>
          
          <!-- 删除这整个分页控件部分 -->
        </div>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="isLoading" class="seller-profile-loading">
        加载中...
      </div>
      
      <!-- 错误信息 -->
      <div v-if="errorMessage" class="seller-profile-error">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<script>
import {ax1} from '@/api/axios';
// 删除这行未使用的导入
// import { getSellerProducts } from '../api/commodity.js';

export default {
  name: 'SellerProfile',
  data() {
    return {
      sellerInfo: {
        userId: '',
        userName: '',
        telephone: '',
        avatarUrl: '',
        registerTime: ''
      },
      products: [],
      isLoading: false,
      productsLoading: false,
      errorMessage: ''
      // 删除以下分页相关数据：
      // currentPage: 1,
      // pageSize: 6,
      // totalProducts: 0
    }
  },
  // 删除整个 computed 部分：
  // computed: {
  //   totalPages() {
  //     return Math.ceil(this.totalProducts / this.pageSize);
  //   }
  // },
  created() {
    this.fetchSellerInfo();
    this.fetchSellerProducts();
  },
  methods: {
    // 获取卖家信息
    // 获取卖家信息
    fetchSellerInfo() {
      const sellerId = this.$route.params.sellerId || this.$route.query.sellerId;
      
      if (!sellerId) {
        this.errorMessage = '未找到卖家ID';
        return;
      }
      
      this.isLoading = true;
      this.errorMessage = '';
      
      ax1.post('/api-8089/user/info', {
        userId: sellerId
      })
      .then(response => {
        if (response.data.success) {
          const userData = response.data.data;
          this.sellerInfo = {
            userId: userData.userId,
            userName: userData.userName,
            telephone: userData.telephone,
            avatarUrl: userData.avatarUrl,
            registerTime: userData.createAt || userData.registerTime // 使用createAt字段作为注册时间
          };
        } else {
          this.errorMessage = response.data.message || '获取卖家信息失败';
        }
      })
      .catch(error => {
        console.error('获取卖家信息出错:', error);
        this.errorMessage = '获取卖家信息时发生错误，请稍后再试';
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    
    // 获取卖家商品列表（简化版，无分页）
    async fetchSellerProducts() {
      const sellerId = this.$route.params.sellerId || this.$route.query.sellerId;
      
      if (!sellerId) {
        return;
      }
      
      this.productsLoading = true;
      
      try {
        // 使用ax1 axios实例调用API
        const response = await ax1.get(`/api-8084/commodity/list/${sellerId}`);
        
        if (response.data.success) {
          this.products = (response.data.data || []).map(item => ({
            id: item.commodityId,
            title: item.commodityName,
            price: item.currentPrice,
            imageUrl: item.mainImageUrl || '/public/测试图片.jpg',
            status: item.commodityStatus,
            condition: item.newness || '成色良好',
            description: item.commodityDescription
          }));
        } else {
          throw new Error(response.data.message || '获取商品列表失败');
        }
      } catch (error) {
        console.error('获取卖家商品列表出错:', error);
        this.products = [];
        // 可以添加用户友好的错误提示
        this.errorMessage = '获取商品列表失败，请稍后重试';
      } finally {
        this.productsLoading = false;
      }
    },
    
    // 切换页面
    // 删除整个 changePage 方法：
    // changePage(page) {
    //   if (page >= 1 && page <= this.totalPages) {
    //     this.fetchSellerProducts(page);
    //   }
    // },
    
    // 查看商品详情
    viewProductDetail(productId) {
      this.$router.push({
        path: `/product/${productId}`,  // 修改为正确的路径
        query: { from: 'sellerProfile' }
      });
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '未知';
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN');
    },
    
    // 返回上一页
    goBack() {
      const from = this.$route.query.from;
      if (from === 'productDetail') {
        this.$router.go(-1);
      } else {
        this.$router.push('/');
      }
    },
    
    // 联系卖家
    contactSeller() {
      alert(`联系卖家：${this.sellerInfo.telephone || '暂无联系方式'}`);
    }
  }
}
</script>

<style scoped>
@import '../styles/SellerProfile.css';
</style>@/api/axios1