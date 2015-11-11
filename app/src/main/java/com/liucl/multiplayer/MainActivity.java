package com.liucl.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liucl.multiplayer.adapter.NetUtils;

public class MainActivity extends AppCompatActivity {

    private String net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(){
            @Override
            public void run() {
                net = NetUtils.getStringFromNet(MainActivity.this);
            }
        }.start();
    }

    /**
     * 点击播放
     * @param view
     */
    public void clickPlay(View view) {

        Intent intent = new Intent(this, IjkPlayerActivity.class);
        intent.putExtra("videoPath",Const.videoUrl);
        intent.putExtra("danmakuPath", net);
        startActivity(intent);
    }
}
