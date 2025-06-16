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
        <label for="realName" class="register-label">真实姓名</label>
        <input 
          type="text" 
          id="realName" 
          v-model="realName" 
          placeholder="请输入真实姓名"
          class="register-input"
        />
        <div class="register-input-tip" v-if="realNameTip">{{ realNameTip }}</div>
      </div>
      <div class="register-form-group">
        <label for="idCard" class="register-label">身份证号</label>
        <input 
          type="text" 
          id="idCard" 
          v-model="idCard" 
          placeholder="请输入18位身份证号"
          class="register-input"
        />
        <div class="register-input-tip" v-if="idCardTip">{{ idCardTip }}</div>
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
      <div class="register-form-group">
        <label class="register-label">兴趣爱好（最多选择5个）</label>
        <div class="hobby-tags">
          <div 
            v-for="hobby in hobbies" 
            :key="hobby"
            class="hobby-tag"
            :class="{ 'selected': selectedHobbies.includes(hobby) }"
            @click="toggleHobby(hobby)"
          >
            {{ hobby }}
          </div>
        </div>
        <div class="register-input-tip" v-if="hobbyTip">{{ hobbyTip }}</div>
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
      realName: '',
      idCard: '',
      phone: '',
      password: '',
      confirmPassword: '',
      errorMessage: '',
      userIdTip: '',
      realNameTip: '',
      idCardTip: '',
      phoneTip: '',
      passwordTip: '',
      hobbyTip: '',
      isLoading: false,
      hobbies: [
        '阅读', '运动', '音乐', '电影', '旅行', '摄影', '绘画', '编程',
        '游戏', '美食', '购物', '健身', '瑜伽', '舞蹈', '唱歌', '书法',
      ],
      selectedHobbies: []
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
    realName(newVal) {
      // 验证真实姓名格式（中文姓名2-4个字符）
      if (newVal && !/^[\u4e00-\u9fa5]{2,4}$/.test(newVal)) {
        this.realNameTip = '请输入2-4个中文字符的真实姓名'
      } else {
        this.realNameTip = ''
      }
    },
    idCard(newVal) {
      // 验证身份证号格式
      if (newVal && !this.validateIdCard(newVal)) {
        this.idCardTip = '请输入有效的18位身份证号'
      } else {
        this.idCardTip = ''
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
    // 身份证号验证函数
    validateIdCard(idCard) {
      // 18位身份证号正则表达式
      const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
      
      if (!idCardRegex.test(idCard)) {
        return false
      }
      
      // 验证校验码
      const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
      const checkCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
      
      let sum = 0
      for (let i = 0; i < 17; i++) {
        sum += parseInt(idCard[i]) * weights[i]
      }
      
      const checkCode = checkCodes[sum % 11]
      return checkCode === idCard[17].toUpperCase()
    },
    
    // 切换爱好选择
    toggleHobby(hobby) {
      const index = this.selectedHobbies.indexOf(hobby)
      if (index > -1) {
        // 如果已选择，则取消选择
        this.selectedHobbies.splice(index, 1)
        this.hobbyTip = ''
      } else {
        // 如果未选择，检查是否超过5个
        if (this.selectedHobbies.length >= 5) {
          this.hobbyTip = '最多只能选择5个爱好'
          return
        }
        this.selectedHobbies.push(hobby)
        this.hobbyTip = ''
      }
    },
    
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
      if (!this.userId || !this.nickname || !this.realName || !this.idCard || !this.phone || !this.password || !this.confirmPassword) {
        this.errorMessage = '所有字段都必须填写'
        return
      }
      
      // 验证真实姓名格式
      if (!/^[\u4e00-\u9fa5]{2,4}$/.test(this.realName)) {
        this.errorMessage = '请输入2-4个中文字符的真实姓名'
        return
      }
      
      // 验证身份证号格式
      if (!this.validateIdCard(this.idCard)) {
        this.errorMessage = '请输入有效的18位身份证号'
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