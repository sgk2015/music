package com.buaa.musicPlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.buaa.bean.Music;
import com.buaa.utils.Global;
import com.buaa.utils.MusicUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectMusicActivity extends Activity implements OnItemClickListener {
    private ListView musicList;
    private List<Music> data;
    private MusicAdapter adapter;
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String musicPath=MusicUtil.PATH+music[position];
        Intent intent=getIntent();
        intent.putExtra("musicPath", musicPath);
        setResult(1, intent);
        finish();
    }

}
