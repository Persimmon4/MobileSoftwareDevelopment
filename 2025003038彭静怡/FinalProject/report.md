# NutriBalance 膳食配比助手
GitHub 仓库地址：https://github.com/Peng-jy-1008/NutriBalance

## 1. 项目简介
- 应用名称：NutriBalance 膳食配比助手
- 目标用户：需要记录日常饮食、监控热量与营养摄入的普通用户、减脂、健身人群
- 核心功能：记录每日饮食、在线搜索食物营养数据、增删改查饮食记录、按餐次筛选记录、统计每日热量/碳水/蛋白质/脂肪、营养饼图可视化、收藏常用食物、保存用户个性化偏好、支持浅色/深色主题切换

## 2. 技术栈
- UI：Jetpack Compose + Material 3
- 数据库：Room
- 网络：Retrofit / OkHttp（接口来源：公共食物营养开放API）
- 状态管理：ViewModel + StateFlow
- 持久化偏好：DataStore
- 导航：Navigation Compose
- 异步处理：Kotlin Coroutines
- 其他依赖：Canvas 自定义图表绘制组件

## 3. 功能清单
### 必做项完成情况
**UI 层**
- [x] Jetpack Compose 构建全部 UI
- [x] 至少 2 个主要页面（首页、食物搜索页、饮食记录页、设置页共4个页面）
- [x] Compose Navigation 导航
- [x] LazyColumn 列表展示饮食记录、搜索结果
- [x] Material 3 组件和主题
- [x] 浅色 / 深色模式支持

**数据层**
- [x] Room 数据库，至少 2 张表（饮食记录表、收藏食物表）
- [x] 完整 CRUD 操作（新增、编辑、删除、查询饮食与收藏食物）
- [x] DAO 查询方法返回 Flow 类型
- [x] 至少一种查询功能（按餐次筛选、食物名称搜索）
- [x] DataStore 保存用户偏好或最近状态（目标热量、深色模式、默认餐次、最近搜索词）

**网络层**
- [x] 声明并使用 Internet 权限
- [x] 使用网络请求获取真实 API 食物营养数据
- [x] 网络数据在核心页面中展示或参与主要功能流程（搜索页展示食物营养，可添加至本地记录）
- [x] 处理 Loading / Success / Error 等网络状态，网络失败展示错误提示并兜底示例数据
- [x] Composable 不直接发起网络请求

**架构层**
- [x] ViewModel 状态管理
- [x] Repository 模式（统一管理本地Room与网络数据）
- [x] StateFlow / Flow 数据流
- [x] Kotlin 协程异步处理
- [x] UiState 描述界面加载、成功、错误、空数据状态
- [x] Composable 不直接访问数据库或网络

**功能完整性**
- [x] 新增 / 编辑 / 删除 / 搜索等核心操作（全部实现）
- [x] 输入验证和错误提示（网络异常、空搜索提示）
- [x] 状态展示（空 / 加载 / 错误中的至少一种）
- [x] 屏幕旋转后状态保持（ViewModel 保存页面状态）

### 选做项完成情况
- [x] Canvas 自定义营养比例图表绘制
- [x] 搜索历史持久化存储展示
- [x] 多条件筛选（按餐次筛选饮食记录）
- [ ] 数据导出备份功能
- [ ] 食物扫码识别功能

## 4. 数据库设计
### 表 1：FoodRecord 饮食记录表
| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| foodName | String | 食物名称 |
| calorie | Float | 热量 |
| carb | Float | 碳水化合物 |
| protein | Float | 蛋白质 |
| fat | Float | 脂肪 |
| mealType | String | 餐次（早餐/午餐/晚餐/加餐） |
| recordDate | Long | 记录日期时间戳（毫秒） |
| createdAt | Long | 创建时间戳（毫秒） |

### 表 2：FavoriteFood 收藏食物表
| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| foodName | String | 食物名称 |
| calorie | Float | 热量 |
| carb | Float | 碳水化合物 |
| protein | Float | 蛋白质 |
| fat | Float | 脂肪 |
| createTime | Long | 收藏时间戳（毫秒） |

说明表关系和主要 DAO 查询方法：
两张表无外键关联，独立存储饮食记录与收藏食物；
1. FoodRecordDao：按日期查询当日记录、按餐次筛选记录、新增/编辑/删除记录、按食物名模糊搜索；所有查询返回 Flow 实时监听数据变化；
2. FavoriteFoodDao：收藏、取消收藏、查询全部收藏食物。

## 5. 网络功能设计
- API 来源：公共食物营养开放API
- 接口地址：公共食物营养检索接口（无需密钥，可直接调用）
- 请求方式：GET
- 主要返回字段：食物名称、热量、碳水、蛋白质、脂肪
- App 中使用这些网络数据的页面或功能：搜索页，输入关键词请求接口展示食物营养，可一键添加到当日饮食记录；
- 网络失败时的处理方式：页面弹出错误提示，展示内置示例食物数据保证基础功能可用。

## 6. 架构设计
整体采用标准分层架构，单向数据流：
1. **UI Layer（界面层）**：全部由 Jetpack Compose 可组合组件构成，包含首页、搜索、记录、设置四大页面，仅负责渲染界面、监听用户点击输入，不处理任何耗时操作；通过 Navigation Compose 实现页面跳转。
2. **ViewModel 层**：每个页面独立 ViewModel，持有 UiState（封装加载、成功、空、错误状态），通过 StateFlow 向UI暴露状态；接收UI操作指令，调用 Repository 执行业务逻辑，所有协程任务统一托管。
3. **Repository 数据仓库层**：分为食物仓库与偏好仓库，作为唯一数据源，统一封装本地 Room 数据库操作与 Retrofit 网络请求，隔离上层与底层数据源；负责网络数据与本地实体的数据转换。
4. **Data Layer 数据层**
    - 本地持久化：Room 存储饮食记录、收藏食物；DataStore 存储轻量用户偏好（目标热量、深色模式、搜索历史等）；
    - 网络层：Retrofit+OkHttp 封装 API 请求，统一封装网络返回结果，区分加载、成功、失败状态。

