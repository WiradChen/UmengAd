package com.umeng.demo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.umeng.advert.FullScreenAd;
import com.umeng.listener.AdLoadListener;
import com.umeng.listener.AdViewListener;

/**
 * 全屏视频 Demo。
 *
 * 广告位ID: 2021839469（见 assets/ad_config.json）
 */
public class FullScreenAdActivity extends AppCompatActivity {

    private TextView tvStatus;
    private FullScreenAd ad;

    private static final String POS_ID = "2021839469";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_common);

        ((TextView) findViewById(R.id.tvTitle)).setText("全屏视频 FullScreen");
        TextView tvPosId = findViewById(R.id.tvPosId);
        tvPosId.setText(getString(R.string.hint_pos_id, POS_ID));
        tvStatus = findViewById(R.id.tvStatus);
        findViewById(R.id.adContainer).setVisibility(android.view.View.GONE);

        findViewById(R.id.btnLoad).setOnClickListener(v -> loadFullScreen());
    }

    private void loadFullScreen() {
        ad = new FullScreenAd(POS_ID);
        ad.enableDebug();

        ad.setAdLoadListener(new AdLoadListener() {
            @Override
            public void onLoad() {
                tvStatus.setText("全屏视频加载成功，正在展示");
                ad.show(FullScreenAdActivity.this);
            }

            @Override
            public void onNoAd(int code, String message) {
                tvStatus.setText("无广告 code=" + code + ", msg=" + message);
                Toast.makeText(FullScreenAdActivity.this, "无广告", Toast.LENGTH_SHORT).show();
            }
        });

        ad.setAdViewListener(new AdViewListener() {
            @Override
            public void onShow() {
                tvStatus.setText("全屏视频已展示");
            }

            @Override
            public void onClose() {
                tvStatus.setText("全屏视频已关闭");
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

        tvStatus.setText("正在加载全屏视频...");
        ad.load(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad = null;
    }
}
