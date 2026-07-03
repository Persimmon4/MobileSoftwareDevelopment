# 待办清单应用 - 项目报告

GitHub 仓库地址：https://github.com/wyhww/2025003023-FinalProject


## 1. 项目简介

- **应用名称**：TodoList（待办清单）
- **目标用户**：需要管理日常任务、学习计划和工作事项的普通用户，特别适合学生和上班族
- **核心功能**：
  - 创建、编辑、删除待办事项
  - 标记待办完成/未完成状态
  - 按关键词搜索待办
  - 按分类筛选待办（默认/学习/工作）
  - 按截止日期排序待办
  - 查看即将到期的待办
  - 从网络获取示例待办数据并保存到本地
  - 支持深色/浅色模式切换


## 2. 技术栈

- **UI**：Jetpack Compose + Material 3
- **数据库**：Room（SQLite）
- **网络**：Retrofit 2 + Gson（接口来源：JSONPlaceholder 模拟 API）
- **状态管理**：ViewModel + StateFlow
- **持久化偏好**：DataStore
- **导航**：Navigation Compose
- **异步处理**：Kotlin Coroutines
- **其他依赖**：
  - `androidx.lifecycle:lifecycle-viewmodel-compose`
  - `androidx.lifecycle:lifecycle-runtime-ktx`
  - `kotlinx.coroutines:kotlinx-coroutines-android`
  - `androidx.compose.material:material-icons-extended`


## 3. 功能清单

### 必做项完成情况

**UI 层**
- [x] Jetpack Compose 构建全部 UI
- [x] 至少 2 个主要页面（首页、设置页、新增/编辑页、网络数据页）
- [x] Compose Navigation 导航
- [x] LazyColumn / LazyRow 列表
- [x] Material 3 组件（Card、Button、TextField、TopAppBar、FloatingActionButton、Switch、FilterChip、AlertDialog 等）
- [x] 浅色 / 深色模式支持
- [x] 列表为空、加载中状态有合理界面反馈

**数据层**
- [x] Room 数据库，2 张表（TodoEntity、CategoryEntity）
- [x] 完整 CRUD 操作（插入、更新、删除、查询）
- [x] DAO 查询方法返回 Flow 类型
- [x] 搜索查询功能（按标题模糊搜索）
- [x] 分类筛选查询（按分类ID筛选）
- [x] 组合查询（分类+搜索）
- [x] 截止日期排序查询
- [x] 即将到期查询（未来3天内）
- [x] DataStore 保存深色模式偏好

**网络层**
- [x] 声明并使用 Internet 权限
- [x] 使用 Retrofit 从 JSONPlaceholder API 获取 Mock 待办数据
- [x] 网络数据在应用中展示并支持保存到本地
- [x] 处理 Loading / Success / Error 网络状态
- [x] Composable 不直接发起网络请求

**架构层**
- [x] ViewModel 状态管理
- [x] Repository 模式
- [x] StateFlow / Flow 数据流
- [x] Kotlin 协程异步处理
- [x] UiState 描述界面状态（Loading、Success、Error、Empty）
- [x] Composable 不直接访问数据库或网络

**功能完整性**
- [x] 新增 / 编辑 / 删除 / 搜索 / 筛选 / 排序 / 切换完成状态（7 项核心操作）
- [x] 标题必填验证
- [x] 状态展示（空状态 / 加载状态 / 错误状态）
- [x] 屏幕旋转后状态保持（ViewModel 自动处理）
- [x] 返回键行为符合预期

### 选做项完成情况

- [x] 复杂数据库查询（分类筛选、组合查询、截止日期排序、即将到期查询）
- [x] 数据库迁移（版本1→2，添加 dueDate 字段）
- [x] 自定义 Material 主题（浅红配色方案）
- [x] 深色/浅色模式切换


## 4. 数据库设计

### 表 1：todos（待办事项）

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| title | String | 待办标题 |
| content | String | 待办详细内容 |
| categoryId | Int | 分类 ID，外键关联 categories 表 |
| isCompleted | Boolean | 是否已完成 |
| createdAt | Long | 创建时间戳 |
| dueDate | Long? | 截止日期时间戳（可为空） |

### 表 2：categories（分类）

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| name | String | 分类名称 |
| color | String | 分类颜色（十六进制颜色值） |

### 表关系

- `todos` 表通过 `categoryId` 字段与 `categories` 表建立一对多关系
- 一个分类可以包含多个待办事项

### 主要 DAO 查询方法

**TodoDao：**
- `getAllTodos()`：获取所有待办，按创建时间降序排列
- `getTodosByCategory(categoryId)`：按分类筛选待办
- `searchTodos(searchQuery)`：按标题模糊搜索
- `searchTodosInCategory(categoryId, searchQuery)`：分类+搜索组合查询
- `getTodosByDueDate()`：按截止日期升序排列
- `getUpcomingTodos(startOfDay, endOfDay)`：获取即将到期的待办
- `insertTodo`、`updateTodo`、`deleteTodo`：标准 CRUD

**CategoryDao：**
- `getAllCategories()`：获取所有分类
- `insertCategory`：新增分类


## 5. 网络功能设计

- **API 来源**：JSONPlaceholder（免费模拟 REST API）
- **接口地址**：`https://jsonplaceholder.typicode.com/todos`
- **请求方式**：GET
- **主要返回字段**：
  - `id`：待办 ID
  - `title`：待办标题
  - `completed`：是否已完成
- **App 中使用这些网络数据的页面或功能**：
  - `NetworkScreen` 页面展示网络数据列表
  - 用户点击"刷新"按钮获取最新网络数据
  - 用户可选择分类后点击"保存"将网络数据存入本地数据库
