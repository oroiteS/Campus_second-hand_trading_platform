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
        <div class="balance-info">
          <div class="info-item">
            <span class="label">冻结金额：</span>
            <span class="value">¥{{ walletInfo.frozenAmount.toFixed(2) }}</span>
          </div>
          <div class="info-item">
            <span class="label">可用余额：</span>
            <span class="value available">¥{{ (walletInfo.balance - walletInfo.frozenAmount).toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 快捷功能 -->
      <div class="quick-actions">
        <div class="action-item" @click="goToTransactionHistory">
          <div class="action-icon" style="background-color: #4ECDC4;">
            📊
          </div>
          <span class="action-name">交易记录</span>
          <span class="action-arrow">→</span>
        </div>
        <!-- 删除银行卡管理功能 -->
        <!-- 删除安全设置功能 -->
      </div>

    </div>

    <!-- 最近交易 -->
    <div class="recent-transactions">
      <div class="section-header">
        <h3>最近交易</h3>
        <button @click="goToTransactionHistory" class="view-all-btn">查看全部</button>
      </div>
      <div class="transaction-list">
        <div v-if="recentTransactions.length === 0" class="empty-state">
          <div class="empty-icon">💰</div>
          <p>暂无交易记录</p>
        </div>
        <div v-for="transaction in recentTransactions" :key="transaction.id" class="transaction-item">
          <div class="transaction-icon" :class="transaction.type">
            {{ getTransactionIcon(transaction.type) }}
          </div>
          <div class="transaction-info">
            <div class="transaction-title">{{ transaction.title }}</div>
            <div class="transaction-time">{{ formatTime(transaction.time) }}</div>
          </div>
          <div class="transaction-amount" :class="transaction.type">
            {{ transaction.type === 'income' ? '+' : '-' }}¥{{ Math.abs(transaction.amount).toFixed(2) }}
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
            <div class="available-balance">可用余额：¥{{ (walletInfo.balance - walletInfo.frozenAmount).toFixed(2) }}</div>
          </div>
          <div class="withdraw-info">
            <p>• 提现手续费：2元/笔</p>
            <p>• 到账时间：1-3个工作日</p>
            <p>• 单笔最低提现金额：10元</p>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeWithdrawModal" class="cancel-btn">取消</button>
          <button @click="confirmWithdraw" :disabled="!withdrawAmount || withdrawAmount < 10 || withdrawAmount > (walletInfo.balance - walletInfo.frozenAmount)" class="confirm-btn">确认提现</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WalletManagement',
  data() {
    return {
      walletInfo: {
        balance: 1250.80,
        frozenAmount: 50.00
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
      recentTransactions: [
        {
          id: 1,
          type: 'income',
          title: '出售商品收入',
          amount: 350.00,
          time: new Date('2024-01-15 14:30:00')
        },
        {
          id: 2,
          type: 'expense',
          title: '购买商品支出',
          amount: 120.00,
          time: new Date('2024-01-14 10:20:00')
        },
        {
          id: 3,
          type: 'income',
          title: '充值',
          amount: 500.00,
          time: new Date('2024-01-13 16:45:00')
        }
      ]
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    goToTransactionHistory() {
      // 跳转到交易记录页面
      this.$message?.info('交易记录功能开发中')
    },
    // 删除 goToBankCards 方法
    // 删除 goToSecuritySettings 方法
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
        this.$message?.error('请输入有效的充值金额')
        return
      }
      
      // 模拟充值成功
      this.walletInfo.balance += parseFloat(this.rechargeAmount)
      this.$message?.success(`充值成功！金额：¥${this.rechargeAmount}`)
      
      // 添加交易记录
      this.recentTransactions.unshift({
        id: Date.now(),
        type: 'income',
        title: '充值',
        amount: parseFloat(this.rechargeAmount),
        time: new Date()
      })
      
      this.closeRechargeModal()
    },
    confirmWithdraw() {
      const amount = parseFloat(this.withdrawAmount)
      const availableBalance = this.walletInfo.balance - this.walletInfo.frozenAmount
      
      if (!amount || amount < 10) {
        this.$message?.error('最低提现金额为10元')
        return
      }
      
      if (amount > availableBalance) {
        this.$message?.error('提现金额不能超过可用余额')
        return
      }
      
      // 模拟提现成功
      this.walletInfo.balance -= amount
      this.$message?.success(`提现申请已提交！金额：¥${amount}，预计1-3个工作日到账`)
      
      // 添加交易记录
      this.recentTransactions.unshift({
        id: Date.now(),
        type: 'expense',
        title: '提现',
        amount: amount,
        time: new Date()
      })
      
      this.closeWithdrawModal()
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
}

.wallet-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
  position: sticky;
  top: 0;
  z-index: 100;
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

.view-all-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 14px;
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
}

.modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
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
}
</style>