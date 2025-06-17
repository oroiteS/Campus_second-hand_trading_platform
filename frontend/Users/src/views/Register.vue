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
import axios from 'axios'

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
    // 身份证号验证函数（宽松验证）
    validateIdCard(idCard) {
      // 简化的身份证号验证：只检查是否为18位数字和字母X的组合
      const idCardRegex = /^\d{17}[\dXx]$/
      return idCardRegex.test(idCard)
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
        this.errorMessage = '请输入18位身份证号（最后一位可以是数字或X）'
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
      
      // 调用后端注册API
      try {
        const response = await axios.post('http://localhost:8080/api/user/register', {
          userId: this.userId,
          userName: this.nickname,
          realName: this.realName,
          idCard: this.idCard,
          telephone: this.phone,
          password: this.password
        })
        
        if (response.data.code === 200 && response.data.data && response.data.data.success) {
          alert('注册成功!')
          this.$router.push('/login')
        } else {
          this.errorMessage = (response.data.data && response.data.data.message) || response.data.message || '注册失败'
        }
      } catch (error) {
        console.error('注册失败:', error)
        if (error.response && error.response.data && error.response.data.message) {
          this.errorMessage = error.response.data.message
        } else {
          this.errorMessage = '网络错误，请稍后再试'
        }
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/Register.css';
</style>