- **网络失败时的处理方式**：
  - 显示错误提示信息和重试按钮
  - 不丢失已有本地数据


## 6. 架构设计

### 数据流向图
UI Layer (Compose) → collectAsStateWithLifecycle
↓
ViewModel (TodoViewModel) ← StateFlow
↓
Repository (TodoRepository + CategoryRepository)
↓
Data Source (Room + Retrofit)

### UiState 设计

```kotlin
sealed interface TodoUiState {
    object Loading : TodoUiState          // 加载中
    data class Success(val todos: List<TodoEntity>) : TodoUiState  // 成功
    data class Error(val message: String) : TodoUiState  // 错误
    object Empty : TodoUiState             // 空列表
}

sealed interface NetworkUiState {
    object Loading : NetworkUiState
    data class Success(val todos: List<MockTodo>) : NetworkUiState
    data class Error(val message: String) : NetworkUiState
}
数据隔离
Repository 模式：TodoRepository 和 CategoryRepository 作为单一数据访问入口
本地数据：通过 Room DAO 访问
网络数据：通过 NetworkDataSource 访问
ViewModel：只与 Repository 交互，不知道数据具体来源
Composable：只通过 ViewModel 的 StateFlow 获取数据，不直接调用任何数据访问方法

7. 核心功能截图
首页（待办列表）
screenshots/home.png
说明：展示所有待办事项列表，支持按标题搜索，分类筛选标签栏，每个待办项显示标题、完成状态复选框和删除按钮。

网络数据页面
screenshots/network.png
说明：展示从 JSONPlaceholder API 获取的网络待办数据，支持刷新获取最新数据，可选择分类后保存到本地数据库。

新增/编辑待办
screenshots/add_todo.png
说明：点击"新增待办"按钮进入编辑页面，输入标题、内容、选择分类和截止日期后保存。

设置页面
screenshots/settings.png
说明：支持深色/浅色模式切换，设置会持久化保存到 DataStore。

8. 技术难点与解决方案
难点 1：CategoryRepository 初始化时的协程调用问题
问题描述：CategoryRepository.initDefaultCategories() 是挂起函数，需要在协程中调用。最初尝试在 Repository 的 init 块中使用 runBlocking 调用，导致主线程阻塞。
原因分析：runBlocking 会阻塞当前线程直到协程完成，在应用启动时使用会导致界面卡顿甚至 ANR。
解决方案：将初始化逻辑移到 MainActivity 中，使用 lifecycleScope.launch 在后台线程执行。
参考资料：Kotlin 协程官方文档 - 避免在 UI 线程使用 runBlocking

难点 2：动态颜色覆盖自定义主题
问题描述：应用启动后界面显示为蓝色而非设计的浅红色，原因是 Theme.kt 中 dynamicColor 参数默认启用。
原因分析：Android 12 及以上版本支持 Material You 动态颜色，dynamicColor = true 时会使用系统壁纸生成的颜色方案覆盖自定义颜色。
解决方案：将 dynamicColor 默认值改为 false，在 MainActivity 调用 Theme 时显式传入 dynamicColor = false。
参考资料：Material Design 3 动态颜色文档

难点 3：数据库迁移
问题描述：添加 dueDate 字段后，Room 检测到 schema 变化但版本号未变，抛出 IllegalStateException: Room cannot verify the data integrity。
原因分析：数据库从版本 1 升级到版本 2，需要增加版本号并提供迁移策略。
解决方案：增加 version = 2，编写 MIGRATION_1_2 执行 ALTER TABLE todos ADD COLUMN dueDate INTEGER，并添加 fallbackToDestructiveMigration() 作为开发时的安全保障。

9. AI 使用说明
请在以下选项中勾选，可多选：
国产大模型服务（DeepSeek）

AI 主要用于哪些环节：
项目选题分析和功能规划
代码生成和重构（数据库设计、Repository 模式、ViewModel 实现）
调试和错误修复（协程问题、主题颜色问题、数据库迁移问题）
报告整理和格式优化
说明：AI 作为编程辅助工具，帮助快速搭建项目框架和解决技术难题，所有代码经过理解和调试后整合到项目中。

10. 运行说明
最低 Android 版本：API 24（Android 7.0）
推荐 Android 版本：API 34（Android 14）
特殊权限：
android.permission.INTERNET（网络权限）

运行步骤：
克隆仓库：git clone https://github.com/wyhww/2025003023-FinalProject.git
使用 Android Studio 打开项目
等待 Gradle 同步完成
连接模拟器或真机，点击 Run 按钮（▶）
应用启动后自动创建数据库并初始化默认分类

11. 项目亮点
完整的 MVVM 架构：严格按照 Repository、ViewModel、Compose UI 三层架构设计，代码职责清晰
统一主题管理：自定义浅红色主题，支持深色/浅色模式切换，所有界面使用 MaterialTheme 颜色系统
丰富的查询功能：支持搜索、分类筛选、组合查询、截止日期排序、即将到期查询等多种数据操作
网络与本地数据结合：支持从网络获取示例待办并保存到本地，展示完整的网络请求流程
数据库迁移支持：优雅处理了数据库版本升级，已有数据不丢失
数据持久化完善：Room 管理核心数据，DataStore 管理用户偏好设置

12. 未来改进方向
分类管理：实现完整的分类增删改查功能，支持自定义分类颜色
网络增强：添加下拉刷新、分页加载、离线缓存功能
数据导入导出：支持 JSON 格式的数据备份和恢复
通知提醒：使用 WorkManager 实现待办截止日期提醒
图表统计：使用图表库展示待办完成率、分类分布等统计信息
单元测试：为 ViewModel 和 Repository 编写单元测试