<template>
  <div class="item-list-page">
    <div class="filter-section">
      <div class="filter-card">
        <div class="filter-card-header">
          <span class="filter-icon">🔍</span>
          <span class="filter-title">筛选条件</span>
        </div>
        <div class="filter-card-body">
          <el-row :gutter="16" align="middle">
            <el-col :xs="24" :sm="12" :md="6">
              <div class="filter-item">
                <label class="filter-label">分类</label>
                <el-select v-model="filters.categoryId" placeholder="选择分类" clearable @change="loadItems"
                           class="custom-select">
                  <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
                </el-select>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="filter-item">
                <label class="filter-label">类型</label>
                <el-select v-model="filters.itemType" placeholder="信息类型" clearable @change="loadItems"
                           class="custom-select">
                  <el-option label="失物招领" :value="0" />
                  <el-option label="寻物启事" :value="1" />
                </el-select>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="filter-item">
                <label class="filter-label">关键词</label>
                <div class="search-input-wrapper">
                  <el-input v-model="filters.keyword" placeholder="搜索物品名称..." clearable
                            prefix-icon="Search" @clear="loadItems" @keyup.enter="loadItems"
                            class="search-input" />
                </div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="filter-item">
                <label class="filter-label">地点</label>
                <div class="search-input-wrapper">
                  <el-input v-model="filters.location" placeholder="搜索地点..." clearable
                            prefix-icon="Location" @clear="loadItems" @keyup.enter="loadItems"
                            class="search-input" />
                </div>
              </div>
            </el-col>
          </el-row>
          <div class="filter-actions">
            <button class="btn-search" @click="loadItems">
              <span>🔍</span> 搜索
            </button>
            <button class="btn-reset" @click="resetFilters">
              <span>🔄</span> 重置
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="list-section">
      <TransitionGroup name="card-reveal" tag="div" class="items-grid">
        <div class="item-card-wrapper" v-for="(item, index) in items" :key="item.id"
             :style="{ '--delay': index * 0.08 + 's' }">
          <div class="item-card" @click="$router.push(`/item/detail/${item.id}`)">
            <div class="item-image-container">
              <img v-if="item.images" :src="getImageUrl(item.images)" :alt="item.title" class="item-img" />
              <div v-else class="item-img-placeholder">
                <span>📦</span>
              </div>
              <div class="image-overlay">
                <span class="overlay-text">查看详情 →</span>
              </div>
              <div class="type-badge" :class="item.itemType === 0 ? 'badge-found' : 'badge-lost'">
                {{ item.itemType === 0 ? '失物招领' : '寻物启事' }}
              </div>
            </div>
            <div class="item-content">
              <h3 class="item-title">{{ item.title }}</h3>
              <p class="item-desc">{{ item.description }}</p>
              <div class="item-tags">
                <span class="tag tag-category">{{ item.categoryName || '未分类' }}</span>
                <span class="tag" :class="item.status === 3 ? 'tag-success' : 'tag-warning'">
                  {{ statusMap[item.status] }}
                </span>
              </div>
              <div class="item-meta">
                <div class="meta-item">
                  <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
                    <circle cx="12" cy="10" r="3"/>
                  </svg>
                  <span>{{ item.location }}</span>
                </div>
                <div class="meta-item">
                  <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12,6 12,12 16,14"/>
                  </svg>
                  <span>{{ formatDate(item.createTime) }}</span>
                </div>
              </div>
              <div class="item-user">
                <div class="user-avatar">
                  <img v-if="item.userAvatar" :src="item.userAvatar" alt="" />
                  <span v-else class="avatar-placeholder">👤</span>
                </div>
                <span class="user-name">{{ item.username }}</span>
              </div>
            </div>
          </div>
        </div>
      </TransitionGroup>

      <div v-if="!items.length && !loading" class="empty-state">
        <div class="empty-illustration">
          <span class="empty-icon">🔍</span>
          <div class="empty-circles">
            <span></span><span></span><span></span>
          </div>
        </div>
        <h3 class="empty-title">暂无失物信息</h3>
        <p class="empty-desc">试试调整筛选条件，或发布一条新的失物/寻物信息吧~</p>
      </div>

      <div v-if="items.length" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="filters.pageNum"
          v-model:page-size="filters.pageSize"
          :total="total"
          :page-sizes="[6, 12, 24]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadItems"
          @current-change="loadItems"
          class="custom-pagination"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { itemApi, categoryApi } from '@/api'

const route = useRoute()
const items = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已认领' }

const filters = reactive({
  categoryId: null,
  itemType: null,
  keyword: '',
  location: '',
  pageNum: 1,
  pageSize: 6
})

/**
 * 获取物品图片URL
 * @param {string} images 图片路径（逗号分隔）
 * @returns {string} 第一张图片的URL
 */
const getImageUrl = (images) => {
  if (!images) return ''
  const url = images.split(',')[0]
  return url.startsWith('/api') ? url : '/api' + url
}

/**
 * 格式化日期
 * @param {string} dateStr 日期字符串
 * @returns {string} 格式化后的日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

/**
 * 初始化筛选条件（从路由query中读取）
 */
