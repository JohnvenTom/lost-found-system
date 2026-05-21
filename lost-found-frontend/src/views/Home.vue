<template>
  <div class="home-page">
    <!-- Hero区域 -->
    <section class="hero-section">
      <div class="hero-bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title animate-fadeInUp">
          校园失物招领平台
        </h1>
        <p class="hero-subtitle animate-fadeInUp delay-1">
          让每一件失物都能找到回家的路
        </p>
        <div class="hero-actions animate-fadeInUp delay-2">
          <button class="btn-primary" @click="$router.push('/item/list')">
            <span>🔍</span>
            浏览失物信息
          </button>
          <button class="btn-secondary" @click="$router.push('/item/publish')">
            <span>✨</span>
            发布失物/寻物
          </button>
        </div>
      </div>
      <div class="scroll-indicator">
        <span></span>
      </div>
    </section>

    <!-- 统计数据区域 -->
    <section class="stats-section">
      <div class="container">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="8">
            <div class="stat-card" @mouseenter="hoveredStat = 'lost'" @mouseleave="hoveredStat = null"
                 :class="{ hovered: hoveredStat === 'lost' }">
              <div class="stat-icon-wrapper lost">
                <el-icon :size="32"><Search /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ animatedStats.lostCount }}</div>
                <div class="stat-label">寻物启事</div>
              </div>
              <div class="stat-bg-icon">🔍</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card" @mouseenter="hoveredStat = 'found'" @mouseleave="hoveredStat = null"
                 :class="{ hovered: hoveredStat === 'found' }">
              <div class="stat-icon-wrapper found">
                <el-icon :size="32"><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ animatedStats.foundCount }}</div>
                <div class="stat-label">失物招领</div>
              </div>
              <div class="stat-bg-icon">📦</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card" @mouseenter="hoveredStat = 'claimed'" @mouseleave="hoveredStat = null"
                 :class="{ hovered: hoveredStat === 'claimed' }">
              <div class="stat-icon-wrapper claimed">
                <el-icon :size="32"><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ animatedStats.claimedCount }}</div>
                <div class="stat-label">成功认领</div>
              </div>
              <div class="stat-bg-icon">✅</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 物品分类区域 -->
    <section class="category-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">📂</span>
            物品分类
          </h2>
          <p class="section-desc">快速找到你需要的物品类型</p>
        </div>
        <el-row :gutter="[20, 20]">
          <el-col :xs="12" :sm="8" :md="6" v-for="(cat, index) in categories" :key="cat.id"
                  :style="{ animationDelay: `${index * 0.1}s` }" class="category-col">
            <div class="category-card" @click="goToCategory(cat.id)">
              <div class="category-icon-wrapper">
                <el-icon :size="36" color="#ff6b6b">
                  <component :is="getIconName(cat.icon)" />
                </el-icon>
              </div>
              <h3 class="category-name">{{ cat.name }}</h3>
              <div class="category-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 最新发布区域 -->
    <section class="recent-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🆕</span>
            最新发布
          </h2>
          <el-button type="primary" round size="small" @click="$router.push('/item/list')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <el-row :gutter="[24, 24]">
          <el-col :xs="24" :sm="12" :md="8" v-for="(item, index) in recentItems" :key="item.id"
                  :style="{ animationDelay: `${index * 0.15}s` }">
            <el-card shadow="hover" class="item-card" @click="$router.push(`/item/detail/${item.id}`)">
              <div class="card-image-wrapper">
                <div v-if="item.images" class="card-image">
                  <img :src="getImageUrl(item.images)" :alt="item.title" loading="lazy" />
                  <div class="image-overlay"></div>
                </div>
                <div v-else class="card-image placeholder">
                  <el-icon :size="48" color="#dfe6e9"><Picture /></el-icon>
                </div>
                <div class="type-badge" :class="item.itemType === 0 ? 'found' : 'lost'">
                  {{ item.itemType === 0 ? '失物招领' : '寻物启事' }}
                </div>
              </div>

              <div class="card-content">
                <h3 class="card-title">{{ item.title }}</h3>
                <p class="card-desc">{{ item.description }}</p>

                <div class="card-tags">
                  <el-tag size="small" effect="light" round>{{ item.categoryName || '未分类' }}</el-tag>
                  <el-tag size="small" :type="item.status === 3 ? 'success' : 'warning'" effect="light" round>
                    {{ statusMap[item.status] }}
                  </el-tag>
                </div>

                <div class="card-meta">
                  <div class="meta-item">
                    <el-icon><Location /></el-icon>
                    <span>{{ item.location }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Clock /></el-icon>
                    <span>{{ formatDate(item.createTime) }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!recentItems.length && !loading" description="暂无发布信息" :image-size="120" />
      </div>
    </section>

    <!-- 公告区域 -->
    <section class="announcement-section" v-if="announcements.length">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">📢</span>
            系统公告
          </h2>
          <el-button text type="primary" @click="$router.push('/announcement')">
            查看全部公告 →
          </el-button>
        </div>

        <div class="announcement-list">
          <div v-for="(ann, index) in announcements" :key="ann.id"
               class="announcement-card"
               :class="{ pinned: ann.isTop === 1 }"
               @click="$router.push('/announcement')"
               :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="ann-indicator" :class="ann.isTop === 1 ? 'top' : 'normal'"></div>
            <div class="ann-content-wrapper">
              <div class="ann-header">
                <el-tag v-if="ann.isTop === 1" type="danger" size="small" effect="dark" round>
                  📌 置顶
                </el-tag>
                <h3 class="ann-title">{{ ann.title }}</h3>
              </div>
              <p class="ann-text">{{ ann.content }}</p>
              <div class="ann-footer">
                <span class="ann-time">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(ann.createTime) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { itemApi, categoryApi, announcementApi } from '@/api'
import {
  Search,
  Box,
  CircleCheck,
  ArrowRight,
  Picture,
  Location,
  Clock,
  FirstAidKit,
  Key,
  Grid,
  Notebook,
  User,
  CoffeeCup
} from '@element-plus/icons-vue'

const router = useRouter()
const categories = ref([])
const recentItems = ref([])
const announcements = ref([])
const stats = reactive({ lostCount: 0, foundCount: 0, claimedCount: 0 })
const animatedStats = reactive({ lostCount: 0, foundCount: 0, claimedCount: 0 })
const hoveredStat = ref(null)
const loading = ref(false)

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已认领' }

/**
 * 图标名称映射表（数据库名称 -> Element Plus 实际图标名）
 */
const iconNameMap = {
  'id-card': FirstAidKit,
  'key': Key,
  'laptop': Grid,
  'book': Notebook,
  'shirt': User,
  'cup': CoffeeCup,
  'gift': Box,
  'box': Box
}

/**
 * 获取实际的图标组件名称
 * @param {string} iconName - 数据库中的图标名称
 * @returns {Object} Element Plus 图标组件
 */
const getIconName = (iconName) => {
  return iconNameMap[iconName] || Box
}

/**
 * 格式化日期显示
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的日期字符串
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

/**
 * 获取物品图片URL
 * @param {string} images - 图片路径（逗号分隔）
 * @returns {string} 第一张图片的完整URL
 */
const getImageUrl = (images) => {
  if (!images) return ''
  const url = images.split(',')[0]
  return url.startsWith('/api') ? url : '/api' + url
}

/**
 * 跳转到分类筛选页面
 * @param {number} categoryId - 分类ID
 */
const goToCategory = (categoryId) => {
  router.push({ path: '/item/list', query: { categoryId } })
}

/**
 * 数字滚动动画效果
 * @param {number} target - 目标数字
 * @param {string} key - 统计数据键名
 */
const animateNumber = (target, key) => {
  const duration = 1500
  const start = 0
  const startTime = performance.now()

  const updateNumber = (currentTime) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const easeProgress = 1 - Math.pow(1 - progress, 3)
    animatedStats[key] = Math.floor(easeProgress * target)

    if (progress < 1) {
      requestAnimationFrame(updateNumber)
    }
  }

  requestAnimationFrame(updateNumber)
}

onMounted(async () => {
  try {
    const [catRes, itemRes, annRes] = await Promise.all([
      categoryApi.getList(),
      itemApi.getList({ pageNum: 1, pageSize: 6 }),
      announcementApi.getList({ status: 1 })
    ])

    categories.value = catRes.data || []
    recentItems.value = itemRes.data?.list || []
    announcements.value = (annRes.data || []).slice(0, 3)

    const items = itemRes.data?.list || []
    stats.lostCount = items.filter(i => i.itemType === 1).length || 0
    stats.foundCount = items.filter(i => i.itemType === 0).length || 0
    stats.claimedCount = items.filter(i => i.status === 3).length || 0

    setTimeout(() => {
      animateNumber(stats.lostCount, 'lostCount')
      animateNumber(stats.foundCount, 'foundCount')
      animateNumber(stats.claimedCount, 'claimedCount')
    }, 300)

  } catch (e) {
    console.error('加载首页数据失败', e)
  }
})
</script>

<style scoped>
.home-page {
  max-width: var(--container-xl);
  margin: 0 auto;
  padding-bottom: var(--space-3xl);
}

/* ========== Hero区域 ========== */
.hero-section {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  padding: calc(var(--space-3xl) + 40px) var(--space-lg);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
  overflow: hidden;
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-3xl);
}

