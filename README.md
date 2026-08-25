# UMengAd Demo（优盟广告引擎接入示例）

一个精简、清晰的**请求广告 Demo**，演示如何接入优盟广告引擎（UMAD SDK），覆盖所有主流广告类型：

| 广告类型 | 说明 |
|---------|------|
| 开屏广告 (Splash) | 应用启动时全屏展示 |
| 横幅广告 (Banner) | 页面内固定区域展示 |
| 插屏广告 (Interstitial) | 页面切换时弹出 |
| 激励视频 (RewardVideo) | 用户看完后发奖 |
| 全屏视频 (FullScreen) | 全屏展示的视频广告 |
| 信息流广告 (Feed) | 嵌在列表中的原生广告 |

---

## 一、项目结构

```
UMengAd/
├── app/                        # Demo 应用
│   ├── libs/
│   │   └── umad-core-1.0.0.aar # ★ 优盟广告 SDK（已混淆，核心代码不开放）
│   └── src/main/
│       ├── assets/ad_config.json    # ★ 广告位配置（平台参数都在这）
│       ├── java/com/umeng/demo/     # Demo 代码
│       │   ├── App.java             # 应用入口，初始化 SDK
│       │   ├── MainActivity.java    # 首页，各广告入口（深色科技风卡片式）
│       │   ├── SplashAdActivity.java   # 全屏开屏页
│       │   ├── BannerAdActivity.java
│       │   ├── InterstitialAdActivity.java
│       │   ├── RewardVideoAdActivity.java
│       │   ├── FullScreenAdActivity.java
│       │   └── FeedAdActivity.java
│       └── res/                     # 布局与资源（logo.png 为应用图标）
├── settings.gradle
└── build.gradle
```

> **说明**：Demo 通过 `app/libs/umad-core-1.0.0.aar` 依赖广告 SDK。

---

## 二、接入步骤（三步）

### 1. 引入 SDK

把 `umad-core-1.0.0.aar` 放进 `app/libs/`，并引入第三方广告源库：

```groovy
// app/build.gradle
dependencies {
    // 方式一：直接依赖整个 libs 文件夹（简单）
    implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])

    // 方式二：单独依赖
    // implementation files('libs/umad-core-1.0.0.aar')

    // 基础库（必须）
    implementation 'androidx.appcompat:appcompat:1.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.0.0'
    implementation 'com.google.android.material:material:1.6.0'  // 部分广告平台（如 MS 美数）依赖 CardView 等组件

    // 瑞狮 SDK（AdCore compileOnly，需在应用侧提供）
    implementation('cn.vlion.inland:vlion-core-ec:7.00.81') {
        exclude group: 'cn.vlion.inland', module: 'vlion-j'
        exclude group: 'cn.vlion.inland', module: 'vlion-wm-sdk'
    }
}
```

> **注意**：广告 SDK 内部聚合了多家广告平台（穿山甲/快手/百度/优量汇等），`ad_config.json` 里用到哪个平台，就引入对应平台的 SDK（AAR 内 `compileOnly`，需在应用侧提供）。用不到的平台无需引入，避免包体过大。
>
> Demo 的 `app/libs/` 里已包含常用平台的 AAR，可按需选用。

### 2. 配置广告位（assets/ad_config.json）

在 `app/src/main/assets/ad_config.json` 里配置每个广告位的平台参数。**这是唯一需要填广告参数的地方，代码里不硬编码。**

```json
{
  "2629995460": {
    "advertises": [
      { "platform": "ST", "appId": "25202", "adId": "10970720" },
      { "platform": "RS", "appId": "A0732", "adId": "P4009", "appKey": "49fbef..." }
    ]
  }
}
```

- **key**：广告位 ID，代码里 `new XXXAd("广告位ID")` 用它对应
- **advertises[]**：该广告位的广告源列表
  - `platform`：平台代号（上推 `ST`(佳投)、穿山甲 `CSJ`、快手 `KS`、百度 `BD`、优量汇 `YLH`、瑞狮 `RS` 等，支持简称或全称）
  - `appId` / `adId` / `appKey`：对应平台的 SDK 参数
  - 多个源用数组元素区分（瀑布流）

> ⚠️ JSON 不支持注释，`ad_config.json` 里不要写 `//` 或 `/* */`，否则 SDK 初始化解析失败。

### 3. 初始化 SDK（App.java）

在 `Application.onCreate()` 里初始化，**SDK 会自动读取 ad_config.json**：

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UMAD.init(this);   // 自动从 assets/ad_config.json 读取广告位配置
    }
}
```

---

## 三、各广告类型调用

所有广告都是同样的套路：**创建广告对象 → 设置监听 → load() → 在 onLoad 回调里 show()**。

### 3.1 开屏广告 SplashAd

```java
SplashAd ad = new SplashAd("2629995460");   // 广告位ID
ad.enableDebug();                            // 调试日志（上线可去掉）

