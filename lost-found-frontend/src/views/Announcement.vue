<template>
  <div class="announcement-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-wrapper">
          <svg class="header-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
            <path d="M2 17l10 5 10-5"/>
            <path d="M2 12l10 5 10-5"/>
          </svg>
          <h2 class="page-title">系统公告</h2>
        </div>
        <span class="announcement-count">{{ announcements.length }} 条公告</span>
      </div>
      <div class="reading-progress">
        <div class="progress-bar" :style="{ width: readingProgress + '%' }"></div>
      </div>
    </div>

    <div class="timeline-container" ref="timelineRef" @scroll="handleScroll">
      <div class="timeline-line"></div>

      <transition-group name="ann-fade" tag="div" class="timeline-list">
        <div
          v-for="(ann, index) in announcements"
          :key="ann.id"
          :class="['timeline-item', { 'is-pinned': ann.isTop === 1, 'is-read': readIds.includes(ann.id) }]"
          @click="markAsRead(ann.id)"
        >
          <div class="timeline-dot-wrapper">
            <div :class="['timeline-dot', { 'dot-pinned': ann.isTop === 1 }]">
              <svg v-if="ann.isTop === 1" class="pin-icon" viewBox="0 0 24 24" fill="currentColor">
                <path d="M16 12V4H17V2H7V4H8V12L6 14V16H11.2V22H12.8V16H18V14L16 12Z"/>
              </svg>
            </div>
            <div v-if="index < announcements.length - 1" class="timeline-connector"></div>
          </div>

          <div class="announcement-card">
            <div class="card-inner">
              <div class="ann-header">
                <div class="header-left">
                  <el-tag v-if="ann.isTop === 1" type="danger" size="small" effect="dark" round class="pinned-tag">
                    <svg class="tag-icon" viewBox="0 0 24 24" fill="currentColor" width="12" height="12">
                      <path d="M16 12V4H17V2H7V4H8V12L6 14V16H11.2V22H12.8V16H18V14L16 12Z"/>
                    </svg>
                    置顶
                  </el-tag>
                  <h3 class="ann-title">{{ ann.title }}</h3>
                </div>
                <span class="ann-time-badge">
                  <svg class="time-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12,6 12,12 16,14"/>
                  </svg>
                  {{ formatTime(ann.createTime) }}
                </span>
              </div>

              <p class="ann-content" :class="{ 'content-collapsed': !expandedId || expandedId !== ann.id }">
                {{ ann.content }}
              </p>
              <button
                v-if="ann.content && ann.content.length > 100"
                class="expand-btn"
                @click.stop="toggleExpand(ann.id)"
              >
                {{ expandedId === ann.id ? '收起' : '展开全文' }}
                <svg :class="['expand-icon', { 'rotated': expandedId === ann.id }]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="6,9 12,15 18,9"/>
                </svg>
              </button>

              <div class="ann-footer">
                <div class="author-info">
                  <div class="avatar-placeholder">
                    {{ (ann.username || '管').charAt(0) }}
                  </div>
                  <span class="ann-author">{{ ann.username || '管理员' }}</span>
                </div>
                <div class="read-status" :class="{ 'is-read-status': readIds.includes(ann.id) }">
                  <div class="status-dot"></div>
                  <span>{{ readIds.includes(ann.id) ? '已读' : '未读' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition-group>

      <div v-if="!announcements.length" class="empty-state">
        <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V9a2 2 0 00-2-2h-1m0 0L9 3m0 0l-4 4m4-4v12"/>
        </svg>
        <p class="empty-text">暂无公告</p>
        <p class="empty-subtext">管理员发布后会在这里显示</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { announcementApi } from '@/api'

const announcements = ref([])
const expandedId = ref(null)
const readIds = ref([])
const timelineRef = ref(null)
const readingProgress = ref(0)

const loadAnnouncements = async () => {
  try {
    const res = await announcementApi.getList({ status: 1 })
    announcements.value = res.data || []
  } catch (e) {
    console.error('加载公告失败', e)
  }
}

const toggleExpand = (id) => {
  expandedId.value = expandedId.value === id ? null : id
}

const markAsRead = (id) => {
  if (!readIds.value.includes(id)) {
    readIds.value.push(id)
    localStorage.setItem('readAnnouncements', JSON.stringify(readIds.value))
  }
}

const handleScroll = () => {
  if (!timelineRef.value) return
  const { scrollTop, scrollHeight, clientHeight } = timelineRef.value
  const progress = (scrollTop / (scrollHeight - clientHeight)) * 100
  readingProgress.value = Math.min(100, Math.max(0, progress))
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return timeStr.split(' ')[0]
}

onMounted(() => {
  loadAnnouncements()
  const saved = localStorage.getItem('readAnnouncements')
  if (saved) {
    readIds.value = JSON.parse(saved)
  }
})
</script>

<style scoped>
:root {
  --warm-primary: #e07850;
  --warm-primary-light: #f4a98b;
  --warm-primary-dark: #c45d35;
  --warm-secondary: #f5c97b;
  --warm-bg: #fff9f5;
  --warm-card: #ffffff;
  --warm-text: #4a3728;
  --warm-text-secondary: #8b7355;
  --warm-border: #f0ddd0;
  --warm-shadow: rgba(224, 120, 80, 0.08);
  --warm-shadow-hover: rgba(224, 120, 80, 0.15);
  --warm-success: #7cb87c;
  --warm-radius: 16px;
  --warm-radius-sm: 10px;
  --warm-transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.announcement-page {
  max-width: 800px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
  background: var(--warm-bg);
  border-radius: var(--warm-radius);
  padding: 32px;
  box-shadow: 0 4px 24px var(--warm-shadow);
}

.page-header {
  margin-bottom: 40px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 32px;
  height: 32px;
  color: var(--warm-primary);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--warm-text);
  margin: 0;
  letter-spacing: -0.5px;
}

.announcement-count {
  font-size: 14px;
  color: var(--warm-text-secondary);
  background: var(--warm-card);
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid var(--warm-border);
  font-weight: 500;
}

.reading-progress {
  height: 4px;
  background: var(--warm-border);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--warm-primary), var(--warm-secondary));
  border-radius: 2px;
  transition: width 0.3s ease;
}

