<template>
  <div class="item-publish-page">
    <div class="page-header">
      <div class="title-wrapper">
        <el-icon class="title-icon" :size="32"><Bell /></el-icon>
        <h2 class="page-title">发布失物/寻物信息</h2>
      </div>
      <div class="title-underline"></div>
    </div>

    <el-steps :active="currentStep" align-center class="form-steps">
      <el-step title="选择类型" icon="InfoFilled" />
      <el-step title="填写详情" icon="EditPen" />
      <el-step title="上传图片" icon="Picture" />
      <el-step title="确认发布" icon="CircleCheck" />
    </el-steps>

    <div class="form-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large"
               @change="updateStep">

        <section class="form-section" data-step="0">
          <el-form-item label="信息类型" prop="itemType">
            <div class="card-radio-group">
              <div class="card-radio-item" :class="{ active: form.itemType === 0 }"
                   @click="form.itemType = 0">
                <div class="card-radio-icon lost-found">
                  <el-icon :size="36"><Box /></el-icon>
                </div>
                <div class="card-radio-content">
                  <span class="card-radio-title">失物招领</span>
                  <span class="card-radio-desc">我捡到了物品，寻找失主</span>
                </div>
                <div class="card-radio-check" v-if="form.itemType === 0">
                  <el-icon><Select /></el-icon>
                </div>
              </div>
              <div class="card-radio-item" :class="{ active: form.itemType === 1 }"
                   @click="form.itemType = 1">
                <div class="card-radio-icon search">
                  <el-icon :size="36"><Search /></el-icon>
                </div>
                <div class="card-radio-content">
                  <span class="card-radio-title">寻物启事</span>
                  <span class="card-radio-desc">我丢失了物品，寻求帮助</span>
                </div>
                <div class="card-radio-check" v-if="form.itemType === 1">
                  <el-icon><Select /></el-icon>
                </div>
              </div>
            </div>
          </el-form-item>
        </section>

        <section class="form-section" data-step="1">
          <div class="form-row">
            <el-form-item label="物品名称" prop="title" class="flex-1">
              <el-input v-model="form.title" placeholder="请输入物品名称，如：黑色钱包、校园卡等"
                        maxlength="100" show-word-limit clearable>
                <template #prefix>
                  <el-icon><Ticket /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <el-form-item label="物品描述" prop="description">
            <div class="textarea-wrapper">
              <el-input v-model="descriptionText" type="textarea"
                        placeholder="请详细描述物品特征（颜色、品牌、尺寸、特殊标记等），AI将自动为您分类和审核..."
                        :autosize="{ minRows: 4, maxRows: 10 }"
                        maxlength="2000" show-word-limit />
              <div class="char-progress">
                <div class="char-progress-bar">
                  <div class="char-progress-fill" :style="{ width: descriptionProgress + '%' }"></div>
                </div>
                <span class="char-count" :class="{ warning: descriptionProgress > 80 }">
                  {{ descriptionText.length }}/2000
                </span>
              </div>
            </div>
          </el-form-item>

          <div class="form-row two-col">
            <el-form-item label="地点" prop="location" class="flex-1">
              <el-input v-model="form.location" placeholder="请输入拾获/丢失地点" clearable>
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="联系方式" prop="contactInfo" class="flex-1">
              <el-input v-model="form.contactInfo" placeholder="手机号/微信/QQ等" clearable>
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <el-form-item label="发生时间">
            <div class="datetime-picker-wrapper">
              <el-date-picker v-model="form.foundTime" type="datetime"
                              placeholder="选择日期和时间"
                              value-format="YYYY-MM-DD HH:mm:ss"
                              class="custom-datetime-picker" />
              <el-icon class="picker-icon"><Calendar /></el-icon>
            </div>
          </el-form-item>
        </section>

        <section class="form-section" data-step="2">
          <el-form-item label="上传图片（最多5张）">
            <div class="upload-area" :class="{ 'is-dragover': isDragOver }"
                 @dragenter.prevent="isDragOver = true"
                 @dragleave.prevent="isDragOver = false"
                 @drop.prevent="handleDrop"
                 @click="triggerUpload">
              <div class="upload-placeholder" v-if="fileList.length === 0">
                <el-icon class="upload-icon" :size="48"><UploadFilled /></el-icon>
                <p class="upload-text">点击或拖拽图片到此处上传</p>
                <p class="upload-hint">支持 JPG、PNG 格式，单张不超过 5MB</p>
              </div>
              <div class="upload-preview-list" v-else>
                <div class="preview-item" v-for="(file, index) in fileList" :key="index"
                     :class="{ removing: removingIndex === index }">
                  <img :src="file.url || getFileUrl(file)" alt="预览图" class="preview-img" />
                  <div class="preview-mask">
                    <el-icon class="preview-delete" @click.stop="handlePreviewRemove(file, index)"><Delete /></el-icon>
                  </div>
                  <div class="upload-percent" v-if="file.percent !== undefined && file.percent < 100">
                    {{ file.percent }}%
                  </div>
                </div>
                <div class="preview-add" v-if="fileList.length < 5" @click.stop="triggerUpload">
                  <el-icon :size="28"><Plus /></el-icon>
                  <span>添加</span>
                </div>
              </div>
            </div>
            <el-upload ref="uploadRef" :action="uploadUrl" :headers="uploadHeaders"
                      :on-success="handleUploadSuccess" :on-remove="handleRemove"
                      :on-progress="handleUploadProgress" :file-list="fileList"
                      accept="image/*" :limit="5" :auto-upload="true" class="hidden-upload" />
            <div class="upload-progress-list" v-if="hasUploading">
              <div class="progress-item" v-for="(file, idx) in uploadingFiles" :key="'prog-' + idx">
                <span class="progress-name">{{ file.name }}</span>
                <el-progress :percentage="file.percent || 0" :stroke-width="6"
                             color="var(--color-primary)" />
              </div>
            </div>
          </el-form-item>
        </section>

        <section class="form-section" data-step="3">
          <div class="ai-tip-card">
            <div class="ai-tip-header">
              <div class="ai-icon-wrapper">
                <el-icon class="ai-pulse-icon"><MagicStick /></el-icon>
              </div>
              <span class="ai-tip-title">AI 智能服务</span>
            </div>
            <ul class="ai-feature-list">
              <li><el-icon><Check /></el-icon>智能分类 — 无需手动选择类别</li>
              <li><el-icon><Check /></el-icon>内容审核 — 违规内容自动拦截</li>
              <li><el-icon><Check /></el-icon>关键词提取 — 提升搜索匹配度</li>
            </ul>
          </div>

          <div class="submit-actions">
            <el-button type="default" size="large" class="btn-draft" @click="handleSaveDraft">
              <el-icon><Document /></el-icon>
              保存草稿
            </el-button>
            <el-button type="primary" size="large" class="btn-submit" :loading="loading"
                       @click="handleSubmit">
              <el-icon><Promotion /></el-icon>
              立即发布
            </el-button>
          </div>
        </section>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Select,
  Ticket,
  Location,
  Phone,
  Check,
  Document,
  Promotion
} from '@element-plus/icons-vue'
import { itemApi } from '@/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])
const uploadedUrls = ref([])
const uploadRef = ref(null)
const isDragOver = ref(false)
const removingIndex = ref(-1)

