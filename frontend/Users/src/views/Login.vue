<template>
  <div class="login-container">
    <div class="login-box">
      <h2>用户登录</h2>
      <div class="form-group">
        <label for="username">用户名</label>
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
      
      // 模拟API调用
      setTimeout(() => {
        // 这里是静态数据测试，实际项目中应该调用后端API
        if (this.username === 'admin' && this.password === '123456') {
          // 普通用户登录成功
          alert('登录成功!')
          // 修改：统一使用userToken和userInfo
          const userToken = 'user-token-' + Date.now()
          const userInfo = {
            username: this.username,
            name: 'xy21675070351', // 显示名称
            avatar: '/测试图片.jpg',
            status: '在线'
          }
          
          localStorage.setItem('userToken', userToken)
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          localStorage.setItem('isLoggedIn', 'true') // 保持兼容性
          localStorage.setItem('username', this.username) // 保持兼容性
          
          // 跳转到首页
          this.$router.push('/')
        } else {
          // 登录失败
          this.errorMessage = '用户名或密码错误'
        }
        this.isLoading = false
      }, 1000)
    }
  }
}
</script>

<style scoped>
@import '../styles/Login.css';
</style>