.hero-bg-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: white;
  top: -100px;
  right: -50px;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  background: white;
  bottom: -80px;
  left: -40px;
  animation-delay: 2s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  background: white;
  top: 50%;
  left: 10%;
  animation-delay: 4s;
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  max-width: 700px;
}

.hero-title {
  font-size: clamp(2rem, 5vw, 3.5rem);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-md);
  letter-spacing: 2px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.hero-subtitle {
  font-size: clamp(1rem, 2.5vw, 1.5rem);
  opacity: 0.95;
  margin-bottom: var(--space-xl);
  font-weight: var(--font-weight-light);
  line-height: var(--line-height-relaxed);
}

.hero-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary,
.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 14px 28px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-bold);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: none;
  letter-spacing: 0.5px;
}

.btn-primary {
  background: white;
  color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.35);
}

.btn-primary:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.5);
}

.btn-secondary {
  background: linear-gradient(135deg, #feca57 0%, #ff9f43 100%);
  color: #2d3436;
  border: 2px solid rgba(254, 202, 87, 0.8);
  box-shadow: 0 4px 16px rgba(254, 202, 87, 0.4);
}

.btn-secondary:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(254, 202, 87, 0.6);
  filter: brightness(1.05);
}

.scroll-indicator {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 40px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  display: flex;
  justify-content: center;
  padding-top: 8px;
}

