package com.quvideo.mobile.demo.cloudengine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quvideo.mobile.external.component.cloudcomposite.model.CompositeFinishResponse;
import com.quvideo.mobile.external.component.cloudcomposite.model.CompositePreResponse;
import com.quvideo.mobile.external.component.cloudcomposite.protocal.ICompositeTask;
import com.quvideo.mobile.external.component.cloudcomposite.protocal.MediaType;
import com.quvideo.mobile.external.component.cloudcomposite.protocal.OnCompositeListener;
import com.quvideo.mobile.external.component.cloudengine.QVCloudEngine;
import com.quvideo.mobile.external.component.cloudengine.model.CompositeConfig;
import com.quvideo.mobile.external.component.cloudengine.model.RequestError;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.Filter;
import com.yanzhenjie.album.api.widget.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.quvideo.mobile.demo.cloudengine.PlayActivity.KEY_PLAY_URL;


/**
 * Created by santa on 2020-03-19.
 */
public class CompositeActivity extends AppCompatActivity {

    public final static String KEY_SOURCE_FILE_COUNT = "key_file_count";
    public final static String KEY_TEMPLATE_ID = "key_template_id";
    public final static String KEY_RESOLUTION = "key_resolution";

    private ProgressDialog mProgressDialog;
    private ICompositeTask mICompositeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initView();
        onGallery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放合成任务。
        if (mICompositeTask != null) {
            mICompositeTask.release();
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void initView() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override public void onCancel(DialogInterface dialog) {
                QVCloudEngine.cancelUpload();
            }
        });
    }

    //加载相册
    private void onGallery() {
        int count = getIntent().getIntExtra(KEY_SOURCE_FILE_COUNT, 1);

        Album.video(this)
                .multipleChoice()
                .camera(false)
                .selectCount(count)
                .columnCount(3)
                .widget(Widget.newDarkBuilder(this)
                        .statusBarColor(getResources().getColor(R.color.colorPrimaryDark))
                        .toolBarColor(getResources().getColor(R.color.colorPrimary))
                        .build())
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        // MimeType: image/jpeg, image/png, video/mp4, video/3gp...
                        return attributes.contains("gif");//该句话过滤掉GIF
                    }
                })
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        make(result);
                    }
                }).onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        finish();
                    }
                }).start();

    }

    //发起视频云端合成
    private void make(List<AlbumFile> medias) {
        // todo： 需要做视频压缩处理

        mProgressDialog.show();
        List<CompositeConfig.Media> list = new ArrayList<>();
        for (AlbumFile media : medias) {
            MediaType type = media.getMediaType() == AlbumFile.TYPE_VIDEO ?
                    MediaType.VIDEO : MediaType.IMAGE;
            list.add(new CompositeConfig.Media(type, Uri.parse(media.getPath())));
        }

        long templateId = getIntent().getLongExtra(KEY_TEMPLATE_ID, 1);
        int resolution = getIntent().getIntExtra(KEY_RESOLUTION, 1);

        /**
         *  单张照片
         */
        CompositeConfig config = new CompositeConfig(templateId, CompositeConfig.Resolution.values()[resolution], list);


        QVCloudEngine.composite(config, new OnCompositeListener() {
            @Override
            public void onPreComposite(ICompositeTask task, CompositePreResponse response) {
                mICompositeTask = task;
                Log.d("CompositeDemo", "onPreComposite");
                /**
                 * 这里回调合成预处理，这里可以记录返回businessId，用于退出应用后，查找相应合成历史记录
                 */
            }

            @Override public void onUploadProgress(ICompositeTask task, int progress) {
                Log.d("CompositeDemo", "onUploadProgress:progress = " + progress);
            }

            @Override
            public void onSuccess(ICompositeTask task, CompositeFinishResponse response) {
                mICompositeTask = task;
                Log.d("CompositeDemo", "onSuccess");
                /**
                 * 这里回调成功，获取成功后上报小影标识成功合成并使用。
                 */
                QVCloudEngine.report(Collections.singletonList(response.getFileId()));
                toPlay(response.getFileUrl());
                finish();
            }

            @Override
            public void onFailure(ICompositeTask task, RequestError error, State state, boolean canForceMake) {
                mICompositeTask = task;
                Log.e("CompositeDemo", "onFailure: " + error.getMessage());
                Toast.makeText(CompositeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                /**
                 * 这里回调失败。
                 */
                finish();
            }

            @Override
            public void onNext(ICompositeTask task, State state) {
                /**
                 * 这里回调状态机改变
                 */
                updateProgress(state);
            }
        });
    }

    /**
     * 建议增加动画，做一个假进度条
     * @param state
     */
    private void updateProgress(OnCompositeListener.State state) {
        switch (state) {
            case IDEL:
                updateProgress(0);
                break;
            case UPLOAD:
                updateProgress(20);
                break;
            case COMPOSITE:
                updateProgress(50);
                break;
            case QUERY:
                updateProgress(80);
                break;
            case SUCCESS:
                updateProgress(100);
                mProgressDialog.dismiss();
                break;
            case CANCEL:
                Toast.makeText(getApplicationContext(), "取消上传", Toast.LENGTH_LONG).show();
                finish();
                break;
            default: break;
        }
    }

    private void updateProgress(int value) {
        mProgressDialog.setProgress(value);
    }


    /**
     * 跳转播放页面
     * @param url
     */
    private void toPlay(String url) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(KEY_PLAY_URL, url);
        startActivity(intent);
    }
}
