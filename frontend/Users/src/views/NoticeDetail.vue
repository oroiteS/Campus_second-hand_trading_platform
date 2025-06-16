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
        <h2 class="notice-title">{{ notice.title }}</h2>
        <div class="notice-meta">
          <span class="notice-date">发布时间：{{ notice.date }}</span>
          <span class="notice-author">发布者：{{ notice.author }}</span>
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
        <div class="notice-actions">
          <button class="action-btn" @click="shareNotice">
            <span class="action-icon">📤</span>
            分享
          </button>
          <button class="action-btn" @click="collectNotice">
            <span class="action-icon">⭐</span>
            收藏
          </button>
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
      const noticeId = this.$route.params.id
      
      // 模拟公告详情数据
      const noticeDetails = {
        1: {
          id: 1,
          title: '新用户注册送积分活动',
          content: `
            <p>亲爱的同学们：</p>
            <p>为了鼓励更多同学参与校园二手交易，现推出新用户注册送积分活动！</p>
            <h3>活动详情：</h3>
            <ul>
              <li>新注册用户即可获得100积分</li>
              <li>完善个人信息额外获得50积分</li>
              <li>首次发布商品再获得100积分</li>
            </ul>
            <h3>积分用途：</h3>
            <ul>
              <li>可用于置顶商品</li>
              <li>可兑换平台优惠券</li>
              <li>可参与积分抽奖活动</li>
            </ul>
            <p>活动时间：即日起至本月底</p>
            <p>快来注册参与吧！</p>
          `,
          date: '2023-12-20',
          author: '平台管理员',
          tags: ['活动', '积分', '新用户']
        },
        2: {
          id: 2,
          title: '期末教材回收活动通知',
          content: `
            <p>各位同学：</p>
            <p>期末考试临近，为了帮助大家处理闲置教材，特举办教材回收活动。</p>
            <h3>回收范围：</h3>
            <ul>
              <li>各类专业教材</li>
              <li>考试辅导书籍</li>
              <li>工具书和参考书</li>
            </ul>
            <h3>回收地点：</h3>
            <ul>
              <li>东校区：图书馆一楼大厅</li>
              <li>西校区：学生活动中心</li>
              <li>南校区：教学楼A座大厅</li>
            </ul>
            <h3>回收时间：</h3>
            <p>每天上午9:00-11:30，下午14:00-17:00</p>
            <p>欢迎大家积极参与！</p>
          `,
          date: '2023-12-18',
          author: '学生会',
          tags: ['教材', '回收', '期末']
        },
        3: {
          id: 3,
          title: '诚信交易倡议书',
          content: `
            <p>亲爱的同学们：</p>
            <p>为了营造良好的校园二手交易环境，我们向全体用户发出诚信交易倡议：</p>
            <h3>诚信原则：</h3>
            <ul>
              <li>如实描述商品信息，不夸大不隐瞒</li>
              <li>按时履行交易约定，不随意毁约</li>
              <li>友善沟通，相互理解和尊重</li>
              <li>遵守平台规则，维护交易秩序</li>
            </ul>
            <h3>违规处理：</h3>
            <ul>
              <li>虚假信息：警告并删除商品</li>
              <li>恶意毁约：扣除信用积分</li>
              <li>严重违规：暂停账号使用</li>
            </ul>
            <p>让我们共同努力，打造诚信、友善的交易环境！</p>
          `,
          date: '2023-12-15',
          author: '平台管理员',
          tags: ['诚信', '倡议', '规则']
        }
      }
      
      this.notice = noticeDetails[noticeId] || {
        id: noticeId,
        title: '公告不存在',
        content: '<p>抱歉，您查看的公告不存在或已被删除。</p>',
        date: '',
        author: '',
        tags: []
      }
    },
    goBack() {
      this.$router.go(-1)
    },
    shareNotice() {
      // 分享功能
      if (navigator.share) {
        navigator.share({
          title: this.notice.title,
          text: this.notice.title,
          url: window.location.href
        })
      } else {
        // 复制链接到剪贴板
        navigator.clipboard.writeText(window.location.href).then(() => {
          alert('链接已复制到剪贴板')
        })
      }
    },
    collectNotice() {
      // 收藏功能
      alert('收藏成功！')
    }
  }
}
</script>

<style scoped>
@import '../styles/NoticeDetail.css';
</style>