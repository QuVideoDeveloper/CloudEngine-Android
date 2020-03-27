package com.quvideo.mobile.demo.cloudengine;

import android.app.Application;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

import androidx.multidex.MultiDexApplication;

/**
 * Created by santa on 2020-03-18.
 */


public class App extends MultiDexApplication {

    public final static String APP_KEY = "此处填写分发的appKey";
    public final static String APP_SECRET = "此处填写分发的appSecret";

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Album.initialize(AlbumConfig.newBuilder(this).
                setAlbumLoader(new AlbumLoader() {
                    @Override
                    public void load(ImageView imageView, AlbumFile albumFile) {
                        load(imageView, albumFile.getPath());
                    }

                    @Override
                    public void load(ImageView imageView, String url) {
                        Glide.with(imageView.getContext())
                                .load(url)
                                .into(imageView);
                    }
                }).build());

    }
}

