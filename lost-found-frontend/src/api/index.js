import request from '@/utils/request'

/**
 * 用户相关API接口
 */
export const userApi = {
  login: (data) => request.post('/user/login', data),
  register: (data) => request.post('/user/register', data),
  getUserInfo: () => request.get('/user/info'),
  updateUser: (data) => request.put('/user/update', data),
  getUserDetail: (id) => request.get(`/user/detail/${id}`),
  deleteUser: (id) => request.delete(`/user/delete/${id}`)
}

/**
 * 物品相关API接口
 */
export const itemApi = {
  publish: (data) => request.post('/item/publish', data),
  getList: (params) => request.get('/item/list', { params }),
  getDetail: (id) => request.get(`/item/detail/${id}`),
  getMyItems: () => request.get('/item/my'),
  updateItem: (id, data) => request.put(`/item/update/${id}`, data),
  reviewItem: (id, status) => request.put(`/item/review/${id}`, null, { params: { status } }),
  deleteItem: (id) => request.delete(`/item/delete/${id}`)
}

/**
 * 分类相关API接口
 */
export const categoryApi = {
  getList: () => request.get('/category/list'),
  getDetail: (id) => request.get(`/category/detail/${id}`),
  addCategory: (data) => request.post('/category/add', data),
  updateCategory: (id, data) => request.put(`/category/update/${id}`, data),
  deleteCategory: (id) => request.delete(`/category/delete/${id}`)
}

/**
 * 认领相关API接口
 */
export const claimApi = {
  submit: (data) => request.post('/claim/submit', data),
  getByItem: (itemId) => request.get(`/claim/item/${itemId}`),
  getMyClaims: () => request.get('/claim/my'),
  reviewClaim: (id, status, reviewRemark) =>
    request.put(`/claim/review/${id}`, null, { params: { status, reviewRemark } }),
  deleteClaim: (id) => request.delete(`/claim/delete/${id}`)
}

/**
 * 留言相关API接口
 */
export const messageApi = {
  add: (data) => request.post('/message/add', data),
  getByItem: (itemId) => request.get(`/message/list/${itemId}`),
  deleteMessage: (id) => request.delete(`/message/delete/${id}`)
}

/**
 * 公告相关API接口
 */
export const announcementApi = {
  getList: (params) => request.get('/announcement/list', { params }),
  getDetail: (id) => request.get(`/announcement/detail/${id}`),
  add: (data) => request.post('/announcement/add', data),
  update: (id, data) => request.put(`/announcement/update/${id}`, data),
  delete: (id) => request.delete(`/announcement/delete/${id}`)
}

/**
 * AI功能API接口
 */
export const aiApi = {
  classify: (title, description) =>
    request.get('/ai/classify', { params: { title, description } }),
  moderate: (title, description) =>
    request.get('/ai/moderate', { params: { title, description } })
}

/**
 * 文件上传API接口
 */
export const uploadApi = {
  uploadFile: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/file', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
