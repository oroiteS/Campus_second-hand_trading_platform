<template>
  <div class="order-management">
    <!-- 顶部导航 -->
    <header class="order-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
          ← 返回
        </button>
        <h1 class="page-title">订单管理</h1>
        <div class="header-placeholder"></div>
      </div>
    </header>

    <div class="order-container">
      <!-- 订单类型切换 -->
      <div class="order-tabs">
        <button v-for="tab in orderTabs" :key="tab.id" class="tab-btn" :class="{ active: activeTab === tab.id }"
          @click="switchTab(tab.id)">
          {{ tab.name }}
          <span class="tab-count">({{ getOrderCount(tab.id) }})</span>
        </button>
      </div>

      <!-- 订单状态筛选器 -->
      <div class="status-filter">
        <div class="filter-label">订单状态：</div>
        <div class="filter-options">
          <button class="filter-btn" :class="{ active: selectedStatus === 'all' }" @click="filterByStatus('all')">
            全部
          </button>
          <button v-for="status in statusOptions" :key="status.value" class="filter-btn"
            :class="{ active: selectedStatus === status.value }" @click="filterByStatus(status.value)">
            {{ status.label }}
          </button>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="order-list" v-if="filteredOrders.length > 0">
        <div class="order-card" v-for="order in filteredOrders" :key="order.id">
          <div class="order-header-info">
            <div class="order-number">订单号：{{ order.orderNumber }}</div>
            <div class="order-status" :class="order.statusClass">{{ order.status }}</div>
          </div>

          <div class="order-content">
            <img :src="order.productImage" :alt="order.productName" class="product-image" />
            <div class="product-info">
              <h4 class="product-name">{{ order.productName }}</h4>
              <p class="product-desc">{{ order.productDesc }}</p>
              <div class="order-details">
                <span class="order-price">¥{{ order.price }}</span>
                <span class="order-quantity">数量：{{ order.quantity }}</span>
              </div>
            </div>
          </div>

          <div class="order-meta">
            <div class="order-time">{{ order.createTime }}</div>
            <div class="order-actions">
              <button v-for="action in order.actions" :key="action.type" class="action-btn" :class="action.type"
                @click="handleOrderAction(order, action.type)">
                {{ action.text }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else>
        <div class="empty-icon">📋</div>
        <p class="empty-text">暂无{{ getTabName(activeTab) }}订单</p>
      </div>
    </div>

    <!-- 申诉弹窗 -->
    <div v-if="showAppealModal" class="modal-overlay" @click="closeAppealModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>订单申诉</h3>
          <button @click="closeAppealModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="appeal-info">
            <p><strong>订单号：</strong>{{ currentOrder?.orderNumber }}</p>
            <p><strong>商品名称：</strong>{{ currentOrder?.productName }}</p>
            <p><strong>申诉角色：</strong>{{ getOrderType(currentOrder?.id) === 'buy' ? '买家申诉' : '卖家申诉' }}</p>
          </div>
          <div class="form-group">
            <label>申诉类型：</label>
            <select v-model="appealForm.type" class="form-control">
              <option value="">请选择申诉类型</option>
              <!-- 买家申诉选项 -->
              <template v-if="getOrderType(currentOrder?.id) === 'buy'">
                <option value="quality">商品质量问题</option>
                <option value="description">商品描述不符</option>
                <option value="delivery">物流问题</option>
                <option value="seller_service">卖家服务问题</option>
                <option value="fake">商品疑似假货</option>
                <option value="not_received">未收到商品</option>
              </template>
              <!-- 卖家申诉选项 -->
              <template v-else>
                <option value="buyer_unreasonable">买家无理要求</option>
                <option value="false_complaint">买家恶意投诉</option>
                <option value="payment_issue">付款问题</option>
                <option value="return_issue">退货问题</option>
                <option value="communication">沟通问题</option>
                <option value="platform_error">平台系统错误</option>
              </template>
              <option value="other">其他问题</option>
            </select>
          </div>
          <div class="form-group">
            <label>申诉描述：</label>
            <textarea v-model="appealForm.description" class="form-control" rows="4"
              :placeholder="getOrderType(currentOrder?.id) === 'buy' ? '请详细描述您遇到的问题...' : '请详细描述买家的问题行为或平台错误...'"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <!-- 调试信息 -->
          <div style="margin-bottom: 10px; font-size: 12px; color: #666;">
            调试信息：type = "{{ appealForm.type }}", description = "{{ appealForm.description }}"
            <br>
            按钮状态：{{ !appealForm.type || !appealForm.description ? '禁用' : '启用' }}
          </div>

          <button @click="closeAppealModal" class="btn btn-cancel">取消</button>
          <button @click="submitAppeal" class="btn btn-primary"
            :disabled="!appealForm.type || !appealForm.description">提交申诉</button>
        </div>
      </div>
    </div>

    <!-- 退款弹窗 -->
    <div v-if="showRefundModal" class="modal-overlay" @click="closeRefundModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>申请退款</h3>
          <button @click="closeRefundModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="refund-info">
            <p><strong>订单号：</strong>{{ currentOrder?.orderNumber }}</p>
            <p><strong>商品名称：</strong>{{ currentOrder?.productName }}</p>
            <p><strong>订单金额：</strong>¥{{ currentOrder?.price }}</p>
          </div>
          <div class="form-group">
            <label>退款原因：</label>
            <select v-model="refundForm.reason" class="form-control">
              <option value="">请选择退款原因</option>
              <option value="quality">商品质量问题</option>
              <option value="description">商品与描述不符</option>
              <option value="damage">商品损坏</option>
              <option value="wrong">发错商品</option>
              <option value="change_mind">不想要了</option>
              <option value="other">其他原因</option>
            </select>
          </div>
          <div class="form-group">
            <label>退款说明：</label>
            <textarea v-model="refundForm.description" class="form-control" rows="3"
              placeholder="请说明退款原因..."></textarea>
          </div>
          <div class="form-group">
            <label>退款金额：</label>
            <input v-model.number="refundForm.amount" type="number" class="form-control" :max="currentOrder?.price"
              step="0.01">
            <div class="form-tips">最大退款金额：¥{{ currentOrder?.price }}</div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeRefundModal" class="btn btn-cancel">取消</button>
          <button @click="submitRefund" class="btn btn-primary"
            :disabled="!refundForm.reason || !refundForm.amount">申请退款</button>
        </div>
      </div>
    </div>

    <!-- 发货状态调整弹窗 -->
    <div v-if="showShippingModal" class="modal-overlay" @click="closeShippingModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>调整发货状态</h3>
          <button @click="closeShippingModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="shipping-info">
            <p><strong>订单号：</strong>{{ currentOrder?.orderNumber }}</p>
            <p><strong>商品名称：</strong>{{ currentOrder?.productName }}</p>
            <p><strong>当前状态：</strong>{{ currentOrder?.status }}</p>
          </div>
          <div class="form-group">
            <label>选择新状态：</label>
            <div class="status-options">
              <label class="status-option">
                <input type="radio" v-model="newShippingStatus" value="未发货">
                <div class="status-content">
                  <span class="status-label">未发货</span>
                  <small class="status-desc">商品尚未发出</small>
                </div>
              </label>
              <label class="status-option">
                <input type="radio" v-model="newShippingStatus" value="已发货">
                <div class="status-content">
                  <span class="status-label">已发货</span>
                  <small class="status-desc">商品已发出，等待买家确认收货</small>
                </div>
              </label>
              <label class="status-option">
                <input type="radio" v-model="newShippingStatus" value="已完成">
                <div class="status-content">
                  <span class="status-label">已完成</span>
                  <small class="status-desc">交易已完成</small>
                </div>
              </label>
            </div>
          </div>
          <div class="form-group" v-if="newShippingStatus === '已发货'">
            <label>物流信息（可选）：</label>
            <input v-model="shippingInfo.trackingNumber" type="text" class="form-control" placeholder="请输入快递单号">
            <select v-model="shippingInfo.courier" class="form-control" style="margin-top: 8px;">
              <option value="">选择快递公司</option>
              <option value="顺丰">顺丰速运</option>
              <option value="圆通">圆通速递</option>
              <option value="中通">中通快递</option>
              <option value="申通">申通快递</option>
              <option value="韵达">韵达速递</option>
              <option value="邮政">中国邮政</option>
              <option value="其他">其他</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeShippingModal" class="btn btn-cancel">取消</button>
          <button @click="updateShippingStatus" class="btn btn-primary" :disabled="!newShippingStatus">确认更新</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'OrderManagement',
  data() {
    return {
      activeTab: 'buy',
      selectedStatus: 'all',
      statusOptions: [
        { value: 'pending_payment', label: '待付款' },
        { value: 'pending_transaction', label: '待交易' },
        { value: 'completed', label: '已完成' }
      ],
      orderTabs: [
        { id: 'buy', name: '我买到的' },
        { id: 'sell', name: '我卖出的' },
        { id: 'all', name: '全部订单' }
      ],
      showAppealModal: false,
      showRefundModal: false,
      showShippingModal: false,
      currentOrder: null,
      newShippingStatus: '',
      shippingInfo: {
        trackingNumber: '',
        courier: ''
      },
      // 添加appealForm的定义
      appealForm: {
        type: '',
        description: ''
      },
      orders: {
        buy: [], // 改为空数组，将通过API获取
        sell: [
          {
            id: 3,
            orderNumber: 'ORD202312180001',
            productName: 'iPhone 13 Pro',
            productDesc: '128G，9成新',
            productImage: '/测试图片.jpg',
            price: 4999,
            quantity: 1,
            status: '已发货',
            statusClass: 'shipped',
            createTime: '2023-12-18 16:45',
            actions: [
              { type: 'shipping', text: '调整发货状态' },
              { type: 'contact', text: '联系买家' },
              { type: 'appeal', text: '申诉' }
            ]
          },
          {
            id: 4,
            orderNumber: 'ORD202312170001',
            productName: '小米台灯',
            productDesc: '护眼版，全新',
            productImage: 'https://via.placeholder.com/80x80/FF9800/FFFFFF?text=台灯',
            price: 89,
            quantity: 1,
            status: '未发货',
            statusClass: 'not-shipped',
            createTime: '2023-12-17 09:20',
            actions: [
              { type: 'shipping', text: '调整发货状态' },
              { type: 'contact', text: '联系买家' },
              { type: 'appeal', text: '申诉' }
            ]
          }
        ]
      },
      loading: false // 添加加载状态
    }
  },
  computed: {
    currentOrders() {
      if (this.activeTab === 'all') {
        return [...this.orders.buy, ...this.orders.sell].sort((a, b) =>
          new Date(b.createTime) - new Date(a.createTime)
        )
      }
      return this.orders[this.activeTab] || []
    },
    filteredOrders() {
      let orders = this.currentOrders

      // 根据选中的状态筛选订单
      if (this.selectedStatus !== 'all') {
        orders = orders.filter(order => {
          // 根据订单状态描述或状态类进行筛选
          return this.getOrderStatusValue(order) === this.selectedStatus
        })
      }

      return orders
    }
  },
  mounted() {
    // 组件挂载时同时获取买家和卖家订单数据
    this.fetchBuyerOrders()
    this.fetchSellerOrders()
  },
  methods: {
    goBack() {
      this.$router.go(-1) // 返回上一页
    },

    filterByStatus(status) {
      this.selectedStatus = status
    },

    getOrderStatusValue(order) {
      // 根据订单状态描述映射到筛选值
      const statusMap = {
        '待交易': 'pending_transaction',
        '待付款': 'pending_payment',
        '已付款': 'pending_transaction',
        '已发货': 'pending_transaction',
        '已送达': 'pending_transaction',
        '已完成': 'completed',
        '已取消': 'completed', // 将已取消归类到已完成
        '已退款': 'completed'  // 将已退款归类到已完成
      }

      return statusMap[order.status] || 'pending_payment'
    },

    switchTab(tabId) {
      this.activeTab = tabId
      this.selectedStatus = 'all' // 切换标签时重置状态筛选
      this.refreshOrders()
    },

    getOrderCount(tabId) {
      if (tabId === 'all') {
        return this.orders.buy.length + this.orders.sell.length
      }
      return this.orders[tabId]?.length || 0
    },
    getTabName(tabId) {
      const tab = this.orderTabs.find(t => t.id === tabId)
      return tab ? tab.name : ''
    },
    handleOrderAction(order, actionType) {
      console.log('订单操作:', order.orderNumber, actionType)
      this.currentOrder = order

      switch (actionType) {
        case 'pay':
          this.handlePayment(order)
          break
        case 'cancel':
          this.cancelOrder(order)
          break
        case 'confirm':
          this.confirmReceive(order)
          break
        case 'review':
          this.showReview(order)
          break
        case 'contact':
          this.contactUser(order)
          break
        case 'appeal':
          this.openAppealModal(order)
          break
        case 'refund':
          this.openRefundModal(order)
          break
        case 'shipping':
          this.openShippingModal(order)
          break
        default:
          console.log('未知操作:', actionType)
      }
    },

    // 新增：处理付款
    handlePayment(order) {
      console.log('处理付款:', order.orderNumber)
      // 这里可以跳转到支付页面或调用支付API
      this.$message?.info(`跳转到支付页面：${order.orderNumber}`) || alert(`跳转到支付页面：${order.orderNumber}`)
    },

    // 新增：取消订单
    async cancelOrder(order) {
      if (confirm('确认取消此订单吗？')) {
        try {
          // 调用取消订单API
          // const response = await axios.post('http://localhost:8095/v3/api/orders/cancel', {
          //   orderId: order.id
          // })

          // 暂时更新本地状态
          order.status = '已取消'
          order.statusClass = 'cancelled'
          order.actions = [
            { type: 'contact', text: '联系卖家' },
            { type: 'appeal', text: '申诉' }
          ]

          this.$message?.success('订单已取消') || alert('订单已取消')
        } catch (error) {
          console.error('取消订单失败:', error)
          this.$message?.error('取消订单失败') || alert('取消订单失败')
        }
      }
    },

    confirmReceive(order) {
      if (confirm('确认收到商品吗？')) {
        order.status = '已完成'
        order.statusClass = 'completed'
        order.actions = [
          { type: 'review', text: '评价' },
          { type: 'contact', text: '联系卖家' },
          { type: 'appeal', text: '申诉' }
        ]
        this.$message?.success('确认收货成功！') || alert('确认收货成功！')
      }
    },
    showReview(order) {
      console.log('查看订单评价:', order.orderNumber)
      this.$message?.info(`查看订单 ${order.orderNumber} 的评价`) || alert(`查看订单 ${order.orderNumber} 的评价`)
    },
    contactUser(order) {
      // 获取当前用户ID
      const userId = localStorage.getItem('userId');

      if (!userId) {
        // 如果用户未登录，提示登录
        this.$message?.error('请先登录后联系用户') || alert('请先登录后联系用户');
        this.$router.push('/login');
        return;
      }

      // 判断当前用户是买家还是卖家，确定要联系的对象
      let targetUserId;
      let targetUserType;

      if (userId === order.buyerId.toString()) {
        // 当前用户是买家，要联系卖家
        targetUserId = order.sellerId;
        targetUserType = '卖家';
      } else if (userId === order.sellerId.toString()) {
        // 当前用户是卖家，要联系买家
        targetUserId = order.buyerId;
        targetUserType = '买家';
      } else {
        // 异常情况：当前用户既不是买家也不是卖家
        this.$message?.error('无法确定联系对象') || alert('无法确定联系对象');
        return;
      }

      if (!targetUserId) {
        this.$message?.error(`无法获取${targetUserType}信息`) || alert(`无法获取${targetUserType}信息`);
        return;
      }

      // 跳转到聊天页面
      this.$router.push({
        path: '/chat-list',
        query: {
          sellerId: order.sellerId,
          buyerId: order.buyerId,
          autoCreate: 'true'
        }
      });
    },

    // 申诉相关方法
    openAppealModal(order) {
      this.currentOrder = order
      this.appealForm = {
        type: '',
        description: '',
      }
      this.showAppealModal = true
    },
    closeAppealModal() {
      this.showAppealModal = false
      this.currentOrder = null
    },
    async submitAppeal() {
      try {
        // 验证表单数据
        if (!this.appealForm.type || !this.appealForm.description) {
          this.$message.error('请选择申诉类型并填写申诉描述');
          return;
        }

        if (!this.currentOrder || !this.currentOrder.id) {
          this.$message.error('订单信息不完整');
          return;
        }

        // 获取当前用户ID（需要从用户状态或localStorage中获取）
        const currentUserId = this.getCurrentUserId(); // 需要实现这个方法
        if (!currentUserId) {
          this.$message.error('用户信息不完整');
          return;
        }

        // 构建申诉数据
        const appealData = {
          argue1Id: currentUserId, // 申诉发起者（当前用户）
          argue2Id: this.getOppositeUserId(), // 被申诉者
          orderId: this.currentOrder.id.toString(), // 确保是字符串
          reason: `${this.appealForm.type}: ${this.appealForm.description}`
        };

        console.log('提交申诉数据:', appealData);

        // 验证必要字段
        if (!appealData.argue1Id || !appealData.orderId) {
          this.$message.error('申诉数据不完整');
          return;
        }

        // 调用后端API
        const response = await axios.post('http://localhost:8093/api/v1/appeals', appealData, {
          headers: {
            'Content-Type': 'application/json'
          }
        });

        console.log('申诉提交响应:', response.data);

        // 检查响应
        if (response.status === 201) {
          this.$message.success('申诉提交成功');
          this.closeAppealModal();
          this.refreshOrders();
        } else {
          throw new Error('申诉提交失败：响应状态码不正确');
        }
      } catch (error) {
        console.error('提交申诉失败:', error);

        let errorMessage = '申诉提交失败';

        if (error.response) {
          // 服务器返回了错误响应
          errorMessage = `申诉提交失败：${error.response.status} ${error.response.statusText}`; // 修正语法错误
          if (error.response.data && error.response.data.error) {
            errorMessage += ` - ${error.response.data.error}`;
          }
        } else if (error.request) {
          // 请求发送了但没有收到响应
          errorMessage = '无法连接到申诉服务器，请检查网络连接';
        } else {
          // 其他错误
          errorMessage = `申诉提交失败：${error.message}`;
        }

        this.$message.error(errorMessage);
      }
    },

    // 新增：获取当前用户ID的方法
    getCurrentUserId() {
      // 从localStorage、vuex store或其他地方获取当前用户ID
      // 这里需要根据你的用户认证系统来实现
      return localStorage.getItem('userId') || this.$store?.state?.user?.id || null;
    },

    // 修正：获取被申诉者ID的方法
    getOppositeUserId() {
      const orderType = this.getOrderType(this.currentOrder.id);

      // 根据订单类型和实际数据结构来获取对方用户ID
      if (orderType === 'buy') {
        // 买家申诉卖家
        return this.currentOrder.sellerId || this.currentOrder.seller_id || null;
      } else if (orderType === 'sell') {
        // 卖家申诉买家
        return this.currentOrder.buyerId || this.currentOrder.buyer_id || null;
      }

      return null;
    },

    // 辅助方法：判断订单类型
    getOrderType(orderId) {
      const buyOrder = this.orders.buy.find(order => order.id === orderId)
      const sellOrder = this.orders.sell.find(order => order.id === orderId)

      if (buyOrder) return 'buy'
      if (sellOrder) return 'sell'
      return 'unknown'
    },

    // 退款相关方法
    openRefundModal(order) {
      this.currentOrder = order
      this.refundForm = {
        reason: '',
        description: '',
        amount: order.price
      }
      this.showRefundModal = true
    },
    closeRefundModal() {
      this.showRefundModal = false
      this.currentOrder = null
    },
    submitRefund() {
      if (!this.refundForm.reason || !this.refundForm.amount) {
        this.$message?.warning('请填写完整的退款信息') || alert('请填写完整的退款信息')
        return
      }

      if (this.refundForm.amount > this.currentOrder.price) {
        this.$message?.warning('退款金额不能超过订单金额') || alert('退款金额不能超过订单金额')
        return
      }

      console.log('提交退款申请:', {
        orderId: this.currentOrder.id,
        orderNumber: this.currentOrder.orderNumber,
        ...this.refundForm
      })

      this.$message?.success('退款申请提交成功，我们会在1-3个工作日内处理') || alert('退款申请提交成功，我们会在1-3个工作日内处理')
      this.closeRefundModal()
    },

    // 发货状态相关方法
    openShippingModal(order) {
      this.currentOrder = order
      this.newShippingStatus = order.status
      this.shippingInfo = {
        trackingNumber: '',
        courier: ''
      }
      this.showShippingModal = true
    },
    closeShippingModal() {
      this.showShippingModal = false
      this.currentOrder = null
      this.newShippingStatus = ''
    },
    updateShippingStatus() {
      if (!this.newShippingStatus) {
        this.$message?.warning('请选择新的发货状态') || alert('请选择新的发货状态')
        return
      }

      this.currentOrder.status = this.newShippingStatus

      switch (this.newShippingStatus) {
        case '未发货':
          this.currentOrder.statusClass = 'not-shipped'
          break
        case '已发货':
          this.currentOrder.statusClass = 'shipped'
          break
        case '已完成':
          this.currentOrder.statusClass = 'completed'
          break
      }

      if (this.newShippingStatus === '已发货' && this.shippingInfo.trackingNumber) {
        console.log('物流信息:', this.shippingInfo)
      }

      console.log('更新发货状态:', {
        orderId: this.currentOrder.id,
        orderNumber: this.currentOrder.orderNumber,
        newStatus: this.newShippingStatus,
        shippingInfo: this.shippingInfo
      })

      this.$message?.success(`发货状态已更新为：${this.newShippingStatus}`) || alert(`发货状态已更新为：${this.newShippingStatus}`)
      this.closeShippingModal()
    },

    // API相关方法
    // 获取买家订单数据 - 使用封装的请求方法
    async fetchBuyerOrders() {
      console.log('开始获取买家订单数据...')
      this.loading = true

      try {
        // 获取当前用户ID，从localStorage获取登录用户信息
        const buyerId = localStorage.getItem('userId') // 从localStorage获取当前登录用户的ID

        if (!buyerId) {
          console.error('用户未登录，无法获取订单信息')
          this.loading = false
          return
        }

        console.log('买家ID:', buyerId)

        // 构建请求参数，模拟Login.vue的请求结构
        const requestBody = {
          buyerId: buyerId
        }

        // 修改API路径，去掉/v3前缀，与后端保持一致
        axios.post('http://localhost:8095/api/orders/query/by-buyer', requestBody, {
          headers: {
            userId: buyerId  // 使用同一个buyerId
          },
          timeout: 10000
        })
          .then(response => {
            console.log('API响应状态:', response.status)
            console.log('API响应数据:', response.data)

            const result = response.data

            if (result.code === 200) {
              // 请求成功
              const orderData = result.data || []
              console.log('订单原始数据:', orderData)

              // 转换API数据为组件需要的格式
              this.orders.buy = this.transformOrderData(orderData)
              console.log('转换后的订单数据:', this.orders.buy)

              this.$message?.success(`成功获取${this.orders.buy.length}个订单`) || console.log(`成功获取${this.orders.buy.length}个订单`)
            } else {
              // 请求失败
              console.warn('API返回异常:', result)
              this.$message?.warning(result.message || '获取订单数据异常') || console.warn('获取订单数据异常')
            }
          })
          .catch(error => {
            console.error('获取买家订单失败:', error)

            if (error.response) {
              // 服务器响应了错误状态码
              console.error('HTTP错误状态:', error.response.status)
              console.error('HTTP错误数据:', error.response.data)
              this.$message?.error(`服务器错误: ${error.response.status}`) || alert(`服务器错误: ${error.response.status}`)
            } else if (error.request) {
              // 请求已发出但没有收到响应
              console.error('请求错误:', error.request)
              this.$message?.error('网络请求失败，请检查网络连接') || alert('网络请求失败，请检查网络连接')
            } else {
              // 其他错误
              console.error('其他错误:', error.message)
              this.$message?.error('获取订单数据失败') || alert('获取订单数据失败')
            }

            console.error('错误堆栈:', error.stack)
          })
          .finally(() => {
            this.loading = false
            console.log('获取买家订单数据完成')
          })

      } catch (error) {
        console.error('fetchBuyerOrders方法异常:', error)
        this.loading = false
      }
    },
    // 获取卖家订单数据
    async fetchSellerOrders() {
      console.log('开始获取卖家订单数据...')
      this.loading = true

      try {
        // 获取当前用户ID，从localStorage获取登录用户信息
        const sellerId = localStorage.getItem('userId') // 从localStorage获取当前登录用户的ID

        if (!sellerId) {
          console.error('用户未登录，无法获取订单信息')
          this.loading = false
          return
        }

        console.log('卖家ID:', sellerId)

        // 构建请求参数
        const requestBody = {
          sellerId: sellerId
        }

        axios.post('http://localhost:8095/api/orders/query/by-seller', requestBody, {
          headers: {
            userId: sellerId
          },
          timeout: 10000
        })
          .then(response => {
            console.log('API响应状态:', response.status)
            console.log('API响应数据:', response.data)

            const result = response.data

            if (result.code === 200) {
              // 请求成功
              const orderData = result.data || []
              console.log('订单原始数据:', orderData)

              // 开始转换订单数据
              console.log('开始转换订单数据:', orderData)
              const transformedOrders = this.transformOrderData(orderData)
              console.log('转换后订单:', transformedOrders)

              // 更新卖家订单数据
              this.orders.sell = transformedOrders
              console.log('获取卖家订单成功，订单数量:', transformedOrders.length)
            } else {
              console.error('获取卖家订单失败:', result.message)
              this.orders.sell = []
            }
          })
          .catch(error => {
            console.error('获取卖家订单请求失败:', error)
            console.error('错误类型:', error.name)
            console.error('错误信息:', error.message)
            console.error('错误堆栈:', error.stack)
            this.orders.sell = []
          })
          .finally(() => {
            this.loading = false
          })

      } catch (error) {
        console.error('获取卖家订单异常:', error)
        this.loading = false
      }
    },

    // 转换API数据为组件需要的格式
    transformOrderData(apiData) {
      console.log('开始转换订单数据:', apiData)

      if (!Array.isArray(apiData)) {
        console.warn('API数据不是数组格式:', apiData)
        return []
      }

      return apiData.map(order => {
        console.log('转换单个订单:', order)

        // 根据订单状态和当前标签页生成操作按钮
        const actions = this.generateOrderActions(order.orderStatus || 'pending_payment', this.activeTab)

        return {
          id: order.orderId,
          orderNumber: order.orderId, // 使用orderId作为订单号
          productName: order.commodityName, // 使用API的commodityName字段
          productImage: order.mainImageUrl || '/测试图片.jpg', // 使用API的mainImageUrl字段
          price: order.money, // 使用API的money字段
          quantity: order.buyQuantity, // 使用API的buyQuantity字段
          status: order.orderStatusDescription, // 使用API的orderStatusDescription字段
          statusClass: this.getStatusClass(order.orderStatus), // 使用API的orderStatus字段
          createTime: order.createTime, // 使用API的createTime字段
          saleTime: order.saleTime, // 添加销售时间
          saleLocation: order.saleLocation, // 添加销售地点
          buyerId: order.buyerId, // 添加买家ID
          sellerId: order.sellerId, // 添加卖家ID
          commodityId: order.commodityId, // 添加商品ID
          buyerName: order.buyerName, // 添加买家名称
          sellerName: order.sellerName, // 添加卖家名称
          actions: actions
        }
      })
    },

    generateOrderActions(orderStatus, viewType = 'buy') {
      const actions = []

      if (viewType === 'sell') {
        // 卖家视角的操作按钮
        switch (orderStatus) {
          case 'pending_payment':
            // 待付款状态：只显示联系买家
            actions.push(
              { type: 'contact', text: '联系买家' }
            )
            break
          case 'paid':
          case 'shipped':
          case 'delivered':
            // 待交易状态：联系买家、调整发货状态、申诉
            actions.push(
              { type: 'shipping', text: '调整发货状态' },
              { type: 'contact', text: '联系买家' },
              { type: 'appeal', text: '申诉' }
            )
            break
          case 'completed':
            // 已完成状态：只保留申诉
            actions.push(
              { type: 'appeal', text: '申诉' }
            )
            break
          default:
            actions.push(
              { type: 'contact', text: '联系买家' },
              { type: 'appeal', text: '申诉' }
            )
        }
      } else {
        // 买家视角的操作按钮（原有逻辑）
        switch (orderStatus) {
          case 'pending_payment':
            actions.push(
              { type: 'pay', text: '立即付款' },
              { type: 'cancel', text: '取消订单' },
              { type: 'contact', text: '联系卖家' }
            )
            break
          case 'paid':
          case 'shipped':
            actions.push(
              { type: 'confirm', text: '确认收货' },
              { type: 'contact', text: '联系卖家' },
              { type: 'refund', text: '申请退款' }
            )
            break
          case 'delivered':
            actions.push(
              { type: 'confirm', text: '确认收货' },
              { type: 'contact', text: '联系卖家' },
              { type: 'appeal', text: '申诉' }
            )
            break
          case 'completed':
            actions.push(
              { type: 'review', text: '评价' },
              { type: 'contact', text: '联系卖家' },
              { type: 'appeal', text: '申诉' }
            )
            break
          default:
            actions.push(
              { type: 'contact', text: '联系卖家' },
              { type: 'appeal', text: '申诉' }
            )
        }
      }

      return actions
    },

    async refreshOrders() {
      if (this.activeTab === 'buy') {
        await this.fetchBuyerOrders()
      } else if (this.activeTab === 'sell') {
        await this.fetchSellerOrders()
      }
    },

    // 添加缺失的getStatusClass方法
    getStatusClass(status) {
      const statusMap = {
        'pending_payment': 'status-pending',
        'paid': 'status-paid',
        'shipped': 'status-shipped',
        'delivered': 'status-delivered',
        'completed': 'status-completed',
        'cancelled': 'status-cancelled',
        '待付款': 'status-pending',
        '已付款': 'status-paid',
        '已发货': 'status-shipped',
        '已送达': 'status-delivered',
        '已完成': 'status-completed',
        '已取消': 'status-cancelled'
      }
      return statusMap[status] || 'status-default'
    }
  }
}
</script>

<style scoped>
@import '../styles/OrderManagement.css';
</style>
