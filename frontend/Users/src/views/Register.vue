<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>
      <div class="register-form-group">
        <label for="userId" class="register-label">用户ID</label>
        <input 
          type="text" 
          id="userId" 
          v-model="userId" 
          placeholder="请输入9位用户ID（以S或T开头）"
          class="register-input"
        />
        <div class="register-input-tip" v-if="userIdTip">{{ userIdTip }}</div>
      </div>
      <div class="register-form-group">
        <label for="nickname" class="register-label">昵称</label>
        <input 
          type="text" 
          id="nickname" 
          v-model="nickname" 
          placeholder="请输入昵称"
          class="register-input"
        />
      </div>
      <div class="register-form-group">
        <label for="phone" class="register-label">手机号</label>
        <input 
          type="text" 
          id="phone" 
          v-model="phone" 
          placeholder="请输入手机号"
          class="register-input"
        />
        <div class="register-input-tip" v-if="phoneTip">{{ phoneTip }}</div>
      </div>
      <div class="register-form-group">
        <label for="password" class="register-label">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          placeholder="请输入密码（至少6位）"
          class="register-input"
        />
        <div class="register-input-tip" v-if="passwordTip">{{ passwordTip }}</div>
      </div>
      <div class="register-form-group">
        <label for="confirmPassword" class="register-label">确认密码</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="confirmPassword" 
          placeholder="请再次输入密码"
          class="register-input"
        />
      </div>
      <div class="register-error-message" v-if="errorMessage">
        {{ errorMessage }}
      </div>
      <button @click="register" class="register-btn" :disabled="isLoading">
        {{ isLoading ? '注册中...' : '注册' }}
      </button>
      <div class="register-links">
        <router-link to="/login" class="register-login-link">已有账号？去登录</router-link>
        <router-link to="/" class="register-back-link">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegisterPage',
  data() {
    return {
      userId: '',
      nickname: '小红',  // 默认昵称
      phone: '',
      password: '',
      confirmPassword: '',
      errorMessage: '',
      userIdTip: '',
      phoneTip: '',
      passwordTip: '',
      isLoading: false
    }
  },
  watch: {
    userId(newVal) {
      // 验证用户ID格式
      if (newVal && (newVal.length !== 9 || !/^[ST]/.test(newVal))) {
        this.userIdTip = '用户ID必须为9位且以S或T开头'
      } else {
        this.userIdTip = ''
      }
    },
    phone(newVal) {
      // 验证中国手机号格式
      if (newVal && !/^1[3-9]\d{9}$/.test(newVal)) {
        this.phoneTip = '请输入有效的中国手机号'
      } else {
        this.phoneTip = ''
      }
    },
    password(newVal) {
      // 验证密码长度
      if (newVal && newVal.length < 6) {
        this.passwordTip = '密码长度至少6位'
      } else {
        this.passwordTip = ''
      }
    }
  },
  methods: {
    async checkUserIdExists(userId) {
      // 这里应该调用后端API检查用户ID是否已存在
      // 模拟API调用
      return new Promise(resolve => {
        setTimeout(() => {
          // 假设S123456789已经存在
          resolve(userId === 'S123456789')
        }, 500)
      })
    },
    async register() {
      // 表单验证
      if (!this.userId || !this.nickname || !this.phone || !this.password || !this.confirmPassword) {
        this.errorMessage = '所有字段都必须填写'
        return
      }
      
      // 验证用户ID格式
      if (this.userId.length !== 9 || !/^[ST]/.test(this.userId)) {
        this.errorMessage = '用户ID必须为9位且以S或T开头'
        return
      }
      
      // 验证手机号格式
      if (!/^1[3-9]\d{9}$/.test(this.phone)) {
        this.errorMessage = '请输入有效的中国手机号'
        return
      }
      
      // 验证密码长度
      if (this.password.length < 6) {
        this.errorMessage = '密码长度至少6位'
        return
      }
      
      // 验证密码一致性
      if (this.password !== this.confirmPassword) {
        this.errorMessage = '两次输入的密码不一致'
        return
      }
      
      this.isLoading = true
      this.errorMessage = ''
      
      // 检查用户ID是否已存在
      const userExists = await this.checkUserIdExists(this.userId)
      if (userExists) {
        this.errorMessage = '该用户ID已被注册'
        this.isLoading = false
        return
      }
      
      // 模拟API调用
      setTimeout(() => {
        // 这里是静态数据测试，实际项目中应该调用后端API
        alert('注册成功!')
        // 跳转到登录页
        this.$router.push('/login')
        this.isLoading = false
      }, 1000)
      
      /* 实际项目中的API调用示例
      // 使用axios或fetch调用后端API
      fetch('/api/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          userId: this.userId,
          nickname: this.nickname,
          phone: this.phone,
          password: this.password
        })
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          // 注册成功
          alert('注册成功!')
          this.$router.push('/login')
        } else {
          // 注册失败
          this.errorMessage = data.message || '注册失败'
        }
        this.isLoading = false
      })
      .catch(error => {
        this.errorMessage = '网络错误，请稍后再试'
        this.isLoading = false
        console.error('Register error:', error)
      })
      */
    }
  }
}
</script>

<style scoped>
@import '../styles/Register.css';
</style>