# 星音收藏

GitHub 仓库地址：https://github.com/gmhjs/2025003027-musiccollect

## 1\. 项目简介

- **应用名称**：星音收藏
- **目标用户**：喜爱音乐、希望收藏和管理个人歌单的 Android 用户，尤其适合从网络热歌挑选曲目、分类整理本地歌单的音乐爱好者。
  
- **核心功能**：
    - 用户登录、个人信息与密码管理
    - 歌单创建、删除、名称搜索、自定义排序
    - 网络热歌接口拉取，一键收藏至本地任意歌单
    - 歌单内歌曲增删、播放、收藏标记、播放记录留存
    - 按歌手聚合所有收藏曲目，独立歌手详情页面
    - 模拟播放器，歌词跟随播放进度滚动高亮
    - 浅色 / 深色主题切换、系统偏好持久存储

## 2\. 技术栈

- **UI**：Jetpack Compose \+ Material 3
- **本地数据库**：Room（SQLite）
- **网络请求**：Retrofit \+ OkHttp（API 来源：api\.apiopen\.top）
- **状态管理**：ViewModel \+ StateFlow
- 轻量持久化：DataStore Preferences
- 页面导航：Navigation Compose
- 异步处理：Kotlin Coroutines
- 图片加载：Coil
- 配套工具：Activity 相册选择、Gson 数据解析

## 3\. 功能清单

### 必做项完成情况

#### UI 层
* [√] 全部页面使用 Jetpack Compose，无 XML 布局
* [√] 多页面：登录页、首页、歌单详情、热歌页、播放页、歌手页、设置、个人中心
* [√] Compose Navigation 页面路由与参数传递
* [√] LazyColumn 展示歌单、歌曲、歌手列表
* [√] Material3 全套组件：TopAppBar、Card、弹窗、输入框、开关、悬浮按钮
* [√] 自定义星紫主题，深浅双模式一键切换

#### 数据层
* [√] Room 双数据表：Playlist 歌单表、Song 歌曲表
* [√] 两张实体完整增删改查 CRUD
* [√] DAO 查询统一返回 Flow，自动监听数据库变更
* [√] 多类检索：歌单搜索、歌单内歌曲检索、歌手聚合查询
* [√] DataStore 存储登录信息、深色模式、头像路径、用户密码

#### 网络层
* [√] AndroidManifest 声明 INTERNET 网络权限
* [√] Retrofit 请求公开音乐 Mock 接口
* [√] 热歌页面展示线上数据，支持收藏入库
* [√] UiState 区分加载 / 成功 / 空数据 / 错误四种页面状态
* [√] 网络逻辑封装 Repository，Compose 不直接发起请求

#### 架构层
* [√] ViewModel 统一管理全局 UI 状态
* [√] Repository 分层隔离网络、本地数据源
* [√] StateFlow 管理页面可观察状态
* [√] 协程处理 IO、数据库、网络异步任务
* [√] 密封类 UiState 标准化页面状态
* [√] 界面仅做渲染，无数据库 / 网络直接调用

#### 功能完整性
* [√] 新增 / 删除 / 搜索歌单、歌曲完整操作
* [√] 登录、新建歌单输入非空校验，错误文字提示
* [√] 加载 / 空数据 / 网络错误专属占位页面
* [√] 屏幕旋转后数据、页面状态不丢失

### 选做项完成情况
* [√] Room 复杂聚合查询（去重歌手、最近播放筛选）
* [√] 歌单按时间 / 名称内存排序
* [√] 播放器歌词进度联动滚动高亮
* [√] 相册选取头像并本地缓存持久化
* [√] 用户密码修改功能
* [ ] 搜索防抖、搜索历史存储

## 4\. 数据库设计

### 表 1：playlist 歌单表
|字段名|类型|说明|
|---|---|---|
|id|Int|主键，自增|
|name|String|歌单名称|
|desc|String|歌单描述|
|createTime|Long|创建时间戳|
|索引：createTime；一对多关联 song 表|||

### 表 2：song 歌曲表
|字段名|类型|说明|
|---|---|---|
|songId|Int|主键，自增|
|playlistId|Int|外键，绑定所属歌单 id|
|songName|String|歌曲名称|
|singer|String|歌手名|
|coverUrl|String|封面网络地址|
|songUrl|String|播放资源链接|
|duration|Long|歌曲总时长|
|isFavorite|Boolean|是否收藏标记|
|addTime|Long|加入歌单时间|
|lastPlayTime|Long|上次播放时间|
|索引：playlistId、lastPlayTime|||

### 核心 DAO 查询方法
1. `getAllPlaylist()`：获取全部歌单，创建时间倒序
2. `searchPlaylist(key)`：歌单名称模糊检索
3. `getSongByPlaylist(pid)`：查询指定歌单所有歌曲
4. `searchSongsInPlaylist(pid, keyword)`：歌单内搜歌名 / 歌手
5. `getFavoriteSongs()`：筛选所有收藏歌曲
6. `getRecentPlaySongs()`：获取最近播放曲目
7. `getDistinctSingers()`：去重获取全部歌手名称
8. `getSongsBySinger(singerName)`：查询指定歌手所有歌曲
9. `updateLastPlayTime(songId, time)`：更新播放记录时间

