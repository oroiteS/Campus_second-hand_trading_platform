<template>
  <div class="notice-detail">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <button class="back-btn" @click="goBack">
          <span class="back-icon">←</span>
          返回
        </button>
        <h1 class="page-title">公告详情</h1>
        <div class="header-actions"></div>
      </div>
    </div>

    <!-- 公告内容 -->
    <div class="notice-content">
      <div class="notice-header">
        <h2 class="notice-title">系统公告</h2>
        <div class="notice-meta">
          <span class="notice-date">{{ notice.date }}</span>
          <span class="notice-author">{{ notice.author }}</span>
        </div>
      </div>
      
      <div class="notice-body">
        <div class="notice-text" v-html="notice.content"></div>
      </div>
      
      <div class="notice-footer">
        <div class="notice-tags" v-if="notice.tags && notice.tags.length > 0">
          <span class="tag" v-for="tag in notice.tags" :key="tag">
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NoticeDetail',
  data() {
    return {
      notice: {
        id: null,
        title: '',
        content: '',
        date: '',
        author: '',
        tags: []
      }
    }
  },
  mounted() {
    this.loadNoticeDetail()
  },
  methods: {
    loadNoticeDetail() {
      // 从路由query参数中获取公告信息
      const query = this.$route.query;
      
      if (query.error === 'notfound') {
        // 处理未找到公告的情况
        this.notice = {
          id: null,
          title: '系统公告',
          content: '<p>抱歉，您查看的公告不存在或已被删除。</p>',
          date: '',
          author: '',
          tags: []
        };
        return;
      }
      
      if (query.id && query.content && query.createdAt) {
        // 从query参数构建公告对象
        this.notice = {
          id: query.id,
          title: '系统公告', // 固定标题为"系统公告"
          content: query.content,
          date: new Date(query.createdAt).toLocaleString(), // 格式化日期
          author: query.rootName || '平台管理员',
          tags: query.tags ? query.tags.split(',') : []
        };
        
        console.log('从路由参数加载公告详情成功:', this.notice);
      } else {
        // 参数不完整，显示错误信息
        this.notice = {
          id: query.id || null,
          title: '系统公告',
          content: '<p>抱歉，公告信息不完整，无法正确显示。</p>',
          date: '',
          author: '',
          tags: []
        };
        
        console.error('公告参数不完整:', query);
      }
    },
    

    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
@import '../styles/NoticeDetail.css';
</style>