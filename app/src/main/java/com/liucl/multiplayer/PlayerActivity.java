package com.liucl.multiplayer;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * 公共播放器的基类(test)
 * Created by 刘晨龙 on 2015/11/11.
 */
public abstract class PlayerActivity extends Activity implements View.OnClickListener {


    public static final int LOOPER = 0;
    public static final int PANEL_SHOW = 1;

    private static final int STATE_PLAY = 44;
    private static final int STATE_PAUSE = 375;
    private static final int STATE_PARPER = 562;
    private static final int STATE_BUFFER = 362;

    private static final int CONTROLLER_STATE_OPEN = 341;
    private static final int CONTROLLER_STATE_CLOSE = 575;

    private int playState = STATE_PARPER;
    private int controllerVisable = CONTROLLER_STATE_CLOSE;

    private TimeUtils timeUtils;
    private GestureDetector gestureDetector;

    protected VideoView mVideoView;
    private MediaPlayer mediaPlayer;

    /** 视频标题 */
    private TextView mTitle;
    /** 系统时间 */
    private TextView mSystemTime;
    /** 电量图标 */
    private ImageView mBattery;
    /** 分享 */
    private TextView mShare;
    /** 投硬币 */
    private TextView mCoin;
    /** 清晰度选择 */
    private TextView mDefinition;
    /** 设置 */
    private ImageView mSetting;
    private RelativeLayout mControllerBar;
    /** 播放暂停 */
    private ImageView mPlayorpause;
    /** 播放时间 */
    private TextView mTextprogress;
    /** 弹幕开关 */
    private TextView mDamakuSwitch;
    /** 发送弹幕 */
    private TextView mSendDanmaku;
    /** 锁定屏幕 */
    private TextView mLockScreen;
    /** 进度条 */
    private SeekBar mSeekbar;
    /** 控制面板 */
    private RelativeLayout mContentPanel;
    /** 滑动时候出现的Dialog */
    private LinearLayout mDialogTime;
    /** 滑动时候出现的Dialog1 */
    private TextView mDialogTimeLine1;
    /** 滑动时候出现的Dialog2 */
    private TextView mDialogTimeLine2;
    /** 弹幕 */
    private DanmakuView mDanmaku;
    /** 逐帧动画 */
    private AnimationDrawable anim;
    /** 加载视频的界面 */
    private View loadvideo;
    /** 消息器 */
    private TimeHandler mTimeHandler;

    private int mCurrrntPosition;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser mParser;

    protected void assignViews() {
        mTitle = (TextView) findViewById(R.id.title);
        mSystemTime = (TextView) findViewById(R.id.system_time);
        mBattery = (ImageView) findViewById(R.id.battery);
        mShare = (TextView) findViewById(R.id.share);
        mCoin = (TextView) findViewById(R.id.coin);
        mDefinition = (TextView) findViewById(R.id.definition);
        mSetting = (ImageView) findViewById(R.id.setting);
        mControllerBar = (RelativeLayout) findViewById(R.id.controller_bar);
        mPlayorpause = (ImageView) findViewById(R.id.playorpause);
        mTextprogress = (TextView) findViewById(R.id.textprogress);
        mDamakuSwitch = (TextView) findViewById(R.id.damaku_switch);
        mSendDanmaku = (TextView) findViewById(R.id.send_danmaku);
        mLockScreen = (TextView) findViewById(R.id.lock_screen);
        mSeekbar = (SeekBar) findViewById(R.id.seekbar);
        mContentPanel = (RelativeLayout) findViewById(R.id.contentPanel);
        mDialogTime = (LinearLayout) findViewById(R.id.dialog_time);
        mDialogTimeLine1 = (TextView) findViewById(R.id.dialog_time_line1);
        mDialogTimeLine2 = (TextView) findViewById(R.id.dialog_time_line2);
        mDanmaku = (DanmakuView) findViewById(R.id.damaku);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        configSystem();
        //fvb
        assignViews();
        //初始化播放器
        initPlay();
        initDamaku();
        //填充数据
        applyView();
        applyFunction();
        addListener();
    }

    /**
     * 初始化系统
     */
    protected void configSystem(){

    }

    /**
     * 加载布局
     * @return
     */
    protected abstract int getContentView();