.scroll-indicator span {
  width: 4px;
  height: 8px;
  background: white;
  border-radius: 2px;
  animation: scrollBounce 2s infinite;
}

/* ========== 动画类 ========== */
.animate-fadeInUp {
  animation: fadeInUp 0.8s ease forwards;
  opacity: 0;
}

.delay-1 {
  animation-delay: 0.2s;
}

.delay-2 {
  animation-delay: 0.4s;
}

/* ========== 统计数据区域 ========== */
.stats-section {
  margin-bottom: var(--space-3xl);
  padding: 0 var(--space-md);
}

.stat-card {
  position: relative;
  background: white;
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
  cursor: default;
  overflow: hidden;
  margin-bottom: var(--space-md);
  border: 2px solid transparent;
}

.stat-card:hover,
.stat-card.hovered {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.stat-icon-wrapper {
  width: 70px;
  height: 70px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
}

.stat-icon-wrapper.lost {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  color: white;
}

.stat-icon-wrapper.found {
  background: linear-gradient(135deg, #55efc4 0%, #00b894 100%);
  color: white;
}

.stat-icon-wrapper.claimed {
  background: linear-gradient(135deg, #fdcb6e 0%, #f39c12 100%);
  color: white;
}

.stat-card:hover .stat-icon-wrapper {
  transform: rotate(10deg) scale(1.1);
}

.stat-info {
  flex: 1;
  z-index: 2;
}

.stat-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-xs);
}

.stat-label {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
}

.stat-bg-icon {
  position: absolute;
  right: -10px;
  bottom: -10px;
  font-size: 80px;
  opacity: 0.05;
  pointer-events: none;
}

/* ========== 分类区域 ========== */
.category-section {
  margin-bottom: var(--space-3xl);
  padding: 0 var(--space-md);
}

.section-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.section-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  display: inline-flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-sm);
}

.title-icon {
  font-size: 1.2em;
}

.section-desc {
  font-size: var(--font-size-base);
  color: var(--text-tertiary);
}

.category-col {
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
}

.category-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: var(--space-xl) var(--space-md);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-sm);
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-gradient);
  transform: scaleX(0);
  transition: transform var(--transition-normal);
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.category-card:hover::before {
  transform: scaleX(1);
}

