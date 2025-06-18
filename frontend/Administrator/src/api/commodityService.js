import axios from 'axios'

// 设置基础 URL（根据您的后端服务器地址调整）
const BASE_URL = 'http://localhost:8000' // 假设后端运行在 8000 端口

// 创建 axios 实例
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 商品相关 API 服务
export const commodityService = {
  // 获取所有商品列表
  async getCommodities() {
    try {
      const response = await api.get('/api/v1/commodities/')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('获取商品列表失败:', error)
      return {
        success: false,
        message: error.response?.data?.detail || '获取商品列表失败'
      }
    }
  },

  // 获取所有商品列表（带用户名）
  async getCommoditiesWithUsername() {
    try {
      const response = await api.get('/api/v1/commodities/with_username')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('获取商品列表（带用户名）失败:', error)
      return {
        success: false,
        message: error.response?.data?.detail || '获取商品列表失败'
      }
    }
  },

  // 更新商品状态
  async updateCommodityStatus(commodityId, newStatus) {
    try {
      const response = await api.post('/api/v1/commodities/update_status', null, {
        params: {
          commodity_id: commodityId,
          new_status: newStatus
        }
      })
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('更新商品状态失败:', error)
      return {
        success: false,
        message: error.response?.data?.detail || '更新商品状态失败'
      }
    }
  },
  
  // 获取待审核商品数量
  async getPendingProductsCount() {
    try {
      const response = await api.get('/api/v1/commodities/with_username')
      const pendingProducts = response.data.filter(product => product.commodity_status === 'to_sale')
      return {
        success: true,
        data: {
          count: pendingProducts.length
        }
      }
    } catch (error) {
      console.error('获取待审核商品数量失败:', error)
      return {
        success: false,
        message: error.response?.data?.detail || '获取待审核商品数量失败'
      }
    }
  }
}

export default commodityService