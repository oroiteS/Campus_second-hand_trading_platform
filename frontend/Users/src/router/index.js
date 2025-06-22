import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import UserProfile from '../views/UserProfile.vue'
import Favorites from '../views/Favorites.vue'
import NearbyUsers from '../views/NearbyUsers.vue'
import ProductDetail from '../views/ProductDetail.vue'
import ProductBrowse from '../views/ProductBrowse.vue'
import OrderManagement from '../views/OrderManagement.vue'
import PublishProduct from '../views/PublishProduct.vue'
import NoticeDetail from '../views/NoticeDetail.vue'
import PasswordReset from '../views/PasswordReset.vue'
import WalletManagement from '../views/WalletManagement.vue'
import SellerProfile from '../views/SellerProfile.vue'
import RecommendedProducts from '../views/RecommendedProducts.vue'
import SearchResult from '../views/SearchResult.vue'
import ChatList from '../views/ChatList.vue'

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
    path: '/browse/:categoryId?',
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
    path: '/notice',
    name: 'NoticeDetail',
    component: NoticeDetail
  },
  {    
    path: '/chat-list/:userId',
    name: 'ChatList',
    component: ChatList
  },
  {
    path: '/chat-list',
    name: 'ChatListQuery',
    component: ChatList
  },
  {
    path: '/password-reset',
    name: 'PasswordReset',
    component: PasswordReset
  },
  {
    path: '/wallet',
    name: 'WalletManagement',
    component: WalletManagement
  },
  {
    path: '/sellerprofile/:sellerId',
    name: 'SellerProfile',
    component: SellerProfile
  },
  {
    path: '/recommended',
    name: 'RecommendedProducts',
    component: RecommendedProducts
  },
   {
    path: '/search',
    name: 'SearchResult',
    component: SearchResult
  },
  {
    path: '/browse/:categoryId',
    name: 'ProductBrowseByCategory',
    component: ProductBrowse
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})
// // 路由守卫 - 防止直接URL访问
// router.beforeEach((to, from, next) => {
//   // 检查是否为直接URL访问（from.name为null表示直接访问）
//   if (from.name === null && to.name !== 'HomePage') {
//     // 直接通过URL访问非首页，重定向到首页
//     next({ name: 'HomePage' })
//     return
//   }
  
//   // 允许正常导航
//   next()
// })
export default router