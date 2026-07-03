# 笔记应用 NoteApp
GitHub 仓库地址：https://github.com/balaba641/2025003026-NoteApp

## 1. 项目简介
应用名称：口袋笔记

### 目标用户
需要日常记录灵感、待办事项、学习笔记的普通用户，偏好玫红 / 红色视觉风格的用户。

### 核心功能
- 用户注册与登录（账号密码持久化）
- 创建、编辑、删除笔记（含标题、正文、可选配图链接）
- 按标题或内容搜索笔记（带防抖优化）
- 分类管理（新建分类、删除分类、按分类筛选笔记）
- 笔记置顶 / 取消置顶
- 笔记列表按置顶和更新时间排序
- 查看笔记详情（支持配图展示）
- 随机名言灵感（网络请求，可刷新）
- 深色 / 浅色模式切换（跟随系统或手动设置）
- 用户偏好持久化（主题模式、最近搜索词、自动保存开关）
- 个人中心（修改用户名 / 昵称 / 密码、注销账户、退出登录）

## 2. 技术栈
- UI：Jetpack Compose + Material 3
- 数据库：Room（3 张表）
- 网络：Retrofit + OkHttp + LoggingInterceptor（接口来源：DummyJSON）
- 状态管理：ViewModel + StateFlow
- 持久化偏好：DataStore
- 导航：Navigation Compose
- 异步处理：Kotlin Coroutines
- 图片加载：Coil（AsyncImage）
- 其他依赖：Material Icons Extended

## 3. 功能清单
### 必做项完成情况
#### UI 层
1. Jetpack Compose 构建全部 UI
2. 多页面实现：首页、详情、编辑、设置、个人中心、登录、注册
3. Compose Navigation 页面导航
4. LazyColumn 实现笔记列表展示
5. 完整使用 Material 3 组件：Card, Button, TextField, TopAppBar, FAB, Switch, Dropdown, Dialog, AlertDialog 等
6. 浅色 / 深色模式支持（跟随系统或手动切换，DataStore 持久存储）
7. 自定义 Material 主题（红色→玫粉色系，自定义颜色、排版样式）

#### 数据层
1. Room 本地数据库，包含 `notes`、`categories`、`users` 三张数据表
2. 全量 CRUD 增删改查操作
3. DAO 查询统一返回 Flow 数据流：`getAllNotes`、`searchNotes` 返回 `Flow<List<NoteEntity>>`
4. 多条件查询：模糊搜索、分类筛选
5. DataStore 持久存储用户偏好：主题模式、搜索历史、自动保存开关、登录状态

#### 网络层
1. 声明并申请 `INTERNET` 网络权限
2. Retrofit 请求 DummyJSON 接口获取随机名言
3. 首页展示名言，支持手动刷新
4. 封装 `QuoteUiState` 区分加载 / 成功 / 失败状态
5. 网络逻辑隔离在 ViewModel + Repository，Compose 不直接发起网络请求

#### 架构层
1. ViewModel 统一管理页面状态（NoteViewModel）
2. Repository 分层隔离本地数据库与远程网络数据源
3. 使用 StateFlow / Flow 响应式数据流
4. Kotlin 协程处理全部异步逻辑
5. 密封类 UiState 统一管理页面状态：Loading / Success / Error / Empty
6. UI 层无直接数据库、网络调用，完全由 ViewModel 驱动业务

#### 功能完整性
1. 六大核心交互：新增 / 编辑 / 删除 / 搜索 / 置顶 / 分类筛选
2. 完整输入校验：笔记标题内容非空、注册登录校验、个人资料修改校验
3. 多状态页面展示：空数据、加载中、网络错误提示
4. ViewModel 保存页面状态，屏幕旋转后数据不丢失

### 选做项完成情况
1. 高级数据库查询：模糊搜索使用 SQL LIKE 匹配标题、正文
2. 搜索防抖：输入延迟 500ms 执行查询，减少数据库频繁访问
3. 搜索历史持久化：搜索关键词自动存入 DataStore
4. Coil 图片加载：笔记详情页 AsyncImage 加载网络配图
5. 完整分类体系：新建分类、删除分类自动迁移笔记、分类筛选
6. 首页刷新功能：顶部按钮手动刷新名言数据

## 4. 数据库设计
### 表 1：notes 笔记表
|字段名|类型|说明|
|---|---|---|
|id|Int|主键，自增|
|title|String|笔记标题|
|content|String|笔记内容|
|categoryId|Int|外键，关联 [categories.id](categories.id)|
|imageUrl|String?|可选配图链接，可为空|
|isPinned|Boolean|是否置顶，默认 false|
|createdAt|Long|创建时间戳（秒）|
|updatedAt|Long|更新时间戳（秒）|

