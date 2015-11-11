package com.liucl.multiplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.VideoView;

public class SystemPlayerActivity extends PlayerActivity {

    /**
     * 获得他的布局
     * @return
     */
    @Override
    protected int getContentView() {
        return R.layout.activity_player;
    }

    protected void addListener(){
        super.addListener();
        mVideoView.setOnErrorListener(new SystemErrorListener());
    }

    @Override
    protected void assignViews() {
        super.assignViews();
        mVideoView = (VideoView) findViewById(R.id.videoview);
    }

    class SystemErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Uri[] uri = getUri();
//            url[0] = Uri.parse(getIntent().getStringExtra("videoPath"));
//            url[1] = Uri.parse(getIntent().getStringExtra("danmakuPath"));
            Intent intent = new Intent(SystemPlayerActivity.this,IjkPlayerActivity.class);
            intent.putExtra("videoPath",uri[0]);
            intent.putExtra("danmakuPath",uri[1]);
            startActivity(intent);
            return false;
        }
    }

}
