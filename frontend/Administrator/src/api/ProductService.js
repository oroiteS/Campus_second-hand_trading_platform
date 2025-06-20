import axios from 'axios'

// 创建 axios 实例
const api = axios.create({
  baseURL: 'http://localhost:8083',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'accept': '*/*'
  }
})

// 商品详情相关 API 服务
export const productService = {
  // 获取商品详情
  async getCommodityDetail(commodityId) {
    try {
      const response = await api.get(`/api/commodity/detail/${commodityId}`)
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('获取商品详情失败:', error)
      return {
        success: false,
        message: error.response?.data?.message || '获取商品详情失败'
      }
    }
  }
}

export default productService