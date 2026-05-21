<template>
  <el-container class="main-layout">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="header-inner">
        <!-- Logo区域 -->
        <div class="header-left">
          <div class="logo" @click="$router.push('/home')">
            <span class="logo-icon">🔍</span>
            <span class="logo-text">校园失物招领</span>
          </div>
        </div>

        <!-- 主导航菜单 -->
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="header-menu"
          router
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/item/list">
            <el-icon><List /></el-icon>
            <span>失物列表</span>
          </el-menu-item>
          <el-menu-item index="/announcement">
            <el-icon><Bell /></el-icon>
            <span>系统公告</span>
          </el-menu-item>
          <el-menu-item v-if="isLoggedIn" index="/item/publish">
            <el-icon><Plus /></el-icon>
            <span>发布信息</span>
          </el-menu-item>
        </el-menu>

        <!-- 右侧用户区域 -->
        <div class="header-right">
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-info">
                <div class="avatar-wrapper">
                  <el-avatar :size="36" :src="user.avatar" class="user-avatar">
                    {{ (user.nickname || user.username).charAt(0).toUpperCase() }}
                  </el-avatar>
                  <span class="online-indicator"></span>
                </div>
                <span class="username">{{ user.nickname || user.username }}</span>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <div class="dropdown-header">
                    <el-avatar :size="48" :src="user.avatar" style="margin-bottom: 8px;">
                      {{ (user.nickname || user.username).charAt(0).toUpperCase() }}
                    </el-avatar>
                    <div class="dropdown-user-name">{{ user.nickname || user.username }}</div>
                    <div class="dropdown-user-role">{{ isAdmin ? '管理员' : '普通用户' }}</div>
                  </div>
                  <el-dropdown-item command="myItems">
                    <el-icon><Document /></el-icon>
                    我的发布
                  </el-dropdown-item>
                  <el-dropdown-item command="myClaims">
                    <el-icon><Finished /></el-icon>
                    我的认领
                  </el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" command="admin" divided>
                    <el-icon><Setting /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" round @click="$router.push('/login')" class="login-btn">
              登录
            </el-button>
            <el-button round @click="$router.push('/register')" class="register-btn">
              注册
            </el-button>
          </template>

          <!-- 移动端菜单按钮 -->
          <button class="mobile-menu-btn" @click="toggleMobileMenu">
            <span class="hamburger" :class="{ active: showMobileMenu }"></span>
          </button>
        </div>
      </div>

      <!-- 移动端导航菜单 -->
      <transition name="slide-down">
        <div v-if="showMobileMenu" class="mobile-menu">
          <div class="mobile-menu-item" @click="navigateTo('/home')">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </div>
          <div class="mobile-menu-item" @click="navigateTo('/item/list')">
            <el-icon><List /></el-icon>
            <span>失物列表</span>
          </div>
          <div class="mobile-menu-item" @click="navigateTo('/announcement')">
            <el-icon><Bell /></el-icon>
            <span>系统公告</span>
          </div>
          <div v-if="isLoggedIn" class="mobile-menu-item" @click="navigateTo('/item/publish')">
            <el-icon><Plus /></el-icon>
            <span>发布信息</span>
          </div>
          <div v-if="!isLoggedIn" class="mobile-menu-actions">
            <el-button type="primary" round @click="navigateTo('/login')">登录</el-button>
            <el-button round @click="navigateTo('/register')">注册</el-button>
          </div>
        </div>
      </transition>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-inner">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="footer-logo">🔍 校园失物招领</span>
            <p class="footer-slogan">让每一件失物都能找到回家的路</p>
          </div>
          <div class="footer-links">
            <a href="#" class="footer-link">关于我们</a>
            <a href="#" class="footer-link">使用帮助</a>
            <a href="#" class="footer-link">联系方式</a>
            <a href="#" class="footer-link">隐私政策</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 校园失物招领系统 | 用心服务每一位同学</p>
        </div>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  List,
  Bell,
  Plus,
  ArrowDown,
  Document,
  Finished,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const showMobileMenu = ref(false)

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))
const isAdmin = computed(() => user.value.role === 1)
const activeMenu = computed(() => route.path)

/**
 * 处理下拉菜单命令
 * @param {string} command - 菜单命令标识符
 */
const handleCommand = (command) => {
  const commandMap = {
    myItems: '/my/items',
    myClaims: '/my/claims',
    admin: '/admin',
    logout: null
  }

  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
    return
  }

  if (commandMap[command]) {
    router.push(commandMap[command])
  }
}

/**
 * 切换移动端菜单显示状态
 */
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

/**
 * 移动端导航跳转并关闭菜单
 * @param {string} path - 目标路由路径
 */
const navigateTo = (path) => {
  router.push(path)
  showMobileMenu.value = false
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
}

