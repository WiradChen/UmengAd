package com.umeng.demo;

import android.os.Bundle;
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
    private TextView tvPosId;
    private SplashAd ad;

    private static final String POS_ID = "2629995460";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_common);

        tvTitle("开屏广告 Splash");
        tvPosId = findViewById(R.id.tvPosId);
        tvPosId.setText(getString(R.string.hint_pos_id, POS_ID));
        tvStatus = findViewById(R.id.tvStatus);
        adContainer = findViewById(R.id.adContainer);

        // 开屏广告建议在页面启动时自动加载展示
        findViewById(R.id.btnLoad).setOnClickListener(v -> loadSplash());
        loadSplash();
    }

    private void tvTitle(CharSequence title) {
        TextView tv = findViewById(R.id.tvTitle);
        tv.setText(title);
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
                tvStatus.setText("广告加载成功，已展示");
            }

            @Override
            public void onNoAd(int code, String message) {
                tvStatus.setText("无广告回调 code=" + code + ", msg=" + message);
                Toast.makeText(SplashAdActivity.this, "无广告", Toast.LENGTH_SHORT).show();
            }
        });

        ad.setAdViewListener(new AdViewListener() {
            @Override
            public void onShow() {
                tvStatus.setText("开屏已展示");
            }

            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onClick() {
                tvStatus.setText("广告被点击");
            }

            @Override
            public void onReward() {
            }

            @Override
            public void onResourceError() {
            }
        });

        tvStatus.setText("正在加载开屏广告...");
        ad.load(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad = null;
    }
}