const uploadUrl = '/api/upload/file'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const form = reactive({
  title: '',
  description: '',
  images: '',
  location: '',
  contactInfo: '',
  itemType: 0,
  foundTime: ''
})

const rules = {
  title: [{ required: true, message: '请输入物品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入物品描述', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

const descriptionText = computed({
  get: () => form.description,
  set: (val) => { form.description = val }
})

const descriptionProgress = computed(() => (descriptionText.value.length / 2000) * 100)

const currentStep = computed(() => {
  if (form.title || form.description || form.location || form.contactInfo) return Math.max(1, fileList.value.length > 0 ? 2 : 1)
  return 0
})

const hasUploading = computed(() => fileList.value.some(f => f.status === 'uploading'))

const uploadingFiles = computed(() => fileList.value.filter(f => f.status === 'uploading'))

/**
 * 根据表单填写进度更新步骤条
 */
const updateStep = () => {}

/**
 * 触发隐藏的上传组件
 */
const triggerUpload = () => {
  uploadRef.value?.$el.querySelector('input')?.click()
}

/**
 * 处理拖拽文件放置
 * @param {DragEvent} e 拖拽事件
 */
const handleDrop = (e) => {
  isDragOver.value = false
  const files = e.dataTransfer?.files
  if (files && files.length > 0 && fileList.value.length < 5) {
    const input = uploadRef.value?.$el.querySelector('input')
    if (input) {
      const dataTransfer = new DataTransfer()
      Array.from(files).forEach(f => dataTransfer.items.add(f))
      input.files = dataTransfer.files
      input.dispatchEvent(new Event('change'))
    }
  }
}

/**
 * 获取文件预览URL
 * @param {Object} file 文件对象
 * @returns {string} 预览URL
 */
const getFileUrl = (file) => {
  if (file.url) return file.url
  if (file.response?.data) return file.response.data
  return ''
}

/**
 * 处理预览区图片删除（带动画）
 * @param {Object} file 文件对象
 * @param {number} index 索引
 */
const handlePreviewRemove = async (file, index) => {
  removingIndex.value = index
  await new Promise(r => setTimeout(r, 300))
  handleRemove(file)
  removingIndex.value = -1
}

/**
 * 图片上传进度回调
 * @param {Object} event 上传事件
 * @param {Object} file 文件对象
 */
const handleUploadProgress = (event, file) => {
  const idx = fileList.value.findIndex(f => f.uid === file.uid)
  if (idx !== -1) fileList.value[idx].percent = event.percent
}

/**
 * 保存草稿处理
 */
const handleSaveDraft = async () => {
  ElMessage.info('草稿功能开发中...')
}

/**
 * 图片上传成功回调
 * @param {Object} response 上传响应
 */
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    uploadedUrls.value.push(response.data)
    form.images = uploadedUrls.value.join(',')
  }
}

/**
 * 移除图片回调
 * @param {Object} file 被移除的文件
 */
const handleRemove = (file) => {
  const url = file.response?.data
  if (url) {
    uploadedUrls.value = uploadedUrls.value.filter(u => u !== url)
    form.images = uploadedUrls.value.join(',')
  }
}

/**
 * 提交发布信息
 */
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await itemApi.publish(form)
    ElMessage.success('发布成功！AI已自动完成分类和审核')
    router.push('/my/items')
  } catch (e) {
    console.error('发布失败', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:root {
  --color-primary: #e07a5f;
  --color-primary-light: #f2cc8f;
  --color-primary-dark: #c45a3e;
  --color-secondary: #81b29a;
  --color-secondary-light: #a8d5ba;
  --color-accent: #f4a261;
  --color-warm-bg: #fef9f3;
  --color-card-bg: #ffffff;
  --color-text-primary: #3d405b;
  --color-text-secondary: #6d6875;
  --color-text-muted: #9a8fa3;
  --color-border: #e8e0d8;
  --color-border-light: #f0ebe4;
  --color-success: #81b29a;
  --color-warning: #f4a261;
  --color-danger: #e07a5f;
  --shadow-sm: 0 2px 8px rgba(224, 122, 95, 0.08);
  --shadow-md: 0 4px 20px rgba(224, 122, 95, 0.12);
  --shadow-lg: 0 8px 32px rgba(224, 122, 95, 0.16);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 24px;
  --transition-fast: 0.2s ease;
  --transition-normal: 0.3s ease;
  --transition-slow: 0.5s ease;
}

.item-publish-page {
  max-width: 780px;
  margin: 0 auto;
  padding: 24px 20px 60px;
  background: var(--color-warm-bg);
  min-height: 100vh;
}

.page-header {
  text-align: center;
  padding: 40px 0 32px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 8px;
}

.title-icon {
  color: #ff6b6b;
  animation: gentleBounce 2s ease-in-out infinite;
  filter: drop-shadow(0 2px 8px rgba(255, 107, 107, 0.4));
}

@keyframes gentleBounce {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-6px) scale(1.1); }
}

