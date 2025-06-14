import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import UserProfile from '../views/UserProfile.vue'
import Favorites from '../views/Favorites.vue'
// 删除这行：import AdminDashboard from '../views/AdminDashboard.vue'
import NearbyUsers from '../views/NearbyUsers.vue'
import ProductDetail from '../views/ProductDetail.vue'
import OrderManagement from '../views/OrderManagement.vue'
import PublishProduct from '../views/PublishProduct.vue'
import NoticeDetail from '../views/NoticeDetail.vue'

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: Home
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: Login
  },
  {
    path: '/register',
    name: 'RegisterPage',
    component: Register
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/userprofile',
    name: 'UserProfile',
    component: UserProfile
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites
  },
  // 删除AdminDashboard路由配置
  // {
  //   path: '/AdminDashboard',
  //   name: 'AdminDashboard',
  //   component: AdminDashboard
  // },
  {
    path: '/nearbyusers',
    name: 'NearbyUsers',
    component: NearbyUsers
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/orders',
    name: 'OrderManagement',
    component: OrderManagement
  },
  {
    path: '/publish',
    name: 'PublishProduct',
    component: PublishProduct
  },
  {
    path: '/notice/:id',
    name: 'NoticeDetail',
    component: NoticeDetail
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router