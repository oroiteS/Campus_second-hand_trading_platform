<template>
  <div class="chat-container">
    <!-- 左侧聊天列表 -->
    <div class="chat-list">
      <div class="chat-header">
        <h3>消息</h3>
        <div class="user-info">
          <span>{{ currentUser.user_name }}</span>
        </div>
      </div>

      <div class="session-list">
        <div v-for="session in sessions" :key="session.session_id" class="session-item"
          :class="{ active: selectedSessionId === session.session_id }" @click="selectSession(session)">
          <div class="avatar">
            <img :src="getOtherUser(session).avatar_url || '/default-avatar.png'" alt="头像">
          </div>
          <div class="session-info">
            <div class="name">{{ getOtherUser(session).user_name }}</div>
            <div class="last-message">{{ session.lastMessage || '暂无消息' }}</div>
          </div>
          <div class="session-meta">
            <div class="time">{{ formatTime(session.updated_at) }}</div>
            <div v-if="session.unreadCount > 0" class="unread-badge">
              {{ session.unreadCount }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天界面 -->
    <div class="chat-content">
      <div v-if="selectedSession" class="chat-window">
        <div class="chat-header">
          <h4>{{ getOtherUser(selectedSession).user_name }}</h4>
          <!-- 移除在线状态显示 -->
        </div>

        <!-- 消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-for="message in messages" :key="message.message_id" class="message"
            :class="{ 'own-message': message.sender_id === currentUser.user_id }">
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-info">
                <span class="message-time">{{ formatTime(message.sent_at) }}</span>
                <!-- 添加已读状态显示 -->
                <span v-if="message.sender_id === currentUser.user_id" class="read-status">
                  {{ message.read_status ? '已读' : '未读' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 错误提示气泡 -->
        <div v-if="errorMessage" class="error-bubble">
          <span>{{ errorMessage }}</span>
          <button @click="closeErrorMessage" class="close-btn">×</button>
        </div>

        <!-- 输入框 -->
        <div class="input-area">
          <input v-model="newMessage" @keyup.enter="sendMessage" placeholder="输入消息..." class="message-input">
          <button @click="sendMessage" class="send-button">发送</button>
        </div>
      </div>

      <div v-else class="no-session">
        <p>请选择一个会话开始聊天</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatList',
  data() {
    return {
      currentUser: {},
      sessions: [],
      selectedSession: null,
      selectedSessionId: null,
      messages: [],
      newMessage: '',
      ws: null,
      errorMessage: '', // 添加错误消息状态
      // 敏感词列表
      sensitiveWords: [
        // 辱骂词汇
        '辱骂',
        //政治
        '政治'
      ]
    }
  },

  async mounted() {
    await this.loadCurrentUser()
    await this.loadSessions()

    // 处理从商品详情页跳转过来的情况
    await this.handleProductChat()

    this.connectWebSocket()
  },

  methods: {
    async loadCurrentUser() {
      // 优先从路由参数获取，然后从localStorage获取
      const userId = this.$route.query.buyerId ||
        this.$route.params.userId ||
        localStorage.getItem('currentUserId')

      if (!userId) {
        this.$router.push('/login')
        return
      }

      try {
        const response = await fetch(`http://localhost:8088/api/v1/chat/user/${userId}/info`)
        if (response.ok) {
          this.currentUser = await response.json()
          // 保存到localStorage
          localStorage.setItem('currentUserId', this.currentUser.user_id)
        } else {
          throw new Error('获取用户信息失败')
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.$router.push('/login')
      }
    },

    async loadSessions() {
      try {
        const response = await fetch(`http://localhost:8088/api/v1/chat/user/${this.currentUser.user_id}/sessions`)
        this.sessions = await response.json()

        // 为每个会话加载最后一条消息和未读数量
        for (let session of this.sessions) {
          await this.loadSessionLastMessage(session)
          await this.loadSessionUnreadCount(session)
        }
      } catch (error) {
        console.error('获取会话列表失败:', error)
      }
    },

    async loadSessionLastMessage(session) {
      try {
        const response = await fetch(`http://localhost:8088/api/v1/chat/session/${session.session_id}/messages?limit=1&offset=0`)
        const messages = await response.json()
        if (messages.length > 0) {
          session.lastMessage = messages[0].content
        }
      } catch (error) {
        console.error('获取最后消息失败:', error)
      }
    },

    // 修复：获取单个会话的未读消息数量
    async loadSessionUnreadCount(session) {
      try {
        // 获取会话中当前用户未读的消息数量
        const response = await fetch(`http://localhost:8088/api/v1/chat/session/${session.session_id}/messages?limit=100&offset=0`)
        const messages = await response.json()

        // 计算当前用户作为接收者的未读消息数量
        const unreadCount = messages.filter(msg =>
          msg.receiver_id === this.currentUser.user_id && !msg.read_status
        ).length

        session.unreadCount = unreadCount
      } catch (error) {
        console.error('获取未读数量失败:', error)
        session.unreadCount = 0
      }
    },

    async selectSession(session) {
      this.selectedSession = session
      this.selectedSessionId = session.session_id
      await this.loadMessages()

      // 选择会话时，将该会话的未读消息标记为已读
      await this.markSessionAsRead(session)
    },

    async markSessionAsRead(session) {
      try {
        // 获取会话中当前用户未读的消息
        const response = await fetch(`http://localhost:8088/api/v1/chat/session/${session.session_id}/messages?limit=100&offset=0`)
        const messages = await response.json()

        const unreadMessages = messages.filter(msg =>
          msg.receiver_id === this.currentUser.user_id && !msg.read_status
        )

        // 标记所有未读消息为已读
        for (let message of unreadMessages) {
          await fetch(`http://localhost:8088/api/v1/chat/messages/${message.message_id}/read?user_id=${this.currentUser.user_id}`, {
            method: 'PUT'
          })
        }

        // 更新本地会话的未读数量
        session.unreadCount = 0
      } catch (error) {
        console.error('标记消息已读失败:', error)
      }
    },

    async loadMessages() {
      try {
        const response = await fetch(`http://localhost:8088/api/v1/chat/session/${this.selectedSessionId}/messages?limit=50&offset=0`)
        this.messages = await response.json()
        this.scrollToBottom()
      } catch (error) {
        console.error('获取消息失败:', error)
      }
    },

    // 内容检测方法
    checkMessageContent(content) {
      const lowerContent = content.toLowerCase()

      // 检查是否包含敏感词
      for (let word of this.sensitiveWords) {
        if (lowerContent.includes(word.toLowerCase())) {
          return {
            isValid: false,
            message: '消息包含不当内容，请修改后重新发送'
          }
        }
      }

      // 检查是否全是特殊字符或数字（可能的垃圾信息）
      const specialCharPattern = /^[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?\s\d]*$/
      if (specialCharPattern.test(content)) {
        return {
          isValid: false,
          message: '请发送有意义的文字内容'
        }
      }

      // 检查消息长度
      if (content.length > 500) {
        return {
          isValid: false,
          message: '消息内容过长，请控制在500字以内'
        }
      }

      return { isValid: true }
    },

    // 显示错误消息
    showErrorMessage(message) {
      this.errorMessage = message
      // 3秒后自动隐藏
      setTimeout(() => {
        this.errorMessage = ''
      }, 3000)
    },

    // 关闭错误消息
    closeErrorMessage() {
      this.errorMessage = ''
    },

    // 添加缺失的sendMessage方法
    async sendMessage() {
      if (!this.newMessage.trim() || !this.selectedSession) return

      // 添加内容检测
      const contentCheck = this.checkMessageContent(this.newMessage.trim())
      if (!contentCheck.isValid) {
        this.showErrorMessage(contentCheck.message)
        return // 阻止发送
      }

      const otherUser = this.getOtherUser(this.selectedSession)

      try {
        const response = await fetch('http://localhost:8088/api/v1/chat/messages', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            session_id: this.selectedSessionId,
            sender_id: this.currentUser.user_id,
            receiver_id: otherUser.user_id,
            message_type: 'text',
            content: this.newMessage
          })
        })

        if (response.ok) {
          this.newMessage = ''
          await this.loadMessages()
          // 重新加载会话列表以更新最后消息
          await this.loadSessions()
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        this.showErrorMessage('发送失败，请重试')
      }
    },

    connectWebSocket() {
      this.ws = new WebSocket(`ws://localhost:8088/api/v1/ws/${this.currentUser.user_id}`)

      this.ws.onmessage = (event) => {
        const data = JSON.parse(event.data)
        if (data.type === 'message') {
          // 如果是当前选中会话的消息，直接添加到消息列表
          if (data.data.session_id === this.selectedSessionId) {
            this.messages.push(data.data)
            this.scrollToBottom()
          }
          // 更新会话列表
          this.loadSessions()
        }
      }
    },

    getOtherUser(session) {
      return session.buyer_id === this.currentUser.user_id ? session.seller : session.buyer
    },

    formatTime(time) {
      return new Date(time).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    // 处理商品聊天
    async handleProductChat() {
      const { productId, sellerId, buyerId, autoCreate } = this.$route.query

      if (autoCreate === 'true' && sellerId && buyerId) {
        try {
          // 1. 首先尝试创建或获取会话
          const sessionResponse = await fetch('http://localhost:8088/api/v1/chat/sessions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              buyer_id: buyerId,
              seller_id: sellerId
            })
          })

          if (sessionResponse.ok) {
            const session = await sessionResponse.json()

            // 2. 重新加载会话列表
            await this.loadSessions()

            // 3. 自动选择这个会话
            const targetSession = this.sessions.find(s => s.session_id === session.session_id)
            if (targetSession) {
              await this.selectSession(targetSession)
            }
          }
        } catch (error) {
          console.error('创建聊天会话失败:', error)
          this.$message?.error('无法创建聊天会话')
        }

        // 清理URL参数
        this.$router.replace({ path: '/chat-list' })
      }
    },
  }
}
</script>

<style scoped>
@import '../styles/ChatList.css';
</style>