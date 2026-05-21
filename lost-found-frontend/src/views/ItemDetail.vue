<template>
  <div class="item-detail-page">
    <div v-if="item" class="detail-wrapper">
      <!-- 面包屑导航 + 返回按钮 -->
      <nav class="breadcrumb-bar">
        <el-button class="back-btn" text @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回</span>
        </el-button>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/items' }">失物招领</el-breadcrumb-item>
          <el-breadcrumb-item>{{ item.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </nav>

      <!-- 标题区：大字号 + 类型/状态标签 -->
      <header class="title-section">
        <h1 class="main-title">{{ item.title }}</h1>
        <div class="tag-row">
          <el-tag :type="item.itemType === 0 ? 'success' : 'warning'" size="large" effect="dark" round>
            {{ item.itemType === 0 ? '失物招领' : '寻物启事' }}
          </el-tag>
          <el-tag :type="statusTypeMap[item.status]" size="large" effect="dark" round>
            {{ statusMap[item.status] }}
          </el-tag>
        </div>
      </header>

      <!-- 元信息区：2x2 网格卡片 -->
      <section class="meta-grid">
        <div class="meta-card">
          <div class="meta-icon"><el-icon><User /></el-icon></div>
          <div class="meta-body">
            <span class="meta-label">发布者</span>
            <span class="meta-value">{{ item.username }}</span>
          </div>
        </div>
        <div class="meta-card">
          <div class="meta-icon"><el-icon><Location /></el-icon></div>
          <div class="meta-body">
            <span class="meta-label">地点</span>
            <span class="meta-value">{{ item.location }}</span>
          </div>
        </div>
        <div class="meta-card">
          <div class="meta-icon"><el-icon><Clock /></el-icon></div>
          <div class="meta-body">
            <span class="meta-label">时间</span>
            <span class="meta-value">{{ item.foundTime || item.createTime }}</span>
          </div>
        </div>
        <div class="meta-card">
          <div class="meta-icon"><el-icon><Collection /></el-icon></div>
          <div class="meta-body">
            <span class="meta-label">分类</span>
            <span class="meta-value">{{ item.categoryName || item.aiCategory || '未分类' }}</span>
          </div>
        </div>
      </section>

      <!-- 图片画廊：缩略图导航 + Lightbox + 懒加载 -->
      <section class="gallery-section" v-if="item.images && getImageUrls(item.images).length">
        <h3 class="section-heading">
          <el-icon><PictureFilled /></el-icon> 图片展示
        </h3>
        <div class="gallery-main" @click="openLightbox(0)">
          <el-image
            :src="getImageUrls(item.images)[currentImgIndex]"
            fit="cover"
            class="gallery-main-img"
            loading="lazy"
          />
        </div>
        <div class="gallery-thumbs" v-if="getImageUrls(item.images).length > 1">
          <div
            v-for="(img, idx) in getImageUrls(item.images)"
            :key="idx"
            class="thumb-item"
            :class="{ active: idx === currentImgIndex }"
            @click="switchImage(idx)"
          >
            <el-image :src="img" fit="cover" loading="lazy" />
          </div>
        </div>

        <!-- Lightbox 遮罩 -->
        <Teleport to="body">
          <Transition name="lightbox-fade">
            <div class="lightbox-overlay" v-show="lightboxVisible" @click.self="closeLightbox">
              <button class="lightbox-close" @click="closeLightbox">&times;</button>
              <button class="lightbox-prev" @click.stop="prevImage" v-if="getImageUrls(item.images).length > 1">&#10094;</button>
              <button class="lightbox-next" @click.stop="nextImage" v-if="getImageUrls(item.images).length > 1">&#10095;</button>
              <div class="lightbox-content">
                <el-image
                  :src="getImageUrls(item.images)[lightboxIndex]"
                  fit="contain"
                  class="lightbox-img"
                />
                <div class="lightbox-counter">{{ lightboxIndex + 1 }} / {{ getImageUrls(item.images).length }}</div>
              </div>
            </div>
          </Transition>
        </Teleport>
      </section>

      <!-- 描述区：引用块样式 + 格式化换行 -->
      <section class="desc-block" v-if="item.description">
        <blockquote class="quote-box">
          <p class="quote-text" style="white-space: pre-wrap;">{{ item.description }}</p>
        </blockquote>
      </section>

      <!-- 联系方式区 -->
      <section class="contact-section" v-if="item.contactInfo">
        <h3 class="section-heading">
          <el-icon><Phone /></el-icon> 联系方式
        </h3>
        <p class="contact-text">{{ item.contactInfo }}</p>
      </section>

      <!-- AI信息区：科技感渐变边框 -->
      <section class="ai-panel" v-if="item.aiCategory || item.aiReviewResult">
        <div class="ai-glow-border">
          <div class="ai-inner">
            <div class="ai-header">
              <span class="ai-icon-wrap">
                <el-icon><MagicStick /></el-icon>
              </span>
              <span class="ai-title">AI 智能分析</span>
            </div>
            <div class="ai-body">
              <div class="ai-row" v-if="item.aiCategory">
                <el-tag type="info" effect="dark" round size="small">AI 分类</el-tag>
                <span>{{ item.aiCategory }}</span>
              </div>
              <div class="ai-row" v-if="item.aiReviewResult">
                <el-tag :type="item.aiReviewPass === 1 ? 'success' : 'danger'" effect="dark" round size="small">AI 审核</el-tag>
                <span>{{ item.aiReviewResult }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 操作按钮：固定底部悬浮（移动端）+ 渐变主按钮 -->
      <div class="action-bar" :class="{ sticky: isMobile }" v-if="isLoggedIn && item.userId !== currentUserId && item.status !== 3">
        <button class="claim-btn gradient-btn" @click="showClaimDialog = true">
          <el-icon><Promotion /></el-icon>
          申请认领
        </button>
      </div>
    </div>

    <el-empty v-else description="加载中..." />

    <!-- 留言区 -->
    <section class="message-section" v-if="item">
      <h3 class="section-heading">
        <el-icon><ChatDotRound /></el-icon> 留言互动
        <el-badge :value="messages.length" :max="99" class="msg-badge" />
      </h3>

      <div class="message-form" v-if="isLoggedIn">
        <div class="form-input-wrap">
          <el-input
            v-model="messageForm.content"
            type="textarea"
            :rows="3"
            placeholder="写下你的留言..."
            maxlength="500"
            show-word-limit
          />
        </div>
        <button class="submit-msg-btn gradient-btn-sm" @click="submitMessage">发布留言</button>
      </div>
      <div class="login-tip" v-else>
        <el-empty description="登录后可留言" :image-size="80" />
      </div>

      <div class="message-list">
        <TransitionGroup name="msg-list" tag="div">
          <article v-for="msg in messages" :key="msg.id" class="msg-article">
            <div class="msg-avatar-ring">
              <el-avatar :size="40" :src="msg.userAvatar" class="msg-avatar" />
            </div>
            <div class="msg-bubble-wrap">
              <div class="bubble-meta">
                <span class="bubble-name">{{ msg.username }}</span>
                <span class="bubble-time">{{ msg.createTime }}</span>
              </div>
              <div class="chat-bubble">
                <p>{{ msg.content }}</p>
              </div>
              <div class="bubble-actions" v-if="isLoggedIn">
                <button class="reply-trigger" @click="toggleReplyBox(msg.id)">
                  回复
                </button>
                <Transition name="slide-up">
                  <div class="quick-reply-box" v-if="activeReplyId === msg.id">
                    <el-input
                      v-model="quickReplyText"
                      :placeholder="'回复 @' + msg.username + '...'"
                      size="small"
                      @keyup.enter="quickReply(msg)"
                    />
                    <button class="send-reply-btn" @click="quickReply(msg)">发送</button>
                    <button class="cancel-reply-btn" @click="activeReplyId = null">取消</button>
                  </div>
                </Transition>
              </div>
            </div>
          </article>
        </TransitionGroup>
        <el-empty v-if="!messages.length" description="暂无留言，来说两句吧~" />
      </div>
    </section>

    <!-- 认领对话框：步骤引导 -->
    <el-dialog v-model="showClaimDialog" width="560px" class="claim-dialog" :close-on-click-modal="false">
      <template #header>
        <div class="dialog-header-step">
          <span class="dialog-title-lg">申请认领</span>
          <el-steps :active="claimStep" simple finish-status="success" class="claim-steps">
            <el-step title="填写理由" />
            <el-step title="留下联系方式" />
            <el-step title="提交审核" />
          </el-steps>
        </div>
      </template>

      <div class="step-content">
        <Transition name="step-slide" mode="out-in">
          <!-- 步骤1：填写理由 -->
          <div v-if="claimStep === 0" key="step1" class="step-pane">
            <div class="step-hint">
              <el-icon><InfoFilled /></el-icon>
              请详细描述您与该物品的关联，提供可验证的证明信息将提高通过率。
            </div>
            <el-form ref="claimFormRef" :model="claimForm" :rules="claimRules" label-position="top">
              <el-form-item label="认领理由 / 物品特征描述" prop="reason">
                <el-input
                  v-model="claimForm.reason"
                  type="textarea"
                  :rows="5"
                  placeholder="例如：物品外观特征、丢失时间地点、内部物品细节等..."
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-form>
            <div class="step-nav">
              <button class="gradient-btn next-step-btn" @click="nextClaimStep">下一步</button>
            </div>
          </div>

          <!-- 步骤2：联系方式 -->
          <div v-else-if="claimStep === 1" key="step2" class="step-pane">
            <div class="step-hint">
              <el-icon><InfoFilled /></el-icon>
              您的联系方式仅对发布者可见，请确保信息准确以便及时联系。
            </div>
            <el-form ref="claimFormRef" :model="claimForm" :rules="claimRules" label-position="top">
              <el-form-item label="联系方式（手机/微信/QQ）" prop="contactInfo">
                <el-input
                  v-model="claimForm.contactInfo"
                  placeholder="请输入您的联系方式"
                  prefix-icon="Phone"
                />
              </el-form-item>
            </el-form>
            <div class="step-nav step-nav-two">
              <button class="prev-step-btn" @click="claimStep--">上一步</button>
              <button class="gradient-btn next-step-btn" @click="nextClaimStep">下一步</button>
            </div>
          </div>

          <!-- 步骤3：确认提交 -->
          <div v-else-if="claimStep === 2" key="step3" class="step-pane">
            <div class="confirm-summary">
              <div class="summary-row">
                <span class="summary-label">认领理由</span>
                <p class="summary-val">{{ claimForm.reason || '(未填写)' }}</p>
              </div>
              <div class="summary-row">
                <span class="summary-label">联系方式</span>
                <p class="summary-val">{{ claimForm.contactInfo || '(未填写)' }}</p>
              </div>
            </div>
            <div class="step-nav step-nav-two">
              <button class="prev-step-btn" @click="claimStep--">上一步</button>
              <button class="gradient-btn submit-claim-btn" :loading="claimLoading" @click="submitClaim">
                <el-icon v-if="!claimLoading"><Select /></el-icon>
                确认提交
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  User,
  Location,
  Clock,
  Collection,
  PictureFilled,
  InfoFilled,
  Phone,
  MagicStick,
  Promotion,
  ChatDotRound,
  Select
} from '@element-plus/icons-vue'
import { itemApi, messageApi, claimApi } from '@/api'

const route = useRoute()
const item = ref(null)
const messages = ref([])
const showClaimDialog = ref(false)
const claimLoading = ref(false)
const claimFormRef = ref(null)

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已认领' }
const statusTypeMap = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const currentUserId = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id
})

