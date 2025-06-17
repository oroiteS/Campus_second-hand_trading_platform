<template>
  <div class="user-profile-container">
    <div class="user-profile-box">
      <h2>修改个人信息</h2>
      
      <!-- 头像放在最上方 -->
    <div class="user-profile-avatar-section">
        <img :src="avatarPreview || avatarUrl" class="user-profile-avatar-preview-large" />
        <div class="user-profile-form-group user-profile-avatar-upload-group">
          <label for="avatarUpload">上传头像</label>
          <input 
            type="file" 
            id="avatarUpload" 
            @change="handleAvatarUpload" 
            accept="image/*"
          />
          <div class="user-profile-input-tip">选择本地图片作为头像</div>
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
import axios from 'axios';

export default {
  name: 'UserProfile',
  data() {
    return {
      userId: '',
      username: '',
      telephone: '',
      realName: '',
      idCard: '',
      avatarUrl: '',
      avatarPreview: null,
      avatarFile: null,
      errorMessage: '',
      isLoading: false,
      validationErrors: {
        username: '',
        telephone: ''
      }
    }
  },
  created() {
    // 页面创建时获取用户信息
    this.fetchUserProfile();
  },
  methods: {
    // 处理头像上传
    handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (!file) return;
      
      // 检查文件类型
      if (!file.type.match('image.*')) {
        this.errorMessage = '请选择图片文件';
        return;
      }
      
      // 保存文件对象
      this.avatarFile = file;
      
      // 创建预览URL
      this.avatarPreview = URL.createObjectURL(file);
    },
    
    // 获取用户信息
    fetchUserProfile() {
      // 从localStorage获取userId
      const userId = localStorage.getItem('userId');
      if (!userId) {
        this.errorMessage = '未找到用户ID，请先登录';
        setTimeout(() => {
          this.$router.push('/');
        }, 1500);
        return;
      }
      
      this.isLoading = true;
      
      // 调用API获取用户信息
      axios.post('http://localhost:8089/api/user/info', {
        userId: userId
      })
      .then(response => {
        if (response.data.success) {
          const userData = response.data.data;
          this.userId = userData.userId;
          this.username = userData.userName;
          this.telephone = userData.telephone;
          this.realName = userData.realName;
          this.idCard = userData.idCard;
          this.avatarUrl = userData.avatarUrl;
        } else {
          this.errorMessage = response.data.message || '获取用户信息失败';
        }
      })
      .catch(error => {
        console.error('获取用户信息出错:', error);
        this.errorMessage = '获取用户信息时发生错误，请稍后再试';
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    
    // 表单验证
    validateForm() {
      let isValid = true;
      this.validationErrors = {
        username: '',
        telephone: ''
      };
      
      // 验证用户名
      if (this.username && this.username.length < 3) {
        this.validationErrors.username = '用户名不能少于3个字符';
        isValid = false;
      }
      
      // 验证电话号码
      if (this.telephone && !/^1[3-9]\d{9}$/.test(this.telephone)) {
        this.validationErrors.telephone = '请输入有效的手机号码';
        isValid = false;
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
      
      // 准备用户信息更新数据
      const userUpdateData = {
        userId: this.userId,
        userName: this.username,
        telephone: this.telephone
      };
      
      // 调用API更新用户信息
      axios.post('http://localhost:8089/api/user/update', userUpdateData)
        .then(response => {
          if (response.data.success) {
            console.log('用户信息更新成功:', response.data);
            this.isLoading = false;
            alert('个人信息保存成功！');
            this.$router.push('/');
          } else {
            throw new Error(response.data.message || '用户信息更新失败');
          }
        })
        .catch(error => {
          console.error('保存用户信息出错:', error);
          this.errorMessage = error.message || '保存信息时发生错误，请稍后再试';
          this.isLoading = false;
        });
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