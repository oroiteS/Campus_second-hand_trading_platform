<template>
  <div class="chat-container">
    <!-- 左侧聊天列表 -->
    <div class="chat-list">
      <div class="chat-header">
        <h3>消息</h3>
        <div class="header-actions">
          <div class="user-info">
            <span>{{ currentUser.user_name }}</span>
          </div>
          <button @click="goToHome" class="home-button">返回首页</button>
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
import { ax1 } from '@/api/axios.js'

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
      const userId = localStorage.getItem('userId');

      if (!userId) {
        this.$router.push('/login')
        return
      }

      try {
        const response = await ax1.get(`/api-8088/v1/chat/user/${userId}/info`)
        this.currentUser = response.data
        // 保存到localStorage
        localStorage.setItem('currentUserId', this.currentUser.user_id)
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.$router.push('/login')
      }
    },

    async loadSessions() {
      try {
        const response = await ax1.get(`/api-8088/v1/chat/user/${this.currentUser.user_id}/sessions`)
        this.sessions = response.data

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
        const response = await ax1.get(`/api-8088/v1/chat/session/${session.session_id}/messages?limit=1&offset=0`)
        const messages = response.data
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
        const response = await ax1.get(`/api-8088/v1/chat/session/${session.session_id}/messages?limit=100&offset=0`)
        const messages = response.data

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
        const response = await ax1.get(`/api-8088/v1/chat/session/${session.session_id}/messages?limit=100&offset=0`)
        const messages = response.data

        const unreadMessages = messages.filter(msg =>
          msg.receiver_id === this.currentUser.user_id && !msg.read_status
        )

        // 标记所有未读消息为已读
        for (let message of unreadMessages) {
          await ax1.put(`/api-8088/v1/chat/messages/${message.message_id}/read?user_id=${this.currentUser.user_id}`)
        }

        // 更新本地会话的未读数量
        session.unreadCount = 0
      } catch (error) {
        console.error('标记消息已读失败:', error)
      }
    },

    async loadMessages() {
      try {
        const response = await ax1.get(`/api-8088/v1/chat/session/${this.selectedSessionId}/messages?limit=50&offset=0`)
        this.messages = response.data
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
        await ax1.post('/api-8088/v1/chat/messages', {
          session_id: this.selectedSessionId,
          sender_id: this.currentUser.user_id,
          receiver_id: otherUser.user_id,
          message_type: 'text',
          content: this.newMessage
        })

        this.newMessage = ''
        await this.loadMessages()
        // 重新加载会话列表以更新最后消息
        await this.loadSessions()
      } catch (error) {
        console.error('发送消息失败:', error)
        this.showErrorMessage('发送失败，请重试')
      }
    },

    connectWebSocket() {
      this.ws = new WebSocket(`ws://47.117.90.63:8088/api/v1/ws/${this.currentUser.user_id}`)

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
      // 使用正确的字段名：first_id, second_id, first, second
      return session.first_id === this.currentUser.user_id ? session.second : session.first
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
      const { sellerId, buyerId, autoCreate } = this.$route.query

      if (autoCreate === 'true' && sellerId && buyerId) {
        try {
          // 1. 首先尝试创建或获取会话
          const sessionResponse = await ax1.post('/api-8088/v1/chat/sessions', {
            first_id: buyerId,
            second_id: sellerId
          })

          const session = sessionResponse.data

          // 2. 重新加载会话列表
          await this.loadSessions()

          // 3. 自动选择这个会话
          const targetSession = this.sessions.find(s => s.session_id === session.session_id)
          if (targetSession) {
            await this.selectSession(targetSession)
          }
        } catch (error) {
          console.error('创建聊天会话失败:', error)
          this.$message?.error('无法创建聊天会话')
        }

        // 清理URL参数
        this.$router.replace({ path: '/chat-list' })
      }
    },

    // 返回首页
    goToHome() {
      const userId = this.currentUser.user_id || localStorage.getItem('userId')
      if (userId) {
        this.$router.push(`/?userId=${userId}`)
      } else {
        this.$router.push('/home')
      }
    }
  }
}
</script>

<style scoped>
/* 聊天容器 */
.chat-container {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

/* 左侧聊天列表 */
.chat-list {
  width: 300px;
  background-color: white;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  color: #666;
  font-size: 14px;
}

.home-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s;
}

.home-button:hover {
  background-color: #0056b3;
}

/* 会话列表 */
.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.session-item:hover {
  background-color: #f8f9fa;
}

.session-item.active {
  background-color: #e3f2fd;
}

.avatar {
  margin-right: 12px;
}

.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.last-message {
  color: #666;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.time {
  color: #999;
  font-size: 12px;
}

.unread-badge {
  background-color: #ff4757;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 11px;
  min-width: 18px;
  text-align: center;
}

/* 右侧聊天内容 */
.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-window .chat-header {
  background-color: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
}

.chat-window .chat-header h4 {
  margin: 0;
  color: #333;
}

.messages-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f8f9fa;
}

.message {
  margin-bottom: 15px;
  display: flex;
}

.message.own-message {
  justify-content: flex-end;
}

.message-content {
  max-width: 70%;
  background-color: white;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.own-message .message-content {
  background-color: #007bff;
  color: white;
}

.message-text {
  margin-bottom: 4px;
  line-height: 1.4;
}

.message-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  opacity: 0.7;
}

.read-status {
  margin-left: 8px;
}

.error-bubble {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: #ff4757;
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-area {
  padding: 20px;
  background-color: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 10px;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 24px;
  outline: none;
  font-size: 14px;
}

.message-input:focus {
  border-color: #007bff;
}

.send-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 24px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.send-button:hover {
  background-color: #0056b3;
}

.no-session {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
  font-size: 16px;
}
</style>