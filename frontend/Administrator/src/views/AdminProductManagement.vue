<template>
  <div class="product-management">
    <h2>商品管理</h2>
    
    <!-- 商品列表 -->
    <div class="product-list">
      <table>
        <thead>
          <tr>
            <th>商品ID</th>
            <th>商品名称</th>
            <th>用户名</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="commodity in commodities" :key="commodity.id">
            <td>{{ commodity.id }}</td>
            <td>{{ commodity.name }}</td>
            <td>{{ commodity.username }}</td>
            <td>{{ commodity.status }}</td>
            <td>
              <button @click="updateStatus(commodity.id, '已审核')" 
                      :disabled="commodity.status === '已审核'">
                审核通过
              </button>
              <button @click="updateStatus(commodity.id, '已拒绝')" 
                      :disabled="commodity.status === '已拒绝'">
                拒绝
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { commodityService } from '../api/commodityService'

export default {
  name: 'AdminProductManagement',
  data() {
    return {
      commodities: [],
      loading: false
    }
  },
  async created() {
    await this.loadCommodities()
  },
  methods: {
    // 加载商品列表
    async loadCommodities() {
      this.loading = true
      try {
        const result = await commodityService.getCommoditiesWithUsername()
        if (result.success) {
          this.commodities = result.data
        } else {
          alert(result.message)
        }
      } catch (error) {
        console.error('加载商品列表失败:', error)
        alert('加载商品列表失败')
      } finally {
        this.loading = false
      }
    },

    // 更新商品状态
    async updateStatus(commodityId, newStatus) {
      try {
        const result = await commodityService.updateCommodityStatus(commodityId, newStatus)
        if (result.success) {
          alert(result.data.message)
          // 重新加载商品列表
          await this.loadCommodities()
        } else {
          alert(result.message)
        }
      } catch (error) {
        console.error('更新商品状态失败:', error)
        alert('更新商品状态失败')
      }
    }
  }
}
</script>