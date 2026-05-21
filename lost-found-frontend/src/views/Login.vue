<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 登录卡片容器 -->
    <div class="login-container">
      <div class="login-card">
        <!-- 左侧装饰区域 -->
        <div class="login-banner">
          <div class="banner-content">
            <div class="banner-icon">🔍</div>
            <h2 class="banner-title">欢迎回来</h2>
            <p class="banner-text">登录您的账号，继续您的失物招领之旅</p>
            <div class="banner-features">
              <div class="feature-item">
                <span class="feature-icon">✨</span>
                <span>快速发布失物信息</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">🎯</span>
                <span>智能匹配认领</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">💬</span>
                <span>实时留言互动</span>
              </div>
            </div>
          </div>
          <div class="banner-bg-pattern"></div>
        </div>

        <!-- 右侧表单区域 -->
        <div class="login-form-wrapper">
          <div class="form-header">
            <h1 class="form-title">校园失物招领系统</h1>
            <p class="form-subtitle">请输入您的账号信息</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large"
                   @submit.prevent="handleLogin">
            <el-form-item prop="username">
              <div class="input-group">
                <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-group">
                <el-input v-model="form.password" type="password" placeholder="请输入密码"
                          prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
              </div>
            </el-form-item>

            <el-form-item>
              <button type="submit" class="submit-btn" :class="{ loading: loading }" :disabled="loading">
                <span v-if="!loading" class="btn-text">登 录</span>
                <span v-else class="btn-loading">
                  <span class="spinner"></span>
                  登录中...
                </span>
              </button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <p class="footer-text">
              还没有账号？
              <router-link to="/register" class="link">立即注册 →</router-link>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

/**
 * 处理用户登录请求
 * 验证表单数据后调用API进行登录，成功后存储Token并跳转
 * @returns {Promise<void>}
 */
const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    const res = await userApi.login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))

    ElMessage({
      message: '登录成功！欢迎回来 🎉',
      type: 'success',
      duration: 2000,
      showClose: true
    })

    const redirect = route.query.redirect || '/home'
    setTimeout(() => {
      router.push(redirect)
    }, 500)
  } catch (e) {
    console.error('登录失败', e)
    ElMessage.error('用户名或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  padding: var(--space-md);
  position: relative;
  overflow: hidden;
}

/* ========== 背景装饰 ========== */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -150px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -100px;
  left: -80px;
  animation: float 10s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 6s ease-in-out infinite 2s;
}

.floating-shape {
  position: absolute;
  opacity: 0.15;
  animation: floatRotate 12s linear infinite;
}

.shape-1 {
  top: 20%;
  left: 15%;
  font-size: 48px;
}

.shape-2 {
  top: 60%;
  right: 20%;
  font-size: 36px;
  animation-delay: 4s;
}

.shape-3 {
  bottom: 25%;
  left: 30%;
  font-size: 42px;
  animation-delay: 8s;
}

/* ========== 登录容器 ========== */
.login-container {
  width: 100%;
  max-width: 900px;
  z-index: 10;
  animation: fadeInUp 0.8s ease;
}

.login-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow:
    0 32px 64px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.5);
  overflow: hidden;
  min-height: 550px;
}

/* ========== 左侧Banner ========== */
.login-banner {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  padding: var(--space-3xl) var(--space-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
}

.banner-icon {
  font-size: 72px;
  margin-bottom: var(--space-lg);
  animation: bounce 2s ease-in-out infinite;
  filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.2));
}

.banner-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-sm);
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.banner-text {
  font-size: var(--font-size-base);
  opacity: 0.95;
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--space-xl);
}

.banner-features {
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  padding: var(--space-sm) var(--space-md);
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  backdrop-filter: blur(10px);
  transition: all var(--transition-normal);
}

.feature-item:hover {
  transform: translateX(8px);
  background: rgba(255, 255, 255, 0.3);
}

.feature-icon {
  font-size: 18px;
}

.banner-bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

/* ========== 右侧表单 ========== */
.login-form-wrapper {
  padding: var(--space-3xl) var(--space-xl);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.form-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-xs);
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-tertiary);
}

.input-group {
  position: relative;
}

.input-group :deep(.el-input__wrapper) {
  border-radius: var(--radius-lg) !important;
  padding: 4px 16px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04), 0 0 0 1px var(--border-color) inset !important;
  transition: all var(--transition-normal) !important;
}

.input-group :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.08), 0 0 0 2px var(--primary-light) inset !important;
}

.input-group :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15), 0 0 0 3px var(--primary-light) !important;
  border-color: var(--primary-solid) !important;
}

.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: var(--radius-lg) !important;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%) !important;
  color: white;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  letter-spacing: 2px;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: 0 8px 24px rgba(255, 107, 107, 0.35);
  position: relative;
  overflow: hidden;
  border: none;
  margin-top: var(--space-sm);
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  transition: left 0.5s;
}

.submit-btn:hover:not(:disabled)::before {
  left: 100%;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(255, 107, 107, 0.45);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(-1px);
}

.submit-btn.loading {
  pointer-events: none;
  opacity: 0.85;
}

.btn-loading {
  display: inline-flex;
  align-items: center;
  gap: var(--space-sm);
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: rotate 0.8s linear infinite;
}

.form-footer {
  text-align: center;
  margin-top: var(--space-xl);
  padding-top: var(--space-lg);
  border-top: 1px solid var(--border-light);
}

.footer-text {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

.link {
  color: var(--primary-solid);
  font-weight: var(--font-weight-semibold);
  transition: all var(--transition-fast);
  margin-left: 4px;
}

.link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

/* ========== 动画定义 ========== */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

@keyframes floatRotate {
  0% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-25px) rotate(180deg);
  }
  100% {
    transform: translateY(0) rotate(360deg);
  }
}

@keyframes bounce {
  0%, 20%, 53%, 80%, 100% {
    transform: translateY(0);
  }
  40%, 43% {
    transform: translateY(-12px);
  }
  70% {
    transform: translateY(-6px);
  }
  90% {
    transform: translateY(-2px);
  }
}

@keyframes rotate {
  to {
    transform: rotate(360deg);
  }
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .login-card {
    grid-template-columns: 1fr;
    max-width: 450px;
    margin: 0 auto;
  }

  .login-banner {
    display: none;
  }

  .login-form-wrapper {
    padding: var(--space-2xl) var(--space-lg);
  }

  .circle {
    display: none;
  }

  .floating-shape {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .login-form-wrapper {
    padding: var(--space-xl) var(--space-md);
  }

  .form-title {
    font-size: var(--font-size-xl);
  }

  .submit-btn {
    height: 48px;
    font-size: var(--font-size-base);
  }
}
</style>
