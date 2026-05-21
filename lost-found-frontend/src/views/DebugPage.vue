<template>
  <div style="padding: 30px; max-width: 900px; margin: 0 auto; font-family: system-ui;">
    <h1 style="color: #333; margin-bottom: 20px;">🔧 系统诊断工具</h1>

    <!-- 1. API连接测试 -->
    <section style="background: #f5f5f5; padding: 20px; border-radius: 12px; margin-bottom: 20px;">
      <h2 style="color: #ff6b6b; margin-bottom: 15px;">1️⃣ API连接状态</h2>
      <button @click="testApi" :disabled="testing" style="
        padding: 10px 24px;
        background: linear-gradient(135deg, #ff6b6b, #feca57);
        color: white;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-weight: bold;
      ">
        {{ testing ? '⏳ 测试中...' : '🚀 测试API连接' }}
      </button>
      <div v-if="apiResult" style="margin-top: 15px; padding: 15px; background: white; border-radius: 8px;">
        <pre style="white-space: pre-wrap; font-size: 13px; color: #333;">{{ apiResult }}</pre>
      </div>
    </section>

    <!-- 2. 物品列表直接查看 -->
    <section style="background: #f0f9ff; padding: 20px; border-radius: 12px; margin-bottom: 20px;">
      <h2 style="color: #00d2d3; margin-bottom: 15px;">2️⃣ 物品数据直查</h2>
      <div style="display: flex; gap: 10px; margin-bottom: 15px;">
        <button @click="loadItemsDirect" style="
          padding: 10px 20px;
          background: linear-gradient(135deg, #00d2d3, #74b9ff);
          color: white;
          border: none;
          border-radius: 8px;
          cursor: pointer;
          font-weight: bold;
        ">📦 加载物品列表</button>
        <span v-if="items.length" style="padding: 10px; color: #00b894; font-weight: bold;">
          ✅ 共 {{ items.length }} 条记录
        </span>
        <span v-else-if="loaded && !items.length" style="padding: 10px; color: #e17055; font-weight: bold;">
          ⚠️ 没有找到物品数据
        </span>
      </div>

      <div v-if="items.length" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px;">
        <div v-for="item in items" :key="item.id"
             style="background: white; padding: 16px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">
          <div style="font-weight: bold; color: #333; margin-bottom: 8px;">{{ item.title }}</div>
          <div style="font-size: 13px; color: #666; margin-bottom: 4px;">类型: {{ item.itemType === 0 ? '失物招领' : '寻物启事' }}</div>
          <div style="font-size: 13px; color: #666; margin-bottom: 4px;">状态: {{ ['待审核','已通过','已拒绝','已认领'][item.status] || '未知' }}</div>
          <div style="font-size: 13px; color: #666;">地点: {{ item.location || '-' }}</div>
          <img v-if="item.images" :src="'/api' + item.images.split(',')[0]"
               style="width: 100%; height: 150px; object-fit: cover; border-radius: 8px; margin-top: 10px;" />
        </div>
      </div>
    </section>

    <!-- 3. 常见问题解决方案 -->
    <section style="background: #fff9f0; padding: 20px; border-radius: 12px; margin-bottom: 20px;">
      <h2 style="color: #fdcb6e; margin-bottom: 15px;">3️⃣ 常见问题 & 解决方案</h2>
      <div style="line-height: 2;">
        <p><strong>❌ 问题：后端未启动</strong><br/>
           ✅ 解决：运行后端项目（Spring Boot）</p>
        <p><strong>❌ 问题：端口不匹配</strong><br/>
           ✅ 解决：确保前端请求的端口与后端一致（默认8080）</p>
        <p><strong>❌ 问题：数据库无数据</strong><br/>
           ✅ 解决：先发布几条测试数据</p>
        <p><strong>❌ 问题：CORS跨域</strong><br/>
           ✅ 解决：检查后端是否配置了CORS允许前端域名</p>
      </div>
    </section>

    <!-- 4. 快速导航 -->
    <section style="background: #f0fff4; padding: 20px; border-radius: 12px;">
      <h2 style="color: #00b894; margin-bottom: 15px;">4️⃣ 快速导航</h2>
      <div style="display: flex; gap: 12px; flex-wrap: wrap;">
        <router-link to="/home" style="padding: 10px 20px; background: white; border-radius: 8px; text-decoration: none; color: #333; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
          🏠 首页
        </router-link>
        <router-link to="/item/list" style="padding: 10px 20px; background: white; border-radius: 8px; text-decoration: none; color: #333; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
          📋 物品列表
        </router-link>
        <router-link to="/login" style="padding: 10px 20px; background: white; border-radius: 8px; text-decoration: none; color: #333; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
          🔐 登录
        </router-link>
        <router-link to="/register" style="padding: 10px 20px; background: white; border-radius: 8px; text-decoration: none; color: #333; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
          📝 注册
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { itemApi } from '@/api'

const testing = ref(false)
const apiResult = ref('')
const items = ref([])
const loaded = ref(false)

/**
 * 测试API连接
 */
const testApi = async () => {
  testing.value = true
  apiResult.value = ''

  try {
    const startTime = Date.now()
    const res = await itemApi.getList({ pageNum: 1, pageSize: 5 })
    const endTime = Date.now()

    apiResult.value = `✅ API连接成功！
📍 请求地址: /item/list
⏱️  响应时间: ${endTime - startTime}ms
📊 返回数据:
${JSON.stringify(res.data, null, 2)}`

    if (res.data?.list?.length > 0) {
      apiResult.value += `\n\n🎉 数据正常！共有 ${res.data.total} 条物品记录`
    } else {
      apiResult.value += `\n\n⚠️ 返回数据为空，请检查：`
      apiResult.value += `\n   1. 后端数据库是否有数据？`
      apiResult.value += `\n   2. 是否需要登录才能查看？`
      apiResult.value += `\n   3. 筛选条件是否过于严格？`
    }
  } catch (error) {
    console.error('API测试失败:', error)
    apiResult.value = `❌ API连接失败！
🔴 错误信息: ${error.message}
🔴 错误详情: ${error.response ? JSON.stringify(error.response.data, null, 2) : '无响应'}

💡 请检查：
   1. 后端服务是否已启动？（通常在 http://localhost:8080）
   2. 前端代理配置是否正确？（vite.config.js）
   3. 浏览器控制台是否有CORS错误？
   4. 网络是否通畅？`
  } finally {
    testing.value = false
  }
}

/**
 * 直接加载物品列表
 */
const loadItemsDirect = async () => {
  try {
    const res = await itemApi.getList({ pageNum: 1, pageSize: 20 })
    items.value = res.data?.list || []
    loaded.value = true

    if (!items.value.length) {
      console.warn('物品列表为空')
    }
  } catch (error) {
    console.error('加载失败:', error)
    alert('加载失败！请查看控制台错误信息')
  }
}
</script>