    /**
     * 初始化弹幕
     */
    private void initDamaku() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        danmakuContext = DanmakuContext.create();
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer()) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmaku != null) {
            //设置弹幕地址
            if(getUri()[1] == null){
                return;
            }
            String url = getUri()[1] .toString();
            mParser = createParser(url);
            //弹幕开始监听
            mDanmaku.setCallback(new MyLoadDanmakuListener());
            mDanmaku.prepare(mParser, danmakuContext);
            mDanmaku.showFPS(true);
            mDanmaku.enableDanmakuDrawingCache(true);
//            ((View) mDanmaku).setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    mMediaController.setVisibility(View.VISIBLE);
//                }
//            });
        }
    }

    /**
     * 监听
     */
    protected void addListener() {
        //播放暂停的点击事件
        mPlayorpause.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mCoin.setOnClickListener(this);
        mDefinition.setOnClickListener(this);
        mSetting.setOnClickListener(this);

        SeekBarListener seekBarListener = new SeekBarListener();
        mSeekbar.setOnSeekBarChangeListener(seekBarListener);
        mVideoView.setOnInfoListener(new MyBufferListener());
    }

    /**
     * 应用显示
     */
    private void applyView() {
        //应用控制栏显示或者隐藏状态
        applyControllerState();
        //初始化手势识别器
        gestureDetector = new GestureDetector(this, new MyGestureListener());
        //设置系统时间
        mSystemTime.setText(getSystemTime());
    }

    /**
     * 应用控制器的显示或者隐藏状态
     */
    private void applyControllerState() {
        if(controllerVisable == CONTROLLER_STATE_OPEN){
            mContentPanel.setVisibility(View.VISIBLE);
        } else {
            mContentPanel.setVisibility(View.GONE);
        }
    }

    /**
     * 应用功能
     */
    private void applyFunction() {
        //加载消息器
        mTimeHandler = new TimeHandler();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 方法逻辑
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 弹幕解析器
     */
    private BaseDanmakuParser createParser(String uri) {

        if (uri == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
//            Log.i("SystemPlayerActivity", "createParser: " + uri);
            if(uri == null){
                return null;
            }
            loader.load(uri);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    /**
     * 切换播放和暂停
     */
    private void switchPlayOrPause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mPlayorpause.setBackgroundResource(R.drawable.bili_player_play_can_play);
            playState = STATE_PAUSE;
        } else {
            mediaPlayer.start();
            mPlayorpause.setBackgroundResource(R.drawable.bili_player_play_can_pause);
            playState = STATE_PLAY;
        }
        showPanel();
    }

    /**
     * 加载数据准备播放
     */
    private void initPlay() {
        //初始化字符串工具 ^_^ 不知道放在那里，就放到这里吧。。。
        timeUtils = new TimeUtils();
        Uri uri = getUri()[0];
        if (uri != null) {
            mVideoView.setVideoURI(uri);
            playState = STATE_PARPER;
            mVideoView.setOnPreparedListener(new VideoPreparedListener());
            //加载等待动画
            loadvideo = findViewById(R.id.loadvideo);
            ImageView imageView = (ImageView) findViewById(R.id.anim);
            anim = (AnimationDrawable) imageView.getBackground();
            anim.start();
        }
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

    /**
     * 各种点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playorpause:
                switchPlayOrPause();
                break;
            case R.id.title:
                mediaPlayer.stop();
                finish();
                break;
        }
    }

    /**
     * 获取系统时间
     * @return
     */
    public String getSystemTime() {
        return TimeUtils.getSystemTime();
    }

    /**
     * 其实这个用于关闭控制面板
     */
    private void showPanel() {
        if(playState != STATE_PARPER){
            mTimeHandler.removeMessages(PANEL_SHOW);
            mTimeHandler.sendEmptyMessageDelayed(PANEL_SHOW, 5000);
            updataProgressInfo();
        }
    }

    /**
     * 当控制面板打开的时候更新其信息
     */
    private void updataProgressInfo(){
        //呃，首先是文字 - -
        StringBuilder builder = new StringBuilder(12);
        builder.append(timeUtils.stringForTime(mediaPlayer.getCurrentPosition()));
        builder.append("/");
        builder.append(timeUtils.stringForTime(mVideoView.getDuration()));
        mTextprogress.setText(builder.toString());
        //咳咳，然后是进度条
        mSeekbar.setProgress(mediaPlayer.getCurrentPosition());
        mTimeHandler.sendEmptyMessageDelayed(LOOPER,1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        mTimeHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 视频准备播放
     * 2
     */
    class VideoPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //当视频加载好之后把他变成成员变量。注意，第二次元
            mediaPlayer = mp;
            anim.stop();
            loadvideo.setVisibility(View.GONE);
            mediaPlayer.start();
            //设置进度条最大值
            mSeekbar.setMax(mVideoView.getDuration());
            playState = STATE_PLAY;
        }
    }

    class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //更新进度
            if(msg.what == LOOPER && controllerVisable == CONTROLLER_STATE_OPEN){
                //这时候播放器控制栏显示状态
                //更新信息
                updataProgressInfo();
            }
            //取消显示控制面板
            if(msg.what == PANEL_SHOW && playState == STATE_PLAY){
                controllerVisable = CONTROLLER_STATE_CLOSE;
                applyControllerState();
            }
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (playState != STATE_PARPER) {
                if (controllerVisable == CONTROLLER_STATE_OPEN) {
                    controllerVisable = CONTROLLER_STATE_CLOSE;
                } else {
                    controllerVisable = CONTROLLER_STATE_OPEN;
                    //控制器显示,显示他要显示的内容
                    showPanel();
                    //开启轮训器更新进度
                    mTimeHandler.sendEmptyMessage(LOOPER);
                }
                applyControllerState();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            switchPlayOrPause();
            return true;
        }
    }

    class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(playState != STATE_PARPER && fromUser){
                mediaPlayer.seekTo(progress);
                //更新Dialog信息
                if (!mDialogTime.isShown()) {
                    mDialogTime.setVisibility(View.VISIBLE);
                }
                StringBuilder builder = new StringBuilder();
                builder.append(timeUtils.stringForTime(mediaPlayer.getCurrentPosition()));
                builder.append("/");
                builder.append(timeUtils.stringForTime(mVideoView.getDuration()));
                mDialogTimeLine1.setText(builder.toString());
                mDialogTimeLine2.setText((progress - mCurrrntPosition)/1000+"秒");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mCurrrntPosition = seekBar.getProgress();
            showPanel();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mCurrrntPosition = 0;
            mTimeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDialogTime.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }

    class MyBufferListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                //开始卡
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    playState = STATE_BUFFER;
                    break;
                //结束卡
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    playState = STATE_PLAY;
                    mDialogTime.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }
            return false;
        }
    }

    class MyLoadDanmakuListener implements DrawHandler.Callback {

        @Override
        public void prepared() {
            mDanmaku.start();
        }

        @Override
        public void updateTimer(DanmakuTimer timer) {

        }

        @Override
        public void drawingFinished() {

        }
    }


}