### 表 2：categories 分类表
|字段名|类型|说明|
|---|---|---|
|id|Int|主键，自增|
|name|String|分类名称|
|color|String|十六进制颜色值，默认 #FF6B6B|

### 表 3：users 用户表
|字段名|类型|说明|
|---|---|---|
|id|Int|主键，自增|
|username|String|用户名，唯一不可重复|
|password|String|密码（当前版本明文存储，生产需加密）|
|nickname|String?|用户昵称，可选|
|avatar|String?|头像链接，预留扩展|

### 表关系
一对多关系：一个分类可包含多条笔记，`notes.categoryId` 外键关联 `categories.id`
当前为单用户版本，后续可新增 `userId` 字段实现多用户数据隔离

### 核心 DAO 查询方法
1. `NoteDao.getAllNotes()`：返回 Flow 列表，按置顶降序、更新时间降序排序
2. `NoteDao.searchNotes(searchQuery)`：模糊匹配标题、内容搜索笔记
3. `NoteDao.getNotesByCategory(categoryId)`：根据分类 ID 筛选笔记
4. `NoteDao.getNoteById(noteId)`：挂起函数，单条笔记查询
5. `UserDao.login(username, password)`：账号密码登录校验
6. `UserDao.getUserByUsername(username)`：查询用户名是否已注册

