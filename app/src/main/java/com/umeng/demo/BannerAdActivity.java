package com.umeng.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.umeng.advert.BannerAd;
import com.umeng.listener.AdLoadListener;
import com.umeng.listener.AdViewListener;

/**
 * 横幅广告 Demo。
 *
 * 广告位ID: 2208423313（见 assets/ad_config.json）
 */
public class BannerAdActivity extends AppCompatActivity {

    private FrameLayout adContainer;
    private TextView tvStatus;
    private Button btnLoad;
    private BannerAd ad;

    private static final String POS_ID = "2208423313";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_common);

        ((TextView) findViewById(R.id.tvTitle)).setText("横幅广告 Banner");
        TextView tvPosId = findViewById(R.id.tvPosId);
        tvPosId.setText(getString(R.string.hint_pos_id, POS_ID));
        tvStatus = findViewById(R.id.tvStatus);
        adContainer = findViewById(R.id.adContainer);
        btnLoad = findViewById(R.id.btnLoad);

        btnLoad.setOnClickListener(v -> loadBanner());
    }

    private void loadBanner() {
        // 每次加载前清空容器
        adContainer.removeAllViews();
        adContainer.setVisibility(FrameLayout.VISIBLE);

        ad = new BannerAd(POS_ID);
        ad.enableDebug();

        ad.setAdLoadListener(new AdLoadListener() {
            @Override
            public void onLoad() {
                tvStatus.setText("横幅加载成功，已展示");
                // 展示到容器（宽度取容器宽度，高度传0使用默认）
                ad.show(adContainer);
            }

            @Override
            public void onNoAd(int code, String message) {
                tvStatus.setText("无广告 code=" + code + ", msg=" + message);
                Toast.makeText(BannerAdActivity.this, "无广告", Toast.LENGTH_SHORT).show();
            }
        });

        ad.setAdViewListener(new AdViewListener() {
            @Override
            public void onShow() {
                tvStatus.setText("横幅已展示");
            }

            @Override
            public void onClose() {
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

        tvStatus.setText("正在加载横幅广告...");
        int width = adContainer.getWidth() > 0 ? adContainer.getWidth() : getResources().getDisplayMetrics().widthPixels;
        ad.load(this, width, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad = null;
    }
}