const messageForm = reactive({ content: '' })
const claimForm = reactive({ reason: '', contactInfo: '' })
const claimRules = {
  reason: [{ required: true, message: '请输入认领理由', trigger: 'blur' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

/**
 * 响应式：当前图片索引
 */
const currentImgIndex = ref(0)

/**
 * 响应式：Lightbox 状态
 */
const lightboxVisible = ref(false)
const lightboxIndex = ref(0)

/**
 * 响应式：移动端检测
 */
const isMobile = ref(false)
const checkMobile = () => { isMobile.value = window.innerWidth < 768 }

/**
 * 响应式：认领步骤
 */
const claimStep = ref(0)

/**
 * 响应式：快捷回复
 */
const activeReplyId = ref(null)
const quickReplyText = ref('')

/**
 * 获取图片URL列表（添加/api前缀）
 * @param {string} images 图片路径（逗号分隔）
 * @returns {string[]} 完整的图片URL列表
 */
const getImageUrls = (images) => {
  if (!images) return []
  return images.split(',').map(url => url.startsWith('/api') ? url : '/api' + url)
}

/**
 * 切换画廊主图
 * @param {number} idx 目标图片索引
 */
const switchImage = (idx) => { currentImgIndex.value = idx }

/**
 * 打开 Lightbox
 * @param {number} idx 起始图片索引
 */
const openLightbox = (idx) => {
  lightboxIndex.value = idx
  lightboxVisible.value = true
  document.body.style.overflow = 'hidden'
}

/**
 * 关闭 Lightbox
 */
const closeLightbox = () => {
  lightboxVisible.value = false
  document.body.style.overflow = ''
}

/**
 * Lightbox 上一张
 */
const prevImage = () => {
  const len = getImageUrls(item.value.images).length
  lightboxIndex.value = (lightboxIndex.value - 1 + len) % len
}

/**
 * Lightbox 下一张
 */
const nextImage = () => {
  const len = getImageUrls(item.value.images).length
  lightboxIndex.value = (lightboxIndex.value + 1) % len
}

/**
 * 认领步骤：下一步校验与切换
 */
const nextClaimStep = async () => {
  if (claimStep.value === 0) {
    const valid = await claimFormRef.value?.validateField('reason').catch(() => false)
    if (!valid && valid !== true) return
  }
  if (claimStep.value === 1) {
    const valid = await claimFormRef.value?.validateField('contactInfo').catch(() => false)
    if (!valid && valid !== true) return
  }
  if (claimStep.value < 2) claimStep.value++
}

/**
 * 切换快捷回复框显示/隐藏
 * @param {number} msgId 留言ID
 */
const toggleReplyBox = (msgId) => {
  activeReplyId.value = activeReplyId.value === msgId ? null : msgId
  quickReplyText.value = ''
}

/**
 * 快捷回复发送
 * @param {Object} msg 目标留言对象
 */
const quickReply = async (msg) => {
  if (!quickReplyText.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await messageApi.add({
      itemId: Number(route.params.id),
      content: `@${msg.username} ${quickReplyText.value}`
    })
    ElMessage.success('回复成功')
    quickReplyText.value = ''
    activeReplyId.value = null
    loadMessages()
  } catch (e) {
    console.error('回复失败', e)
  }
}

/**
 * 加载物品详情
 */
const loadItem = async () => {
  try {
    const res = await itemApi.getDetail(route.params.id)
    item.value = res.data
  } catch (e) {
    console.error('加载物品详情失败', e)
  }
}

/**
 * 加载留言列表
 */
const loadMessages = async () => {
  try {
    const res = await messageApi.getByItem(route.params.id)
    messages.value = res.data || []
  } catch (e) {
    console.error('加载留言失败', e)
  }
}

/**
 * 提交留言
 */
const submitMessage = async () => {
  if (!messageForm.content.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  try {
    await messageApi.add({
      itemId: Number(route.params.id),
      content: messageForm.content
    })
    ElMessage.success('留言发布成功')
    messageForm.content = ''
    loadMessages()
  } catch (e) {
    console.error('留言发布失败', e)
  }
}

/**
 * 回复留言
 * @param {Object} msg 被回复的留言
 */
const replyTo = (msg) => {
  messageForm.content = `@${msg.username} `
}

/**
 * 提交认领申请
 */
const submitClaim = async () => {
  const valid = await claimFormRef.value.validate().catch(() => false)
  if (!valid) return

  claimLoading.value = true
  try {
    await claimApi.submit({
      itemId: Number(route.params.id),
      reason: claimForm.reason,
      contactInfo: claimForm.contactInfo
    })
    ElMessage.success('认领申请提交成功')
    showClaimDialog.value = false
    claimForm.reason = ''
    claimForm.contactInfo = ''
    claimStep.value = 0
  } catch (e) {
    console.error('认领申请失败', e)
  } finally {
    claimLoading.value = false
  }
}

onMounted(() => {
  loadItem()
  loadMessages()
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobile)
  document.body.style.overflow = ''
})
</script>

<style scoped>
/* ========== CSS 变量系统 - 温暖校园配色 ========== */
.item-detail-page {
  --color-primary: #e07a5f;
  --color-primary-light: #f2a68b;
  --color-primary-dark: #c45a3d;
  --color-secondary: #81b29a;
  --color-secondary-light: #a8d4bc;
  --color-accent: #f2cc8f;
  --color-accent-light: #f8e4b8;
  --color-bg: #fef9f3;
  --color-card: #ffffff;
  --color-text: #3d405b;
  --color-text-secondary: #6d7087;
  --color-text-muted: #9a9cb0;
  --color-border: #ede4dc;
  --color-shadow: rgba(224, 122, 95, 0.08);
  --radius-sm: 8px;
  --radius-md: 14px;
  --radius-lg: 20px;
  --radius-xl: 28px;
  --gradient-primary: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  --gradient-warm: linear-gradient(135deg, #f2cc8f, #e07a5f);
  --gradient-ai: linear-gradient(135deg, #667eea, #764ba2);

  max-width: 800px;
  margin: 0 auto;
  padding: 20px 16px 100px;
  background: var(--color-bg);
  min-height: 100vh;
}

/* ========== 面包屑导航 ========== */
.breadcrumb-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  margin-bottom: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--color-primary);
  font-size: 14px;
  font-weight: 500;
  flex-shrink: 0;
}

.back-btn:hover { color: var(--color-primary-dark); }

.breadcrumb-bar :deep(.el-breadcrumb) { font-size: 13px; }
.breadcrumb-bar :deep(.el-breadcrumb__inner) { color: var(--color-text-muted); }
.breadcrumb-bar :deep(.el-breadcrumb__inner.is-link:hover) { color: var(--color-primary); }
.breadcrumb-bar :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* ========== 标题区 ========== */
.title-section {
  margin-bottom: 24px;
}

.main-title {
  font-size: clamp(1.5rem, 4vw, 2rem);
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.35;
  margin-bottom: 14px;
  letter-spacing: -0.02em;
}

.tag-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* ========== 元信息网格卡片 (2x2) ========== */
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 28px;
}

.meta-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 16px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.meta-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px var(--color-shadow);
}

.meta-icon {
  width: 42px;
  height: 42px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--color-accent-light), var(--color-accent));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary-dark);
  font-size: 20px;
  flex-shrink: 0;
}

