# 图书收藏 (BookCollector) —— Android 应用实验报告

**姓名**：吴仪  
**学号**：2025003016  

---

## 1. GitHub 仓库地址

```
https://github.com/Wuyi-star/2025003016-FinalProject
```

---

## 2. App 名称、选题背景和目标用户

### App 名称

**图书收藏（BookCollector）**

### 选题背景

在日常生活和学习中，阅读是获取知识、拓展视野的重要途径。很多读者有记录和管理读书进度的需求——比如标记想读的书、记录正在读的书、给读过的书打分并写笔记。然而，当前市面上的读书管理软件多为商业产品，功能臃肿或需要注册账户。

本选题属于「内容管理类应用」方向，旨在开发一款轻量级、离线可用的个人图书收藏管理工具，让用户能够方便地管理自己的阅读生活。

### 目标用户

- 在校学生：管理课外阅读书单，记录读书笔记
- 普通阅读爱好者：收藏想读的图书，跟踪阅读进度
- 任何希望通过手机管理个人书库的用户

---

## 3. 核心功能说明

本应用围绕图书收藏管理场景，实现了以下核心功能：

| 序号 | 功能 | 说明 |
|------|------|------|
| 1 | **本地图书收藏** | 预置 20 本经典图书（三体、红楼梦、百年孤独等），启动时自动导入 Room 数据库，首页使用 LazyColumn 展示图书列表 |
| 2 | **在线搜索添加** | 通过 OpenLibrary API 在线搜索图书，搜索结果可一键添加到本地收藏，自动去重 |
| 3 | **删除图书** | 在详情页提供删除按钮，AlertDialog 确认后从 Room 数据库永久删除 |
| 4 | **本地搜索** | 支持按书名/作者模糊搜索本地收藏的图书，输入关键词实时匹配 |
| 5 | **状态筛选** | FilterChip 行支持按"全部/想读/在读/已读"四种状态筛选图书 |
| 6 | **收藏切换** | 对任意图书标记/取消收藏，收藏图书单独视图展示，TopAppBar 按钮快速切换 |
| 7 | **图书详情** | 展示封面、作者、出版社、ISBN、页数、评分、阅读状态和笔记，支持修改 |
| 8 | **用户偏好** | DataStore 保存深色模式偏好和最近搜索关键词，重启后自动恢复 |

---

## 4. 页面结构说明

本应用包含 **2 个主要页面**，通过 Compose Navigation 实现页面跳转：

### 页面 1：图书列表首页（BookListScreen）

- **TopAppBar**：显示"图书收藏"标题，右侧提供收藏视图切换按钮和搜索栏展开按钮
- **搜索栏**：点击搜索按钮展开，支持输入关键词实时搜索本地图书，点击搜索图标同时搜索 OpenLibrary API
- **状态筛选栏**：FilterChip 行展示"全部/想读/在读/已读"四个筛选标签，选中后过滤本地图书
- **内容区域**：根据当前模式自动切换三种视图：
  - **本地图书列表**：展示筛选后的图书卡片，点击跳转详情页
  - **收藏视图**：展示所有已收藏的图书
  - **网络搜索结果**：展示 OpenLibrary 返回的图书，点击添加到本地
- **FAB**：在线搜索模式下显示主页按钮，点击返回本地列表
- **Snackbar**：显示错误提示和操作反馈

### 页面 2：图书详情页（BookDetailScreen）

- **TopAppBar**：显示"图书详情"标题，左侧返回按钮，右侧删除按钮
- **封面区域**：使用 Coil 异步加载网络封面图片，无封面时显示默认图标
- **信息卡片**：展示书名、作者、出版社、出版年份、ISBN、页数
- **阅读状态卡片**：FilterChip 切换"想读/在读/已读"三种状态
- **评分卡片**：Slider 滑块设置 0-5 分评分，右侧实时显示分值
- **笔记卡片**：支持查看和编辑读书笔记，使用 OutlinedTextField 输入
- **简介卡片**：展示图书简介（如有）
- **删除确认**：AlertDialog 弹出确认删除，防止误操作

### 导航关系

```
图书列表首页 ──(点击图书)──▶ 图书详情页
图书详情页 ──(返回按钮)──▶ 图书列表首页
图书详情页 ──(删除确认)──▶ 返回列表首页
```

