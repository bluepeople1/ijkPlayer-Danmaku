package com.liucl.multiplayer.adapter;

/**
 * Created by 刘晨龙 on 2015/11/9.
 */
public interface PlayerController {

    /**
     * 开始播放
     */
    void play();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 跳转到
     */
    void seekTo(long position);

    /**
     * 增大音量
     */
    void volumeUp();

    /**
     * 减小音量
     */
    void volumeDown();

    /**
     * 增加亮度
     */
    void brightnessUp();

    /**
     * 减小亮度
     */
    void brightnessDown();

    /**
     * 打开弹幕
     */
    void openDanMuka();

    /**
     * 关闭弹幕
     */
    void closeDanMuka();

    /**
     * 发射弹幕
     */
    void sendDanMaKu(String str,int type);

    /**
     * 锁屏
     */
    void lockScreen();

    /**
     * progress
     */
    void showProgress();

    /**
     * 分享
     */
    void share();

    /**
     * 显示电量
     */
    void showBattery();

}
