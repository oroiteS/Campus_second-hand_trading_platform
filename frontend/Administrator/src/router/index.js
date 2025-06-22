import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '../views/AdminLogin.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import AdminProductDetail from '../views/AdminProductDetail.vue'

const routes = [
  {
    path: '/',
    redirect: '/AdminDashboard'
  },
  {
    path: '/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/AdminDashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    //meta: { requiresAuth: true }
  },
  {
    path: '/admin/product/:id',
    name: 'AdminProductDetail',
    component: AdminProductDetail,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const isAdminLoggedIn = localStorage.getItem('isAdminLoggedIn')
  const adminToken = localStorage.getItem('adminToken')
  
  // 如果访问登录页面，直接允许访问（移除自动跳转逻辑）
  if (to.path === '/AdminDashboard') {
    next()
    return
  }
  
  // 如果访问根路径
  if (to.path === '/') {
    next('/AdminDashboard')
    return
  }
  
  // 检查是否需要认证的页面
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAdminLoggedIn || !adminToken) {
      // 没有登录信息，跳转到登录页
      next('/AdminDashboard')
      return
    }
    
    // token有效，允许访问
    next()
  } else {
    // 不需要认证的页面，直接允许访问
    next()
  }
})

export default router