const initFiltersFromRoute = () => {
  if (route.query.categoryId) {
    filters.categoryId = Number(route.query.categoryId)
  }
  if (route.query.itemType !== undefined) {
    filters.itemType = Number(route.query.itemType)
  }
  if (route.query.keyword) {
    filters.keyword = route.query.keyword
  }
}

/**
 * 加载物品列表
 */
const loadItems = async () => {
  loading.value = true

  try {
    console.log('📡 请求参数:', filters)
    const res = await itemApi.getList(filters)
    console.log('📦 API响应:', res)
    console.log('📊 res.data:', res.data)

    // 兼容多种数据格式
    const data = res.data
    if (data) {
      // 格式1: { list: [...], total: N }
      if (Array.isArray(data.list)) {
        items.value = data.list
        total.value = data.total || data.list.length
      }
      // 格式2: 直接是数组
      else if (Array.isArray(data)) {
        items.value = data
        total.value = data.length
      }
      // 格式3: { records: [...], total: N }
      else if (Array.isArray(data.records)) {
        items.value = data.records
        total.value = data.total || data.records.length
      }
      // 格式4: 其他情况
      else {
        items.value = []
        total.value = 0
        console.warn('⚠️ 无法识别的数据格式:', data)
      }
    } else {
      items.value = []
      total.value = 0
    }

    console.log(`✅ 加载完成: ${items.value.length} 条记录`)
  } catch (e) {
    console.error('❌ 加载物品列表失败:', e)
    console.error('错误详情:', e.response?.data || e.message)
    items.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

/**
 * 重置筛选条件
 */
const resetFilters = () => {
  filters.categoryId = null
  filters.itemType = null
  filters.keyword = ''
  filters.location = ''
  filters.pageNum = 1
  loadItems()
}

// 监听路由变化，重新加载数据
watch(() => route.query, (newQuery) => {
  if (newQuery.categoryId) {
    filters.categoryId = Number(newQuery.categoryId)
    filters.pageNum = 1
  }
  loadItems()
}, { immediate: false })

onMounted(async () => {
  // 先加载分类列表
  try {
    const catRes = await categoryApi.getList()
    categories.value = catRes.data || []
  } catch (e) {
    console.error('加载分类失败', e)
  }

  // 初始化筛选条件并加载数据
  initFiltersFromRoute()
  loadItems()
})
</script>

<style scoped>
:root {
  --primary-color: #ff6b6b;
  --primary-gradient: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  --secondary-color: #00d2d3;
  --secondary-gradient: linear-gradient(135deg, #00d2d3 0%, #54a0ff 100%);
  --bg-page: #f8f9fe;
  --bg-card: #ffffff;
  --text-primary: #2d3436;
  --text-secondary: #636e72;
  --text-muted: #b2bec3;
  --border-color: #eee;
  --shadow-sm: 0 2px 12px rgba(255, 107, 107, 0.06);
  --shadow-md: 0 4px 20px rgba(255, 107, 107, 0.1);
  --shadow-lg: 0 12px 40px rgba(255, 107, 107, 0.15);
  --radius-sm: 10px;
  --radius-md: 16px;
  --radius-lg: 20px;
  --transition-fast: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-smooth: 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.item-list-page {
  max-width: 1160px;
  margin: 0 auto;
  padding: 24px 16px;
  min-height: calc(100vh - 120px);
  background: var(--bg-page);
  border-radius: var(--radius-lg);
}

.filter-section {
  margin-bottom: 28px;
}

.filter-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  border: 1px solid rgba(255, 107, 107, 0.08);
  transition: box-shadow var(--transition-fast);
}

.filter-card:hover {
  box-shadow: var(--shadow-md);
}

.filter-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 24px;
  background: var(--primary-gradient);
  color: #fff;
}

.filter-icon {
  font-size: 20px;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.filter-card-body {
  padding: 24px;
}

.filter-item {
  margin-bottom: 14px;
}

.filter-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 8px;
  letter-spacing: 0.3px;
}

.search-input-wrapper {
  position: relative;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  box-shadow: 0 0 0 1px var(--border-color) inset;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-color) inset, 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color) inset, 0 0 0 4px rgba(255, 107, 107, 0.15), 0 4px 12px rgba(255, 107, 107, 0.12);
  transform: scale(1.02);
}

.custom-select :deep(.el-select__wrapper) {
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  box-shadow: 0 0 0 1px var(--border-color) inset;
}

.custom-select :deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--secondary-color) inset, 0 0 0 3px rgba(0, 210, 211, 0.1);
}

.custom-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 2px var(--secondary-color) inset, 0 0 0 4px rgba(0, 210, 211, 0.15);
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px dashed var(--border-color);
}

.btn-search, .btn-reset {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 50px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  border: none;
  outline: none;
}

.btn-search {
  background: var(--primary-gradient);
  color: #fff;
  box-shadow: 0 4px 14px rgba(255, 107, 107, 0.35);
}

.btn-search:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 22px rgba(255, 107, 107, 0.45);
}

.btn-search:active {
  transform: translateY(0);
}

