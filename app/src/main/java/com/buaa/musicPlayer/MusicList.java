package com.buaa.musicPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.buaa.bean.Music;
import com.buaa.utils.Global;
import com.buaa.utils.MusicUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MusicList extends Activity implements OnItemClickListener,OnItemLongClickListener {
    private ListView musicList;
    private List<Music> data;
    private MusicAdapter adapter;
    boolean isPlaying = false;
    private File file;
    private String[] music;

    private void init() {
        data = new ArrayList<Music>();
        file = new File(MusicUtil.PATH);
        music = file.list();//获得的SD卡文件
        for (int i = 0; i < music.length; i++) {
            int s1 = music[i].lastIndexOf("-");
            Music m = new Music(music[i].substring(0, music[i].length()), music[i].substring(0, s1));
            data.add(m);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        init();
        Global.init(this);
        musicList = (ListView) findViewById(R.id.musicList);
        adapter = new MusicAdapter(this, data);
        musicList.setAdapter(adapter);
        musicList.setOnItemClickListener(this);    //设置单击监听，接口实现
        musicList.setOnItemLongClickListener(this);//设置长按监听，接口实现
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent("musicService");
        intent.putExtra("action", MusicUtil.STATE_PLAY);
        intent.putExtra("position", position);
        startService(intent);
        isPlaying = true;
        Log.i("sgk", "onItemClick...............");
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicList.this);
        builder.setTitle("提示");
        builder.setMessage("确定要删除歌曲（" + data.get(position).getName() + ")吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final File f = new File(MusicUtil.PATH+data.get(position).getName());
                //将SD卡的文件删除
                if (f.exists()) {
                    f.delete();
                }
                //将列表中的数据删除
                data.remove(position);
                Toast.makeText(MusicList.this, data.get(position).getName() + "删除成功", Toast.LENGTH_SHORT).show();
                //通知前端界面确认修改数据
                adapter.notifyDataSetChanged();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
        return false;
    }

}
