package com.buaa.utils;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map;
import com.buaa.musicPlayer.R;


public class Global {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    //建立一个Map集合，里面封装了所有扩展名对应的图标名称，以便进行文件图标的显示
    public static Map<String, Integer> allIconImgs = new HashMap<String, Integer>();

    public static void init(Activity a) {
        SCREEN_WIDTH = a.getWindowManager().getDefaultDisplay().getWidth();
        SCREEN_HEIGHT = a.getWindowManager().getDefaultDisplay().getHeight();

        //初始化所有扩展名和图片的对应关系
        allIconImgs.put("txt", R.drawable.txt_file);
        allIconImgs.put("mp3", R.drawable.mp3_file);
        allIconImgs.put("mp4", R.drawable.mp4_file);
        allIconImgs.put("bmp", R.drawable.image_file);
        allIconImgs.put("gif", R.drawable.image_file);
        allIconImgs.put("png", R.drawable.image_file);
        allIconImgs.put("jpg", R.drawable.image_file);
        allIconImgs.put("dir_open", R.drawable.open_dir);
        allIconImgs.put("dir_close", R.drawable.close_dir);

    }
}