数据流向：用户操作UI → ViewModel → Repository → 网络/Room；数据变更通过 Flow/StateFlow 反向推送至UI自动刷新，严格遵循单向数据流原则。

## 7. 核心功能截图
### 首页
![首页截图](screenshots/homepage.png)
说明：展示当日总摄入热量、Canvas绘制营养占比图表、今日全部饮食记录列表。

### 食物搜索页
![搜索页截图](screenshots/search.png)
说明：输入食物名称发起网络请求，展示食物营养数据，支持添加至当日记录、收藏食物。

### 饮食记录页
![记录页截图](screenshots/record.png)
说明：按餐次筛选全部饮食记录，支持编辑、删除已有饮食条目。

### 设置页面
![设置页截图](screenshots/setting.png)
说明：配置每日目标热量、切换浅色/深色主题、修改默认餐次。

## 8. 技术难点与解决方案
### 难点 1：Canvas 绘制动态营养比例饼图
- 问题描述：原生Compose无内置饼图组件，需要根据每日碳水、蛋白、脂肪、热量实时计算占比并绘制扇形，数值变化时图表平滑更新。
- 原因分析：Canvas绘制需要手动计算角度、路径，多组动态数值实时刷新容易出现重绘卡顿，颜色分区、文字标注逻辑复杂。
- 解决方案：封装独立Chart工具类，根据四种营养数值自动计算扇形角度；使用remember保存绘图状态，数值变更时重组重绘；划分独立色块并添加中心文字展示总热量。
- 参考资料：Jetpack Compose Canvas 官方文档、Compose 自定义绘图开源示例。

### 难点 2：网络请求与本地数据状态统一管理
- 问题描述：搜索页面同时依赖网络食物数据与本地收藏数据，网络加载、失败、空数据多种状态混杂，UI状态混乱。
- 原因分析：网络请求为异步协程操作，与本地Room Flow数据流同时存在，未统一封装状态会导致UI多处判断加载/错误。
- 解决方案：统一封装NetworkResult密封类区分Loading/Success/Error；ViewModel通过单一UiState整合网络与本地数据状态，UI仅监听单一状态分支渲染对应视图。
- 参考资料：Jetpack ViewModel 官方文档、StateFlow 数据流最佳实践。

### 难点 3：深色模式全局持久化生效
- 问题描述：切换深色模式后旋转屏幕、重启App主题丢失，页面组件主题不统一。
- 原因分析：主题状态未持久化，Compose主题未使用DataStore读取的全局状态。
- 解决方案：使用DataStore持久存储主题开关，App顶层包裹主题容器，从ViewModel读取主题状态全局分发，所有页面自动跟随主题切换。
- 参考资料：Jetpack DataStore 持久化官方教程、Material3 主题全局配置文档。

## 9. AI 使用说明
请在以下选项中勾选，可多选：
- [ ] 未使用 AI
- [x] 网页版 AI（如 ChatGPT、Claude、Kimi、豆包等）
- [ ] AI Agent / 编程代理（如 Claude Code、Codex、OpenCode、Cursor Agent 等）
- [x] 国产大模型服务（如 DeepSeek、GLM、通义千问、文心一言等）
- [x] IDE 插件或代码补全工具（如 GitHub Copilot、Cursor、CodeGeeX 等）
- [ ] 其他：

具体工具名称：豆包、CodeGeeX
AI 主要用于哪些环节：UI组件代码生成、Canvas图表绘图逻辑编写、异常逻辑处理、项目报告文档整理、数据库DAO模板生成
说明：是否使用 AI 以及使用了什么 AI 工具不会影响分值，请如实填写。

## 10. 运行说明
- 最低 Android 版本：API 24（Android 7.0）
- 推荐 Android 版本：API 34（Android 14）
- 特殊权限：网络权限；无相机、通知等额外权限
- 运行步骤：
  1. 克隆仓库：`git clone https://github.com/Peng-jy-1008/NutriBalance`
  2. 使用 Android Studio 打开项目
  3. 在 local.properties 中配置本地 Android SDK 路径 `sdk.dir=你的SDK路径`
  4. 等待 Gradle 同步完成（项目使用 Gradle 8.x、JDK17）
  5. 连接模拟器或真机，点击 Run 运行项目

## 11. 项目亮点（可选）
1. 完整遵循现代Android Jetpack分层架构，Compose全量UI，无XML布局，代码解耦易维护；
2. 本地Room+网络Retrofit双数据源结合，Repository统一管控数据，网络异常具备降级兜底方案；
3. 原生Canvas自定义营养饼图，可视化展示每日营养摄入，交互直观；
4. 完整支持浅色/深色主题，使用DataStore持久保存全部用户个性化配置；
5. 多页面完整业务闭环：记录、搜索、筛选、收藏、数据统计、偏好设置功能齐全；
6. 完善状态处理：加载动画、空数据页面、网络错误提示、屏幕旋转状态不丢失。

## 12. 未来改进方向（可选）
1. 完善数据统计功能，增加周/月营养摄入趋势折线图；
2. 新增扫码识别食物营养功能，拓展录入方式；
3. 增加饮食导出、数据备份功能；
4. 完善权限管理，添加离线缓存全部搜索食物数据；
5. 增加目标营养提醒，当日热量超标弹窗提示；
6. 优化网络请求，增加搜索防抖、请求缓存机制。