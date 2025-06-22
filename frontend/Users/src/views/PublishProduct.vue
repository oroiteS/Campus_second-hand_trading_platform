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

        <div class="publish-form-row">
          <div class="publish-form-section half">
            <label class="publish-section-title">商品分类 *</label>
            <select v-model="productForm.category" class="publish-form-select" required @change="loadTagsByCategory">
              <option value="">请选择分类</option>
              <option value="1">电子设备</option>
              <option value="2">教材书籍</option>
              <option value="3">生活用品</option>
              <option value="4">服装配饰</option>
              <option value="5">运动器材</option>
              <option value="6">学习用品</option>
              <option value="7">美妆护肤</option>
              <option value="8">其他</option>
            </select>
          </div>
          <div class="publish-form-section half">
            <label class="publish-section-title">商品标签 * (可多选)</label>
            <div class="tags-container" :class="{ 'disabled': !productForm.category || isLoadingTags }">
              <div v-if="isLoadingTags" class="loading-tags">加载中...</div>
              <div v-else-if="availableTags.length === 0" class="no-tags">请先选择商品分类</div>
              <div v-else class="tag-options">
                <label v-for="tag in availableTags" :key="tag.id" class="tag-checkbox">
                  <input type="checkbox" 
                    :value="tag.value" 
                    v-model="productForm.tags"
                    :disabled="!productForm.category || isLoadingTags">
                  <span>{{ tag.label }}</span>
                </label>
              </div>
            </div>
          </div>
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
              <option value="95新">95新</option>
              <option value="9成新">9成新</option>
              <option value="8成新">8成新</option>
              <option value="7成新">7成新</option>
            </select>
          </div>
        </div>

        <div class="publish-form-section">
          <label class="publish-section-title">交易数量 *</label>
          <input 
            v-model="productForm.quantity" 
            type="number" 
            placeholder="请输入数量" 
            class="publish-form-input"
            min="1"
            step="1"
            @input="validateQuantity"
            @paste.prevent
            required
          >
          <div v-if="quantityError" class="publish-error-message">{{ quantityError }}</div>
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
        <!-- 提交按钮 -->
        <div class="publish-submit-section">
          <button 
            type="submit" 
            class="publish-submit-btn full-width" 
            :disabled="!isFormValid || isSubmitting"
          >
            {{ isSubmitting ? '发布中...' : '立即发布' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import {ax1,instance} from '@/api/axios';

export default {
  name: 'PublishProduct',
  data() {
    return {
      productForm: {
        title: '',
        category: '',
        price: '',
        condition: '',
        quantity: 1,
        description: '',
        tags: []
      },
      productImages: [],
      availableTags: [],
      quantityError: '',
      isLoadingTags: false,
      isSubmitting: false
    }
  },
  computed: {
    isFormValid() {
      return this.productForm.title && 
             this.productForm.category && 
             this.productForm.price && 
             this.productForm.condition && 
             this.productForm.quantity && 
             this.productForm.tags.length > 0 && 
             this.productImages.length > 0
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    async loadTagsByCategory() {
      // 重置标签选择
      this.productForm.tags = []
      this.availableTags = []
      
      if (!this.productForm.category) {
        return
      }
      this.isLoadingTags = true
      try {
        // 调用API获取标签数据
        const response = await ax1.get('/api-8084/commodity/tags', {
          params: {
            category_id: this.productForm.category
          }
        })
        // 处理API返回的数据
        console.log('API返回数据:', response.data) // 调试用，了解实际返回的数据结构
        
        if (response.data) {
          // 检查返回的数据结构
          let tagsData = []
          // 尝试获取标签数据，适应不同的API返回结构
          if (Array.isArray(response.data)) {
            tagsData = response.data
          } else if (response.data.data && Array.isArray(response.data.data)) {
            tagsData = response.data.data
          }
          // 根据用户提供的API返回格式处理标签数据
          // 返回格式: { success: true, message: "获取标签列表成功", data: [{ tid: 1, tagName: "vivo" }, ...] }
          this.availableTags = tagsData.map(tag => ({
            id: tag.tid || tag.id || 0,
            value: (tag.tid || tag.id || '').toString(),
            label: tag.tagName || tag.name || '未命名标签'
          }))
          console.log('处理后的标签数据:', this.availableTags) // 调试用
        }
      } catch (error) {
        console.error('获取标签失败:', error)
        alert('获取标签失败，请重试')
      } finally {
        this.isLoadingTags = false
      }
    },
    validateQuantity() {
      // 验证数量是否为整数
      const quantity = this.productForm.quantity
      
      if (quantity === '') {
        this.quantityError = ''
        return
      }
      // 检查是否为小数
      if (quantity % 1 !== 0) {
        this.quantityError = '交易数量必须为整数'
        // 转换为整数
        this.productForm.quantity = Math.floor(quantity)
      } else if (quantity < 1) {
        this.quantityError = '交易数量必须大于0'
        this.productForm.quantity = 1
      } else {
        this.quantityError = ''
      }
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

    async submitProduct() {
      if (!this.isFormValid) {
        alert('请填写完整的商品信息')
        return
      }
      
      // 验证交易数量
      this.validateQuantity()
      if (this.quantityError) {
        alert(this.quantityError)
        return
      }
      // 防止重复提交
      if (this.isSubmitting) {
        return
      }
      this.isSubmitting = true
      try {
        // 准备FormData对象
        const formData = new FormData()
        // 添加商品信息字段
        formData.append('commodityName', this.productForm.title)
        formData.append('commodityDescription', this.productForm.description)
        formData.append('categoryId', parseInt(this.productForm.category))
        
        // 添加多个标签ID作为数组元素
        if (this.productForm.tags.length > 0) {
          this.productForm.tags.forEach(tagId => {
            formData.append('tagsId[]', parseInt(tagId))
          })
        }
        
        formData.append('currentPrice', parseFloat(this.productForm.price))
        formData.append('quantity', parseInt(this.productForm.quantity))
        formData.append('sellerId', localStorage.getItem('userId'))
        formData.append('newness', this.productForm.condition)
        // 添加图片文件
        this.productImages.forEach((img, index) => {
          // 如果有文件对象，使用文件对象；否则，尝试从Base64 URL创建Blob
          if (img.file) {
            formData.append(`images`, img.file)
          } else if (img.url && img.url.startsWith('data:')) {
            // 从Base64 URL创建Blob
            const byteString = atob(img.url.split(',')[1])
            const mimeString = img.url.split(',')[0].split(':')[1].split(';')[0]
            const ab = new ArrayBuffer(byteString.length)
            const ia = new Uint8Array(ab)
            for (let i = 0; i < byteString.length; i++) {
              ia[i] = byteString.charCodeAt(i)
            }
            const blob = new Blob([ab], { type: mimeString })
            formData.append(`images`, blob, `image${index}.${mimeString.split('/')[1]}`)
          }
        })
        
        console.log('准备提交商品数据...')
        
        // 调用API发布商品，使用multipart/form-data格式
        const response = await ax1.post('/api-8084/commodity/create-and-put-on-sale', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        console.log('发布响应:', response.data)
        
        if (response.data && response.data.success) {
          // 获取返回的商品数据
          const commodityData = response.data.data;
          console.log('商品数据:', commodityData);
          
          // 准备第二个API调用的请求体
           const uploadRequestData = {
             seller_id: localStorage.getItem('userId'),
             commodity_id: commodityData.commodityId,
             category_id: parseInt(this.productForm.category),
             tags: this.productForm.tags.map(tagId => parseInt(tagId))
           };
           
           console.log('准备上传商品数据到第二个API:', uploadRequestData);
           
           try {
             // 调用第二个API上传商品信息
             const uploadResponse = await instance.post('/api/v1/commodities/upload_commodity', uploadRequestData, {
               headers: {
                 'Content-Type': 'application/json'
               }
             });
            
            console.log('商品上传响应:', uploadResponse.data);
             alert('商品发布成功！');
             this.$router.push('/');
           } catch (uploadError) {
             console.error('商品信息上传失败:', uploadError);
             alert('商品发布成功，但商品信息上传到第二个系统失败: ' + (uploadError.response?.data?.message || uploadError.message || '请重试'));
             this.$router.push('/');
          }
        } else {
          const errorMessage = response.data.message || '发布失败，请重试'
          alert(errorMessage)
        }
      } catch (error) {
        console.error('发布失败:', error)
        alert('发布失败: ' + (error.response?.data?.message || error.message || '请重试'))
      } finally {
        this.isSubmitting = false
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/PublishProduct.css';
</style>


@/api/axios1