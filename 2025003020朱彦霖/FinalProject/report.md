# FocusTimer 专注时光 - 期末大作业报告

GitHub 仓库地址:https://github.com/1011zyl/2025003020-finalwork.git

## 1\. 项目简介

* **应用名称**:FocusTimer(专注时光)
* **选题背景**:番茄工作法在学生和程序员群体中应用广泛,但市面上很多 App 强制注册、夹杂广告。本项目从"工具属性"出发,做一个无登录、无广告、纯本地数据的轻量番茄钟,并通过实时网络名言给用户持续的心理激励。
* **目标用户**:学生、程序员、设计师、自考/备考人群,需要长时间专注工作/学习,且希望用科学方式管理时间、复盘自己专注时长的用户。
* **核心功能**:番茄钟/专注计时 + 专注记录管理 + 自定义任务分类 + 励志名言实时拉取 + 历史统计与分类排行 + 用户偏好持久化。

## 2\. 技术栈

|类别|选型|
|-|-|
|**UI**|Jetpack Compose(BOM 2024.02)+ Material 3 + 自定义 Color/Type/Shape|
|**导航**|Navigation Compose 2.8(底部 Tab + 详情页路由参数)|
|**数据持久化**|Room 2.6.1(KSP 编译期生成)+ DataStore Preferences 1.1|
|**网络**|Retrofit 2.11 + OkHttp 4.12 + Moshi 1.15(API:`https://api.quotable.io`)|
|**状态管理**|ViewModel + StateFlow + sealed interface UiState|
|**异步**|Kotlin Coroutines 1.8(`viewModelScope` + `stateIn` + `combine` + `flatMapLatest`)|
|**架构**|MVVM + Repository + 简易依赖容器(`FocusTimerApp`)|
|**主题**|浅色 / 深色 / 跟随系统 三种模式|
|**构建**|Gradle Kotlin DSL + version catalog(`libs.versions.toml`)|
|**JDK / Kotlin**|JDK 11 + Kotlin 2.0+|

第三方库除课程要求外无额外引入,仅使用 Coil 不必要的图片加载(本应用为纯文字+矢量图标)。

## 3\. 功能清单

### 必做项完成情况

**UI 层**

* \[x] Jetpack Compose 构建全部 UI(无任何 XML 布局)
* \[x] 4 个主要页面:首页(计时)、历史(列表)、设置、详情
* \[x] Compose Navigation 导航 + 底部 Tab 栏(`NavigationBar` + `NavigationBarItem`)
* \[x] `LazyColumn` 列表(历史记录、分类筛选)
* \[x] Material 3 组件:`Card`、`Button`、`TextField`、`TopAppBar`、`FloatingActionButton`、`Snackbar`、`Dialog`、`FilterChip`、`SegmentedButton`、`Slider`、`Switch`、`NavigationBar`、`AssistChip`、`Badge`
* \[x] 自定义主题:颜色(Color.kt)、字体(Type.kt)、形状(Shape.kt)全部自定义
* \[x] 浅色 / 深色 / 跟随系统三种模式(DataStore 持久化)
* \[x] 圆形进度环(纯 Compose `Canvas` 绘制,无第三方库)

**数据层**

* \[x] Room 数据库,2 张表:`focus\\\\\\\\\\\\\\\_sessions`(专注记录)、`categories`(任务分类)
* \[x] 完整 CRUD:新增、查询、更新、删除、批量清空
* \[x] DAO 查询方法返回 `Flow<List<T>>`
* \[x] 多种查询:全部列表、关键词搜索(LIKE 模糊)、按分类筛选、分组统计(GROUP BY)、时间范围统计
* \[x] DataStore 保存:默认专注/休息时长、最近任务名、最近分类、主题模式、自动开始休息开关、累计完成次数
* \[x] App 首次启动自动种入 5 个默认分类(学习/工作/运动/阅读/创作)

**网络层**