.category-icon-wrapper {
  width: 72px;
  height: 72px;
  margin: 0 auto var(--space-md);
  border-radius: var(--radius-lg);
  background: var(--primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-bounce);
}

.category-card:hover .category-icon-wrapper {
  transform: scale(1.15) rotate(5deg);
  background: var(--primary-gradient);
  color: white;
}

.category-card:hover .category-icon-wrapper .el-icon {
  color: white !important;
}

.category-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}

.category-arrow {
  opacity: 0;
  transform: translateX(-10px);
  transition: all var(--transition-normal);
  color: var(--primary-solid);
}

.category-card:hover .category-arrow {
  opacity: 1;
  transform: translateX(0);
}

/* ========== 最新发布区域 ========== */
.recent-section {
  margin-bottom: var(--space-3xl);
  padding: 0 var(--space-md);
}

.recent-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align: left;
}

.recent-section .el-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.el-col {
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
}

.item-card {
  cursor: pointer;
  transition: all var(--transition-normal);
  height: 100%;
  border: 2px solid transparent;
}

.item-card:hover {
  transform: translateY(-10px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

.card-image-wrapper {
  position: relative;
  margin: calc(var(--space-lg) * -1) calc(var(--space-lg) * -1);
  margin-bottom: var(--space-lg);
  overflow: hidden;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.card-image {
  width: 100%;
  height: 220px;
  overflow: hidden;
  position: relative;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.item-card:hover .card-image img {
  transform: scale(1.08);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 60%, rgba(0, 0, 0, 0.3));
  transition: opacity var(--transition-normal);
}

.item-card:hover .image-overlay {
  opacity: 0.7;
}

.card-image.placeholder {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.type-badge {
  position: absolute;
  top: var(--space-md);
  left: var(--space-md);
  padding: 6px 14px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: white;
  z-index: 2;
  box-shadow: var(--shadow-sm);
}

.type-badge.found {
  background: linear-gradient(135deg, #00b894 0%, #55efc4 100%);
}

.type-badge.lost {
  background: linear-gradient(135deg, #e17055 0%, #fdcb6e 100%);
}

.card-content {
  padding: 0;
}

.card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
  line-height: var(--line-height-tight);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  margin-bottom: var(--space-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: var(--line-height-normal);
}

.card-tags {
  display: flex;
  gap: var(--space-sm);
  margin-bottom: var(--space-md);
  flex-wrap: wrap;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
  padding-top: var(--space-md);
  border-top: 1px solid var(--border-light);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item .el-icon {
  font-size: 14px;
}

/* ========== 公告区域 ========== */
.announcement-section {
  margin-bottom: var(--space-3xl);
  padding: 0 var(--space-md);
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.announcement-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  gap: var(--space-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-sm);
  border-left: 4px solid transparent;
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
}

.announcement-card:hover {
  transform: translateX(8px);
  box-shadow: var(--shadow-md);
  border-left-color: var(--primary-solid);
}

.announcement-card.pinned {
  background: linear-gradient(135deg, #fff5f5 0%, #ffffff 100%);
  border-left-color: var(--danger);
}

.ann-indicator {
  width: 4px;
  border-radius: 2px;
  flex-shrink: 0;
}

.ann-indicator.top {
  background: linear-gradient(180deg, var(--danger), var(--warning));
}

.ann-indicator.normal {
  background: var(--primary-solid);
}

.ann-content-wrapper {
  flex: 1;
}

.ann-header {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-sm);
}

.ann-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.ann-text {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--space-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ann-footer {
  display: flex;
  justify-content: flex-end;
}

.ann-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .hero-section {
    min-height: 350px;
    padding: var(--space-2xl) var(--space-md);
  }

  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-primary,
  .btn-secondary {
    justify-content: center;
  }

  .stat-card {
    padding: var(--space-lg);
  }

  .stat-icon-wrapper {
    width: 56px;
    height: 56px;
  }

  .stat-value {
    font-size: var(--font-size-2xl);
  }

  .card-image {
    height: 180px;
  }

  .recent-section .section-header {
    flex-direction: column;
    gap: var(--space-md);
    text-align: center;
  }
}

@keyframes scrollBounce {
  0%, 100% {
    transform: translateY(0);
    opacity: 1;
  }
  50% {
    transform: translateY(10px);
    opacity: 0.5;
  }
}
</style>
