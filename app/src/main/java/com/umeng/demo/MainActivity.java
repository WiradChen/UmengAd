package com.umeng.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.umeng.UMAD;

/**
 * Demo 首页：展示各种广告类型的接入入口。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(getString(R.string.sdk_version, UMAD.getVersion()));

        Button btnSplash = findViewById(R.id.btnSplash);
        Button btnBanner = findViewById(R.id.btnBanner);
        Button btnInterstitial = findViewById(R.id.btnInterstitial);
        Button btnReward = findViewById(R.id.btnReward);
        Button btnFullScreen = findViewById(R.id.btnFullScreen);
        Button btnFeed = findViewById(R.id.btnFeed);

        btnSplash.setOnClickListener(v -> startActivity(new Intent(this, SplashAdActivity.class)));
        btnBanner.setOnClickListener(v -> startActivity(new Intent(this, BannerAdActivity.class)));
        btnInterstitial.setOnClickListener(v -> startActivity(new Intent(this, InterstitialAdActivity.class)));
        btnReward.setOnClickListener(v -> startActivity(new Intent(this, RewardVideoAdActivity.class)));
        btnFullScreen.setOnClickListener(v -> startActivity(new Intent(this, FullScreenAdActivity.class)));
        btnFeed.setOnClickListener(v -> startActivity(new Intent(this, FeedAdActivity.class)));
    }
}
