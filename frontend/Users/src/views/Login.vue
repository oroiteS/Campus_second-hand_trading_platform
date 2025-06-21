<template>
  <div class="login-container">
    <div class="login-box">
      <h2>用户登录</h2>
      <div class="form-group">
        <label for="username">用户ID</label>
        <input 
          type="text" 
          id="username" 
          v-model="username" 
          placeholder="请输入用户名"
        />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          placeholder="请输入密码"
        />
      </div>
      <div class="error-message" v-if="errorMessage">
        {{ errorMessage }}
      </div>
      <button @click="login" class="login-btn" :disabled="isLoading">
        {{ isLoading ? '登录中...' : '登录' }}
      </button>
      <div class="links">
        <router-link to="/" class="back-link">返回首页</router-link>
        <router-link to="/register" class="register-link">没有账号？去注册</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import {ax1} from '@/api/axios'

export default {
  name: 'LoginPage',
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      isLoading: false
    }
  },
  methods: {
    login() {
      // 简单的表单验证
      if (!this.username || !this.password) {
        this.errorMessage = '用户名和密码不能为空'
        return
      }
      
      this.isLoading = true
      this.errorMessage = ''
      
      // 调用实际后端API
      const loginRequest = {
        userId: this.username,
        password: this.password
      }
      
      ax1.post('/api-8080/user/login', loginRequest, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      .then(response => {
        const result = response.data
        
        if (result.code === 200 && result.data.success) {
          // 登录成功
          alert('登录成功!')
          
          // 存储用户信息和token
          const token = result.data.token
          // 保存用户ID、token和登录状态
          localStorage.setItem('userToken', token)
          localStorage.setItem('userId', result.data.userId)
          localStorage.setItem('isLoggedIn', 'true')
          
          // 跳转到首页，携带userId数据
          this.$router.push({
            path: '/',
            query: {
              userId: result.data.userId
            }
          })
        } else {
          // 登录失败
          this.errorMessage = result.message || '登录失败'
        }
      })
      .catch(error => {
        console.error('登录请求失败:', error)
        if (error.response) {
          // 服务器返回错误响应
          const result = error.response.data
          this.errorMessage = result.message || '登录失败'
        } else if (error.request) {
          // 请求发送失败
          this.errorMessage = '网络连接失败，请检查后端服务是否启动'
        } else {
          // 其他错误
          this.errorMessage = '登录失败，请稍后重试'
        }
      })
      .finally(() => {
        this.isLoading = false
      })
    }
  }
}
</script>

<style scoped>
@import '../styles/Login.css';
</style>@/api/axios1