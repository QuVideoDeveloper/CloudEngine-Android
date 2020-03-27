package com.quvideo.mobile.demo.cloudengine;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quvideo.mobile.demo.cloudengine.adapter.HomeAdapter;
import com.quvideo.mobile.external.component.cloudengine.QVCloudEngine;
import com.quvideo.mobile.external.component.cloudengine.model.RequestError;
import com.quvideo.mobile.external.component.cloudengine.model.TemplateConfig;
import com.quvideo.mobile.external.component.cloudengine.model.TemplateResponse;
import com.quvideo.mobile.external.component.cloudengine.protocal.OnTemplateListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    //分页请求
    private int pageIndex = 1;
    private boolean hasMore = false;

    //素材列表
    private List<TemplateResponse.Data> templates = new ArrayList<>();

    private HomeAdapter mHomeAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        requestData();

        // 申请权限
        verifyStoragePermissions(this);
    }

    //申请相册权限
    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            // 授权被允许
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
            }
        }
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton actionButton = findViewById(R.id.float_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mHomeAdapter = new HomeAdapter(templates);
        recyclerView.setAdapter(mHomeAdapter);
        actionButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, VideosActivity.class))
        );


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
    }

    private void requestData() {
        /**
         * 获取素材列表
         */
        QVCloudEngine.getTemplates(new TemplateConfig(pageIndex, 10), new OnTemplateListener() {
            @Override
            public void onSuccess(TemplateResponse response) {
                mSmartRefreshLayout.finishLoadMore();
                hasMore = response.isHasMore();
                templates.addAll(response.getData());
                mHomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(RequestError error) {

            }
        });
    }

}
