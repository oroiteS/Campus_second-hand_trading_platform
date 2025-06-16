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
      // 跳转到聊天页面，传递商品和卖家信息
      this.$router.push({
        path: '/chat',
        query: {
          productId: this.product.id,
          sellerId: this.product.seller.id,
          sellerName: this.product.seller.name,
          sellerSchool: this.product.seller.school
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
.product-detail {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
  }
  
  /* 顶部导航 */
  .detail-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-bottom: none;
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
    backdrop-filter: blur(10px);
  }
  
  .header-btn:hover {
    background: rgba(255,255,255,0.3);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  }
  
  .page-title {
    font-size: 18px;
    font-weight: bold;
    color: white;
    margin: 0;
    text-shadow: 0 1px 3px rgba(0,0,0,0.3);
  }
  
  /* 主要内容区域 */
  .detail-container {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 40px;
    padding: 30px 20px;
  }
  
  /* 商品图片区域 */
  .product-images {
    background: white;
    border-radius: 16px;
    padding: 25px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.2);
  }
  
  .main-image {
    margin-bottom: 20px;
  }
  
  .main-img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    border-radius: 12px;
    border: 1px solid #e0e0e0;
    transition: transform 0.3s;
  }
  
  .main-img:hover {
    transform: scale(1.02);
  }
  
  .thumbnail-list {
    display: flex;
    gap: 12px;
    overflow-x: auto;
    padding: 5px 0;
  }
  
  .thumbnail {
    flex-shrink: 0;
    width: 80px;
    height: 80px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 3px solid transparent;
    transition: all 0.3s;
  }
  
  .thumbnail.active {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  }
  
  .thumbnail:hover {
    transform: scale(1.05);
  }
  
  .thumbnail img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  /* 商品信息区域 */
  .product-info {
    background: white;
    border-radius: 16px;
    padding: 30px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.2);
    height: fit-content;
  }
  
  .price-section {
    margin-bottom: 20px;
    padding: 20px;
    background: linear-gradient(135deg, #FFE4E1 0%, #FFF0F5 100%);
    border-radius: 12px;
    border-left: 4px solid #FF6B35;
  }
  
  .price-main {
    display: flex;
    align-items: baseline;
    margin-bottom: 5px;
  }
  
  .currency {
    font-size: 24px;
    color: #FF6B35;
    font-weight: bold;
    margin-right: 5px;
  }
  
  .price {
    font-size: 36px;
    color: #FF6B35;
    font-weight: bold;
  }
  
  .price-original {
    font-size: 14px;
    color: #999;
    text-decoration: line-through;
  }
  
  .product-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
    line-height: 1.4;
  }
  
  .product-description {
    color: #666;
    line-height: 1.6;
    margin-bottom: 25px;
    padding: 15px;
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-radius: 12px;
    border-left: 4px solid #667eea;
  }
  
  .product-details {
    margin-bottom: 30px;
  }
  
  .detail-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .detail-item:last-child {
    border-bottom: none;
  }
  
  .detail-item .label {
    font-weight: 500;
    color: #666;
    width: 100px;
    flex-shrink: 0;
  }
  
  .detail-item .value {
    color: #333;
  }
  
  .condition {
    background: linear-gradient(135deg, #E8F5E8 0%, #C8E6C9 100%);
    color: #2E7D32;
    padding: 4px 12px;
    border-radius: 15px;
    font-size: 12px;
    font-weight: bold;
  }
  
  .location {
    color: #667eea;
    font-weight: 500;
  }
  
  /* 卖家信息 */
  .seller-section {
    border: 1px solid #e0e0e0;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 30px;
    background: linear-gradient(135deg, #fafafa 0%, #f0f0f0 100%);
    box-shadow: 0 4px 16px rgba(0,0,0,0.05);
  }
  
  .seller-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .seller-header h3 {
    margin: 0;
    color: #333;
    font-size: 16px;
  }
  
  .seller-badge {
    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
    color: white;
    padding: 6px 16px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: bold;
    box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
  }
  
  .seller-info {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  
  .seller-avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid #e0e0e0;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
  
  .seller-details {
    flex: 1;
  }
  
  .seller-name {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 5px;
  }
  
  .seller-school {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .seller-stats {
    display: flex;
    gap: 15px;
    font-size: 12px;
    color: #999;
  }
  
  /* 操作按钮 */
  .action-buttons {
    display: grid;
    grid-template-columns: 1fr 1fr 80px;
    gap: 15px;
  }
  
  .action-buttons button {
    padding: 15px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  }
  
  .btn-contact {
    background: linear-gradient(135deg, #FFD700 0%, #FFC107 100%);
    color: #333;
  }
  
  .btn-contact:hover {
    background: linear-gradient(135deg, #FFC107 0%, #FF8F00 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
  }
  
  .btn-buy {
    background: linear-gradient(135deg, #FF6B35 0%, #E55A2B 100%);
    color: white;
  }
  
  .btn-buy:hover {
    background: linear-gradient(135deg, #E55A2B 0%, #D84315 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
  }
  
  .btn-favorite {
    background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
    color: #666;
    font-size: 20px;
  }
  
  .btn-favorite.active {
    background: linear-gradient(135deg, #FFE4E1 0%, #FFCDD2 100%);
    color: #FF69B4;
  }
  
  .btn-favorite:hover {
    background: linear-gradient(135deg, #e0e0e0 0%, #d0d0d0 100%);
    transform: translateY(-2px);
  }
  
  /* 详细描述区域 */
  .description-section, .related-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 30px;
  }
  
  .section-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 3px solid #667eea;
    position: relative;
  }
  
  .section-title::after {
    content: '';
    position: absolute;
    bottom: -3px;
    left: 0;
    width: 60px;
    height: 3px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
  }
  
  .description-content {
    background: white;
    border-radius: 16px;
    padding: 25px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.2);
    line-height: 1.8;
    color: #666;
  }
  
  .description-content p {
    margin-bottom: 15px;
  }
  
  .description-content p:last-child {
    margin-bottom: 0;
  }
  
  /* 相关推荐 */
  .related-products {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
  }
  
  .related-item {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.2);
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .related-item:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(0,0,0,0.15);
  }
  
  .related-item img {
    width: 100%;
    height: 150px;
    object-fit: cover;
  }
  
  .related-info {
    padding: 15px;
  }
  
  .related-info h4 {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    line-height: 1.4;
  }
  
  .related-price {
    font-size: 16px;
    font-weight: bold;
    color: #FF6B35;
    margin: 0;
  }
  
  /* 评论区域样式 */
  .comments-section {
    max-width: 1200px;
    margin: 30px auto;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    overflow: hidden;
  }
  
  .comments-section .section-title {
    padding: 20px 30px;
    margin: 0;
    border-bottom: 1px solid #eee;
    background: #fafafa;
  }
  
  /* 评论表单 */
  .comment-form {
    padding: 20px 30px;
    border-bottom: 1px solid #eee;
  }
  
  .comment-input-area {
    display: flex;
    gap: 12px;
  }
  
  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
  }
  
  .user-avatar.small {
    width: 32px;
    height: 32px;
  }
  
  .input-container {
    flex: 1;
  }
  
  .comment-input,
  .reply-input {
    width: 100%;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 12px;
    font-size: 14px;
    resize: vertical;
    min-height: 80px;
    font-family: inherit;
  }
  
  .comment-input:focus,
  .reply-input:focus {
    outline: none;
    border-color: #4CAF50;
    box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.1);
  }
  
  .input-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 8px;
  }
  
  .char-count {
    font-size: 12px;
    color: #999;
  }
  
  .submit-btn {
    background: #4CAF50;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    transition: background 0.3s;
  }
  
  .submit-btn:hover:not(:disabled) {
    background: #45a049;
  }
  
  .submit-btn:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
  
  .cancel-btn {
    background: #f5f5f5;
    color: #666;
    border: none;
    padding: 8px 16px;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    margin-right: 8px;
    transition: background 0.3s;
  }
  
  .cancel-btn:hover {
    background: #e0e0e0;
  }
  
  /* 评论列表 */
  .comments-list {
    padding: 20px 30px;
  }
  
  .no-comments {
    text-align: center;
    padding: 40px 20px;
    color: #999;
  }
  
  .no-comments-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  .comment-item {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .comment-item:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
  
  .comment-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
  }
  
  .comment-content {
    flex: 1;
  }
  
  .comment-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
  
  .comment-author {
    font-weight: bold;
    color: #333;
  }
  
  .comment-school {
    font-size: 12px;
    color: #666;
    background: #f0f0f0;
    padding: 2px 8px;
    border-radius: 10px;
  }
  
  .comment-time {
    font-size: 12px;
    color: #999;
    margin-left: auto;
  }
  
  .comment-text {
    color: #333;
    line-height: 1.6;
    margin-bottom: 12px;
  }
  
  .comment-actions {
    display: flex;
    gap: 16px;
  }
  
  .action-btn {
    background: none;
    border: none;
    color: #666;
    cursor: pointer;
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 12px;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    gap: 4px;
  }
  
  .action-btn:hover {
    background: #f5f5f5;
    color: #333;
  }
  
  .action-btn.liked {
    color: #e91e63;
  }
  
  /* 回复区域 */
  .reply-input-area {
    margin-top: 16px;
    display: flex;
    gap: 8px;
  }
  
  .reply-actions {
    display: flex;
    gap: 8px;
  }
  
  .replies-list {
    margin-top: 16px;
    padding-left: 20px;
    border-left: 2px solid #f0f0f0;
  }
  
  .reply-item {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
  }
  
  .reply-item:last-child {
    margin-bottom: 0;
  }
  
  .reply-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
  }
  
  .reply-content {
    flex: 1;
  }
  
  .reply-header {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 6px;
  }
  
  .reply-author {
    font-weight: bold;
    color: #333;
    font-size: 14px;
  }
  
  .reply-school {
    font-size: 11px;
    color: #666;
    background: #f0f0f0;
    padding: 1px 6px;
    border-radius: 8px;
  }
  
  .reply-time {
    font-size: 11px;
    color: #999;
    margin-left: auto;
  }
  
  .reply-text {
    color: #333;
    line-height: 1.5;
    margin-bottom: 8px;
    font-size: 14px;
  }
  
  .reply-target {
    color: #4CAF50;
    font-weight: bold;
    margin-right: 4px;
  }
  
  .reply-actions {
    display: flex;
    gap: 12px;
  }
  
  .reply-actions .action-btn {
      font-size: 11px;
      padding: 2px 6px;
    }
  
  /* 编辑按钮样式 */
  .edit-btn {
    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
    color: white;
  }
  
  .edit-btn:hover {
    background: linear-gradient(135deg, #45a049 0%, #3d8b40 100%);
  }
  
  /* 编辑弹窗样式 */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .edit-modal {
    background: white;
    border-radius: 12px;
    width: 90%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
  }
  
  .modal-header {
    padding: 20px;
    border-bottom: 1px solid #e0e0e0;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .modal-header h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
  }
  
  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #999;
    padding: 0;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .close-btn:hover {
    color: #333;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
  }
  
  .form-input, .form-textarea, .form-select {
    width: 100%;
    padding: 12px;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    font-size: 14px;
    transition: border-color 0.3s;
    box-sizing: border-box;
  }
  
  .form-input:focus, .form-textarea:focus, .form-select:focus {
    outline: none;
    border-color: #667eea;
  }
  
  .form-textarea {
    height: 80px;
    resize: vertical;
  }
  
  .form-textarea.large {
    height: 120px;
  }
  
  .modal-footer {
    padding: 20px;
    border-top: 1px solid #e0e0e0;
    display: flex;
    gap: 10px;
    justify-content: flex-end;
  }
  
  .cancel-btn, .save-btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .cancel-btn {
    background: #f5f5f5;
    color: #666;
  }
  
  .cancel-btn:hover {
    background: #e0e0e0;
  }
  
  .save-btn {
    background: #667eea;
    color: white;
  }
  
  .save-btn:hover {
    background: #5a6fd8;
  }
  
    /* 响应式设计 */
  @media (max-width: 768px) {
    .detail-container {
      grid-template-columns: 1fr;
      gap: 20px;
      padding: 20px 15px;
    }
    
    .action-buttons {
      grid-template-columns: 1fr;
      gap: 10px;
    }
    
    .btn-favorite {
      order: -1;
    }
    
    .related-products {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .main-img {
      height: 300px;
    }
    
    .price {
      font-size: 28px;
    }
    
    .product-title {
      font-size: 20px;
    }
    
    .header-content {
      padding: 12px 15px;
    }
    
    .header-btn {
      padding: 8px 16px;
      font-size: 13px;
    }
    
    .comments-section {
      margin: 20px;
      border-radius: 8px;
    }
    
    .comment-form,
    .comments-list {
      padding: 16px 20px;
    }
    
    .comments-section .section-title {
      padding: 16px 20px;
    }
    
    .comment-input-area,
    .reply-input-area {
      gap: 8px;
    }
    
    .user-avatar {
      width: 36px;
      height: 36px;
    }
    
    .comment-avatar {
      width: 36px;
      height: 36px;
    }
    
    .reply-avatar {
        width: 28px;
        height: 28px;
      }
      
      .edit-modal {
        width: 95%;
        margin: 20px;
      }
      
      .form-row {
        grid-template-columns: 1fr;
      }
    }
</style>