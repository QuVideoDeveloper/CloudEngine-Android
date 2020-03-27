package com.quvideo.mobile.demo.cloudengine;

import android.os.Bundle;
import android.view.MenuItem;

import com.quvideo.mobile.demo.cloudengine.adapter.VideoAdapter;
import com.quvideo.mobile.external.component.cloudcomposite.model.VideoResponse;
import com.quvideo.mobile.external.component.cloudengine.QVCloudEngine;
import com.quvideo.mobile.external.component.cloudengine.model.RequestError;
import com.quvideo.mobile.external.component.cloudengine.model.VideoConfig;
import com.quvideo.mobile.external.component.cloudengine.protocal.OnVideoListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by santa on 2020-03-20.
 */
public class VideosActivity extends AppCompatActivity {
    private VideoAdapter mHomeAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private int pageIndex = 1;
    private boolean hasMore = false;
    private List<VideoResponse.Data> videos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        initView();
        requestData();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mHomeAdapter = new VideoAdapter(videos);
        recyclerView.setAdapter(mHomeAdapter);

        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout1 -> {
            if (hasMore) {
                pageIndex ++;
                requestData();
            } else {
                mSmartRefreshLayout.finishLoadMore();
            }
        });
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("历史合成视频列表(点击播放)");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void requestData() {
        /**
         * 请求历史合成视频
         */
        QVCloudEngine.getVideos(new VideoConfig(pageIndex, 10), new OnVideoListener() {

            @Override
            public void onSuccess(VideoResponse response) {
                mSmartRefreshLayout.finishLoadMore();
                hasMore = response.isHasMore();
                videos.addAll(response.getData());
                mHomeAdapter.notifyDataSetChanged();
                QVCloudEngine.report(response.getFileIds());
            }

            @Override
            public void onFailure(RequestError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
