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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
export default {
  name: 'NearbyUsers',
  mounted() {
    this.fetchNearbyUsers()
    },

  setup() {
    const route = useRoute()
    const showLocationInfo = ref(true)
    const locationInfo = ref({
      userId: '',
      longitude: '',
      latitude: '',
      
    })

    onMounted(() => {
      // 从路由参数中获取定位信息
      locationInfo.value = {
        userId: route.query.userId || '未知用户',
        longitude: route.query.lon || '未知',
        latitude: route.query.lat || '未知',
        
      }
      
      // 这里可以调用API获取附近用户，使用获取到的位置信息
      // fetchNearbyUsers(locationInfo.value.longitude, locationInfo.value.latitude)
    })

    return {
      showLocationInfo,
      locationInfo
    }
  },
  data() {
    return {
      currentPage: 1,
      usersPerPage: 8,
      nearbyUsers: []
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
    async fetchNearbyUsers() {
      const { userId, lat, lon } = this.$route.query

      try {
        const response = await fetch(
          `http://localhost:8086/api/user/nearby?userId=${userId}&lat=${lat}&lon=${lon}`,
          {
            method: 'POST',
            headers: {
              'Accept': '*/*'
            }
          }
        )

        if (!response.ok) {
          throw new Error('请求失败')
        }

        const data = await response.json()

        // 将后端返回的数据映射为当前页面可用的数据格式
        this.nearbyUsers = data.map((user, index) => ({
          id: index + 1,
          studentId: user.userId,
          nickname: user.realName,
          avatarUrl: user.avatarUrl, // 你可以在 UI 中使用这个头像字段
          latitude: user.userLocLatitude,
          longitude: user.userLocLongitude
        }))
      } catch (error) {
        console.error('获取附近用户失败:', error)
      }
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
@import '../styles/NearbyUsers.css';
.location-modal {
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
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 400px;
}

.info-row {
  margin: 10px 0;
  display: flex;
}

.label {
  font-weight: bold;
  width: 80px;
}

.value {
  flex: 1;
  word-break: break-all;
}

.close-btn {
  margin-top: 15px;
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>