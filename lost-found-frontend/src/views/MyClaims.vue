<template>
  <div class="my-claims-page">
    <div class="page-header">
      <div class="user-card">
        <div class="avatar-wrapper">
          <div class="avatar">📋</div>
        </div>
        <div class="user-info">
          <h3 class="user-name">我的认领</h3>
          <p class="user-desc">查看你提交的物品认申请记录</p>
        </div>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-value">{{ claims.length }}</span>
            <span class="stat-label">全部</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ claims.filter(c => c.status === 1).length }}</span>
            <span class="stat-label">已通过</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ claims.filter(c => c.status === 0).length }}</span>
            <span class="stat-label">审核中</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ claims.filter(c => c.status === 2).length }}</span>
            <span class="stat-label">已拒绝</span>
          </div>
        </div>
      </div>
    </div>

    <div class="content-area" v-if="claims.length">
      <div class="tabs-wrapper">
        <div class="tabs">
          <div
            v-for="(tab, index) in tabs"
            :key="tab.key"
            class="tab-item"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
            <span class="tab-count">{{ getTabCount(tab.key) }}</span>
          </div>
          <div class="tab-indicator" :style="indicatorStyle"></div>
        </div>
      </div>

      <div class="claims-list">
        <div
          v-for="claim in filteredClaims"
          :key="claim.id"
          class="claim-card"
          :class="`status-${claim.status}`"
        >
          <div class="claim-icon-wrapper">
            <div class="claim-icon" :class="`icon-${claim.status}`">
              {{ statusIconMap[claim.status] }}
            </div>
          </div>
          <div class="claim-content">
            <div class="claim-header">
              <h4 class="claim-title">{{ claim.itemTitle }}</h4>
              <span class="status-badge" :class="`badge-${claim.status}`">
                {{ statusMap[claim.status] }}
              </span>
            </div>
            <div class="claim-reason" v-if="claim.reason">
              <span class="reason-label">认领理由：</span>
              <span class="reason-text">{{ claim.reason }}</span>
            </div>
            <div class="claim-meta">
              <span class="meta-tag contact">
                <svg viewBox="0 0 24 24" width="14" height="14"><path fill="currentColor" d="M6.62 10.79c1.44 2.83 3.76 5.15 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/></svg>
                {{ claim.contactInfo }}
              </span>
              <span class="meta-tag time">
                <svg viewBox="0 0 24 24" width="14" height="14"><path fill="currentColor" d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/></svg>
                {{ formatTime(claim.createTime) }}
              </span>
            </div>
            <div class="review-remark" v-if="claim.reviewRemark && claim.status !== 0">
              <span class="remark-icon">💬</span>
              <span class="remark-text">{{ claim.reviewRemark }}</span>
            </div>
          </div>
          <div class="claim-actions">
            <button class="action-btn view" @click="$router.push(`/item/detail/${claim.itemId}`)" title="查看物品详情">
              <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-illustration">
        <div class="empty-icon">🔎</div>
      </div>
      <h3 class="empty-title">还没有任何认领记录</h3>
      <p class="empty-desc">发现丢失物品？提交认领申请吧</p>
      <button class="cta-button" @click="$router.push('/')">
        <span>去浏览失物</span>
        <svg viewBox="0 0 24 24" width="18" height="18"><path fill="currentColor" d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z"/></svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { claimApi } from '@/api'

const claims = ref([])
const activeTab = ref('all')
const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
const statusTypeMap = { 0: 'info', 1: 'success', 2: 'danger' }

const tabs = [
  { key: 'all', label: '全部' },
  { key: '1', label: '已通过' },
  { key: '0', label: '审核中' },
  { key: '2', label: '已拒绝' }
]

const statusIconMap = {
  0: '⏳',
  1: '✅',
  2: '❌'
}

const indicatorStyle = computed(() => {
  const index = tabs.findIndex(t => t.key === activeTab.value)
  return {
    transform: `translateX(${index * 100}%)`
  }
})

const filteredClaims = computed(() => {
  if (activeTab.value === 'all') return claims.value
  return claims.value.filter(claim => String(claim.status) === activeTab.value)
})

const getTabCount = (key) => {
  if (key === 'all') return claims.value.length
  return claims.value.filter(claim => String(claim.status) === key).length
}

/**
 * 格式化时间显示
 * @param {string} time - 时间字符串
 * @returns {string} 格式化后的时间
 */
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天申请'
  if (days === 1) return '昨天申请'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const loadClaims = async () => {
  try {
    const res = await claimApi.getMyClaims()
    claims.value = res.data || []
  } catch (e) {
    console.error('加载我的认领失败', e)
  }
}

onMounted(loadClaims)
</script>

<style scoped>
:root {
  --primary-color: #E17055;
  --primary-light: #FAB1A0;
  --primary-dark: #D63031;
  --accent-yellow: #FDCB6E;
  --accent-green: #00B894;
  --accent-purple: #A29BFE;
  --bg-main: #FFF9F5;
  --bg-card: #FFFFFF;
  --text-primary: #2D3436;
  --text-secondary: #636E72;
  --text-muted: #B2BEC3;
  --border-color: #DFE6E9;
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.06);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.12);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
}

.my-claims-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 16px;
  min-height: calc(100vh - 120px);
  background: var(--bg-main);
}

.page-header {
  margin-bottom: 28px;
}