/* ========== 导航栏样式 ========== */
.header {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(223, 230, 233, 0.5);
  padding: 0;
  height: auto !important;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  transition: all var(--transition-normal);
}

.header:hover {
  border-bottom-color: rgba(255, 107, 107, 0.2);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.06);
}

.header-inner {
  max-width: var(--container-xl);
  margin: 0 auto;
  padding: 0 var(--space-lg);
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo样式 */
.logo {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
}

.logo:hover {
  transform: scale(1.05);
  background: var(--primary-light);
}

.logo-icon {
  font-size: 28px;
  animation: float 3s ease-in-out infinite;
  display: inline-block;
}

.logo-text {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
}

/* 菜单样式 */
.header-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none !important;
  max-width: 600px;
  margin: 0 var(--space-xl);
}

.header-menu .el-menu-item {
  height: 70px;
  line-height: 70px;
  padding: 0 var(--space-lg);
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  position: relative;
}

.header-menu .el-menu-item .el-icon {
  margin-right: 6px;
  vertical-align: middle;
}

/* 用户区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-full);
  transition: all var(--transition-normal);
}

.user-info:hover {
  background: var(--bg-tertiary);
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  transition: all var(--transition-normal);
  background: var(--primary-gradient);
  color: white;
  font-weight: var(--font-weight-bold);
}

.user-info:hover .user-avatar {
  transform: scale(1.1);
  box-shadow: var(--shadow-glow);
}

.online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background: var(--success);
  border: 2px solid white;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.username {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  color: var(--text-tertiary);
  font-size: 12px;
  transition: transform var(--transition-fast);
}

.login-btn,
.register-btn {
  font-weight: var(--font-weight-semibold);
  padding: 10px 20px;
  border-radius: var(--radius-full) !important;
}

.register-btn {
  border: 2px solid var(--primary-solid) !important;
  color: var(--primary-solid) !important;
  background: transparent !important;
}

.register-btn:hover {
  background: var(--primary-light) !important;
  transform: translateY(-2px);
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--bg-tertiary);
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.mobile-menu-btn:hover {
  background: var(--border-color);
}

.hamburger {
  width: 20px;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  position: relative;
  transition: all var(--transition-normal);
}

.hamburger::before,
.hamburger::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  transition: all var(--transition-normal);
}

.hamburger::before {
  top: -6px;
}

.hamburger::after {
  bottom: -6px;
}

.hamburger.active {
  background: transparent;
}

.hamburger.active::before {
  top: 0;
  transform: rotate(45deg);
}

.hamburger.active::after {
  bottom: 0;
  transform: rotate(-45deg);
}

/* 移动端菜单 */
.mobile-menu {
  position: absolute;
  top: 70px;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  padding: var(--space-lg);
  box-shadow: var(--shadow-lg);
  animation: fadeInDown 0.3s ease;
}

.mobile-menu-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
}

.mobile-menu-item:hover {
  background: var(--primary-light);
  color: var(--primary-solid);
}

.mobile-menu-actions {
  display: flex;
  gap: var(--space-md);
  margin-top: var(--space-lg);
  padding-top: var(--space-lg);
  border-top: 1px solid var(--border-light);
}

/* ========== 主内容区 ========== */
.main-content {
  flex: 1;
  padding: var(--space-xl);
  max-width: var(--container-xl);
  margin: 0 auto;
  width: 100%;
  min-height: calc(100vh - 70px - 200px);
}

/* ========== 页脚样式 ========== */
.footer {
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
  border-top: 2px solid transparent;
  border-image: var(--primary-gradient) 1;
  padding: 0;
  height: auto !important;
  margin-top: auto;
}

.footer-inner {
  max-width: var(--container-xl);
  margin: 0 auto;
  padding: var(--space-xl) var(--space-lg) var(--space-lg);
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-xl);
  padding-bottom: var(--space-lg);
  border-bottom: 1px solid var(--border-light);
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.footer-logo {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.footer-slogan {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  font-style: italic;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  align-items: flex-end;
}

.footer-link {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-medium);
}

.footer-link:hover {
  color: var(--primary-solid);
  transform: translateX(5px);
}

.footer-bottom {
  padding-top: var(--space-md);
  text-align: center;
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
}

/* ========== 过渡动画 ========== */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all var(--transition-normal);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(15px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all var(--transition-normal);
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .header-inner {
    padding: 0 var(--space-md);
  }

  .header-menu {
    display: none;
  }

  .header-right .username,
  .header-right .arrow-icon {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .main-content {
    padding: var(--space-md);
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: var(--space-lg);
  }

  .footer-links {
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .logo-text {
    font-size: var(--font-size-lg);
  }

  .login-btn,
  .register-btn {
    padding: 8px 16px;
    font-size: var(--font-size-sm);
  }
}
</style>
