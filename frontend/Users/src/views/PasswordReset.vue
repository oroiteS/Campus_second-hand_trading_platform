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
        // 这里应该调用实际的API进行密码修改
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 模拟成功响应
        this.successMessage = '密码修改成功！'
        
        // 清空表单
        this.currentPassword = ''
        this.newPassword = ''
        this.confirmPassword = ''
        
        // 3秒后自动返回上一页
        setTimeout(() => {
          this.goBack()
        }, 3000)
      } catch (error) {
        console.error('密码修改失败:', error)
        this.errorMessage = '密码修改失败，请稍后再试'
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.password-reset-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.password-reset-box {
  width: 100%;
  max-width: 450px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.password-reset-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.password-reset-form-group {
  margin-bottom: 20px;
}

.password-reset-form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

.password-reset-form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.password-reset-form-group input:focus {
  border-color: #4CAF50;
  outline: none;
}

.password-reset-tip {
  color: #ff6b6b;
  font-size: 14px;
  margin-top: 5px;
}

.password-reset-submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.password-reset-submit-btn:hover {
  background-color: #45a049;
}

.password-reset-submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.password-reset-error {
  color: #ff6b6b;
  text-align: center;
  margin-top: 15px;
}

.password-reset-success {
  color: #4CAF50;
  text-align: center;
  margin-top: 15px;
}

.password-reset-back {
  text-align: center;
  margin-top: 20px;
}

.password-reset-back-btn {
  background: none;
  border: none;
  color: #666;
  text-decoration: underline;
  cursor: pointer;
  font-size: 14px;
}

.password-reset-back-btn:hover {
  color: #333;
}

@media (max-width: 480px) {
  .password-reset-box {
    padding: 20px;
  }
  
  .password-reset-form-group input {
    padding: 10px;
  }
}
</style>