## 5. 网络功能设计
### API 基础信息
- 数据源：DummyJSON [https://dummyjson\.com/](https://dummyjson.com/)
- 接口地址：`/quotes/random`
- 请求方式：GET

### 返回 JSON 结构
```json
{
  "id": 1,
  "quote": "Life is what happens when you're busy making other plans.",
  "author": "John Lennon",
  "tags": ["life", "humor"]
}
```

### 页面使用
首页底部展示名言文本与作者，提供刷新按钮重新发起网络请求。

### 异常处理
使用密封类 `QuoteUiState` 区分加载、成功、错误状态；网络请求失败时展示错误提示，保留上一次成功缓存的名言，避免页面空白。

## 6. 架构设计
### 分层结构
1. **Data Layer 数据层**
    - 本地数据源：Room（NoteDao、CategoryDao、UserDao）
    - 远程数据源：Retrofit ApiService 网络接口

2. **Repository 仓库层**
    - `NoteRepository` 统一封装本地、远程数据操作
    - 对外暴露 Flow、suspend 挂起函数，屏蔽底层数据源细节

3. **ViewModel 视图模型层**
    - `NoteViewModel` 持有 Repository 实例
    - 管理全部 UI 状态流：笔记状态、名言状态、编辑表单、登录注册、当前用户信息
    - 处理业务逻辑：搜索防抖、笔记增删改查、网络请求、用户信息修改

4. **UI Layer 界面层**
    - Compose 页面通过 `collectAsStateWithLifecycle` 订阅 ViewModel 状态流
    - 用户点击、输入等事件统一回调 ViewModel 处理
    - 界面无任何数据库、网络直接调用逻辑

### UiState 密封类定义
- `NoteUiState`：Loading / Success / Error / Empty（笔记列表状态）
- `QuoteUiState`：Loading / Success / Error（网络名言状态）
- `EditNoteUiState`：DataClass 表单数据类（编辑笔记表单）
- `LoginUiState`：Idle / Loading / Success / Error（登录页面状态）
- `RegisterUiState`：Idle / Loading / Success / Error（注册页面状态）

## 7. 核心功能截图说明
1. **登录注册页**
图片地址：screenshots\login.png
说明：应用图标 + 标语「口袋笔记，随时记录灵感」，用户名密码输入框，登录按钮、注册跳转链接。
1. **首页（笔记列表）**
图片地址：screenshots\home.png
说明：顶部搜索框、应用标题、个人中心入口；中间笔记列表展示标题内容预览；右下角新建笔记悬浮按钮；底部随机名言 + 刷新按钮；无数据时展示空状态提示。
1. **新建 / 编辑笔记页**
图片地址：screenshots\edit.png
说明：标题、内容输入框、分类选择器（支持新增 / 删除分类）、配图链接输入框；保存按钮，空输入时弹出错误提示。
1. **笔记详情页**
图片地址：screenshots\detail.png
说明：完整展示笔记标题、正文；存在图片链接时加载网络配图；顶部操作栏：返回、编辑按钮。
1. **个人中心页**
图片地址：screenshots\profile.png
说明：用户名首字母头像、用户名、昵称展示；功能列表：修改资料、注销账户、退出登录。

## 8. 技术难点与解决方案
### 难点 1：新建笔记表单残留旧编辑内容
- 问题：点击新建笔记时，编辑页保留上一条笔记数据，混淆用户操作
- 原因：ViewModel 共用单一 `EditNoteUiState`，跳转页面未重置状态
- 解决方案：ViewModel 新增 `resetEditState()` 重置表单；编辑页面使用 `LaunchedEffect` 判断，新建笔记（noteId=0）时执行重置。
- 参考资料：Jetpack Compose 状态管理最佳实践

### 难点 2：实时搜索频繁查询数据库造成 UI 卡顿
- 问题：输入每一个字符都触发数据库模糊查询，列表频繁刷新卡顿
- 原因：未做防抖，文本实时变化立即执行查询
- 解决方案：搜索框监听文本变化，延迟 500ms 后再执行数据库查询，减少无效访问。
- 参考资料：Kotlin Flow 官方文档

### 难点 3：删除分类存在外键约束报错
- 问题：分类下存在笔记时直接删除，触发 Room 外键约束异常
- 原因：笔记表 categoryId 关联分类主键，直接删除会破坏外键完整性
- 解决方案：删除分类前批量更新该分类下所有笔记，迁移至默认分类 ID，再执行删除；无默认分类时弹窗提示先创建。
- 参考资料：Room 外键约束处理策略

### 难点 4：深色模式切换后主题不实时刷新
- 问题：修改主题偏好后需重启 APP 才生效，无法即时切换
- 原因：Compose 主题未订阅 DataStore 数据流变化
- 解决方案：将深色模式封装为 StateFlow，主题顶层直接收集状态，数值变化自动重组页面，无需重启 Activity。
- 参考资料：Android DataStore 与 Compose 集成指南

## 9. AI 使用说明
### 使用工具
网页版 AI：豆包、ChatGPT 4、DeepSeek
IDE 辅助工具：GitHub Copilot、Cursor
### AI 参与开发环节
- 选题分析：笔记应用需求、功能方案设计
- 代码框架生成：数据层、ViewModel、Compose UI 基础模板
- 代码调试：依赖冲突、编译报错、布局样式修复
- 功能拓展：登录注册、分类管理、个人中心完整逻辑实现
- 文档撰写：本项目报告、架构、难点梳理
- UI 优化：玫红 / 红色主题配色方案、页面视觉优化
### 使用说明
AI 仅作为辅助开发工具，全部代码均自行阅读、修改、调试，保证逻辑完整可正常运行。

## 10. 运行说明
### 版本要求
- 最低适配：Android API 24（Android 7.0）
- 推荐运行：Android API 34（Android 14）

### 权限说明
仅需要网络权限：`<uses-permission android:name="android.permission.INTERNET" />`

### 运行步骤
1. 克隆项目仓库
```shell
git clone https://github.com/balaba641/2025003026-NoteApp
```
2. Android Studio 打开项目文件夹
3. 等待 Gradle 自动同步下载依赖
4. 连接安卓模拟器或真机设备
5. 点击 Run 按钮编译安装运行

## 11. 项目亮点
1. 专属玫红主题体系：深浅两套配色，视觉风格统一鲜明
2. 高性能搜索：防抖机制优化数据库查询，滚动流畅不卡顿
3. 完善分类系统：新建、删除自动迁移笔记、多维度筛选
4. 网络 + 本地双数据源融合：随机名言增加产品趣味性，完善异常容错
5. 完整用户账号体系：注册登录、资料修改、账户注销，表单校验齐全
6. 标准 MVVM + Repository 分层架构，代码解耦、易维护扩展
7. 全量用户配置持久化：主题、搜索记录、自动保存、登录状态本地存储
8. 人性化交互：删除笔记 / 分类 / 账户均提供二次确认弹窗，防止误操作

## 12. 未来改进方向
1. 多用户数据隔离：笔记、分类绑定 userId，支持多账号切换独立数据
2. 笔记导出功能：支持导出 TXT / JSON 文件
3. 本地定时通知：定时提醒记录灵感
4. 页面过渡动画：列表条目入场、退场交互动画
5. 名言本地缓存：无网络时展示离线缓存名言
6. 标签体系：笔记多标签标记、标签筛选
7. 本地图片支持：集成 CameraX 拍照、相册选取图片本地存储