---

## 5. 技术栈说明

| 技术 | 版本 | 用途 |
|------|------|------|
| **Kotlin** | 1.9.20 | 开发语言 |
| **Jetpack Compose** | BOM 2023.08.00 | 声明式 UI 框架，100% Compose 构建界面 |
| **Material 3** | — | UI 组件库（Card、TopAppBar、FAB、FilterChip、Slider、AlertDialog、Snackbar、AssistChip、CircularProgressIndicator 等） |
| **Room** | 2.5.2 | 本地 SQLite 数据库，管理图书和搜索历史数据 |
| **DataStore Preferences** | 1.0.0 | 轻量级键值存储，保存用户偏好设置 |
| **Navigation Compose** | 2.6.0 | 页面路由与导航 |
| **ViewModel** | 2.6.2 | 管理 UI 状态与业务逻辑 |
| **StateFlow** | — | 向 UI 暴露响应式数据流 |
| **Retrofit** | 2.9.0 | 网络请求框架 |
| **OkHttp** | 4.12.0 | HTTP 客户端，配置日志拦截器和超时 |
| **Gson** | — | JSON 解析 |
| **Coil** | 2.5.0 | 网络图片异步加载 |
| **Kotlin Coroutines** | 1.7.3 | 异步编程，处理数据库和网络操作 |
| **KSP** | 1.9.20-1.0.14 | Room 编译期注解处理 |

---

## 6. Room 数据库设计
### 基本配置

- 数据库类：`AppDatabase`
- 数据库文件：`book_collector_database`
- 当前版本：**version = 2**
- 使用 `Room.databaseBuilder()` 创建单例实例

### Entity 1：BookEntity（books 表）

存储用户收藏的图书信息，共 16 个字段：

| 字段名 | 类型 | 说明 | 注解 |
|--------|------|------|------|
| `id` | Long | 主键，自增 | `@PrimaryKey(autoGenerate = true)` |
| `title` | String | 书名 | |
| `author` | String | 作者 | |
| `description` | String | 简介/描述 | |
| `coverUrl` | String | 封面图片 URL | `@ColumnInfo(name = "cover_url")` |
| `publishYear` | String | 出版年份 | `@ColumnInfo(name = "publish_year")` |
| `publisher` | String | 出版社 | |
| `isbn` | String | ISBN 编号 | |
| `pageCount` | Int | 页数 | `@ColumnInfo(name = "page_count")` |
| `rating` | Float | 用户评分（0-5） | |
| `notes` | String | 读书笔记 | |
| `readingStatus` | String | 阅读状态：want_to_read / reading / finished | `@ColumnInfo(name = "reading_status")` |
| `isFavorite` | Boolean | 是否收藏 | `@ColumnInfo(name = "is_favorite")` |
| `createdAt` | Long | 创建时间戳 | `@ColumnInfo(name = "created_at")` |
| `updatedAt` | Long | 更新时间戳 | `@ColumnInfo(name = "updated_at")` |

### Entity 2：SearchHistoryEntity（search_history 表）

存储用户搜索历史记录，共 3 个字段：

| 字段名 | 类型 | 说明 | 注解 |
|--------|------|------|------|
| `id` | Long | 主键，自增 | `@PrimaryKey(autoGenerate = true)` |
| `query` | String | 搜索关键词 | |
| `searchedAt` | Long | 搜索时间戳 | `@ColumnInfo(name = "searched_at")` |

### 表关系

两张表为独立表，无外键关联。搜索历史表独立存储搜索关键词，图书表通过 `isFavorite` 字段标记收藏状态。查询时通过以下方式关联：
- 收藏查询：`SELECT * FROM books WHERE is_favorite = 1 ORDER BY updated_at DESC`

### 主要 DAO 查询

**BookDao**（10 个方法，全部返回 Flow 或 suspend 函数）：