* \[x] `AndroidManifest.xml` 声明 `INTERNET` + `ACCESS\\\\\\\\\\\\\\\_NETWORK\\\\\\\\\\\\\\\_STATE`
* \[x] 使用 Retrofit + OkHttp + Moshi(全部经由 `NetworkDataSource` 单例 + `QuoteApiService`)
* \[x] API 来源:`https://api.quotable.io`(公共名言,无需 API Key)
* \[x] 网络数据在首页"今日名言"卡片中展示,用户可下拉/点按钮刷新
* \[x] 处理 Loading / Success / Error 状态(密封接口 `QuoteUiState`)
* \[x] 网络失败时 `runCatching + recoverCatching` 降级到本地 8 条 fallback 名言,保证体验
* \[x] Composable 不直接发起请求,全部经 ViewModel → Repository → NetworkDataSource

**架构层**

* \[x] ViewModel + StateFlow 状态管理(3 个 ViewModel:`TimerViewModel`、`HistoryViewModel`、`SettingsViewModel`)
* \[x] Repository 模式隔离数据源(`FocusRepository` 统一 DAO 与网络)
* \[x] UiState 使用 `sealed interface`(`QuoteUiState`、`ListUiState`)
* \[x] ViewModel 通过 Repository 访问数据,不直接调用 DAO/Service
* \[x] UI 通过 `collectAsStateWithLifecycle()` 收集状态
* \[x] Composable 不直接访问数据库或网络

**功能完整性**

* \[x] 新增/删除专注记录、新增/删除分类
* \[x] 搜索(任务名关键词 LIKE)
* \[x] 筛选(按分类)
* \[x] 编辑(详情页备注 `note` 字段)
* \[x] 用户偏好保存(DataStore)
* \[x] 输入验证(任务名非空、时长 1-180 分钟、休息 1-60 分钟)
* \[x] 错误提示(Snackbar + `errorMessage` 字段)
* \[x] 空/加载/错误状态展示(`ListUiState` 三态 + `StatusComponents` 通用组件)
* \[x] 屏幕旋转后 ViewModel 状态保持(`AndroidViewModel` + `viewModelScope`)

### 选做项完成情况

* \[x] **复杂数据库查询**:`WHERE task\\\\\\\\\\\\\\\_name LIKE '%' || :keyword || '%'` 模糊搜索 + `GROUP BY category` 统计查询
* \[x] **搜索体验优化**:搜索框带清空按钮(尾缀 IconButton)
* \[x] **错误处理增强**:`runCatching + recoverCatching` 网络失败 fallback + Snackbar 提示
* \[x] **空/加载/错误状态**:抽取通用组件 `EmptyState`、`LoadingState`、`ErrorState`
* \[x] **Repository 隔离**:UI 只与 Repository 交互,DAO 与 Service 不暴露给 ViewModel
* \[ ] 未完成的项(留待后续):CameraX、通知提醒、单元测试(作业时间有限)

## 4\. 数据库设计

数据库名:`focus\\\\\\\\\\\\\\\_timer\\\\\\\\\\\\\\\_database`,版本 1,首次启动通过 `AppDatabaseCallback.onCreate` 种入 5 个默认分类。

### 表 1:`focus\\\\\\\\\\\\\\\_sessions`(专注记录)

|字段名|Kotlin 类型|类型|说明|
|-|-|-|-|
|`id`|`Long`|主键,自增|记录唯一 ID|
|`task\\\\\\\\\\\\\\\_name`|`String`|非空|任务名称(用于模糊搜索)|
|`duration\\\\\\\\\\\\\\\_seconds`|`Int`|非空|专注时长(秒)|
|`category`|`String`|非空|任务分类,与 `categories.name` 关联(逻辑外键)|
|`completed\\\\\\\\\\\\\\\_at`|`Long`|非空|完成时间戳(毫秒,用于排序)|
|`is\\\\\\\\\\\\\\\_completed`|`Boolean`|默认 true|是否已完成(预留字段)|
|`note`|`String`|默认 ""|备注,详情页可编辑|

### 表 2:`categories`(任务分类)

|字段名|Kotlin 类型|类型|说明|
|-|-|-|-|
|`id`|`Long`|主键,自增|分类唯一 ID|
|`name`|`String`|非空|分类名称(唯一)|
|`color\\\\\\\\\\\\\\\_hex`|`String`|默认 `#3F51B5`|分类主色(预留,用于徽章染色)|
|`icon\\\\\\\\\\\\\\\_name`|`String`|默认 `"Timer"`|图标名(预留)|
|`created\\\\\\\\\\\\\\\_at`|`Long`|默认当前时间|创建时间戳|

