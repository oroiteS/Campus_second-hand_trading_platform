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
          :class="{ active: activeMenu === 'orders' }" 
          @click="activeMenu = 'orders'"
        >
          <span class="nav-icon">📋</span>
          <span class="nav-text">订单管理</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: activeMenu === 'appeals' }" 
          @click="activeMenu = 'appeals'"
        >
          <span class="nav-icon">⚖️</span>
          <span class="nav-text">申诉管理</span>
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
              <div class="stat-icon" style="background-color: #e67e22;">⚖️</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingAppeals }}</div>
                <div class="stat-label">待处理申诉</div>
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

          <!-- 公告管理 -->
          <div class="announcements-section">
            <div class="section-header">
              <h3 class="section-title">📢 公告管理</h3>
              <button class="add-btn" @click="showAnnouncementForm = true">
                <span class="add-icon">+</span> 发布新公告
              </button>
            </div>

            <div class="announcements-list" v-if="!showAnnouncementForm">
              <div class="announcement-item" v-for="announcement in announcements" :key="announcement.id">
                <!-- 公告内容 -->
                <div class="announcement-content">{{ announcement.content }}</div>
                
                <!-- 底部信息和操作按钮 -->
                <div class="announcement-footer">
                  <div class="announcement-meta">
                    <span class="announcement-time">{{ announcement.publishTime }}</span>
                    <span class="announcement-publisher">发布者: {{ announcement.publisher }}</span>
                  </div>
                  <div class="announcement-actions">
                    <button class="action-btn edit-btn" @click="editAnnouncement(announcement)">
                      编辑
                    </button>
                    <button class="action-btn delete-btn" @click="deleteAnnouncement(announcement.id)">
                      删除
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="announcement-form" v-if="showAnnouncementForm">
              <h3>{{ editingAnnouncement ? '编辑公告' : '发布新公告' }}</h3>
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
              <!-- 批量操作按钮已删除 -->
            </div>
          </div>

          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <!-- 全选复选框已删除 -->
                  <th>ID</th>
                  <th>用户名</th>
                  <th>电话</th>
                  <th>注册时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id" class="clickable-row">
                  <!-- 用户行复选框已删除 -->
                  <td @click="viewUserDetail(user.id)">{{ user.id }}</td>
                  <td @click="viewUserDetail(user.id)">
                    <div class="user-info">
                      <div class="user-avatar">{{ user.avatar || '👤' }}</div>
                      <div class="user-name">{{ user.username }}</div>
                    </div>
                  </td>
                  <td @click="viewUserDetail(user.id)">{{ user.telephone || '未设置' }}</td>
                  <td @click="viewUserDetail(user.id)">{{ user.registerTime }}</td>
                  <td @click="viewUserDetail(user.id)">
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
                      {{ user.status === 'active' ? '封号' : '解封' }}
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
              @click="goToFirstPage"
            >
              首页
            </button>
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
            <button 
              class="page-btn" 
              :disabled="currentUserPage === totalUserPages" 
              @click="goToLastPage"
            >
              末页
            </button>
          </div>
        </div>
        
        <!-- 用户详情弹窗 -->
        <div v-if="showUserDetailModal" class="modal-overlay" @click="closeUserDetailModal">
          <div class="modal-content user-detail-modal" @click.stop>
            <div class="modal-header">
              <h3>用户详情</h3>
              <button class="close-btn" @click="closeUserDetailModal">×</button>
            </div>
            <div class="modal-body">
              <div class="user-detail-info">
                <!-- 用户头像 -->
                <div class="info-row avatar-row" v-if="selectedUserDetail.avatarUrl">
                  <label>头像：</label>
                  <div class="user-avatar">
                    <img :src="selectedUserDetail.avatarUrl" :alt="selectedUserDetail.userName" />
                  </div>
                </div>
                
                <div class="info-row">
                  <label>用户ID：</label>
                  <span>{{ selectedUserDetail.userId }}</span>
                </div>
                <div class="info-row">
                  <label>用户名：</label>
                  <span>{{ selectedUserDetail.userName }}</span>
                </div>
                <div class="info-row">
                  <label>真实姓名：</label>
                  <span>{{ selectedUserDetail.realName || '未设置' }}</span>
                </div>
                <div class="info-row">
                  <label>身份证号：</label>
                  <span>{{ selectedUserDetail.idCard || '未设置' }}</span>
                </div>
                <div class="info-row">
                  <label>电话：</label>
                  <span>{{ selectedUserDetail.telephone || '未设置' }}</span>
                </div>
                <div class="info-row">
                  <label>账号状态：</label>
                  <span class="status-text" :class="{ 'status-banned': selectedUserDetail.userSta }">
                    {{ selectedUserDetail.userSta ? '已封号' : '正常' }}
                  </span>
                </div>
                <div class="info-row">
                  <label>地理位置：</label>
                  <span v-if="selectedUserDetail.userLocLatitude && selectedUserDetail.userLocLongitude">
                    纬度: {{ selectedUserDetail.userLocLatitude }}, 经度: {{ selectedUserDetail.userLocLongitude }}
                  </span>
                  <span v-else>未设置</span>
                </div>
                <div class="info-row">
                  <label>注册时间：</label>
                  <span>{{ selectedUserDetail.createAt }}</span>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="cancel-btn" @click="closeUserDetailModal">
                关闭
              </button>
            </div>
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
                <option value="sold">已卖出</option>
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
                        'status-rejected': product.status === 'rejected',
                        'status-sold': product.status === 'sold'
                      }"
                    >
                      {{ 
                        product.status === 'pending' ? '待审核' : 
                        product.status === 'approved' ? '已上架' : 
                        product.status === 'rejected' ? '已下架' :
                        product.status === 'sold' ? '已卖出' : '未知状态'
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
                      v-if="product.status !== 'rejected' && product.status !== 'sold'"
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
              @click="goToFirstProductPage"
            >
              首页
            </button>
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
            <button 
              class="page-btn" 
              :disabled="currentProductPage === totalProductPages" 
              @click="goToLastProductPage"
            >
              末页
            </button>
          </div>
        </div>

        <!-- 订单管理 -->
        <div v-if="activeMenu === 'orders'" class="orders-content">
          <div class="content-header">
            <div class="search-box">
              <input type="text" placeholder="搜索订单..." v-model="orderSearchQuery" @input="searchOrders" />
              <span class="search-icon">🔍</span>
            </div>
            <div class="filter-actions">
              <select v-model="orderStatusFilter" @change="filterOrders">
                <option value="all">所有状态</option>
                <option value="pending">待付款</option>
                <option value="paid">已付款</option>
                <option value="shipped">已发货</option>
                <option value="completed">已完成</option>
                <option value="cancelled">已取消</option>
                <option value="refunding">退款中</option>
                <option value="refunded">已退款</option>
              </select>
            </div>
          </div>

          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>订单ID</th>
                  <th>买家</th>
                  <th>卖家</th>
                  <th>商品名称</th>
                  <th>订单金额</th>
                  <th>订单状态</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in filteredOrders" :key="order.id" class="clickable-row">
                  <td @click="viewOrderDetail(order.id)">{{ order.id }}</td>
                  <td @click="viewOrderDetail(order.id)">{{ order.buyerName }}</td>
                  <td @click="viewOrderDetail(order.id)">{{ order.sellerName }}</td>
                  <td @click="viewOrderDetail(order.id)">
                    <div class="product-info">
                      <div class="product-image">
                        <img :src="order.productImage" :alt="order.productName" />
                      </div>
                      <div class="product-name">{{ order.productName }}</div>
                    </div>
                  </td>
                  <td @click="viewOrderDetail(order.id)">¥{{ order.totalAmount }}</td>
                  <td @click="viewOrderDetail(order.id)">
                    <span 
                      class="status-badge" 
                      :class="{
                        'status-pending': order.status === 'pending',
                        'status-paid': order.status === 'paid',
                        'status-shipped': order.status === 'shipped',
                        'status-completed': order.status === 'completed',
                        'status-cancelled': order.status === 'cancelled',
                        'status-refunding': order.status === 'refunding',
                        'status-refunded': order.status === 'refunded'
                      }"
                    >
                      {{ getOrderStatusText(order.status) }}
                    </span>
                  </td>
                  <td @click="viewOrderDetail(order.id)">{{ order.createTime }}</td>
                  <td @click.stop>
                    <button 
                      v-if="order.status === 'refunding'"
                      class="action-btn approve-btn" 
                      @click="processRefund(order.id, 'approve')"
                    >
                      同意退款
                    </button>
                    <button 
                      v-if="order.status === 'refunding'"
                      class="action-btn reject-btn" 
                      @click="processRefund(order.id, 'reject')"
                    >
                      拒绝退款
                    </button>
                    <button 
                      class="action-btn detail-btn" 
                      @click="viewOrderDetail(order.id)"
                    >
                      详情
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pagination">
            <button 
              class="page-btn" 
              :disabled="currentOrderPage === 1" 
              @click="goToFirstOrderPage"
            >
              首页
            </button>
            <button 
              class="page-btn" 
              :disabled="currentOrderPage === 1" 
              @click="currentOrderPage--"
            >
              上一页
            </button>
            <span class="page-info">{{ currentOrderPage }} / {{ totalOrderPages }}</span>
            <button 
              class="page-btn" 
              :disabled="currentOrderPage === totalOrderPages" 
              @click="currentOrderPage++"
            >
              下一页
            </button>
            <button 
              class="page-btn" 
              :disabled="currentOrderPage === totalOrderPages" 
              @click="goToLastOrderPage"
            >
              末页
            </button>
          </div>
        </div>

        <!-- 申诉管理 -->
        <div v-if="activeMenu === 'appeals'" class="appeals-content">
          <div class="content-header">
            <div class="filter-actions">
              <select v-model="appealStatusFilter" @change="filterAppeals">
                <option value="all">所有状态</option>
                <option value="pending">待处理</option>
                <option value="processing">处理中</option>
                <option value="resolved">已解决</option>
                <option value="rejected">已拒绝</option>
              </select>
            </div>
          </div>

          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr>
                  <th>申诉ID</th>
                  <th>发起申诉者ID</th>
                  <th>被申诉者ID</th>
                  <th>订单ID</th>
                  <th>申诉理由</th>
                  <th>申诉发起时间</th>
                  <th>申诉是否完成</th>
                  <th>申诉处理管理员ID</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="refund in filteredRefunds" :key="refund.id">
                  <td>{{ refund.id }}</td>
                  <td>{{ refund.applicantId }}</td>
                  <td>{{ refund.respondentId }}</td>
                  <td>{{ refund.orderId }}</td>
                  <td class="content-cell" :title="refund.reason">
                    {{ refund.reason.length > 20 ? refund.reason.substring(0, 20) + '...' : refund.reason }}
                  </td>
                  <td>{{ refund.submitTime }}</td>
                  <td>
                    <span 
                      class="status-badge" 
                      :class="getStatusClass(refund.status)"
                    >
                      {{ refund.isCompleted ? '已完成' : '未完成' }}
                    </span>
                  </td>
                  <td>{{ refund.adminId || '未分配' }}</td>
                  <td>
                    <button 
                      v-if="refund.status === 'pending'"
                      class="action-btn approve-btn" 
                      @click="approveRefund(refund)"
                    >
                      同意
                    </button>
                    <button 
                      v-if="refund.status === 'pending'"
                      class="action-btn reject-btn" 
                      @click="rejectRefund(refund)"
                    >
                      拒绝
                    </button>
                    <button 
                      class="action-btn detail-btn" 
                      @click="viewRefundDetail(refund)"
                    >
                      详情
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pagination">
            <button 
              class="page-btn" 
              :disabled="currentAppealPage === 1" 
              @click="goToFirstAppealPage"
            >
              首页
            </button>
            <button 
              class="page-btn" 
              :disabled="currentAppealPage === 1" 
              @click="currentAppealPage--"
            >
              上一页
            </button>
            <span class="page-info">{{ currentAppealPage }} / {{ totalAppealPages }}</span>
            <button 
              class="page-btn" 
              :disabled="currentAppealPage === totalAppealPages" 
              @click="currentAppealPage++"
            >
              下一页
            </button>
            <button 
              class="page-btn" 
              :disabled="currentAppealPage === totalAppealPages" 
              @click="goToLastAppealPage"
            >
              末页
            </button>
          </div>
        </div>

        <!-- 公告管理 -->
        <div v-if="activeMenu === 'announcements'" class="announcements-content">
          <div class="content-header">
            <h2>公告管理</h2>
            <button class="add-btn" @click="showAnnouncementForm = true">
              <span class="add-icon">+</span> 发布新公告
            </button>
          </div>

          <div class="announcements-list" v-if="!showAnnouncementForm">
            <div class="announcement-item" v-for="announcement in announcements" :key="announcement.id">
              <!-- 公告内容 -->
              <div class="announcement-content">{{ announcement.content }}</div>
              
              <!-- 底部信息和操作按钮 -->
              <div class="announcement-footer">
                <div class="announcement-meta">
                  <span class="announcement-time">{{ announcement.publishTime }}</span>
                  <span class="announcement-publisher">发布者: {{ announcement.publisher }}</span>
                </div>
                <div class="announcement-actions">
                  <button class="action-btn edit-btn" @click="editAnnouncement(announcement)">
                    编辑
                  </button>
                  <button class="action-btn delete-btn" @click="deleteAnnouncement(announcement.id)">
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="announcement-form" v-if="showAnnouncementForm">
            <h3>{{ editingAnnouncement ? '编辑公告' : '发布新公告' }}</h3>
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
import { userService, commodityService } from '../api/services';
import { banService } from '../api/banService';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      adminUsername: localStorage.getItem('adminUsername') || '管理员',
      adminId: localStorage.getItem('adminId') || 'ADMIN0001',
      activeMenu: 'dashboard',
      
      // 统计数据
      stats: {
        totalUsers: 0,
        totalProducts: 0,
        pendingAppeals: 0,
        pendingProducts: 0
      },
      
      // 用户管理
      users: [],
      filteredUsers: [],
      userSearchQuery: '',
      userStatusFilter: 'all',
      currentUserPage: 1,
      totalUserPages: 1,
      
      // 用户详情弹窗
      showUserDetailModal: false,
      selectedUserDetail: {
        userId: '',
        userName: '',
        realName: '',
        telephone: '',
        userSta: false,
        createAt: '',
        avatarUrl: '',
        idCard: '',
        isBanned: false,
        userLocLatitude: null,
        userLocLongitude: null
      },
      
      // 商品管理
      products: [],
      filteredProducts: [],
      productSearchQuery: '',
      productStatusFilter: 'all',
      currentProductPage: 1,
      totalProductPages: 1,
      
      // 订单管理
      orders: [],
      filteredOrders: [],
      orderSearchQuery: '',
      orderStatusFilter: 'all',
      currentOrderPage: 1,
      totalOrderPages: 1,
      
      // 申诉管理
      appeals: [],
      filteredAppeals: [],
      appealSearchQuery: '',
      appealTypeFilter: 'all',
      appealStatusFilter: 'all',
      currentAppealPage: 1,
      totalAppealPages: 1,

      
      // 退款请求
      refundAppeals: [],
      filteredRefunds: [],
      
      // 公告管理
      announcements: [],
      showAnnouncementForm: false,
      editingAnnouncement: null,
      announcementForm: {
        content: ''
      },
      
      // 商品统计
      commodities: [],
      commodityStats: {
        total: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      }
      
      // 删除最近活动相关字段
      // recentActivities: []
    }
  },
  computed: {
    pageTitle() {
      switch(this.activeMenu) {
        case 'dashboard': return '控制面板';
        case 'users': return '用户管理';
        case 'products': return '商品管理';
        case 'orders': return '订单管理';
        case 'appeals': return '申诉管理';
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
  async created() {
    this.checkAdminAuth();
    this.loadDashboardData();
    await this.loadCommodityStats();
    this.loadAnnouncements();
  },
  watch: {
    activeMenu(newValue) {
      if (newValue === 'users') {
        this.loadUsers();
      } else if (newValue === 'products') {
        this.loadProducts();
      } else if (newValue === 'orders') {
        this.loadOrders();
      } else if (newValue === 'appeals') {
        this.loadAppeals();
      } else if (newValue === 'announcements') {
        this.loadAnnouncements();
      }
    },
    // 添加对用户页码变化的监听
    currentUserPage() {
      this.loadUsers();
    },
    // 添加对商品页码变化的监听
    currentProductPage() {
      this.loadProducts();
    },
    // 添加对商品状态筛选的监听
    productStatusFilter() {
      this.filterProducts();
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
      // 通过getAllUsers API获取总用户数（不分页，获取所有用户）
      userService.getAllUsers({
        pageSize: 999999, // 设置一个很大的页面大小以获取所有用户
        page: 1
      })
        .then(response => {
          // 使用API返回的total字段作为总用户数
          this.stats.totalUsers = response.total || 0;
        })
        .catch(error => {
          console.error('获取用户数据失败:', error);
          this.stats.totalUsers = 0;
        });
      
      // 计算待处理申诉数量
      this.calculatePendingAppeals();
    },
    
    // 用户管理方法
    async loadUsers() {
      try {
        const response = await userService.getAllUsers({
          page: this.currentUserPage,
          status: this.userStatusFilter === 'all' ? '' : this.userStatusFilter
        });
        
        this.users = response.data;
        this.filteredUsers = [...this.users];
        this.totalUserPages = response.totalPages || 1;
        
      } catch (error) {
        console.error('获取用户列表失败:', error);
      }
    },
    

    
    searchUsers() {
      if (!this.userSearchQuery) {
        this.filteredUsers = [...this.users];
        return;
      }
      
      const query = this.userSearchQuery.toLowerCase();
      this.filteredUsers = this.users.filter(user => 
        user.username.toLowerCase().includes(query) ||
        user.id.toString().includes(query)
      );
    },
    
    filterUsers() {
      this.currentUserPage = 1;
      this.loadUsers();
    },
    
    async toggleUserStatus(user) {
      const action = user.status === 'active' ? 'ban' : 'unban';
      const confirmMessage = action === 'ban' ? 
        `确定要封禁用户 ${user.username} 吗？` : 
        `确定要解封用户 ${user.username} 吗？`;
      
      if (confirm(confirmMessage)) {
        try {
          if (action === 'ban') {
            // 调用封号API
            const result = await banService.banUserById(user.id);
            console.log('封号结果:', result);
          } else {
            // 调用解封API
            const result = await banService.unbanUserById(user.id);
            console.log('解封结果:', result);
          }
          
          // 重新加载用户列表以获取最新状态
          await this.loadUsers();
          alert(`${action === 'ban' ? '封号' : '解封'}操作成功！`);
          
        } catch (error) {
          console.error(`${action === 'ban' ? '封号' : '解封'}操作失败:`, error);
          alert(`操作失败：${error.message}`);
        }
      }
    },
    

    
    async viewUserDetail(userId) {
      try {
        const response = await userService.getUserDetail(userId);
        this.selectedUserDetail = {
          userId: response.data.user_id,
          userName: response.data.user_name,
          realName: response.data.real_name || '未设置',
          telephone: response.data.telephone || '未设置',
          userSta: response.data.user_sta,
          createAt: new Date(response.data.create_at).toLocaleString(),
          avatarUrl: response.data.avatar_url || '',
          idCard: response.data.id_card || '未设置',
          isBanned: response.data.is_banned,
          userLocLatitude: response.data.user_loc_latitude,
          userLocLongitude: response.data.user_loc_longitude
        };
        this.showUserDetailModal = true;
      } catch (error) {
        console.error('获取用户详情失败:', error);
        alert('获取用户详情失败，请稍后重试');
      }
    },
    
    closeUserDetailModal() {
      this.showUserDetailModal = false;
      this.selectedUserDetail = {
        userId: '',
        userName: '',
        realName: '',
        telephone: '',
        userSta: false,
        createAt: ''
      };
    },
    
    async resetUserPassword() {
      if (confirm(`确定要重置用户 ${this.selectedUserDetail.userName} 的密码吗？`)) {
        try {
          const response = await userService.resetUserPassword(this.selectedUserDetail.userId);
          // 显示API返回的具体消息，包含新密码信息
          alert(response.message || '密码重置成功！');
          this.closeUserDetailModal();
        } catch (error) {
          console.error('重置密码失败:', error);
          alert('重置密码失败，请稍后重试');
        }
      }
    },
    
    // 商品管理方法
    loadProducts() {
      commodityService.getCommoditiesWithUsername({
        page: this.currentProductPage,
        status: '' // 获取所有状态的商品，在前端进行筛选
      })
        .then(response => {
          console.log('后端返回的商品数据:', response.data); // 调试日志
          
          // 处理后端返回的商品数据，映射到前端需要的格式
      this.products = response.data.map(commodity => {
        console.log('商品状态:', commodity.commodity_status); // 调试每个商品的状态
        return {
          id: commodity.commodity_id,
          name: commodity.commodity_name,
          price: commodity.current_price,
          seller: commodity.user_name, // 修改：使用 user_name 字段
          publishTime: new Date(commodity.created_at).toLocaleDateString(),
          status: this.mapCommodityStatus(commodity.commodity_status),
          originalStatus: commodity.commodity_status, // 保存原始状态用于筛选
          image: commodity.main_image_url || 'https://via.placeholder.com/50'
        };
      });
          
          console.log('处理后的商品数据:', this.products); // 调试处理后的数据
          
          // 应用筛选
          this.applyProductFilter();
          // 如果后端支持分页，使用返回的分页信息
          this.totalProductPages = response.totalPages || Math.ceil(this.filteredProducts.length / 10);
        })
        .catch(error => {
          console.error('获取商品列表失败:', error);
          alert('获取商品列表失败，请稍后重试');
        });
    },
    
    // 修正状态映射方法
    mapCommodityStatus(commodityStatus) {
      console.log('映射状态:', commodityStatus); // 调试状态映射
      const statusMap = {
        'to_sale': 'pending',     // 待审核
        'on_sale': 'approved',    // 已上架  
        'off_sale': 'rejected',   // 已下架
        'sold': 'sold',           // 已卖出
        // 添加可能的其他状态值
        'pending': 'pending',
        'approved': 'approved', 
        'rejected': 'rejected',
        'active': 'approved',
        'inactive': 'rejected'
      };
      const mappedStatus = statusMap[commodityStatus] || 'pending';
      console.log('映射结果:', commodityStatus, '->', mappedStatus);
      return mappedStatus;
    },
    
    // 应用商品筛选
    applyProductFilter() {
      console.log('开始筛选，当前筛选条件:', this.productStatusFilter); // 调试筛选条件
      let filtered = [...this.products];
      
      // 按状态筛选
      if (this.productStatusFilter !== 'all') {
        console.log('筛选前商品数量:', filtered.length);
        
        filtered = filtered.filter(product => {
          // 直接使用映射后的状态进行筛选
          const match = product.status === this.productStatusFilter;
          console.log(`商品 ${product.name} 状态: ${product.status}, 筛选条件: ${this.productStatusFilter}, 匹配: ${match}`);
          return match;
        });
        
        console.log('筛选后商品数量:', filtered.length);
      }
      
      // 按搜索关键词筛选
      if (this.productSearchQuery) {
        const query = this.productSearchQuery.toLowerCase();
        filtered = filtered.filter(product => 
          product.name.toLowerCase().includes(query) ||
          product.seller.toLowerCase().includes(query)
        );
      }
      
      this.filteredProducts = filtered;
      console.log('最终筛选结果:', this.filteredProducts);
    },
    
    searchProducts() {
      this.applyProductFilter();
    },
    
    filterProducts() {
      this.currentProductPage = 1;
      this.applyProductFilter();
    },
    
    async approveProduct(productId) {
      console.log('=== 开始审核通过操作 ===');
      console.log('商品ID:', productId);
      
      if (confirm('确定要通过审核并上架这个商品吗？')) {
        try {
          console.log('准备调用 updateCommodityStatus，参数:', {
            productId: productId,
            newStatus: 'on_sale'  // 修正：审核通过后应该是上架状态
          });
          
          const result = await commodityService.updateCommodityStatus(productId, 'on_sale');
          console.log('API调用成功，返回结果:', result);
          
          alert('商品审核通过，已上架！');
          
          console.log('开始重新加载商品列表...');
          this.loadProducts();
          
        } catch (error) {
          console.error('=== 审核通过失败 ===');
          console.error('错误详情:', error);
          console.error('错误响应:', error.response);
          console.error('错误状态码:', error.response?.status);
          console.error('错误数据:', error.response?.data);
          
          alert(`操作失败：${error.response?.data?.message || error.message || '请稍后重试'}`);
        }
      }
    },
    
    async rejectProduct(productId) {
      console.log('=== 开始下架操作 ===');
      console.log('商品ID:', productId);
      
      if (confirm('确定要下架这个商品吗？')) {
        try {
          console.log('准备调用 updateCommodityStatus，参数:', {
            productId: productId,
            newStatus: 'off_sale'
          });
          
          const result = await commodityService.updateCommodityStatus(productId, 'off_sale');
          console.log('API调用成功，返回结果:', result);
          
          alert('商品已下架！');
          
          console.log('开始重新加载商品列表...');
          this.loadProducts(); // 重新加载商品列表
          
        } catch (error) {
          console.error('=== 下架失败 ===');
          console.error('错误详情:', error);
          console.error('错误响应:', error.response);
          console.error('错误状态码:', error.response?.status);
          console.error('错误数据:', error.response?.data);
          
          alert(`操作失败：${error.response?.data?.message || error.message || '请稍后重试'}`);
        }
      }
    },
    
    viewProductDetail(productId) {
      // 跳转到管理员商品详情页面
      this.$router.push(`/admin/product/${productId}`);
    },
    
    // 申诉管理方法
    loadAppeals() {
      // 模拟退款请求数据
      this.refundAppeals = [
        {
          id: 1,
          applicantId: 'USER005',
          respondentId: 'USER002',
          orderId: 'ORD001',
          reason: '商品与描述不符，要求退款',
          submitTime: '2024-01-13 09:15:00',
          status: 'pending',
          isCompleted: false,
          adminId: null
        },
        {
          id: 2,
          applicantId: 'USER006',
          respondentId: 'USER004',
          orderId: 'ORD002',
          reason: '商品有质量问题，无法正常使用',
          submitTime: '2024-01-12 14:30:00',
          status: 'resolved',
          isCompleted: true,
          adminId: 'ADMIN0001'
        }
      ];
      
      this.filteredRefunds = [...this.refundAppeals];
      this.totalAppealPages = Math.ceil(this.refundAppeals.length / 10);
    },
    
    filterAppeals() {
      // 过滤退款请求
      let filteredRefunds = [...this.refundAppeals];
      if (this.appealStatusFilter !== 'all') {
        filteredRefunds = filteredRefunds.filter(refund => refund.status === this.appealStatusFilter);
      }
      this.filteredRefunds = filteredRefunds;
      
      this.currentAppealPage = 1;
    },
    
    // 退款请求相关方法
    approveRefund(refund) {
      if (confirm(`确定要同意退款 ¥${refund.amount} 吗？`)) {
        refund.status = 'resolved';
        this.filterAppeals();
        alert('退款申请已同意');
      }
    },
    
    rejectRefund(refund) {
      if (confirm('确定要拒绝这个退款申请吗？')) {
        refund.status = 'rejected';
        this.filterAppeals();
        alert('退款申请已拒绝');
      }
    },
    
    viewRefundDetail(refund) {
      const statusText = this.getStatusText(refund.status);
      
      alert(`退款详情：\n退款ID: ${refund.id}\n申请人: ${refund.applicant}\n订单号: ${refund.orderId}\n商品名称: ${refund.productName}\n退款金额: ¥${refund.amount}\n退款原因: ${refund.reason}\n提交时间: ${refund.submitTime}\n状态: ${statusText}`);
    },
    

    
    // 通用方法
    getStatusText(status) {
      const statusMap = {
        'pending': '待处理',
        'processing': '处理中',
        'resolved': '已解决',
        'rejected': '已拒绝'
      };
      return statusMap[status] || '未知状态';
    },
    
    getStatusClass(status) {
      return {
        'status-pending': status === 'pending',
        'status-processing': status === 'processing',
        'status-resolved': status === 'resolved',
        'status-rejected': status === 'rejected'
      };
    },
    
    // 公告管理方法
    async loadAnnouncements() {
      try {
        const adminIds = ['ADMIN0001', 'ADMIN0002', 'ADMIN0003', 'ADMIN0004', 'ADMIN0005'];
        let allAnnouncements = [];
        
        // 遍历所有管理员ID获取公告
        for (const adminId of adminIds) {
          try {
            const response = await fetch(`/api/announcements?n=9999&rootId=${adminId}`);
            if (response.ok) {
              const data = await response.json();
              // 过滤只显示可见状态的公告，并为每个公告添加发布者信息
              const visibleAnnouncements = data
                .filter(announcement => announcement.visibleStatus === true) // 只保留可见的公告
                .map(announcement => ({
                  ...announcement,
                  id: announcement.announcementId,
                  title: announcement.content.substring(0, 20) + (announcement.content.length > 20 ? '...' : ''), // 从内容生成标题
                  content: announcement.content,
                  publishTime: new Date(announcement.createdAt).toLocaleString('zh-CN'),
                  publisher: adminId
                }));
              allAnnouncements = allAnnouncements.concat(visibleAnnouncements);
            }
          } catch (error) {
            console.warn(`获取管理员 ${adminId} 的公告失败:`, error);
          }
        }
        
        // 按创建时间排序（最新的在前）
        allAnnouncements.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        this.announcements = allAnnouncements;
        
      } catch (error) {
        console.error('获取公告列表失败:', error);
        this.announcements = [];
      }
    },
    
    editAnnouncement(announcement) {
      this.editingAnnouncement = announcement;
      this.announcementForm = {
        content: announcement.content
      };
      this.showAnnouncementForm = true;
    },
    
    deleteAnnouncement(announcementId) {
      if (confirm('确定要删除这条公告吗？')) {
        // 找到要删除的公告
        const announcement = this.announcements.find(a => a.id === announcementId);
        if (!announcement) {
          alert('找不到要删除的公告');
          return;
        }
        
        // 使用 PUT 请求将 visibleStatus 设置为 false
        const deleteData = {
          announcementId: announcement.announcementId,
          rootId: announcement.publisher, // 发布者ID
          createdAt: new Date().toISOString(), // 当前更新时间
          content: announcement.content, // 保持原内容
          visibleStatus: false // 设置为不可见（删除）
        };
        
        fetch('/api/announcements', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'accept': '*/*'
          },
          body: JSON.stringify(deleteData)
        })
        .then(response => {
          if (response.ok) {
            // 删除成功，从列表中移除
            this.announcements = this.announcements.filter(a => a.id !== announcementId);
            alert('公告删除成功');
          } else {
            throw new Error('删除失败');
          }
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
        content: ''
      };
    },
    
    submitAnnouncement() {
      if (!this.announcementForm.content.trim()) {
        alert('请输入公告内容');
        return;
      }
      
      if (this.editingAnnouncement) {
        // 更新公告 - 使用新的 API
        const updateData = {
          announcementId: this.editingAnnouncement.announcementId,
          rootId: this.editingAnnouncement.publisher, // 发布者ID
          createdAt: new Date().toISOString(), // 当前更新时间
          content: this.announcementForm.content,
          visibleStatus: true // 保持可见
        };
        
        fetch('/api/announcements', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'accept': '*/*'
          },
          body: JSON.stringify(updateData)
        })
        .then(response => {
          if (response.ok) {
            // 更新成功
            this.loadAnnouncements();
            this.cancelAnnouncementForm();
            alert('公告更新成功');
          } else {
            throw new Error('更新失败');
          }
        })
        .catch(error => {
          console.error('更新公告失败:', error);
          alert('操作失败，请重试');
        });
      } else {
        // 创建新公告 - 使用新的 POST API
        this.createNewAnnouncement();
      }
    },
    
    // 新增方法：创建新公告
    async createNewAnnouncement() {
      try {
        // 1. 生成符合要求的 announcementId：ANNO + 6位数字
        const timestamp = Date.now().toString();
        // 取时间戳的后6位，前面加上ANNO前缀，总长度为10（符合 varchar(10) 限制）
        const newAnnouncementId = 'ANNO' + timestamp.slice(-6);
        
        // 2. 获取当前真实时间（使用ISO 8601格式，后端可以正确解析）
        const currentTime = new Date().toISOString();
        
        // 3. 创建新公告数据
        const newAnnouncementData = {
          announcementId: newAnnouncementId,
          rootId: this.adminId,
          createdAt: currentTime,
          content: this.announcementForm.content.trim(),
          visibleStatus: true
        };
        
        console.log('发送的数据:', newAnnouncementData);
        console.log('生成的ID:', newAnnouncementId);
        console.log('当前时间:', currentTime);
        
        // 4. 发送 POST 请求创建公告
        const createResponse = await fetch('/api/announcements', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'accept': '*/*'
          },
          body: JSON.stringify(newAnnouncementData)
        });
        
        console.log('响应状态:', createResponse.status);
        
        if (createResponse.ok) {
          // 修复：后端返回纯文本，不是JSON，所以使用text()而不是json()
          const result = await createResponse.text();
          console.log('创建成功:', result);
          this.loadAnnouncements();
          this.cancelAnnouncementForm();
          alert('公告发布成功');
        } else {
          const errorText = await createResponse.text();
          console.error('服务器错误响应:', errorText);
          throw new Error(`发布失败: ${createResponse.status} - ${errorText}`);
        }
        
      } catch (error) {
        console.error('发布公告失败:', error);
        alert(`发布失败: ${error.message}`);
      }
    },
    
    // 加载商品统计信息
    async loadCommodityStats() {
      try {
        const response = await commodityService.getCommoditiesWithUsername();
        const commodities = response.data || [];
        
        this.commodityStats = {
          total: commodities.length,
          pending: commodities.filter(c => c.commodity_status === 'to_sale').length,
          approved: commodities.filter(c => c.commodity_status === 'on sale').length,
          rejected: commodities.filter(c => c.commodity_status === 'off_sale').length
        };
        
        // 更新统计面板中的待审核商品数量
        this.stats.pendingProducts = this.commodityStats.pending;
        this.stats.totalProducts = this.commodityStats.total;
        
      } catch (error) {
        console.error('获取商品统计失败:', error);
      }
    },
    
    // 计算待处理申诉数量
    calculatePendingAppeals() {
      // 确保申诉数据已加载
      if (this.refundAppeals.length === 0) {
        this.loadAppeals();
      }
      
      const pendingRefunds = this.refundAppeals.filter(r => r.status === 'pending').length;
      
      this.stats.pendingAppeals = pendingRefunds; // 只计算退款申请
    },
    
    // 订单管理方法
    loadOrders() {
      // 模拟订单数据
      this.orders = [
        {
          id: 'ORD001',
          productName: 'iPhone 13',
          productImage: 'https://via.placeholder.com/50',
          buyerName: '用户001',
          sellerName: '用户002',
          totalAmount: 5000,
          createTime: '2024-01-13 10:30:00',
          status: 'pending'
        },
        {
          id: 'ORD002',
          productName: '笔记本电脑',
          productImage: 'https://via.placeholder.com/50',
          buyerName: '用户003',
          sellerName: '用户004',
          totalAmount: 3500,
          createTime: '2024-01-12 15:20:00',
          status: 'paid'
        },
        {
          id: 'ORD003',
          productName: '二手自行车',
          productImage: 'https://via.placeholder.com/50',
          buyerName: '用户005',
          sellerName: '用户006',
          totalAmount: 800,
          createTime: '2024-01-11 09:15:00',
          status: 'completed'
        },
        {
          id: 'ORD004',
          productName: '游戏机',
          productImage: 'https://via.placeholder.com/50',
          buyerName: '用户007',
          sellerName: '用户008',
          totalAmount: 1200,
          createTime: '2024-01-10 14:20:00',
          status: 'refunding'
        }
      ];
      
      this.filteredOrders = [...this.orders];
      this.totalOrderPages = Math.ceil(this.filteredOrders.length / 10);
    },
    
    searchOrders() {
      if (!this.orderSearchQuery) {
        this.filteredOrders = [...this.orders];
        return;
      }
      
      const query = this.orderSearchQuery.toLowerCase();
      this.filteredOrders = this.orders.filter(order => 
        order.id.toLowerCase().includes(query) ||
        order.productName.toLowerCase().includes(query) ||
        order.buyerName.toLowerCase().includes(query) ||
        order.sellerName.toLowerCase().includes(query)
      );
    },
    
    filterOrders() {
      let filtered = [...this.orders];
      
      if (this.orderStatusFilter !== 'all') {
        filtered = filtered.filter(order => order.status === this.orderStatusFilter);
      }
      
      if (this.orderSearchQuery) {
        const query = this.orderSearchQuery.toLowerCase();
        filtered = filtered.filter(order => 
          order.id.toLowerCase().includes(query) ||
          order.productName.toLowerCase().includes(query) ||
          order.buyerName.toLowerCase().includes(query) ||
          order.sellerName.toLowerCase().includes(query)
        );
      }
      
      this.filteredOrders = filtered;
      this.currentOrderPage = 1;
      this.totalOrderPages = Math.ceil(this.filteredOrders.length / 10);
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        'pending': '待付款',
        'paid': '已付款',
        'shipped': '已发货',
        'completed': '已完成',
        'cancelled': '已取消',
        'refunding': '退款中',
        'refunded': '已退款'
      };
      return statusMap[status] || '未知状态';
    },
    
    viewOrderDetail(orderId) {
      const order = this.orders.find(o => o.id === orderId);
      if (order) {
        const statusText = this.getOrderStatusText(order.status);
        
        alert(`订单详情：\n订单ID: ${order.id}\n商品名称: ${order.productName}\n买家: ${order.buyerName}\n卖家: ${order.sellerName}\n金额: ¥${order.totalAmount}\n下单时间: ${order.createTime}\n状态: ${statusText}`);
      }
    },
    
    processRefund(orderId, action) {
      const order = this.orders.find(o => o.id === orderId);
      if (order) {
        const actionText = action === 'approve' ? '同意' : '拒绝';
        if (confirm(`确定要${actionText}订单 ${orderId} 的退款申请吗？`)) {
          if (action === 'approve') {
            order.status = 'refunded';
            alert('退款申请已同意，订单状态已更新为已退款');
          } else {
            order.status = 'paid';
            alert('退款申请已拒绝，订单状态已恢复为已付款');
          }
          this.filterOrders();
        }
      }
    },
    

    
    // 分页导航方法
    goToFirstPage() {
      this.currentUserPage = 1;
    },
    
    goToLastPage() {
      this.currentUserPage = this.totalUserPages;
    },
    
    goToFirstProductPage() {
      this.currentProductPage = 1;
      this.loadProducts();
    },
    
    goToLastProductPage() {
      this.currentProductPage = this.totalProductPages;
      this.loadProducts();
    },
    
    goToFirstOrderPage() {
      this.currentOrderPage = 1;
      this.loadOrders();
    },
    
    goToLastOrderPage() {
      this.currentOrderPage = this.totalOrderPages;
      this.loadOrders();
    },
    
    goToFirstAppealPage() {
      this.currentAppealPage = 1;
      this.loadAppeals();
    },
    
    goToLastAppealPage() {
      this.currentAppealPage = this.totalAppealPages;
      this.loadAppeals();
    },
    
    // 退出登录
    logout() {
      if (confirm('确定要退出登录吗？')) {
        // 清除登录状态
        localStorage.removeItem('isAdminLoggedIn');
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUsername');
        localStorage.removeItem('adminRememberLogin');
        
        // 跳转到登录页
        this.$router.push('/login');
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
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 1000;
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
  margin-left: 250px;
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

.filter-actions {
  display: flex;
  gap: 10px;
  align-items: center;
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
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.announcement-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #666;
}

.announcement-actions {
  display: flex;
  gap: 8px;
}

.announcement-time {
  color: #999;
}

.announcement-publisher {
  color: #666;
  font-weight: bold;
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
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

/* 已卖出状态样式 */
.status-sold {
  background-color: #6c757d;
  color: white;
}

/* 订单状态样式 */
.status-paid {
  background-color: #e3f2fd;
  color: #1565c0;
}

.status-shipped {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.status-completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-cancelled {
  background-color: #ffebee;
  color: #c62828;
}

.status-refunding {
  background-color: #fff3e0;
  color: #f57c00;
}

.status-refunded {
  background-color: #fce4ec;
  color: #ad1457;
}

.cancel-order-btn {
  background-color: #ffebee;
  color: #c62828;
}

/* 申诉管理样式 */
.type-badge {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type-report {
  background-color: #ffebee;
  color: #c62828;
}

.type-refund {
  background-color: #fff8e1;
  color: #f57f17;
}

.type-password {
  background-color: #e3f2fd;
  color: #1565c0;
}

.status-processing {
  background-color: #e1f5fe;
  color: #0277bd;
}

.status-resolved {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.process-btn {
  background-color: #e1f5fe;
  color: #0277bd;
}

.resolve-btn {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.detail-btn {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

/* 申诉管理标签页样式 */
.appeals-tabs {
  display: flex;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.tab-item {
  flex: 1;
  padding: 15px 20px;
  text-align: center;
  cursor: pointer;
  background-color: #f8f9fa;
  color: #6c757d;
  border-right: 1px solid #dee2e6;
  transition: all 0.3s ease;
  font-weight: 500;
}

.tab-item:last-child {
  border-right: none;
}

.tab-item:hover {
  background-color: #e9ecef;
  color: #495057;
}

.tab-item.active {
  background-color: #3498db;
  color: white;
}

/* 内容单元格样式 */
.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: help;
}

.price-cell {
  font-weight: 600;
  color: #e74c3c;
}

/* 公告管理在控制面板中的样式 */
.announcements-section {
  margin-top: 30px;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.section-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 用户详情弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.user-detail-modal {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90vw;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 20px;
}

.user-detail-info {
  padding: 20px 0;
}

.info-row {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}

.info-row label {
  width: 100px;
  font-weight: bold;
  color: #333;
  flex-shrink: 0;
}

.info-row span {
  flex: 1;
  color: #666;
}

.avatar-row {
  align-items: flex-start;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #ddd;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-text {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-banned {
  background-color: #ffebee;
  color: #c62828;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.reset-password-btn {
  background-color: #f39c12;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.reset-password-btn:hover {
  background-color: #e67e22;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
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
  
  .modal-content {
    width: 95%;
    margin: 10px;
  }
  
  .info-row {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .info-row label {
    min-width: auto;
    margin-bottom: 5px;
  }
}
</style>