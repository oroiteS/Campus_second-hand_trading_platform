import axios from 'axios';

// 创建axios实例
const api = axios.create({
  // 修改为正确的后端地址和端口
  baseURL: 'http://localhost:8082/api',
  // 请求超时时间
  timeout: 10000,
  // 请求头设置
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    // 例如：获取并设置token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    const res = response.data;
    
    // 根据后端约定的状态码，判断请求是否成功
    if (res.code && res.code !== 200) {
      console.error('响应错误:', res.message || '未知错误');
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        // 可以在这里处理登出逻辑
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
      
      return Promise.reject(new Error(res.message || '未知错误'));
    } else {
      return res;
    }
  },
  error => {
    // 对响应错误做点什么
    console.error('响应错误:', error);
    return Promise.reject(error);
  }
);

export default api;