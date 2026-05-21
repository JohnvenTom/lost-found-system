<template>
  <div class="my-items-page">
    <div class="page-header">
      <div class="user-card">
        <div class="avatar-wrapper">
          <div class="avatar">👤</div>
        </div>
        <div class="user-info">
          <h3 class="user-name">我的发布</h3>
          <p class="user-desc">管理你发布的失物招领和寻物启事</p>
        </div>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-value">{{ items.length }}</span>
            <span class="stat-label">全部</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ items.filter(i => i.status === 1).length }}</span>
            <span class="stat-label">已通过</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ items.filter(i => i.status === 0).length }}</span>
            <span class="stat-label">审核中</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ items.filter(i => i.status === 3).length }}</span>
            <span class="stat-label">已认领</span>
          </div>
        </div>
      </div>
    </div>

    <div class="content-area" v-if="items.length">
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

      <div class="items-list">
        <div
          v-for="item in filteredItems"
          :key="item.id"
          class="item-card"
          :class="`status-${item.status}`"
        >
          <div class="item-thumbnail">
            <img v-if="item.images?.[0]" :src="item.images[0]" :alt="item.title" />
            <div v-else class="thumbnail-placeholder">
              {{ item.itemType === 0 ? '🔍' : '📢' }}
            </div>
          </div>
          <div class="item-content">
            <div class="item-header">
              <h4 class="item-title">{{ item.title }}</h4>
              <span class="status-badge" :class="`badge-${item.status}`">
                {{ statusMap[item.status] }}
              </span>
            </div>
            <div class="item-meta">
              <span class="meta-tag category">
                <span class="tag-icon">📁</span> {{ item.categoryName }}
              </span>
              <span class="meta-tag type" :class="item.itemType === 0 ? 'type-lost' : 'type-found'">
                {{ item.itemType === 0 ? '失物招领' : '寻物启事' }}
              </span>
              <span class="meta-tag location">
                <span class="tag-icon">📍</span> {{ item.location }}
              </span>
            </div>
            <p class="item-time">{{ formatTime(item.createTime) }}</p>
          </div>
          <div class="item-actions">
            <button class="action-btn view" @click="$router.push(`/item/detail/${item.id}`)" title="查看详情">
              <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
            </button>
            <button class="action-btn delete" @click="handleDelete(item.id)" title="删除">
              <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-illustration">
        <div class="empty-icon">📦</div>
      </div>
      <h3 class="empty-title">还没有发布任何物品</h3>
      <p class="empty-desc">发布失物招领或寻物启事，让校园更温暖</p>
      <button class="cta-button" @click="$router.push('/item/publish')">
        <span>立即发布</span>
        <svg viewBox="0 0 24 24" width="18" height="18"><path fill="currentColor" d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z"/></svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { itemApi } from '@/api'

const items = ref([])
const activeTab = ref('all')
const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已认领' }
const statusTypeMap = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }

const tabs = [
  { key: 'all', label: '全部' },
  { key: '1', label: '已通过' },
  { key: '0', label: '审核中' },
  { key: '3', label: '已认领' }
]

const indicatorStyle = computed(() => {
  const index = tabs.findIndex(t => t.key === activeTab.value)
  return {
    transform: `translateX(${index * 100}%)`
  }
})

const filteredItems = computed(() => {
  if (activeTab.value === 'all') return items.value
  return items.value.filter(item => String(item.status) === activeTab.value)
})

const getTabCount = (key) => {
  if (key === 'all') return items.value.length
  return items.value.filter(item => String(item.status) === key).length
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
  if (days === 0) return '今天发布'
  if (days === 1) return '昨天发布'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const loadItems = async () => {
  try {
    const res = await itemApi.getMyItems()
    items.value = res.data || []
  } catch (e) {
    console.error('加载我的发布失败', e)
  }
}

/**
 * 删除物品
 * @param {number} id 物品ID
 */
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该物品信息吗？', '提示', { type: 'warning' })
    await itemApi.deleteItem(id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (e) {
    if (e !== 'cancel') console.error('删除失败', e)
  }
}

onMounted(loadItems)
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

.my-items-page {
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
  background: linear-gradient(135deg, var(--primary-color) 0%, #D35400 100%);
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
  color: var(--primary-color);
  background: rgba(225, 112, 85, 0.05);
}

.tab-item.active {
  color: var(--primary-color);
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
  background: var(--primary-light);
  color: var(--primary-dark);
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 25%;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
  border-left: 4px solid transparent;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.item-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateX(4px);
}

.item-card.status-0 {
  border-left-color: #74B9FF;
}

.item-card.status-1 {
  border-left-color: var(--accent-green);
}

.item-card.status-2 {
  border-left-color: var(--primary-dark);
}

.item-card.status-3 {
  border-left-color: var(--accent-yellow);
}

.item-thumbnail {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-main);
}

.item-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  background: linear-gradient(135deg, #FAB1A0 0%, #FFEAA7 100%);
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.item-title {
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

.badge-3 {
  background: #FFF8E1;
  color: #E17055;
}

.item-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 6px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  padding: 4px 10px;
  background: var(--bg-main);
  border-radius: 6px;
}

.tag-icon {
  font-size: 14px;
}

.type {
  font-weight: 500;
}

.type-lost {
  background: #E8F4FD;
  color: #0984E3;
}

.type-found {
  background: #FFF3E0;
  color: #E17055;
}

.item-time {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.item-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.item-card:hover .item-actions {
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
  background: #E8F4FD;
  color: #0984E3;
}

.action-btn.delete:hover {
  background: #FFE8E8;
  color: #D63031;
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
  background: linear-gradient(135deg, var(--primary-color) 0%, #D35400 100%);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(225, 112, 85, 0.35);
}

.cta-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(225, 112, 85, 0.45);
}

.cta-button svg {
  transition: transform 0.3s ease;
}

.cta-button:hover svg {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .my-items-page {
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

  .item-card {
    flex-direction: column;
    align-items: stretch;
    padding: 16px;
  }

  .item-thumbnail {
    width: 100%;
    height: 160px;
  }

  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .item-actions {
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
}
</style>
