package com.umeng.demo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.umeng.advert.RewardVideoAd;
import com.umeng.listener.AdLoadListener;
import com.umeng.listener.AdViewListener;
import com.umeng.listener.VideoPlayListener;

/**
 * 激励视频 Demo。
 *
 * 广告位ID: 2919646015（见 assets/ad_config.json）
 */
public class RewardVideoAdActivity extends AppCompatActivity {

    private TextView tvStatus;
    private RewardVideoAd ad;

    private static final String POS_ID = "2919646015";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_common);

        ((TextView) findViewById(R.id.tvTitle)).setText("激励视频 RewardVideo");
        TextView tvPosId = findViewById(R.id.tvPosId);
        tvPosId.setText(getString(R.string.hint_pos_id, POS_ID));
        tvStatus = findViewById(R.id.tvStatus);
        findViewById(R.id.adContainer).setVisibility(android.view.View.GONE);

        findViewById(R.id.btnLoad).setOnClickListener(v -> loadRewardVideo());
    }

    private void loadRewardVideo() {
        ad = new RewardVideoAd(POS_ID);
        ad.enableDebug();

        ad.setAdLoadListener(new AdLoadListener() {
            @Override
            public void onLoad() {
                tvStatus.setText("激励视频加载成功，正在播放");
                ad.show(RewardVideoAdActivity.this);
            }

            @Override
            public void onNoAd(int code, String message) {
                tvStatus.setText("无广告 code=" + code + ", msg=" + message);
                Toast.makeText(RewardVideoAdActivity.this, "无广告", Toast.LENGTH_SHORT).show();
            }
        });

        // 视频播放回调
        ad.setVideoPlayListener(new VideoPlayListener() {
            @Override
            public void onPlayStart() {
                tvStatus.setText("视频开始播放");
            }

            @Override
            public void onPlaySkip() {
                tvStatus.setText("视频被跳过");
            }

            @Override
            public void onPlayFinish() {
                tvStatus.setText("视频播放完成");
            }
        });

        // 广告视图回调
        ad.setAdViewListener(new AdViewListener() {
            @Override
            public void onShow() {
                tvStatus.setText("激励视频已展示");
            }

            @Override
            public void onClose() {
                tvStatus.setText("激励视频已关闭");
            }

            @Override
            public void onClick() {
                tvStatus.setText("广告被点击");
            }

            @Override
            public void onReward() {
                // 满足发奖条件
                Toast.makeText(RewardVideoAdActivity.this, "奖励已发放", Toast.LENGTH_SHORT).show();
                tvStatus.setText("已发放奖励");
            }

            @Override
            public void onResourceError() {
            }
        });

        tvStatus.setText("正在加载激励视频...");
        ad.load(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad = null;
    }
}
