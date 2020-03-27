package com.quvideo.mobile.demo.cloudengine;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by santa on 2020-03-20.
 */
public class PlayActivity extends AppCompatActivity {
    public final static String KEY_PLAY_URL = "key_play_url";

    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        playVideo();
    }

    private void initView() {
        mPlayerView = findViewById(R.id.play_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
        }
    }


    /**
     * 播放视频
     */
    private void playVideo() {
        String url = getIntent().getStringExtra(KEY_PLAY_URL);
        mPlayer = new SimpleExoPlayer.Builder(this).build();
        mPlayerView.setPlayer(mPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, ""), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        Uri mp4VideoUri = Uri.parse(url);
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
                dataSourceFactory, extractorsFactory, null, null);

        mPlayer.setPlayWhenReady(true);
        mPlayer.prepare(videoSource);
    }
}
