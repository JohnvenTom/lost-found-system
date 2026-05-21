<template>
  <router-view />
</template>

<script setup>
</script>

<style>
@import './styles/variables.css';

/* ========== Element Plus 主题覆盖 ========== */
:root {
  --el-color-primary: #ff6b6b;
  --el-color-primary-light-3: #ff8787;
  --el-color-primary-light-5: #fab1a0;
  --el-color-primary-light-7: #ffe0e0;
  --el-color-primary-light-8: #fff5f5;
  --el-color-primary-light-9: #fffafa;
  --el-color-primary-dark-2: #ee5a5a;

  --el-color-success: #00b894;
  --el-color-warning: #fdcb6e;
  --el-color-danger: #e17055;
  --el-color-info: #74b9ff;

  --el-border-radius-base: 10px;
  --el-border-radius-small: 6px;
  --el-border-radius-round: 20px;

  --el-font-family: var(--font-family-body);

  --el-box-shadow-light: var(--shadow-sm);
  --el-box-shadow: var(--shadow-md);
  --el-box-shadow-dark: var(--shadow-lg);

  --el-transition-duration: 0.3s;
}

/* ========== 全局组件样式增强 ========== */

/* 按钮增强 */
.el-button {
  border-radius: var(--radius-md) !important;
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-normal) !important;
  letter-spacing: 0.3px;
}

.el-button--primary {
  background: var(--primary-gradient) !important;
  border: none !important;
  box-shadow: var(--shadow-sm);
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow) !important;
}

.el-button--primary:active {
  transform: translateY(0);
}

.el-button--large {
  padding: 12px 24px;
  font-size: var(--font-size-md);
}

/* 卡片增强 */
.el-card {
  border-radius: var(--radius-lg) !important;
  border: none !important;
  box-shadow: var(--shadow-sm) !important;
  transition: all var(--transition-normal) !important;
  overflow: hidden;
}

.el-card:hover {
  box-shadow: var(--shadow-lg) !important;
}

.el-card__body {
  padding: var(--space-lg) !important;
}

/* 输入框增强 */
.el-input__wrapper {
  border-radius: var(--radius-md) !important;
  transition: all var(--transition-normal) !important;
  box-shadow: 0 0 0 1px var(--border-color) inset !important;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--primary-light) inset !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px var(--primary-light), var(--shadow-sm) !important;
}

/* 表单标签 */
.el-form-item__label {
  color: var(--text-secondary) !important;
  font-weight: var(--font-weight-medium) !important;
}

/* 菜单增强 */
.el-menu--horizontal {
  border-bottom: none !important;
}

.el-menu-item {
  transition: all var(--transition-normal) !important;
  font-weight: var(--font-weight-medium) !important;
  position: relative;
}

.el-menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: var(--primary-gradient);
  transition: all var(--transition-normal);
  transform: translateX(-50%);
  border-radius: var(--radius-full);
}

.el-menu-item.is-active::after,
.el-menu-item:hover::after {
  width: 80%;
}

.el-menu-item.is-active {
  color: var(--primary-solid) !important;
}

/* 标签增强 */
.el-tag {
  border-radius: var(--radius-full) !important;
  font-weight: var(--font-weight-medium) !important;
  padding: 4px 12px !important;
  border: none !important;
}

/* 对话框增强 */
.el-dialog {
  border-radius: var(--radius-xl) !important;
  overflow: hidden;
}

.el-dialog__header {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  padding: var(--space-lg) !important;
  border-bottom: 1px solid var(--border-light);
}

.el-dialog__title {
  font-size: var(--font-size-xl) !important;
  font-weight: var(--font-weight-bold) !important;
  color: var(--text-primary) !important;
}

.el-dialog__body {
  padding: var(--space-xl) !important;
}

/* 表格增强 */
.el-table {
  border-radius: var(--radius-lg) !important;
  overflow: hidden;
}

.el-table th.el-table__cell {
  background-color: var(--bg-tertiary) !important;
  color: var(--text-secondary) !important;
  font-weight: var(--font-weight-semibold) !important;
}

.el-table .el-table__row {
  transition: all var(--transition-fast) !important;
}