ad.setAdLoadListener(new AdLoadListener() {
    @Override public void onLoad() {
        ad.show(container);                  // 展示到全屏容器
    }
    @Override public void onNoAd(int code, String message) {
        // 无广告，进入主页面
    }
});
ad.load(this);                               // Activity
```

- `load(Activity)`：加载
- `show(ViewGroup)`：展示到容器（一般是全屏 FrameLayout）
- 建议在启动页 `onCreate` 里立即调用
- 开屏页建议设为**全屏沉浸式**（隐藏状态栏 + 导航栏），体验更好

### 3.2 横幅广告 BannerAd

```java
BannerAd ad = new BannerAd("2208423313");
ad.enableDebug();
ad.setAdLoadListener(new AdLoadListener() {
    @Override public void onLoad() {
        ad.show(container);                  // 展示到页面内容器
    }
    @Override public void onNoAd(int code, String message) { }
});
ad.load(this, width, 0);                     // width=容器宽，height 传 0 用默认
```

- `load(Activity, int width, int height)`：宽度传容器宽度，高度传 `0` 使用平台默认
- `show(ViewGroup)`：展示到指定容器

### 3.3 插屏广告 InterstitialAd

```java
InterstitialAd ad = new InterstitialAd("2102886533");
ad.enableDebug();
ad.setAdLoadListener(new AdLoadListener() {
    @Override public void onLoad() {
        ad.show(activity);                   // 弹出插屏
    }
    @Override public void onNoAd(int code, String message) { }
});
ad.load(activity);
```

- `load(Activity)`：加载
- `show(Activity)`：弹出展示

### 3.4 激励视频 RewardVideoAd

```java
RewardVideoAd ad = new RewardVideoAd("2919646015");
ad.enableDebug();

// 视频播放回调
ad.setVideoPlayListener(new VideoPlayListener() {
    @Override public void onPlayStart() { }
    @Override public void onPlaySkip() { }
    @Override public void onPlayFinish() { }
});

// 视图回调（含发奖）
ad.setAdViewListener(new AdViewListener() {
    @Override public void onShow() { }
    @Override public void onClose() { }
    @Override public void onClick() { }
    @Override public void onReward() {      // ★ 满足发奖条件
        // 在这里发放奖励
    }
    @Override public void onResourceError() { }
});

