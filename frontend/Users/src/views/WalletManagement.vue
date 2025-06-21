<template>
  <div class="wallet-page">
    <!-- 顶部导航 -->
    <header class="wallet-header">
      <div class="header-content">
        <button @click="goBack" class="header-btn back-btn">
          ← 返回
        </button>
        <h1 class="page-title">钱包管理</h1>
        <div class="header-placeholder"></div>
      </div>
    </header>
    
    <div class="wallet-content">

    <div class="wallet-container">
      <!-- 钱包余额卡片 -->
      <div class="balance-card">
        <div class="balance-header">
          <h2>我的余额</h2>
          <div class="balance-actions">
            <button @click="showRechargeModal = true" class="action-btn recharge-btn">
              充值
            </button>
            <button @click="showWithdrawModal = true" class="action-btn withdraw-btn">
              提现
            </button>
          </div>
        </div>
        <div class="balance-amount">
          <span class="currency">¥</span>
          <span class="amount">{{ walletInfo.balance.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 交易记录 -->
    <div class="recent-transactions">
      <div class="section-header">
        <h3>交易记录</h3>
      </div>
      <div class="transaction-list">
        <div v-if="recentTransactions.length === 0" class="empty-state">
          <div class="empty-icon">💰</div>
          <p>暂无交易记录</p>
        </div>
        <div v-for="transaction in recentTransactions" :key="transaction.orderId" class="transaction-item">
          <div class="transaction-icon">
            💰
          </div>
          <div class="transaction-info">
            <div class="transaction-title">{{ transaction.commodityName }}</div>
            <div class="transaction-details">
              <div class="transaction-detail"><span class="detail-label">买家:</span> {{ transaction.buyerName }}</div>
              <div class="transaction-detail"><span class="detail-label">卖家:</span> {{ transaction.sellerName }}</div>
              <div class="transaction-detail"><span class="detail-label">状态:</span> {{ transaction.orderStatusDescription }}</div>
              <div class="transaction-detail"><span class="detail-label">时间:</span> {{ transaction.saleTime }}</div>
              <div class="transaction-detail"><span class="detail-label">数量:</span> {{ transaction.buyQuantity || 1 }}</div>
            </div>
          </div>
          <div class="transaction-amount">
            ¥{{ transaction.money.toFixed(2) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 充值弹窗 -->
    <div v-if="showRechargeModal" class="modal-overlay" @click="closeRechargeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>充值</h3>
          <button @click="closeRechargeModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="amount-input">
            <label>充值金额</label>
            <input v-model="rechargeAmount" type="number" placeholder="请输入充值金额" class="amount-field" />
          </div>
          <div class="payment-methods">
            <label>支付方式</label>
            <div class="method-list">
              <div v-for="method in paymentMethods" :key="method.id" 
                   class="method-item" 
                   :class="{ active: selectedPaymentMethod === method.id }"
                   @click="selectedPaymentMethod = method.id">
                <span class="method-icon">{{ method.icon }}</span>
                <span class="method-name">{{ method.name }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeRechargeModal" class="cancel-btn">取消</button>
          <button @click="confirmRecharge" :disabled="!rechargeAmount || rechargeAmount <= 0" class="confirm-btn">确认充值</button>
        </div>
      </div>
    </div>

    <!-- 提现弹窗 -->
    <div v-if="showWithdrawModal" class="modal-overlay" @click="closeWithdrawModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>提现</h3>
          <button @click="closeWithdrawModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="amount-input">
            <label>提现金额</label>
            <input v-model="withdrawAmount" type="number" placeholder="请输入提现金额" class="amount-field" />
            <div class="available-balance">余额：¥{{ walletInfo.balance.toFixed(2) }}</div>
          </div>
          <div class="withdraw-info">
            <p>• 到账时间：1-3个工作日</p>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeWithdrawModal" class="cancel-btn">取消</button>
          <button @click="confirmWithdraw" :disabled="!withdrawAmount || withdrawAmount <= 0 || withdrawAmount > walletInfo.balance" class="confirm-btn">确认提现</button>
        </div>
      </div>
    </div>
    
    </div> <!-- 结束wallet-content -->
  </div>
</template>

<script>
import {ax1} from '@/api/axios'

export default {
  name: 'WalletManagement',
  data() {
    return {
      userId: '',
      walletInfo: {
        balance: 0.00
      },
      showRechargeModal: false,
      showWithdrawModal: false,
      rechargeAmount: '',
      withdrawAmount: '',
      selectedPaymentMethod: 1,
      paymentMethods: [
        { id: 1, name: '微信支付', icon: '💚' },
        { id: 2, name: '支付宝', icon: '💙' },
        { id: 3, name: '银行卡', icon: '💳' }
      ],
      recentTransactions: [],
      isLoading: false,
      errorMessage: '',
      transactionType: 'all' // 只显示全部交易
    }
  },
  created() {
    // 从路由参数中获取userId
    if (this.$route.query.userId) {
      console.log('钱包管理页面获取到的userId:', this.$route.query.userId);
      this.userId = this.$route.query.userId;
      // 获取钱包余额
      this.fetchWalletBalance();
      // 获取交易记录（根据当前选择的类型）
      this.fetchTransactions();
    } else {
      console.error('未获取到用户ID');
      alert('未获取到用户ID，请重新登录');
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    // 获取交易记录（统一方法）
    fetchTransactions() {
      this.fetchAllTransactions();
    },
    
    // 获取钱包余额
    fetchWalletBalance() {
      if (!this.userId) {
        console.error('获取钱包余额失败：用户ID不存在');
        return;
      }
      
      this.isLoading = true;
      
      // 调用后端API获取钱包余额
      ax1.post('/api-8081/user/account/balance', {
        userId: this.userId
      })
      .then(response => {
        if (response.data.code === 200) {
          // 更新钱包余额 - 直接使用data字段的值作为余额
          this.walletInfo.balance = response.data.data || 0;
          console.log('成功获取钱包余额:', this.walletInfo.balance);
        } else {
          console.error('获取钱包余额失败:', response.data.message);
          this.errorMessage = response.data.message || '获取钱包余额失败';
          alert(this.errorMessage);
        }
      })
      .catch(error => {
        console.error('获取钱包余额请求出错:', error);
        this.errorMessage = '网络错误，请稍后重试';
        alert(this.errorMessage);
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    
    // 获取全部交易记录
    fetchAllTransactions() {
      if (!this.userId) {
        console.error('获取交易记录失败：用户ID不存在');
        return;
      }
      
      this.isLoading = true;
      
      // 调用后端API获取全部交易记录
      ax1.post('/api-8095/orders/query/by-user', {
        user_id: this.userId
      })
      .then(response => {
        if (response.data.code === 200 && response.data.success) {
          // 更新交易记录
          this.recentTransactions = response.data.data || [];
          console.log('成功获取全部交易记录:', this.recentTransactions);
        } else {
          console.error('获取交易记录失败:', response.data.message);
          alert(response.data.message || '获取交易记录失败');
        }
      })
      .catch(error => {
        console.error('获取交易记录请求出错:', error);
        alert('网络错误，请稍后重试');
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    
    closeRechargeModal() {
      this.showRechargeModal = false
      this.rechargeAmount = ''
      this.selectedPaymentMethod = 1
    },
    closeWithdrawModal() {
      this.showWithdrawModal = false
      this.withdrawAmount = ''
    },
    confirmRecharge() {
      if (!this.rechargeAmount || this.rechargeAmount <= 0) {
        alert('请输入有效的充值金额')
        return
      }
      
      if (!this.userId) {
        alert('用户ID不存在，请重新登录')
        return
      }
      
      this.isLoading = true;
      
      // 调用后端API进行充值
      ax1.post('/api-8081/user/account/recharge', {
        userId: this.userId,
        amount: parseFloat(this.rechargeAmount),
        paymentMethod: this.selectedPaymentMethod
      })
      .then(response => {
        if (response.data.code === 200) {
          // 充值成功
          alert(`充值成功！金额：¥${this.rechargeAmount}`)
          
          // 重新获取钱包余额和交易记录
          this.fetchWalletBalance();
          this.fetchTransactions();
          
          this.closeRechargeModal();
        } else {
          console.error('充值失败:', response.data.message);
          alert(response.data.message || '充值失败，请稍后重试');
        }
      })
      .catch(error => {
        console.error('充值请求出错:', error);
        alert('网络错误，请稍后重试');
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    confirmWithdraw() {
      const amount = parseFloat(this.withdrawAmount)
      
      if (!amount || amount <= 0) {
        alert('请输入有效的提现金额')
        return
      }
      
      if (amount > this.walletInfo.balance) {
        alert('提现金额不能超过余额')
        return
      }
      
      if (!this.userId) {
        this.$message?.error('用户ID不存在，请重新登录')
        return
      }
      
      this.isLoading = true;
      
      // 调用后端API进行提现
      ax1.post('/api-8081/user/account/withdraw', {
        userId: this.userId,
        amount: amount
      })
      .then(response => {
        if (response.data.code === 200) {
          // 提现成功
          alert(`提现申请已提交！金额：¥${amount}，预计1-3个工作日到账`)
          
          // 重新获取钱包余额和交易记录
          this.fetchWalletBalance();
          this.fetchTransactions();
          
          this.closeWithdrawModal();
        } else {
          console.error('提现失败:', response.data.message);
          alert(response.data.message || '提现失败，请稍后重试');
        }
      })
      .catch(error => {
        console.error('提现请求出错:', error);
        alert('网络错误，请稍后重试');
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    getTransactionIcon(type) {
      return type === 'income' ? '💰' : '💸'
    },
    formatTime(time) {
      const now = new Date()
      const diff = now - new Date(time)
      const days = Math.floor(diff / 86400000)
      
      if (days === 0) {
        return '今天 ' + new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else if (days === 1) {
        return '昨天 ' + new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else {
        return new Date(time).toLocaleDateString('zh-CN')
      }
    }
  }
}
</script>

<style scoped>
.wallet-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  position: relative;
  overflow-x: hidden;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.wallet-content {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch; /* 增强iOS滚动体验 */
  position: relative;
  z-index: 1;
}

.wallet-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.header-placeholder {
  width: 60px;
}

.wallet-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.balance-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.balance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.balance-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.balance-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.balance-amount {
  font-size: 48px;
  font-weight: 700;
  margin: 20px 0;
}

.currency {
  font-size: 24px;
  margin-right: 5px;
}

.balance-info {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.label {
  font-size: 14px;
  opacity: 0.8;
}

.value {
  font-size: 16px;
  font-weight: 600;
}

.value.available {
  color: #FFE082;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.action-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
}

.action-item:last-child {
  border-bottom: none;
}

.action-item:hover {
  background-color: #f8f9fa;
  margin: 0 -20px;
  padding-left: 20px;
  padding-right: 20px;
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 15px;
}

.action-name {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.action-arrow {
  color: #999;
  font-size: 18px;
}

.recent-transactions {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.transaction-filter {
  display: flex;
  align-items: center;
}

.transaction-select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background-color: #f8f9fa;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
}

.transaction-select:hover {
  border-color: #007bff;
}

.transaction-select:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 15px;
}

.transaction-icon.income {
  background-color: #E8F5E8;
}

.transaction-icon.expense {
  background-color: #FFF3E0;
}

.transaction-info {
  flex: 1;
}

.transaction-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.transaction-time {
  font-size: 14px;
  color: #999;
}

.transaction-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.transaction-detail {
  margin-right: 10px;
}

.detail-label {
  font-weight: 500;
  color: #555;
  margin-right: 4px;
}

.transaction-amount {
  font-size: 16px;
  font-weight: 600;
}

.transaction-amount.income {
  color: #4CAF50;
}

.transaction-amount.expense {
  color: #FF5722;
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
  touch-action: none; /* 防止触摸事件穿透 */
}

.modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch; /* 增强iOS滚动体验 */
  position: relative;
  z-index: 1001;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

.amount-input {
  margin-bottom: 20px;
}

.amount-input label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.amount-field {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}

.available-balance {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}

.payment-methods label {
  display: block;
  margin-bottom: 12px;
  font-weight: 500;
  color: #333;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-item.active {
  border-color: #007bff;
  background-color: #f0f8ff;
}

.method-icon {
  margin-right: 10px;
  font-size: 18px;
}

.method-name {
  font-size: 16px;
}

.withdraw-info {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
}

.withdraw-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.modal-footer {
  display: flex;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn, .confirm-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #f8f9fa;
  color: #666;
}

.cancel-btn:hover {
  background-color: #e9ecef;
}

.confirm-btn {
  background-color: #007bff;
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.confirm-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .wallet-container {
    padding: 15px;
  }
  
  .balance-card {
    padding: 20px;
  }
  
  .balance-amount {
    font-size: 36px;
  }
  
  .balance-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .balance-info {
    flex-direction: column;
    gap: 15px;
  }
  
  .transaction-details {
    flex-direction: column;
    gap: 5px;
  }
  
  .transaction-detail {
    margin-right: 0;
    margin-bottom: 3px;
  }
}

/* 修复触摸事件 */
* {
  touch-action: manipulation; /* 优化触摸操作 */
}
</style>@/api/axios1