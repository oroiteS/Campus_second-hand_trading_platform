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

    <div class="detail-container">
      <!-- 左侧：商品图片 -->
      <div class="product-images">
        <div class="main-image">
          <img :src="currentImage" :alt="product.name" class="main-img" />
        </div>
        <div class="thumbnail-list">
          <div 
            v-for="(image, index) in product.images" 
            :key="index"
            class="thumbnail"
            :class="{ active: currentImageIndex === index }"
            @click="selectImage(index)"
          >
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
          <div class="price-original" v-if="product.originalPrice">
            原价：¥{{ product.originalPrice }}
          </div>
        </div>

        <h2 class="product-title">{{ product.name }}</h2>
        
        <!-- 商品描述 -->
        <div class="product-description">
          <p>{{ product.description }}</p>
        </div>

        <!-- 商品详细信息 -->
        <div class="product-details">
          <div class="detail-item">
            <span class="label">成色：</span>
            <span class="value condition">{{ product.condition }}</span>
          </div>
          <div class="detail-item">
            <span class="label">品牌：</span>
            <span class="value">{{ product.brand }}</span>
          </div>
          <div class="detail-item">
            <span class="label">交易地点：</span>
            <span class="value location">📍 {{ product.location }}</span>
          </div>
          <div class="detail-item">
            <span class="label">发布时间：</span>
            <span class="value">{{ product.publishTime }}</span>
          </div>
          <div class="detail-item">
            <span class="label">浏览次数：</span>
            <span class="value">{{ product.viewCount }} 次</span>
          </div>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section">
          <div class="seller-header">
            <h3>卖家信息</h3>
            <span class="seller-badge">{{ product.seller.badge }}</span>
          </div>
          <div class="seller-info">
            <img :src="product.seller.avatar" class="seller-avatar" />
            <div class="seller-details">
              <div class="seller-name">{{ product.seller.name }}</div>
              <div class="seller-school">{{ product.seller.school }}</div>
              <div class="seller-stats">
                <span>信用评分：{{ product.seller.creditScore }}</span>
                <span>成交：{{ product.seller.dealCount }}笔</span>
              </div>
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
            <textarea 
              v-model="newComment" 
              placeholder="写下你的评论..." 
              class="comment-input"
              rows="3"
              maxlength="500"
            ></textarea>
            <div class="input-footer">
              <span class="char-count">{{ newComment.length }}/500</span>
              <button 
                @click="submitComment" 
                :disabled="!newComment.trim()" 
                class="submit-btn"
              >
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
              <button 
                @click="toggleLike(comment)" 
                :class="['action-btn', { liked: comment.isLiked }]"
              >
                {{ comment.isLiked ? '❤️' : '🤍' }} {{ comment.likeCount }}
              </button>
              <button 
                @click="showReplyInput(comment.id)" 
                class="action-btn"
              >
                💬 回复
              </button>
            </div>
            
            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="reply-input-area">
              <img :src="currentUser.avatar" class="user-avatar small" />
              <div class="input-container">
                <textarea 
                  v-model="replyContent" 
                  :placeholder="`回复 ${comment.user.name}...`" 
                  class="reply-input"
                  rows="2"
                  maxlength="300"
                ></textarea>
                <div class="input-footer">
                  <span class="char-count">{{ replyContent.length }}/300</span>
                  <div class="reply-actions">
                    <button @click="cancelReply" class="cancel-btn">取消</button>
                    <button 
                      @click="submitReply(comment.id)" 
                      :disabled="!replyContent.trim()" 
                      class="submit-btn"
                    >
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
                    <button 
                      @click="toggleReplyLike(reply)" 
                      :class="['action-btn', { liked: reply.isLiked }]"
                    >
                      {{ reply.isLiked ? '❤️' : '🤍' }} {{ reply.likeCount }}
                    </button>
                    <button 
                      @click="replyToReply(comment.id, reply)" 
                      class="action-btn"
                    >
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
        <div 
          v-for="item in relatedProducts" 
          :key="item.id"
          class="related-item"
          @click="viewProduct(item.id)"
        >
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
            <textarea v-model="editingProduct.detailDescription" class="form-textarea large" placeholder="请输入详细描述，每行一段" rows="6"></textarea>
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
export default {
  name: 'ProductDetail',
  data() {
    return {
      currentImageIndex: 0,
      isFavorited: false,
      isEditable: false, // 是否可编辑
      showEditModal: false, // 是否显示编辑弹窗
      editingProduct: {}, // 编辑中的商品信息
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
        id: 1,
        name: 'JOYO JAM BUDDY电吉他音箱效果器',
        price: 350,
        originalPrice: 500,
        condition: '9成新',
        brand: 'JOYO',
        location: '东校区宿舍',
        publishTime: '2024年11月14日',
        viewCount: 128,
        description: 'JOYO JAM BUDDY电吉他音箱效果器，这个小巧的设备是卓越的爆款产品，适合练琴和演出',
        detailDescription: [
          '功能正常，九五新，配9v电源，原厂包装都在，标价就是卖价，不议价，议价不回复......',
          '需要直接拍了发出......邮费到付，签收不退！',
          '这是一款非常适合初学者和专业音乐人的便携式音箱效果器，音质清晰，功能齐全。'
        ],
        images: [
          '/测试图片.jpg',
          'https://via.placeholder.com/400x300/FF6B35/FFFFFF?text=图片2',
          'https://via.placeholder.com/400x300/4CAF50/FFFFFF?text=图片3',
          'https://via.placeholder.com/400x300/2196F3/FFFFFF?text=图片4',
          'https://via.placeholder.com/400x300/9C27B0/FFFFFF?text=图片5'
        ],
        seller: {
          name: '江城空空的铺子',
          school: '音乐学院',
          avatar: 'https://via.placeholder.com/60x60/4CAF50/FFFFFF?text=江',
          badge: '几乎全新',
          creditScore: '98%',
          dealCount: 47
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
      return this.product.images[this.currentImageIndex]
    }
  },
  mounted() {
    // 根据路由参数获取商品ID，实际项目中这里会调用API获取商品详情
    const productId = this.$route.params.id
    console.log('商品ID:', productId)
    
    // 检查是否从个人资料页面进入，决定是否显示编辑功能
    this.isEditable = this.$route.query.from === 'profile' && this.$route.query.editable === 'true'
    console.log('是否可编辑:', this.isEditable)
    
    // this.fetchProductDetail(productId)
  },
  methods: {
    goBack() {
      this.$router.go(-1); // 返回上一页
    },
    selectImage(index) {
      this.currentImageIndex = index
    },
    contactSeller() {
      // 获取从Home.vue传递过来的userId
      const userId = this.$route.query.userId;
      
      // 跳转到聊天列表页面，使用路径参数传递userId
      this.$router.push({
        path: `/chat-list/${userId}`,
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
    
    // ... existing code ...
    
    // 评论相关方法
    submitComment() {
      if (!this.newComment.trim()) return
      
      const comment = {
        id: Date.now(),
        user: { ...this.currentUser },
        content: this.newComment.trim(),
        createTime: new Date(),
        likeCount: 0,
        isLiked: false,
        replies: []
      }
      
      this.comments.unshift(comment)
      this.newComment = ''
      
      // 实际项目中这里会调用API提交评论
      this.$message?.success('评论发表成功！')
    },
    
    showReplyInput(commentId) {
      this.replyingTo = this.replyingTo === commentId ? null : commentId
      this.replyContent = ''
    },
    
    cancelReply() {
      this.replyingTo = null
      this.replyContent = ''
    },
    
    submitReply(commentId) {
      if (!this.replyContent.trim()) return
      
      const comment = this.comments.find(c => c.id === commentId)
      if (!comment) return
      
      const reply = {
        id: Date.now(),
        user: { ...this.currentUser },
        content: this.replyContent.trim(),
        createTime: new Date(),
        likeCount: 0,
        isLiked: false,
        replyTo: {
          id: comment.user.id,
          name: comment.user.name
        }
      }
      
      if (!comment.replies) {
        comment.replies = []
      }
      comment.replies.push(reply)
      
      this.cancelReply()
      
      // 实际项目中这里会调用API提交回复
      this.$message?.success('回复发表成功！')
    },
    
    replyToReply(commentId, targetReply) {
      this.replyingTo = commentId
      this.replyContent = `@${targetReply.user.name} `
    },
    
    toggleLike(comment) {
      comment.isLiked = !comment.isLiked
      comment.likeCount += comment.isLiked ? 1 : -1
      
      // 实际项目中这里会调用API
    },
    
    toggleReplyLike(reply) {
      reply.isLiked = !reply.isLiked
      reply.likeCount += reply.isLiked ? 1 : -1
      
      // 实际项目中这里会调用API
    },
    
    formatTime(time) {
      const now = new Date()
      const diff = now - new Date(time)
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      
      return new Date(time).toLocaleDateString()
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
      this.product.detailDescription = this.editingProduct.detailDescription.split('\n').filter(p => p.trim())
      
      this.closeEditModal()
      
      // 实际项目中这里会调用API保存到后端
      alert('商品信息已更新！')
    }
  }
}
</script>

<style scoped>
@import '../styles/ProductDetail.css';
</style>