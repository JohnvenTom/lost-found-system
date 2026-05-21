<template>
  <div class="register-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="floating-shape shape-1">✨</div>
      <div class="floating-shape shape-2">🎯</div>
      <div class="floating-shape shape-3">💫</div>
    </div>

    <!-- 注册卡片容器 -->
    <div class="register-container">
      <div class="register-card">
        <!-- 左侧装饰区域 -->
        <div class="register-banner">
          <div class="banner-content">
            <div class="banner-icon">🚀</div>
            <h2 class="banner-title">加入我们</h2>
            <p class="banner-text">创建账号，开启您的校园失物招领之旅</p>
            <div class="banner-stats">
              <div class="stat-item">
                <div class="stat-number">10K+</div>
                <div class="stat-label">活跃用户</div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <div class="stat-number">5K+</div>
                <div class="stat-label">成功认领</div>
              </div>
            </div>
          </div>
          <div class="banner-bg-pattern"></div>
        </div>

        <!-- 右侧表单区域 -->
        <div class="register-form-wrapper">
          <div class="form-header">
            <h1 class="form-title">创建新账号</h1>
            <p class="form-subtitle">填写以下信息完成注册</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large"
                   @submit.prevent="handleRegister">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item prop="username">
                  <div class="input-group">
                    <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="nickname">
                  <div class="input-group">
                    <el-input v-model="form.nickname" placeholder="昵称（可选）" prefix-icon="UserFilled" />
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item prop="password">
              <div class="input-group password-group">
                <el-input v-model="form.password" type="password" placeholder="密码"
                          prefix-icon="Lock" show-password @input="checkPasswordStrength" />
                <div v-if="form.password" class="password-strength">
                  <div class="strength-bar">
                    <div class="strength-fill" :class="passwordStrength.level"
                         :style="{ width: passwordStrength.percent + '%' }"></div>
                  </div>
                  <span class="strength-text">{{ passwordStrength.text }}</span>
                </div>
              </div>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <div class="input-group">
                <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码"
                          prefix-icon="Lock" show-password />
              </div>
            </el-form-item>

            <el-form-item prop="phone">
              <div class="input-group">
                <el-input v-model="form.phone" placeholder="手机号（可选）" prefix-icon="Phone" />
              </div>
            </el-form-item>

            <el-form-item>
              <button type="submit" class="submit-btn" :class="{ loading: loading }" :disabled="loading">
                <span v-if="!loading" class="btn-text">立即注册</span>
                <span v-else class="btn-loading">
                  <span class="spinner"></span>
                  注册中...
                </span>
              </button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <p class="footer-text">
              已有账号？
              <router-link to="/login" class="link">立即登录 →</router-link>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

/**
 * 验证密码确认是否匹配
 * @param {Object} rule - 验证规则对象
 * @param {string} value - 输入的确认密码值
 * @param {Function} callback - 验证回调函数
 */
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度在2到50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

/**
 * 检查密码强度并返回强度等级
 * @returns {Object} 包含强度等级、百分比和文本描述
 */
const passwordStrength = computed(() => {
  const pwd = form.password

  if (!pwd) {
    return { level: '', percent: 0, text: '' }
  }

  let score = 0
  if (pwd.length >= 6) score++
  if (pwd.length >= 10) score++
  if (/[A-Z]/.test(pwd)) score++
  if (/[0-9]/.test(pwd)) score++
  if (/[^A-Za-z0-9]/.test(pwd)) score++

  if (score <= 1) {
    return { level: 'weak', percent: 33, text: '弱' }
  } else if (score <= 3) {
    return { level: 'medium', percent: 66, text: '中等' }
  } else {
    return { level: 'strong', percent: 100, text: '强' }
  }
})

/**
 * 触发密码强度检查（响应式计算属性会自动更新）
 */
const checkPasswordStrength = () => {
  // 密码强度通过computed自动计算
}

/**
 * 处理用户注册请求
 * 验证表单数据后调用API进行注册，成功后跳转到登录页
 * @returns {Promise<void>}
 */
const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    await userApi.register(form)

    ElMessage({
      message: '注册成功！欢迎加入 🎉',
      type: 'success',
      duration: 2000,
      showClose: true
    })

    setTimeout(() => {
      router.push('/login')
    }, 800)
  } catch (e) {
    console.error('注册失败', e)
    ElMessage.error(e.response?.data?.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
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
  font-size: 42px;
}

.shape-1 {
  top: 20%;
  left: 15%;
}

.shape-2 {
  top: 60%;
  right: 20%;
  animation-delay: 4s;
}

.shape-3 {
  bottom: 25%;
  left: 30%;
  animation-delay: 8s;
}

/* ========== 注册容器 ========== */
.register-container {
  width: 100%;
  max-width: 900px;
  z-index: 10;
  animation: fadeInUp 0.8s ease;
}

.register-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow:
    0 32px 64px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.5);
  overflow: hidden;
  min-height: 600px;
}

/* ========== 左侧Banner ========== */
.register-banner {
  background: linear-gradient(135deg, #00d2d3 0%, #74b9ff 100%);
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

.banner-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-xl);
  padding: var(--space-lg) var(--space-xl);
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  line-height: 1;
  margin-bottom: var(--space-xs);
}

.stat-label {
  font-size: var(--font-size-sm);
  opacity: 0.95;
}

.stat-divider {
  width: 2px;
  height: 40px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 1px;
}

.banner-bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 70% 30%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

/* ========== 右侧表单 ========== */
.register-form-wrapper {
  padding: var(--space-2xl) var(--space-xl);
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
  max-height: 600px;
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
  background: linear-gradient(135deg, #00d2d3 0%, #74b9ff 100%);
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
  box-shadow: 0 4px 16px rgba(0, 210, 211, 0.08), 0 0 0 2px rgba(0, 210, 211, 0.1) inset !important;
}

.input-group :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 20px rgba(0, 210, 211, 0.15), 0 0 0 3px rgba(0, 210, 211, 0.15) !important;
}

.password-group {
  position: relative;
}

.password-strength {
  margin-top: var(--space-sm);
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: var(--radius-full);
  transition: all var(--transition-normal);
}

.strength-fill.weak {
  background: linear-gradient(90deg, #e17055, #fdcb6e);
}

.strength-fill.medium {
  background: linear-gradient(90deg, #fdcb6e, #00b894);
}

.strength-fill.strong {
  background: linear-gradient(90deg, #00b894, #55efc4);
}

.strength-text {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
  min-width: 32px;
}

.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: var(--radius-lg) !important;
  background: linear-gradient(135deg, #00d2d3 0%, #74b9ff 100%) !important;
  color: white;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  letter-spacing: 2px;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: 0 8px 24px rgba(0, 210, 211, 0.35);
  position: relative;
  overflow: hidden;
  border: none;
  margin-top: var(--space-md);
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
  box-shadow: 0 12px 32px rgba(0, 210, 211, 0.45);
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
  margin-top: var(--space-lg);
  padding-top: var(--space-lg);
  border-top: 1px solid var(--border-light);
}

.footer-text {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

.link {
  color: #00d2d3;
  font-weight: var(--font-weight-semibold);
  transition: all var(--transition-fast);
  margin-left: 4px;
}

.link:hover {
  color: #0984e3;
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
  .register-card {
    grid-template-columns: 1fr;
    max-width: 480px;
    margin: 0 auto;
  }

  .register-banner {
    display: none;
  }

  .register-form-wrapper {
    padding: var(--space-2xl) var(--space-lg);
    max-height: none;
  }

  .circle {
    display: none;
  }

  .floating-shape {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .register-form-wrapper {
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