| 方法 | 返回类型 | 功能 |
|------|----------|------|
| `getAllBooks()` | `Flow<List<BookEntity>>` | 按更新时间倒序获取全部图书 |
| `getBookById(id)` | `BookEntity?` | 根据 ID 查询单本图书 |
| `getBooksByStatus(status)` | `Flow<List<BookEntity>>` | 按阅读状态筛选 |
| `searchBooks(query)` | `Flow<List<BookEntity>>` | 模糊搜索书名或作者（`LIKE '%query%'`） |
| `insertBook(book)` | `Long` | 插入图书，冲突时 REPLACE |
| `updateBook(book)` | — | 更新图书信息 |
| `deleteBook(book)` | — | 删除图书 |
| `deleteBookById(id)` | — | 根据 ID 删除 |
| `getBookCount()` | `Int` | 获取图书总数 |
| `getFavoriteBooks()` | `Flow<List<BookEntity>>` | 获取收藏图书 |
| `updateFavoriteStatus(id, isFavorite)` | — | 切换收藏状态 |

**SearchHistoryDao**（4 个方法）：

| 方法 | 返回类型 | 功能 |
|------|----------|------|
| `getAllHistory()` | `Flow<List<SearchHistoryEntity>>` | 按时间倒序获取最近 20 条历史 |
| `insertHistory(history)` | — | 插入搜索记录，REPLACE 策略 |
| `clearAllHistory()` | — | 清空全部历史 |
| `searchHistory(query)` | `Flow<List<SearchHistoryEntity>>` | 模糊搜索历史记录 |

---

## 7. DataStore 保存了什么数据

### 存储内容

使用 `UserPreferencesRepository` 封装 DataStore Preferences，保存 3 个键值：

| 键名 | 类型 | 内容 | 使用场景 |
|------|------|------|----------|
| `dark_mode` | Boolean | 深色模式开关 | 写入：用户切换主题时；读取：应用启动时设置主题 |
| `default_reading_status` | String | 默认阅读状态 | 写入：用户修改默认状态时；读取：新增图书时作为默认状态 |
| `last_search` | String | 最近搜索关键词 | 写入：每次在线搜索时保存；读取：应用启动时回填搜索框 |

### 读写时机

- **读**：以 `Flow<Boolean>` / `Flow<String>` 形式暴露，ViewModel 初始化时 `collect` 读取，UI 展示偏好值
- **写**：用户操作触发时，通过 `suspend` 函数异步写入，使用 `edit {}` 事务确保原子性
- **作用**：实现搜索关键词跨会话恢复、主题偏好持久化，提升用户体验

---

## 8. 网络功能设计

### API 来源

- **API 名称**：OpenLibrary Search API
- **API 类型**：公开免费 API，无需 API Key
- **Base URL**：`https://openlibrary.org/`
- **接口地址**：`GET https://openlibrary.org/search.json`
- **请求参数**：
  - `q`（String）：搜索关键词
  - `limit`（Int，默认 20）：返回结果数量
- **图片地址**：`https://covers.openlibrary.org/b/id/{coverId}-M.jpg`

### 主要返回字段

| 字段 | 类型 | 在 App 中的用途 |
|------|------|----------------|
| `docs[].title` | String | 显示图书书名 |
| `docs[].author_name` | List\<String\> | 显示图书作者 |
| `docs[].first_publish_year` | Int | 显示出版年份 |
| `docs[].publisher` | List\<String\> | 显示出版社 |
| `docs[].isbn` | List\<String\> | 显示 ISBN |
| `docs[].number_of_pages_median` | Int | 显示页数 |
| `docs[].cover_i` | Long | 拼接封面图片 URL |
| `docs[].subject` | List\<String\> | 展示图书简介/主题 |
| `docs[].ratings_average` | Double | 展示初始评分参考 |

### 网络层架构

```
UI (Composable)  ←  ViewModel  ←  Repository  ←  Retrofit (ApiService)
                                                          ↓
                                                    OkHttp Client
                                                          ↓
                                                   OpenLibrary API
```

- Retrofit 接口定义在 `ApiService.kt`，Repository 调用 `RetrofitClient.apiService.searchBooks()`
- OkHttp 配置日志拦截器（BASIC 级别）+ 15 秒连接/读写超时
- Gson 解析 JSON 到 `OpenLibrarySearchResponse` → `OpenLibraryDoc` DTO
- Repository 将 DTO 转换为 `BookModel` 返回 ViewModel
- 网络请求使用 `kotlinx.coroutines` 协程，返回 `Result<List<BookModel>>`
- ViewModel 根据 Result 更新 Loading / Success / Error 状态

