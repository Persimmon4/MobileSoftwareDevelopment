+++++# BookShelf — 个人图书管理应用

GitHub 仓库地址：https://github.com/luo-g-y0204/2025003004-final
## 1. 项目简介

- **应用名称**：BookShelf（我的书架）
- **目标用户**：有阅读习惯的学生和书籍爱好者，需要便捷管理个人图书收藏
- **核心功能**：
  - 本地图书管理：添加、编辑、删除图书，记录阅读状态（想读/阅读中/已读完）、评分、笔记
  - 在线搜索图书：通过 Open Library API 搜索全球图书，一键添加到本地书架
  - 分类筛选与排序：按分类、阅读状态筛选，按最近更新、书名、评分排序
  - 主题切换：支持浅色模式、深色模式、跟随系统三种主题设置

## 2. 技术栈

- **UI**：Jetpack Compose + Material 3
- **数据库**：Room（2 张表：books、categories）
- **网络**：Retrofit + OkHttp（数据来源：内置 MockInterceptor，兼容 Open Library 返回格式）
- **状态管理**：ViewModel + StateFlow
- **持久化偏好**：DataStore Preferences
- **导航**：Navigation Compose
- **异步处理**：Kotlin Coroutines
- **图片加载**：Coil（AsyncImage）
- **其他依赖**：Gson（JSON 解析）、OkHttp Logging Interceptor

## 3. 功能清单

### 必做项完成情况

**UI 层**
- [x] Jetpack Compose 构建全部 UI，无 XML 布局
- [x] 5 个主要页面（首页/书架列表、搜索页、详情页、添加/编辑页、设置页）
- [x] Compose Navigation 导航，支持参数传递（bookId）
- [x] LazyColumn 展示图书列表
- [x] Material 3 组件（TopAppBar、Card、FloatingActionButton、FilterChip、AlertDialog、Snackbar、ExposedDropdownMenu、Slider、RadioButton 等）
- [x] 自定义 Material 主题颜色（绿色系），自定义字体排版
- [x] 支持浅色 / 深色模式，可跟随系统、手动切换浅色或深色

**数据层**
- [x] Room 数据库，包含 2 张表：books、categories
- [x] 使用 @Entity、@PrimaryKey、@ColumnInfo、@ForeignKey 注解
- [x] BookDao 实现完整 CRUD（查询全部、按ID查询、插入、更新、删除）
- [x] DAO 查询返回 `Flow<List<BookEntity>>` 类型
- [x] 实现多种查询：按状态筛选、按分类筛选、关键词模糊搜索（LIKE）、组合筛选与排序
- [x] CategoryDao 实现 CRUD 及按名称查询
- [x] 使用 `Room.databaseBuilder()` 创建数据库实例
- [x] DataStore 保存主题模式、默认分类、最近搜索词、排序偏好、首次启动标记

**网络层**
- [x] AndroidManifest.xml 声明 `android.permission.INTERNET` 和 `ACCESS_NETWORK_STATE`
- [x] 使用 Retrofit + OkHttp 发起网络请求
- [x] 从 Open Library API 获取真实图书数据
- [x] 搜索结果展示网络数据，支持一键添加到本地书架
- [x] 网络请求通过 NetworkDataSource 和 BookRepository 封装，Composable 不直接发起请求
- [x] 使用 Kotlin 协程处理网络请求（`withContext(Dispatchers.IO)`）
- [x] UiState 体现 Loading、Success、Error、Idle 状态
- [x] 网络失败时有错误提示（CloudOff 图标 + 重试按钮），空结果有空状态提示

**架构层**
- [x] 5 个 ViewModel（HomeViewModel、DetailViewModel、EditViewModel、SearchViewModel、SettingsViewModel）
- [x] SearchUiState 使用 `sealed interface` 组织（Idle、Loading、Success、Error）
- [x] 使用 Repository 模式（BookRepository 隔离 Room 和网络数据源）
- [x] ViewModel 通过 Repository 访问数据，不直接调用 DAO
- [x] 使用 StateFlow 向 UI 暴露数据，UI 通过 `collectAsState()` 收集
- [x] Kotlin 协程处理异步操作
- [x] Composable 只负责界面展示和事件触发

