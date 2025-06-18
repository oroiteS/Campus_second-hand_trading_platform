import axios from 'axios';

// 创建专门用于封号服务的axios实例，使用8082端口
const banApi = axios.create({
  baseURL: 'http://localhost:8082/api/ban',
  //baseURL: 'http://localhost:8.tcp.cpolar.cn:14893/api/ban',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
banApi.interceptors.request.use(
  config => {
    // 可以在这里添加认证token
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken) {
      config.headers.Authorization = `Bearer ${adminToken}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
banApi.interceptors.response.use(
  response => {
    return response.data; // 直接返回data部分
  },
  error => {
    console.error('Ban API Error:', error);
    if (error.response) {
      // 服务器返回错误状态码
      const message = error.response.data?.message || '操作失败';
      throw new Error(message);
    } else if (error.request) {
      // 请求发送失败
      throw new Error('网络连接失败，请检查后端服务是否启动');
    } else {
      // 其他错误
      throw new Error('请求配置错误');
    }
  }
);

// 封号服务
export const banService = {
  /**
   * 根据用户ID封号
   * @param {string|number} userId - 用户ID
   * @returns {Promise}
   */
  banUserById(userId) {
    return banApi.post(`/user/${userId}`);
  },

  /**
   * 根据用户名封号
   * @param {string} userName - 用户名
   * @returns {Promise}
   */
  banUserByName(userName) {
    return banApi.post(`/username/${userName}`);
  },

  /**
   * 根据用户ID解封
   * @param {string|number} userId - 用户ID
   * @returns {Promise}
   */
  unbanUserById(userId) {
    return banApi.delete(`/user/${userId}`);
  },

  /**
   * 根据用户名解封
   * @param {string} userName - 用户名
   * @returns {Promise}
   */
  unbanUserByName(userName) {
    return banApi.delete(`/username/${userName}`);
  },

  /**
   * 查询用户状态(根据ID)
   * @param {string|number} userId - 用户ID
   * @returns {Promise}
   */
  getUserStatusById(userId) {
    return banApi.get(`/status/user/${userId}`);
  },

  /**
   * 查询用户状态(根据用户名)
   * @param {string} userName - 用户名
   * @returns {Promise}
   */
  getUserStatusByName(userName) {
    return banApi.get(`/status/username/${userName}`);
  }
};

export default banService;