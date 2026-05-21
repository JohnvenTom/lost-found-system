<template>
  <div style="padding: 20px; background: #fff; min-height: 100vh;">
    <h1 style="color: #333; font-size: 24px;">✅ 测试页面 - 如果你看到这个，说明Vue正常工作</h1>
    <p style="margin-top: 20px; color: #666;">当前时间: {{ currentTime }}</p>
    <el-button type="primary" @click="$router.push('/home')">前往首页</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const currentTime = ref('')

onMounted(() => {
  currentTime.value = new Date().toLocaleString()
})
</script>
