<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="header-left">
        <svg class="header-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="3" width="7" height="7" rx="1"/>
          <rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="3" y="14" width="7" height="7" rx="1"/>
          <rect x="14" y="14" width="7" height="7" rx="1"/>
        </svg>
        <h2 class="page-title">管理后台</h2>
      </div>
      <span class="header-subtitle">Lost & Found 管理中心</span>
    </div>

    <div class="stats-grid">
      <div class="stat-card stat-pending-items">
        <div class="stat-icon-wrapper">
          <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
            <path d="M12 11v.01M8 21v-2a4 4 0 014-4h0"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ pendingItems.length }}</span>
          <span class="stat-label">待审核物品</span>
        </div>
        <div class="stat-trend trend-warning">
          <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
            <path d="M12 2L2 22h20L12 2z"/>
          </svg>
        </div>
      </div>

      <div class="stat-card stat-pending-claims">
        <div class="stat-icon-wrapper">
          <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ pendingClaims.length }}</span>
          <span class="stat-label">待审核认领</span>
        </div>
        <div class="stat-trend trend-info">
          <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 6v6l4 2" stroke="#fff" stroke-width="2" fill="none"/>
          </svg>
        </div>
      </div>

      <div class="stat-card stat-total-announcements">
        <div class="stat-icon-wrapper">
          <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V9a2 2 0 00-2-2h-1m0 0L9 3m0 0l-4 4m4-4v12"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ announcements.length }}</span>
          <span class="stat-label">公告总数</span>
        </div>
        <div class="stat-trend trend-success">
          <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
            <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
          </svg>
        </div>
      </div>

      <div class="stat-card stat-categories">
        <div class="stat-icon-wrapper">
          <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 6h16M4 12h16M4 18h16"/>
            <circle cx="6" cy="6" r="1" fill="currentColor"/>
            <circle cx="6" cy="12" r="1" fill="currentColor"/>
            <circle cx="6" cy="18" r="1" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ categories.length }}</span>
          <span class="stat-label">分类数量</span>
        </div>
        <div class="stat-trend trend-secondary">
          <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="tabs-container">
      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane name="items">
          template #label
          <div class="tab-label">
            <svg class="tab-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            <span>物品审核</span>
            <el-badge :value="pendingItems.length" :max="99" :hidden="pendingItems.length === 0" class="tab-badge" />
          </div>
          /template

          <div class="tab-toolbar">
            <div class="batch-actions" v-if="selectedItems.length > 0">
              <span class="selected-info">已选 {{ selectedItems.length }} 项</span>
              <el-button type="success" size="small" @click="batchReviewItems(1)" round>
                批量通过
              </el-button>
              <el-button type="danger" size="small" @click="batchReviewItems(2)" round>
                批量拒绝
              </el-button>
              <el-button size="small" @click="selectedItems = []" round plain>
                取消选择
              </el-button>
            </div>
          </div>

          <el-table
            :data="pendingItems"
            stripe
            row-class-name="table-row-hover"
            @selection-change="handleItemSelection"
            ref="itemsTableRef"
            class="enhanced-table"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="title" label="物品名称" min-width="140">
              <template #default="{ row }">
                <div class="cell-with-icon">
                  <span class="cell-dot"></span>
                  <span class="cell-text">{{ row.title }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="username" label="发布者" width="110">
              <template #default="{ row }">
                <div class="user-cell">
                  <div class="user-avatar">{{ (row.username || '?').charAt(0) }}</div>
                  <span>{{ row.username }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="categoryName" label="分类" width="100">
              <template #default="{ row }">
                <el-tag size="small" round effect="plain">{{ row.categoryName || '未分类' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="aiReviewPass" label="AI审核" width="100">
              <template #default="{ row }">
                <div class="status-cell">
                  <span :class="['status-dot', row.aiReviewPass === 1 ? 'dot-success' : 'dot-danger']"></span>
                  <span :class="['status-text', row.aiReviewPass === 1 ? 'text-success' : 'text-danger']">
                    {{ row.aiReviewPass === 1 ? '通过' : '未通过' }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <div class="status-cell">
                  <span :class="['status-dot', getStatusDotClass(row.status)]"></span>
                  <span :class="['status-text', getTextClass(row.status)]">
                    {{ statusMap[row.status] }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button type="success" size="small" round @click="reviewItem(row.id, 1)">
                    <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="20,6 9,17 4,12"/>
                    </svg>
                    通过
                  </el-button>
                  <el-button type="danger" size="small" round @click="reviewItem(row.id, 2)">
                    <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="18" y1="6" x2="6" y2="18"/>
                      <line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                    拒绝
                  </el-button>
                  <el-button type="primary" size="small" round plain @click="$router.push(`/item/detail/${row.id}`)">
                    详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane name="claims">
          template #label
          <div class="tab-label">
            <svg class="tab-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
            </svg>
            <span>认领审核</span>
            <el-badge :value="pendingClaims.length" :max="99" :hidden="pendingClaims.length === 0" class="tab-badge" />
          </div>
          /template

          <el-table
            :data="pendingClaims"
            stripe
            row-class-name="table-row-hover"
            class="enhanced-table"
          >
            <el-table-column prop="itemTitle" label="物品名称" min-width="140">
              <template #default="{ row }">
                <div class="cell-with-icon">
                  <span class="cell-dot dot-claim"></span>
                  <span class="cell-text">{{ row.itemTitle }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="username" label="认领人" width="110">
              <template #default="{ row }">
                <div class="user-cell">
                  <div class="user-avatar avatar-claim">{{ (row.username || '?').charAt(0) }}</div>
                  <span>{{ row.username }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="认领理由" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="reason-text">{{ row.reason }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="contactInfo" label="联系方式" width="140" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <div class="status-cell">
                  <span :class="['status-dot', getClaimStatusDotClass(row.status)]"></span>
                  <span :class="['status-text', getClaimTextClass(row.status)]">
                    {{ claimStatusMap[row.status] }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button type="success" size="small" round @click="reviewClaim(row.id, 1)">通过</el-button>
                  <el-button type="danger" size="small" round @click="reviewClaim(row.id, 2)">拒绝</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane name="announcements">
          template #label
          <div class="tab-label">
            <svg class="tab-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V9a2 2 0 00-2-2h-1m0 0L9 3m0 0l-4 4m4-4v12"/>
            </svg>
            <span>公告管理</span>
          </div>
          /template

          <div class="tab-header">
            <el-button type="primary" round @click="showAnnDialog = true" class="add-btn">
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
              </svg>
              发布公告
            </el-button>
          </div>

          <el-table
            :data="announcements"
            stripe
            row-class-name="table-row-hover"
            class="enhanced-table"
          >
            <el-table-column prop="title" label="标题" min-width="200">
              <template #default="{ row }">
                <div class="cell-with-icon">
                  <span v-if="row.isTop === 1" class="pin-indicator">📌</span>
                  <span class="cell-text" :class="{ 'text-pinned': row.isTop === 1 }">{{ row.title }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="isTop" label="置顶" width="80">
              <template #default="{ row }">
                <el-tag :type="row.isTop === 1 ? 'danger' : 'info'" size="small" round effect="plain">
                  {{ row.isTop === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <div class="status-cell">
                  <span :class="['status-dot', row.status === 1 ? 'dot-success' : 'dot-info']"></span>
                  <span :class="['status-text', row.status === 1 ? 'text-success' : 'text-info']">
                    {{ row.status === 1 ? '已发布' : '草稿' }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="180">
              <template #default="{ row }">
                <span class="time-cell">{{ row.createTime }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button type="danger" size="small" round plain @click="deleteAnnouncement(row.id)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane name="categories">
          template #label
          <div class="tab-label">
            <svg class="tab-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 6h16M4 12h16M4 18h16"/>
              <circle cx="6" cy="6" r="1" fill="currentColor"/>
              <circle cx="6" cy="12" r="1" fill="currentColor"/>
              <circle cx="6" cy="18" r="1" fill="currentColor"/>
            </svg>
            <span>分类管理</span>
          </div>
          /template

          <div class="tab-header">
            <el-button type="primary" round @click="showCatDialog = true" class="add-btn">
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
              </svg>
              新增分类
            </el-button>
          </div>

          <el-table
            :data="categories"
            stripe
            row-class-name="table-row-hover"
            class="enhanced-table"
          >
            <el-table-column prop="name" label="分类名称" min-width="160">
              <template #default="{ row }">
                <div class="cell-with-icon">
                  <span v-if="row.icon" class="cat-icon">{{ row.icon }}</span>
                  <span class="cell-text">{{ row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="icon" label="图标" width="90">
              <template #default="{ row }">
                <span class="icon-display">{{ row.icon || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" align="center">
              <template #default="{ row }">
                <span class="sort-badge">{{ row.sortOrder ?? 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <div class="status-cell">
                  <span :class="['status-dot', row.status === 1 ? 'dot-success' : 'dot-danger']"></span>
                  <span :class="['status-text', row.status === 1 ? 'text-success' : 'text-danger']">
                    {{ row.status === 1 ? '正常' : '禁用' }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button type="primary" size="small" round plain @click="editCategory(row)">编辑</el-button>
                  <el-button type="danger" size="small" round plain @click="deleteCategory(row.id)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="showAnnDialog" title="发布公告" width="600px" class="custom-dialog" :close-on-click-modal="false">
      <div class="dialog-content">
        <el-form ref="annFormRef" :model="annForm" :rules="annRules" label-position="top" class="animated-form">
          <el-form-item label="公告标题" prop="title" class="form-item-animated">
            <el-input v-model="annForm.title" placeholder="请输入公告标题" :prefix-icon="EditPen" />
          </el-form-item>
          <el-form-item label="公告内容" prop="content" class="form-item-animated">
            <el-input v-model="annForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
          </el-form-item>
          <el-form-item label="是否置顶" class="form-item-animated switch-item">
            <el-switch
              v-model="annForm.isTop"
              :active-value="1"
              :inactive-value="0"
              active-text="置顶"
              inactive-text="普通"
              inline-prompt
              style="--el-switch-on-color: var(--warm-primary);"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAnnDialog = false" round size="large">取消</el-button>
          <el-button type="primary" @click="submitAnnouncement" round size="large" class="submit-btn">
            发布公告
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showCatDialog" :title="catForm.id ? '编辑分类' : '新增分类'" width="480px" class="custom-dialog" :close-on-click-modal="false">
      <div class="dialog-content">
        <el-form ref="catFormRef" :model="catForm" :rules="catRules" label-position="top" class="animated-form">
          <el-form-item label="分类名称" prop="name" class="form-item-animated">
            <el-input v-model="catForm.name" placeholder="请输入分类名称" :prefix-icon="FolderOpened" />
          </el-form-item>
          <el-form-item label="图标标识" class="form-item-animated">
            <el-input v-model="catForm.icon" placeholder="请输入图标名称（如：📱、💻）" />
          </el-form-item>
          <el-form-item label="排序权重" class="form-item-animated">
            <el-input-number v-model="catForm.sortOrder" :min="0" :max="999" controls-position="right" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCatDialog = false" round size="large">取消</el-button>
          <el-button type="primary" @click="submitCategory" round size="large" class="submit-btn">
            {{ catForm.id ? '保存修改' : '确认添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { EditPen, FolderOpened } from '@element-plus/icons-vue'
import { itemApi, claimApi, announcementApi, categoryApi } from '@/api'

const activeTab = ref('items')
const pendingItems = ref([])
const pendingClaims = ref([])
const announcements = ref([])
const categories = ref([])
const showAnnDialog = ref(false)
const showCatDialog = ref(false)
const annFormRef = ref(null)
const catFormRef = ref(null)
const selectedItems = ref([])
const itemsTableRef = ref(null)

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已认领' }
const statusTypeMap = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }
const claimStatusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
const claimStatusTypeMap = { 0: 'info', 1: 'success', 2: 'danger' }

const annForm = reactive({ title: '', content: '', isTop: 0 })
const annRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const catForm = reactive({ id: null, name: '', icon: '', sortOrder: 0 })
const catRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadPendingItems = async () => {
  try {
    const res = await itemApi.getList({ status: 0, pageNum: 1, pageSize: 100 })
    pendingItems.value = res.data?.list || []
  } catch (e) { console.error(e) }
}

const loadPendingClaims = async () => {
  try {
    const res = await itemApi.getList({ status: 1, pageNum: 1, pageSize: 100 })
    const items = res.data?.list || []
    let allClaims = []
    for (const item of items) {
      const claimRes = await claimApi.getByItem(item.id)
      const pending = (claimRes.data || []).filter(c => c.status === 0)
      allClaims = allClaims.concat(pending)
    }
    pendingClaims.value = allClaims
  } catch (e) { console.error(e) }
}

const loadAnnouncements = async () => {
  try {
    const res = await announcementApi.getList({})
    announcements.value = res.data || []
  } catch (e) { console.error(e) }
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getList()
    categories.value = res.data || []
  } catch (e) { console.error(e) }
}

/**
 * 审核物品
 * @param {number} id 物品ID
 * @param {number} status 审核状态
 */
const reviewItem = async (id, status) => {
  try {
    await itemApi.reviewItem(id, status)
    ElMessage.success(status === 1 ? '审核通过' : '已拒绝')
    loadPendingItems()
  } catch (e) { console.error(e) }
}

/**
 * 审核认领申请
 * @param {number} id 认领ID
 * @param {number} status 审核状态
 */
const reviewClaim = async (id, status) => {
  try {
    await claimApi.reviewClaim(id, status, status === 1 ? '审核通过' : '审核拒绝')
    ElMessage.success(status === 1 ? '认领审核通过' : '认领已拒绝')
    loadPendingClaims()
  } catch (e) { console.error(e) }
}

/**
 * 提交公告
 */
const submitAnnouncement = async () => {
  const valid = await annFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await announcementApi.add(annForm)
    ElMessage.success('公告发布成功')
    showAnnDialog.value = false
    annForm.title = ''
    annForm.content = ''
    annForm.isTop = 0
    loadAnnouncements()
  } catch (e) { console.error(e) }
}

/**
 * 删除公告
 * @param {number} id 公告ID
 */
const deleteAnnouncement = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
    await announcementApi.delete(id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

/**
 * 编辑分类
 * @param {Object} cat 分类对象
 */
const editCategory = (cat) => {
  catForm.id = cat.id
  catForm.name = cat.name
  catForm.icon = cat.icon
  catForm.sortOrder = cat.sortOrder
  showCatDialog.value = true
}

/**
 * 提交分类
 */
const submitCategory = async () => {
  const valid = await catFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (catForm.id) {
      await categoryApi.updateCategory(catForm.id, catForm)
    } else {
      await categoryApi.addCategory(catForm)
    }
    ElMessage.success(catForm.id ? '更新成功' : '新增成功')
    showCatDialog.value = false
    catForm.id = null
    catForm.name = ''
    catForm.icon = ''
    catForm.sortOrder = 0
    loadCategories()
  } catch (e) { console.error(e) }
}

/**
 * 删除分类
 * @param {number} id 分类ID
 */
const deleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该分类？', '提示', { type: 'warning' })
    await categoryApi.deleteCategory(id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleItemSelection = (rows) => {
  selectedItems.value = rows
}

const batchReviewItems = async (status) => {
  if (selectedItems.value.length === 0) return
  try {
    for (const item of selectedItems.value) {
      await itemApi.reviewItem(item.id, status)
    }
    ElMessage.success(`批量${status === 1 ? '通过' : '拒绝'}成功，共 ${selectedItems.value.length} 项`)
    selectedItems.value = []
    loadPendingItems()
  } catch (e) {
    console.error(e)
    ElMessage.error('批量操作失败')
  }
}

const getStatusDotClass = (status) => {
  const map = { 0: 'dot-warning', 1: 'dot-success', 2: 'dot-danger', 3: 'dot-info' }
  return map[status] || 'dot-default'
}

const getTextClass = (status) => {
  const map = { 0: 'text-warning', 1: 'text-success', 2: 'text-danger', 3: 'text-info' }
  return map[status] || ''
}

const getClaimStatusDotClass = (status) => {
  const map = { 0: 'dot-warning', 1: 'dot-success', 2: 'dot-danger' }
  return map[status] || 'dot-default'
}

const getClaimTextClass = (status) => {
  const map = { 0: 'text-warning', 1: 'text-success', 2: 'text-danger' }
  return map[status] || ''
}

watch(activeTab, (val) => {
  if (val === 'items') loadPendingItems()
  else if (val === 'claims') loadPendingClaims()
  else if (val === 'announcements') loadAnnouncements()
  else if (val === 'categories') loadCategories()
})

onMounted(() => {
  loadPendingItems()
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
  --warm-warning: #f5a623;
  --warm-danger: #e05555;
  --warm-info: #6b9dc7;
  --warm-radius: 16px;
  --warm-radius-sm: 10px;
  --warm-transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.admin-page {
  max-width: 1200px;
  margin: 0 auto;
  background: var(--warm-bg);
  border-radius: var(--warm-radius);
  padding: 32px;
  box-shadow: 0 4px 24px var(--warm-shadow);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 2px solid var(--warm-border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 36px;
  height: 36px;
  color: var(--warm-primary);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--warm-text);
  margin: 0;
  letter-spacing: -0.5px;
}

.header-subtitle {
  font-size: 14px;
  color: var(--warm-text-secondary);
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: var(--warm-card);
  border-radius: var(--warm-radius-sm);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid var(--warm-border);
  transition: var(--warm-transition);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px var(--warm-shadow-hover);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-pending-items::before { background: linear-gradient(90deg, var(--warm-warning), #ffd700); }
.stat-pending-claims::before { background: linear-gradient(90deg, var(--warm-info), #89c4f4); }
.stat-total-announcements::before { background: linear-gradient(90deg, var(--warm-success), #a8e6a3); }
.stat-categories::before { background: linear-gradient(90deg, var(--warm-secondary), #ffe08a); }

.stat-icon-wrapper {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-pending-items .stat-icon-wrapper { background: linear-gradient(135deg, #fff5e6, #ffe4cc); }
.stat-pending-claims .stat-icon-wrapper { background: linear-gradient(135deg, #e6f2ff, #cce5ff); }
.stat-total-announcements .stat-icon-wrapper { background: linear-gradient(135deg, #e6f7ed, #ccf0db); }
.stat-categories .stat-icon-wrapper { background: linear-gradient(135deg, #fffbeb, #fff0cc); }

.stat-icon {
  width: 26px;
  height: 26px;
}

.stat-pending-items .stat-icon { color: var(--warm-warning); }
.stat-pending-claims .stat-icon { color: var(--warm-info); }
.stat-total-announcements .stat-icon { color: var(--warm-success); }
.stat-categories .stat-icon { color: var(--warm-secondary); }

.stat-content {
  flex: 1;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: var(--warm-text);
  line-height: 1.2;
}

.stat-label {
  display: block;
  font-size: 13px;
  color: var(--warm-text-secondary);
  margin-top: 4px;
  font-weight: 500;
}

.stat-trend {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.trend-warning { background: #fff5e6; color: var(--warm-warning); }
.trend-info { background: #e6f2ff; color: var(--warm-info); }
.trend-success { background: #e6f7ed; color: var(--warm-success); }
.trend-secondary { background: #fffbeb; color: var(--warm-secondary); }

.tabs-container {
  background: var(--warm-card);
  border-radius: var(--warm-radius-sm);
  padding: 24px;
  border: 1px solid var(--warm-border);
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 24px;
  border-bottom: 2px solid var(--warm-border);
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.custom-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  color: var(--warm-text-secondary);
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
  transition: var(--warm-transition);
  position: relative;
}

.custom-tabs :deep(.el-tabs__item:hover) {
  color: var(--warm-primary);
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: var(--warm-primary);
  font-weight: 600;
}

.custom-tabs :deep(.el-tabs__item.is-active::after) {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 24px;
  right: 24px;
  height: 3px;
  background: linear-gradient(90deg, var(--warm-primary), var(--warm-secondary));
  border-radius: 2px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-icon {
  width: 18px;
  height: 18px;
}

.tab-badge {
  margin-left: 4px;
}

.tab-badge :deep(.el-badge__content) {
  background: var(--warm-danger);
  border: none;
  font-weight: 600;
  font-size: 11px;
}

.tab-toolbar {
  margin-bottom: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fffbf7, #fff5ee);
  border-radius: var(--warm-radius-sm);
  border: 1px dashed var(--warm-primary-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-info {
  font-size: 14px;
  font-weight: 600;
  color: var(--warm-primary-dark);
  margin-right: 8px;
}

.tab-header {
  margin-bottom: 16px;
  text-align: right;
}

.add-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.btn-icon {
  width: 14px;
  height: 14px;
}

.enhanced-table {
  border-radius: var(--warm-radius-sm);
  overflow: hidden;
  border: 1px solid var(--warm-border);
}

.enhanced-table :deep(.el-table__header th) {
  background: linear-gradient(135deg, #faf6f3, #fff9f5) !important;
  color: var(--warm-text);
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.3px;
  border-bottom: 2px solid var(--warm-border) !important;
}

.enhanced-table :deep(.el-table__row) {
  transition: var(--warm-transition);
}

.enhanced-table :deep(.el-table__row--striped td) {
  background: #fefaf7 !important;
}

.enhanced-table :deep(.table-row-hover:hover > td) {
  background: #fff5ee !important;
}

.enhanced-table :deep(td) {
  border-bottom: 1px solid #faeee4;
  padding: 14px 0;
}

.cell-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cell-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--warm-primary-light);
  flex-shrink: 0;
}

.cell-dot.dot-claim {
  background: var(--warm-info);
}

.cell-text {
  font-weight: 500;
  color: var(--warm-text);
}

.cell-text.text-pinned {
  color: var(--warm-primary-dark);
  font-weight: 600;
}

.pin-indicator {
  font-size: 14px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--warm-primary-light), var(--warm-secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 12px;
  flex-shrink: 0;
}

.user-avatar.avatar-claim {
  background: linear-gradient(135deg, var(--warm-info), #89c4f4);
}

.status-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

.dot-success { background: var(--warm-success); box-shadow: 0 0 6px rgba(124, 184, 124, 0.4); }
.dot-danger { background: var(--warm-danger); box-shadow: 0 0 6px rgba(224, 85, 85, 0.4); }
.dot-warning { background: var(--warm-warning); box-shadow: 0 0 6px rgba(245, 166, 35, 0.4); }
.dot-info { background: var(--warm-info); box-shadow: 0 0 6px rgba(107, 157, 199, 0.4); }
.dot-default { background: var(--warm-border); }

.status-text {
  font-size: 13px;
  font-weight: 500;
}

.text-success { color: var(--warm-success); }
.text-danger { color: var(--warm-danger); }
.text-warning { color: var(--warm-warning); }
.text-info { color: var(--warm-info); }

.action-buttons {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;

.action-buttons .el-button {
  font-weight: 500;
  transition: var(--warm-transition);
}

.action-buttons .el-button:hover {
  transform: translateY(-1px);
}

.reason-text {
  color: var(--warm-text-secondary);
  font-size: 13px;
}

.time-cell {
  color: var(--warm-text-secondary);
  font-size: 13px;
  font-family: monospace;
}

.cat-icon {
  font-size: 16px;
}

.icon-display {
  font-size: 16px;
  background: var(--warm-bg);
  padding: 4px 10px;
  border-radius: 8px;
  display: inline-block;
}

.sort-badge {
  background: var(--warm-bg);
  color: var(--warm-text-secondary);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  font-family: monospace;
}

.custom-dialog :deep(.el-dialog) {
  border-radius: var(--warm-radius) !important;
  overflow: hidden;
  border: 1px solid var(--warm-border);
  box-shadow: 0 20px 60px rgba(74, 55, 40, 0.15);
}

.custom-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, var(--warm-primary), var(--warm-primary-dark));
  padding: 20px 24px;
  margin: 0;
}

.custom-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.custom-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
}

.custom-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: white;
}

.dialog-content {
  padding: 24px;
}

.animated-form :deep(.el-form-item) {
  margin-bottom: 24px;
  transition: var(--warm-transition);
}

.animated-form :deep(.el-form-item.is-focused) {
  transform: translateX(4px);
}

.animated-form :deep(.el-form-item.is-focused .el-input__wrapper),
.animated-form :deep(.el-form-item.is-focused .el-textarea__inner) {
  box-shadow: 0 0 0 2px var(--warm-primary-light), 0 4px 12px var(--warm-shadow-hover) !important;
  border-color: var(--warm-primary);
}

.animated-form :deep(.el-input__wrapper),
.animated-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  transition: var(--warm-transition);
  border: 1px solid var(--warm-border);
  box-shadow: none;
}

.animated-form :deep(.el-input__wrapper:hover),
.animated-form :deep(.el-textarea__inner:hover) {
  border-color: var(--warm-primary-light);
}

.animated-form :deep(.el-form-item__label) {
  color: var(--warm-text);
  font-weight: 600;
  font-size: 14px;
  padding-bottom: 8px;
}

.switch-item :deep(.el-switch) {
  --el-switch-on-color: var(--warm-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  background: #fafafa;
  border-top: 1px solid var(--warm-border);
}

.submit-btn {
  background: linear-gradient(135deg, var(--warm-primary), var(--warm-primary-dark)) !important;
  border: none !important;
  font-weight: 600;
  padding: 10px 28px;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px var(--warm-shadow-hover);
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .admin-page {
    padding: 24px 20px;
  }
}

@media (max-width: 768px) {
  .admin-page {
    padding: 20px 16px;
    border-radius: var(--warm-radius-sm);
  }

  .page-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .page-title {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .stat-card {
    padding: 18px;
  }

  .stat-value {
    font-size: 24px;
  }

  .tabs-container {
    padding: 16px;
  }

  .custom-tabs :deep(.el-tabs__item) {
    padding: 0 16px;
    font-size: 14px;
  }

  .tab-label {
    gap: 6px;
  }

  .tab-icon {
    width: 16px;
    height: 16px;
  }

  .enhanced-table :deep(.el-table__body-wrapper) {
    overflow-x: auto;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .action-buttons .el-button {
    width: 100%;
  }

  .tab-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .batch-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .admin-page {
    padding: 16px 12px;
  }

  .page-title {
    font-size: 20px;
  }

  .header-icon {
    width: 28px;
    height: 28px;
  }

  .stat-card {
    padding: 14px;
  }

  .stat-icon-wrapper {
    width: 44px;
    height: 44px;
  }

  .stat-icon {
    width: 22px;
    height: 22px;
  }

  .stat-value {
    font-size: 22px;
  }

  .custom-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 10px auto !important;
  }

  .dialog-content {
    padding: 16px;
  }

  .dialog-footer {
    padding: 12px 16px;
  }
}
</style>