.meta-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.meta-label {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.meta-value {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ========== 图片画廊 ========== */
.gallery-section { margin-bottom: 32px; }

.section-heading {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-heading .el-icon { color: var(--color-primary); }

.gallery-main {
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: zoom-in;
  margin-bottom: 10px;
  aspect-ratio: 16 / 10;
  background: #f0ece4;
}

.gallery-main-img {
  width: 100%;
  height: 100%;
}

.gallery-thumbs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: thin;
  scrollbar-color: var(--color-border) transparent;
}

.thumb-item {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: pointer;
  border: 2.5px solid transparent;
  transition: all 0.2s ease;
  flex-shrink: 0;
  opacity: 0.65;
}

.thumb-item:hover { opacity: 0.9; }

.thumb-item.active {
  border-color: var(--color-primary);
  opacity: 1;
  box-shadow: 0 0 0 2px rgba(224, 122, 95, 0.2);
}

.thumb-item :deep(.el-image) { width: 100%; height: 100%; }

/* ========== Lightbox ========== */
.lightbox-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(30, 28, 45, 0.92);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.lightbox-close {
  position: absolute;
  top: 20px;
  right: 24px;
  background: none;
  border: none;
  color: #fff;
  font-size: 36px;
  cursor: pointer;
  z-index: 10;
  line-height: 1;
  opacity: 0.75;
  transition: opacity 0.2s;
}
.lightbox-close:hover { opacity: 1; }

.lightbox-prev,
.lightbox-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.12);
  border: none;
  color: #fff;
  font-size: 28px;
  padding: 16px 14px;
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.2s;
  z-index: 10;
}
.lightbox-prev { left: 24px; }
.lightbox-next { right: 24px; }
.lightbox-prev:hover,
.lightbox-next:hover { background: rgba(255,255,255,0.25); }

