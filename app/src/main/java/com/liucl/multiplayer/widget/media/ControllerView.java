package com.liucl.multiplayer.widget.media;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liucl.multiplayer.R;
import com.liucl.multiplayer.TimeUtils;

/**
 * 通用控制器
 * Created by 刘晨龙 on 2015/11/12.
 */
public class ControllerView extends RelativeLayout implements View.OnClickListener {

    public static final int LOOPER = 0;
    public static final int PANEL_SHOW = 1;

    public static final int STATE_PLAY = 44;
    public static final int STATE_PAUSE = 375;
    public static final int STATE_PARPER = 562;
    public static final int STATE_BUFFER = 362;

    private static final int CONTROLLER_STATE_OPEN = 341;
    private static final int CONTROLLER_STATE_CLOSE = 575;

    private int playState = STATE_PARPER;
    private int controllerVisable = CONTROLLER_STATE_CLOSE;

    private TimeUtils timeUtils;
    private GestureDetector gestureDetector;

//    protected VideoView mVideoView;
//    private MediaPlayer mediaPlayer;

    /**
     * 视频标题
     */
    private TextView mTitle;
    /**
     * 系统时间
     */
    private TextView mSystemTime;
    /**
     * 电量图标
     */
    private ImageView mBattery;
    /**
     * 分享
     */
    private TextView mShare;
    /**
     * 投硬币
     */
    private TextView mCoin;
    /**
     * 清晰度选择
     */
    private TextView mDefinition;
    /**
     * 设置
     */
    private ImageView mSetting;
    private RelativeLayout mControllerBar;
    /**
     * 播放暂停
     */
    private ImageView mPlayorpause;
    /**
     * 播放时间
     */
    private TextView mTextprogress;
    /**
     * 弹幕开关
     */
    private TextView mDamakuSwitch;
    /**
     * 发送弹幕
     */
    private TextView mSendDanmaku;
    /**
     * 锁定屏幕
     */
    private TextView mLockScreen;
    /**
     * 进度条
     */
    private SeekBar mSeekbar;
    /**
     * 控制面板
     */
    private RelativeLayout mContentPanel;
    /**
     * 滑动时候出现的Dialog
     */
    private LinearLayout mDialogTime;
    /**
     * 滑动时候出现的Dialog1
     */
    private TextView mDialogTimeLine1;
    /**
     * 滑动时候出现的Dialog2
     */
    private TextView mDialogTimeLine2;

    /**
     * 逐帧动画
     */
    private AnimationDrawable anim;
    /**
     * 加载视频的界面
     */
    private View loadvideo;
    /**
     * 消息器
     */
    private TimeHandler mTimeHandler;
    private LinearLayout mDanmakuContent;

    public void setmVideoDuration(int mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }

    /**
     * 总时长
     */
    private int mVideoDuration;

    private Context mContext;


    public ControllerView(Context context) {
        super(context);
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        timeUtils = new TimeUtils();
        mContext = context;
    }

    public ControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        assignViews();
        addListener();
        applyFunction();
        applyView();
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
        mDamakuSwitch.setOnClickListener(this);
        mSendDanmaku.setOnClickListener(this);

