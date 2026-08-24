package com.umeng.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.umeng.advert.FeedAd;
import com.umeng.advert.FeedAdData;
import com.umeng.listener.FeedLoadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 信息流广告 Demo。
 *
 * 广告位ID: 2540096452（见 assets/ad_config.json）
 * 广告加载后通过 FeedLoadListener.onLoad 返回 List&lt;FeedAdData&gt;，
 * 将 FeedAdData.getAdView() 渲染进 RecyclerView 列表项即可。
 */
public class FeedAdActivity extends AppCompatActivity {

    private static final String POS_ID = "2540096452";
    private static final int DATA_COUNT = 20;

    private FeedAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FeedAdapter();
        recyclerView.setAdapter(adapter);

        loadFeed();
    }

    private void loadFeed() {
        FeedAd advert = new FeedAd(POS_ID);
        advert.enableDebug();
        advert.setAdLoadListener(new FeedLoadListener() {
            @Override
            public void onLoad(List<FeedAdData> list) {
                List<String> data = new ArrayList<>();
                for (int i = 1; i <= DATA_COUNT; i++) {
                    data.add("列表内容 Item " + i);
                }
                adapter.setData(data, list);
            }

            @Override
            public void onNoAd(int code, String message) {
                Toast.makeText(FeedAdActivity.this,
                        "无广告 code=" + code + ", msg=" + message, Toast.LENGTH_SHORT).show();
                List<String> data = new ArrayList<>();
                for (int i = 1; i <= DATA_COUNT; i++) {
                    data.add("列表内容 Item " + i);
                }
                adapter.setData(data, Collections.emptyList());
            }
        });
        advert.load(this);
    }

    /** 列表适配器：数据项 + 广告项混合 */
    private static class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_DATA = 1;
        private static final int TYPE_AD = 2;

        private final List<Object> items = new ArrayList<>();
        private final ViewGroup.LayoutParams adParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        void setData(List<String> data, List<FeedAdData> adverts) {
            items.clear();
            items.addAll(data);
            if (!adverts.isEmpty()) {
                // 在列表中间位置插入广告
                int perSpan = data.size() / (adverts.size() + 1);
                List<Object> mixed = new ArrayList<>(data);
                for (int i = 0; i < adverts.size(); i++) {
                    adverts.get(i).render();
                    mixed.add((i + 1) * perSpan, adverts.get(i));
                }
                items.clear();
                items.addAll(mixed);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position) instanceof FeedAdData ? TYPE_AD : TYPE_DATA;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_AD) {
                FrameLayout container = new FrameLayout(parent.getContext());
                container.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new AdHolder(container);
            }
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_feed_data, parent, false);
            return new DataHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DataHolder) {
                ((DataHolder) holder).textView.setText(items.get(position).toString());
            } else if (holder instanceof AdHolder) {
                AdHolder adHolder = (AdHolder) holder;
                adHolder.container.removeAllViews();
                View adView = ((FeedAdData) items.get(position)).getAdView();
                if (adView != null) {
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }
                    adHolder.container.addView(adView, adParams);
                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static class DataHolder extends RecyclerView.ViewHolder {
            TextView textView;

            DataHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text1);
            }
        }

        private static class AdHolder extends RecyclerView.ViewHolder {
            FrameLayout container;

            AdHolder(FrameLayout container) {
                super(container);
                this.container = container;
            }
        }
    }
}
