package com.buaa.musicPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.buaa.utils.MusicUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    Timer mTimer;
    TimerTask mTimerTask;
    public static MediaPlayer mediaPlayer;// 定义多媒体对象
    static boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    //记录Timer运行状态
    boolean isTimerRunning = false;

    int current = 0;//功能限制为只能从播放列表的第一首歌开始播放
    //当前播放状态
    int state = MusicUtil.STATE_NON;

    private String[] s;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建MediaPlayer
        mediaPlayer = new MediaPlayer();
    }

    private void prepareAndPlay(int index) {
        File file = new File(MusicUtil.PATH);
        s = file.list();
        Log.i("sgk","s......................."+s.toString());

        if (isTimerRunning) {//如果Timer正在运行
            mTimer.cancel();//取消定时器
            isTimerRunning = false;
        }

        mediaPlayer.reset();//初始化mediaPlayer对象
        try {
            mediaPlayer.setDataSource(MusicUtil.PATH + s[index]);
            //准备播放音乐
            mediaPlayer.prepare();
            //播放音乐
            mediaPlayer.start();


            //getDuration()方法要在prepare()方法之后，否则会出现Attempt to call getDuration without a valid mediaplayer异常
            MainActivity.seekBar.setMax(mediaPlayer.getDuration());//设置SeekBar的长度

            state = MusicUtil.STATE_PLAY;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int totalTime = MainActivity.seekBar.getMax();
        //发送广播停止前台Activity更新界面
        Intent intent = new Intent();
        intent.putExtra("current", current);
        intent.putExtra("totalTime", totalTime);
        intent.setAction(MusicUtil.MusicPlayer_ACTION);
        sendBroadcast(intent);
        //----------定时器记录播放进度---------//
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerRunning = true;
                if (isChanging == true)//当用户正在拖动进度进度条时不处理进度条的的进度
                    return;
                //更新进度条进度
                MainActivity.seekBar.setProgress(mediaPlayer.getCurrentPosition());


            }
        };
        //每隔10毫秒检测一下播放进度
        mTimer.schedule(mTimerTask, 0, 10);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra("action", -1);
        int position=intent.getIntExtra("position",-1);
        Log.i("sgk","position...................."+position);
        Log.i("sgk", "action..........." + action);
        switch (action) {
            case MusicUtil.STATE_PLAY://播放音乐
                if (state == MusicUtil.STATE_NON) {//如果原来状态是未播放
                    current=position;
                    prepareAndPlay(current);
                    state = MusicUtil.STATE_PLAY;
                } else if (state == MusicUtil.STATE_PAUSE) {
                    mediaPlayer.start();
                    state = MusicUtil.STATE_PLAY;
                }else if (state==MusicUtil.STATE_PLAY){
                    current=position;
                    prepareAndPlay(current);
                }

                break;
            case MusicUtil.STATE_PAUSE://暂停播放
                if (state == MusicUtil.STATE_PLAY) {
                    mediaPlayer.pause();
                    state = MusicUtil.STATE_PAUSE;
                }
                break;
            case MusicUtil.STATE_NEXT://播放下一首
                current += 1;
                if (current < s.length) {
                    Log.i("sgk", "准备播放下一首" + current + ".........next..................");
                    prepareAndPlay(current);
                } else {
                    //否则否方第一首歌
                    current = 0;
                    prepareAndPlay(current);
                }
                break;
            case MusicUtil.STATE_LAST://播放上一首
                current -= 1;
                if (current >= 0) {
                    Log.i("sgk", "准备播放上一首" + current + "last...................");
                    prepareAndPlay(current);
                } else {
                    //否则播放最后一首歌
                    current = s.length - 1;
                    prepareAndPlay(current);
                }
                break;
            default:
                break;


        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Mybinder();
    }
    public class Mybinder extends Binder {
        public String[] getS(){
            File file=new File(MusicUtil.PATH);
            s=file.list();
            return s;
        }
    }

}