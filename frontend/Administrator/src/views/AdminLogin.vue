<template>
  <div class="admin-login">
    <div class="login-container">
      <div class="login-header">
        <h1 class="login-title">管理员登录</h1>
        <p class="login-subtitle">校园二手交易平台管理系统</p>
      </div>
      
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username" class="form-label">
            <span class="label-icon">👤</span>
            用户名
          </label>
          <input 
            type="text" 
            id="username" 
            v-model="loginForm.username" 
            class="form-input"
            placeholder="请输入管理员用户名"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password" class="form-label">
            <span class="label-icon">🔒</span>
            密码
          </label>
          <input 
            type="password" 
            id="password" 
            v-model="loginForm.password" 
            class="form-input"
            placeholder="请输入密码"
            required
          />
        </div>
        
        <button type="submit" class="login-btn" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div class="login-footer">
        <p class="footer-text">© 2023 校园二手交易平台 - 管理员系统</p>
      </div>
    </div>
  </div>
</template>

<script>
import { userService } from '../api/services'

export default {
  name: 'AdminLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      isLoading: false
    }
  },
  created() {
    // 检查是否已经登录
    this.checkExistingLogin()
  },
  methods: {
    checkExistingLogin() {
      const isAdminLoggedIn = localStorage.getItem('isAdminLoggedIn')
      const adminToken = localStorage.getItem('adminToken')
      
      if (isAdminLoggedIn && adminToken) {
        // 已登录，跳转到管理面板
        this.$router.push('/AdminDashboard')
      }
    },
    
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        alert('请填写完整的登录信息')
        return
      }
      
      this.isLoading = true
      
      try {
        // 调用登录API
        const response = await userService.adminLogin({
          username: this.loginForm.username,
          password: this.loginForm.password
        })
        
        if (response.success) {
          // 登录成功，保存登录状态
          localStorage.setItem('isAdminLoggedIn', 'true')
          localStorage.setItem('adminToken', response.token)
          localStorage.setItem('adminUsername', response.username || this.loginForm.username)
          // 使用返回的adminId
          localStorage.setItem('adminId', response.adminId || this.loginForm.username)
          
          alert('登录成功！')
          // 跳转到管理面板
          this.$router.push('/AdminDashboard')
        } else {
          alert(response.message || '登录失败，请检查用户名和密码')
        }
      } catch (error) {
        console.error('登录错误:', error)
        alert('登录失败：' + (error.message || '网络连接错误'))
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.admin-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
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

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #2f3542;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #747d8c;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #2f3542;
  margin-bottom: 8px;
}

.label-icon {
  margin-right: 8px;
  font-size: 16px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.checkbox-text {
  user-select: none;
}

.login-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top: 2px solid white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e1e8ed;
}

.footer-text {
  font-size: 12px;
  color: #a4b0be;
  margin: 0;
}

@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .login-title {
    font-size: 24px;
  }
}
</style>