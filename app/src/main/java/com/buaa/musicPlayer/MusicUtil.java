package com.buaa.musicPlayer;

/**
 * Created by sgk on 2016/3/19.
 */
public class MusicUtil {
    //MusicPlayer接收器所能响应的Action
    public static final String MusicPlayer_ACTION="com.buaa.music.MAIN_ACTION";
    //MusicService接收器所能响应的Action
    public static final String MUSICSERVICE_ACTION="com.buaa.music.MUSICSERVICE_ACTION";
    //初始化flag
    public static final int STATE_NON=0;
    //播放的flag
    public static final int STATE_PLAY=1;
    //暂停的flag
    public static final int STATE_PAUSE=2;
    //播放上一首的flag
    public static final int STATE_LAST=4;
    //播放下一首的flag
    public static final int STATE_NEXT=5;
    //菜单关于选项的itemId
    public static final int MENU_ABOUT=6;
    //菜单退出选的项的itemId
    public static final int MENU_EXIT=7;
    public static final int MUSIC_ITEM=8;

    public MusicUtil() {
        // TODO Auto-generated constructor stub
    }
}
