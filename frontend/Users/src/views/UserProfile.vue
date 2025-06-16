<template>
  <div class="user-profile-container">
    <div class="user-profile-box">
      <h2>修改个人信息</h2>
      
      <!-- 头像放在最上方 -->
      <div class="user-profile-avatar-section">
        <img :src="avatarUrl || 'https://via.placeholder.com/100x100'" class="user-profile-avatar-preview-large" />
        <div class="user-profile-form-group user-profile-avatar-url-group">
          <label for="avatarUrl">头像地址</label>
          <input 
            type="text" 
            id="avatarUrl" 
            v-model="avatarUrl" 
            placeholder="请输入头像URL地址"
          />
        </div>
      </div>
      
      <div class="user-profile-form-group" v-if="userId">
        <label for="userId">用户ID</label>
        <input 
          type="text" 
          id="userId" 
          v-model="userId" 
          placeholder="用户ID" 
          disabled
        />
        <div class="user-profile-input-tip">用户ID不可修改</div>
      </div>
      
      <div class="user-profile-form-group" v-if="username !== undefined">
        <label for="username">昵称</label>
        <input 
          type="text" 
          id="username" 
          v-model="username" 
          placeholder="请输入昵称"
        />
        <div class="user-profile-input-error" v-if="validationErrors.username">{{ validationErrors.username }}</div>
      </div>
      
      <div class="user-profile-form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          placeholder="请输入密码（不少于6位）"
        />
        <div class="user-profile-input-error" v-if="validationErrors.password">{{ validationErrors.password }}</div>
      </div>
      
      <div class="user-profile-form-group" v-if="telephone !== undefined">
        <label for="telephone">电话信息</label>
        <input 
          type="text" 
          id="telephone" 
          v-model="telephone" 
          placeholder="请输入电话号码"
        />
        <div class="user-profile-input-error" v-if="validationErrors.telephone">{{ validationErrors.telephone }}</div>
      </div>
      
      <div class="user-profile-form-group" v-if="realName !== undefined">
        <label for="realName">真实姓名</label>
        <input 
          type="text" 
          id="realName" 
          v-model="realName" 
          placeholder="真实姓名" 
          disabled
        />
        <div class="user-profile-input-tip">真实姓名不可修改</div>
      </div>
      
      <div class="user-profile-form-group" v-if="idCard !== undefined">
        <label for="idCard">身份证号</label>
        <input 
          type="text" 
          id="idCard" 
          v-model="idCard" 
          placeholder="身份证号" 
          disabled
        />
        <div class="user-profile-input-tip">身份证号不可修改</div>
      </div>
      
      <div class="user-profile-error-message" v-if="errorMessage">
        {{ errorMessage }}
      </div>
      
      <div class="user-profile-button-group">
        <button @click="saveProfile" class="user-profile-save-btn" :disabled="isLoading">
          {{ isLoading ? '保存中...' : '保存' }}
        </button>
        <button @click="cancel" class="user-profile-cancel-btn">
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
      errorMessage: '',
      isLoading: false,
      validationErrors: {
        username: '',
        password: '',
        telephone: ''
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
      
      // 身份证号不需要验证，因为已设置为不可修改
      
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
      
      // 只包含有值的字段，不包括经度纬度，真实姓名和身份证号不可修改
      ['username', 'password', 'telephone', 'avatarUrl'].forEach(field => {
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
@import '../styles/UserProfile.css';
</style>