.lightbox-content {
  max-width: 85vw;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.lightbox-img { max-width: 100%; max-height: 80vh; }

.lightbox-counter {
  color: rgba(255,255,255,0.6);
  font-size: 13px;
  margin-top: 12px;
}

.lightbox-fade-enter-active,
.lightbox-fade-leave-active { transition: opacity 0.3s ease; }
.lightbox-fade-enter-from,
.lightbox-fade-leave-to { opacity: 0; }

/* ========== 描述引用块 ========== */
.desc-block { margin-bottom: 28px; }

.quote-box {
  background: linear-gradient(135deg, #fffaf5, #fef5eb);
  border-left: 4px solid var(--color-primary);
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  padding: 20px 24px;
  margin: 0;
  position: relative;
}

.quote-box::before {
  content: '"';
  position: absolute;
  top: 8px;
  left: 14px;
  font-size: 48px;
  color: var(--color-primary);
  opacity: 0.15;
  font-family: Georgia, serif;
  line-height: 1;
}

.quote-text {
  font-size: 15px;
  color: var(--color-text-secondary);
  line-height: 1.85;
  margin: 0;
  position: relative;
  z-index: 1;
}

/* ========== 联系方式 ========== */
.contact-section { margin-bottom: 28px; }

.contact-text {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.7;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 14px 18px;
}

/* ========== AI 科技感面板 ========== */
.ai-panel { margin-bottom: 28px; }

.ai-glow-border {
  position: relative;
  border-radius: var(--radius-lg);
  padding: 2px;
  background: var(--gradient-ai);
}

.ai-inner {
  background: #faf8ff;
  border-radius: calc(var(--radius-lg) - 2px);
  padding: 20px 24px;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.ai-icon-wrap {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--gradient-ai);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 17px;
}

.ai-title {
  font-size: 16px;
  font-weight: 700;
  background: var(--gradient-ai);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.ai-body { display: flex; flex-direction: column; gap: 10px; }

.ai-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* ========== 操作按钮栏 ========== */
.action-bar {
  text-align: center;
  padding: 20px 0;
}

.action-bar.sticky {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(254, 249, 243, 0.95);
  backdrop-filter: blur(10px);
  padding: 14px 20px;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.06);
  max-width: 800px;
  margin: 0 auto;
}

.gradient-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 13px 36px;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  border: none;
  border-radius: 999px;
  background: var(--gradient-primary);
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(224, 122, 95, 0.35);
  letter-spacing: 0.02em;
}

.gradient-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 26px rgba(224, 122, 95, 0.45);
}