## 5\. 网络功能设计
- API 来源：api\.apiopen\.top 免费音乐接口
- 接口地址：`https://api.apiopen.top/music/hot?limit=20`
- 请求方式：GET
- 返回结构：外层 MusicResponse 包裹 data 数组，单条 MusicDto 包含 id、name、artist、picUrl、url
- 使用页面：HotMusic 全网热歌页面
- 业务逻辑：展示线上歌曲，点击收藏存入本地 Room；点击卡片跳转播放页
- 异常处理：网络 404 / 断网 / 超时自动切换内置 Mock 歌曲，同时弹出错误提示，保证页面不会空白崩溃

## 6\. 架构设计
整体 MVVM 分层结构：
1. **UI 层**：所有 Screen 页面仅渲染界面、转发点击事件，无业务、IO 逻辑
2. **ViewModel 层**：持有所有 StateFlow 状态，封装增删改查、网络、主题切换业务，使用 viewModelScope 协程执行异步
3. **Repository 层**：NetworkRepository 统一封装 Retrofit，处理异常兜底；Room、DataStore 作为本地数据源直接供 ViewModel 调用
4. **数据层**：Room 实体、DAO；DataStore 键值存储；网络 Dto 实体
数据流：用户交互触发页面回调 → ViewModel 方法执行操作 → 更新 StateFlow 状态 → Compose 页面自动重组刷新

## 7\. 核心功能截图

### 登录页
"screenshots\login.png"
说明：应用星紫渐变背景，Logo 图标，用户名密码输入框，登录加载状态与错误提示

### 我的歌单（首页）
"screenshots\home.png"
说明：展示全部歌单卡片，顶部搜索，底部导航切换功能入口，右下角新建歌单

### 歌单详情页
"screenshots\detail.png"
说明：展示当前歌单内所有歌曲，支持删除单曲，悬浮按钮跳转全网热歌添加曲目

### 全网热歌页
"screenshots\hot_music.png"
说明：加载线上音乐列表，下拉刷新，点击收藏可选择目标歌单保存

### 个人中心
"screenshots\profile_center.png"
说明：展示头像、账号信息，支持更换头像、修改密码、切换深色模式

## 8\. 技术难点与解决方案

### 难点 1：Room 新增 lastPlayTime 字段，数据库升级不丢失数据
- 问题：初始版本无播放时间字段，直接重建会清空测试数据
- 原因：默认 destructive 迁移策略会删除数据表
- 解决方案：数据库版本升级至 2，正式环境编写 ALTER 迁移 SQL；开发测试环境使用快速销毁策略迭代

### 难点 2：播放进度与歌词同步滚动高亮
- 问题：播放进度实时变化，需要定位对应歌词并自动滚动到可视区域
- 解决方案：封装 LRC 歌词实体，进度协程实时匹配时间戳，调用 LazyList 滚动方法，动态修改当前行字体、颜色

### 难点 3 相册头像选择持久化
- 问题：相册 Uri 重启后权限失效，图片无法加载
- 解决方案：选中图片复制到应用私有缓存目录，保存本地文件路径至 DataStore，每次启动读取本地文件渲染头像

## 9\. AI 使用说明
* [√] 网页版国产大模型（豆包、DeepSeek）
* [ ] AI 编程代理
* [ ] IDE 代码补全插件 GitHub Copilot
* [ ] 其他工具
具体工具：DeepSeek、豆包网页端、GitHub 
AI 使用环节：项目功能规划、bug 调试、项目报告撰写优化

## 10\. 运行说明
- 最低 Android 版本：API 24（Android7\.0）
- 推荐 Android 版本：API 34（Android14）
- 所需权限：INTERNET（网络请求、加载封面）、外部存储（相册头像）
- 运行步骤：
1. 克隆项目 `git clone https://github.com/gmhjs/2025003027-musiccollect`
2. Android Studio 打开项目，等待 Gradle 同步依赖
3. 配置本地 SDK 路径，连接模拟器 / 真机
4. 点击 Run 编译安装应用
5. 登录页输入任意账号密码即可进入

## 11\. 项目亮点
1. 完整业务闭环：线上热歌收藏 → 本地歌单管理 → 播放记录留存
2. 完整个性化模块：头像更换、密码修改、深浅色主题
3. 模拟播放器实现歌词联动滚动，具备真实播放软件交互体验
4. 线上接口失效自动兜底 Mock 数据，断网场景无空白页面
5. 标准 MVVM 分层，代码解耦，便于扩展维护
6. 统一 Loading/Empty/Error 全局占位组件，视觉统一美观

## 12\. 未来改进方向
1. 接入真实音乐播放 MediaSession，实现后台播放、通知栏控制
2. 新增歌单封面自定义、歌单导入导出备份
3. 实现播放队列、随机 / 循环播放模式
4. 增加搜索防抖、搜索历史记录功能