        SeekBarListener seekBarListener = new SeekBarListener();
        mSeekbar.setOnSeekBarChangeListener(seekBarListener);
    }

    /**
     * 设置状态
     *
     * @param controllerVisable
     */
    public void setControllerVisable(int controllerVisable) {
        this.controllerVisable = controllerVisable;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }

    /**
     * 应用显示
     */
    private void applyView() {
        //应用控制栏显示或者隐藏状态
        applyControllerState();
        //初始化手势识别器
        gestureDetector = new GestureDetector(mContext, new MyGestureListener());
        //设置系统时间
        mSystemTime.setText(getSystemTime());
    }

    /**
     * 其实这个用于关闭控制面板
     */
    private void showPanel() {
        if (playState != STATE_PARPER) {
            mSeekbar.setMax(mVideoDuration);
            mTimeHandler.removeMessages(PANEL_SHOW);
            mTimeHandler.sendEmptyMessageDelayed(PANEL_SHOW, 5000);
            updataProgressInfo();
        }
    }

    /**
     * 当控制面板打开的时候更新其信息
     */
    private void updataProgressInfo() {
        //呃，首先是文字 - -
/*      */
        int currentPosition = 0;
        if (mImIMediaCenter != null) {
            currentPosition = mImIMediaCenter.updataProgressInfo();
        }
        //咳咳，然后是进度条
        StringBuilder builder = new StringBuilder(12);
        mTextprogress.setText(builder.toString());
        builder.append(timeUtils.stringForTime(currentPosition));
        builder.append("/");
        builder.append(timeUtils.stringForTime(mVideoDuration));

        mSeekbar.setProgress(currentPosition);
        mTimeHandler.sendEmptyMessageDelayed(LOOPER, 1000);
    }

    /**
     * 应用控制器的显示或者隐藏状态
     */
    private void applyControllerState() {
        if (controllerVisable == CONTROLLER_STATE_OPEN) {
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

    /**
     * 获取系统时间
     *
     * @return
     */
    public String getSystemTime() {
        return TimeUtils.getSystemTime();
    }

    private boolean isShowDanmaku = true;

    /**
     * 各种点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        showPanel();
        int i = v.getId();
        if (i == R.id.playorpause) {
            switchPlayOrPause();

        } else if (i == R.id.title) {
            if (mImIMediaCenter != null) {
                mImIMediaCenter.finish();
            }

        } else if (i == R.id.damaku_switch) {
            if (mDamakuCenter != null) {
                if (isShowDanmaku) {
                    mDamakuCenter.hide();
                    isShowDanmaku = false;
                } else {
                    mDamakuCenter.show();
                    isShowDanmaku = true;
                }
            }

        } else if (i == R.id.send_danmaku) {
            mDamakuCenter.pause();
            mImIMediaCenter.switchPlayOrPause();
            sendDanamku();

        } else {
        }
    }

    /**
     * （烂）
     * 反射弹幕
     */
    private void sendDanamku() {
        if (mDanmakuContent == null) {
            mDanmakuContent = (LinearLayout) View.inflate(mContext, R.layout.send_danmaku, null);
            addView(mDanmakuContent);
        }
        mDanmakuContent.setVisibility(VISIBLE);
        ImageView cancel = (ImageView) mDanmakuContent.findViewById(R.id.cancel);
        final EditText danmakuContent = (EditText) mDanmakuContent.findViewById(R.id.damaku_content);
        ImageView send = (ImageView) mDanmakuContent.findViewById(R.id.send);
        danmakuContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                danmakuContent.setFocusable(true);
            }
        });
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String textcontent = danmakuContent.getText().toString();
                if (mDamakuCenter != null) {
                    mDamakuCenter.sendDamaku(textcontent);
                    mDanmakuContent.setVisibility(GONE);
                    switchPlayOrPause();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmakuContent.setVisibility(GONE);
                switchPlayOrPause();
            }
        });

//        mDanmakuContent
    }


    /**
     * 切换播放和暂停
     */
    private void switchPlayOrPause() {
        boolean isPlay = false;
        if (mImIMediaCenter != null) {
            isPlay = mImIMediaCenter.switchPlayOrPause();
        }
        if (!isPlay) {
            mPlayorpause.setBackgroundResource(R.drawable.bili_player_play_can_play);
            playState = STATE_PAUSE;
        } else {
            mPlayorpause.setBackgroundResource(R.drawable.bili_player_play_can_pause);
            playState = STATE_PLAY;
        }
        showPanel();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimeHandler.removeCallbacksAndMessages(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //更新进度
            if (msg.what == LOOPER && controllerVisable == CONTROLLER_STATE_OPEN) {
                //这时候播放器控制栏显示状态
                //更新信息
                updataProgressInfo();
            }
            //取消显示控制面板
            if (msg.what == PANEL_SHOW && playState == STATE_PLAY) {
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

    private int mCurrrntPosition;

    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (playState != STATE_PARPER && fromUser) {
                int currentPosition = 0;
                if (mImIMediaCenter != null) {
                    currentPosition = mImIMediaCenter.seekToPosition(progress);
                }

                //更新Dialog信息
                if (!mDialogTime.isShown()) {
                    mDialogTime.setVisibility(View.VISIBLE);
                }
                StringBuilder builder = new StringBuilder();
                builder.append(timeUtils.stringForTime(currentPosition));
                builder.append("/");
                builder.append(timeUtils.stringForTime(mVideoDuration));
                mDialogTimeLine1.setText(builder.toString());
                mDialogTimeLine2.setText((progress - mCurrrntPosition) / 1000 + "秒");
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


    private IMediaCenter mImIMediaCenter;

    public void setmImIMediaCenter(IMediaCenter mImIMediaCenter) {
        this.mImIMediaCenter = mImIMediaCenter;
    }

    private DamakuCenter mDamakuCenter;

    public void setmDamakuCenter(DamakuCenter mDamakuCenter) {
        this.mDamakuCenter = mDamakuCenter;
    }

    /**
     * 操作接口
     */
    public interface IMediaCenter {

        /**
         * 结束播放
         */
        void finish();

        /**
         * 更新进度信息
         */
        int updataProgressInfo();

        /**
         * 切换播放和暂停
         *
         * @return
         */
        boolean switchPlayOrPause();

        /**
         * 跳转到指定位置
         *
         * @param progress
         */
        int seekToPosition(int progress);
    }

    /**
     * 弹幕操作接口
     */
    public interface DamakuCenter {
        /**
         * 显示弹幕
         */
        void start();

        /**
         * 隐藏弹幕
         */
        void hide();

        /**
         * 发射弹幕
         */
        void sendDamaku(String text);

        /**
         * 继续弹幕
         */
        void pause();

        /**
         * 显示弹幕
         */
        void show();

        /**
         * 继续
         */
        void resume();
    }
}
