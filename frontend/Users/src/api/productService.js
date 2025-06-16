import api from './index';

// 商品相关的API服务
const productService = {
  // ==================== 普通用户相关接口 ====================
  /**
   * 获取商品列表
   * @param {Object} params - 查询参数
   * @param {number} params.page - 页码
   * @param {number} params.pageSize - 每页数量
   * @param {string} params.category - 分类ID
   * @param {string} params.sort - 排序方式
   * @returns {Promise}
   */
  getProducts(params) {
    return api.get('/products', { params });
  },

  /**
   * 获取热门商品
   * @param {Object} params - 查询参数
   * @param {number} params.limit - 限制数量
   * @returns {Promise}
   */
  getHotProducts(params) {
    return api.get('/products/hot', { params });
  },

  /**
   * 获取最新商品
   * @param {Object} params - 查询参数
   * @param {number} params.limit - 限制数量
   * @returns {Promise}
   */
  getNewProducts(params) {
    return api.get('/products/new', { params });
  },

  /**
   * 获取商品详情
   * @param {string} id - 商品ID
   * @returns {Promise}
   */
  getProductDetail(id) {
    return api.get(`/products/${id}`);
  },

  /**
   * 获取商品分类
   * @returns {Promise}
   */
  getCategories() {
    return api.get('/categories');
  },

  /**
   * 获取分类下的商品
   * @param {string} categoryId - 分类ID
   * @param {Object} params - 查询参数
   * @returns {Promise}
   */
  getProductsByCategory(categoryId, params) {
    return api.get(`/categories/${categoryId}/products`, { params });
  },

  /**
   * 发布商品
   * @param {Object} productData - 商品数据
   * @returns {Promise}
   */
  publishProduct(productData) {
    return api.post('/products', productData);
  },

  /**
   * 上传商品图片
   * @param {FormData} formData - 包含图片的FormData
   * @returns {Promise}
   */
  uploadProductImage(formData) {
    return api.post('/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },

  /**
   * 搜索商品
   * @param {Object} params - 搜索参数
   * @param {string} params.keyword - 搜索关键词
   * @param {number} params.page - 页码
   * @param {number} params.pageSize - 每页数量
   * @returns {Promise}
   */
  searchProducts(params) {
    return api.get('/products/search', { params });
  },

  // ==================== 管理员相关接口 ====================
  /**
   * 管理员获取所有商品
   * @param {Object} params - 查询参数
   * @param {number} params.page - 页码
   * @param {number} params.pageSize - 每页数量
   * @param {string} params.status - 商品状态筛选 (pending, approved, rejected)
   * @returns {Promise}
   */
  getAdminProducts(params) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 模拟商品数据
          const allProducts = [
            { id: 1, name: 'iPhone 13 Pro Max', price: 7999, seller: '李四', publishTime: '2023-05-10', status: 'approved', image: 'https://img12.360buyimg.com/n1/s450x450_jfs/t1/220693/33/16553/41069/6218427dE2d411fc7/b8a6233a23b0a82d.jpg' },
            { id: 2, name: '二手笔记本电脑', price: 3500, seller: '张三', publishTime: '2023-05-15', status: 'approved', image: 'https://img14.360buyimg.com/n0/jfs/t1/106133/8/35183/543220/63fe9f01F9b3d2625/7e443ff4e9eec2c7.jpg' },
            { id: 3, name: '耐克运动鞋', price: 499, seller: '王五', publishTime: '2023-05-20', status: 'pending', image: 'https://img30.360buyimg.com/n0/jfs/t1/117573/18/23906/189634/62b53e0cE75764251/6561a7b9c5c7a2c4.jpg' },
            { id: 4, name: '二手自行车', price: 350, seller: '赵六', publishTime: '2023-05-25', status: 'pending', image: 'https://img30.360buyimg.com/n0/jfs/t1/185433/15/33473/101555/63fe4a6aF0e3f9383/1c0a3e1b9a8f1638.jpg' },
            { id: 5, name: 'AirPods Pro', price: 1299, seller: '钱七', publishTime: '2023-06-01', status: 'approved', image: 'https://img14.360buyimg.com/n0/jfs/t1/199405/29/30366/32862/63fef1e1F5ba9780a/cbc2f8f9bb2d730c.jpg' },
            { id: 6, name: '二手相机', price: 2500, seller: '孙八', publishTime: '2023-06-05', status: 'rejected', image: 'https://img30.360buyimg.com/n0/jfs/t1/187069/23/33255/56038/63fe4a6aF2e2397ef/d0f5e8e31a89eec4.jpg' },
            { id: 7, name: '电动滑板车', price: 1200, seller: '周九', publishTime: '2023-06-10', status: 'pending', image: 'https://img14.360buyimg.com/n0/jfs/t1/170424/33/35181/65342/63fe4a6aF7e5f4ecc/5e4e9bafd1d0e48d.jpg' },
            { id: 8, name: '二手iPad', price: 2300, seller: '吴十', publishTime: '2023-06-15', status: 'approved', image: 'https://img14.360buyimg.com/n0/jfs/t1/216071/19/21532/41067/63fe4a6aF5e2d0fc8/5d59c27100e87d6c.jpg' },
            { id: 9, name: '机械键盘', price: 299, seller: '郑十一', publishTime: '2023-06-20', status: 'pending', image: 'https://img14.360buyimg.com/n0/jfs/t1/220693/33/16553/41069/6218427dE2d411fc7/b8a6233a23b0a82d.jpg' },
            { id: 10, name: '二手显示器', price: 800, seller: '王十二', publishTime: '2023-06-25', status: 'approved', image: 'https://img14.360buyimg.com/n0/jfs/t1/106133/8/35183/543220/63fe9f01F9b3d2625/7e443ff4e9eec2c7.jpg' },
            { id: 11, name: '游戏手柄', price: 199, seller: '李十三', publishTime: '2023-07-01', status: 'rejected', image: 'https://img30.360buyimg.com/n0/jfs/t1/117573/18/23906/189634/62b53e0cE75764251/6561a7b9c5c7a2c4.jpg' },
            { id: 12, name: '二手书籍', price: 50, seller: '赵十四', publishTime: '2023-07-05', status: 'pending', image: 'https://img30.360buyimg.com/n0/jfs/t1/185433/15/33473/101555/63fe4a6aF0e3f9383/1c0a3e1b9a8f1638.jpg' },
            { id: 13, name: '蓝牙音箱', price: 129, seller: '钱十五', publishTime: '2023-07-10', status: 'approved', image: 'https://img14.360buyimg.com/n0/jfs/t1/199405/29/30366/32862/63fef1e1F5ba9780a/cbc2f8f9bb2d730c.jpg' },
            { id: 14, name: '二手手表', price: 1500, seller: '孙十六', publishTime: '2023-07-15', status: 'pending', image: 'https://img30.360buyimg.com/n0/jfs/t1/187069/23/33255/56038/63fe4a6aF2e2397ef/d0f5e8e31a89eec4.jpg' },
            { id: 15, name: '违规商品示例', price: 9999, seller: '周十七', publishTime: '2023-07-20', status: 'rejected', image: 'https://img14.360buyimg.com/n0/jfs/t1/170424/33/35181/65342/63fe4a6aF7e5f4ecc/5e4e9bafd1d0e48d.jpg' }
          ];
          
          // 根据状态筛选
          let filteredProducts = allProducts;
          if (params && params.status) {
            filteredProducts = allProducts.filter(product => product.status === params.status);
          }
          
          // 分页处理
          const pageSize = params?.pageSize || 10;
          const page = params?.page || 1;
          const startIndex = (page - 1) * pageSize;
          const endIndex = startIndex + pageSize;
          const paginatedProducts = filteredProducts.slice(startIndex, endIndex);
          
          resolve({
            data: paginatedProducts,
            totalPages: Math.ceil(filteredProducts.length / pageSize),
            total: filteredProducts.length
          });
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.get('/admin/products', { params });
  },

  /**
   * 更新商品状态（审核通过/下架）
   * @param {string} productId - 商品ID
   * @param {Object} data - 状态数据
   * @param {string} data.status - 状态值 ('pending', 'approved' 或 'rejected')
   * @returns {Promise}
   */
  updateProductStatus(productId, data) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 模拟更新成功
          resolve({
            success: true,
            message: `商品状态已更新为${data.status === 'approved' ? '已上架' : data.status === 'rejected' ? '已下架' : '待审核'}`,
            data: {
              id: productId,
              status: data.status
            }
          });
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.put(`/admin/products/${productId}/status`, data);
  },

  /**
   * 获取待审核商品数量
   * @returns {Promise}
   */
  getPendingProductsCount() {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 返回固定的待审核商品数量
          resolve({
            data: {
              count: 5
            }
          });
        }, 300);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.get('/admin/products/pending/count');
  }
};

export default productService;