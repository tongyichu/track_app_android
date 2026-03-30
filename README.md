# 户外轨迹 APP - Android Kotlin 客户端

本项目是“户外轨迹 APP”的全新原生 Android 客户端实现，与既有的 React Native 客户端并存。它遵循现代 Android 开发实践，采用 Kotlin、MVVM 架构和 Jetpack 组件。

## 一、环境要求

- **Android Studio**: `Hedgehog | 2023.1.1` 或更高版本。
- **JDK**: `17` 或更高版本。
- **Gradle**: 项目使用的 Gradle Wrapper `8.2`。
- **Android SDK**:
  - `minSdk`: `24` (Android 7.0)
  - `targetSdk`: `34` (Android 14)
  - `compileSdk`: `34`

## 二、配置项

在首次运行项目前，您需要配置必要的 API Key 和后端地址。

1. **创建 `local.properties`**

   在项目根目录（`android-client-kotlin/`）下，复制 `local.properties.example` 文件并重命名为 `local.properties`。

2. **填写配置值**

   打开 `local.properties` 并填入以下字段的真实值：

   ```properties
   # 后端 API 基础地址
   API_BASE_URL=https://your-api-base-url.example.com/

   # 高德地图 Android SDK Key
   AMAP_API_KEY=your-amap-api-key

   # 客户端版本号（用于 Header）
   X_CLIENT_VERSION=1.0.0
   ```

3. **微信登录占位说明**

   微信登录功能目前仅为占位实现，未集成真实 SDK。如需开发，请参考微信开放平台文档，并将 `AppID` 和 `Universal Link` 配置到项目中，通常通过 `BuildConfig` 或安全存储方式管理。

## 三、运行步骤

1. **打开项目**: 使用 Android Studio 打开 `android-client-kotlin/` 目录。
2. **同步 Gradle**: Android Studio 会自动提示同步 Gradle。等待依赖下载完成。
3. **构建并运行**:
   - 选择一个模拟器或连接一台物理设备。
   - 点击 Android Studio 工具栏中的 "Run 'app'" 按钮（或使用快捷键 `Shift+F10`）。

应用将安装并启动。如果未检测到有效登录会话，将首先显示登录页面。

## 四、页面与接口对应关系

| 页面                  | 主要接口 (Method & Path)                                 | 描述                                                           |
| --------------------- | ------------------------------------------------------ | -------------------------------------------------------------- |
| **登录页**            | - (本地处理)                                           | 手机号登录（本地 Session），微信登录占位。                       |
| **首页**              | `GET /api/track/recommend/list`                          | 展示推荐轨迹。                                                 |
| **首页-正在记录**     | `GET /api/track/running?user_id=xxx`                     | 检测到未结束轨迹时展示该状态，按钮变为“正在记录”。               |
| **开始记录**          | `POST /api/track/create`                                 | 点击“开始记录”后调用，创建新轨迹并跳转。                     |
| **轨迹搜索**          | `GET /api/track/search/list`                             | 根据关键词搜索轨迹。                                           |
| **正在记录详情**      | `GET /api/track/{id}/map`, `GET /api/track/{id}/detail`  | 展示实时地图与信息，可暂停/继续/长按结束。                     |
| **轨迹记录结束总结**  | 同上                                                   | 展示地图与汇总信息，可导出图片或上传云端。                     |
| **上传到云端**        | `POST /api/track/{id}/upload_cloud`                      | 上传轨迹到云端后，进入成功反馈页。                             |
| **他人轨迹详情**      | `GET /api/track/{id}/map`, `.../summary`, `.../collect` | 展示他人轨迹、收藏状态，并可“使用轨迹导航”。                   |
| **个人中心**          | `GET /api/user/{uid}/detail`                             | 展示用户昵称、头像、签名。                                       |
| **设置**              | `PUT /api/user/profile/*`                                | 修改头像、用户名、签名、语言；退出登录。                         |
