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
            <table class="data-table products-table">
              <thead>
                <tr>
                  <th class="id-column">ID</th>
                  <th class="product-column">商品名</th>
                  <th class="price-column">价格</th>
                  <th class="seller-column">卖家</th>
                  <th class="time-column">发布时间</th>
                  <th class="status-column">状态</th>
                  <th class="action-column">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="product in sortedFilteredProducts" :key="product.id" class="clickable-row" @click="viewProductDetail(product.id)">
                  <td class="id-column">#{{ product.id }}</td>
                  <td class="product-column">
                    <div class="product-info">
                      <div class="product-image">
                        <img :src="product.image" :alt="product.name" />
                      </div>
                      <div class="product-name">{{ product.name }}</div>
                    </div>
                  </td>
                  <td class="price-column">
                    <span class="price-value">¥{{ product.price }}</span>
                  </td>
                  <td class="seller-column">{{ product.seller }}</td>
                  <td class="time-column">
                    <span class="time-value">{{ product.publishTime }}</span>
                  </td>
                  <td class="status-column">
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
                  <td class="action-column" @click.stop>
                    <div class="action-buttons">
                      <button 
                        v-if="product.status === 'pending'"
                        class="action-btn approve-btn" 
                        @click="approveProduct(product.id)"
                        title="通过审核"
                      >
                        <span class="btn-icon">✓</span>
                        <span class="btn-text">通过</span>
                      </button>
                      <button 
                        v-if="product.status !== 'rejected' && product.status !== 'sold'"
                        class="action-btn reject-btn" 
                        @click="rejectProduct(product.id)"
                        title="下架商品"
                      >
                        <span class="btn-icon">✕</span>
                        <span class="btn-text">下架</span>
                      </button>
                    </div>
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

        <!-- 订单管理 - 重新设计的布局 -->
        <div v-if="activeMenu === 'orders'" class="orders-content">
          <!-- 页面标题和统计 -->
          <div class="orders-header">
            <div class="header-info">
              <div class="stats-summary">
                <div class="stat-item">
                  <span class="stat-number">{{ totalOrders }}</span>
                  <span class="stat-label">总订单</span>
                </div>
                <div class="stat-item processing">
                  <span class="stat-number">{{ filteredOrders.filter(o => o.status === 'pending_transaction').length }}</span>
                  <span class="stat-label">待交易</span>
                </div>
                <div class="stat-item completed">
                  <span class="stat-number">{{ filteredOrders.filter(o => o.status === 'completed').length }}</span>
                  <span class="stat-label">已完成</span>
                </div>
              </div>
            </div>
            
            <!-- 搜索和筛选 -->
            <div class="filter-section">
              <div class="search-container">
                <div class="search-input-wrapper">
                  <i class="search-icon">🔍</i>
                  <input 
                    type="text" 
                    placeholder="搜索订单ID、买家、卖家或商品名称..." 
                    v-model="orderSearchQuery" 
                    @input="searchOrders"
                    class="search-input"
                  />
                </div>
              </div>
              <div class="filter-controls">
                <select v-model="orderStatusFilter" @change="filterOrders" class="status-filter">
                  <option value="all">全部状态</option>
                  <option value="pending_transaction">待交易</option>
                  <option value="completed">已完成</option>
                </select>
                <!-- 删除了刷新按钮 -->
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p>正在加载订单数据...</p>
          </div>

          <!-- 订单卡片列表 -->
          <div v-else-if="filteredOrders.length > 0" class="orders-grid">
            <div 
              v-for="order in filteredOrders" 
              :key="order.id" 
              class="order-card"
              @click="viewOrderDetail(order.id)"
            >
              <div class="order-header">
                <div class="order-id">
                  <span class="id-label">订单号</span>
                  <span class="id-value">{{ order.id }}</span>
                </div>
                <div class="order-status">
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
                </div>
              </div>

              <div class="order-content">
                <div class="product-section">
                  <div class="product-image">
                    <img :src="order.productImage" :alt="order.productName" />
                  </div>
                  <div class="product-details">
                    <h4 class="product-name">{{ order.productName }}</h4>
                    <div class="price-info">
                      <span class="price">¥{{ order.totalAmount }}</span>
                    </div>
                  </div>
                </div>

                <div class="participants-section">
                  <div class="participant">
                    <span class="participant-label">买家</span>
                    <span class="participant-name">{{ order.buyerName }}</span>
                  </div>
                  <div class="participant">
                    <span class="participant-label">卖家</span>
                    <span class="participant-name">{{ order.sellerName }}</span>
                  </div>
                </div>

                <div class="time-section">
                  <div class="time-info">
                    <span class="time-label">创建时间</span>
                    <span class="time-value">{{ order.createTime }}</span>
                  </div>
                </div>
              </div>

              <div class="order-actions" @click.stop>
                <button 
                  v-if="order.status === 'refunding'"
                  class="action-btn approve-btn" 
                  @click="processRefund(order.id, 'approve')"
                >
                  <i class="btn-icon">✓</i>
                  同意退款
                </button>
                <button 
                  v-if="order.status === 'refunding'"
                  class="action-btn reject-btn" 
                  @click="processRefund(order.id, 'reject')"
                >
                  <i class="btn-icon">✗</i>
                  拒绝退款
                </button>
                <!-- 新增：管理员主动退款按钮 -->
                <!-- 移除：管理员主动退款按钮 -->
                <!-- <button 
                  v-if="order.status === 'pending_transaction' || order.status === 'completed'"
                  class="action-btn refund-btn" 
                  @click="initiateRefund(order)"
                >
                  <i class="btn-icon">💰</i>
                  退款
                </button> -->
                <button 
                  class="action-btn detail-btn" 
                  @click="viewOrderDetail(order.id)"
                >
                  <i class="btn-icon">👁</i>
                  查看详情
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-icon">📦</div>
            <h3>暂无订单数据</h3>
            <p>{{ orderSearchQuery || orderStatusFilter !== 'all' ? '没有找到符合条件的订单' : '还没有任何订单' }}</p>
            <button @click="loadOrders" class="retry-btn">重新加载</button>
          </div>

          <!-- 分页控件 -->
          <div v-if="totalOrderPages > 1" class="pagination-container">
            <div class="pagination-info">
              <span>共 {{ totalOrders }} 条订单，第 {{ currentOrderPage }} / {{ totalOrderPages }} 页</span>
            </div>
            <div class="pagination-controls">
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
                @click="currentOrderPage--; loadOrders()"
              >
                上一页
              </button>
              
              <!-- 页码显示 -->
              <div class="page-numbers">
                <button 
                  v-for="page in getVisiblePages(currentOrderPage, totalOrderPages)" 
                  :key="page"
                  class="page-number"
                  :class="{ active: page === currentOrderPage }"
                  @click="currentOrderPage = page; loadOrders()"
                >
                  {{ page }}
                </button>
              </div>
              
              <button 
                class="page-btn" 
                :disabled="currentOrderPage === totalOrderPages" 
                @click="currentOrderPage++; loadOrders()"
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
        </div>
        
        <!-- 订单详情弹窗（用于申诉退款） -->
        <div v-if="showOrderDetailModal" class="modal-overlay" @click="closeOrderDetailModal">
          <div class="modal-content order-detail-modal" @click.stop>
            <div class="modal-header">
              <h3>订单详情</h3>
              <button class="close-btn" @click="closeOrderDetailModal">×</button>
            </div>
            <div class="modal-body">
              <div class="order-detail-info" v-if="selectedOrderForRefund">
                <div class="info-row">
                  <label>订单ID：</label>
                  <span>{{ selectedOrderForRefund.id }}</span>
                </div>
                <div class="info-row">
                  <label>商品名称：</label>
                  <span>{{ selectedOrderForRefund.productName }}</span>
                </div>
                <div class="info-row">
                  <label>买家：</label>
                  <span>{{ selectedOrderForRefund.buyerName }}</span>
                </div>
                <div class="info-row">
                  <label>卖家：</label>
                  <span>{{ selectedOrderForRefund.sellerName }}</span>
                </div>
                <div class="info-row">
                  <label>订单金额：</label>
                  <span>¥{{ selectedOrderForRefund.totalAmount }}</span>
                </div>
                <div class="info-row">
                  <label>订单状态：</label>
                  <span class="status-text">{{ getOrderStatusText(selectedOrderForRefund.status) }}</span>
                </div>
                <div class="info-row">
                  <label>创建时间：</label>
                  <span>{{ selectedOrderForRefund.createTime }}</span>
                </div>
                <div class="info-row" v-if="relatedAppeal">
                  <label>申诉状态：</label>
                  <span class="appeal-status" :class="'status-' + relatedAppeal.status">
                    {{ relatedAppeal.status === 'process' ? '处理中' : relatedAppeal.status === 'finish' ? '已完成' : '已拒绝' }}
                  </span>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="cancel-btn" @click="closeOrderDetailModal">
                关闭
              </button>
              <!-- 退款按钮 - 只在申诉状态为process时显示 -->
              <button 
                v-if="relatedAppeal && relatedAppeal.status === 'process'"
                class="refund-btn"
                @click="initiateRefundFromAppeal(selectedOrderForRefund, relatedAppeal)"
              >
                💰 执行退款
              </button>
            </div>
          </div>
        </div>

        <!-- 申诉管理 -->
        <div v-if="activeMenu === 'appeals'" class="appeals-content">
          <!-- 页面标题和统计 -->
          <div class="appeals-header">
            <div class="header-info">
              <div class="stats-summary">
                <div class="stat-item">
                  <span class="stat-number">{{ refundAppeals.length }}</span>
                  <span class="stat-label">总申诉</span>
                </div>
                <div class="stat-item pending">
                  <span class="stat-number">{{ refundAppeals.filter(r => r.status === 'process').length }}</span>
                  <span class="stat-label">待处理</span>
                </div>
                <div class="stat-item completed">
                  <span class="stat-number">{{ refundAppeals.filter(r => r.status === 'finish').length }}</span>
                  <span class="stat-label">已完成</span>
                </div>
              </div>
            </div>
            
            <!-- 筛选器 -->
            <div class="filter-section">
              <div class="filter-group">
                <label class="filter-label">状态筛选</label>
                <select v-model="appealStatusFilter" @change="filterAppeals" class="modern-select">
                  <option value="all">🔍 所有状态</option>
                  <option value="process">⏳ 处理中</option>
                  <option value="finish">✅ 已完成</option>
                  <option value="refuse">❌ 已拒绝</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 申诉列表 - 卡片式布局 -->
          <div class="appeals-grid">
            <div v-for="appeal in filteredRefunds" :key="appeal.id" class="appeal-card">
              <!-- 卡片头部 -->
              <div class="card-header">
                <div class="appeal-meta">
                  <span class="appeal-id">申诉 #{{ appeal.id.substring(0, 8) }}...</span>
                  <span class="appeal-time">{{ formatDate(appeal.submitTime) }}</span>
                </div>
                <div class="status-container">
                  <span class="status-badge" :class="getStatusClass(appeal.status)">
                    {{ getStatusText(appeal.status) }}
                  </span>
                </div>
              </div>

              <!-- 卡片主体 -->
              <div class="card-body">
                <div class="appeal-info">
                  <div class="info-row">
                    <div class="info-item">
                      <span class="info-label">申诉人</span>
                      <span class="info-value user-id">{{ appeal.applicantId }}</span>
                    </div>
                    <div class="info-item">
                      <span class="info-label">被申诉人</span>
                      <span class="info-value user-id">{{ appeal.respondentId }}</span>
                    </div>
                  </div>
                  
                  <div class="info-row">
                    <div class="info-item full-width">
                      <span class="info-label">订单编号</span>
                      <span class="info-value order-id">{{ appeal.orderId }}</span>
                    </div>
                  </div>
                  
                  <div class="info-row">
                    <div class="info-item full-width">
                      <span class="info-label">申诉理由</span>
                      <div class="reason-content" :title="appeal.reason">
                        {{ appeal.reason }}
                      </div>
                    </div>
                  </div>
                  
                  <div class="info-row" v-if="appeal.adminId">
                    <div class="info-item">
                      <span class="info-label">处理管理员</span>
                      <span class="info-value admin-id">{{ appeal.adminId }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 卡片底部操作 -->
              <div class="card-footer">
                <div class="action-buttons">
                  <!-- 移除完成处理按钮，只保留拒绝申诉和查看详情按钮 -->
                  <button 
                    v-if="appeal.status === 'process'"
                    class="btn btn-danger" 
                    @click="rejectRefund(appeal)"
                  >
                    <i class="btn-icon">❌</i>
                    拒绝申诉
                  </button>
                  <button 
                    class="btn btn-info" 
                    @click="viewRefundDetail(appeal)"
                  >
                    <i class="btn-icon">👁️</i>
                    查看详情
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredRefunds.length === 0" class="empty-state">
            <div class="empty-icon">📋</div>
            <h3 class="empty-title">暂无申诉记录</h3>
            <p class="empty-description">当前筛选条件下没有找到申诉记录</p>
          </div>

          <!-- 分页 -->
          <div v-if="filteredRefunds.length > 0" class="pagination-container">
            <div class="pagination">
              <button 
                class="page-btn" 
                :disabled="currentAppealPage === 1" 
                @click="currentAppealPage = 1"
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
                @click="currentAppealPage = totalAppealPages"
              >
                末页
              </button>
            </div>
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
import axios from 'axios';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      adminUsername: localStorage.getItem('adminUsername') || '管理员',
      adminId: localStorage.getItem('adminId') || localStorage.getItem('adminToken') || null,
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
      totalOrders: 0, // 新增：总订单数
      loading: false, // 新增：加载状态
      
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
      
      // 订单详情弹窗（用于申诉退款）
      showOrderDetailModal: false,
      selectedOrderForRefund: null,
      relatedAppeal: null,
      
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
    
    // 处理路由查询参数
    if (this.$route.query.activeMenu) {
      this.activeMenu = this.$route.query.activeMenu;
    }
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
    },
    // 添加对订单页码变化的监听
    currentOrderPage() {
      this.loadOrders();
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
      console.log('开始加载控制面板数据...');
      
      // 直接调用API获取所有用户，不进行分页和筛选
      userService.getAllUsers({
        pageSize: 999999, // 获取所有用户
        page: 1,
        status: 'all' // 获取所有状态的用户
      })
        .then(response => {
          console.log('用户统计API响应:', response);
          // 使用映射后的所有用户数据长度作为总用户数
          this.stats.totalUsers = response.total || 0;
          console.log('设置总用户数为:', this.stats.totalUsers);
        })
        .catch(error => {
          console.error('获取用户数据失败:', error);
          this.stats.totalUsers = 0;
          
          // 如果API调用失败，尝试直接调用原始API
          fetch('http://localhost:3000/api-8087/users/all')
            .then(response => response.json())
            .then(result => {
              if (result.code === 200 && result.data) {
                this.stats.totalUsers = result.data.length;
                console.log('通过原始API获取总用户数:', this.stats.totalUsers);
              }
            })
            .catch(err => {
              console.error('原始API调用也失败:', err);
            });
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
    async loadAppeals() {
      console.log('开始加载申诉数据...');
      try {
        // 调用真实的申诉API
        const response = await axios.get('http://localhost:3000/api-8093/v1/appeals/all', {
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        console.log('申诉API响应状态:', response.status);
        console.log('申诉API响应数据:', response.data);
        
        if (response.status === 200 && response.data.appeals) {
          // 将API返回的数据格式转换为前端需要的格式
          this.refundAppeals = response.data.appeals.map(appeal => ({
            id: appeal.argumentId,
            applicantId: appeal.argue1Id,
            respondentId: appeal.argue2Id || '未指定',
            orderId: appeal.orderId,
            reason: appeal.reason,
            submitTime: this.formatDate(appeal.createdAt),
            status: this.mapAppealStatus(appeal.status),
            isCompleted: appeal.status === 'finish',
            adminId: appeal.rootId || null
          }));
          
          console.log('转换后的申诉数据:', this.refundAppeals);
          console.log(`成功加载${response.data.count}条申诉记录`);
          
          this.filteredRefunds = [...this.refundAppeals];
          this.totalAppealPages = Math.ceil(this.refundAppeals.length / 10);
          
        } else {
          console.error('API响应格式不正确:', response.data);
          throw new Error('获取申诉记录失败：响应格式不正确');
        }
        
      } catch (error) {
        console.error('加载申诉数据失败:', error);
        
        let errorMessage = '获取申诉记录失败';
        if (error.response) {
          console.error('错误响应状态:', error.response.status);
          console.error('错误响应数据:', error.response.data);
          errorMessage = `获取申诉记录失败：${error.response.status} ${error.response.statusText}`;
        } else if (error.request) {
          console.error('网络请求失败:', error.request);
          errorMessage = '无法连接到申诉服务器，请检查网络连接';
        } else {
          console.error('请求配置错误:', error.message);
          errorMessage = `获取申诉记录失败：${error.message}`;
        }
        
        alert(errorMessage);
        
        // 失败时使用空数组
        this.refundAppeals = [];
        this.filteredRefunds = [];
        this.totalAppealPages = 0;
      }
    },
    
    // 修改状态映射方法
    mapAppealStatus(apiStatus) {
      console.log('映射申诉状态:', apiStatus);
      // 直接使用API返回的状态，因为后端返回的就是这三个状态
      const validStatuses = ['finish', 'refuse', 'process'];
      return validStatuses.includes(apiStatus) ? apiStatus : 'process';
    },
    
    // 添加日期格式化方法
    formatDate(dateString) {
      if (!dateString) return '未知时间';
      try {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        });
      } catch (error) {
        console.error('日期格式化失败:', error);
        return dateString;
      }
    },
    
    filterAppeals() {
      // 过滤申诉记录
      let filteredRefunds = [...this.refundAppeals];
      if (this.appealStatusFilter !== 'all') {
        filteredRefunds = filteredRefunds.filter(refund => refund.status === this.appealStatusFilter);
      }
      
      // 排序：待处理状态置顶，然后按提交时间倒序
        filteredRefunds.sort((a, b) => {
          // 首先按状态排序：process > finish > refuse
          const statusPriority = { 'process': 0, 'finish': 1, 'refuse': 2 };
          const statusDiff = statusPriority[a.status] - statusPriority[b.status];
          if (statusDiff !== 0) {
            return statusDiff; // 状态不同时，直接返回状态差值
          }
          
          // 状态相同时，按提交时间倒序（最新的在前）
          return new Date(b.submitTime) - new Date(a.submitTime);
        });
      
      this.filteredRefunds = filteredRefunds;
      this.currentAppealPage = 1;
    },
    
    approveRefund(refund) {
      if (confirm('确定要完成处理这个申诉吗？')) {
        console.log('开始处理申诉完成请求:', refund);
        
        // 调用后端API更新申诉状态
        axios.put(`http://localhost:3000/api-8093/v1/appeals/${refund.id}/admin-update`, {
          status: 'finish',
          rootId: this.adminId // 使用当前管理员ID
        })
        .then(response => {
          console.log('申诉处理成功:', response.data);
          // 更新本地数据
          refund.status = 'finish';
          this.filterAppeals();
          alert('申诉处理已完成');
          // 重新加载申诉列表以确保数据同步
          this.loadAppeals();
        })
        .catch(error => {
          console.error('申诉处理失败:', error);
          if (error.response) {
            console.error('错误响应:', error.response.data);
            alert(`申诉处理失败: ${error.response.data.error || '未知错误'}`);
          } else {
            alert('申诉处理失败: 网络错误');
          }
        });
      }
    },
    
    rejectRefund(refund) {
      if (confirm('确定要拒绝这个申诉吗？')) {
        console.log('开始处理申诉拒绝请求:', refund);
        
        // 调用后端API更新申诉状态
        axios.put(`http://localhost:3000/api-8093/v1/appeals/${refund.id}/admin-update`, {
          status: 'refuse',
          rootId: this.adminId // 使用当前管理员ID
        })
        .then(response => {
          console.log('申诉拒绝成功:', response.data);
          // 更新本地数据
          refund.status = 'refuse';
          this.filterAppeals();
          alert('申诉已拒绝');
          // 重新加载申诉列表以确保数据同步
          this.loadAppeals();
        })
        .catch(error => {
          console.error('申诉拒绝失败:', error);
          if (error.response) {
            console.error('错误响应:', error.response.data);
            alert(`申诉拒绝失败: ${error.response.data.error || '未知错误'}`);
          } else {
            alert('申诉拒绝失败: 网络错误');
          }
        });
      }
    },
    
    viewRefundDetail(refund) {
      console.log('申诉详情数据:', refund);
      
      // 获取正确的订单ID字段
      const orderId = refund.orderId || refund.order_id || refund.argumentId || refund.id;
      console.log('提取的订单ID:', orderId);
      
      // 查找对应的订单
      const order = this.orders.find(o => o.id == orderId);
      if (order) {
        // 显示带退款功能的订单详情弹窗
        this.showOrderDetailWithRefund(order, refund);
      } else {
        // 如果当前订单列表中没有找到，在后台加载订单数据，但不切换页面
        this.$nextTick(async () => {
          try {
            if (this.orders.length === 0) {
              console.log('订单数据为空，开始加载...');
              await this.loadOrders();
            }
            
            // 再次查找订单
            const foundOrder = this.orders.find(o => o.id == orderId);
            if (foundOrder) {
              this.showOrderDetailWithRefund(foundOrder, refund);
            } else {
              // 如果还是找不到，可能需要从服务器获取特定订单
              alert('未找到对应的订单，请稍后重试');
            }
          } catch (error) {
            console.error('加载订单失败:', error);
            alert('加载订单失败，请重试');
          }
        });
      }
    },
    

    
    // 修改状态显示文本方法
    getStatusText(status) {
      const statusMap = {
        'process': '处理中',
        'finish': '已完成', 
        'refuse': '已拒绝'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 修改状态样式类方法
    getStatusClass(status) {
      const classMap = {
        'process': 'status-processing',
        'finish': 'status-completed',
        'refuse': 'status-rejected'
      };
      return classMap[status] || 'status-default';
    },
    
        // 新增：管理员主动发起退款
    async initiateRefund(order) {
      if (!confirm(`确定要为订单 ${order.id} 发起退款吗？\n\n订单信息：\n商品：${order.productName}\n买家：${order.buyerName}\n卖家：${order.sellerName}\n金额：¥${order.totalAmount}`)) {
        return;
      }
      
      try {
        // 调用钱包模块的退款API
        const response = await fetch('http://localhost:3000/api-8081/user/account/sellerRefund', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'accept': '*/*'
          },
          body: JSON.stringify({
            userId: order.originalData.buyerId, // 买家ID，退款给买家
            orderID: order.id // 订单ID
          })
        });
        
        const result = await response.json();
        
        if (result.success && result.code === 200) {
          // 退款成功
          alert('退款成功！' + result.message);
          
          // 更新订单状态为已退款
          const orderIndex = this.orders.findIndex(o => o.id === order.id);
          if (orderIndex !== -1) {
            this.orders[orderIndex].status = 'refunded';
            this.orders[orderIndex].statusDescription = '已退款';
          }
          
          // 重新筛选订单列表
          this.filterOrders();
          
          // 重新加载订单数据以确保同步
          await this.loadOrders();
          
        } else {
          // 退款失败
          const errorMessage = result.message || '退款失败，请重试';
          alert('退款失败：' + errorMessage);
          console.error('退款API返回错误:', result);
        }
        
      } catch (error) {
        console.error('调用退款API失败:', error);
        let errorMessage = '退款操作失败';
        
        if (error.response) {
          errorMessage = `退款失败: ${error.response.status} ${error.response.statusText}`;
          if (error.response.data && error.response.data.message) {
            errorMessage += ` - ${error.response.data.message}`;
          }
        } else if (error.request) {
          errorMessage = '无法连接到钱包服务器，请检查网络连接';
        } else {
          errorMessage = `退款失败: ${error.message}`;
        }
        
        alert(errorMessage);
      }
    },

    // 新增：管理员主动发起退款（仅在申诉详情弹窗中使用）
    async initiateRefundFromAppeal(order, appeal) {
      if (!confirm(`确定要为订单 ${order.id} 发起退款吗？\n\n订单信息：\n商品：${order.productName}\n买家：${order.buyerName}\n卖家：${order.sellerName}\n金额：¥${order.totalAmount}`)) {
        return;
      }
      
      try {
        // 调用钱包模块的退款API - 修复参数格式
        const response = await fetch('http://localhost:3000/api-8081/user/account/sellerRefund', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'accept': '*/*'
          },
          body: JSON.stringify({
            userId: order.originalData.buyerId, // 买家ID，退款给买家
            orderID: order.id // 使用orderID而不是orderId
          })
        });
        
        const result = await response.json();
        
        if (result.success && result.code === 200) {
          // 退款成功
          alert('退款成功！' + result.message);
          
          // 更新订单状态为已退款
          const orderIndex = this.orders.findIndex(o => o.id === order.id);
          if (orderIndex !== -1) {
            this.orders[orderIndex].status = 'refunded';
            this.orders[orderIndex].statusDescription = '已退款';
          }
          
          // 更新对应申诉的状态为已完成
          if (appeal) {
            await this.updateAppealStatus(appeal.id, 'finish');
          }
          
          // 关闭弹窗
          this.closeOrderDetailModal();
          
          // 重新加载申诉数据（不跳转页面）
          await this.loadAppeals();
          
          // 确保停留在申诉管理页面
          this.activeMenu = 'appeals';
        } else {
          // 退款失败
          const errorMessage = result.message || '退款失败，请重试';
          alert('退款失败：' + errorMessage);
          console.error('退款API返回错误:', result);
        }
      } catch (error) {
        console.error('调用退款API失败:', error);
        let errorMessage = '退款操作失败';
        
        if (error.response) {
          errorMessage = `退款失败: ${error.response.status} ${error.response.statusText}`;
          if (error.response.data && error.response.data.message) {
            errorMessage += ` - ${error.response.data.message}`;
          }
        } else if (error.request) {
          errorMessage = '无法连接到钱包服务器，请检查网络连接';
        } else {
          errorMessage = `退款失败: ${error.message}`;
        }
        
        alert(errorMessage);
      }
    },
    
    // 更新申诉状态
    async updateAppealStatus(appealId, status) {
      try {
        console.log('正在更新申诉状态:', appealId, status);
        
        // 首先检查服务是否可用
        await fetch('http://localhost:3000/api-8093/v1/appeals/all', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          }
        }).catch(err => {
          console.error('申诉服务连接失败:', err);
          throw new Error('无法连接到申诉服务，请确认服务是否启动');
        });
        
        const response = await axios.put(`http://localhost:3000/api-8093/v1/appeals/${appealId}/admin-update`, {
          rootId: this.adminId, // 使用动态获取的管理员ID
          status: status
        }, {
          headers: {
            'Content-Type': 'application/json'
          },
          timeout: 10000 // 10秒超时
        });
        
        console.log('申诉状态更新响应:', response.data);
        
        if (response.data.message && response.data.message.includes('成功')) {
          // 更新本地申诉数据
          const appealIndex = this.refundAppeals.findIndex(a => a.id === appealId);
          if (appealIndex !== -1) {
            this.refundAppeals[appealIndex].status = status;
            this.refundAppeals[appealIndex].adminId = this.adminId; // 使用动态获取的管理员ID
            console.log('本地申诉状态已更新:', this.refundAppeals[appealIndex]);
          }
          
          // 重新过滤申诉列表
          this.filterAppeals();
          
          // 显示成功提示
          alert('申诉状态更新成功');
        } else {
          console.error('申诉状态更新失败:', response.data);
          alert('申诉状态更新失败：' + (response.data.message || '未知错误'));
        }
      } catch (error) {
        console.error('更新申诉状态失败:', error);
        
        let errorMessage = '更新申诉状态失败：';
        if (error.message.includes('无法连接到申诉服务')) {
          errorMessage += '申诉服务未启动或端口不正确';
        } else if (error.code === 'ECONNREFUSED') {
          errorMessage += '无法连接到申诉服务(端口8093)，请检查服务是否启动';
        } else if (error.response) {
          errorMessage += `HTTP ${error.response.status} - ${error.response.statusText}`;
          if (error.response.data && error.response.data.error) {
            errorMessage += ` (${error.response.data.error})`;
          }
        } else {
          errorMessage += error.message;
        }
        
        alert(errorMessage);
      }
    },



    // 公告管理方法
    async loadAnnouncements() {
      try {
        // 查询所有管理员的公告
        let allAnnouncements = [];
        
        try {
          // 移除rootId参数，获取所有管理员的公告
          const response = await fetch(`http://localhost:3000/api-8092/announcements?n=9999`);
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
                publisher: announcement.rootId || '未知管理员' // 使用实际的rootId作为发布者
              }));
            allAnnouncements = visibleAnnouncements;
          }
        } catch (error) {
          console.error('获取所有管理员公告失败:', error);
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
        
        fetch('http://localhost:3000/api-8092/announcements', {
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
          rootId: this.adminId, // 发布者ID
          createdAt: new Date().toISOString(), // 当前更新时间
          content: this.announcementForm.content,
          visibleStatus: true // 保持可见
        };
        
        fetch('http://localhost:3000/api-8092/announcements', {
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
        const createResponse = await fetch('http://localhost:3000/api-8092/announcements', {
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
      
      const pendingRefunds = this.refundAppeals.filter(r => r.status === 'process').length;
      
      this.stats.pendingAppeals = pendingRefunds; // 只计算退款申请
    },
    
    // 订单管理方法
    async loadOrders() {
      console.log('开始加载订单数据...');
      this.loading = true;
      
      try {
        // 调用分页查询所有订单API
        const response = await axios.post('http://localhost:3000/api-8095/orders/query/all-paged', {
          pageNum: this.currentOrderPage,
          pageSize: 20
        }, {
          headers: {
            'Content-Type': 'application/json',
            'accept': '*/*'
          }
        });
        
        console.log('订单API响应:', response.data);
        
        if (response.data.success && response.data.data) {
          const { orders, pageNum, total, totalPages } = response.data.data;
          
          // 转换API数据格式为前端需要的格式
          this.orders = orders.map(order => ({
            id: order.orderId,
            productName: order.commodityName,
            productImage: order.mainImageUrl || 'https://via.placeholder.com/50',
            buyerName: order.buyerName,
            sellerName: order.sellerName,
            totalAmount: order.money,
            createTime: order.createTime,
            saleTime: order.saleTime,
            status: this.mapOrderStatus(order.orderStatus),
            statusDescription: order.orderStatusDescription,
            // 保留原始数据以备后用
            originalData: {
              orderId: order.orderId,
              commodityId: order.commodityId,
              buyerId: order.buyerId,
              sellerId: order.sellerId,
              orderStatus: order.orderStatus,
              saleLocation: order.saleLocation,
              buyQuantity: order.buyQuantity
            }
          }));
          
          // 更新分页信息
          this.currentOrderPage = pageNum;
          this.totalOrderPages = totalPages;
          this.totalOrders = total;
          
          // 初始化筛选结果
          this.filteredOrders = [...this.orders];
          
          console.log('订单数据加载成功:', {
            订单数量: this.orders.length,
            当前页: pageNum,
            总页数: totalPages,
            总数量: total
          });
          
        } else {
          console.error('订单API返回数据格式错误:', response.data);
          this.$message?.error('订单数据格式错误') || alert('订单数据格式错误');
          // 使用空数组作为备选
          this.orders = [];
          this.filteredOrders = [];
        }
        
      } catch (error) {
        console.error('加载订单数据失败:', error);
        
        let errorMessage = '加载订单数据失败';
        if (error.response) {
          errorMessage = `加载失败: ${error.response.status} ${error.response.statusText}`;
          if (error.response.data && error.response.data.message) {
            errorMessage += ` - ${error.response.data.message}`;
          }
        } else if (error.request) {
          errorMessage = '无法连接到订单服务器，请检查网络连接';
        } else {
          errorMessage = `加载失败: ${error.message}`;
        }
        
        this.$message?.error(errorMessage) || alert(errorMessage);
        
        // 错误时使用空数组
        this.orders = [];
        this.filteredOrders = [];
      } finally {
        this.loading = false;
      }
    },
    
    // 更新：订单状态映射方法
    mapOrderStatus(apiStatus) {
      const statusMap = {
        'pending_payment': 'pending_payment',
        'pending_transaction': 'pending_transaction', 
        'completed': 'completed',
        'refunded': 'refunded' // 新增退款状态
      };
      return statusMap[apiStatus] || 'pending_payment';
    },
    
    searchOrders() {
      console.log('执行搜索，查询条件:', this.orderSearchQuery);
      console.log('当前订单数据:', this.orders);
      
      if (!this.orderSearchQuery) {
        this.filteredOrders = [...this.orders];
        return;
      }
      
      const query = this.orderSearchQuery.toString().toLowerCase();
      this.filteredOrders = this.orders.filter(order => {
        // 确保所有字段都转换为字符串再进行比较
        const orderId = (order.id || '').toString().toLowerCase();
        const productName = (order.productName || '').toString().toLowerCase();
        const buyerName = (order.buyerName || '').toString().toLowerCase();
        const sellerName = (order.sellerName || '').toString().toLowerCase();
        
        return orderId.includes(query) ||
               productName.includes(query) ||
               buyerName.includes(query) ||
               sellerName.includes(query);
      });
      
      console.log('搜索结果:', this.filteredOrders);
    },
    
    filterOrders() {
      let filtered = [...this.orders];
      
      if (this.orderStatusFilter !== 'all') {
        filtered = filtered.filter(order => order.status === this.orderStatusFilter);
      }
      
      if (this.orderSearchQuery) {
        const query = this.orderSearchQuery.toString().toLowerCase();
        filtered = filtered.filter(order => {
          const orderId = (order.id || '').toString().toLowerCase();
          const productName = (order.productName || '').toString().toLowerCase();
          const buyerName = (order.buyerName || '').toString().toLowerCase();
          const sellerName = (order.sellerName || '').toString().toLowerCase();
          
          return orderId.includes(query) ||
                 productName.includes(query) ||
                 buyerName.includes(query) ||
                 sellerName.includes(query);
        });
      }
      
      this.filteredOrders = filtered;
      this.currentOrderPage = 1;
      this.totalOrderPages = Math.ceil(this.filteredOrders.length / 20); // 注意这里改为20，与loadOrders中的pageSize一致
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        'pending_transaction': '待交易',
        'completed': '已完成',
        'refunded': '已退款' // 新增退款状态文本
      };
      return statusMap[status] || '未知状态';
    },
    
    viewOrderDetail(orderId) {
      const order = this.orders.find(o => o.id === orderId);
      if (order) {
        const statusText = this.getOrderStatusText(order.status);
        const actions = this.getAvailableActions(order.status);
        
        alert(`订单详情:\n订单ID: ${order.id}\n商品: ${order.productName}\n买家: ${order.buyerName}\n卖家: ${order.sellerName}\n金额: ¥${order.totalAmount}\n状态: ${statusText}\n创建时间: ${order.createTime}\n可用操作: ${actions.join(', ')}`);
      }
    },
    
    getAvailableActions(status) {
      switch(status) {
        case 'pending_payment':
          return ['催促付款', '取消订单'];
        case 'pending_transaction':
          return ['联系买卖双方', '处理纠纷'];
        case 'completed':
          return ['查看评价', '导出记录'];
        default:
          return [];
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
      if (this.currentOrderPage !== 1) {
        this.currentOrderPage = 1;
        this.loadOrders();
      }
    },
    
    goToLastOrderPage() {
      if (this.currentOrderPage !== this.totalOrderPages) {
        this.currentOrderPage = this.totalOrderPages;
        this.loadOrders();
      }
    },
    
    // 获取可见的页码
    getVisiblePages(current, total) {
      const pages = [];
      const maxVisible = 5;
      
      if (total <= maxVisible) {
        for (let i = 1; i <= total; i++) {
          pages.push(i);
        }
      } else {
        const start = Math.max(1, current - 2);
        const end = Math.min(total, start + maxVisible - 1);
        
        for (let i = start; i <= end; i++) {
          pages.push(i);
        }
      }
      
      return pages;
    },
    
    goToFirstAppealPage() {
      this.currentAppealPage = 1;
      this.loadAppeals();
    },
    
    goToLastAppealPage() {
      this.currentAppealPage = this.totalAppealPages;
      this.loadAppeals();
    },
    
    // 显示带退款功能的订单详情弹窗
    showOrderDetailWithRefund(order, appeal) {
      this.selectedOrderForRefund = order;
      this.relatedAppeal = appeal;
      this.showOrderDetailModal = true;
    },
    
    // 关闭订单详情弹窗
    closeOrderDetailModal() {
      this.showOrderDetailModal = false;
      this.selectedOrderForRefund = null;
      this.relatedAppeal = null;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: white;
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
  margin-bottom: 30px;
  padding: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.search-box {
  position: relative;
  width: 350px;
}

.search-box input {
  width: 100%;
  padding: 15px 20px;
  padding-right: 50px;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  color: #2c3e50;
}

.search-box input:focus {
  outline: none;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.search-box input::placeholder {
  color: #7f8c8d;
  font-weight: 500;
}

.search-icon {
  position: absolute;
  right: 18px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #667eea;
  pointer-events: none;
  transition: all 0.3s ease;
}

.search-box:hover .search-icon {
  color: #764ba2;
  transform: translateY(-50%) scale(1.1);
}

.filter-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.filter-actions select {
  padding: 12px 18px;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.filter-actions select:focus {
  outline: none;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.filter-actions select:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-1px);
}

/* 统一背景色 - 用户管理和商品管理都使用相同的渐变 */
.users-content .content-header,
.products-content .content-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 订单管理保持不同的颜色以区分 */
.orders-content .content-header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-header {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .search-box {
    width: 100%;
    max-width: 400px;
  }
  
  .filter-actions {
    width: 100%;
    justify-content: center;
  }
  
  .filter-actions select {
    flex: 1;
    max-width: 200px;
  }
}

/* 搜索框动画效果 */
@keyframes searchPulse {
  0% {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }
  50% {
    box-shadow: 0 6px 25px rgba(102, 126, 234, 0.3);
  }
  100% {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }
}

.search-box input:focus {
  animation: searchPulse 2s infinite;
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

/* 订单管理样式 */
.orders-content {
  padding: 0;
}

.orders-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
}

.icon-order {
  font-size: 32px;
}

.stats-summary {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px 25px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-number {
  display: block;
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
  color: #2c3e50;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 14px;
  font-weight: 600;
  color: #5a6c7d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-item.pending .stat-number {
  color: #f39c12;
}

.stat-item.completed .stat-number {
  color: #27ae60;
}

.stat-item.processing .stat-number {
  color: #3498db;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.search-container {
  flex: 1;
  max-width: 500px;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 15px;
  font-size: 16px;
  color: #666;
  z-index: 1;
  pointer-events: none; /* 关键：让图标不阻挡点击事件 */
}

.search-input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  background: white;
  color: #333;
  font-size: 16px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2; /* 确保输入框在图标之上 */
}

.search-input::placeholder {
  color: #999;
}

.search-input:focus {
  outline: none;
  border-color: #4CAF50;
  background: white;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.filter-controls {
  display: flex;
  gap: 15px;
  align-items: center;
}

.status-filter {
  padding: 10px 15px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  font-size: 14px;
  backdrop-filter: blur(10px);
  cursor: pointer;
}

.status-filter option {
  background: #333;
  color: white;
}



.loading-container {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.order-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  border-bottom: 1px solid #e8ecf7;
}

.order-id {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.id-label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.id-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  font-family: 'Courier New', monospace;
}

.order-content {
  padding: 20px;
}

.product-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #e74c3c;
}

.participants-section {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.participant {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.participant-label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.participant-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.time-section {
  margin-bottom: 15px;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time-label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.time-value {
  font-size: 14px;
  color: #333;
}

.order-actions {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

.action-btn {
  flex: 1;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-icon {
  font-size: 12px;
}

.approve-btn {
  background: #28a745;
  color: white;
}

.approve-btn:hover {
  background: #218838;
  transform: translateY(-1px);
}

.reject-btn {
  background: #dc3545;
  color: white;
}

.reject-btn:hover {
  background: #c82333;
  transform: translateY(-1px);
}

.detail-btn {
  background: #6c757d;
  color: white;
}

.detail-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #333;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 30px;
  opacity: 0.8;
}

.retry-btn {
  padding: 12px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  background: #5a6fd8;
  transform: translateY(-2px);
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-top: 20px;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  color: #333;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.page-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 36px;
  height: 36px;
  border: 1px solid #ddd;
  background: white;
  color: #333;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-number:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.page-number.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 状态徽章样式 */
.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeaa7;
}

.status-paid {
  background: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.status-shipped {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-completed {
  background: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.status-refunding {
  background: #ffeaa7;
  color: #6c5ce7;
  border: 1px solid #fdcb6e;
}

.status-refunded {
  background: #ff9800;
  color: white;
  border: 1px solid #e84393;
}

/* 退款按钮样式 */
.refund-btn {
  background: linear-gradient(135deg, #ff9a56 0%, #ff6b35 100%);
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.refund-btn:hover {
  background: linear-gradient(135deg, #ff8a45 0%, #ff5722 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .orders-grid {
    grid-template-columns: 1fr;
  }
  
  .header-info {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .stats-summary {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 15px;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 15px;
  }
}

/* 申诉管理样式 */
.appeals-content {
  padding: 0;
}

.appeals-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 16px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  color: white;
}

.icon-shield {
  margin-right: 12px;
  font-size: 32px;
}

.appeals-header .stats-summary {
  display: flex;
  gap: 30px;
}

.appeals-header .stat-item {
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px 25px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.appeals-header .stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.appeals-header .stat-item.pending {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 193, 7, 0.6);
}

.appeals-header .stat-item.completed {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(40, 167, 69, 0.6);
}

.appeals-header .stat-number {
  display: block;
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
  color: #2c3e50;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.appeals-header .stat-label {
  font-size: 14px;
  font-weight: 600;
  color: #5a6c7d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.appeals-header .stat-item.pending .stat-number {
  color: #f39c12;
}

.appeals-header .stat-item.completed .stat-number {
  color: #27ae60;
}

.stats-summary {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
  background: rgba(255, 255, 255, 0.15);
  padding: 15px 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item.pending {
  background: rgba(255, 193, 7, 0.2);
  border-color: rgba(255, 193, 7, 0.3);
}

.stat-item.completed {
  background: rgba(40, 167, 69, 0.2);
  border-color: rgba(40, 167, 69, 0.3);
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.filter-section {
  display: flex;
  gap: 20px;
  align-items: center;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  opacity: 0.9;
}

.modern-select {
  padding: 10px 15px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  font-size: 14px;
  backdrop-filter: blur(10px);
  min-width: 150px;
}

.modern-select option {
  background: #2c3e50;
  color: white;
}

.appeals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.appeal-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.appeal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.appeal-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.appeal-id {
  font-weight: 700;
  color: #2c3e50;
  font-size: 16px;
}

.appeal-time {
  font-size: 12px;
  color: #6c757d;
}

.status-container {
  display: flex;
  align-items: center;
}

.status-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-processing {
  background: linear-gradient(135deg, #ffc107, #ff8f00);
  color: white;
}

.status-completed {
  background: linear-gradient(135deg, #28a745, #20c997);
  color: white;
}

.status-rejected {
  background: linear-gradient(135deg, #dc3545, #e83e8c);
  color: white;
}

.card-body {
  padding: 24px;
}

.appeal-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  gap: 16px;
}

.info-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item.full-width {
  flex: 1 1 100%;
}

.info-label {
  font-size: 12px;
  font-weight: 600;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.user-id, .order-id, .admin-id {
  font-family: 'Courier New', monospace;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 13px;
}

.reason-content {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
  border-left: 4px solid #007bff;
  font-size: 14px;
  line-height: 1.5;
  color: #495057;
  max-height: 80px;
  overflow-y: auto;
}

.card-footer {
  background: #f8f9fa;
  padding: 20px;
  border-top: 1px solid #e9ecef;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-icon {
  font-size: 14px;
}

.btn-success {
  background: linear-gradient(135deg, #28a745, #20c997);
  color: white;
}

.btn-success:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.btn-danger {
  background: linear-gradient(135deg, #dc3545, #e83e8c);
  color: white;
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
}

.btn-info {
  background: linear-gradient(135deg, #17a2b8, #6f42c1);
  color: white;
}

.btn-info:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(23, 162, 184, 0.3);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-description {
  font-size: 16px;
  color: #6c757d;
  margin: 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  background: white;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.page-btn {
  padding: 10px 16px;
  border: 1px solid #dee2e6;
  background: white;
  color: #495057;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #007bff;
  color: white;
  border-color: #007bff;
  transform: translateY(-1px);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  margin: 0 16px;
  font-weight: 600;
  color: #495057;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .appeals-grid {
    grid-template-columns: 1fr;
  }
  
  .header-info {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .stats-summary {
    justify-content: center;
  }
  
  .filter-section {
    justify-content: center;
  }
  
  .info-row {
    flex-direction: column;
    gap: 12px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .btn {
    justify-content: center;
  }
}

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

/* 模态框基础样式 */
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
  z-index: 9999;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  position: relative;
}

.user-detail-modal {
  width: 500px;
  max-width: 90vw;
  max-height: 80vh;
  overflow-y: auto;
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
  color: #333;
  font-size: 18px;
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
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background-color: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 0 20px;
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

/* 用户详情弹窗样式 */
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

/* 订单详情弹窗样式 */
.order-detail-modal {
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.order-detail-info {
  padding: 20px 0;
}

.info-row {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}

.info-row label {
  font-weight: 600;
  color: #333;
  min-width: 100px;
  margin-right: 15px;
}

.info-row span {
  color: #666;
  flex: 1;
}

.status-text {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background-color: #e3f2fd;
  color: #1976d2;
}

.appeal-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.appeal-status.status-process {
  background-color: #fff3cd;
  color: #856404;
}

.appeal-status.status-finish {
  background-color: #d4edda;
  color: #155724;
}

.appeal-status.status-refuse {
  background-color: #f8d7da;
  color: #721c24;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.modal-footer .refund-btn {
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.modal-footer .refund-btn:hover {
  background: linear-gradient(135deg, #ee5a24, #ff6b6b);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(238, 90, 36, 0.3);
}

.modal-footer .cancel-btn {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-footer .cancel-btn:hover {
  background-color: #5a6268;
}

/* 商品管理表格优化样式 */
.products-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 列宽优化 */
.products-table .id-column {
  width: 80px;
  text-align: center;
}

.products-table .product-column {
  width: 28%;
  min-width: 200px;
}

.products-table .price-column {
  width: 100px;
  text-align: right;
}

.products-table .seller-column {
  width: 120px;
}

.products-table .time-column {
  width: 140px;
}

.products-table .status-column {
  width: 100px;
  text-align: center;
}

.products-table .action-column {
  width: 140px;
  text-align: center;
}

/* 表头样式优化 */
.products-table th {
  background: #f8f9fa;
  padding: 18px 15px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
  font-size: 14px;
  letter-spacing: 0.5px;
}

.products-table td {
  padding: 16px 15px;
  border-bottom: 1px solid #f1f3f4;
  color: #2c3e50;
  vertical-align: middle;
}

/* 商品信息样式优化 */
.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-image {
  width: 45px;
  height: 45px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-details {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.product-category {
  font-size: 12px;
  color: #6c757d;
  background: #f8f9fa;
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
}

/* ID列样式 */
.id-column {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  color: #6c757d;
  font-size: 13px;
}

/* 价格样式 */
.price-value {
  font-weight: 700;
  color: #e74c3c;
  font-size: 16px;
}

/* 时间样式 */
.time-value {
  font-size: 13px;
  color: #6c757d;
}

/* 状态徽章优化 */
.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid transparent;
}

.status-pending {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  color: #856404;
  border-color: #ffeaa7;
}

.status-approved {
  background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
  color: #0c5460;
  border-color: #bee5eb;
}

.status-rejected {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
  color: #721c24;
  border-color: #f5c6cb;
}

.status-sold {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
  border-color: #c3e6cb;
}

/* 操作按钮区域优化 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  min-width: 60px;
  justify-content: center;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-btn:active {
  transform: translateY(0);
}

.approve-btn {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.approve-btn:hover {
  background: linear-gradient(135deg, #218838 0%, #1ea080 100%);
}

.reject-btn {
  background: linear-gradient(135deg, #dc3545 0%, #e74c3c 100%);
  color: white;
}

.reject-btn:hover {
  background: linear-gradient(135deg, #c82333 0%, #dc2626 100%);
}

.btn-icon {
  font-size: 14px;
  font-weight: bold;
}

.btn-text {
  font-size: 11px;
}

/* 行悬停效果优化 */
.products-table .clickable-row {
  cursor: pointer;
  transition: all 0.3s ease;
}

.products-table .clickable-row:hover {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-table .product-column {
    width: 30%;
    min-width: 200px;
  }
  
  .btn-text {
    display: none;
  }
  
  .action-btn {
    min-width: 36px;
    padding: 8px;
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
  
  .products-table {
    font-size: 12px;
  }
  
  .products-table th,
  .products-table td {
    padding: 12px 8px;
  }
  
  .product-image {
    width: 40px;
    height: 40px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}
</style>