<template>
  <div class="product-detail">
    <!-- 顶部导航 -->
    <header class="detail-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
          ← 返回
        </button>
        <h1 class="page-title">商品详情</h1>
        <!-- 根据来源显示不同按钮 -->
        <button v-if="isEditable" @click="editProduct" class="header-btn edit-btn">编辑</button>
        <button v-else class="header-btn share-btn">分享</button>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载商品详情...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">⚠️</div>
      <p class="error-message">{{ error }}</p>
      <button @click="goBack" class="back-btn">返回上一页</button>
    </div>

    <!-- 商品详情内容 -->
    <div v-else class="detail-container">
      <div class="product-images">
        <div class="main-image">
          <img :src="currentImage" :alt="product.name" class="main-img" />
        </div>
        <div class="thumbnail-list">
          <div v-for="(image, index) in product.images" :key="index" class="thumbnail"
            :class="{ active: currentImageIndex === index }" @click="selectImage(index)">
            <img :src="image" :alt="`商品图片${index + 1}`" />
          </div>
        </div>
      </div>

      <!-- 右侧：商品信息 -->
      <div class="product-info">
        <!-- 价格和标题 -->
        <div class="price-section">
          <div class="price-main">
            <span class="currency">¥</span>
            <span class="price">{{ product.price }}</span>
          </div>
          <!-- 删除原价显示部分 -->
          <!-- <div class="price-original" v-if="product.originalPrice">
            原价：¥{{ product.originalPrice }}
          </div> -->
        </div>

        <h2 class="product-title">{{ product.name }}</h2>

        <!-- 删除商品描述部分 -->
        <!-- <div class="product-description">
          <p>{{ product.description }}</p>
        </div> -->

        <!-- 商品详细信息 -->
        <div class="product-details">
          <div class="detail-item">
            <span class="label">成色：</span>
            <span class="value condition">{{ product.condition }}</span>
          </div>
          <!-- 删除品牌字段 -->
          <!-- <div class="detail-item">
            <span class="label">品牌：</span>
            <span class="value">{{ product.brand }}</span>
          </div> -->
          <!-- 删除交易地点字段 -->
          <!-- <div class="detail-item">
            <span class="label">交易地点：</span>
            <span class="value location">📍 {{ product.location }}</span>
          </div> -->
          <div class="detail-item">
            <span class="label">发布时间：</span>
            <span class="value">{{ product.publishTime }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品数量：</span>
            <span class="value">{{ product.quantity }} 件</span>
          </div>
          <!-- 删除以下浏览次数相关代码 -->
          <!-- <div class="detail-item">
            <span class="label">浏览次数：</span>
            <span class="value">{{ product.viewCount }} 次</span>
          </div> -->
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section">
          <div class="seller-header" @click="viewSellerProfile" style="cursor: pointer;">
            <h3>卖家信息</h3>
            <!-- 删除卖家徽章 -->
            <!-- <span class="seller-badge">{{ product.seller.badge }}</span> -->
          </div>
          <div class="seller-info" @click="viewSellerProfile" style="cursor: pointer;">
            <img :src="product.seller.avatar" class="seller-avatar" />
            <div class="seller-details">
              <div class="seller-name">{{ product.seller.name }}</div>
              <!-- 删除学院信息 -->
              <!-- <div class="seller-school">{{ product.seller.school }}</div> -->
              <!-- 删除信用评分和成交次数 -->
              <!-- <div class="seller-stats">
                <span>信用评分：{{ product.seller.creditScore }}</span>
                <span>成交：{{ product.seller.dealCount }}笔</span>
              </div> -->
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="btn-contact" @click="contactSeller">
            💬 联系卖家
          </button>
          <button class="btn-buy" @click="buyNow">
            立即购买
          </button>
          <button class="btn-favorite" @click="toggleFavorite" :class="{ active: isFavorited }">
            {{ isFavorited ? '❤️' : '🤍' }} 收藏
          </button>
        </div>
      </div>
    </div>

    <!-- 商品详细描述 -->
    <div class="description-section">
      <h3 class="section-title">商品详情</h3>
      <div class="description-content">
        <p v-for="(paragraph, index) in product.detailDescription" :key="index">
          {{ paragraph }}
        </p>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section">
      <h3 class="section-title">评论 ({{ comments.length }})</h3>

      <!-- 发表评论 -->
      <div class="comment-form">
        <div class="comment-input-area">
          <img :src="currentUser.avatar" class="user-avatar" />
          <div class="input-container">
            <textarea v-model="newComment" placeholder="写下你的评论..." class="comment-input" rows="3"
              maxlength="500"></textarea>
            <div class="input-footer">
              <span class="char-count">{{ newComment.length }}/500</span>
              <button @click="submitComment" :disabled="!newComment.trim()" class="submit-btn">
                发表评论
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-if="comments.length === 0" class="no-comments">
          <div class="no-comments-icon">💬</div>
          <p>暂无评论，快来发表第一条评论吧！</p>
        </div>

        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <img :src="comment.user.avatar" class="comment-avatar" />
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.user.name }}</span>
              <span class="comment-school">{{ comment.user.school }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <button @click="toggleLike(comment)" :class="['action-btn', { liked: comment.isLiked }]">
                {{ comment.isLiked ? '❤️' : '🤍' }} {{ comment.likeCount }}
              </button>
              <button @click="showReplyInput(comment.id)" class="action-btn">
                💬 回复
              </button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="reply-input-area">
              <img :src="currentUser.avatar" class="user-avatar small" />
              <div class="input-container">
                <textarea v-model="replyContent" :placeholder="`回复 ${comment.user.name}...`" class="reply-input"
                  rows="2" maxlength="300"></textarea>
                <div class="input-footer">
                  <span class="char-count">{{ replyContent.length }}/300</span>
                  <div class="reply-actions">
                    <button @click="cancelReply" class="cancel-btn">取消</button>
                    <button @click="submitReply(comment.id)" :disabled="!replyContent.trim()" class="submit-btn">
                      回复
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <img :src="reply.user.avatar" class="reply-avatar" />
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="reply-author">{{ reply.user.name }}</span>
                    <span class="reply-school">{{ reply.user.school }}</span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <div class="reply-text">
                    <span v-if="reply.replyTo" class="reply-target">@{{ reply.replyTo.name }}</span>
                    {{ reply.content }}
                  </div>
                  <div class="reply-actions">
                    <button @click="toggleReplyLike(reply)" :class="['action-btn', { liked: reply.isLiked }]">
                      {{ reply.isLiked ? '❤️' : '🤍' }} {{ reply.likeCount }}
                    </button>
                    <button @click="replyToReply(comment.id, reply)" class="action-btn">
                      💬 回复
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 相关推荐 -->
    <div class="related-section">
      <h3 class="section-title">相关推荐</h3>
      <div class="related-products">
        <div v-for="item in relatedProducts" :key="item.id" class="related-item" @click="viewProduct(item.id)">
          <img :src="item.image" :alt="item.name" />
          <div class="related-info">
            <h4>{{ item.name }}</h4>
            <p class="related-price">¥{{ item.price }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑商品弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="edit-modal" @click.stop>
        <div class="modal-header">
          <h3>编辑商品信息</h3>
          <button @click="closeEditModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>商品名称</label>
            <input v-model="editingProduct.name" type="text" class="form-input" placeholder="请输入商品名称">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>现价</label>
              <input v-model="editingProduct.price" type="number" class="form-input" placeholder="请输入价格">
            </div>
            <div class="form-group">
              <label>原价（可选）</label>
              <input v-model="editingProduct.originalPrice" type="number" class="form-input" placeholder="请输入原价">
            </div>
          </div>
          <div class="form-group">
            <label>商品数量</label>
            <input v-model="editingProduct.quantity" type="number" class="form-input" placeholder="请输入商品数量" min="1">
          </div>
          <div class="form-group">
            <label>商品描述</label>
            <textarea v-model="editingProduct.description" class="form-textarea" placeholder="请输入商品描述"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>成色</label>
              <select v-model="editingProduct.condition" class="form-select">
                <option value="全新">全新</option>
                <option value="几乎全新">几乎全新</option>
                <option value="轻微使用痕迹">轻微使用痕迹</option>
                <option value="明显使用痕迹">明显使用痕迹</option>
                <option value="重度使用痕迹">重度使用痕迹</option>
              </select>
            </div>
            <div class="form-group">
              <label>品牌</label>
              <input v-model="editingProduct.brand" type="text" class="form-input" placeholder="请输入品牌">
            </div>
          </div>
          <div class="form-group">
            <label>交易地点</label>
            <input v-model="editingProduct.location" type="text" class="form-input" placeholder="请输入交易地点">
          </div>
          <div class="form-group">
            <label>详细描述</label>
            <textarea v-model="editingProduct.detailDescription" class="form-textarea large" placeholder="请输入详细描述，每行一段"
              rows="6"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeEditModal" class="cancel-btn">取消</button>
          <button @click="saveProductChanges" class="save-btn">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCommodityDetail, transformCommodityDetailData } from '@/api/commodity'

export default {
  name: 'ProductDetail',
  data() {
    return {
      currentImageIndex: 0,
      isFavorited: false,
      isEditable: false,
      showEditModal: false,
      editingProduct: {},
      loading: true, // 添加加载状态
      error: null, // 添加错误状态
      // 评论相关数据
      newComment: '',
      replyContent: '',
      replyingTo: null,
      currentUser: {
        id: 1,
        name: '当前用户',
        school: '计算机学院',
        avatar: 'https://via.placeholder.com/40x40/4CAF50/FFFFFF?text=我'
      },
      comments: [
        {
          id: 1,
          user: {
            id: 2,
            name: '张同学',
            school: '音乐学院',
            avatar: 'https://via.placeholder.com/40x40/2196F3/FFFFFF?text=张'
          },
          content: '这个音箱效果器看起来不错，音质怎么样？',
          createTime: new Date('2024-01-15 14:30:00'),
          likeCount: 3,
          isLiked: false,
          replies: [
            {
              id: 101,
              user: {
                id: 3,
                name: '江城空空的铺子',
                school: '音乐学院',
                avatar: 'https://via.placeholder.com/40x40/4CAF50/FFFFFF?text=江'
              },
              content: '音质很棒，适合练琴和小型演出，失真效果特别好！',
              createTime: new Date('2024-01-15 15:00:00'),
              likeCount: 1,
              isLiked: false,
              replyTo: {
                id: 2,
                name: '张同学'
              }
            }
          ]
        },
        {
          id: 2,
          user: {
            id: 4,
            name: '李同学',
            school: '艺术学院',
            avatar: 'https://via.placeholder.com/40x40/FF9800/FFFFFF?text=李'
          },
          content: '价格很合理，比市面上便宜不少，成色也很好！',
          createTime: new Date('2024-01-15 16:20:00'),
          likeCount: 5,
          isLiked: true,
          replies: []
        }
      ],
      product: {
        // 初始化为空对象，将通过API获取
        id: '',
        name: '',
        price: 0,
        condition: '',
        publishTime: '',
        quantity: 1,
        description: '',
        detailDescription: [],
        images: ['/测试图片.jpg'],
        status: '',
        sellerId: '',
        seller: {
          id: '',
          name: '',
          avatar: ''
        }
      },
      relatedProducts: [
        {
          id: 2,
          name: '电吉他拾音器',
          price: 120,
          image: 'https://via.placeholder.com/150x150/FF9800/FFFFFF?text=拾音器'
        },
        {
          id: 3,
          name: '吉他调音器',
          price: 45,
          image: 'https://via.placeholder.com/150x150/E91E63/FFFFFF?text=调音器'
        },
        {
          id: 4,
          name: '音箱连接线',
          price: 25,
          image: 'https://via.placeholder.com/150x150/607D8B/FFFFFF?text=连接线'
        },
        {
          id: 5,
          name: '电子节拍器',
          price: 80,
          image: 'https://via.placeholder.com/150x150/795548/FFFFFF?text=节拍器'
        }
      ]
    }
  },
  computed: {
    currentImage() {
      return this.product.images && this.product.images.length > 0 ?
        this.product.images[this.currentImageIndex] : '/测试图片.jpg'
    }
  },
  async mounted() {
    // 获取路由参数中的商品ID
    const productId = this.$route.params.id
    console.log('商品ID:', productId)

    // 检查是否从个人资料页面进入
    this.isEditable = this.$route.query.from === 'profile' && this.$route.query.editable === 'true'

    // 获取商品详情
    if (productId) {
      await this.fetchProductDetail(productId)
    } else {
      this.error = '商品ID不存在'
      this.loading = false
    }
  },
  methods: {
    // 添加获取商品详情的方法
    async fetchProductDetail(commodityId) {
      try {
        this.loading = true
        this.error = null

        // 调用API获取商品详情
        const commodityData = await getCommodityDetail(commodityId)

        // 转换数据格式（现在是异步的）
        this.product = await transformCommodityDetailData(commodityData)

        console.log('获取到的商品详情:', this.product)

      } catch (error) {
        console.error('获取商品详情失败:', error)
        this.error = error.message || '获取商品详情失败'

        // 如果是404错误，显示商品不存在
        if (error.message.includes('404')) {
          this.error = '商品不存在或已被删除'
        }
      } finally {
        this.loading = false
      }
    },

    goBack() {
      this.$router.go(-1); // 返回上一页
    },
    selectImage(index) {
      this.currentImageIndex = index
    },
    contactSeller() {
      // 获取当前用户ID
      const userId = localStorage.getItem('userId');

      if (!userId) {
        // 如果用户未登录，提示登录
        alert('请先登录后联系卖家');
        this.$router.push('/login');
        return;
      }

      // 跳转到聊天列表页面
      // this.$router.push(`/chat-list/${userId}`);

      // 可选：如果需要直接创建与卖家的会话，可以在ChatList页面中处理
      // 或者可以传递卖家信息作为query参数
      // this.$router.push({
      //   path: `/chat-list/${userId}`,
      //   query: {
      //     sellerId: this.product.seller.id,
      //     sellerName: this.product.seller.name,
      //     productId: this.product.id
      //   }
      // });
      // 跳转到聊天页面
      this.$router.push({
        path: '/chat-list',
        query: {
          sellerId: this.product.sellerId,
          buyerId: userId,
          autoCreate: 'true'
        }
      })
    },
    buyNow() {
      alert('立即购买功能')
      // 实际项目中这里会跳转到订单确认页面
    },
    toggleFavorite() {
      this.isFavorited = !this.isFavorited
      // 实际项目中这里会调用收藏/取消收藏的API
    },
    viewProduct(productId) {
      this.$router.push(`/product/${productId}`)
    },

    // 编辑商品
    editProduct() {
      this.editingProduct = {
        id: this.product.id,
        name: this.product.name,
        price: this.product.price,
        originalPrice: this.product.originalPrice,
        description: this.product.description,
        condition: this.product.condition,
        brand: this.product.brand,
        location: this.product.location,
        detailDescription: this.product.detailDescription.join('\n')
      }
      this.showEditModal = true
    },

    // 关闭编辑弹窗
    closeEditModal() {
      this.showEditModal = false
      this.editingProduct = {}
    },

    // 保存商品信息
    saveProductChanges() {
      // 更新商品信息
      this.product.name = this.editingProduct.name
      this.product.price = this.editingProduct.price
      this.product.originalPrice = this.editingProduct.originalPrice
      this.product.description = this.editingProduct.description
      this.product.condition = this.editingProduct.condition
      this.product.brand = this.editingProduct.brand
      this.product.location = this.editingProduct.location
      this.product.detailDescription = this.editingProduct.detailDescription.split('\n').filter(line => line.trim())

      this.closeEditModal()

      // 实际项目中这里会调用API保存到后端
      alert('商品信息已更新！')
    },

    // 添加formatTime方法
    formatTime(time) {
      const now = new Date()
      const diff = now - new Date(time)
      const days = Math.floor(diff / 86400000)

      if (days === 0) {
        return '今天 ' + new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else if (days === 1) {
        return '昨天 ' + new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else {
        return new Date(time).toLocaleDateString('zh-CN')
      }
    },
    // 在methods中更新
    viewSellerProfile() {
      this.$router.push({
        path: `/sellerprofile/${this.product.sellerId}`,
        query: { from: 'productDetail' }
      });
    }
  }
}
</script>

<style scoped>
@import '../styles/ProductDetail.css';
</style>