.el-table .el-table__row:hover > td {
  background-color: var(--primary-light) !important;
}

/* 分页器增强 */
.el-pagination {
  gap: var(--space-sm);
}

.el-pagination.is-background .btn-prev,
.el-pagination.is-background .btn-next,
.el-pagination.is-background .el-pager li {
  border-radius: var(--radius-md) !important;
  transition: all var(--transition-fast) !important;
}

.el-pagination.is-background .el-pager li.is-active {
  background: var(--primary-gradient) !important;
  box-shadow: var(--shadow-sm);
}

/* 下拉菜单增强 */
.el-dropdown-menu {
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-lg) !important;
  border: none !important;
  padding: var(--space-sm) !important;
}

.el-dropdown-menu__item {
  border-radius: var(--radius-md) !important;
  margin: 2px 0;
  transition: all var(--transition-fast) !important;
}

.el-dropdown-menu__item:hover {
  background-color: var(--primary-light) !important;
  color: var(--primary-solid) !important;
}

/* 头像增强 */
.el-avatar {
  border: 3px solid white;
  box-shadow: var(--shadow-sm);
}

/* 空状态增强 */
.el-empty__description p {
  color: var(--text-tertiary) !important;
  font-size: var(--font-size-md) !important;
}

/* 消息提示增强 */
.el-message {
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-lg) !important;
  border: none !important;
  padding: var(--space-md) var(--space-lg) !important;
}

.el-message--success {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%) !important;
}

.el-message--warning {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeeba 100%) !important;
}

.el-message--error {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%) !important;
}

/* 加载遮罩增强 */
.el-loading-mask {
  background-color: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(8px);
}

/* 选择器增强 */
.el-select .el-input__wrapper {
  border-radius: var(--radius-md) !important;
}

/* 单选按钮组增强 */
.el-radio-group {
  flex-wrap: wrap;
  gap: var(--space-sm);
}

.el-radio-button__inner {
  border-radius: var(--radius-md) !important;
  border: 2px solid var(--border-color) !important;
  transition: all var(--transition-normal) !important;
  font-weight: var(--font-weight-medium);
}

.el-radio-button__original-radio:checked + .el-radio-button__inner {
  background: var(--primary-gradient) !important;
  border-color: var(--primary-solid) !important;
  box-shadow: var(--shadow-glow);
}

/* 开关增强 */
.el-switch.is-checked .el-switch__core {
  background: var(--primary-gradient) !important;
  border-color: var(--primary-solid) !important;
}

/* 上传组件增强 */
.el-upload--picture-card {
  border-radius: var(--radius-lg) !important;
  border: 2px dashed var(--border-color) !important;
  transition: all var(--transition-normal) !important;
  background: var(--bg-secondary);
}

.el-upload--picture-card:hover {
  border-color: var(--primary-solid) !important;
  background: var(--primary-light);
}

/* 标签页增强 */
.el-tabs__item {
  font-weight: var(--font-weight-medium) !important;
  transition: all var(--transition-normal) !important;
  position: relative;
}

.el-tabs__item.is-active {
  color: var(--primary-solid) !important;
  font-weight: var(--font-weight-bold) !important;
}

.el-tabs__active-bar {
  background: var(--primary-gradient) !important;
  height: 4px !important;
  border-radius: var(--radius-full) !important;
}

/* 警告提示增强 */
.el-alert {
  border-radius: var(--radius-lg) !important;
  border: none !important;
}

.el-alert--info {
  background: linear-gradient(135deg, #e7f3ff 0%, #d6ebff 100%) !important;
}

/* 面包屑增强 */
.el-breadcrumb__inner.is-link,
.el-breadcrumb__inner a {
  font-weight: var(--font-weight-medium) !important;
  color: var(--text-secondary) !important;
  transition: color var(--transition-fast);
}

.el-breadcrumb__inner.is-link:hover,
.el-breadcrumb__inner a:hover {
  color: var(--primary-solid) !important;
}

/* 进度条增强 */
.el-progress-bar__outer {
  border-radius: var(--radius-full) !important;
}

.el-progress-bar__inner {
  background: var(--primary-gradient) !important;
  border-radius: var(--radius-full) !important;
}
</style>
