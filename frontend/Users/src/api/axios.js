import axios from "axios";

// 配置 Axios 的基础路径为网关地址
const ax1 = axios.create({
  baseURL: "http://47.117.90.63:3000", // 网关地址
  timeout: 5000000, // 请求超时时间
  withCredentials: false, // 禁止携带跨域请求的 Cookie
});
const instance = axios.create({
  baseURL: "http://47.117.90.63:8000", // 网关地址
  timeout: 5000000, // 请求超时时间
  withCredentials: false, // 禁止携带跨域请求的 Cookie
});

// 导出 Axios 实例
export  { ax1, instance };