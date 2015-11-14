package com.liucl.multiplayer;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;

import com.liucl.multiplayer.widget.media.ControllerView;

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
 * 燃烧吧，烈焰弹幕使
 * Created by 刘晨龙 on 2015/11/12.
 */
public class Damaku implements ControllerView.DamakuCenter {

    /** 弹幕 */
    private DanmakuView mDanmaku;

    private DanmakuContext danmakuContext;
    private BaseDanmakuParser mParser;

    public Damaku(Context context){
        mDanmaku = (DanmakuView) View.inflate(context,R.layout.content_damaku,null);
    }

    public DanmakuView getmDanmakuView(){
        return mDanmaku;
    }

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
     * 初始化弹幕
     */
    public void startDamaku(Uri uri) {
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
            if(uri == null){
                return;
            }
            String url = uri.toString();
            mParser = createParser(url);
            //弹幕开始监听
            mDanmaku.setCallback(new MyLoadDanmakuListener());
            mDanmaku.prepare(mParser, danmakuContext);
            mDanmaku.showFPS(true);
            mDanmaku.enableDanmakuDrawingCache(true);
        }
    }

    public void hide(){
        mDanmaku.hide();
    }

    @Override
    public void sendDamaku(String text) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmaku == null) {
            return;
        }
        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = true;
        danmaku.time = mDanmaku.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmaku.addDanmaku(danmaku);
    }

    public void start(){
        mDanmaku.start();
    }

    public void pause(){
        mDanmaku.pause();
    }

    public void resume(){
        mDanmaku.resume();
    }

    public void show(){
        mDanmaku.show();
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
