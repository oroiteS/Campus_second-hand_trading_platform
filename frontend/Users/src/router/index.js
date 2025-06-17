import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import UserProfile from '../views/UserProfile.vue'
import Favorites from '../views/Favorites.vue'
import NearbyUsers from '../views/NearbyUsers.vue'
import ProductDetail from '../views/ProductDetail.vue'
import OrderManagement from '../views/OrderManagement.vue'
import PublishProduct from '../views/PublishProduct.vue'
import NoticeDetail from '../views/NoticeDetail.vue'
import WalletManagement from '../views/WalletManagement.vue'
import ProductBrowse from '../views/ProductBrowse.vue'
import ChatList from '@/views/ChatList.vue'

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
    path: '/browse/:categoryId',
    name: 'ProductBrowse',
    component: ProductBrowse
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
  },
  {
    path: '/wallet',
    name: 'WalletManagement',
    component: WalletManagement
  },
  {
    path: '/chat-list/:userId',
    name: 'ChatList',
    component: ChatList
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router