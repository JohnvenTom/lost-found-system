import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/debug',
    name: 'Debug',
    component: () => import('@/views/DebugPage.vue'),
    meta: { title: '系统诊断' }
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/TestPage.vue'),
    meta: { title: '测试' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'item/list',
        name: 'ItemList',
        component: () => import('@/views/ItemList.vue'),
        meta: { title: '失物列表' }
      },
      {
        path: 'item/detail/:id',
        name: 'ItemDetail',
        component: () => import('@/views/ItemDetail.vue'),
        meta: { title: '物品详情' }
      },
      {
        path: 'item/publish',
        name: 'ItemPublish',
        component: () => import('@/views/ItemPublish.vue'),
        meta: { title: '发布信息', requiresAuth: true }
      },
      {
        path: 'my/items',
        name: 'MyItems',
        component: () => import('@/views/MyItems.vue'),
        meta: { title: '我的发布', requiresAuth: true }
      },
      {
        path: 'my/claims',
        name: 'MyClaims',
        component: () => import('@/views/MyClaims.vue'),
        meta: { title: '我的认领', requiresAuth: true }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('@/views/Announcement.vue'),
        meta: { title: '系统公告' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/Admin.vue'),
        meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫
 * 校验登录状态和管理员权限
 */
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '校园失物招领'} - 校园失物招领系统`

  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin && user.role !== 1) {
    next('/home')
    return
  }

  next()
})

export default router
