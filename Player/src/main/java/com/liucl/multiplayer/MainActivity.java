package com.liucl.multiplayer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class MainActivity extends AppCompatActivity {

    private String net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new Thread(){
//            @Override
//            public void run() {
//                net = NetUtils.getStringFromNet(MainActivity.this);
//            }
//        }.start();
    }

    /**
     * 点击播放
     * @param view
     */
    public void clickPlay(View view) throws IOException {
        /*
         *  videoPath “ 播放地址 ”
         *  danmakuPath “ 弹幕地址（file对象的地址） ”
         */

        AssetManager assetManager = getAssets();
        File file = getFilesDir();
        File fileDir = new File(file, "danmakutest.xml");
        try {
            InputStream is = assetManager.open("danmakutest.xml");
            FileOutputStream fos = new FileOutputStream(fileDir);
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("MainActivity", "clickPlay: " + fileDir.length());
        Intent intent = new Intent(this, IjkPlayerActivity.class);
        intent.putExtra("videoPath", Const.videoUrl);
        intent.putExtra("danmakuPath", fileDir.getAbsolutePath());
        startActivity(intent);

        InputStream is = assetManager.open("danmakutest.xml");

        File f = new File(getFilesDir(), "text.xml");
//        FileOutputStream fos = new FileOutputStream(f);
//        BufferedOutputStream bos = new BufferedOutputStream(fos);
//        int len = -1;
//        byte[] buffer = new byte[1024];
//        while ((len = is.read(buffer)) != -1) {
//            bos.write(buffer,0,len);
//            bos.flush();
//        }
//
//        fos.close();
//        is.close();

//        FileReader reader = new FileReader(file);
//        BufferedReader br = new BufferedReader(reader);
//
//
//        FileWriter fw = new FileWriter(f);
//        BufferedWriter bw = new BufferedWriter(fw);
//        char[] buffer = new char[1024];
////        StringBuilder sb = new StringBuilder();
//        String str;
//        while ((str = br.readLine()) != null) {
//            bw.write(str);
//            bw.flush();
//        }
//
//        byte[] b = new byte[1024];
//        b[1] = 2;
//        ByteArrayInputStream bis = new ByteArrayInputStream(b);
//        ByteArrayOutputStream baps = new ByteArrayOutputStream();
//
//        byte[] bb = new byte[111];
//        int len = -1;
//        while ((len = bis.read(bb)) != -1) {
//            baps.write(bb,0,len);
//        }
//
//        baps.toString();

        RandomAccessFile rd = new RandomAccessFile(file, "rwd");
        RandomAccessFile wd = new RandomAccessFile(f, "rwd");




    }
}