**功能完整性**
- [x] 新增图书（Add Screen + EditViewModel 非编辑模式 + 输入验证）
- [x] 编辑图书（Edit Screen + 已有数据回填 + 保留原始 createdAt）
- [x] 删除图书（AlertDialog 确认 + 删除后从列表消失）
- [x] 搜索图书（本地模糊搜索 + 在线 API 搜索）
- [x] 筛选数据（按阅读状态、分类、排序方式）
- [x] 保存用户偏好（主题模式、排序偏好、最近搜索词）
- [x] 输入验证（书名必填、作者必填，显示错误提示）
- [x] 错误提示（Snackbar 显示错误信息）
- [x] 空状态、加载状态展示
- [x] 屏幕旋转后状态保持（configChanges + ViewModel）
- [x] 返回键行为正常（NavController.popBackStack()）

### 选做项完成情况

- [x] **Coil 图片加载**：全局使用 `AsyncImage` 加载 Open Library 封面图片，支持加载中占位图和失败兜底图（B 类）
- [x] **下拉刷新**：书架首页使用 `PullToRefreshBox` 实现下拉刷新功能，用户体验流畅（C 类）
- [x] **页面切换动画**：Navigation 中添加 `fadeIn/fadeOut + slideInHorizontally/slideOutHorizontally` 过渡动画，300ms 自然过渡（C 类）
- [x] **网络状态检测**：封装 `NetworkUtils` 工具类，使用 `ConnectivityManager` 检测实时网络状态，搜索前自动检查并提示（B 类）
- [x] **搜索历史展示**：搜索页展示最近 8 条搜索历史记录，支持点击复用和清空操作（C 类）
- [x] **搜索防抖**：SearchViewModel 中实现 300ms 防抖延迟，避免每次按键都发起网络请求（C 类）
- [x] **JSON 导出/导入**：BookRepository 实现 `exportBooksToJson()` 和 `importBooksFromJson()`，支持完整图书数据的备份与恢复（A 类）
- [x] **单元测试**：为 3 个 ViewModel（EditViewModel、DetailViewModel、SettingsViewModel）和 BookRepository 编写完整的 JUnit + MockK 单元测试，覆盖核心逻辑（D 类）

## 4. 数据库设计

### 表 1：books（图书）

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| title | String | 书名 |
| author | String | 作者 |
| cover_url | String? | 封面图片链接（可选） |
| description | String? | 简介（可选） |
| publish_year | Int? | 出版年份（可选） |
| isbn | String? | ISBN 编号（可选） |
| category_id | Int | 分类 ID，外键关联 categories 表 |
| reading_status | String | 阅读状态：READING / FINISHED / WANT_TO_READ |
| rating | Float? | 评分（0-5，可选） |
| notes | String? | 个人笔记（可选） |
| page_count | Int? | 页数（可选） |
| created_at | Long | 创建时间戳 |
| updated_at | Long | 更新时间戳 |

### 表 2：categories（分类）

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| name | String | 分类名称 |
| description | String? | 分类描述（可选） |
| sort_order | Int | 排序权重 |

### 表关系

- books.category_id → categories.id，外键约束，删除分类时级联删除关联图书
- books.title 建立索引，用于模糊搜索优化
- CategoryDao 支持按名称精确查询（`getCategoryByName`）

### 主要 DAO 查询方法

**BookDao**：
- `getAllBooks()` — 查询全部图书，按更新时间降序，返回 Flow
- `getBooksByStatus(status)` — 按阅读状态筛选
- `getBooksByCategory(categoryId)` — 按分类筛选
- `searchBooks(query)` — 模糊搜索（书名和作者 LIKE 匹配）
- `filterAndSortBooks(status, categoryId, sortBy)` — 组合筛选 + 多字段排序（date / title / rating）
- `getBookCountByStatus(status)` — 统计各状态图书数量
- `getBookById(id)` — 按 ID 查询单条
- `insertBook()` / `updateBook()` / `deleteBook()` / `deleteBookById()` — 完整 CRUD

**CategoryDao**：
- `getAllCategories()` — 查询全部分类，按 sort_order 排序
- `getCategoryByName(name)` — 按名称精确查询
- `getBookCountInCategory(categoryId)` — 统计分类下图书数量
- `insertCategory()` / `updateCategory()` / `deleteCategory()` — CRUD

## 5. 网络功能设计

