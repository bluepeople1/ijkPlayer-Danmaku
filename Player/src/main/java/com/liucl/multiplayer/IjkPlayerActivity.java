package com.liucl.multiplayer;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.liucl.multiplayer.widget.media.ControllerView;
import com.liucl.multiplayer.widget.media.IjkVideoView;

import master.flame.danmaku.ui.widget.DanmakuView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * ijkPlayer
 * Created by 刘晨龙 on 2015/11/10.
 */
public class IjkPlayerActivity extends Activity implements ControllerView.IMediaCenter, ControllerView.DamakuCenter {

    private IjkVideoView mIjkVideoView;

    private ControllerView mControllerView;
    private RelativeLayout loadvideo;
    private AnimationDrawable anim;
    private FrameLayout mFrameLayout;


    private IMediaPlayer mediaPlayer;
    private Damaku damaku;
    private Uri[] uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player);
        assignViews();
        configSystem();
        //初始化播放器
        initPlay();
        addListener();
    }

    private void addListener() {

    }

    private void assignViews() {
        mIjkVideoView = (IjkVideoView) findViewById(R.id.videoview);
        mControllerView = (ControllerView) findViewById(R.id.contentPanel);
        mFrameLayout = (FrameLayout) findViewById(R.id.fl_content);
    }

    protected void configSystem() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mControllerView.setmImIMediaCenter(this);
        mControllerView.setmDamakuCenter(this);
    }
    ///////////////////////////////////////////////////////////////////////////
    // 方法逻辑
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 加载数据准备播放
     */
    private void initPlay() {
        //加载控制器
        getUri();
        if (uri != null) {
            mIjkVideoView.setVideoURI(uri[0]);
            damaku = new Damaku(this);
            mControllerView.setPlayState(ControllerView.STATE_PARPER);
            mIjkVideoView.setOnPreparedListener(new VideoPreparedListener());
            //加载等待动画
            loadvideo = (RelativeLayout) findViewById(R.id.loadvideo);
            ImageView imageView = (ImageView) findViewById(R.id.anim);
            anim = (AnimationDrawable) imageView.getBackground();
            anim.start();
        }
    }


    /**
     * 获取Uri
     * 1、视频地址
     * 2、弹幕地址
     *
     * @return
     */
    public void getUri() {
        uri = new Uri[2];
        uri[0] = Uri.parse(getIntent().getStringExtra("videoPath"));
        uri[1] = Uri.parse(getIntent().getStringExtra("danmakuPath"));
    }

    @Override
    public int updataProgressInfo() {
        return (int) mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean switchPlayOrPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            damaku.pause();
        } else {
            mediaPlayer.start();
            damaku.resume();
        }
        return mediaPlayer.isPlaying();
    }

    @Override
    public int seekToPosition(int progress) {
        mediaPlayer.seekTo(progress);
        return (int) mediaPlayer.getCurrentPosition();
    }

    @Override
    public void start() {
        damaku.start();
    }

    @Override
    public void hide() {
        damaku.hide();
    }

    @Override
    public void sendDamaku(String text) {
        damaku.sendDamaku(text);
    }

    @Override
    public void pause() {
        damaku.pause();
    }

    @Override
    public void show() {
        damaku.show();
    }

    @Override
    public void resume() {
        damaku.resume();
    }


    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 视频准备播放
     * 2
     */
    class VideoPreparedListener implements IMediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(IMediaPlayer mp) {
            //当视频加载好之后把他变成成员变量。注意，第二次元
            mediaPlayer = mp;
            anim.stop();
            loadvideo.setVisibility(View.GONE);
            mediaPlayer.start();
            //开始弹幕
            damaku.startDamaku(uri[1]);
            DanmakuView danmakuView = damaku.getmDanmakuView();
            mFrameLayout.addView(danmakuView, 1);
            //设置进度条最大值
            mControllerView.setmVideoDuration(mIjkVideoView.getDuration());
            mControllerView.setPlayState(ControllerView.STATE_PLAY);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mControllerView.dispatchTouchEvent(ev);
    }

    class MyBufferListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                //开始卡
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mControllerView.setPlayState(ControllerView.STATE_BUFFER);
                    break;
                //结束卡
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mControllerView.setPlayState(ControllerView.STATE_PLAY);
//                    mDialogTime.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }
            return false;
        }
    }

    @Override
    protected void onStop() {
        switchPlayOrPause();
        damaku.pause();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        damaku.resume();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }
}
