<template>
  <div class="password-reset-container">
    <div class="password-reset-box">
      <h2>修改密码</h2>
      
      <form @submit.prevent="updatePassword" class="password-reset-form">
        <div class="password-reset-form-group">
          <label for="currentPassword">当前密码</label>
          <input 
            type="password" 
            id="currentPassword" 
            v-model="currentPassword" 
            required
            placeholder="请输入当前密码"
          />
        </div>
        
        <div class="password-reset-form-group">
          <label for="newPassword">新密码</label>
          <input 
            type="password" 
            id="newPassword" 
            v-model="newPassword" 
            required
            placeholder="请输入新密码（至少6位）"
          />
          <p class="password-reset-tip" v-if="newPasswordTip">{{ newPasswordTip }}</p>
        </div>
        
        <div class="password-reset-form-group">
          <label for="confirmPassword">确认新密码</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="confirmPassword" 
            required
            placeholder="请再次输入新密码"
          />
          <p class="password-reset-tip" v-if="confirmPasswordTip">{{ confirmPasswordTip }}</p>
        </div>
        
        <div class="password-reset-form-group">
          <button type="submit" class="password-reset-submit-btn" :disabled="isLoading">
            {{ isLoading ? '处理中...' : '确认修改' }}
          </button>
        </div>
        
        <p class="password-reset-error" v-if="errorMessage">{{ errorMessage }}</p>
        <p class="password-reset-success" v-if="successMessage">{{ successMessage }}</p>
      </form>
      
      <div class="password-reset-back">
        <button @click="goBack" class="password-reset-back-btn">返回</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'PasswordReset',
  data() {
    return {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
      newPasswordTip: '',
      confirmPasswordTip: '',
      errorMessage: '',
      successMessage: '',
      isLoading: false,
      userId: this.$route.query.userId || ''
    }
  },
  watch: {
    newPassword(newVal) {
      // 验证密码长度
      if (newVal && newVal.length < 6) {
        this.newPasswordTip = '密码长度至少6位'
      } else {
        this.newPasswordTip = ''
      }
    },
    confirmPassword(newVal) {
      // 验证密码一致性
      if (newVal && newVal !== this.newPassword) {
        this.confirmPasswordTip = '两次输入的密码不一致'
      } else {
        this.confirmPasswordTip = ''
      }
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    async updatePassword() {
      // 清除之前的消息
      this.errorMessage = ''
      this.successMessage = ''
      
      // 表单验证
      if (this.newPassword.length < 6) {
        this.errorMessage = '新密码长度至少6位'
        return
      }
      
      if (this.newPassword !== this.confirmPassword) {
        this.errorMessage = '两次输入的密码不一致'
        return
      }
      
      this.isLoading = true
      
      try {
        // 调用后端API进行密码修改
        console.log('修改密码，用户ID:', this.userId)
        
        if (!this.userId) {
          this.errorMessage = '用户ID不存在，请重新登录'
          return
        }
        
        // 调用后端API
        const response = await axios.post('http://localhost:8089/api/user/password/change', {
          userId: this.userId,
          oldPassword: this.currentPassword,
          newPassword: this.newPassword
        })
        
        // 处理响应
        if (response.data.success && response.data.code === 200) {
          this.successMessage = response.data.message || '密码修改成功！'
          
          // 清空表单
          this.currentPassword = ''
          this.newPassword = ''
          this.confirmPassword = ''
          
          // 3秒后自动返回上一页
          setTimeout(() => {
            this.goBack()
          }, 3000)
        } else {
          this.errorMessage = response.data.message || '密码修改失败，请稍后再试'
        }
      } catch (error) {
        console.error('密码修改失败:', error)
        // 处理不同类型的错误
        if (error.response) {
          // 服务器返回了错误状态码
          const responseData = error.response.data
          this.errorMessage = responseData.message || `密码修改失败: ${error.response.status} ${error.response.statusText}`
        } else if (error.request) {
          // 请求已发送但没有收到响应
          this.errorMessage = '服务器无响应，请检查网络连接'
        } else {
          // 请求设置时发生错误
          this.errorMessage = `请求错误: ${error.message}`
        }
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
/* 容器样式 */
.password-reset-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 主卡片样式 */
.password-reset-box {
  width: 100%;
  max-width: 480px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  padding: 40px;
  position: relative;
  z-index: 2;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 标题区域 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  margin-bottom: 20px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.4);
  }
  70% {
    box-shadow: 0 0 0 20px rgba(102, 126, 234, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0);
  }
}

.lock-icon {
  width: 40px;
  height: 40px;
  color: white;
}

.password-reset-box h2 {
  margin: 0 0 10px 0;
  color: #2d3748;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #718096;
  font-size: 16px;
  margin: 0;
  line-height: 1.5;
}

/* 表单样式 */
.password-reset-form-group {
  margin-bottom: 25px;
}

.password-reset-form-group label {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #4a5568;
  font-size: 15px;
}

.input-icon {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  color: #667eea;
}

.input-wrapper {
  position: relative;
}

.password-reset-form-group input {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.password-reset-form-group input:focus {
  border-color: #667eea;
  outline: none;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.password-reset-form-group input::placeholder {
  color: #a0aec0;
}

/* 提示信息 */
.password-reset-tip {
  color: #e53e3e;
  font-size: 14px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

/* 提交按钮 */
.password-reset-submit-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.password-reset-submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.password-reset-submit-btn:hover::before {
  left: 100%;
}

.password-reset-submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.password-reset-submit-btn:active {
  transform: translateY(0);
}

.password-reset-submit-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-icon {
  width: 20px;
  height: 20px;
}

/* 加载动画 */
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 消息容器 */
.message-container {
  margin-top: 20px;
}

.password-reset-error,
.password-reset-success {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  animation: fadeIn 0.3s ease-out;
}

.password-reset-error {
  background: rgba(254, 226, 226, 0.8);
  color: #c53030;
  border: 1px solid rgba(254, 202, 202, 0.8);
}

.password-reset-success {
  background: rgba(240, 253, 244, 0.8);
  color: #38a169;
  border: 1px solid rgba(196, 245, 208, 0.8);
}

.message-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 返回按钮 */
.password-reset-back {
  text-align: center;
  margin-top: 30px;
}

.password-reset-back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(113, 128, 150, 0.1);
  border: 1px solid rgba(113, 128, 150, 0.2);
  color: #718096;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  text-decoration: none;
}

.password-reset-back-btn:hover {
  background: rgba(113, 128, 150, 0.15);
  color: #4a5568;
  transform: translateY(-1px);
}

.back-icon {
  width: 16px;
  height: 16px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .password-reset-container {
    padding: 15px;
  }
  
  .password-reset-box {
    padding: 30px 25px;
    border-radius: 16px;
  }
  
  .password-reset-box h2 {
    font-size: 24px;
  }
  
  .password-reset-form-group input {
    padding: 14px 16px;
    font-size: 15px;
  }
  
  .password-reset-submit-btn {
    padding: 14px;
    font-size: 15px;
  }
  
  .icon-wrapper {
    width: 70px;
    height: 70px;
  }
  
  .lock-icon {
    width: 35px;
    height: 35px;
  }
}

@media (max-width: 360px) {
  .password-reset-box {
    padding: 25px 20px;
  }
}
</style>