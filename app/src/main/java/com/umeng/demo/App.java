package com.umeng.demo;

import android.app.Application;

import com.umeng.UMAD;
import com.umeng.optimize.AdOptimizer;

/**
 * 应用入口：初始化广告 SDK。
 *
 * 初始化后，SDK 会自动从 assets/ad_config.json 读取各广告位的平台参数。
 * 广告位ID 与 ad_config.json 里的 key 一一对应。
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 默认从 assets/ad_config.json 读取广告位配置
        UMAD.init(this);

        // 可选：配置统一频控（不配置则使用 SDK 内置默认规则）
        // 统一频控在聚合层做总闸门，同类型广告位共享计数，避免各平台各自记频控导致总量超标
        // AdOptimizer.setFreqCapEnabled(false);  // 如需整体关闭频控，取消注释即可（默认开启）
        AdOptimizer.setFreqCap(AdOptimizer.TYPE_SPLASH, 2, 10, 0);        // 开屏：每天2次，最小间隔10秒
        AdOptimizer.setFreqCap(AdOptimizer.TYPE_REWARD, 10, 0, 0);        // 激励视频：每天10次
        AdOptimizer.setFreqCap(AdOptimizer.TYPE_INTERSTITIAL, 5, 180, 0); // 插屏：每天5次，最小间隔180秒
        AdOptimizer.setFreqCap(AdOptimizer.TYPE_BANNER, 0, 60, 20);       // 横幅：每小时20次，最小间隔60秒
    }
}
