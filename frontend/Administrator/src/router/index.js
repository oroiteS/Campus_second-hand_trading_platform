import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '../views/AdminLogin.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import AdminProductDetail from '../views/AdminProductDetail.vue'

const routes = [
  {
    path: '/',
    name: 'AdminLogin',
    component: AdminLogin
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
    meta: { requiresAuth: true }
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
router.beforeEach((to, from, next) => {
  const isAdminLoggedIn = localStorage.getItem('isAdminLoggedIn')
  const adminToken = localStorage.getItem('adminToken')
  
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要认证的页面
    if (!isAdminLoggedIn || !adminToken) {
      // 未登录，跳转到登录页
      next('/login')
    } else {
      // 已登录，允许访问
      next()
    }
  } else {
    // 不需要认证的页面
    if ((to.path === '/login') && isAdminLoggedIn && adminToken) {
      // 已登录用户访问登录页，跳转到管理面板
      next('/AdminDashboard')
    } 
    else if(to.path === '/' ){
      next('/login')
    }else {
      next()
    }
  }
})

export default router