ad.setAdLoadListener(new AdLoadListener() {
    @Override public void onLoad() {
        ad.show(activity);                   // 播放激励视频
    }
    @Override public void onNoAd(int code, String message) { }
});
ad.load(context);                            // Context
```

- **发奖时机**：在 `AdViewListener.onReward()` 回调里发奖
- `load(Context)` / `show(Activity)`

### 3.5 全屏视频 FullScreenAd

```java
FullScreenAd ad = new FullScreenAd("2021839469");
ad.enableDebug();
ad.setAdLoadListener(new AdLoadListener() {
    @Override public void onLoad() {
        ad.show(activity);                   // 全屏展示
    }
    @Override public void onNoAd(int code, String message) { }
});
ad.load(activity);
```

- `load(Activity)` / `show(Activity)`

### 3.6 信息流广告 FeedAd

信息流广告嵌在 RecyclerView 列表里。加载后用 `FeedAdData.getAdView()` 拿到广告视图，插到列表项中：

```java
FeedAd advert = new FeedAd("2540096452");
advert.enableDebug();
advert.setAdLoadListener(new FeedLoadListener() {
    @Override public void onLoad(List<FeedAdData> list) {
        // list 里的每个 FeedAdData 对应一个广告
        for (FeedAdData data : list) {
            data.render();                          // 渲染广告
            View adView = data.getAdView();         // 获取广告视图
            // 将 adView 添加到列表项容器
        }
    }
    @Override public void onNoAd(int code, String message) { }
});
advert.load(activity);
```

> 详见 `FeedAdActivity.java` 里的 `FeedAdapter`，演示了广告项与内容项混排。

---

## 四、公共回调接口

所有广告类型共享以下监听器：

### AdLoadListener（加载结果）
```java
public interface AdLoadListener {
    void onLoad();                          // 加载成功
    void onNoAd(int code, String message);  // 无广告/失败
}
```

### AdViewListener（展示/交互）
```java
public interface AdViewListener {
    void onShow();          // 展示
    void onClose();         // 关闭
    void onClick();         // 点击
    void onReward();        // 满足发奖条件（激励视频）
    void onResourceError(); // 资源错误
}
```

### VideoPlayListener（视频播放，激励/全屏）
```java
public interface VideoPlayListener {
    void onPlayStart();     // 开始播放
    void onPlaySkip();      // 跳过
    void onPlayFinish();    // 播放完成
}
```

### FeedLoadListener（信息流）
```java
public interface FeedLoadListener {
    void onLoad(List<FeedAdData> list);     // 加载成功，返回广告数据列表
    void onNoAd(int code, String message);  // 无广告
}
```

---

## 五、FAQ

### 1. 广告位配置可以动态传吗？
可以。除了默认读 `ad_config.json`，也支持代码传入 String map：

```java
Map<String, String> adPosMap = new HashMap<>();
adPosMap.put("2629995460", "ST,25202,10970720;RS,A0732,P4009,49fbef...");
UMAD.init(this, adPosMap);   // 传入配置，多源用 ";" 分隔
```

读取优先级：**ad_config.json 优先，String map 兜底**。

### 2. ad_config.json 不存在会怎样？
不会崩溃。SDK 内部 try-catch 兜底返回空配置，只是该广告位拉不到广告（走 `onNoAd`）。

### 3. 平台代号支持哪些写法？
支持简称（`CSJ`/`KS`/`BD`/`YLH`/`RS`/`ST` 等）或全称（`CHUANSHANJIA`/`KUAISHOU` 等），`Platform` 枚举会自动识别。
> **区分**：`ST`(上推) 是佳投 advista SDK；`CSJ` 是穿山甲，两者是不同平台。

### 4. 调试怎么看？
调用 `ad.enableDebug()` 可打开调试日志，上线前移除即可。

### 5. 启动报 ClassNotFoundException（找不到 XXXFileProvider）？
这是**第三方平台 SDK 未引入**导致。SDK 的 manifest 会声明各平台用到的 FileProvider，但对应的平台 SDK 类需由你在应用侧引入。

**解决**：按 `ad_config.json` 里用到的平台，把对应平台 SDK 加到依赖里。用不到的平台不要声明 provider 相关类。例如报 `com.qq.e.comm.GDTFileProvider` 找不到，说明你用了优量汇但没引入 GDT SDK（或反了）。

### 6. 打包 OOM（内存溢出）？
如果 `libs/` 里全量引入所有平台 SDK，包体会很大，低内存机器编译可能 OOM。

**解决**：只引入 `ad_config.json` 里实际用到的平台 SDK，没用的从 `libs/` 里移除。
Demo 默认全部放在 libs 里方便切换，正式集成建议按需引入。

---

## 六、构建

```bash
# 在项目根目录（需已配置 Android SDK）
./gradlew assembleDebug        # Debug 包
./gradlew assembleRelease      # Release 包
```

生成的 APK 位于：
- Debug：`app/build/outputs/apk/debug/app-debug.apk`
- Release：`app/build/outputs/apk/release/app-release.apk`

---

## 七、SDK 发布说明（维护者）

### 1. 构建混淆后的核心 AAR

广告 SDK 核心（`AdCore`）在 **UMAD 项目**里维护（与本 Demo 独立）。
> ⚠️ UMAD 项目**只编辑 `main` 分支**，远程 `master` 已删除。

发布新版 AAR：

```bash
cd ../UMAD
git pull                     # 先更新代码
./gradlew :AdCore:assembleRelease
```

生成的 AAR 会输出到：
- `AdCore/build/outputs/aar/AdCore-release.aar`
- `app/libs/ZhaiXin_2.7.8_release.aar`（自动复制，版本号以实际为准）

### 2. 混淆保护

AdCore 通过 R8 在打包时混淆核心代码：
- **保留（不混淆）**：对外公共 API — `com.umeng.UMAD`、`com.umeng.advert.*`、`com.umeng.listener.*`，以及平台适配器 `com.umeng.adapter.*`（AdapterRegistry 用 `Class.forName` 字符串加载，混淆会导致"未注册广告加载类"）
- **混淆**：内部实现 — `com.umeng.manager.*`、`com.umeng.config.*`、`com.umeng.util.*`、`request` 等全部混淆为 `a/b/c` 短类名

关键配置（`AdCore/build.gradle` + `proguard-api.pro`）：

```groovy
buildTypes {
    release {
        minifyEnabled true
        consumerProguardFiles 'consumer-rules.pro'  // 传给集成方，保公共 API
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-api.pro'
    }
}
```

### 3. 更新 Demo 里的 AAR

打包出新版 AAR 后，复制到 Demo 并重命名：

```bash
cp ../UMAD/app/libs/ZhaiXin_xxx_release.aar app/libs/umad-core-x.x.x.aar
```

### 4. 代码提交规则

改完代码后直接 `git commit + git push` 到远程 `main`，不需要逐项确认。

### 5. 发布给第三方

将以下内容交付给集成方：
1. `umad-core-x.x.x.aar`（已混淆的核心 SDK）
2. 第三方广告源 SDK（穿山甲/快手/百度/优量汇等 AAR）
3. 本 README（接入文档）

> ⚠️ **重要**：`assets/ad_config.json` 里的广告位配置由集成方自行填写自己的平台参数，不随 SDK 分发。