### 表关系

`focus\\\\\\\\\\\\\\\_sessions.category` 与 `categories.name` 构成逻辑外键关联,删除分类时**不会**级联删除已有记录(避免数据丢失);UI 列表对无对应分类的记录仍能正常显示,只是不再出现在分类筛选行中。

### 主要 DAO 查询

`FocusSessionDao`:

* `getAllSessions(): Flow<List<FocusSessionEntity>>` —— 全部记录,按 `completed\\\\\\\\\\\\\\\_at` 倒序
* `getSessionById(id): FocusSessionEntity?` —— 主键查询(suspend,详情页使用)
* `searchSessions(keyword): Flow<List<...>>` —— `LIKE '%' || :keyword || '%'` 模糊搜索
* `getSessionsByCategory(category): Flow<List<...>>` —— 分类精确匹配
* `getTotalSessionCount(): Flow<Int>` —— 累计专注次数
* `getTotalFocusSeconds(): Flow<Long>` —— 累计专注总秒数
* `getSessionCountSince(startTime): Flow<Int>` —— 起始时间之后次数
* `getSessionCountBetween(startTime, endTime): Int` —— 时间区间统计(suspend)
* `getCategoryStatistics(): Flow<List<CategoryStat>>` —— `GROUP BY category` 分组统计
* `insertSession / updateSession / deleteSession / deleteSessionById / clearAllSessions` —— CRUD

`CategoryDao`:

* `getAllCategories(): Flow<List<CategoryEntity>>` —— 全部分类
* `getCategoryById(id): CategoryEntity?` —— 主键查询
* `getCategoryCount(): Int` —— 数量统计
* `insertCategory / updateCategory / deleteCategory / clearAllCategories` —— CRUD(`OnConflictStrategy.IGNORE` 防止重名)

## 5\. 网络功能设计

* **API 来源**:`https://api.quotable.io/`(公共名言 API,无需认证,无需 API Key)
* **基础地址**:`https://api.quotable.io/`(由 `QuoteApiService.BASE\\\\\\\\\\\\\\\_URL` 集中管理)
* **接口 1**:`GET /random?maxLength=150` —— 随机获取一条名言(`getRandomQuote`)
* **接口 2**:`GET /random?tags=wisdom\\\\\\\\\\\\\\\&maxLength=150` —— 按标签获取(`getQuoteByTag`,首页"励志"按钮使用)
* **请求方式**:Retrofit 协程挂起函数 + OkHttp(`HttpLoggingInterceptor.Level.BASIC` 打日志,10s 超时)
* **数据格式**:JSON,经 Moshi(`KotlinJsonAdapterFactory` + `@JsonClass(generateAdapter = true)`)反序列化为 `QuoteDto`
* **主要返回字段**:

  * `\\\\\\\\\\\\\\\_id`:名言唯一 ID
  * `content`:名言正文
  * `author`:作者
  * `tags`:标签数组(可为 null)
* **App 中使用**:首页顶部的"今日名言"卡片(`HomeScreen` 顶部),进入 App 时自动 `init { fetchQuote() }` 拉取,卡片右侧按钮可手动刷新,失败时由 `recoverCatching` 返回本地 fallback
* **网络失败处理**:`Repository.fetchRandomQuote` 使用 `runCatching { ... }.recoverCatching { ... }`,失败时从 8 条本地 `FallbackQuote` 中随机选一条;UI 永远能拿到非空结果,仅在 `QuoteUiState` 中标记成功/失败
* **网络状态展示**:`QuoteUiState = Idle | Loading | Success(quote) | Error(msg)`,UI 根据状态显示 loading 动画 / 名言卡片 / 错误 Snackbar
* **Composable 隔离**:`HomeScreen` 不直接 import `QuoteApiService`,而是通过 `TimerViewModel.quoteState` 收集,业务逻辑全部下沉

## 6\. 架构设计

