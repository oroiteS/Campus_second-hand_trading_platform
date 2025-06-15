<template>
  <div class="admin-dashboard">
    <!-- 侧边栏导航 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>管理员后台</h2>
      </div>
      <div class="admin-info">
        <div class="admin-avatar">👤</div>
        <div class="admin-name">{{ adminUsername }}</div>
      </div>
      <nav class="sidebar-nav">
        <div 
          class="nav-item" 
          :class="{ active: activeMenu === 'dashboard' }" 
          @click="activeMenu = 'dashboard'"
        >
          <span class="nav-icon">📊</span>
          <span class="nav-text">控制面板</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: activeMenu === 'users' }" 
          @click="activeMenu = 'users'"
        >
          <span class="nav-icon">👥</span>
          <span class="nav-text">用户管理</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: activeMenu === 'products' }" 
          @click="activeMenu = 'products'"
        >
          <span class="nav-icon">🛒</span>
          <span class="nav-text">商品管理</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: activeMenu === 'announcements' }" 
          @click="activeMenu = 'announcements'"
        >
          <span class="nav-icon">📢</span>
          <span class="nav-text">公告管理</span>
        </div>
      </nav>
      <div class="sidebar-footer">
        <div class="nav-item logout" @click="logout">
          <span class="nav-icon">🚪</span>
          <span class="nav-text">退出登录</span>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-bar">
        <div class="page-title">
          {{ pageTitle }}
        </div>
        <div class="top-actions">
          <div class="action-item">
            <span class="action-icon">🔔</span>
          </div>
          <div class="action-item">
            <span class="action-icon">⚙️</span>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 控制面板 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-icon" style="background-color: #3498db;">👥</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalUsers }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon" style="background-color: #2ecc71;">🛒</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalProducts }}</div>
                <div class="stat-label">总商品数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon" style="background-color: #e74c3c;">🚫</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.disabledUsers }}</div>
                <div class="stat-label">禁用用户</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon" style="background-color: #f39c12;">⏳</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingProducts }}</div>
                <div class="stat-label">待审核商品</div>
              </div>
            </div>
          </div>

          <div class="recent-activity">
            <h3>最近活动</h3>
            <div class="activity-list">
              <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
                <div class="activity-icon" :style="{ backgroundColor: activity.iconColor }">{{ activity.icon }}</div>
                <div class="activity-details">
                  <div class="activity-text">{{ activity.text }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-content">
          <div class="content-header">
            <div class="search-box">
              <input type="text" placeholder="搜索用户..." v-model="userSearchQuery" @input="searchUsers" />
              <span class="search-icon">🔍</span>
            </div>
            <div class="filter-actions">
              <select v-model="userStatusFilter" @change="filterUsers">
                <option value="all">所有状态</option>
                <option value="active">正常</option>
                <option value="disabled">已禁用</option>
              </select>
            </div>
          </div>

          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>学校</th>
                  <th>注册时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id" class="clickable-row" @click="viewUserDetail(user.id)">
                  <td>{{ user.id }}</td>
                  <td>
                    <div class="user-info">
                      <div class="user-avatar">{{ user.avatar || '👤' }}</div>
                      <div class="user-name">{{ user.username }}</div>
                    </div>
                  </td>
                  <td>{{ user.school }}</td>
                  <td>{{ user.registerTime }}</td>
                  <td>
                    <span 
                      class="status-badge" 
                      :class="{ 'status-active': user.status === 'active', 'status-disabled': user.status === 'disabled' }"
                    >
                      {{ user.status === 'active' ? '正常' : '已禁用' }}
                    </span>
                  </td>
                  <td @click.stop>
                    <button 
                      class="action-btn" 
                      :class="{ 'disable-btn': user.status === 'active', 'enable-btn': user.status === 'disabled' }"
                      @click="toggleUserStatus(user)"
                    >
                      {{ user.status === 'active' ? '禁用' : '启用' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pagination">
            <button 
              class="page-btn" 
              :disabled="currentUserPage === 1" 
              @click="currentUserPage--"
            >
              上一页
            </button>
            <span class="page-info">{{ currentUserPage }} / {{ totalUserPages }}</span>
            <button 
              class="page-btn" 
              :disabled="currentUserPage === totalUserPages" 
              @click="currentUserPage++"
            >
              下一页
            </button>
          </div>
        </div>

        <!-- 商品管理 -->
        <div v-if="activeMenu === 'products'" class="products-content">
          <div class="content-header">
            <div class="search-box">
              <input type="text" placeholder="搜索商品..." v-model="productSearchQuery" @input="searchProducts" />
              <span class="search-icon">🔍</span>
            </div>
            <div class="filter-actions">
              <select v-model="productStatusFilter" @change="filterProducts">
                <option value="all">所有状态</option>
                <option value="pending">待审核</option>
                <option value="approved">已上架</option>
                <option value="rejected">已下架</option>
              </select>
            </div>
          </div>

          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>商品名称</th>
                  <th>价格</th>
                  <th>卖家</th>
                  <th>发布时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="product in sortedFilteredProducts" :key="product.id" class="clickable-row" @click="viewProductDetail(product.id)">
                  <td>{{ product.id }}</td>
                  <td>
                    <div class="product-info">
                      <div class="product-image">
                        <img :src="product.image" :alt="product.name" />
                      </div>
                      <div class="product-name">{{ product.name }}</div>
                    </div>
                  </td>
                  <td>¥{{ product.price }}</td>
                  <td>{{ product.seller }}</td>
                  <td>{{ product.publishTime }}</td>
                  <td>
                    <span 
                      class="status-badge" 
                      :class="{
                        'status-pending': product.status === 'pending',
                        'status-approved': product.status === 'approved',
                        'status-rejected': product.status === 'rejected'
                      }"
                    >
                      {{ 
                        product.status === 'pending' ? '待审核' : 
                        product.status === 'approved' ? '已上架' : '已下架' 
                      }}
                    </span>
                  </td>
                  <td @click.stop>
                    <button 
                      v-if="product.status === 'pending'"
                      class="action-btn approve-btn" 
                      @click="approveProduct(product.id)"
                    >
                      通过
                    </button>
                    <button 
                      v-if="product.status !== 'rejected'"
                      class="action-btn reject-btn" 
                      @click="rejectProduct(product.id)"
                    >
                      下架
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pagination">
            <button 
              class="page-btn" 
              :disabled="currentProductPage === 1" 
              @click="currentProductPage--"
            >
              上一页
            </button>
            <span class="page-info">{{ currentProductPage }} / {{ totalProductPages }}</span>
            <button 
              class="page-btn" 
              :disabled="currentProductPage === totalProductPages" 
              @click="currentProductPage++"
            >
              下一页
            </button>
          </div>
        </div>

        <!-- 公告管理 -->
        <div v-if="activeMenu === 'announcements'" class="announcements-content">
          <div class="content-header">
            <button class="add-btn" @click="showAnnouncementForm = true">
              <span class="add-icon">+</span> 发布新公告
            </button>
          </div>

          <div class="announcements-list" v-if="!showAnnouncementForm">
            <div class="announcement-item" v-for="announcement in announcements" :key="announcement.id">
              <div class="announcement-header">
                <h3 class="announcement-title">{{ announcement.title }}</h3>
                <div class="announcement-actions">
                  <button class="action-btn edit-btn" @click="editAnnouncement(announcement)">
                    编辑
                  </button>
                  <button class="action-btn delete-btn" @click="deleteAnnouncement(announcement.id)">
                    删除
                  </button>
                </div>
              </div>
              <div class="announcement-content">{{ announcement.content }}</div>
              <div class="announcement-footer">
                <span class="announcement-time">发布时间: {{ announcement.publishTime }}</span>
              </div>
            </div>
          </div>

          <div class="announcement-form" v-if="showAnnouncementForm">
            <h3>{{ editingAnnouncement ? '编辑公告' : '发布新公告' }}</h3>
            <div class="form-group">
              <label for="announcement-title">公告标题</label>
              <input 
                type="text" 
                id="announcement-title" 
                v-model="announcementForm.title" 
                placeholder="请输入公告标题"
              />
            </div>
            <div class="form-group">
              <label for="announcement-content">公告内容</label>
              <textarea 
                id="announcement-content" 
                v-model="announcementForm.content" 
                placeholder="请输入公告内容"
                rows="6"
              ></textarea>
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="cancelAnnouncementForm">
                取消
              </button>
              <button class="submit-btn" @click="submitAnnouncement">
                {{ editingAnnouncement ? '保存修改' : '发布公告' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { userService } from '../api/services';
import { productService } from '../api/services';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      adminUsername: localStorage.getItem('adminUsername') || '管理员',
      activeMenu: 'dashboard',
      
      // 统计数据
      stats: {
        totalUsers: 0,
        totalProducts: 0,
        disabledUsers: 0,
        pendingProducts: 0
      },
      
      // 最近活动
      recentActivities: [],
      
      // 用户管理
      users: [],
      filteredUsers: [],
      userSearchQuery: '',
      userStatusFilter: 'all',
      currentUserPage: 1,
      totalUserPages: 1,
      
      // 商品管理
      products: [],
      filteredProducts: [],
      productSearchQuery: '',
      productStatusFilter: 'all',
      currentProductPage: 1,
      totalProductPages: 1,
      
      // 公告管理
      announcements: [],
      showAnnouncementForm: false,
      editingAnnouncement: null,
      announcementForm: {
        title: '',
        content: ''
      }
    }
  },
  computed: {
    pageTitle() {
      switch(this.activeMenu) {
        case 'dashboard': return '控制面板';
        case 'users': return '用户管理';
        case 'products': return '商品管理';
        case 'announcements': return '公告管理';
        default: return '控制面板';
      }
    },
    // 计算属性：将待审核商品置顶
    sortedFilteredProducts() {
      return [...this.filteredProducts].sort((a, b) => {
        // 待审核商品优先级最高
        if (a.status === 'pending' && b.status !== 'pending') return -1;
        if (a.status !== 'pending' && b.status === 'pending') return 1;
        // 其他商品按ID排序
        return a.id - b.id;
      });
    }
  },
  created() {
    this.checkAdminAuth();
    this.loadDashboardData();
  },
  watch: {
    activeMenu(newValue) {
      if (newValue === 'users') {
        this.loadUsers();
      } else if (newValue === 'products') {
        this.loadProducts();
      } else if (newValue === 'announcements') {
        this.loadAnnouncements();
      }
    }
  },
  methods: {
    // 检查管理员权限
    checkAdminAuth() {
      const isAdminLoggedIn = localStorage.getItem('isAdminLoggedIn');
      const adminToken = localStorage.getItem('adminToken');
      
      if (!isAdminLoggedIn || !adminToken) {
        this.$router.push('/AdminDashboard');
        return;
      }
      
      // 验证管理员token
      userService.verifyAdminToken()
        .catch(() => {
          // Token无效，跳转到登录页
          localStorage.removeItem('isAdminLoggedIn');
          localStorage.removeItem('adminToken');
          localStorage.removeItem('adminUsername');
          this.$router.push('/AdminDashboard');
        });
    },
    
    // 加载控制面板数据
    loadDashboardData() {
      // 加载统计数据
      userService.getAdminStats()
        .then(response => {
          this.stats = response.data;
        })
        .catch(error => {
          console.error('获取统计数据失败:', error);
        });
      
      // 加载最近活动
      userService.getRecentActivities()
        .then(response => {
          this.recentActivities = response.data;
        })
        .catch(error => {
          console.error('获取最近活动失败:', error);
        });
    },
    
    // 用户管理方法
    loadUsers() {
      userService.getAllUsers({
        page: this.currentUserPage,
        status: this.userStatusFilter === 'all' ? '' : this.userStatusFilter
      })
        .then(response => {
          this.users = response.data;
          this.filteredUsers = [...this.users];
          this.totalUserPages = response.totalPages || 1;
        })
        .catch(error => {
          console.error('获取用户列表失败:', error);
        });
    },
    
    searchUsers() {
      if (!this.userSearchQuery) {
        this.filteredUsers = [...this.users];
        return;
      }
      
      const query = this.userSearchQuery.toLowerCase();
      this.filteredUsers = this.users.filter(user => 
        user.username.toLowerCase().includes(query) ||
        user.school.toLowerCase().includes(query)
      );
    },
    
    filterUsers() {
      this.currentUserPage = 1;
      this.loadUsers();
    },
    
    toggleUserStatus(user) {
      const action = user.status === 'active' ? 'disable' : 'enable';
      const confirmMessage = action === 'disable' ? 
        `确定要禁用用户 ${user.username} 吗？` : 
        `确定要启用用户 ${user.username} 吗？`;
      
      if (confirm(confirmMessage)) {
        userService.updateUserStatus(user.id, { status: action === 'disable' ? 'disabled' : 'active' })
          .then(() => {
            // 更新本地数据
            user.status = action === 'disable' ? 'disabled' : 'active';
            // 重新加载用户列表
            this.loadUsers();
          })
          .catch(error => {
            console.error('更新用户状态失败:', error);
            alert('操作失败，请重试');
          });
      }
    },
    
    viewUserDetail(userId) {
      // 查看用户详情，可以在这里实现弹窗或跳转到详情页
      console.log('查看用户详情:', userId);
    },
    
    // 商品管理方法
    loadProducts() {
      productService.getAdminProducts({
        page: this.currentProductPage,
        status: this.productStatusFilter === 'all' ? '' : this.productStatusFilter
      })
        .then(response => {
          this.products = response.data;
          this.filteredProducts = [...this.products];
          this.totalProductPages = response.totalPages || 1;
        })
        .catch(error => {
          console.error('获取商品列表失败:', error);
        });
    },
    
    searchProducts() {
      if (!this.productSearchQuery) {
        this.filteredProducts = [...this.products];
        return;
      }
      
      const query = this.productSearchQuery.toLowerCase();
      this.filteredProducts = this.products.filter(product => 
        product.name.toLowerCase().includes(query) ||
        product.seller.toLowerCase().includes(query)
      );
    },
    
    filterProducts() {
      this.currentProductPage = 1;
      this.loadProducts();
    },
    
    approveProduct(productId) {
      if (confirm('确定要通过审核这个商品吗？')) {
        productService.updateProductStatus(productId, { status: 'approved' })
          .then(() => {
            // 重新加载商品列表
            this.loadProducts();
          })
          .catch(error => {
            console.error('更新商品状态失败:', error);
            alert('操作失败，请重试');
          });
      }
    },
    
    rejectProduct(productId) {
      if (confirm('确定要下架这个商品吗？')) {
        productService.updateProductStatus(productId, { status: 'rejected' })
          .then(() => {
            // 重新加载商品列表
            this.loadProducts();
          })
          .catch(error => {
            console.error('更新商品状态失败:', error);
            alert('操作失败，请重试');
          });
      }
    },
    
    viewProductDetail(productId) {
      // 跳转到管理员商品详情页面
      this.$router.push(`/admin/product/${productId}`);
    },
    
    // 公告管理方法
    loadAnnouncements() {
      userService.getAnnouncements()
        .then(response => {
          this.announcements = response.data;
        })
        .catch(error => {
          console.error('获取公告列表失败:', error);
        });
    },
    
    editAnnouncement(announcement) {
      this.editingAnnouncement = announcement;
      this.announcementForm = {
        title: announcement.title,
        content: announcement.content
      };
      this.showAnnouncementForm = true;
    },
    
    deleteAnnouncement(announcementId) {
      if (confirm('确定要删除这条公告吗？')) {
        userService.deleteAnnouncement(announcementId)
          .then(() => {
            // 从列表中移除
            this.announcements = this.announcements.filter(a => a.id !== announcementId);
          })
          .catch(error => {
            console.error('删除公告失败:', error);
            alert('操作失败，请重试');
          });
      }
    },
    
    cancelAnnouncementForm() {
      this.showAnnouncementForm = false;
      this.editingAnnouncement = null;
      this.announcementForm = {
        title: '',
        content: ''
      };
    },
    
    submitAnnouncement() {
      if (!this.announcementForm.title || !this.announcementForm.content) {
        alert('标题和内容不能为空');
        return;
      }
      
      if (this.editingAnnouncement) {
        // 更新公告
        userService.updateAnnouncement(this.editingAnnouncement.id, this.announcementForm)
          .then(() => {
            // 更新成功
            this.loadAnnouncements();
            this.cancelAnnouncementForm();
          })
          .catch(error => {
            console.error('更新公告失败:', error);
            alert('操作失败，请重试');
          });
      } else {
        // 创建新公告
        userService.createAnnouncement(this.announcementForm)
          .then(() => {
            // 创建成功
            this.loadAnnouncements();
            this.cancelAnnouncementForm();
          })
          .catch(error => {
            console.error('创建公告失败:', error);
            alert('操作失败，请重试');
          });
      }
    },
    
    // 退出登录
    logout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('isAdminLoggedIn');
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUsername');
        this.$router.push('/');
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 侧边栏样式 */
.sidebar {
  width: 250px;
  background-color: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.admin-info {
  padding: 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-avatar {
  width: 40px;
  height: 40px;
  background-color: #3498db;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  font-size: 20px;
}

.admin-name {
  font-weight: 500;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-item.active {
  background-color: #3498db;
}

.nav-icon {
  margin-right: 10px;
  font-size: 18px;
}

.sidebar-footer {
  padding: 20px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.logout {
  color: #e74c3c;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.top-bar {
  height: 60px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.top-actions {
  display: flex;
}

.action-item {
  margin-left: 20px;
  cursor: pointer;
  font-size: 18px;
}

.content-area {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

/* 控制面板样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
}

.recent-activity {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.recent-activity h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 18px;
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ecf0f1;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 18px;
}

.activity-details {
  flex: 1;
}

.activity-text {
  margin-bottom: 5px;
  color: #2c3e50;
}

.activity-time {
  font-size: 12px;
  color: #95a5a6;
}

/* 表格通用样式 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 10px 15px;
  padding-right: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #95a5a6;
}

.filter-actions select {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
}

.table-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background-color: #f8f9fa;
  padding: 15px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid #ecf0f1;
}

.data-table td {
  padding: 15px;
  border-bottom: 1px solid #ecf0f1;
  color: #2c3e50;
}

.data-table tr:last-child td {
  border-bottom: none;
}

.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.clickable-row:hover {
  background-color: #e3f2fd !important;
}

.user-info, .product-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 30px;
  height: 30px;
  background-color: #3498db;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  color: white;
  font-size: 14px;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 10px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-badge {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-disabled {
  background-color: #ffebee;
  color: #c62828;
}

.status-pending {
  background-color: #fff8e1;
  color: #f57f17;
}

.status-approved {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-rejected {
  background-color: #ffebee;
  color: #c62828;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  margin-right: 5px;
}

.disable-btn {
  background-color: #ffebee;
  color: #c62828;
}

.enable-btn {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.view-btn {
  background-color: #e3f2fd;
  color: #1565c0;
}

.approve-btn {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.reject-btn {
  background-color: #ffebee;
  color: #c62828;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
}

.page-btn {
  padding: 8px 15px;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  margin: 0 15px;
  color: #2c3e50;
}

/* 公告管理样式 */
.add-btn {
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.add-icon {
  margin-right: 5px;
  font-size: 16px;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.announcement-item {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.announcement-title {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
}

.announcement-actions {
  display: flex;
  gap: 10px;
}

.edit-btn {
  background-color: #e3f2fd;
  color: #1565c0;
}

.delete-btn {
  background-color: #ffebee;
  color: #c62828;
}

.announcement-content {
  color: #2c3e50;
  margin-bottom: 15px;
  line-height: 1.5;
}

.announcement-footer {
  display: flex;
  justify-content: flex-end;
}

.announcement-time {
  font-size: 12px;
  color: #95a5a6;
}

.announcement-form {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.announcement-form h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input, .form-group textarea {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn {
  padding: 10px 20px;
  background-color: #ecf0f1;
  color: #7f8c8d;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-dashboard {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>