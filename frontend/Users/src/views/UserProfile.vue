<template>
  <div class="profile-container">
    <div class="profile-box">
      <h2>修改个人信息</h2>
      
      <!-- 头像放在最上方 -->
      <div class="avatar-section">
        <img :src="avatarUrl || 'https://via.placeholder.com/100x100'" class="avatar-preview-large" />
        <div class="form-group avatar-url-group">
          <label for="avatarUrl">头像地址</label>
          <input 
            type="text" 
            id="avatarUrl" 
            v-model="avatarUrl" 
            placeholder="请输入头像URL地址"
          />
        </div>
      </div>
      
      <div class="form-group" v-if="userId">
        <label for="userId">用户ID</label>
        <input 
          type="text" 
          id="userId" 
          v-model="userId" 
          placeholder="用户ID" 
          disabled
        />
        <div class="input-tip">用户ID不可修改</div>
      </div>
      
      <div class="form-group" v-if="username !== undefined">
        <label for="username">昵称</label>
        <input 
          type="text" 
          id="username" 
          v-model="username" 
          placeholder="请输入昵称"
        />
        <div class="input-error" v-if="validationErrors.username">{{ validationErrors.username }}</div>
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          placeholder="请输入密码（不少于6位）"
        />
        <div class="input-error" v-if="validationErrors.password">{{ validationErrors.password }}</div>
      </div>
      
      <div class="form-group" v-if="telephone !== undefined">
        <label for="telephone">电话信息</label>
        <input 
          type="text" 
          id="telephone" 
          v-model="telephone" 
          placeholder="请输入电话号码"
        />
        <div class="input-error" v-if="validationErrors.telephone">{{ validationErrors.telephone }}</div>
      </div>
      
      <div class="form-group" v-if="realName !== undefined">
        <label for="realName">真实姓名</label>
        <input 
          type="text" 
          id="realName" 
          v-model="realName" 
          placeholder="请输入真实姓名"
        />
      </div>
      
      <div class="form-group" v-if="idCard !== undefined">
        <label for="idCard">身份证号</label>
        <input 
          type="text" 
          id="idCard" 
          v-model="idCard" 
          placeholder="请输入身份证号"
        />
        <div class="input-error" v-if="validationErrors.idCard">{{ validationErrors.idCard }}</div>
      </div>
      
      <div class="form-group location-group" v-if="longitude !== undefined || latitude !== undefined">
        <label>用户位置</label>
        <div class="location-inputs">
          <div class="location-input" v-if="longitude !== undefined">
            <label for="longitude">经度</label>
            <input 
              type="text" 
              id="longitude" 
              v-model="longitude" 
              placeholder="经度"
            />
          </div>
          <div class="location-input" v-if="latitude !== undefined">
            <label for="latitude">纬度</label>
            <input 
              type="text" 
              id="latitude" 
              v-model="latitude" 
              placeholder="纬度"
            />
          </div>
        </div>
      </div>
      
      <div class="error-message" v-if="errorMessage">
        {{ errorMessage }}
      </div>
      
      <div class="button-group">
        <button @click="saveProfile" class="save-btn" :disabled="isLoading">
          {{ isLoading ? '保存中...' : '保存' }}
        </button>
        <button @click="cancel" class="cancel-btn">
          取消
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserProfile',
  data() {
    return {
      userId: '',
      username: '',
      password: '',
      telephone: '',
      realName: '',
      idCard: '',
      avatarUrl: '',
      longitude: '',
      latitude: '',
      errorMessage: '',
      isLoading: false,
      validationErrors: {
        username: '',
        password: '',
        telephone: '',
        idCard: ''
      }
    }
  },
  created() {
    // 页面创建时获取用户信息
    this.fetchUserProfile();
  },
  methods: {
    // 获取用户信息
    fetchUserProfile() {
      this.isLoading = true;
      
      // 这里应该调用后端API获取用户信息
      // 模拟API请求获取用户数据
      setTimeout(() => {
        // 模拟从后端获取的数据
        const userData = {
          userId: '148625502103600905',
          username: 'jack',
          telephone: '13800138000',
          realName: '张三',
          idCard: '110101199001011234',
          avatarUrl: 'https://via.placeholder.com/150',
          longitude: '116.404',
          latitude: '39.915'
        };
        
        // 更新数据
        Object.keys(userData).forEach(key => {
          if (key in this && userData[key] !== undefined) {
            this[key] = userData[key];
          }
        });
        
        this.isLoading = false;
      }, 500);
    },
    
    // 表单验证
    validateForm() {
      let isValid = true;
      
      // 重置验证错误
      Object.keys(this.validationErrors).forEach(key => {
        this.validationErrors[key] = '';
      });
      
      // 验证用户名
      if (this.username !== undefined) {
        if (!this.username) {
          this.validationErrors.username = '昵称不能为空';
          isValid = false;
        }
      }
      
      // 验证密码（如果有输入）
      if (this.password) {
        if (this.password.length < 6) {
          this.validationErrors.password = '密码长度不能少于6位';
          isValid = false;
        }
      }
      
      // 验证电话号码（中国手机号格式）
      if (this.telephone) {
        const phoneRegex = /^1[3-9]\d{9}$/;
        if (!phoneRegex.test(this.telephone)) {
          this.validationErrors.telephone = '请输入有效的中国手机号码';
          isValid = false;
        }
      }
      
      // 验证身份证号
      if (this.idCard) {
        const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        if (!idCardRegex.test(this.idCard)) {
          this.validationErrors.idCard = '请输入有效的身份证号码';
          isValid = false;
        }
      }
      
      return isValid;
    },
    
    // 保存个人信息
    saveProfile() {
      this.isLoading = true;
      this.errorMessage = '';
      
      // 表单验证
      if (!this.validateForm()) {
        this.isLoading = false;
        return;
      }
      
      // 准备要提交的数据
      const profileData = {};
      
      // 只包含有值的字段
      ['username', 'password', 'telephone', 'realName', 'idCard', 'avatarUrl', 'longitude', 'latitude'].forEach(field => {
        if (this[field]) {
          profileData[field] = this[field];
        }
      });
      
      // 调用API保存用户信息
      // 这里应该是实际的API调用
      console.log('提交的数据:', profileData);
      
      // 模拟API请求
      setTimeout(() => {
        // 保存成功后的处理
        this.isLoading = false;
        alert('个人信息保存成功！');
        this.$router.push('/');
      }, 1000);
    },
    
    cancel() {
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 20px;
}

.profile-box {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-weight: bold;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border 0.3s;
}

input:focus {
  border-color: #667eea;
  outline: none;
}

input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.input-tip {
  font-size: 12px;
  color: #888;
  margin-top: 5px;
}

.error-message {
  color: #e74c3c;
  margin-bottom: 15px;
  font-size: 14px;
}

.button-group {
  display: flex;
  gap: 15px;
}

.save-btn, .cancel-btn {
  flex: 1;
  padding: 12px 0;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn {
  background: #667eea;
  color: white;
}

.save-btn:hover {
  background: #5a6fd8;
}

.save-btn:disabled {
  background: #a5afd7;
  cursor: not-allowed;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-preview-large {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #667eea;
  margin-bottom: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.avatar-url-group {
  width: 100%;
}

.avatar-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ddd;
}

.input-error {
  color: #e74c3c;
  font-size: 12px;
  margin-top: 5px;
}

.location-group {
  margin-bottom: 25px;
}

.location-inputs {
  display: flex;
  gap: 15px;
}

.location-input {
  flex: 1;
}

.location-input label {
  font-size: 12px;
  margin-bottom: 5px;
}

@media (max-width: 576px) {
  .profile-box {
    padding: 20px;
  }
  
  .location-inputs {
    flex-direction: column;
    gap: 10px;
  }
  
  .button-group {
    flex-direction: column;
  }
}
</style>