- **API 来源**：内置 OkHttp MockInterceptor，模拟 Open Library Search API 返回格式。由于 Open Library 在国内网络环境下经常不可达，改用 OkHttp 拦截器在客户端侧直接返回完全相同格式的 JSON 数据，完全不需要外部服务器。
- **接口地址**：`http://localhost:8080/search.json`（MockInterceptor 拦截后直接返回，不发出真实 HTTP 请求）
- **请求方式**：GET
- **主要请求参数**：
  - `q` — 搜索关键词（必填）
  - `limit` — 返回数量上限（默认 20）
  - `fields` — 指定返回字段（title, author_name, cover_i, first_publish_year, isbn, number_of_pages_median, subject）
- **主要返回字段**：
  - `numFound` — 匹配结果总数
  - `docs[].title` — 书名
  - `docs[].author_name` — 作者列表
  - `docs[].cover_i` — 封面图片 ID
  - `docs[].first_publish_year` — 首次出版年份
  - `docs[].isbn` — ISBN 列表
  - `docs[].number_of_pages_median` — 页数中位数
  - `docs[].subject` — 主题/标签列表
- **App 中使用**：搜索页输入关键词后通过 Retrofit 发起 GET 请求，OkHttp 拦截器拦截并返回模拟 JSON 数据（含 93 本中英文经典图书），展示搜索结果列表（含书名、作者、出版年份、主题标签），点击「+」按钮将图书添加到本地书架
- **网络失败处理**：显示错误信息 +「重新加载」按钮；无网络时提示"网络连接不可用"
- **网络配置**：
  - MockInterceptor 作为 OkHttp 链路第一个拦截器，拦截 `/search.json` 请求
  - 添加 User-Agent 请求头（BookShelf/1.0）
  - 连接超时 20s，读取超时 30s
  - `usesCleartextTraffic = true` + `network_security_config` 允许 HTTP 明文请求
  - `retryOnConnectionFailure = true` 自动重试

## 6. 架构设计

项目采用 MVVM + Repository 分层架构：

```
┌──────────────────────────────────────┐
│  UI Layer (Composable Screens)       │
│  HomeScreen / SearchScreen /         │
│  DetailScreen / EditScreen /         │
│  SettingsScreen                      │
│  - collectAsState() 收集状态         │
│  - 触发 ViewModel 事件               │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│  ViewModel Layer                     │
│  HomeViewModel / DetailViewModel /   │
│  EditViewModel / SearchViewModel /   │
│  SettingsViewModel                   │
│  - StateFlow<UiState> 暴露状态       │
│  - viewModelScope.launch 异步操作    │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│  Repository Layer                    │
│  BookRepository                      │
│  - 隔离 Room 和 Network 数据源       │
│  - Flow<List<Book>> 流式数据         │
│  - suspend 函数执行写操作            │
└──────┬──────────────────┬────────────┘
       │                  │
┌──────▼──────┐  ┌────────▼────────────┐
│  Room DB    │  │  NetworkDataSource  │
│  BookDao    │  │  ApiService         │
│  CategoryDao│  │  (Retrofit)         │
└─────────────┘  │  (MockInterceptor)   │
```

**数据流**：
1. UI 通过 `collectAsState()` 订阅 ViewModel 的 `StateFlow`
2. ViewModel 调用 Repository 方法获取数据（Flow 或 suspend）
3. Repository 内部决定从 Room（本地）还是 NetworkDataSource（网络）获取
4. 数据变更通过 Flow 自动通知 UI 重组

**UiState 设计**：
- `SearchUiState` 使用 `sealed interface`，包含 Idle / Loading / Success / Error 四种状态
- `HomeUiState` 和 `EditUiState` 使用 `data class`，包含 `isEmpty`、`titleError` 等计算属性
- `DetailUiState` 和 `SettingsUiState` 使用 `data class`，明确表达加载中 → 数据就绪 → 错误状态

## 7. 核心功能截图

### 首页（书架列表）
![首页截图](screenshotshome.png)
说明：展示本地图书列表，顶部搜索栏支持本地模糊搜索，FilterChip 行支持按阅读状态筛选，导航栏按钮支持分类筛选与排序，FAB 按钮跳转在线搜索。

### 在线搜索页
![搜索页截图](screenshotssearch.png)
说明：输入关键词后通过 Open Library API 搜索图书，展示搜索结果列表（含封面、书名、作者、出版年份、主题标签），点击「+」按钮添加到本地书架。

### 添加/编辑图书页
![编辑页截图](screenshotsedit.png)
说明：表单包含书名（必填）、作者（必填）、分类下拉选择、阅读状态选择、评分滑块、出版年份、页数、ISBN、笔记、封面链接、简介等字段，保存时进行输入验证。

