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
        <button v-else ></button>
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
        </div>

        <h2 class="product-title">{{ product.name }}</h2>

        <!-- 商品详细信息 -->
        <div class="product-details">
          <div class="detail-item">
            <span class="label">成色：</span>
            <span class="value condition">{{ product.condition }}</span>
          </div>
          <div class="detail-item">
            <span class="label">发布时间：</span>
            <span class="value">{{ product.publishTime }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品数量：</span>
            <span class="value">{{ product.quantity }} 件</span>
          </div>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section">
          <div class="seller-header" @click="viewSellerProfile" style="cursor: pointer;">
            <h3>卖家信息</h3>
          </div>
          <div class="seller-info" @click="viewSellerProfile" style="cursor: pointer;">
            <img :src="product.seller.avatar" class="seller-avatar" />
            <div class="seller-details">
              <div class="seller-name">{{ product.seller.name }}</div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="btn-contact" @click="contactSeller" :disabled="isOwner">
            💬 {{ isOwner ? '您是卖家' : '联系卖家' }}
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
      <h3 class="section-title">评论 ({{ commentTotal }})</h3>

      <!-- 发表评论 -->
      <div class="comment-form">
        <div class="comment-input-area">
          <img :src="currentUser.avatar" class="user-avatar" />
          <div class="input-container">
            <textarea v-model="newComment" placeholder="写下你的评论..." class="comment-input" rows="3" maxlength="2000"
              :disabled="submittingComment"></textarea>
            <div class="input-footer">
              <span class="char-count">{{ newComment.length }}/2000</span>
              <button @click="submitComment" :disabled="!newComment.trim() || submittingComment" class="submit-btn">
                {{ submittingComment ? '发表中...' : '发表评论' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-if="loadingComments" class="loading-comments">
          <div class="loading-spinner"></div>
          <p>正在加载评论...</p>
        </div>

        <div v-else-if="comments.length === 0" class="no-comments">
          <div class="no-comments-icon">💬</div>
          <p>暂无评论，快来发表第一条评论吧！</p>
        </div>

        <div v-else>
          <div v-for="comment in comments" :key="comment.message_id" class="comment-item">
            <img :src="getUserAvatar(comment.user_id)" class="comment-avatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ getUserName(comment.user_id) }}</span>
                <span v-if="comment.user_id === product.sellerId" class="seller-tag">卖家</span>
                <span class="comment-time">{{ formatTime(comment.created_at) }}</span>
              </div>
              <div class="comment-text">{{ comment.message }}</div>
              <div class="comment-actions">
                <button @click="showReplyInput(comment.message_id)" class="action-btn">
                  💬 回复
                </button>
                <button v-if="canDeleteComment(comment.user_id)" @click="deleteComment(comment.message_id)"
                  class="action-btn delete-btn">
                  🗑️ 删除
                </button>
              </div>

              <!-- 回复输入框 -->
              <div v-if="replyingTo === comment.message_id" class="reply-input-area">
                <img :src="currentUser.avatar" class="user-avatar small" />
                <div class="input-container">
                  <textarea v-model="replyContent" :placeholder="`回复 ${getUserName(comment.user_id)}...`"
                    class="reply-input" rows="2" maxlength="2000" :disabled="submittingReply"></textarea>
                  <div class="input-footer">
                    <span class="char-count">{{ replyContent.length }}/2000</span>
                    <div class="reply-actions">
                      <button @click="cancelReply" class="cancel-btn">取消</button>
                      <button @click="submitReply(comment.message_id)"
                        :disabled="!replyContent.trim() || submittingReply" class="submit-btn">
                        {{ submittingReply ? '回复中...' : '回复' }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                <div v-for="reply in comment.replies" :key="reply.message_id" class="reply-item">
                  <img :src="getUserAvatar(reply.user_id)" class="reply-avatar" />
                  <div class="reply-content">
                    <div class="reply-header">
                      <span class="reply-author">{{ getUserName(reply.user_id) }}</span>
                      <span v-if="reply.user_id === product.sellerId" class="seller-tag">卖家</span>
                      <span class="reply-time">{{ formatTime(reply.created_at) }}</span>
                    </div>
                    <div class="reply-text">{{ reply.message }}</div>
                    <div class="reply-actions">
                      <button @click="replyToReply(comment.message_id, reply)" class="action-btn">
                        💬 回复
                      </button>
                      <button v-if="canDeleteComment(reply.user_id)" @click="deleteComment(reply.message_id)"
                        class="action-btn delete-btn">
                        🗑️ 删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="commentTotal > commentPageSize" class="pagination">
            <button @click="loadComments(currentPage - 1)" :disabled="currentPage <= 1 || loadingComments"
              class="page-btn">
              上一页
            </button>
            <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
            <button @click="loadComments(currentPage + 1)" :disabled="currentPage >= totalPages || loadingComments"
              class="page-btn">
              下一页
            </button>
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
          <div class="form-group">
            <label>商品价格</label>
            <input v-model="editingProduct.price" type="number" class="form-input" placeholder="请输入价格">
          </div>
          <div class="form-group">
            <label>商品数量</label>
            <input v-model="editingProduct.quantity" type="number" class="form-input" placeholder="请输入商品数量" min="1">
          </div>
          <div class="form-group">
            <label>商品描述</label>
            <textarea v-model="editingProduct.description" class="form-textarea" placeholder="请输入商品描述"></textarea>
          </div>
          <div class="form-group">
            <label>成色</label>
            <select v-model="editingProduct.condition" class="form-select">
              <option value="全新">全新</option>
              <option value="95新">95新</option>
              <option value="9成新">9成新</option>
              <option value="8成新">8成新</option>
              <option value="7成新">7成新</option>
            </select>
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
import axios from 'axios'
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
      comments: [],
      commentTotal: 0,
      currentPage: 1,
      commentPageSize: 10,
      totalPages: 0,
      loadingComments: false,

      // 评论输入
      newComment: '',
      replyContent: '',
      replyingTo: null,
      submittingComment: false,
      submittingReply: false,

      // 用户信息缓存
      userCache: {},

      currentUser: {
        id: localStorage.getItem('userId') || '',
        name: '当前用户',
        avatar: 'https://via.placeholder.com/40x40/4CAF50/FFFFFF?text=我'
      },

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
    },
    isOwner() {
      // 检查当前用户是否是卖家
      return this.currentUser.id && this.currentUser.id === this.product.sellerId;
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
      await this.loadComments(1)
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

    // 加载评论列表
    async loadComments(page = 1) {
      try {
        this.loadingComments = true

        const response = await axios.get('http://localhost:8091/api/v1/comments', {
          params: {
            commodity_id: this.product.id,
            page: page,
            page_size: this.commentPageSize,
            sort_by: 'created_at',
            order: 'desc'
          }
        })

        if (response.data && response.data.data) {
          this.comments = response.data.data.comments || []
          this.commentTotal = response.data.data.total || 0
          this.currentPage = response.data.data.page || 1
          this.totalPages = response.data.data.total_pages || 0

          // 预加载用户信息
          await this.preloadUserInfo()
        }
      } catch (error) {
        console.error('加载评论失败:', error)
        this.$message?.error('加载评论失败')
      } finally {
        this.loadingComments = false
      }
    },

    // 预加载用户信息
    async preloadUserInfo() {
      const userIds = new Set()

      // 收集所有用户ID
      this.comments.forEach(comment => {
        userIds.add(comment.user_id)
        if (comment.replies) {
          comment.replies.forEach(reply => {
            userIds.add(reply.user_id)
          })
        }
      })

      // 批量获取用户信息
      for (const userId of userIds) {
        if (!this.userCache[userId]) {
          try {
            // 调用后端用户基本信息API
            const response = await axios.post('http://localhost:8089/api/user/basic', {
              userId: userId
            })

            if (response.data && response.data.success && response.data.data) {
              const userData = response.data.data
              this.userCache[userId] = {
                name: userData.userName || `用户${userId}`,
                avatar: userData.avatarUrl || `/测试图片.jpg`
              }
            } else {
              // API返回失败，使用默认信息
              this.userCache[userId] = {
                name: `用户${userId}`,
                avatar: `/测试图片.jpg`
              }
            }
          } catch (error) {
            console.error(`获取用户${userId}信息失败:`, error)
            // 如果API调用失败，使用默认信息
            this.userCache[userId] = {
              name: `用户${userId}`,
              avatar: `/测试图片.jpg`
            }
          }
        }
      }
    },

    // 获取用户名称
    getUserName(userId) {
      return this.userCache[userId]?.name || `用户${userId}`
    },

    // 获取用户头像
    getUserAvatar(userId) {
      return this.userCache[userId]?.avatar || 'https://via.placeholder.com/40x40/999999/FFFFFF?text=?'
    },

    // 提交评论
    async submitComment() {
      if (!this.newComment.trim()) return
      if (!this.currentUser.id) {
        this.$message?.error('请先登录')
        return
      }

      try {
        this.submittingComment = true

        const response = await axios.post('http://localhost:8091/api/v1/comments', {
          commodity_id: this.product.id,
          user_id: this.currentUser.id,
          message: this.newComment.trim()
        })

        if (response.data && response.data.data) {
          this.newComment = ''
          await this.loadComments(1) // 重新加载第一页评论
          this.$message?.success('评论发表成功')
        }
      } catch (error) {
        console.error('发表评论失败:', error)
        this.$message?.error('发表评论失败')
      } finally {
        this.submittingComment = false
      }
    },

    // 显示回复输入框
    showReplyInput(messageId) {
      this.replyingTo = messageId
      this.replyContent = ''
    },

    // 取消回复
    cancelReply() {
      this.replyingTo = null
      this.replyContent = ''
    },

    // 提交回复
    async submitReply(parentMessageId) {
      if (!this.replyContent.trim()) return
      if (!this.currentUser.id) {
        this.$message?.error('请先登录')
        return
      }

      try {
        this.submittingReply = true

        const response = await axios.post('http://localhost:8091/api/v1/comments', {
          commodity_id: this.product.id,
          user_id: this.currentUser.id,
          message: this.replyContent.trim(),
          reply_to_message_id: parentMessageId
        })

        if (response.data && response.data.data) {
          this.cancelReply()
          await this.loadComments(this.currentPage) // 重新加载当前页评论
          this.$message?.success('回复发表成功')
        }
      } catch (error) {
        console.error('发表回复失败:', error)
        this.$message?.error('发表回复失败')
      } finally {
        this.submittingReply = false
      }
    },

    // 回复某个回复
    replyToReply(parentMessageId, reply) {
      this.replyingTo = parentMessageId
      this.replyContent = `@${this.getUserName(reply.user_id)} `
    },

    // 删除评论
    async deleteComment(messageId) {
      if (!confirm('确定要删除这条评论吗？')) return

      try {
        await axios.delete(`http://localhost:8091/api/v1/comments/${messageId}`, {
          params: {
            user_id: this.currentUser.id
          }
        })

        await this.loadComments(this.currentPage)
        this.$message?.success('评论删除成功')
      } catch (error) {
        console.error('删除评论失败:', error)
        this.$message?.error('删除评论失败')
      }
    },

    // 检查是否可以删除评论
    canDeleteComment(commentUserId) {
      return this.currentUser.id === commentUserId
    },

    // 格式化时间
    formatTime(timeString) {
      const date = new Date(timeString)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
      } else if (diff < 86400000) { // 24小时内
        return `${Math.floor(diff / 3600000)}小时前`
      } else if (diff < 604800000) { // 7天内
        return `${Math.floor(diff / 86400000)}天前`
      } else {
        return date.toLocaleDateString()
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