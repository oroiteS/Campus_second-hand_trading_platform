<template>
  <div class="publish-page">
    <!-- 顶部导航 -->
    <header class="publish-header">
      <div class="publish-header-content">
        <button @click="goBack" class="publish-back-btn">← 返回</button>
        <h1 class="publish-page-title">发布闲置</h1>
        <div class="publish-placeholder"></div>
      </div>
    </header>

    <!-- 发布表单 -->
    <div class="publish-container">
      <form @submit.prevent="submitProduct" class="publish-form">
        <!-- 商品图片 -->
        <div class="publish-form-section">
          <label class="publish-section-title">商品图片 *</label>
          <div class="publish-image-upload">
            <div class="publish-upload-area" @click="triggerFileInput">
              <input 
                type="file" 
                ref="fileInput" 
                @change="handleImageUpload" 
                accept="image/*" 
                multiple 
                style="display: none"
              >
              <div v-if="productImages.length === 0" class="publish-upload-placeholder">
                <div class="publish-upload-icon">📷</div>
                <p>点击上传商品图片</p>
                <p class="publish-upload-tip">最多上传5张图片</p>
              </div>
              <div v-else class="publish-image-preview">
                <div v-for="(image, index) in productImages" :key="index" class="publish-preview-item">
                  <img :src="image.url" :alt="`商品图片${index + 1}`" class="publish-preview-image">
                  <button @click.stop="removeImage(index)" class="publish-remove-btn">×</button>
                </div>
                <div v-if="productImages.length < 5" class="publish-add-more" @click.stop="triggerFileInput">
                  <span>+</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="publish-form-section">
          <label class="publish-section-title">商品标题 *</label>
          <input 
            v-model="productForm.title" 
            type="text" 
            placeholder="请输入商品标题" 
            class="publish-form-input"
            maxlength="50"
            required
          >
          <div class="publish-char-count">{{ productForm.title.length }}/50</div>
        </div>

        <div class="publish-form-section">
          <label class="publish-section-title">商品分类 *</label>
          <select v-model="productForm.category" class="publish-form-select" required>
            <option value="">请选择分类</option>
            <option value="digital">数码电子</option>
            <option value="books">教材书籍</option>
            <option value="daily">生活用品</option>
            <option value="clothes">服装配饰</option>
            <option value="sports">运动器材</option>
            <option value="study">学习用品</option>
            <option value="beauty">美妆护肤</option>
            <option value="other">其他物品</option>
          </select>
        </div>

        <div class="publish-form-row">
          <div class="publish-form-section half">
            <label class="publish-section-title">价格 *</label>
            <div class="publish-price-input">
              <span class="publish-currency">¥</span>
              <input 
                v-model="productForm.price" 
                type="number" 
                placeholder="0.00" 
                class="publish-form-input price"
                min="0"
                step="0.01"
                required
              >
            </div>
          </div>
          <div class="publish-form-section half">
            <label class="publish-section-title">新旧程度 *</label>
            <select v-model="productForm.condition" class="publish-form-select" required>
              <option value="">请选择</option>
              <option value="全新">全新</option>
              <option value="9成新">9成新</option>
              <option value="8成新">8成新</option>
              <option value="7成新">7成新</option>
              <option value="6成新">6成新</option>
              <option value="5成新及以下">5成新及以下</option>
            </select>
          </div>
        </div>

        <div class="publish-form-section">
          <label class="publish-section-title">交易地点 *</label>
          <select v-model="productForm.location" class="publish-form-select" required>
            <option value="">请选择交易地点</option>
            <option value="东校区">东校区</option>
            <option value="西校区">西校区</option>
            <option value="南校区">南校区</option>
            <option value="北校区">北校区</option>
            <option value="校外">校外</option>
          </select>
        </div>

        <div class="publish-form-section">
          <label class="publish-section-title">商品描述</label>
          <textarea 
            v-model="productForm.description" 
            placeholder="请详细描述商品的使用情况、购买时间、转让原因等" 
            class="publish-form-textarea"
            maxlength="500"
            rows="4"
          ></textarea>
          <div class="publish-char-count">{{ productForm.description.length }}/500</div>
        </div>

        <div class="publish-form-section">
          <label class="publish-section-title">联系方式 *</label>
          <input 
            v-model="productForm.contact" 
            type="text" 
            placeholder="请输入手机号" 
            class="publish-form-input"
            required
          >
        </div>

        <!-- 提交按钮 -->
        <div class="publish-submit-section">
          <button type="button" @click="saveDraft" class="publish-draft-btn">保存草稿</button>
          <button type="submit" class="publish-submit-btn" :disabled="!isFormValid">立即发布</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PublishProduct',
  data() {
    return {
      productForm: {
        title: '',
        category: '',
        price: '',
        condition: '',
        location: '',
        description: '',
        contact: ''
      },
      productImages: []
    }
  },
  computed: {
    isFormValid() {
      return this.productForm.title && 
             this.productForm.category && 
             this.productForm.price && 
             this.productForm.condition && 
             this.productForm.location && 
             this.productForm.contact &&
             this.productImages.length > 0
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    handleImageUpload(event) {
      const files = Array.from(event.target.files)
      
      files.forEach(file => {
        if (this.productImages.length >= 5) {
          alert('最多只能上传5张图片')
          return
        }
        
        if (file.type.startsWith('image/')) {
          const reader = new FileReader()
          reader.onload = (e) => {
            this.productImages.push({
              file: file,
              url: e.target.result
            })
          }
          reader.readAsDataURL(file)
        }
      })
      
      // 清空input值，允许重复选择同一文件
      event.target.value = ''
    },
    removeImage(index) {
      this.productImages.splice(index, 1)
    },
    saveDraft() {
      // 保存草稿到本地存储
      const draftData = {
        ...this.productForm,
        images: this.productImages,
        savedAt: new Date().toISOString()
      }
      localStorage.setItem('productDraft', JSON.stringify(draftData))
      alert('草稿已保存')
    },
    async submitProduct() {
      if (!this.isFormValid) {
        alert('请填写完整的商品信息')
        return
      }
      
      try {
        // 这里应该调用API提交商品数据
        const productData = {
          ...this.productForm,
          images: this.productImages,
          publishTime: new Date().toISOString(),
          sellerId: localStorage.getItem('username') || 'xy21675070351',
          status: '在售'
        }
        
        console.log('提交商品数据:', productData)
        
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 清除草稿
        localStorage.removeItem('productDraft')
        
        alert('商品发布成功！')
        this.$router.push('/')
        
      } catch (error) {
        console.error('发布失败:', error)
        alert('发布失败，请重试')
      }
    },
    loadDraft() {
      // 加载草稿
      const draftData = localStorage.getItem('productDraft')
      if (draftData) {
        try {
          const draft = JSON.parse(draftData)
          this.productForm = { ...draft }
          if (draft.images) {
            this.productImages = draft.images
          }
        } catch (error) {
          console.error('加载草稿失败:', error)
        }
      }
    }
  },
  mounted() {
    // 页面加载时尝试加载草稿
    this.loadDraft()
  }
}
</script>

<style scoped>
@import '../styles/PublishProduct.css';
</style>