### 网络数据在 App 中的用途

- 在线搜索功能：用户在搜索框中输入关键词，获取 OpenLibrary 数据库中的匹配图书
- 添加到本地收藏：网络搜索结果可一键保存到 Room 数据库
- 封面图片展示：使用 Coil 加载 OpenLibrary 封面图片

---

## 9. ViewModel 和 UiState 的设计说明

### BookListUiState

使用 **data class** 组织列表页所有 UI 状态，共 9 个字段：

| 字段 | 类型 | 含义 |
|------|------|------|
| `localBooks` | `List<BookModel>` | 本地图书列表（受筛选影响） |
| `favoriteBooks` | `List<BookModel>` | 收藏图书列表 |
| `searchOnlineBooks` | `List<BookModel>` | 网络搜索结果 |
| `isLoading` | Boolean | 是否正在加载（如网络搜索中） |
| `isSearching` | Boolean | 是否正在本地搜索 |
| `errorMessage` | String? | 错误提示（null 表示无错误） |
| `searchQuery` | String | 当前搜索框输入文本 |
| `selectedStatus` | String | 当前状态筛选（"all"/"want_to_read"/"reading"/"finished"） |
| `isOnlineSearch` | Boolean | 是否处于在线搜索视图模式 |
| `showFavorites` | Boolean | 是否处于收藏视图模式 |

### BookListViewModel

- 继承 `AndroidViewModel`，持有 `BookRepository` 和 `UserPreferencesRepository`
- 通过 `MutableStateFlow<BookListUiState>` 管理状态，对外暴露 `StateFlow`
- **init 时**：自动加载本地图书、收藏图书、搜索历史；首次启动时插入 Mock 数据
- **核心方法**：`searchLocal()`、`searchOnline()`、`addBookFromSearch()`、`toggleFavorite()`、`loadAllBooks()`、`deleteBook()`
- **关键逻辑**：`searchOnline()` 同时触发本地搜索和网络搜索；`addBookFromSearch()` 带去重检查
- 所有数据库和网络操作放入 `viewModelScope` 协程中

### BookDetailUiState

| 字段 | 类型 | 含义 |
|------|------|------|
| `book` | `BookModel?` | 图书详情数据（null 表示未加载或已删除） |
| `isLoading` | Boolean | 是否正在加载 |
| `errorMessage` | String? | 错误信息 |
| `isSaved` | Boolean | 图书是否在本地收藏中 |

### BookDetailViewModel

- 根据图书 ID 加载详情数据
- 提供 `updateReadingStatus()`、`updateRating()`、`updateBookNotes()`、`deleteBook()` 等方法

### 状态设计特点

- 全部状态集中在单一 data class，避免状态分散
- `errorMessage` 为空字符串或 null 时表示无错误，UI 通过 Snackbar 展示
- 通过 `isOnlineSearch` / `showFavorites` 控制内容区域视图切换

---

## 10. Repository 如何隔离本地数据和网络数据

### 架构设计

`BookRepository` 作为唯一数据访问入口，内部持有 `BookDao` 和 `SearchHistoryDao`，对外屏蔽底层数据来源细节：

```
┌─────────────────────────────────┐
│       BookRepository            │
│  ┌───────────┐ ┌─────────────┐  │
│  │ BookDao   │ │Retrofit     │  │
│  │(本地Room) │ │(远程API)    │  │
│  └───────────┘ └─────────────┘  │
│         ↓             ↓          │
│   BookEntity    OpenLibraryDoc   │
│         ↓             ↓          │
│     统一转换为 BookModel         │
└─────────────────────────────────┘
```

### 隔离方式

- **本地操作**：调用 BookDao / SearchHistoryDao 方法，使用 Entity ↔ Model 转换
- **网络操作**：调用 `RetrofitClient.apiService.searchBooks()`，使用 DTO → Model 转换
- **统一出口**：所有方法返回 `BookModel`（而非 Entity 或 DTO），ViewModel 只与 Model 和 Repository 交互
- **错误隔离**：网络请求使用 `try-catch` 包装为 `Result<T>`，网络异常不传播到 ViewModel 之外
- **转换方法**：Repository 内部提供 `BookEntity.toBookModel()`、`BookModel.toEntity()`、`OpenLibraryDoc.toBookModel()` 三个转换函数