```
┌───────────────────────────────────────────────┐
│  UI Layer(Composable)                          │
│  HomeScreen / HistoryScreen / SettingsScreen  │
│  SessionDetailScreen + StatusComponents        │
│  Theme(Color/Type/Shape)                       │
└───────────────▲───────────────▼────────────────┘
                collectAsStateWithLifecycle()
                调用 ViewModel 事件回调
┌───────────────────────────────────────────────┐
│  ViewModel Layer(3 个 ViewModel)              │
│  TimerViewModel   : 计时、名言拉取            │
│  HistoryViewModel : 搜索/筛选/统计 Flow 组合  │
│  SettingsViewModel: 偏好读写                  │
│  UiState:TimerUiState / QuoteUiState /        │
│          ListUiState<T>                        │
└───────────────▲───────────────▼────────────────┘
                Flow / suspend
┌───────────────────────────────────────────────┐
│  Repository Layer                             │
│  FocusRepository                              │
│   - 屏蔽 DAO 与 Service 细节                  │
│   - 统一对外暴露 Flow / suspend fun            │
│   - 包装 runCatching + recoverCatching        │
└───────▲───────────────────▲───────────────────┘
        │                   │
┌───────┴──────┐    ┌───────┴──────────────────┐
│  Room DAO    │    │  NetworkDataSource         │
│  + Database  │    │  (Retrofit + OkHttp + Moshi)│
└──────────────┘    └────────────────────────────┘
                ▲
                │  DataStore Preferences
        ┌───────┴───────────────┐
        │ UserPreferencesRepository │
        └───────────────────────────┘
```

* **UI Layer**:纯 Composable,只负责渲染 + 事件回调,不持有业务状态。
* **ViewModel**:管理 `UiState`(全部为 `StateFlow`),使用 `viewModelScope` 启动协程,工厂模式注入 `FocusTimerApp` 中的依赖。
* **Repository**:统一数据访问,`FocusRepository` 同时持有两个 DAO 与 `NetworkDataSource`,对外只暴露 Flow 与 suspend 函数。
* **DataStore**:`UserPreferencesRepository` 独立管理,ViewModel 通过 `stateIn` 转换为 `StateFlow` 供 UI 收集。
* **依赖注入**:`FocusTimerApp`(`Application`)充当简易容器,避免引入 Hilt,降低学习成本。

## 7\. DataStore 设计

`UserPreferencesRepository` 使用 DataStore Preferences(name = `user\\\\\\\\\\\\\\\_preferences`),管理以下 7 个键:

|Key|类型|默认值|写入场景|读取场景|
|-|-|-|-|-|
|`default\\\\\\\\\\\\\\\_focus\\\\\\\\\\\\\\\_minutes`|Int|25|设置页 Slider 调整|App 启动初始化计时|
|`default\\\\\\\\\\\\\\\_break\\\\\\\\\\\\\\\_minutes`|Int|5|设置页 Slider 调整|切换到"休息"模式|
|`recent\\\\\\\\\\\\\\\_task\\\\\\\\\\\\\\\_name`|String|""|一次专注完成|App 启动时填入输入框|
|`recent\\\\\\\\\\\\\\\_category`|String|"学习"|一次专注完成|App 启动时选中分类|
|`theme\\\\\\\\\\\\\\\_mode`|String|"system"|设置页切换主题|`MainActivity.setContent` 决定 `darkTheme`|
|`auto\\\\\\\\\\\\\\\_start\\\\\\\\\\\\\\\_break`|Boolean|true|设置页 Switch|(预留)专注完成后自动开启休息|
|`completed\\\\\\\\\\\\\\\_focus\\\\\\\\\\\\\\\_count`|Int|0|每次专注完成 +1|设置页"我的成就"展示|

所有键均以 `Flow` 形式对外暴露,ViewModel 用 `stateIn(WhileSubscribed(5000))` 转 `StateFlow`,配置变化时不会重复订阅导致数据丢失。

## 8\. ViewModel 与 UiState 设计

### 8.1 `TimerUiState`(`data class`,首页计时)