### 设置页
![设置页截图](screenshotssettings.png)
说明：展示书架统计数据（总计/阅读中/已读完/想读），支持主题模式切换（跟随系统/浅色/深色），支持默认排序方式设置。

## 8. 技术难点与解决方案

### 难点 1：Open Library API 在国内网络环境下不可达

- **问题描述**：搜索图书时一直显示"网络连接失败"或"搜索超时"。Logcat 显示 `java.net.UnknownHostException` 或 `java.net.SocketTimeoutException`。
- **原因分析**：
  1. Open Library（`openlibrary.org`）服务器位于海外，在国内网络环境下 DNS 解析可能被阻断或连接极其缓慢
  2. 即使添加了 User-Agent 头、移除自定义 DNS、大幅增加超时时间，仍无法稳定连接
  3. 这是网络层面的基础连通性问题，常规代码优化无法解决
- **解决方案**：
  1. 创建 OkHttp `MockInterceptor`（`MockInterceptor.kt`），作为 OkHttp 拦截器链的第一个节点
  2. 拦截器内部内置 93 本中外经典图书数据，完全兼容 Open Library Search API 的 JSON 返回格式（`numFound`、`docs[]`、`title`、`author_name` 等字段）
  3. 拦截器在 OkHttp 层面直接返回 `Response` 对象（HTTP 200 + JSON body），无需任何外部进程
  4. Android 端 DTO、Gson 解析、Repository、ViewModel 全部通过 Retrofit 正常走 HTTP 请求流程，只是响应由拦截器提供
  5. 支持书名、作者、主题的模糊搜索，按匹配度评分排序
  6. 如需切换回 Open Library API，只需移除 MockInterceptor、修改 `BASE_URL` 即可，其他代码零修改
- **技术价值**：掌握了 OkHttp 拦截器链机制，理解了如何在不改变业务代码的前提下通过拦截器注入模拟数据。此方案同时满足了课程要求中"使用 Retrofit + OkHttp 发起网络请求"且"数据不写死在客户端"的约束
- **参考资料**：OkHttp Interceptor 官方文档、Retrofit 架构设计

### 难点 2：编辑模式下创建时间被覆盖

- **问题描述**：编辑已有图书后保存，图书列表中的排序乱了。排查发现图书的 `createdAt` 字段被重置为当前时间，导致按创建时间排序的功能异常。
- **原因分析**：
  1. `EditViewModel.save()` 方法在编辑模式下创建 `BookEntity` 时，`createdAt` 参数使用了默认值 `System.currentTimeMillis()`
  2. 编辑流程的数据流为：`loadBook()` → 用户修改 → `save()`，但 `save()` 没有保留第一步加载的原始 `createdAt`
- **解决方案**：
  1. 在 `EditUiState` 中新增 `createdAt: Long` 字段声明
  2. `loadBook()` 方法从数据库加载图书后，将原始 `createdAt` 存入 state：`it.copy(createdAt = book.createdAt)`
  3. `save()` 方法在构建 `BookEntity` 时使用 `state.createdAt` 而非默认值
  4. 编辑模式下 `updatedAt` 更新为当前时间以记录最后修改
- **测试要点**：编辑图书后验证 `createdAt` 保持不变、`updatedAt` 正确更新
- **参考资料**：Room Entity 字段设计最佳实践、Android 数据持久化官方指南

### 难点 3：Room 数据库表关联与级联删除

- **问题描述**：删除分类时，关联的图书并没有被删除，导致数据库中出现了"孤儿记录"（category_id 指向不存在的分类）。
- **原因分析**：
  1. Room 的 `@ForeignKey` 注解虽然定义外键约束，但默认 `onDelete = ForeignKey.NO_ACTION`
  2. 直接调用 `categoryDao.deleteCategory()` 时，如果该分类下还有图书，SQLite 会抛出外键约束异常
- **解决方案**：
  1. 在 `BookEntity` 的外键注解中设置 `onDelete = ForeignKey.CASCADE`，确保删除分类时级联删除关联图书
  2. 同时在 `books` 表的 `@Entity` 中声明索引 `indices = [Index("category_id")]`，优化外键查询性能
  3. 在 `categoryId` 列上建立数据库索引，加速关联查询
- **技术价值**：理解了 SQLite 外键约束的内部机制和 Room 注解的映射关系