### 为何这样设计

- ViewModel 不需要知道数据来自本地数据库还是远程 API
- 如果将来更换 API 或数据库结构，只需修改 Repository 内部逻辑
- 转换逻辑集中管理，避免散落在 ViewModel 或 Composable 中

---

## 11. 应用运行截图

### 截图一：图书收藏首页
展示已收藏图书列表、阅读状态标签、收藏按钮

![图书收藏首页](./screenshots/collect.png)

### 截图二：在线搜索图书
展示搜索框输入关键词后本地数据库匹配结果

![在线搜索图书](./screenshots/search.png)

### 截图三：图书详情页
展示封面、信息卡片、阅读状态、评分滑块、笔记展示

![图书详情页](./screenshots/detail.png)

### 截图四：笔记编辑
展示笔记编辑输入框、保存按钮

![笔记编辑](./screenshots/edit.png)

### 截图五：删除确认弹窗
展示删除图书前的确认对话框

![删除确认弹窗](./screenshots/delete.png)

---

## 12. 遇到的问题与解决过程

### 问题一：在线搜索"傲慢与偏见"搜不出结果，但图书列表中已有该书

**现象**：本地预置了《傲慢与偏见》，在搜索框输入"傲慢与偏见"并点击搜索按钮后， OpenLibrary API 返回的结果中没有找到该书，用户无法确认该图书是否已在本地收藏。

**原因分析**：搜索按钮仅调用 `searchOnline()` 方法，该方法只向 OpenLibrary API 发起网络请求，没有同时搜索本地 Room 数据库。用户期望搜索本地图书时直接展示本地结果。

**解决方案**：
- 修改搜索逻辑：搜索按钮改为同时调用 `searchLocal()` 和 `searchOnline()`
- 输入框文字变化时使用 `onQueryChange` 实时触发本地搜索
- 搜索结果中同时展示本地匹配和网络匹配结果

### 问题二：点击 FAB 主页按钮不会返回主界面

**现象**：在在线搜索模式下右下角会显示一个主页图标 FAB，点击后页面没有变化，仍停留在网络搜索结果页面。

**原因分析**：FAB 的 onClick 调用 `loadAllBooks()` 方法，该方法的 `_uiState.update` 中只重置了 `searchQuery`、`selectedStatus`、`errorMessage` 三个字段，没有重置 `isOnlineSearch` 和 `showFavorites` 状态标志，导致 UI 仍渲染网络搜索结果。

**解决方案**：在 `loadAllBooks()` 的 `_uiState.update` 中增加 `isOnlineSearch = false` 和 `showFavorites = false` 的状态重置。

### 问题三：数据库升级导致用户收藏数据丢失

**现象**：开发过程中需要为 books 表新增 `is_favorite` 字段和新建 search_history 表，数据库版本从 1 升级到 2 后，之前的图书收藏数据全部丢失。

**原因分析**：AppDatabase 使用了 `fallbackToDestructiveMigration()` 配置，Room 在版本变更时会丢弃旧数据库直接重建，导致已有图书数据丢失。

**解决方案**：
- 编写 `MIGRATION_1_2` 迁移对象，手动编写 SQL：
  - `CREATE TABLE IF NOT EXISTS search_history (...)` 用于新建搜索历史表
  - `ALTER TABLE books ADD COLUMN is_favorite INTEGER NOT NULL DEFAULT 0` 用于新增收藏字段
- 将 `fallbackToDestructiveMigration()` 替换为 `.addMigrations(MIGRATION_1_2)`
- 使用 `DEFAULT 0` 确保已有数据的 `is_favorite` 默认为 false，数据不丢失

### 问题四：搜索历史无法跨会话保存

**现象**：用户搜索图书后关闭应用，再次打开时搜索框中是空的，搜索历史没有保留。

**原因分析**：最初只将搜索关键词保存在 ViewModel 的内存状态中，未做持久化处理。