.user-card {
  background: linear-gradient(135deg, #6C5CE7 0%, #A29BFE 100%);
  border-radius: var(--radius-xl);
  padding: 32px;
  color: white;
  display: flex;
  align-items: center;
  gap: 24px;
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.user-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.avatar-wrapper {
  flex-shrink: 0;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  backdrop-filter: blur(10px);
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 6px 0;
  letter-spacing: 0.5px;
}

.user-desc {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--radius-md);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  opacity: 0.85;
  display: block;
  margin-top: 2px;
}

.tabs-wrapper {
  margin-bottom: 20px;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 6px;
  box-shadow: var(--shadow-sm);
}

.tabs {
  display: flex;
  position: relative;
}

.tab-item {
  flex: 1;
  padding: 12px 16px;
  text-align: center;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  border-radius: var(--radius-sm);
}

.tab-item:hover {
  color: var(--accent-purple);
  background: rgba(162, 155, 254, 0.08);
}

.tab-item.active {
  color: var(--accent-purple);
  font-weight: 600;
}

.tab-count {
  display: inline-block;
  margin-left: 6px;
  padding: 2px 8px;
  font-size: 12px;
  background: var(--border-color);
  border-radius: 10px;
  color: var(--text-secondary);
}

.tab-item.active .tab-count {
  background: #E8E4FD;
  color: #6C5CE7;
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 25%;
  height: 3px;
  background: var(--accent-purple);
  border-radius: 3px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.claims-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.claim-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 20px;
  display: flex;
  align-items: flex-start;
  gap: 18px;
  box-shadow: var(--shadow-sm);
  border-left: 4px solid transparent;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.claim-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateX(4px);
}

.claim-card.status-0 {
  border-left-color: #74B9FF;
}

.claim-card.status-1 {
  border-left-color: var(--accent-green);
}

.claim-card.status-2 {
  border-left-color: var(--primary-dark);
}

.claim-icon-wrapper {
  flex-shrink: 0;
}

.claim-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  transition: transform 0.3s ease;
}

.claim-card:hover .claim-icon {
  transform: scale(1.08);
}

.icon-0 {
  background: linear-gradient(135deg, #74B9FF 0%, #0984E3 100%);
}

.icon-1 {
  background: linear-gradient(135deg, #55EFC4 0%, #00B894 100%);
}

.icon-2 {
  background: linear-gradient(135deg, #FF7675 0%, #D63031 100%);
}

.claim-content {
  flex: 1;
  min-width: 0;
}

.claim-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.claim-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  padding: 3px 10px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 20px;
  flex-shrink: 0;
}

.badge-0 {
  background: #E8F4FD;
  color: #0984E3;
}

.badge-1 {
  background: #E8F8F0;
  color: #00B894;
}

.badge-2 {
  background: #FFE8E8;
  color: #D63031;
}

.claim-reason {
  background: var(--bg-main);
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.reason-label {
  color: var(--text-muted);
  font-size: 13px;
  margin-right: 4px;
}

.reason-text {
  color: var(--text-primary);
}

.claim-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--text-secondary);
  padding: 5px 12px;
  background: var(--bg-main);
  border-radius: 6px;
}

.meta-tag svg {
  opacity: 0.7;
}

.review-remark {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 10px 14px;
  background: #FFF8E1;
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--accent-yellow);
}

.remark-icon {
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 1px;
}

.remark-text {
  font-size: 13px;
  color: #B7791F;
  line-height: 1.5;
}

.claim-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
  flex-shrink: 0;
  padding-top: 4px;
}

.claim-card:hover .claim-actions {
  opacity: 1;
  transform: translateX(0);
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  background: var(--bg-main);
  color: var(--text-secondary);
}

.action-btn:hover {
  transform: scale(1.1);
}

.action-btn.view:hover {
  background: #EDE7F6;
  color: #6C5CE7;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.empty-illustration {
  margin-bottom: 24px;
}

.empty-icon {
  font-size: 80px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.empty-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.empty-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0 0 32px 0;
}

.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #6C5CE7 0%, #A29BFE 100%);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(108, 92, 231, 0.35);
}

.cta-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(108, 92, 231, 0.45);
}

.cta-button svg {
  transition: transform 0.3s ease;
}

.cta-button:hover svg {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .my-claims-page {
    padding: 16px 12px;
  }

  .user-card {
    flex-direction: column;
    text-align: center;
    padding: 24px 20px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    width: 100%;
  }

  .claim-card {
    flex-direction: column;
    align-items: stretch;
    padding: 16px;
  }

  .claim-icon-wrapper {
    display: flex;
    justify-content: center;
  }

  .claim-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .claim-actions {
    opacity: 1;
    transform: none;
    justify-content: flex-end;
    margin-top: 12px;
  }

  .tabs-wrapper {
    overflow-x: auto;
  }

  .tab-item {
    padding: 10px 12px;
    font-size: 13px;
    white-space: nowrap;
  }

  .claim-reason {
    padding: 8px 12px;
  }

  .review-remark {
    padding: 8px 12px;
  }
}

@media (max-width: 480px) {
  .avatar {
    width: 56px;
    height: 56px;
    font-size: 26px;
  }

  .user-name {
    font-size: 20px;
  }

  .stat-value {
    font-size: 20px;
  }

  .empty-icon {
    font-size: 64px;
  }

  .empty-title {
    font-size: 18px;
  }

  .claim-icon {
    width: 48px;
    height: 48px;
    font-size: 22px;
  }
}
</style>