.timeline-container {
  position: relative;
  max-height: calc(100vh - 300px);
  overflow-y: auto;
  padding-right: 8px;
  scrollbar-width: thin;
  scrollbar-color: var(--warm-primary-light) transparent;
}

.timeline-container::-webkit-scrollbar {
  width: 6px;
}

.timeline-container::-webkit-scrollbar-track {
  background: transparent;
}

.timeline-container::-webkit-scrollbar-thumb {
  background: var(--warm-primary-light);
  border-radius: 3px;
}

.timeline-line {
  position: absolute;
  left: 19px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, var(--warm-primary), var(--warm-secondary), transparent);
  opacity: 0.3;
}

.timeline-list {
  position: relative;
}

.timeline-item {
  display: flex;
  gap: 24px;
  margin-bottom: 28px;
  position: relative;
  transition: var(--warm-transition);
}

.timeline-item:hover {
  transform: translateX(4px);
}

.timeline-item.is-pinned {
  margin-bottom: 36px;
}

.timeline-dot-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  padding-top: 8px;
}

.timeline-dot {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--warm-card);
  border: 3px solid var(--warm-border);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--warm-transition);
  z-index: 2;
  position: relative;
}

.timeline-item:hover .timeline-dot {
  border-color: var(--warm-primary);
  transform: scale(1.1);
  box-shadow: 0 0 0 4px var(--warm-shadow);
}

.timeline-dot.dot-pinned {
  background: linear-gradient(135deg, var(--warm-primary), var(--warm-primary-dark));
  border-color: var(--warm-primary-dark);
  animation: pulse-pin 2s infinite;
}

.pin-icon {
  width: 18px;
  height: 18px;
  color: white;
}

@keyframes pulse-pin {
  0%, 100% { box-shadow: 0 0 0 0 rgba(224, 120, 80, 0.4); }
  50% { box-shadow: 0 0 0 8px rgba(224, 120, 80, 0); }
}

.timeline-connector {
  width: 2px;
  flex: 1;
  background: linear-gradient(to bottom, var(--warm-border), transparent);
  margin-top: 4px;
  min-height: 20px;
}