**解决方案**：
- 引入 DataStore 存储最近搜索关键词（`LAST_SEARCH_KEY`）
- 同时将搜索历史保存到 Room 的 `search_history` 表
- ViewModel 初始化时从 DataStore 读取 `lastSearch` flow 并回填到搜索框

---

## 13. 已实现的选做项说明

| 序号 | 选做项 | 类别 | 说明 |
|------|--------|------|------|
| 1 | **数据库迁移（不丢失数据）** | A 类 | 实现 `MIGRATION_1_2`，版本升级时用 SQL 新建搜索历史表并增加收藏字段，已有数据不丢失 |
| 2 | **Coil 图片加载** | B 类 | 使用 `AsyncImage` 异步加载 OpenLibrary 图书封面图片，支持占位图和无封面显示 |
| 3 | **搜索历史持久化** | C 类 | 搜索关键词保存到 DataStore 和 Room 双重存储，应用重启自动恢复搜索记录，输入时模糊匹配历史记录 |
| 4 | **模糊搜索与多状态筛选** | A 类 | BookDao 实现 `LIKE '%keyword%'` 模糊搜索书名和作者，按阅读状态 FilterChip 筛选，支持四种状态切换 |

---

## 14. AI 使用说明

本项目在开发过程中使用了以下 AI 工具辅助开发：

- **工具名称**：CodeBuddy（AI 编程助手）
- **使用范围**：
  - 代码审查与 Bug 诊断（搜索功能逻辑问题、FAB 按钮状态问题定位）
  - Room 数据库迁移功能实现参考
  - 报告文档结构整理与内容生成
---

## 15. 运行说明

### 环境要求

| 项目 | 要求 |
|------|------|
| 最低 Android 版本 | **Android 8.0（API Level 26）** |
| 目标 SDK 版本 | Android 14（API Level 34） |
| JDK 版本 | JDK 17 |
| Android Studio | Hedgehog 2023.1.1 或更高版本 |
| Gradle | 8.0.2 + AGP 8.0.2 |

### 特殊权限

| 权限 | 用途 |
|------|------|
| `android.permission.INTERNET` | 在线搜索 OpenLibrary API 和加载封面图片 |
| `android.permission.ACCESS_NETWORK_STATE` | 检测网络状态 |

### 运行步骤

1. 使用 Android Studio 打开项目根目录（`FinalProject/`）
2. 等待 Gradle 依赖同步完成
3. 选择模拟器（Android 8.0+）或真机
4. 点击 Run 按钮编译并运行
5. 首次启动会自动导入 20 本预置图书到本地数据库
6. 使用搜索功能需要确保设备有网络连接

---

## 16. 项目亮点与未来改进方向

### 项目亮点

1. **架构清晰分层**：严格遵循 MVVM + Repository 模式，UI 层、ViewModel 层、Repository 层、数据层清晰分离，Composable 不直接访问数据库和网络
2. **完整 CRUD + 筛选 + 收藏 + 搜索**：覆盖图书管理的全部核心操作，满足实际使用需求
3. **本地与网络双数据源**：本地 Room 模糊搜索 + OpenLibrary 在线搜索，搜索结果统一展示
4. **数据库迁移**：实现版本 1→2 无损升级，展示了实际开发中处理数据库版本变更的能力
5. **Flow 响应式数据流**：所有查询返回 Flow，数据库变更自动刷新 UI，无需手动刷新
6. **Material 3 深色模式**：自定义亮色/深色两套完整主题，跟随系统设置或手动切换

### 未来改进方向

1. **编辑图书功能**：目前详情页可修改阅读状态、评分、笔记，但无法修改标题、作者等基本信息，后续可增加编辑页面的完善
2. **离线缓存**：将网络搜索结果缓存到 Room，无网络时可查看历史搜索缓存
3. **数据导出**：支持将收藏图书列表导出为 JSON/CSV 文件
4. **大屏适配**：平板或横屏下实现列表-详情双栏布局
5. **页面切换动画**：Navigation 添加过渡动画提升视觉体验
6. **下拉刷新**：为在线搜索和本地列表添加下拉刷新功能
7. **单元测试**：为 ViewModel 和 Repository 编写单元测试，确保核心逻辑正确