.btn-reset {
  background: #f5f6fa;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-reset:hover {
  background: #eef0f7;
  color: var(--text-primary);
  border-color: var(--secondary-color);
  transform: translateY(-2px);
}

.list-section {
  min-height: 400px;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  align-items: start; /* 防止动画期间的对齐跳动 */
}

.item-card-wrapper {
  /* 保持空白，不设置任何默认transform */
}

/* TransitionGroup 动画 - 纯淡入效果，避免位置跳跃 */
.card-reveal-enter-from {
  opacity: 0;
  transform: translateY(20px); /* 只用Y轴位移，不用scale */
}

.card-reveal-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.card-reveal-enter-active {
  transition: opacity 0.4s ease-out, transform 0.4s ease-out;
  transition-delay: var(--delay, 0s);
}

.card-reveal-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.card-reveal-leave-to {
  opacity: 0;
  transform: translateY(-10px); /* 轻微上移，避免大幅位移 */
}

.card-reveal-leave-active {
  transition: opacity 0.25s ease-in, transform 0.25s ease-in;
}

.card-reveal-move {
  transition: transform 0.35s ease;
}

.item-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-smooth);
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0, 0, 0, 0.04);
  position: relative;
}

.item-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
  border-color: rgba(255, 107, 107, 0.15);
}

.item-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #f8f9fe 0%, #eef2f9 100%);
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.item-card:hover .item-img {
  transform: scale(1.08);
}

.item-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  opacity: 0.35;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(255, 107, 107, 0.75) 0%, transparent 60%);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 20px;
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.item-card:hover .image-overlay {
  opacity: 1;
}

.overlay-text {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 1px;
  transform: translateY(10px);
  transition: transform var(--transition-fast);
}

.item-card:hover .overlay-text {
  transform: translateY(0);
}

.type-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 5px 14px;
  border-radius: 50px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  z-index: 2;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.badge-found {
  background: linear-gradient(135deg, #00d2d3 0%, #54a0ff 100%);
}

.badge-lost {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
}

.item-content {
  padding: 18px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-desc {
  font-size: 13px;
  color: var(--text-muted);
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 50px;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.tag-category {
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  color: #00838f;
}

.tag-success {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  color: #2e7d32;
}

.tag-warning {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  color: #e65100;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--text-muted);
}

.meta-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.item-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 14px;
}

.user-name {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-illustration {
  position: relative;
  width: 140px;
  height: 140px;
  margin: 0 auto 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 56px;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-circles {
  position: absolute;
  inset: 0;
}

.empty-circles span {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.empty-circles span:nth-child(1) {
  width: 100%;
  height: 100%;
  background: var(--primary-color);
  animation: pulse 2s ease-in-out infinite;
}

.empty-circles span:nth-child(2) {
  width: 70%;
  height: 70%;
  top: 15%;
  left: 15%;
  background: var(--secondary-color);
  animation: pulse 2s ease-in-out infinite 0.4s;
}

.empty-circles span:nth-child(3) {
  width: 40%;
  height: 40%;
  top: 30%;
  left: 30%;
  background: var(--primary-color);
  animation: pulse 2s ease-in-out infinite 0.8s;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.08; }
  50% { transform: scale(1.05); opacity: 0.12; }
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
  max-width: 320px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 36px;
  padding: 20px 0;
}

.custom-pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.custom-pagination :deep(.btn-prev),
.custom-pagination :deep(.btn-next) {
  border-radius: 50px !important;
  border: 1px solid var(--border-color) !important;
  transition: all var(--transition-fast) !important;
}

.custom-pagination :deep(.btn-prev):hover,
.custom-pagination :deep(.btn-next):hover {
  border-color: var(--primary-color) !important;
  color: var(--primary-color) !important;
  transform: scale(1.05);
}

.custom-pagination :deep(.el-pager li) {
  border-radius: 50% !important;
  min-width: 34px;
  height: 34px;
  transition: all var(--transition-fast) !important;
  font-weight: 500;
}

.custom-pagination :deep(.el-pager li.is-active) {
  background: var(--primary-gradient) !important;
  color: #fff !important;
  box-shadow: 0 3px 10px rgba(255, 107, 107, 0.3);
}

.custom-pagination :deep(.el-pager li:hover:not(.is-active)) {
  color: var(--primary-color) !important;
  transform: scale(1.1);
}

.custom-pagination :deep(.el-select .el-input .el-input__wrapper) {
  border-radius: 50px !important;
}

@media (max-width: 992px) {
  .items-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 18px;
  }
}

@media (max-width: 640px) {
  .item-list-page {
    padding: 16px 12px;
  }

  .filter-card-body {
    padding: 16px;
  }

  .filter-actions {
    flex-direction: column;
  }

  .btn-search, .btn-reset {
    justify-content: center;
  }

  .items-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .item-image-container {
    height: 180px;
  }

  .pagination-wrapper {
    margin-top: 24px;
  }

  .custom-pagination :deep(.el-pagination) {
    gap: 4px;
  }

  .custom-pagination :deep(.el-pagination .el-pagination__sizes) {
    margin-left: 0;
  }

  .custom-pagination :deep(.el-pagination__jump) {
    display: none;
  }
}
</style>
