<template>
  <div class="nearby-users-container">
    <div class="nearby-users-card">
      <h2 class="nearby-title">附近的人</h2>
      
      <!-- 查询按钮 -->
      <div class="search-range">
        <button @click="searchNearbyUsers" class="search-btn">返回第一页</button>
      </div>
      
      <!-- 用户表格 -->
      <div class="users-table-container">
        <table class="users-table">
          <thead>
            <tr>
              <th>学号</th>
              <th>昵称</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in displayedUsers" :key="user.id">
              <td>{{ user.studentId }}</td>
              <td>{{ user.nickname }}</td>
              <td>
                <a href="#" class="detail-link" @click.prevent="viewUserDetail(user.id)">查看详情</a>
              </td>
            </tr>
            <tr v-if="displayedUsers.length === 0">
              <td colspan="3" class="no-data">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 分页控件 -->
      <div class="pagination">
        <button 
          @click="prevPage" 
          :disabled="currentPage === 1" 
          class="page-btn"
        >
          上一页
        </button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button 
          @click="nextPage" 
          :disabled="currentPage === totalPages" 
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NearbyUsers',
  data() {
    return {
      currentPage: 1,
      usersPerPage: 8,
      nearbyUsers: [
        // 模拟数据
        { id: 1, studentId: '2023001', nickname: '张同学' },
        { id: 2, studentId: '2023002', nickname: '李同学' },
        { id: 3, studentId: '2023003', nickname: '王同学' },
        { id: 4, studentId: '2023004', nickname: '赵同学' },
        { id: 5, studentId: '2023005', nickname: '陈同学' },
        { id: 6, studentId: '2023006', nickname: '刘同学' },
        { id: 7, studentId: '2023007', nickname: '周同学' },
        { id: 8, studentId: '2023008', nickname: '吴同学' },
        { id: 9, studentId: '2023009', nickname: '郑同学' },
        { id: 10, studentId: '2023010', nickname: '孙同学' },
        { id: 11, studentId: '2023011', nickname: '马同学' },
        { id: 12, studentId: '2023012', nickname: '朱同学' }
      ]
    }
  },
  computed: {
    displayedUsers() {
      const start = (this.currentPage - 1) * this.usersPerPage
      const end = start + this.usersPerPage
      return this.nearbyUsers.slice(start, end)
    },
    totalPages() {
      return Math.ceil(this.nearbyUsers.length / this.usersPerPage)
    }
  },
  methods: {
    searchNearbyUsers() {
      // 这里应该调用API获取附近的用户
      // 目前使用模拟数据
      console.log('返回第一页')
      this.currentPage = 1 // 重置到第一页
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
      }
    },
    viewUserDetail(userId) {
      console.log(`查看用户详情: ${userId}`)
      // 这里应该跳转到用户详情页面
    }
  }
}
</script>

<style scoped>
.nearby-users-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 20px;
}

.nearby-users-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 800px;
  padding: 30px;
}

.nearby-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.search-range {
  display: flex;
  justify-content: center;
  margin-bottom: 25px;
}

.search-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s;
  font-size: 16px;
}

.search-btn:hover {
  background: #5a6fd8;
}

.users-table-container {
  margin-bottom: 20px;
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.users-table th {
  background-color: #f8f9fa;
  padding: 12px 15px;
  font-weight: bold;
  color: #333;
  border-bottom: 2px solid #ddd;
}

.users-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  color: #666;
}

.users-table tr:last-child td {
  border-bottom: none;
}

.users-table tr:hover td {
  background-color: #f8f9fa;
}

.detail-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.detail-link:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 30px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.page-btn {
  background: #f0f0f0;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.page-btn:hover:not(:disabled) {
  background: #e0e0e0;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .nearby-users-card {
    padding: 20px;
  }
}
</style>