.page-title {
  font-size: clamp(24px, 3vw, 32px);
  font-weight: 800;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 30%, #feca57 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
  line-height: 1.2;
  position: relative;
}

/* 兜底：不支持background-clip的浏览器显示实色 */
@supports not (-webkit-background-clip: text) {
  .page-title {
    color: #ff6b6b;
    background: none;
  }
}

.title-underline {
  width: 140px;
  height: 4px;
  margin: 16px auto 0;
  background: linear-gradient(90deg, #ff6b6b 0%, #feca57 50%, #00d2d3 100%);
  border-radius: var(--radius-full);
  position: relative;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.3);
}

.title-underline::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 8px;
  top: -2px;
  left: 0;
  background: linear-gradient(90deg, #ff6b6b, #feca57, #00d2d3);
  opacity: 0.35;
  filter: blur(6px);
  border-radius: var(--radius-full);
}

.form-steps {
  margin-bottom: 28px;
  padding: 16px 20px;
  background: var(--color-card-bg);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.form-steps :deep(.el-step__head.is-finish) {
  color: var(--color-primary);
  border-color: var(--color-primary);
}

.form-steps :deep(.el-step__title.is-finish) {
  color: var(--color-primary);
}

.form-steps :deep(.el-step__head.is-process) {
  color: var(--color-accent);
  border-color: var(--color-accent);
}

.form-container {
  background: var(--color-card-bg);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 36px 32px 40px;
}

.form-section {
  margin-bottom: 32px;
}

.form-section:last-child {
  margin-bottom: 0;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row.two-col {
  gap: 16px;
}

.flex-1 {
  flex: 1;
}

.card-radio-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  width: 100%;
}

.card-radio-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border: 2px solid #e8e0d8;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  background: #ffffff;
  overflow: hidden;
}

