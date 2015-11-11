package com.liucl.multiplayer;

import android.widget.VideoView;

import com.liucl.multiplayer.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * ijkPlayer
 * Created by 刘晨龙 on 2015/11/10.
 */
public class IjkPlayerActivity extends PlayerActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_ijk_player;
    }

    @Override
    protected void assignViews() {
        super.assignViews();
        mVideoView = (IjkVideoView) findViewById(R.id.videoview);
    }

    @Override
    protected void configSystem() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

}
