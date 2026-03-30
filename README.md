# 户外轨迹 App - Android 客户端

基于 Kotlin + MVVM + Retrofit + Glide 的安卓客户端。

## 技术栈

| 组件 | 技术 |
|------|------|
| 语言 | Kotlin |
| 架构 | MVVM (ViewModel + LiveData) |
| 网络 | Retrofit2 + OkHttp4 + Gson |
| 图片 | Glide 4 |
| 地图 | 高德地图 3D SDK + 定位SDK |
| 登录 | 微信SDK + 手机号验证码 |
| 存储 | EncryptedSharedPreferences |
| UI | Material Design Components |

## 项目结构

```
app/src/main/java/com/outdoor/trail/
├── TrailApp.kt                      # Application
├── data/
│   ├── local/TokenManager.kt        # Token安全存储
│   ├── model/Models.kt              # 数据模型
│   ├── remote/
│   │   ├── ApiClient.kt             # Retrofit客户端
│   │   └── ApiService.kt            # API接口定义
│   └── repository/
│       ├── TrackRepository.kt       # 轨迹数据仓库
│       └── UserRepository.kt        # 用户数据仓库
├── service/
│   └── TrackRecordService.kt        # 轨迹记录前台服务
├── ui/
│   ├── login/                        # 登录页
│   ├── home/                         # 首页（含TrackListAdapter）
│   ├── search/                       # 搜索页
│   ├── recording/                    # 正在记录页
│   ├── summary/                      # 轨迹结束总结页
│   ├── detail/                       # 他人轨迹详情页
│   ├── feedback/                     # 上传成功反馈页
│   ├── profile/                      # 个人中心页
│   └── settings/                     # 设置页
└── wxapi/
    └── WXEntryActivity.kt            # 微信回调
```

## 页面对照

| 页面 | Activity | ViewModel | 布局文件 |
|------|----------|-----------|----------|
| 登录页 | LoginActivity | LoginViewModel | activity_login.xml |
| 首页 | HomeActivity | HomeViewModel | activity_home.xml |
| 搜索页 | SearchActivity | SearchViewModel | activity_search.xml |
| 正在记录 | RecordingActivity | RecordingViewModel | activity_recording.xml |
| 轨迹总结 | TrackSummaryActivity | TrackSummaryViewModel | activity_track_summary.xml |
| 他人轨迹详情 | TrackDetailActivity | TrackDetailViewModel | activity_track_detail.xml |
| 上传成功 | UploadSuccessActivity | - | activity_upload_success.xml |
| 个人中心 | ProfileActivity | ProfileViewModel | activity_profile.xml |
| 设置 | SettingsActivity | SettingsViewModel | activity_settings.xml |

## 配置说明

### 1. 高德地图Key

在 `local.properties` 中添加：
```properties
AMAP_API_KEY=your_amap_key
```

### 2. 微信AppID

在 `local.properties` 中添加：
```properties
WECHAT_APP_ID=your_wechat_app_id
```

### 3. 服务端地址

在 `local.properties` 中添加：
```properties
BASE_URL=http://your-server-ip:8080
```

## 编译运行

### 前置条件
- Android Studio Hedgehog+ (2023.1+)
- JDK 17
- Android SDK 34
- Gradle 8.x

### 步骤

1. 用 Android Studio 打开 `outdoor-trail-android/` 目录
2. 在 `local.properties` 中配置 `AMAP_API_KEY` 和 `WECHAT_APP_ID`
3. Sync Gradle
4. 连接Android设备或启动模拟器
5. Run → Run 'app'

## 核心设计决策

### 登录有效期
- 使用 `EncryptedSharedPreferences` 加密存储JWT Token
- 记录最后活跃时间，超过15天未打开自动退出
- 每次打开App时更新活跃时间戳

### 轨迹记录
- 使用前台服务 `TrackRecordService` 持续定位
- 高德融合定位：GNSS + WiFi + 基站 + 传感器
- 2秒采样间隔，精度>50m自动丢弃
- 支持暂停/继续/结束

### 网络层
- 所有请求自动携带公共Header（X-User-ID等5个字段）
- Repository层统一包装 `Result<T>` 返回值
- 支持JWT认证和开发模式X-User-ID认证

### 定位技术
- 基于高德SDK的融合定位能力
- 集成GNSS（GPS/北斗/GLONASS/Galileo）+ AGNSS + WiFi + 基站 + 传感器
- 高精度模式，GPS优先
- 支持后台持续定位（前台服务+FOREGROUND_SERVICE_LOCATION）

## 响应式布局

- 使用 `ConstraintLayout` 实现自适应布局
- 轨迹列表使用 `RecyclerView` + `DiffUtil` 高效更新
- 地图区域支持全屏/半屏切换
- 适配不同屏幕尺寸和分辨率