.announcement-card {
  flex: 1;
  background: var(--warm-card);
  border-radius: var(--warm-radius-sm);
  border: 1px solid var(--warm-border);
  transition: var(--warm-transition);
  overflow: hidden;
  cursor: pointer;
}

.announcement-card:hover {
  border-color: var(--warm-primary-light);
  box-shadow: 0 8px 24px var(--warm-shadow-hover);
  transform: translateY(-2px);
}

.timeline-item.is-pinned .announcement-card {
  border: 2px solid var(--warm-primary);
  background: linear-gradient(135deg, #fff 0%, #fffbf7 100%);
  box-shadow: 0 8px 32px var(--warm-shadow-hover);
  position: relative;
}

.timeline-item.is-pinned .announcement-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--warm-primary), var(--warm-secondary));
}

.card-inner {
  padding: 24px;
}

.ann-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  flex-wrap: wrap;
}

.pinned-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.tag-icon {
  flex-shrink: 0;
}

.ann-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--warm-text);
  margin: 0;
  line-height: 1.4;
  transition: var(--warm-transition);
}

.timeline-item.is-pinned .ann-title {
  color: var(--warm-primary-dark);
  font-size: 19px;
}

.announcement-card:hover .ann-title {
  color: var(--warm-primary);
}

.ann-time-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--warm-text-secondary);
  white-space: nowrap;
  padding: 4px 10px;
  background: var(--warm-bg);
  border-radius: 12px;
  font-weight: 500;
}

.time-icon {
  width: 14px;
  height: 14px;
}

.ann-content {
  font-size: 15px;
  color: var(--warm-text-secondary);
  line-height: 1.8;
  margin: 0 0 16px 0;
  transition: var(--warm-transition);
}

.ann-content.content-collapsed {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.expand-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  color: var(--warm-primary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  padding: 4px 0;
  margin-bottom: 12px;
  transition: var(--warm-transition);
}

.expand-btn:hover {
  color: var(--warm-primary-dark);
}

.expand-icon {
  width: 16px;
  height: 16px;
  transition: transform 0.3s ease;
}

.expand-icon.rotated {
  transform: rotate(180deg);
}

.ann-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px dashed var(--warm-border);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--warm-primary-light), var(--warm-secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.ann-author {
  font-size: 14px;
  color: var(--warm-text);
  font-weight: 500;
}

.read-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--warm-text-secondary);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--warm-border);
  transition: var(--warm-transition);
}

.read-status.is-read-status .status-dot {
  background: var(--warm-success);
  box-shadow: 0 0 6px rgba(124, 184, 124, 0.4);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: var(--warm-border);
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: var(--warm-text);
  margin: 0 0 8px 0;
  font-weight: 500;
}

.empty-subtext {
  font-size: 14px;
  color: var(--warm-text-secondary);
  margin: 0;
}

.ann-fade-enter-active,
.ann-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.ann-fade-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.ann-fade-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

@media (max-width: 768px) {
  .announcement-page {
    padding: 20px 16px;
    border-radius: var(--warm-radius-sm);
  }

  .page-title {
    font-size: 24px;
  }

  .header-icon {
    width: 28px;
    height: 28px;
  }

  .announcement-count {
    display: none;
  }

  .timeline-item {
    gap: 16px;
    margin-bottom: 24px;
  }

  .timeline-dot {
    width: 36px;
    height: 36px;
  }

  .timeline-line,
  .timeline-connector {
    left: 17px;
  }

  .card-inner {
    padding: 18px;
  }

  .ann-title {
    font-size: 16px;
  }

  .ann-content {
    font-size: 14px;
  }

  .ann-header {
    flex-direction: column;
    gap: 8px;
  }

  .ann-time-badge {
    align-self: flex-start;
  }
}

@media (max-width: 480px) {
  .announcement-page {
    padding: 16px 12px;
  }

  .page-title {
    font-size: 20px;
  }

  .card-inner {
    padding: 14px;
  }

  .ann-title {
    font-size: 15px;
  }

  .timeline-dot {
    width: 32px;
    height: 32px;
  }

  .timeline-line,
  .timeline-connector {
    left: 15px;
  }
}
</style>
