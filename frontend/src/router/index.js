import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserProfile from '../views/UserProfile.vue'

const routes = [
  {
    path: '/',
    name: 'HomePage',  // 更新为与组件名称一致
    component: Home
  },
  {
    path: '/login',
    name: 'LoginPage',  // 更新为与组件名称一致
    component: Login
  },
  {
    path: '/register',
    name: 'RegisterPage',  // 更新为与组件名称一致
    component: Register
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: UserProfile
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router