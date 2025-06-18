import api from './index';

// 用户相关的API服务
const userService = {
  // ==================== 普通用户相关接口 ====================
  /**
   * 用户登录
   * @param {Object} loginData - 登录数据
   * @param {string} loginData.username - 用户名
   * @param {string} loginData.password - 密码
   * @returns {Promise}
   */
  login(loginData) {
    return api.post('/user/login', loginData);
  },

  /**
   * 用户注册
   * @param {Object} registerData - 注册数据
   * @param {string} registerData.username - 用户名
   * @param {string} registerData.password - 密码
   * @param {string} registerData.email - 邮箱
   * @param {string} registerData.school - 学校
   * @returns {Promise}
   */
  register(registerData) {
    return api.post('/user/register', registerData);
  },

  /**
   * 获取用户信息
   * @returns {Promise}
   */
  getUserInfo() {
    return api.get('/user/info');
  },

  /**
   * 更新用户信息
   * @param {Object} userData - 用户数据
   * @returns {Promise}
   */
  updateUserInfo(userData) {
    return api.put('/user/info', userData);
  },

  /**
   * 上传用户头像
   * @param {FormData} formData - 包含头像的FormData
   * @returns {Promise}
   */
  uploadAvatar(formData) {
    return api.post('/user/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },

  /**
   * 修改密码
   * @param {Object} passwordData - 密码数据
   * @param {string} passwordData.oldPassword - 旧密码
   * @param {string} passwordData.newPassword - 新密码
   * @returns {Promise}
   */
  changePassword(passwordData) {
    return api.put('/user/password', passwordData);
  },

  /**
   * 获取用户发布的商品
   * @param {Object} params - 查询参数
   * @returns {Promise}
   */
  getUserProducts(params) {
    return api.get('/user/products', { params });
  },

  /**
   * 获取用户收藏的商品
   * @param {Object} params - 查询参数
   * @returns {Promise}
   */
  getFavorites(params) {
    return api.get('/user/favorites', { params });
  },

  /**
   * 添加收藏
   * @param {string} productId - 商品ID
   * @returns {Promise}
   */
  addFavorite(productId) {
    return api.post('/user/favorites', { productId });
  },

  /**
   * 取消收藏
   * @param {string} productId - 商品ID
   * @returns {Promise}
   */
  removeFavorite(productId) {
    return api.delete(`/user/favorites/${productId}`);
  },

  /**
   * 退出登录
   * @returns {Promise}
   */
  logout() {
    return api.post('/user/logout');
  },

  // ==================== 管理员相关接口 ====================
  /**
   * 管理员登录
   * @param {Object} loginData - 登录数据
   * @param {string} loginData.username - 管理员用户名
   * @param {string} loginData.password - 密码
   * @returns {Promise}
   */
  adminLogin(loginData) {
    // 测试管理员账号验证
    if (loginData.username === '00000000' && loginData.password === '00000000') {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          resolve({
            token: 'admin-test-token-' + Date.now(),
            username: loginData.username
          });
        }, 500);
      });
    }
    // 如果不是测试账号，则调用实际API
    return api.post('/admin/login', loginData);
  },

  /**
   * 验证管理员token
   * @returns {Promise}
   */
  verifyAdminToken() {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          resolve({ valid: true });
        }, 300);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.get('/admin/verify');
  },

  /**
   * 获取管理员统计数据
   * @returns {Promise}
   */
  getAdminStats() {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          resolve({
            data: {
              totalUsers: 128,
              totalProducts: 356,
              disabledUsers: 5,
              pendingProducts: 12
            }
          });
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.get('/admin/stats');
  },

  /**
   * 获取最近活动
   * @param {Object} params - 查询参数
   * @param {number} params.limit - 限制数量
   * @returns {Promise}
   */
  getRecentActivities(params) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          const activities = [
            { icon: '👤', text: '新用户 张三 注册了账号', time: '10分钟前', iconColor: '#3498db' },
            { icon: '🛒', text: '用户 李四 发布了新商品「iPhone 13 Pro Max」', time: '30分钟前', iconColor: '#2ecc71' },
            { icon: '🚫', text: '管理员下架了商品「违规商品示例」', time: '1小时前', iconColor: '#e74c3c' },
            { icon: '⚠️', text: '用户 王五 的账号被禁用', time: '2小时前', iconColor: '#f39c12' },
            { icon: '✅', text: '管理员通过了商品「二手笔记本电脑」的审核', time: '3小时前', iconColor: '#27ae60' },
            { icon: '📢', text: '管理员发布了新公告「系统维护通知」', time: '5小时前', iconColor: '#9b59b6' }
          ];
          
          resolve({
            data: activities.slice(0, params?.limit || activities.length)
          });
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.get('/admin/activities', { params });
  },

  /**
   * 获取所有用户列表
   * @param {Object} params - 查询参数
   * @param {number} params.page - 页码
   * @param {number} params.pageSize - 每页数量
   * @param {string} params.status - 用户状态筛选
   * @returns {Promise}
   */
  getAllUsers(params) {
    // 使用新的API接口
    return fetch('http://localhost:8087/api/users/all')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(result => {
        console.log('API返回的原始数据:', result);
        
        if (result.code !== 200) {
          throw new Error(result.message || '获取用户信息失败');
        }
        
        // 映射API返回的数据格式到前端需要的格式
        const mappedData = result.data.map(user => ({
          id: user.userId,
          username: user.userName,
          telephone: user.telephone || '未设置',
          registerTime: user.createAt,
          // userSta: true表示已禁用，false表示正常
          status: user.userSta ? 'disabled' : 'active',
          avatar: user.avatarUrl
        }));
        
        console.log('映射后的用户数据:', mappedData);
        
        // 根据状态筛选
        let filteredUsers = mappedData;
        if (params && params.status && params.status !== 'all') {
          filteredUsers = mappedData.filter(user => user.status === params.status);
        }
        
        // 分页处理
        const pageSize = params?.pageSize || 10;
        const page = params?.page || 1;
        const startIndex = (page - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        const paginatedUsers = filteredUsers.slice(startIndex, endIndex);
        
        console.log('最终返回的数据:', {
          data: paginatedUsers,
          totalPages: Math.ceil(filteredUsers.length / pageSize),
          total: filteredUsers.length
        });
        
        return {
          data: paginatedUsers,
          totalPages: Math.ceil(filteredUsers.length / pageSize),
          total: filteredUsers.length
        };
      })
      .catch(error => {
        console.error('获取用户列表失败:', error);
        throw error;
      });
  },

  /**
   * 更新用户状态（禁用/启用）
   * @param {string} userId - 用户ID
   * @param {Object} data - 状态数据
   * @param {string} data.status - 状态值 ('active' 或 'disabled')
   * @returns {Promise}
   */
  updateUserStatus(userId, data) {
    return api.put(`/admin/users/${userId}/status`, data);
  },

  /**
   * 获取公告列表
   * @param {Object} params - 查询参数
   * @returns {Promise}
   */
  getAnnouncements(params) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 从localStorage获取公告列表，如果没有则使用默认数据
          let announcements = JSON.parse(localStorage.getItem('testAnnouncements')) || [
            { id: 1, title: '系统维护通知', content: '系统将于2023年8月1凌晨2点至4点进行维护，期间可能无法访问。', publishTime: '2023-07-25 14:30:00' },
            { id: 2, title: '新功能上线公告', content: '我们新增了商品搜索功能，现在您可以更方便地找到您想要的商品。', publishTime: '2023-07-20 10:15:00' },
            { id: 3, title: '关于禁止发布违规商品的通知', content: '请勿发布侵权、盗版或违反法律法规的商品，一经发现将立即下架并可能封禁账号。', publishTime: '2023-07-15 16:45:00' }
          ];
          
          resolve({
            data: announcements
          });
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.get('/admin/announcements', { params });
  },

  /**
   * 创建公告
   * @param {Object} data - 公告数据
   * @param {string} data.title - 公告标题
   * @param {string} data.content - 公告内容
   * @returns {Promise}
   */
  createAnnouncement(data) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 从localStorage获取公告列表，如果没有则创建空数组
          let announcements = JSON.parse(localStorage.getItem('testAnnouncements')) || [];
          
          // 创建新公告
          const newAnnouncement = {
            id: Date.now(), // 使用时间戳作为ID
            title: data.title,
            content: data.content,
            publishTime: new Date().toLocaleString()
          };
          
          // 添加到列表开头
          announcements.unshift(newAnnouncement);
          
          // 保存到localStorage
          localStorage.setItem('testAnnouncements', JSON.stringify(announcements));
          
          resolve({
            success: true,
            message: '公告创建成功',
            data: newAnnouncement
          });
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.post('/admin/announcements', data);
  },

  /**
   * 更新公告
   * @param {string} id - 公告ID
   * @param {Object} data - 公告数据
   * @param {string} data.title - 公告标题
   * @param {string} data.content - 公告内容
   * @returns {Promise}
   */
  updateAnnouncement(id, data) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 从localStorage获取公告列表
          let announcements = JSON.parse(localStorage.getItem('testAnnouncements')) || [];
          
          // 查找并更新公告
          const index = announcements.findIndex(a => a.id == id);
          if (index !== -1) {
            announcements[index] = {
              ...announcements[index],
              title: data.title,
              content: data.content,
              publishTime: new Date().toLocaleString() + ' (已编辑)'
            };
            
            // 保存到localStorage
            localStorage.setItem('testAnnouncements', JSON.stringify(announcements));
            
            resolve({
              success: true,
              message: '公告更新成功',
              data: announcements[index]
            });
          } else {
            resolve({
              success: false,
              message: '公告不存在',
              data: null
            });
          }
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.put(`/admin/announcements/${id}`, data);
  },
  
  /**
   * 删除公告
   * @param {string} id - 公告ID
   * @returns {Promise}
   */
  deleteAnnouncement(id) {
    // 检查是否是测试管理员token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken && adminToken.startsWith('admin-test-token-')) {
      return new Promise((resolve) => {
        // 模拟网络延迟
        setTimeout(() => {
          // 从localStorage获取公告列表
          let announcements = JSON.parse(localStorage.getItem('testAnnouncements')) || [];
          
          // 查找并删除公告
          const index = announcements.findIndex(a => a.id == id);
          if (index !== -1) {
            announcements.splice(index, 1);
            
            // 保存到localStorage
            localStorage.setItem('testAnnouncements', JSON.stringify(announcements));
            
            resolve({
              success: true,
              message: '公告删除成功'
            });
          } else {
            resolve({
              success: false,
              message: '公告不存在'
            });
          }
        }, 500);
      });
    }
    // 如果不是测试token，则调用实际API
    return api.delete(`/admin/announcements/${id}`);
  },

  /**
   * 获取用户详细信息（管理员接口）
   * @param {string} userId - 用户ID
   * @returns {Promise}
   */
  getUserDetail(userId) {
    return fetch('http://localhost:8089/api/user/admin/info', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'accept': '*/*'
      },
      body: JSON.stringify({
        userId: userId
      })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(result => {
      console.log('获取用户详情API返回:', result);
      
      if (result.code !== 200) {
        throw new Error(result.message || '获取用户详情失败');
      }
      
      return {
        data: {
          user_id: result.data.userId,
          user_name: result.data.userName,
          real_name: result.data.realName,
          telephone: result.data.telephone,
          user_sta: result.data.userStatus,
          create_at: result.data.createAt,
          avatar_url: result.data.avatarUrl,
          id_card: result.data.idCard,
          user_loc_latitude: result.data.userLocLatitude,
          user_loc_longitude: result.data.userLocLongitude,
          is_banned: result.data.isBanned
        }
      };
    })
    .catch(error => {
      console.error('获取用户详情失败:', error);
      throw error;
    });
  },  // 在 getUserDetail 方法后添加逗号

  /**
   * 重置用户密码（管理员接口）
   * @param {string} userId - 用户ID
   * @returns {Promise}
   */
  resetUserPassword(userId) {  // 将逗号改为开括号
    return fetch('http://localhost:8089/api/user/password/reset', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'accept': '*/*'
      },
      body: JSON.stringify({
        userId: userId
      })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(result => {
      console.log('重置密码API返回:', result);
      
      if (result.code !== 200) {
        throw new Error(result.message || '重置密码失败');
      }
      
      return result;
    })
    .catch(error => {
      console.error('重置密码失败:', error);
      throw error;
    });
  }  // 最后一个方法不需要逗号
};

export default userService;