.card-radio-item:hover {
  border-color: #f2cc8f;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(224, 122, 95, 0.15);
}

.card-radio-item.active {
  border: 3px solid #ff6b6b;
  background: linear-gradient(135deg, #fff5f5 0%, #fff9f0 50%, #f0fff4 100%);
  box-shadow:
    0 8px 30px rgba(255, 107, 107, 0.2),
    inset 0 0 20px rgba(255, 107, 107, 0.05);
  transform: scale(1.02) translateY(-2px);
}

.card-radio-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
}

.card-radio-icon::after {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: inherit;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.card-radio-item.active .card-radio-icon::after {
  opacity: 1;
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.08); opacity: 0.8; }
}

.card-radio-icon.lost-found {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  color: #00b894;
  border: 2px solid transparent;
}

.card-radio-icon.search {
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
  color: #e17055;
  border: 2px solid transparent;
}

.card-radio-item.active .card-radio-icon.lost-found {
  background: linear-gradient(135deg, #00b894 0%, #55efc4 100%) !important;
  color: #ffffff !important;
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.45);
  transform: scale(1.1) rotate(-5deg);
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.card-radio-item.active .card-radio-icon.search {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%) !important;
  color: #ffffff !important;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.45);
  transform: scale(1.1) rotate(5deg);
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.card-radio-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.card-radio-item.active .card-radio-title {
  color: #ff6b6b;
  font-weight: 700;
}

.card-radio-item.active .card-radio-desc {
  color: #636e72;
  font-weight: 500;
}

.card-radio-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text-primary);
  transition: all 0.3s ease;
}

.card-radio-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  transition: all 0.3s ease;
}

.card-radio-check {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
  animation: popIn 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 2;
}

@keyframes popIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.textarea-wrapper {
  position: relative;
  width: 100%;
}

.textarea-wrapper :deep(.el-textarea__inner) {
  border-radius: var(--radius-md);
  border: 2px solid var(--color-border);
  transition: all var(--transition-normal);
  line-height: 1.7;
  font-size: 14px;
  padding: 14px 16px;
}

.textarea-wrapper :deep(.el-textarea__inner:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(224, 122, 95, 0.1);
}

.char-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.char-progress-bar {
  flex: 1;
  height: 6px;
  background: var(--color-border-light);
  border-radius: 3px;
  overflow: hidden;
}

.char-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-secondary), var(--color-primary));
  border-radius: 3px;
  transition: width var(--transition-fast);
}

.char-count {
  font-size: 12px;
  color: var(--color-text-muted);
  min-width: 52px;
  text-align: right;
  font-variant-numeric: tabular-nums;
}

.char-count.warning {
  color: var(--color-warning);
  font-weight: 600;
}

.upload-area {
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-lg);
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-normal);
  background: var(--color-warm-bg);
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-area:hover {
  border-color: var(--color-primary-light);
  background: rgba(224, 122, 95, 0.03);
}

.upload-area.is-dragover {
  border-color: var(--color-primary);
  background: rgba(224, 122, 95, 0.06);
  transform: scale(1.01);
  box-shadow: var(--shadow-md);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.upload-icon {
  color: var(--color-primary);
  opacity: 0.6;
  transition: all var(--transition-normal);
}

.upload-area:hover .upload-icon {
  opacity: 1;
  transform: translateY(-4px);
}

.upload-text {
  font-size: 15px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.upload-hint {
  font-size: 12px;
  color: var(--color-text-muted);
}

.upload-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  width: 100%;
}

.preview-item {
  position: relative;
  width: 110px;
  height: 110px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 2px solid var(--color-border-light);
  transition: all var(--transition-normal);
}

.preview-item:hover {
  border-color: var(--color-primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.preview-item.removing {
  animation: shrinkOut 0.35s ease forwards;
}

@keyframes shrinkOut {
  to { transform: scale(0); opacity: 0; }
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-fast);
  border-radius: var(--radius-md);
}

.preview-item:hover .preview-mask {
  opacity: 1;
}

.preview-delete {
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
  background: rgba(224, 122, 95, 0.85);
  transition: all var(--transition-fast);
}

.preview-delete:hover {
  background: var(--color-danger);
  transform: scale(1.1);
}

.upload-percent {
  position: absolute;
  bottom: 4px;
  left: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
  font-size: 11px;
  padding: 2px 0;
  border-radius: 4px;
  text-align: center;
}

.preview-add {
  width: 110px;
  height: 110px;
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  color: var(--color-text-muted);
  transition: all var(--transition-normal);
  font-size: 12px;
}

.preview-add:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(224, 122, 95, 0.04);
}

.hidden-upload {
  display: none;
}

.upload-progress-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: var(--color-warm-bg);
  border-radius: var(--radius-sm);
}