.gradient-btn:active { transform: translateY(0); }

.gradient-btn-sm {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 24px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  border: none;
  border-radius: 999px;
  background: var(--gradient-primary);
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 3px 12px rgba(224, 122, 95, 0.3);
}

.gradient-btn-sm:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(224, 122, 95, 0.4);
}

/* ========== 留言区 ========== */
.message-section {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-top: 8px;
  border: 1px solid var(--color-border);
}

.msg-badge { margin-left: 8px; }

.message-form { margin-bottom: 24px; }

.form-input-wrap { margin-bottom: 10px; }

.login-tip { text-align: center; padding: 10px 0; }

/* 聊天气泡消息列表 */
.message-list { display: flex; flex-direction: column; gap: 18px; }

.msg-article {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.msg-avatar-ring {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  padding: 2.5px;
  background: var(--gradient-warm);
  flex-shrink: 0;
}

.msg-avatar {
  width: 100% !important;
  height: 100% !important;
  border: 2px solid #fff !important;
}

.msg-bubble-wrap { flex: 1; min-width: 0; }

.bubble-meta {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 6px;
}

.bubble-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.bubble-time {
  font-size: 11px;
  color: var(--color-text-muted);
}

.chat-bubble {
  background: #f7f3ee;
  border-radius: 0 var(--radius-md) var(--radius-md) var(--radius-md);
  padding: 12px 16px;
  position: relative;
  display: inline-block;
  max-width: 100%;
}

.chat-bubble::before {
  content: '';
  position: absolute;
  top: 0;
  left: -8px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 10px 10px 0;
  border-color: transparent #f7f3ee transparent transparent;
}

.chat-bubble p {
  margin: 0;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.65;
  word-break: break-word;
}

.bubble-actions { margin-top: 8px; }

.reply-trigger {
  background: none;
  border: none;
  color: var(--color-primary);
  font-size: 12px;
  cursor: pointer;
  padding: 2px 8px;
  border-radius: 999px;
  transition: all 0.2s;
}

.reply-trigger:hover {
  background: rgba(224, 122, 95, 0.08);
}

.quick-reply-box {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  align-items: center;
}

.quick-reply-box :deep(.el-input__wrapper) { border-radius: 999px; }

.send-reply-btn {
  padding: 6px 14px;
  font-size: 12px;
  color: #fff;
  background: var(--gradient-primary);
  border: none;
  border-radius: 999px;
  cursor: pointer;
  white-space: nowrap;
}

.cancel-reply-btn {
  padding: 6px 10px;
  font-size: 12px;
  color: var(--color-text-muted);
  background: none;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  cursor: pointer;
  white-space: nowrap;
}

.slide-up-enter-active,
.slide-up-leave-active { transition: all 0.25s ease; }
.slide-up-enter-from,
.slide-up-leave-to { opacity: 0; transform: translateY(-6px); }

.msg-list-enter-active { transition: all 0.35s ease; }
.msg-list-leave-active { transition: all 0.25s ease; }
.msg-list-enter-from { opacity: 0; transform: translateX(-16px); }
.msg-list-leave-to { opacity: 0; transform: translateX(16px); }

/* ========== 认领步骤对话框 ========== */
.claim-dialog :deep(.el-dialog__header) { padding: 20px 24px 0; }
.claim-dialog :deep(.el-dialog__body) { padding: 20px 24px 24px; }

.dialog-header-step { display: flex; flex-direction: column; gap: 12px; }

.dialog-title-lg {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
}

.claim-steps { background: #faf8ff; border-radius: var(--radius-sm); padding: 4px 8px; }

.step-content { min-height: 260px; position: relative; overflow: hidden; }

.step-pane { animation: fadeIn 0.3s ease; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }

.step-slide-enter-active,
.step-slide-leave-active { transition: all 0.3s ease; }
.step-slide-enter-from { opacity: 0; transform: translateX(30px); }
.step-slide-leave-to { opacity: 0; transform: translateX(-30px); }

.step-hint {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-muted);
  background: #f0f7ff;
  border-radius: var(--radius-sm);
  padding: 12px 14px;
  margin-bottom: 18px;
  line-height: 1.55;
}

.step-hint .el-icon { color: #409eff; flex-shrink: 0; margin-top: 2px; }

.step-nav { display: flex; justify-content: flex-end; margin-top: 24px; }
.step-nav-two { justify-content: space-between; }

.prev-step-btn {
  padding: 9px 22px;
  font-size: 14px;
  color: var(--color-text-secondary);
  background: #f5f3ef;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s;
}
.prev-step-btn:hover { background: #ebe8e2; }

.next-step-btn { padding: 10px 28px; font-size: 14px; }

.submit-claim-btn { padding: 10px 32px; font-size: 15px; }

.confirm-summary {
  background: #fafafa;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 18px 20px;
  margin-bottom: 8px;
}

.summary-row { margin-bottom: 12px; }
.summary-row:last-child { margin-bottom: 0; }

.summary-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  display: block;
  margin-bottom: 4px;
}

.summary-val {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

/* ========== 响应式适配 ========== */
@media (max-width: 768px) {
  .item-detail-page { padding: 12px 12px 90px; }

  .meta-grid { grid-template-columns: 1fr 1fr; gap: 8px; }
  .meta-card { padding: 12px; gap: 10px; }
  .meta-icon { width: 36px; height: 36px; font-size: 17px; }
  .meta-label { font-size: 11px; }
  .meta-value { font-size: 13px; }

  .gallery-thumbs .thumb-item { width: 52px; height: 52px; }

  .message-section { padding: 16px; border-radius: var(--radius-md); }

  .msg-article { gap: 10px; }
  .msg-avatar-ring { width: 38px; height: 38px; }

  .lightbox-prev, .lightbox-next { padding: 12px 10px; font-size: 22px; }
  .lightbox-prev { left: 10px; }
  .lightbox-next { right: 10px; }
}

@media (max-width: 480px) {
  .breadcrumb-bar { flex-direction: column; align-items: flex-start; gap: 8px; }

  .main-title { font-size: 1.35rem; }

  .meta-grid { grid-template-columns: 1fr; }

  .quote-box { padding: 16px 18px; }
  .quote-text { font-size: 14px; }

  .quick-reply-box { flex-direction: column; align-items: stretch; }
  .send-reply-btn, .cancel-reply-btn { text-align: center; }

  .claim-dialog :deep(.el-dialog) { width: 92% !important; margin: 0 auto; }
  .dialog-title-lg { font-size: 17px; }
}
</style>
