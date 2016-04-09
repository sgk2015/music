package com.buaa.musicPlayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.buaa.bean.Music;
import com.buaa.utils.MusicUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private TextView lrc;
    private TextView musicName;
    private TextView musicAuthor;
    static TextView playTime;
    static SeekBar seekBar;
    static TextView totalTime;
    private Button open;
    private Button play;
    private Button preMusic;
    static Button playOrPause;
    private Button nextMusic;

    final int UPDATE_ITEM=0;
    final int SCAN_ITEM=1;
    final int EXIT_ITEM=2;
    final int CANCLE_ITEM=3;

    private MusicService.Mybinder mybinder;
    List<Music> list = new ArrayList<Music>();
    private int timeall;
    boolean isPlaying = false;
    private Handler handler;
    private String[] music;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //自动检查版本更新
        Intent intent2=new Intent("update");
        startActivity(intent2);

        open=(Button)findViewById(R.id.open);//打开音乐播放列表
        play=(Button)findViewById(R.id.play);
        lrc=(TextView)findViewById(R.id.lrc);//显示歌词区域
        musicName = (TextView) findViewById(R.id.musicName);//歌名
        musicAuthor = (TextView) findViewById(R.id.musicAuthor);//歌手
        playTime = (TextView) findViewById(R.id.playTime);//当前时间
        seekBar = (SeekBar) findViewById(R.id.sb);//进度条
        seekBar.setOnSeekBarChangeListener(sChangeListener);
        totalTime = (TextView) findViewById(R.id.totalTime);//歌曲总时间
        preMusic = (Button) findViewById(R.id.preMusic);//上一首
        playOrPause = (Button) findViewById(R.id.pOp);//播放、暂停
        nextMusic = (Button) findViewById(R.id.nextMusic);//下一首

        open.setOnClickListener(listener);
        preMusic.setOnClickListener(listener1);
        playOrPause.setOnClickListener(listener1);
        nextMusic.setOnClickListener(listener1);
        play.setOnClickListener(listener1);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                music=msg.getData().getStringArray("s");
                Log.i("sgk","run..............."+music);
                for (int i = 0; i < music.length; i++) {
                    int s1 = music[i].lastIndexOf("-");
                    Music m = new Music(music[i].substring(s1+1,music[i].length()),music[i].substring(0,s1));
                    list.add(m);
                }
            }
        };


        Intent intent=new Intent("musicService");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mybinder = (MusicService.Mybinder) service;
                new Thread() {
                    @Override
                    public void run() {
                        String[] s = mybinder.getS();
                        Log.i("sgk","run......................."+s.hashCode());
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("s", s);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, BIND_AUTO_CREATE);

        //注册接收器
        MusicPlayerReceiver receiver = new MusicPlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicUtil.MusicPlayer_ACTION);
        registerReceiver(receiver, filter);
    }
    //歌曲扫描监听器
    View.OnClickListener listener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent=new Intent("musicList");
            startActivity(intent);
        }
    };

    //播放监听器
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent("musicService");
                switch (v.getId()) {
                    case R.id.play:
                        intent.putExtra("action", MusicUtil.STATE_PLAY);
                        intent.putExtra("position",position);
                        isPlaying = true;
                        break;

                    case R.id.nextMusic://下一首
                        // btnPlayOrPause.setBackgroundResource(R.drawable.state_pasue);
                        intent.putExtra("action", MusicUtil.STATE_NEXT);

                        isPlaying = true;
                        break;
                    case R.id.pOp://播放或暂停
                        if (!isPlaying) {
                            //btnPlayOrPause.setBackgroundResource(R.drawable.state_pasue);
                            intent.putExtra("action", MusicUtil.STATE_PLAY);
                            isPlaying = true;
                        } else {
                            // btnPlayOrPause.setBackgroundResource(R.drawable.state_play);
                            intent.putExtra("action", MusicUtil.STATE_PAUSE);
                            isPlaying = false;
                        }
                        break;
                    case R.id.preMusic://上一首
                        //btnPlayOrPause.setBackgroundResource(R.drawable.state_pasue);
                        intent.putExtra("action", MusicUtil.STATE_LAST);
                        isPlaying = true;
                        break;
                    default:
                        break;

                }
                startService(intent);
            }
        };
        /**
         * SeekBar进度改变事件
         */
        SeekBar.OnSeekBarChangeListener sChangeListener = new SeekBar.OnSeekBarChangeListener() {
            //进度条停止拖动时执行的操作
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //当拖动停止后，控制mediaPlayer播放指定位置的音乐
                MusicService.mediaPlayer.seekTo(seekBar.getProgress());
                MusicService.isChanging = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                MusicService.isChanging = true;
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress,
                                          boolean fromUser) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                playTime.setText(simpleDateFormat.format(seekBar.getProgress()));
                //Log.i("sgk","当前播放时间："+simpleDateFormat.format(seekBar.getProgress()));
               MusicService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.i("sgk", "即将播放下一首。。。。。。。。。。。。。。");
                        Intent intent = new Intent("musicService");
                        intent.putExtra("action", MusicUtil.STATE_NEXT);
                        seekBar.setProgress(0);
                        startService(intent);
                    }
                });
            }
        };


        //创建一个广播接收器用于接收后台Service发出的广播
        class MusicPlayerReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                // 获取Intent中的current消息，current代表当前正在播放的歌曲
                int current = intent.getIntExtra("current", -1);
                musicName.setText(list.get(current).getName());//更新音乐标题
                musicAuthor.setText(list.get(current).getAuthor());//更新音乐作者
                timeall = intent.getIntExtra("totalTime", 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                String timeallStr = simpleDateFormat.format(timeall);
                totalTime.setText(timeallStr);

            }
        }

    //添加菜单，用户点击菜单键时触发该方法

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.addSubMenu(Menu.NONE, UPDATE_ITEM, 0, "版本更新");
        menu.addSubMenu(Menu.NONE,SCAN_ITEM,1,"上传与扫描");
        menu.addSubMenu(Menu.NONE, EXIT_ITEM, 2, "退出IMusic");
        menu.addSubMenu(Menu.NONE, CANCLE_ITEM, 3, "取消");
        return true;
    }

    //菜单选项的监听事件

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case UPDATE_ITEM:
                Intent intent=new Intent("update");
                startActivity(intent);
                break;
            case SCAN_ITEM:
                Intent intent1=new Intent("scan");
                startActivity(intent1);
                break;
            case EXIT_ITEM:
                break;
            case CANCLE_ITEM:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