.progress-name {
  font-size: 13px;
  color: var(--color-text-secondary);
  min-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.progress-item :deep(.el-progress) {
  flex: 1;
}

.datetime-picker-wrapper {
  position: relative;
  width: 100%;
}

.custom-datetime-picker {
  width: 100%;
}

.picker-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  pointer-events: none;
  z-index: 1;
}

.datetime-picker-wrapper :deep(.el-input__wrapper) {
  padding-right: 36px;
  border-radius: var(--radius-md);
}

.ai-tip-card {
  background: linear-gradient(135deg, rgba(224, 122, 95, 0.07), rgba(129, 178, 154, 0.07), rgba(244, 162, 97, 0.05));
  border: 1px solid rgba(224, 122, 95, 0.15);
  border-radius: var(--radius-lg);
  padding: 24px 28px;
  margin-bottom: 28px;
}

.ai-tip-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.ai-icon-wrapper {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.ai-pulse-icon {
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.12); opacity: 0.85; }
}

.ai-tip-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.ai-feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ai-feature-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.ai-feature-list li .el-icon {
  color: var(--color-secondary);
  font-size: 16px;
  flex-shrink: 0;
}

.submit-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding-top: 24px;
}

.btn-draft,
.btn-submit {
  min-width: 160px;
  height: 52px !important;
  font-size: 16px !important;
  font-weight: 700 !important;
  border-radius: var(--radius-md) !important;
  transition: all var(--transition-normal) !important;
  letter-spacing: 1px;
  position: relative;
  overflow: hidden;
}

.btn-draft {
  background: #ffffff !important;
  border: 2px solid #e8e0d8 !important;
  color: #6d7087 !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}

.btn-draft:hover {
  border-color: #e07a5f !important;
  color: #e07a5f !important;
  background: #fff5f3 !important;
  transform: translateY(-3px) !important;
  box-shadow: 0 6px 20px rgba(224, 122, 95, 0.25) !important;
}

.btn-submit {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4) !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
}

.btn-submit:hover {
  transform: translateY(-3px) scale(1.02) !important;
  box-shadow: 0 10px 30px rgba(255, 107, 107, 0.5) !important;
  filter: brightness(1.05);
}

.btn-submit:active {
  transform: translateY(-1px) scale(0.98) !important;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 14px;
  padding-bottom: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  box-shadow: 0 0 0 1px var(--color-border) inset;
  transition: all var(--transition-normal);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--color-primary-light) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--color-primary) inset, 0 0 0 3px rgba(224, 122, 95, 0.1);
}

:deep(.el-input__prefix .el-icon) {
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .item-publish-page {
    padding: 16px 12px 40px;
  }

  .form-container {
    padding: 24px 18px 28px;
    border-radius: var(--radius-lg);
  }

  .page-title {
    font-size: 21px;
  }

  .card-radio-group {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .card-radio-item {
    padding: 16px 18px;
  }

  .form-row.two-col {
    flex-direction: column;
    gap: 0;
  }

  .upload-area {
    padding: 20px;
    min-height: 140px;
  }

  .preview-item,
  .preview-add {
    width: 85px;
    height: 85px;
  }

  .submit-actions {
    flex-direction: column;
  }

  .btn-draft,
  .btn-submit {
    width: 100%;
  }

  .form-steps {
    padding: 12px;
  }

  .ai-tip-card {
    padding: 18px 20px;
  }

  .progress-name {
    min-width: 80px;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .title-wrapper {
    flex-direction: column;
    gap: 8px;
  }

  .card-radio-icon {
    width: 46px;
    height: 46px;
  }

  .card-radio-icon :deep(.el-icon) {
    font-size: 26px !important;
  }

  .upload-preview-list {
    gap: 8px;
  }

  .preview-item,
  .preview-add {
    width: 72px;
    height: 72px;
  }
}
</style>