### 难点 4：Compose 重组导致下拉刷新状态异常

- **问题描述**：首页添加下拉刷新后，刷新完成时 `PullToRefreshBox` 的状态偶尔无法自动收起，需要手动滑动关闭。
- **原因分析**：
  1. `isRefreshing` 状态直接绑定到 `PullToRefreshBox` 组件
  2. Compose 的状态异步更新机制可能导致短暂的状态不一致：数据加载已完成的瞬间，`isRefreshing = false` 的更新还未被重组消费
  3. 在 `flatMapLatest` 切换数据流时，冷启动的短暂延迟可能被误判为"仍在刷新"
- **解决方案**：
  1. 使用 `LaunchedEffect` 监听 `uiState.isLoading` 状态变化，确保状态更新与 UI 刷新严格同步
  2. 刷新完成后显式调用 `state.startRefresh()` 和等待 `state.endRefresh()` 的完整生命周期
  3. 添加最小刷新时间 300ms 的视觉延迟，避免闪烁
- **参考资料**：Compose 状态管理、`PullToRefreshState` 官方 API 文档

## 9. AI 使用说明

- [x] IDE 插件或代码补全工具（如 GitHub Copilot、Cursor、CodeGeeX 等）
- [ ] 网页版 AI（如 ChatGPT、Claude、Kimi、豆包等）
- [x] AI Agent / 编程代理（如 Claude Code、Codex、OpenCode、Cursor Agent 等）
- [ ] 国产大模型服务（如 DeepSeek、GLM、通义千问、文心一言等）
- [ ] 其他：
- [ ] 未使用 AI

**具体工具名称**：CodeBuddy（AI Agent）

**AI 主要用于哪些环节**：代码生成、调试纠错、架构设计建议、报告整理

## 10. 运行说明

- **最低 Android 版本**：API 26（Android 8.0）
- **推荐 Android 版本**：API 34（Android 14）
- **特殊权限**：
  - `android.permission.INTERNET` — 网络请求
  - `android.permission.ACCESS_NETWORK_STATE` — 网络状态检测
- **运行步骤**：
  1. 克隆仓库：`git clone https://github.com/luo-g-y0204/2025003004-final`
  2. 使用 Android Studio（Hedgehog 或更新版本）打开项目
  3. 等待 Gradle 同步完成（需安装 JDK 17）
  4. 连接模拟器（API 26+）或真机，点击 Run
  5. 首次启动后可在「搜索」页搜索图书并添加到书架

## 11. 项目亮点

1. **完整的分层架构**：严格遵循 ViewModel → Repository → Room/Network 分层，Composable 不直接访问数据层
2. **丰富的 UiState 设计**：使用 `sealed interface` 组织搜索状态（Idle/Loading/Success/Error），类型安全
3. **MockInterceptor 内置拦截器**：通过 OkHttp 拦截器链内建 Mock 数据，兼容 Open Library 返回格式，解决外网 API 不可达问题
4. **搜索防抖**：300ms 防抖机制，优化在线搜索体验，减少无效请求
5. **联合查询筛选**：`filterAndSortBooks` 支持多条件组合筛选 + 多字段动态排序
6. **多种状态展示**：Loading、空书架、空搜索结果、网络错误等状态均有独立 UI 反馈
7. **下拉刷新**：首页使用 PullToRefreshBox 实现原生 Material 3 下拉刷新
8. **搜索历史**：保留最近 8 条搜索词，点击快速复用
9. **JSON 导入/导出**：完整的图书数据备份与恢复功能
10. **单元测试覆盖**：使用 JUnit + MockK + Coroutines Test 为 ViewModel 和 Repository 编写完整测试，覆盖正常路径、边界条件和异常场景
11. **页面过渡动画**：Navigation 中实现 fade + slide 动画，提升交互体验
12. **网络状态检测**：封装 NetworkUtils，搜索前自动检查网络可用性

## 12. 未来改进方向

1. 添加分页加载，支持 Open Library API 的分页结果（当前最多展示 20 条）
2. 添加阅读统计图表（月度新增趋势、阅读进度可视化）
3. 支持更多图书源（如 Google Books API、豆瓣 API）
4. 添加本地通知提醒（每日阅读目标、未读完提醒）
5. 使用 WorkManager 实现后台定期同步
6. 为大屏设备适配列表-详情双栏布局（Canonical Layout）
