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
      <div class="order-list" v-if="activeTab !== 'appeals' && filteredOrders.length > 0">
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

      <!-- 申诉管理列表 -->
      <div class="appeals-list" v-if="activeTab === 'appeals'">
        <div v-if="sortedAppealsList.length > 0">
          <div class="appeal-card" v-for="appeal in sortedAppealsList" :key="appeal.argumentId">
            <div class="appeal-header">
              <div class="appeal-id">申诉ID：{{ appeal.argumentId }}</div>
              <div class="appeal-status" :class="getAppealStatusClass(appeal.status)">
                {{ getAppealStatusText(appeal.status) }}
              </div>
            </div>
            
            <div class="appeal-content">
              <div class="appeal-info">
                <div class="info-row">
                  <span class="label">订单ID：</span>
                  <span class="value">{{ appeal.orderId }}</span>
                </div>
                <div class="info-row">
                  <span class="label">申诉方：</span>
                  <span class="value">{{ appeal.argue1Id }}</span>
                </div>
                <div class="info-row">
                  <span class="label">被申诉方：</span>
                  <span class="value">{{ appeal.argue2Id }}</span>
                </div>
                <div class="info-row">
                  <span class="label">申诉原因：</span>
                  <span class="value">{{ appeal.reason }}</span>
                </div>
                <div class="info-row">
                  <span class="label">创建时间：</span>
                  <span class="value">{{ formatDate(appeal.createdAt) }}</span>
                </div>
                <div class="info-row" v-if="appeal.rootId">
                  <span class="label">处理人：</span>
                  <span class="value">{{ appeal.rootId }}</span>
                </div>
              </div>
            </div>
            
            <!-- 只保留查看详情按钮 -->
            <div class="appeal-actions">
              <button class="action-btn detail" @click="viewAppealDetail(appeal)">
                查看详情
              </button>
            </div>
          </div>
        </div>
        
        <!-- 申诉列表空状态 -->
        <div class="empty-state" v-else>
          <div class="empty-icon">📋</div>
          <p class="empty-text">暂无申诉记录</p>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="activeTab !== 'appeals' && filteredOrders.length === 0">
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

    <!-- 申诉详情弹窗 -->
    <div v-if="showAppealDetailModal" class="modal-overlay" @click="closeAppealDetailModal">
      <div class="modal-content appeal-detail-modal" @click.stop>
        <div class="modal-header">
          <h3>申诉详情</h3>
          <button @click="closeAppealDetailModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <!-- 申诉信息 -->
          <div class="appeal-detail-info">
            <h4>申诉信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">申诉ID：</span>
                <span class="value">{{ currentAppeal?.argumentId }}</span>
              </div>
              <div class="info-item">
                <span class="label">申诉状态：</span>
                <span class="value" :class="getAppealStatusClass(currentAppeal?.status)">
                  {{ getAppealStatusText(currentAppeal?.status) }}
                </span>
              </div>
              <div class="info-item">
                <span class="label">申诉方：</span>
                <span class="value">{{ currentAppeal?.argue1Id }}</span>
              </div>
              <div class="info-item">
                <span class="label">被申诉方：</span>
                <span class="value">{{ currentAppeal?.argue2Id }}</span>
              </div>
              <div class="info-item full-width">
                <span class="label">申诉原因：</span>
                <span class="value">{{ currentAppeal?.reason }}</span>
              </div>
              <div class="info-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ formatDate(currentAppeal?.createdAt) }}</span>
              </div>
              <div class="info-item" v-if="currentAppeal?.rootId">
                <span class="label">处理人：</span>
                <span class="value">{{ currentAppeal?.rootId }}</span>
              </div>
            </div>
          </div>
          
          <!-- 相关订单信息 -->
          <div class="related-order-info" v-if="currentAppealOrder">
            <h4>相关订单</h4>
            <div class="order-card">
              <div class="order-header-info">
                <div class="order-number">订单号：{{ currentAppealOrder.orderNumber }}</div>
                <div class="order-status" :class="currentAppealOrder.statusClass">{{ currentAppealOrder.status }}</div>
              </div>

              <div class="order-content">
                <img :src="currentAppealOrder.productImage" :alt="currentAppealOrder.productName" class="product-image" />
                <div class="product-info">
                  <h4 class="product-name">{{ currentAppealOrder.productName }}</h4>
                  <p class="product-desc">{{ currentAppealOrder.productDesc }}</p>
                  <div class="order-details">
                    <span class="order-price">¥{{ currentAppealOrder.price }}</span>
                    <span class="order-quantity">数量：{{ currentAppealOrder.quantity }}</span>
                  </div>
                </div>
              </div>

              <div class="order-meta">
                <div class="order-time">{{ currentAppealOrder.createTime }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeAppealDetailModal" class="btn btn-cancel">关闭</button>
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
        { value: 'pending_transaction', label: '待交易' },
        { value: 'completed', label: '已完成' }
      ],
      orderTabs: [
        { id: 'buy', name: '我买到的' },
        { id: 'sell', name: '我卖出的' },
        { id: 'all', name: '全部订单' },
        { id: 'appeals', name: '申诉管理' } // 新增申诉管理标签
      ],
      showAppealModal: false,
      showRefundModal: false,
      showShippingModal: false,
      showAppealDetailModal: false, // 新增申诉详情弹窗
      currentOrder: null,
      currentAppeal: null, // 新增当前申诉记录
      newShippingStatus: '',
      shippingInfo: {
        trackingNumber: '',
        courier: ''
      },
      appealForm: {
        type: '',
        description: ''
      },
      appealsList: [], // 新增申诉列表数据
      currentAppealOrder: null, // 当前申诉相关的订单信息
      orders: {
        buy: [],
        sell: [
          // ... existing code ...
        ]
      },
      loading: false
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
    },
    // 新增：排序后的申诉列表，处理中的申诉置顶
    sortedAppealsList() {
      return [...this.appealsList].sort((a, b) => {
        // 处理中的申诉排在前面
        if (a.status === 'process' && b.status !== 'process') {
          return -1
        }
        if (a.status !== 'process' && b.status === 'process') {
          return 1
        }
        // 相同状态按创建时间倒序排列
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    }
  },
  mounted() {
    console.log('OrderManagement组件已挂载，当前标签页:', this.activeTab);
    // 组件挂载时同时获取买家和卖家订单数据
    this.fetchBuyerOrders()
    this.fetchSellerOrders()
    
    // 始终获取申诉列表数据，确保统计数量正确显示
    this.fetchAllAppeals();
    
    // 如果默认标签页是申诉管理，添加额外的调试日志
    if (this.activeTab === 'appeals') {
      console.log('默认标签页是申诉管理');
    }
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
        '已完成': 'completed'
      }
      return statusMap[order.status] || 'pending_transaction'
    },

    switchTab(tabId) {
      console.log('切换标签页:', tabId); // 添加调试日志
      this.activeTab = tabId
      this.selectedStatus = 'all' // 切换标签时重置状态筛选
      
      // 根据标签页类型调用相应的数据获取方法
      if (tabId === 'appeals') {
        console.log('切换到申诉管理标签页，开始获取申诉列表'); // 添加调试日志
        this.fetchAllAppeals(); // 添加申诉列表获取
      } else {
        this.refreshOrders();
      }
    },

    getOrderCount(tabId) {
      if (tabId === 'all') {
        return this.orders.buy.length + this.orders.sell.length
      } else if (tabId === 'appeals') {
        return this.appealsList.length
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

    async confirmReceive(order) {
      if (confirm('确认收到商品吗？')) {
        try {
          // 获取卖家ID
          const sellerId = order.sellerId || order.seller_id
          if (!sellerId) {
            this.$message?.error('无法获取卖家信息') || alert('无法获取卖家信息')
            return
          }

          // 调用确认收货API（同时完成卖家收款和订单状态更新）
          const confirmReceiptResponse = await axios.post(
            'http://localhost:8081/user/account/confirmReceipt',
            {
              userId: sellerId,  // 使用卖家ID
              orderID: order.id
            },
            {
              headers: {
                'Content-Type': 'application/json'
              },
              timeout: 10000
            }
          )

          console.log('确认收货API响应:', confirmReceiptResponse.data)

          // 更新本地订单状态
          order.status = '已完成'
          order.statusClass = 'completed'
          order.actions = [
            { type: 'contact', text: '联系卖家' },
            { type: 'appeal', text: '申诉' }
          ]

          this.$message?.success('确认收货成功！订单已完成，款项已转入卖家账户') || alert('确认收货成功！订单已完成，款项已转入卖家账户')
          
          // 刷新订单列表
          await this.fetchBuyerOrders()
          
        } catch (error) {
          console.error('确认收货失败:', error)
          
          // 添加详细的调试信息
          console.log('错误详情:')
          console.log('- 请求URL:', error.config?.url)
          console.log('- 请求方法:', error.config?.method)
          console.log('- 请求数据:', error.config?.data)
          console.log('- 请求头:', error.config?.headers)
          console.log('- 响应状态:', error.response?.status)
          console.log('- 响应数据:', error.response?.data)
          console.log('- 完整错误对象:', error)
          
          let errorMessage = '确认收货失败'
          
          if (error.response) {
            errorMessage = `确认收货失败：${error.response.status} ${error.response.statusText}`
            if (error.response.data && error.response.data.message) {
              errorMessage += ` - ${error.response.data.message}`
            }
            
            // 针对404错误的特殊提示
            if (error.response.status === 404) {
              console.error('404错误分析:')
              console.error('- 检查API端点是否正确:', error.config?.url)
              console.error('- 检查订单ID是否存在:', JSON.parse(error.config?.data || '{}').orderID)
              errorMessage += ' (API端点不存在或订单ID无效)'
            }
          } else if (error.request) {
            errorMessage = '网络请求失败，请检查网络连接'
            console.error('网络请求失败，无响应:', error.request)
          } else {
            errorMessage = `确认收货失败：${error.message}`
            console.error('请求配置错误:', error.message)
          }
          
          this.$message?.error(errorMessage) || alert(errorMessage)
        }
      }
    },

    contactUser(order) {
      console.log('联系用户:', order.orderNumber)
      this.$message?.info(`联系订单 ${order.orderNumber} 的相关用户`) || alert(`联系订单 ${order.orderNumber} 的相关用户`)
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

        // 构建申诉数据
        const appealData = {
          argue1Id: this.currentOrder.buyerId, // 改为小写开头
          argue2Id: this.currentOrder.sellerId, // 改为小写开头
          orderId: this.currentOrder.id, // 改为小写开头
          reason: `${this.appealForm.type}: ${this.appealForm.description}` // 改为小写开头
        };

        console.log('提交申诉数据:', appealData); // 调试信息

        // 直接使用axios调用后端API
        const response = await axios.post('http://localhost:8093/api/v1/appeals', appealData, {
          headers: {
            'Content-Type': 'application/json'
          }
        });

        console.log('申诉提交响应:', response.data);

        // 根据API文档，成功响应是201状态码
        if (response.status === 201 && (response.data.argumentId || response.data.status === 'process')) {
          this.$message.success('申诉提交成功');
          this.closeAppealModal();

          // 可选：刷新订单列表
          this.refreshOrders();
        } else {
          throw new Error('申诉提交失败：响应格式不正确');
        }
      } catch (error) {
        console.error('提交申诉失败:', error);

        let errorMessage = '申诉提交失败';

        if (error.response) {
          // 服务器返回了错误响应
          errorMessage = `申诉提交失败：${error.response.status} ${error.response.statusText}`;
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

    // 新增辅助方法：获取被申诉者ID
    getOppositeUserId() {
      // 根据订单类型确定被申诉者
      // 如果是买家申诉，被申诉者是卖家；如果是卖家申诉，被申诉者是买家
      const orderType = this.getOrderType(this.currentOrder.id)

      if (orderType === 'buy') {
        // 买家申诉卖家，返回卖家ID
        return this.currentOrder.sellerId || this.currentOrder.seller_id
      } else if (orderType === 'sell') {
        // 卖家申诉买家，返回买家ID  
        return this.currentOrder.buyerId || this.currentOrder.buyer_id
      }

      return null
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
              this.orders.buy = this.transformOrderData(orderData, 'buy')
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
              const transformedOrders = this.transformOrderData(orderData, 'sell')
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
    transformOrderData(apiData, viewType = 'buy') {
      console.log('开始转换订单数据:', apiData, 'viewType:', viewType)

      if (!Array.isArray(apiData)) {
        console.warn('API数据不是数组格式:', apiData)
        return []
      }

      return apiData.map(order => {
        console.log('转换单个订单:', order)

        // 根据订单状态和视图类型生成操作按钮
        const actions = this.generateOrderActions(order.orderStatus || 'pending_transaction', viewType)

        return {
          id: order.orderId,
          orderNumber: order.orderId,
          productName: order.commodityName,
          productImage: order.mainImageUrl || '/测试图片.jpg',
          price: order.money,
          quantity: order.buyQuantity,
          status: order.orderStatusDescription,
          statusClass: this.getStatusClass(order.orderStatus),
          createTime: order.createTime,
          saleTime: order.saleTime,
          saleLocation: order.saleLocation,
          buyerId: order.buyerId,
          sellerId: order.sellerId,
          commodityId: order.commodityId,
          buyerName: order.buyerName,
          sellerName: order.sellerName,
          actions: actions
        }
      })
    },

    generateOrderActions(orderStatus, viewType = 'buy') {
      const actions = []

      if (viewType === 'sell') {
        // 卖家视角的操作按钮
        switch (orderStatus) {
          case 'pending_transaction':
            // 待交易状态：联系买家、申诉
            actions.push(
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
        // 买家视角的操作按钮
        switch (orderStatus) {
          case 'pending_transaction':
            // 待交易状态：显示已收货按钮
            actions.push(
              { type: 'confirm', text: '已收货' },
              { type: 'contact', text: '联系卖家' },
              { type: 'appeal', text: '申诉' }
            )
            break
          case 'completed':
            // 已完成状态：联系卖家、申诉
            actions.push(
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
      console.log('刷新订单数据，当前标签页:', this.activeTab); // 添加调试日志
      if (this.activeTab === 'buy') {
        await this.fetchBuyerOrders()
      } else if (this.activeTab === 'sell') {
        await this.fetchSellerOrders()
      } else if (this.activeTab === 'appeals') {
        console.log('刷新申诉列表'); // 添加调试日志
        await this.fetchAllAppeals(); // 添加申诉列表刷新
      }
    },

    // 添加缺失的getStatusClass方法
    getStatusClass(status) {
      const statusMap = {
        'pending_transaction': 'status-pending',
        'completed': 'status-completed',
        '待交易': 'status-pending',
        '已完成': 'status-completed'
      }
      return statusMap[status] || 'status-default'
    },

    // 新增：获取所有申诉记录
    async fetchAllAppeals() {
      try {
        this.loading = true
        console.log('开始获取申诉记录...')
        console.log('请求URL: http://localhost:8093/api/v1/appeals/all'); // 添加URL调试
        
        const response = await axios.get('http://localhost:8093/api/v1/appeals/all', {
          timeout: 10000
        })
        
        console.log('申诉API响应状态:', response.status); // 添加状态调试
        console.log('申诉API响应头:', response.headers); // 添加响应头调试
        console.log('申诉API响应数据:', response.data)
        
        if (response.status === 200 && response.data.appeals) {
          this.appealsList = response.data.appeals
          console.log('申诉列表数据:', this.appealsList); // 添加列表数据调试
          console.log(`成功获取${response.data.count}条申诉记录`)
          this.$message?.success(`成功获取${response.data.count}条申诉记录`) || console.log(`成功获取${response.data.count}条申诉记录`)
        } else {
          console.log('响应格式检查失败 - status:', response.status, 'appeals字段:', response.data.appeals); // 添加格式检查调试
          throw new Error('获取申诉记录失败：响应格式不正确')
        }
      } catch (error) {
        console.error('获取申诉记录失败:', error)
        console.error('错误详情:', {
          message: error.message,
          response: error.response,
          request: error.request,
          config: error.config
        }); // 添加详细错误信息
        
        let errorMessage = '获取申诉记录失败'
        
        if (error.response) {
          console.log('服务器响应错误 - 状态码:', error.response.status); // 添加状态码调试
          console.log('服务器响应数据:', error.response.data); // 添加响应数据调试
          errorMessage = `获取申诉记录失败：${error.response.status} ${error.response.statusText}`
          if (error.response.data && error.response.data.error) {
            errorMessage += ` - ${error.response.data.error}`
          }
        } else if (error.request) {
          console.log('网络请求错误:', error.request); // 添加网络错误调试
          errorMessage = '无法连接到申诉服务器，请检查网络连接'
        } else {
          console.log('其他错误:', error.message); // 添加其他错误调试
          errorMessage = `获取申诉记录失败：${error.message}`
        }
        
        this.$message?.error(errorMessage) || alert(errorMessage)
        this.appealsList = [] // 失败时清空列表
      } finally {
        this.loading = false
        console.log('申诉记录获取完成，loading状态:', this.loading); // 添加完成状态调试
      }
    },

    // 新增：处理申诉操作（通过/拒绝）
    async handleAppealAction(appeal, action) {
      try {
        const actionText = action === 'finish' ? '通过' : '拒绝'
        if (!confirm(`确认${actionText}此申诉吗？`)) {
          return
        }

        const response = await axios.put(
          `http://localhost:8093/api/v1/appeals/${appeal.argumentId}/admin-update`,
          {
            status: action,
            rootId: localStorage.getItem('userId') || 'admin001' // 使用当前登录用户ID作为管理员ID
          },
          {
            headers: {
              'Content-Type': 'application/json'
            },
            timeout: 10000
          }
        )

        if (response.status === 200) {
          this.$message?.success(`申诉${actionText}成功`) || alert(`申诉${actionText}成功`)
          // 刷新申诉列表
          await this.fetchAllAppeals()
        } else {
          throw new Error(`申诉${actionText}失败：响应格式不正确`)
        }
      } catch (error) {
        console.error('处理申诉失败:', error)
        const actionText = action === 'finish' ? '通过' : '拒绝'
        let errorMessage = `申诉${actionText}失败`
        
        if (error.response) {
          errorMessage = `申诉${actionText}失败：${error.response.status} ${error.response.statusText}`
          if (error.response.data && error.response.data.error) {
            errorMessage += ` - ${error.response.data.error}`
          }
        } else if (error.request) {
          errorMessage = '无法连接到申诉服务器，请检查网络连接'
        } else {
          errorMessage = `申诉${actionText}失败：${error.message}`
        }
        
        this.$message?.error(errorMessage) || alert(errorMessage)
      }
    },

    // 新增：查看申诉详情
    viewAppealDetails(appeal) {
      this.currentAppeal = appeal
      this.showAppealDetailModal = true
    },

    // 新增：查看申诉详情（新方法名）
    async viewAppealDetail(appeal) {
      this.currentAppeal = appeal
      
      // 根据申诉中的订单ID获取订单详情
      try {
        // 从现有订单列表中查找相关订单
        const allOrders = [...this.orders.buy, ...this.orders.sell];
        this.currentAppealOrder = allOrders.find(order => order.id === appeal.orderId);
        
        if (!this.currentAppealOrder) {
          // 如果在现有订单中找不到，可以调用API获取
          console.log('未在现有订单中找到相关订单，订单ID:', appeal.orderId);
          // 这里可以添加API调用来获取订单详情
        }
      } catch (error) {
        console.error('获取申诉相关订单失败:', error);
      }
      
      this.showAppealDetailModal = true
    },

    // 新增：查找相关订单信息
    async findRelatedOrder(orderId) {
      // 在买家和卖家订单中查找
      let relatedOrder = null
      
      // 先在已有的订单列表中查找
      relatedOrder = this.orders.buy.find(order => order.id === orderId) ||
                    this.orders.sell.find(order => order.id === orderId)
      
      if (relatedOrder) {
        this.currentAppealOrder = relatedOrder
        return
      }
      
      // 如果在现有订单中找不到，尝试通过API获取
      try {
        // 这里可以调用API获取特定订单信息
        // 暂时设置为null，表示未找到相关订单
        this.currentAppealOrder = null
        console.log('未找到相关订单信息，订单ID:', orderId)
      } catch (error) {
        console.error('获取相关订单信息失败:', error)
        this.currentAppealOrder = null
      }
    },

    // 新增：关闭申诉详情弹窗
    closeAppealDetailModal() {
      this.showAppealDetailModal = false
      this.currentAppeal = null
      this.currentAppealOrder = null
    },

    // 新增：获取申诉状态样式类
    getAppealStatusClass(status) {
      const statusClasses = {
        'process': 'status-processing',
        'finish': 'status-finished', 
        'refuse': 'status-refused'
      }
      return statusClasses[status] || 'status-unknown'
    },

    // 新增：获取申诉状态文本
    getAppealStatusText(status) {
      const statusMap = {
        'process': '处理中',
        'finish': '已通过',
        'reject': '已拒绝'
      }
      return statusMap[status] || '未知状态'
    },

    // 新增：格式化日期
    formatDate(dateString) {
      if (!dateString) return '无'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
@import '../styles/OrderManagement.css';
</style>
