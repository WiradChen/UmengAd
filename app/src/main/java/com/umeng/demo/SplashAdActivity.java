package com.umeng.demo;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.umeng.advert.SplashAd;
import com.umeng.listener.AdLoadListener;
import com.umeng.listener.AdViewListener;

/**
 * 开屏广告 Demo。
 *
 * 广告位ID: 2629995460（见 assets/ad_config.json）
 */
public class SplashAdActivity extends AppCompatActivity {

    private FrameLayout adContainer;
    private TextView tvStatus;
    private SplashAd ad;

    private static final String POS_ID = "2629995460";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏：隐藏状态栏 + 导航栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_splash);

        tvStatus = findViewById(R.id.tvStatus);
        adContainer = findViewById(R.id.adContainer);

        // 开屏广告建议在页面启动时自动加载展示
        loadSplash();
    }

    private void loadSplash() {
        ad = new SplashAd(POS_ID);
        ad.enableDebug();

        ad.setAdLoadListener(new AdLoadListener() {
            @Override
            public void onLoad() {
                // 加载成功，展示到容器
                adContainer.setVisibility(FrameLayout.VISIBLE);
                ad.show(adContainer);
                tvStatus.setVisibility(View.GONE);
            }

            @Override
            public void onNoAd(int code, String message) {
                tvStatus.setText("无广告 code=" + code + " " + message);
                tvStatus.setVisibility(View.VISIBLE);
                Toast.makeText(SplashAdActivity.this, "无广告", Toast.LENGTH_SHORT).show();
            }
        });

        ad.setAdViewListener(new AdViewListener() {
            @Override
            public void onShow() {
                tvStatus.setVisibility(View.GONE);
            }

            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onClick() {
                // 广告被点击
            }

            @Override
            public void onReward() {
            }

            @Override
            public void onResourceError() {
            }
        });

        tvStatus.setText("正在加载开屏广告...");
        tvStatus.setVisibility(View.VISIBLE);
        ad.load(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad = null;
    }
}
