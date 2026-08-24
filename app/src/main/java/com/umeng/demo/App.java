package com.umeng.demo;

import android.app.Application;

import com.umeng.UMAD;

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
    }
}
