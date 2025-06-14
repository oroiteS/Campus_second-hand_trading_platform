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
  name: 'LoginPage',  // 将'Login'改为'LoginPage'
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
        if (this.username === '00000000' && this.password === '00000000') {
          // 管理员登录成功
          alert('管理员登录成功!')
          // 存储管理员登录状态
          localStorage.setItem('isAdminLoggedIn', 'true')
          localStorage.setItem('adminToken', 'admin-token-' + Date.now())
          localStorage.setItem('adminUsername', '管理员')
          // 跳转到管理员后台
          this.$router.push('/AdminDashboard')
        } else if (this.username === 'admin' && this.password === '123456') {
          // 普通用户登录成功
          alert('登录成功!')
          // 存储登录状态
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('username', this.username)
          // 跳转到首页
          this.$router.push('/')
        } else {
          // 登录失败
          this.errorMessage = '用户名或密码错误'
        }
        this.isLoading = false
      }, 1000)
      
      /* 实际项目中的API调用示例
      // 使用axios或fetch调用后端API
      fetch('/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: this.username,
          password: this.password
        })
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          // 登录成功
          localStorage.setItem('token', data.token)
          this.$router.push('/')
        } else {
          // 登录失败
          this.errorMessage = data.message || '登录失败'
        }
        this.isLoading = false
      })
      .catch(error => {
        this.errorMessage = '网络错误，请稍后再试'
        this.isLoading = false
        console.error('Login error:', error)
      })
      */
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('../assets/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.error-message {
  color: #e53935;
  margin-bottom: 15px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #3aa876;
}

.login-btn:disabled {
  background-color: #a8d5c3;
  cursor: not-allowed;
}

.links {
  margin-top: 20px;
  text-align: center;
  display: flex;
  justify-content: space-between;
}

.back-link, .register-link {
  color: #42b983;
  text-decoration: none;
}

.back-link:hover, .register-link:hover {
  text-decoration: underline;
}
</style>