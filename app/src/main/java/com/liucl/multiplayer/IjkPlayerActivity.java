package com.liucl.multiplayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.liucl.multiplayer.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * ijkPlayer
 * Created by 刘晨龙 on 2015/11/10.
 */
public class IjkPlayerActivity extends AppCompatActivity {

    private IjkVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player);
        Uri[] uri = getUri();

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.videoview);
//        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        mVideoView.setVideoPath(uri[0].toString());
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.start();
            }
        });
        mVideoView.start();

    }


    /**
     * 获取Uri
     * 1、视频地址
     * 2、弹幕地址
     * @return
     */
    public Uri[] getUri() {
        Uri[] url = new Uri[2];
        url[0] = Uri.parse(getIntent().getStringExtra("videoPath"));
        url[1] = Uri.parse(getIntent().getStringExtra("danmakuPath"));
        return url;
    }

}