```kotlin
data class TimerUiState(
    val mode: TimerMode = FOCUS,          // 专注/休息
    val status: TimerStatus = IDLE,       // IDLE/RUNNING/PAUSED/FINISHED
    val totalSeconds: Int = 25 \\\\\\\\\\\\\\\* 60,
    val remainingSeconds: Int = 25 \\\\\\\\\\\\\\\* 60,
    val taskName: String = "",
    val category: String = "学习",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

* 用 `MutableStateFlow + asStateFlow()` 暴露。
* 计时核心在 `viewModelScope.launch { while(...) delay(1000) }`,`onCleared` 时取消 `timerJob`,避免泄漏。
* 屏幕旋转时 `AndroidViewModel` 不会被销毁,`StateFlow` 当前值直接复用,无需手动 `SavedStateHandle`。

### 8.2 `QuoteUiState`(`sealed interface`,名言)

```kotlin
sealed interface QuoteUiState {
    data object Idle
    data object Loading
    data class Success(val quote: FallbackQuote)
    data class Error(val message: String)
}
```

* `fold` + `Result` 映射,UI 一处 `when` 渲染三态,无空指针。

### 8.3 `ListUiState<T>`(`sealed interface`,列表通用)

```kotlin
sealed interface ListUiState<out T> {
    data object Loading
    data class Success<T>(val data: List<T>)
    data object Empty
    data class Error(val message: String)
}
```

* 抽到 `viewmodel` 包,被 `HistoryViewModel.sessions` 使用,UI 通过 `StatusComponents.kt` 中的三个 Composable 渲染。

### 8.4 `HistoryViewModel` 协程组合

`HistoryViewModel` 同时支持"按关键词搜索"与"按分类筛选",使用 `combine + flatMapLatest` 动态切换数据源:

```kotlin
val sourceFlow = combine(\\\\\\\\\\\\\\\_searchKeyword, \\\\\\\\\\\\\\\_selectedCategory) { k, c -> k to c }
    .flatMapLatest { (keyword, category) ->
        when {
            keyword.isNotBlank() -> repository.searchSessions(keyword)
            category != null      -> repository.getSessionsByCategory(category)
            else                  -> repository.allSessions
        }
    }
