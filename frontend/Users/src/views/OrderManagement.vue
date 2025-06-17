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
        <button 
          v-for="tab in orderTabs" 
          :key="tab.id"
          class="tab-btn"
          :class="{active: activeTab === tab.id}"
          @click="activeTab = tab.id"
        >
          {{ tab.name }}
          <span class="tab-count">({{ getOrderCount(tab.id) }})</span>
        </button>
      </div>

      <!-- 订单列表 -->
      <div class="order-list" v-if="currentOrders.length > 0">
        <div class="order-card" v-for="order in currentOrders" :key="order.id">
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
              <button 
                v-for="action in order.actions" 
                :key="action.type"
                class="action-btn"
                :class="action.type"
                @click="handleOrderAction(order, action.type)"
              >
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
            <textarea 
              v-model="appealForm.description" 
              class="form-control" 
              rows="4" 
              :placeholder="getOrderType(currentOrder?.id) === 'buy' ? '请详细描述您遇到的问题...' : '请详细描述买家的问题行为或平台错误...'"
            ></textarea>
          </div>
          <div class="form-group">
            <label>上传凭证：</label>
            <input type="file" @change="handleFileUpload" class="form-control" multiple accept="image/*">
            <div class="file-tips">支持上传图片，最多5张（聊天记录截图、商品照片等）</div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeAppealModal" class="btn btn-cancel">取消</button>
          <button @click="submitAppeal" class="btn btn-primary" :disabled="!appealForm.type || !appealForm.description">提交申诉</button>
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
            <textarea 
              v-model="refundForm.description" 
              class="form-control" 
              rows="3" 
              placeholder="请说明退款原因..."
            ></textarea>
          </div>
          <div class="form-group">
            <label>退款金额：</label>
            <input 
              v-model.number="refundForm.amount" 
              type="number" 
              class="form-control" 
              :max="currentOrder?.price"
              step="0.01"
            >
            <div class="form-tips">最大退款金额：¥{{ currentOrder?.price }}</div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeRefundModal" class="btn btn-cancel">取消</button>
          <button @click="submitRefund" class="btn btn-primary" :disabled="!refundForm.reason || !refundForm.amount">申请退款</button>
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
            <input 
              v-model="shippingInfo.trackingNumber" 
              type="text" 
              class="form-control" 
              placeholder="请输入快递单号"
            >
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
export default {
  name: 'OrderManagement',
  data() {
    return {
      activeTab: 'buy',
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
      appealForm: {
        type: '',
        description: '',
        files: []
      },
      refundForm: {
        reason: '',
        description: '',
        amount: 0
      },
      orders: {
        buy: [
          {
            id: 1,
            orderNumber: 'ORD202312200001',
            productName: 'MacBook Air M1',
            productDesc: '9成新，配件齐全',
            productImage: 'https://via.placeholder.com/80x80/F0F0F0/666666?text=MacBook',
            price: 6500,
            quantity: 1,
            status: '已完成',
            statusClass: 'completed',
            createTime: '2023-12-20 14:30',
            actions: [
              { type: 'review', text: '评价' },
              { type: 'contact', text: '联系卖家' },
              { type: 'appeal', text: '申诉' }
            ]
          },
          {
            id: 2,
            orderNumber: 'ORD202312190001',
            productName: '高等数学教材',
            productDesc: '第七版，8成新',
            productImage: 'https://via.placeholder.com/80x80/4CAF50/FFFFFF?text=教材',
            price: 25,
            quantity: 1,
            status: '待收货',
            statusClass: 'pending',
            createTime: '2023-12-19 10:15',
            actions: [
              { type: 'confirm', text: '确认收货' },
              { type: 'contact', text: '联系卖家' },
              { type: 'refund', text: '申请退款' }
            ]
          }
        ],
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
      }
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
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
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
      
      switch(actionType) {
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
      console.log('联系用户:', order.orderNumber)
      this.$message?.info(`联系订单 ${order.orderNumber} 的相关用户`) || alert(`联系订单 ${order.orderNumber} 的相关用户`)
    },
    // 申诉相关方法
    openAppealModal(order) {
      this.currentOrder = order
      // 根据订单类型设置不同的申诉选项
      this.appealForm = {
        type: '',
        description: '',
        files: []
      }
      this.showAppealModal = true
    },
    closeAppealModal() {
      this.showAppealModal = false
      this.currentOrder = null
    },
    handleFileUpload(event) {
      const files = Array.from(event.target.files)
      if (files.length > 5) {
        this.$message?.warning('最多只能上传5张图片') || alert('最多只能上传5张图片')
        return
      }
      this.appealForm.files = files
    },
    submitAppeal() {
      if (!this.appealForm.type || !this.appealForm.description) {
        this.$message?.warning('请填写完整的申诉信息') || alert('请填写完整的申诉信息')
        return
      }
      
      console.log('提交申诉:', {
        orderId: this.currentOrder.id,
        orderNumber: this.currentOrder.orderNumber,
        orderType: this.getOrderType(this.currentOrder.id), // 区分买入/卖出订单
        ...this.appealForm
      })
      
      this.$message?.success('申诉提交成功，我们会在3个工作日内处理') || alert('申诉提交成功，我们会在3个工作日内处理')
      this.closeAppealModal()
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
      
      // 更新订单状态
      this.currentOrder.status = this.newShippingStatus
      
      // 根据状态设置样式类
      switch(this.newShippingStatus) {
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
      
      // 记录物流信息（如果有）
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
    }
  }
}
</script>

<style scoped>
.order-management {
    background-color: #f8f9fa;
    min-height: 100vh;
  }
  
  /* 顶部导航 */
  .order-management .order-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: sticky;
    top: 0;
    z-index: 100;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  }
  
  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 20px;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .header-btn {
    background: rgba(255,255,255,0.2);
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .header-btn:hover {
    background: rgba(255,255,255,0.3);
  }
  
  .page-title {
    color: white;
    font-size: 20px;
    font-weight: bold;
    margin: 0;
  }
  
  .header-placeholder {
    width: 80px;
  }
  
  /* 订单容器 */
  .order-management .order-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  /* 订单标签页 */
  .order-management .order-tabs {
    display: flex;
    background: white;
    border-radius: 12px;
    padding: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
  
  .tab-btn {
    flex: 1;
    padding: 12px 20px;
    border: none;
    background: transparent;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    font-weight: 500;
  }
  
  .tab-btn.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
  
  .tab-count {
    font-size: 12px;
    opacity: 0.8;
  }
  
  /* 订单列表 */
  .order-management .order-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .order-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    transition: all 0.3s;
  }
  
  .order-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  }
  
  .order-header-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #eee;
  }
  
  .order-number {
    font-size: 14px;
    color: #666;
  }
  
  .order-status {
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: bold;
  }
  
  .order-status.pending {
    background: #fff3cd;
    color: #856404;
  }
  
  .order-status.shipped {
    background: #d1ecf1;
    color: #0c5460;
  }
  
  .order-status.completed {
    background: #d4edda;
    color: #155724;
  }
  
  .order-status.not-shipped {
    background: #f8d7da;
    color: #721c24;
  }
  
  .order-content {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
  }
  
  .product-image {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    object-fit: cover;
  }
  
  .product-info {
    flex: 1;
  }
  
  .product-name {
    font-size: 16px;
    font-weight: bold;
    margin: 0 0 8px 0;
    color: #333;
  }
  
  .product-desc {
    font-size: 14px;
    color: #666;
    margin: 0 0 12px 0;
  }
  
  .order-details {
    display: flex;
    gap: 20px;
  }
  
  .order-price {
    font-size: 18px;
    font-weight: bold;
    color: #e74c3c;
  }
  
  .order-quantity {
    font-size: 14px;
    color: #666;
  }
  
  .order-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #eee;
  }
  
  .order-time {
    font-size: 14px;
    color: #666;
  }
  
  .order-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
  
  .action-btn {
    padding: 6px 16px;
    border: 1px solid #ddd;
    background: white;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;
    white-space: nowrap;
  }
  
  .action-btn:hover {
    background: #f8f9fa;
  }
  
  .action-btn.confirm {
    background: #28a745;
    color: white;
    border-color: #28a745;
  }
  
  .action-btn.confirm:hover {
    background: #218838;
  }
  
  .action-btn.appeal {
    background: #ffc107;
    color: #212529;
    border-color: #ffc107;
  }
  
  .action-btn.appeal:hover {
    background: #e0a800;
  }
  
  .action-btn.refund {
    background: #dc3545;
    color: white;
    border-color: #dc3545;
  }
  
  .action-btn.refund:hover {
    background: #c82333;
  }
  
  .action-btn.shipping {
    background: #17a2b8;
    color: white;
    border-color: #17a2b8;
  }
  
  .action-btn.shipping:hover {
    background: #138496;
  }
  
  /* 空状态 */
  .empty-state {
    text-align: center;
    padding: 60px 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  .empty-text {
    font-size: 16px;
    color: #666;
    margin: 0;
  }
  
  /* 弹窗样式 */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: white;
    border-radius: 12px;
    width: 90%;
    max-width: 500px;
    max-height: 80vh;
    overflow-y: auto;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
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
    font-size: 18px;
    font-weight: bold;
  }
  
  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #666;
    padding: 0;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .close-btn:hover {
    color: #333;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 20px;
    border-top: 1px solid #eee;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
  }
  
  .form-control {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 14px;
    transition: border-color 0.3s;
    box-sizing: border-box;
  }
  
  .form-control:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  }
  
  .file-tips, .form-tips {
    font-size: 12px;
    color: #666;
    margin-top: 4px;
  }
  
  .refund-info, .shipping-info, .appeal-info {
    background: #f8f9fa;
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
  
  .refund-info p, .shipping-info p, .appeal-info p {
    margin: 0 0 8px 0;
    font-size: 14px;
  }
  
  .refund-info p:last-child, .shipping-info p:last-child, .appeal-info p:last-child {
    margin-bottom: 0;
  }
  
  .appeal-info {
    background: #f8f9fa;
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
  
  .appeal-info p {
    margin: 0 0 8px 0;
    font-size: 14px;
  }
  
  .appeal-info p:last-child {
    margin-bottom: 0;
  }
  
  /* 发货状态选择样式 */
  .status-options {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .status-option {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px;
    border: 2px solid #eee;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .status-option:hover {
    border-color: #667eea;
    background: #f8f9ff;
  }
  
  .status-option input[type="radio"] {
    margin: 0;
    margin-top: 2px;
  }
  
  .status-option input[type="radio"]:checked + .status-content .status-label {
    color: #667eea;
    font-weight: bold;
  }
  
  .status-content {
    display: flex;
    flex-direction: column;
  }
  
  .status-label {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 4px;
  }
  
  .status-desc {
    font-size: 12px;
    color: #666;
    display: block;
  }
  
  .btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s;
  }
  
  .btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  .btn-cancel {
    background: #6c757d;
    color: white;
  }
  
  .btn-cancel:hover:not(:disabled) {
    background: #5a6268;
  }
  
  .btn-primary {
    background: #667eea;
    color: white;
  }
  
  .btn-primary:hover:not(:disabled) {
    background: #5a6fd8;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .order-container {
      padding: 10px;
    }
    
    .order-content {
      flex-direction: column;
    }
    
    .product-image {
      width: 100%;
      height: 200px;
    }
    
    .order-meta {
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;
    }
    
    .order-actions {
      width: 100%;
      justify-content: flex-start;
    }
    
    .modal-content {
      width: 95%;
      margin: 10px;
    }
    
    .modal-footer {
      flex-direction: column;
    }
    
    .btn {
      width: 100%;
    }
    
    .status-options {
      gap: 8px;
    }
    
    .status-option {
      padding: 8px;
    }
  }</style>