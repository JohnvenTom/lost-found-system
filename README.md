# 校园失物招领管理系统

> Campus Lost & Found Management System —— 基于 Spring Boot + Vue3 的前后端分离校园失物招领平台，集成 AI 智能分类与内容安全审核。

---

## 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [项目结构](#项目结构)
- [数据库设计](#数据库设计)
- [快速开始](#快速开始)
  - [环境要求](#环境要求)
  - [后端启动](#后端启动)
  - [前端启动](#前端启动)
  - [Docker 部署](#docker-部署)
- [API 接口概览](#api-接口概览)
- [AI 功能说明](#ai-功能说明)
- [配置说明](#配置说明)
- [开发规范](#开发规范)

---

## 项目简介

校园失物招领管理系统是一个面向高校师生的公益服务平台，旨在帮助师生快速发布和查找失物信息，提高失物归还效率。系统支持失物发布、寻物启事、物品认领、留言互动等功能，并创新性地引入 **AI 智能分类** 与 **AI 内容安全审核**，自动对发布信息进行分类和合规审查，降低人工审核成本。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 3.2.5 |
| **JDK** | Java | 17 |
| **ORM** | MyBatis + MyBatis-Plus PageHelper | 3.0.3 / 2.1.0 |
| **数据库** | MySQL | 8.0+ |
| **缓存** | Redis | 6.0+ |
| **认证授权** | JWT (jjwt) | 0.12.5 |
| **AI 集成** | Spring AI + OpenAI API | 1.0.0-M4 |
| **JSON 处理** | FastJSON | 2.0.43 |
| **工具库** | Lombok | latest |
| **前端框架** | Vue 3 | 3.4.21 |
| **构建工具** | Vite | 5.2.8 |
| **路由** | Vue Router | 4.3.0 |
| **HTTP 客户端** | Axios | 1.6.8 |
| **UI 组件库** | Element Plus | 2.6.3 |
| **容器化** | Docker | 20.0+ |

## 功能模块

### 用户模块
- 用户注册 / 登录（JWT 令牌认证）
- 个人信息管理（资料修改、密码加密存储）
- 权限控制（普通用户 / 管理员角色隔离）

### 失物招领模块
- 失物信息发布（捡到物品）与寻物启事（丢失物品）
- 物品列表浏览（分类筛选、关键词搜索、分页加载）
- 物品详情查看（含发布者联系方式、认领入口）

### 分类管理模块
- 系统预设物品分类（证件卡类、电子产品、钥匙钱包等 8 大类）
- 支持管理员自定义分类、排序

### 认领模块
- 在线提交认领申请（填写认领理由及证明材料）
- 管理员审核认领请求
- 物品状态流转（待审核 → 已通过 → 已认领）

### 留言互动模块
- 物品详情页留言讨论
- 支持回复（嵌套评论）

### 公告模块
- 管理员发布系统公告
- 公告置顶、草稿/发布状态管理

### AI 智能功能
- **AI 自动分类**：发布物品时根据标题和描述自动识别所属分类
- **AI 内容安全审核**：自动检测发布内容是否合规（违禁词、敏感信息等）

### 文件上传
- 物品图片上传（多图支持，逗号分隔存储）
- 头像上传、静态资源映射

## 项目结构

```
lost-found-backend/                     # 后端项目
├── src/main/java/com/campus/lostfound/
│   ├── LostFoundApplication.java       # 启动类
│   ├── common/                         # 公共类
│   │   ├── Constants.java              # 常量定义
│   │   ├── PageResult.java             # 分页结果封装
│   │   └── Result.java                 # 统一响应封装
│   ├── config/
│   │   └── WebMvcConfig.java           # Web MVC 配置（CORS、拦截器、静态资源）
│   ├── controller/                     # 控制器层
│   │   ├── AiController.java           # AI 接口
│   │   ├── AnnouncementController.java # 公告接口
│   │   ├── CategoryController.java     # 分类接口
│   │   ├── ClaimController.java        # 认领接口
│   │   ├── ItemController.java         # 物品接口
│   │   ├── MessageController.java      # 留言接口
│   │   ├── UploadController.java       # 文件上传接口
│   │   └── UserController.java         # 用户接口
│   ├── dao/                            # MyBatis Mapper 接口
│   ├── entity/                         # 实体类
│   │   ├── dto/                        # 数据传输对象
│   │   └── *.java                      # 数据库实体
│   ├── exception/                      # 异常处理
│   │   ├── BusinessException.java      # 业务异常
│   │   ├── GlobalExceptionHandler.java # 全局异常处理器
│   │   └── UnauthorizedException.java  # 未授权异常
│   ├── interceptor/
│   │   └── AuthInterceptor.java        # JWT 认证拦截器
│   ├── service/                        # 业务逻辑层
│   │   ├── ai/                         # AI 服务
│   │   │   ├── AiClassificationService.java    # AI 分类服务
│   │   │   └── AiContentModerationService.java # AI 内容审核服务
│   │   └── *.java                      # 各业务服务
│   └── util/
│       └── JwtUtil.java                # JWT 工具类
├── src/main/resources/
│   ├── application.yml                 # 主配置
│   ├── application-dev.yml             # 开发环境配置
│   ├── application-prod.yml            # 生产环境配置
│   └── mapper/                         # MyBatis XML 映射文件
├── sql/
│   └── init.sql                        # 数据库初始化脚本
├── Dockerfile                          # Docker 多阶段构建
└── pom.xml                             # Maven 依赖管理

lost-found-frontend/                    # 前端项目
├── src/
│   ├── main.js                         # 入口文件
│   ├── App.vue                         # 根组件
│   ├── api/index.js                    # API 接口封装
│   ├── utils/request.js                # Axios 请求封装
│   ├── router/index.js                 # 路由配置
│   ├── layout/MainLayout.vue           # 主布局组件
│   ├── styles/variables.css            # 全局样式变量
│   └── views/                          # 页面视图
│       ├── Home.vue                    # 首页
│       ├── Login.vue                   # 登录页
│       ├── Register.vue                # 注册页
│       ├── ItemList.vue                # 物品列表
│       ├── ItemDetail.vue              # 物品详情
│       ├── ItemPublish.vue             # 物品发布
│       ├── MyItems.vue                 # 我的物品
│       ├── MyClaims.vue                # 我的认领
│       ├── Announcement.vue            # 公告页
│       ├── Admin.vue                   # 管理后台
│       ├── DebugPage.vue               # 调试页面
│       └── TestPage.vue                # 测试页面
├── index.html                          # HTML 模板
├── vite.config.js                      # Vite 配置
└── package.json                        # 依赖管理
```

## 数据库设计

系统使用 MySQL `campus_lost_found` 数据库，共 6 张核心表：

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `t_user` | 用户表 | id, username, password(BCrypt), nickname, avatar, phone, email, role, status |
| `t_category` | 分类表 | id, name, icon, sort_order, status |
| `t_item` | 物品表 | id, user_id, category_id, title, description, images, location, item_type, status, ai_category, ai_review_result |
| `t_claim` | 认领表 | id, item_id, user_id, reason, contact_info, status, review_remark |
| `t_message` | 留言表 | id, item_id, user_id, content, parent_id |
| `t_announcement` | 公告表 | id, title, content, user_id, is_top, status |

**关键索引**：用户名唯一索引、手机号索引、物品状态/类型/时间联合索引、外键级联删除。

**初始化数据**（见 `sql/init.sql`）：
- 管理员账户：`admin` / `admin123`
- 测试用户：`zhangsan` / `123456`、`lisi` / `123456`
- 8 个预置分类
- 3 条示例物品数据 + 2 条公告

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+
- npm / pnpm

### 后端启动

```bash
# 1. 进入后端项目目录
cd lost-found-backend

# 2. 初始化数据库（在 MySQL 中执行）
# 执行 sql/init.sql 脚本

# 3. 修改 application-dev.yml 中的数据库连接信息（如需要）
# spring.datasource.username / password
# spring.data.redis.host / port

# 4. 编译运行
mvn clean package -DskipTests
mvn spring-boot:run

# 服务启动后访问: http://localhost:8088/api
```

### 前端启动

```bash
# 1. 进入前端项目目录
cd lost-found-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 打开浏览器访问: http://localhost:5173
```

### Docker 部署

```bash
# 进入后端项目目录
cd lost-found-backend

# 构建 Docker 镜像（多阶段构建）
docker build -t campus-lost-found:1.0.0 .

# 运行容器（需要设置生产环境变量）
docker run -d \
  -p 8088:8088 \
  -e DB_HOST=your-mysql-host \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your-password \
  -e REDIS_HOST=your-redis-host \
  -e REDIS_PASSWORD=your-redis-password \
  -e OPENAI_API_KEY=your-api-key \
  -e JWT_SECRET=your-jwt-secret \
  --name lost-found \
  campus-lost-found:1.0.0
```

## API 接口概览

| 模块 | 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|------|
| **用户** | POST | `/api/user/register` | 用户注册 | 否 |
| | POST | `/api/user/login` | 用户登录 | 否 |
| | GET | `/api/user/info` | 获取当前用户信息 | 是 |
| | PUT | `/api/user/update` | 更新个人信息 | 是 |
| **物品** | POST | `/api/item/publish` | 发布物品 | 是 |
| | GET | `/api/item/list` | 物品列表（分页+筛选） | 否 |
| | GET | `/api/item/detail/{id}` | 物品详情 | 否 |
| | GET | `/api/item/my` | 我的物品 | 是 |
| **分类** | GET | `/api/category/list` | 分类列表 | 否 |
| **认领** | POST | `/api/claim/apply` | 提交认领 | 是 |
| | GET | `/api/claim/my` | 我的认领 | 是 |
| **留言** | POST | `/api/message/send` | 发送留言 | 是 |
| | GET | `/api/message/list/{itemId}` | 留言列表 | 否 |
| **公告** | GET | `/api/announcement/list` | 公告列表 | 否 |
| | GET | `/api/announcement/detail/{id}` | 公告详情 | 否 |
| **AI** | GET | `/api/ai/classify` | AI 智能分类 | 否 |
| | GET | `/api/ai/moderate` | AI 内容审核 | 否 |
| **上传** | POST | `/api/upload/image` | 图片上传 | 是 |

> 统一响应格式：`{ "code": 200, "message": "操作成功", "data": {...} }`

## AI 功能说明

系统集成 **Spring AI** 框架，通过 OpenAI 协议调用 LLM 大模型实现智能能力：

1. **智能分类**（`AiClassificationService`）
   - 输入：物品标题 + 描述
   - 输出：匹配的系统分类名称
   - 触发时机：用户发布物品时自动调用

2. **内容安全审核**（`AiContentModerationService`）
   - 输入：物品标题 + 描述
   - 输出：审核是否通过 + 审核理由
   - 违规内容将被标记为"AI 审核不通过"状态
   - 触发时机：用户发布物品时自动调用

生产环境需配置有效的 OpenAI API Key 或兼容接口地址。

## 配置说明

### 后端多环境配置

| 配置文件 | 环境 | 说明 |
|----------|------|------|
| `application.yml` | 公共 | 服务端口(8088)、context-path(/api)、文件上传限制、MyBatis、JWT |
| `application-dev.yml` | 开发 | 本地 MySQL/Redis、LongCat 模型 |
| `application-prod.yml` | 生产 | 环境变量注入（DB_HOST、DB_PASSWORD 等），MyBatis 日志关闭 |

### JWT 配置

```yaml
jwt:
  secret: <自定义256位密钥>    # 生产环境务必更换
  expiration: 86400000         # Token 有效期（毫秒），默认 24 小时
```

### 文件上传配置

```yaml
spring.servlet.multipart:
  max-file-size: 10MB          # 单文件最大 10MB
  max-request-size: 20MB       # 单次请求最大 20MB
upload.path: ./uploads/        # 上传文件存储路径
```

## 开发规范

本项目遵循以下开发规范：

- **代码规范**：Java 遵循《阿里巴巴 Java 开发手册》，前端使用 TypeScript / ESLint
- **命名规范**：统一使用英文驼峰命名，禁止拼音 / 中文 / 无意义缩写
- **安全规范**：
  - 密码使用 BCrypt 加密存储，禁止明文
  - JWT 令牌认证，敏感接口强制鉴权
  - SQL 使用 MyBatis 参数化查询，防止注入
  - 生产环境密钥通过环境变量注入，禁止硬编码
- **异常规范**：全局异常统一捕获（`GlobalExceptionHandler`），对外不暴露堆栈信息
- **环境隔离**：开发 / 测试 / 生产环境严格分离（`application-{profile}.yml`）
- **日志规范**：使用 Slf4j 统一日志，开发环境 DEBUG 级别，生产环境 INFO 级别

---

**License**: 本项目仅供学习交流使用。