```

`flatMapLatest` 会在上游变化时自动取消上一个 Flow,保证列表不会闪旧数据。

## 9\. Repository 隔离

`FocusRepository` 是 ViewModel 与数据源之间的唯一桥梁:

|调用方|看到的内容|看不到的内容|
|-|-|-|
|`HomeScreen`|`TimerViewModel.uiState / quoteState`|`FocusSessionDao` / `QuoteApiService` / `OkHttpClient`|
|`HistoryScreen`|`HistoryViewModel.sessions / categories / categoryStatistics`|SQL 语句、Retrofit 注解|
|`SettingsScreen`|`SettingsViewModel.\\\\\\\\\\\\\\\*Minutes / themeMode`|DataStore Key 名、Edit API|

具体实现要点:

* 写入路径:`ViewModel.xxx() → repository.xxx() → dao.insertXxx() / NetworkDataSource.xxx()`,ViewModel 不直接 `import androidx.room.\\\\\\\\\\\\\\\*`。
* 读取路径:`val flow: Flow<...> = sessionDao.xxx()`,ViewModel 通过 `stateIn` 暴露,Composable 通过 `collectAsStateWithLifecycle()` 订阅。
* 网络容错:`runCatching { api.xxx() }.recoverCatching { fallback.random() }`,UI 拿到的永远是 `Result<FallbackQuote>` 而非裸异常。

## 10\. 核心功能截图

> 截图位于同目录 `screenshots/`。

### 10.1 首页 `screenshots/home.png`

说明:展示进入 App 后的主界面,自上而下为:

* TopAppBar 标题"专注时光";
* "今日名言"卡片(loading 态或已加载的金句 + 刷新按钮);
* 圆形进度环 + 大字 `MM:SS`;
* `SegmentedButton` 切换"专注/休息";
* 任务名输入框 + 分类 `AssistChip` 行;
* 时长 `FilterChip`(15/25/45/60/90);
* 主操作按钮"开始/暂停/重置"。

### 10.2 历史页 `screenshots/history.png`

说明:展示专注记录列表页,自上而下为:

* 顶部统计卡片(总次数 / 总时长 / 分类数);
* 分类排行 TOP3;
* 搜索框(带 ✕ 清空按钮);
* 分类筛选 `FilterChip` 行(长按可删除);
* `LazyColumn` 记录卡片(任务名、分类徽章、时长、完成时间);
* 右下角 `FloatingActionButton` 添加新分类。

### 10.3 设置页 `screenshots/settings.png`

说明:展示设置页:

* "我的成就"卡片(累计完成次数,大字);
* 默认专注时长 `Slider`(5-120 分钟);
* 默认休息时长 `Slider`(1-30 分钟);
* 主题模式 `SegmentedButton`(跟随/浅色/深色);
* 自动开始休息 `Switch`;
* 关于卡片(版本号、GitHub 链接)。

### 10.4 详情页 `screenshots/detail.png`

说明:展示点击历史记录后的详情页:

* TopAppBar 返回箭头;
* 任务名(大标题)+ 分类 `Badge`;
* 时长卡片(转时分秒显示);
* 完成时间(日期 + 时刻);
* 备注卡片(可编辑 `TextField` + "保存"按钮)。

## 11\. 技术难点与解决方案

### 难点 1:计时器在屏幕旋转 / 进程切换后状态保持

* **问题描述**:默认情况下 Composable 在 Activity 重建后会丢失内部状态,若把计时变量用 `remember` 持有,旋转屏幕即归零。
* **原因分析**:`remember` 的值绑定在 Composition 上,Activity 因配置变化重建时整棵 Composition 重建,状态被丢弃。
* **解决方案**:将 `TimerStatus / remainingSeconds / totalSeconds` 等所有计时状态全部上移至 `TimerViewModel`(`AndroidViewModel`,绑定 `Application` 生命周期),通过 `MutableStateFlow` 暴露;计时循环使用 `viewModelScope.launch { while(...) delay(1000) }`,旋转屏幕时 ViewModel 实例被保留,Flow 当前值不变,UI 自动重组并显示正确剩余时间。

### 难点 2:协程 + Flow 组合查询(搜索词 / 分类筛选 动态切换)

* **问题描述**:历史页需要同时支持"按任务名模糊搜索"和"按分类精确筛选",且两者可能互斥切换,如果用普通 `suspend` 切换会出现竞态。
* **原因分析**:两个查询源都是冷 Flow,直接用 `await` 顺序调用,旧请求的回调可能在 UI 重组后到达,导致列表闪烁。
* **解决方案**:`HistoryViewModel` 用 `combine(\\\\\\\\\\\\\\\_searchKeyword, \\\\\\\\\\\\\\\_selectedCategory) { ... }.flatMapLatest { ... }` 把两个 `MutableStateFlow` 合成一个数据源,`flatMapLatest` 会在上游变化时取消上一个 Flow 订阅,保证 UI 永远只看到最新一次查询的结果,再 `stateIn(WhileSubscribed(5000))` 暴露给 UI。

### 难点 3:网络失败兜底,UI 不卡 Loading

* **问题描述**:`api.quotable.io` 偶发超时或被墙,如果 `catch` 后只显示错误,首页会显得很"空",体验差。
* **原因分析**:网络异常应该降级而非阻塞 UI。
* **解决方案**:`FocusRepository.fetchRandomQuote` 使用 `runCatching { api.getRandomQuote() }.recoverCatching { NetworkDataSource.fallbackQuotes.random() }`,失败时从 8 条本地 fallback 名言中随机返回一条,`HomeScreen` 永远能拿到非空名言,只在 `QuoteUiState` 中以 `Success` 标记,UI 行为完全一致。

### 难点 4:依赖注入的轻量化

* **问题描述**:ViewModel 需要 `FocusRepository` 与 `UserPreferencesRepository`,若直接 `new` 出来,会和 Room/DataStore 的生命周期耦合,且不便测试。
* **原因分析**:不引入 Hilt(为了课程作业的简洁性),但又不能破坏 MVVM 分层。
* **解决方案**:用 `FocusTimerApp : Application` 作为简易容器,以 `by lazy` 持有 `database / repository / preferencesRepository` 三个单例,`ViewModelProvider.Factory` 在 `create()` 时从 Application 取依赖,既保持分层,又避免 Hilt 的样板代码。

## 12\. AI 使用说明

请在以下选项中勾选,可多选:

* \[ ] 未使用 AI
* \[x] 网页版 AI(如 ChatGPT、Claude、Kimi、豆包等)
* \[x] AI Agent / 编程代理(如 Claude Code、Codex、OpenCode、Cursor Agent 等)
* \[ ] 国产大模型服务(如 DeepSeek、GLM、通义千问、文心一言等)
* \[x] IDE 插件或代码补全工具(如 GitHub Copilot、Cursor、CodeGeeX、CodeBuddy 等)
* \[ ] 其他:

具体工具名称:**CodeBuddy(本 IDE 内置助手,基于混元大模型)**,辅以 ChatGPT 检索 Retrofit/Room 用法。

AI 主要用于哪些环节:

* **项目结构规划**:目录划分、`data/network/dto` 三层分包建议;
* **Compose UI 代码生成**:`HomeScreen` 圆形进度环 `Canvas` 绘制、`StatusComponents` 三态通用组件;
* **Gradle / libs.versions.toml 配置生成**:Room/Compose BOM/Retrofit 版本号;
* **协程 Flow 组合**:`combine + flatMapLatest` 的标准写法参考;
* **报告模板填充**:本 `report.md` 的章节框架与文字润色;
* **Bug 定位**:`runCatching` 包装网络请求的写法参考。

说明:核心架构决策(选择 `sealed interface ListUiState`、用 `Application` 充当依赖容器、放弃 Hilt)均为本人设计,AI 仅提供建议与代码片段。

## 13\. 运行说明

* **最低 Android 版本**:API 24(Android 7.0)
* **推荐 Android 版本**:API 34(Android 14)
* **开发工具**:Android Studio Hedgehog(2023.1.1)或更新
* **JDK / Kotlin**:JDK 11、Kotlin 2.0+
* **特殊权限**:`INTERNET`(获取名言)、`ACCESS\\\\\\\\\\\\\\\_NETWORK\\\\\\\\\\\\\\\_STATE`(预留)
* **运行步骤**:

  1. 克隆仓库:`git clone https://github.com/1011zyl/2025003020-finalwork.git`
  2. 使用 Android Studio 打开项目
  3. 等待 Gradle 同步完成(首次较慢,需下载 Compose BOM、Room、Retrofit 等依赖)
  4. 连接 Android 7.0+ 模拟器或真机,点击 Run
  5. (可选)打包命令:`./gradlew assembleDebug`,产物在 `app/build/outputs/apk/debug/`

