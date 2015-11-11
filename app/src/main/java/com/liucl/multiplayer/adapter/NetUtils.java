package com.liucl.multiplayer.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.liucl.multiplayer.Const;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by 刘晨龙 on 2015/11/11.
 */
public class NetUtils {

    public static String getStringFromNet(Context context) {
        File file = null;
//        try {
//            File filesDir = context.getFilesDir();
//            file = new File(filesDir, "dmk.xml");
//            URL url = new URL(Const.danmakuUrl);
//            InputStream inputStream = url.openStream();
//            FileOutputStream dmk = new FileOutputStream(file);
//            int len = 0;
//            byte[] buffer = new byte[1024];
//            while ((len = inputStream.read(buffer)) != -1) {
//                dmk.write(buffer, 0, len);
//                Log.i("NetUtils", "getStringFromNet: " + new String(buffer));
//                dmk.flush();
//            }
//            inputStream.close();
//            dmk.close();
//        } catch (java.io.IOException e) {
//            e.printStackTrace();
//        }
        try {
            File filesDir = context.getFilesDir();
            file = new File(filesDir, "dmk.xml");
            AssetManager assets = context.getAssets();
            InputStream is = assets.open("5036614.xml");
            FileOutputStream dmk = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                dmk.write(buffer, 0, len);
                Log.i("NetUtils", "getStringFromNet: " + new String(buffer));
                dmk.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

}