## 14\. 项目亮点

* 完整 MVVM + Repository 架构,层次清晰,`FocusTimerApp` 充当轻量依赖容器,避免 Hilt 复杂度;
* UiState 全部使用 `sealed interface`,编译期类型安全,UI 一处 `when` 覆盖所有状态;
* `combine + flatMapLatest` 优雅解决"搜索 / 筛选"动态切换的竞态问题;
* `runCatching + recoverCatching` 实现网络失败的优雅降级,UI 永远不卡 Loading;
* `AndroidViewModel` + `viewModelScope` 自然实现屏幕旋转 / 进程切换后的计时状态保持;
* 自定义 Material 3 主题(颜色/字体/形状),支持浅色 / 深色 / 跟随系统;
* `Canvas` 自绘圆形进度环,不依赖任何第三方进度条库;
* 通用 `ListUiState<T>` + `StatusComponents` 可复用到任意列表页;
* DataStore 7 个键的合理设计,既支持用户偏好,又支持"最近任务/分类"等业务字段。

## 15\. 未来改进方向

* 接入 `NotificationManager` + `WorkManager`,专注完成时本地通知 + 后台提醒;
* 历史页增加周报 / 月报图表(可手绘 `Canvas` 或引入 Vico);
* 支持大屏自适应(平板下历史+详情双栏 `Row`);
* 单元测试覆盖 `TimerViewModel` 计时逻辑与 `FocusRepository` fallback 分支;
* 数据库迁移支持(Migration 替代 `fallbackToDestructiveMigration`);
* 集成白噪音播放(专注时播放环境音);
* 支持 JSON 导入导出备份;
* 接入多语言(目前 strings.xml 为中文,需补 en);
* 主页"今日名言"增加点击复制 / 分享功能;
* 为 `FocusSession` 增加 `started\\\\\\\\\\\\\\\_at` / `paused\\\\\\\\\\\\\\\_seconds` 字段,实现更精细的暂停时长统计。

