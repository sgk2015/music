package com.buaa.musicPlayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.buaa.utils.NetWorkUtils;
import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {
    private Button local;
    private Button net;
    private Button select;
    private Button upload;
    private TextView songName;
    private String musicPath;
    private String musicName;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        local=(Button)findViewById(R.id.local);
        net=(Button)findViewById(R.id.net);
        select=(Button)findViewById(R.id.select);
        upload=(Button)findViewById(R.id.upload);
        songName=(TextView)findViewById(R.id.songName);

        local.setOnClickListener(search);
        net.setOnClickListener(search);
        select.setOnClickListener(submit);
        upload.setOnClickListener(submit);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
               if (msg.what==0){
                   Toast.makeText(ScanActivity.this,"上传完成",Toast.LENGTH_SHORT).show();
                   songName.setText("");
               }else if (msg.what==1){
                   Toast.makeText(ScanActivity.this,"上传失败，请重新上传",Toast.LENGTH_SHORT).show();
               }
            }
        };
    }

    View.OnClickListener search=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.local:
                    break;
                case R.id.net:
                    Intent intent1=new Intent("netSongs");
                    startActivity(intent1);
                    break;
            }
        }
    };

    View.OnClickListener submit=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         switch (v.getId()){
             case R.id.select:
                 Intent intent=new Intent("select");
                 startActivityForResult(intent,1);
                 break;
             case R.id.upload:
                 final ProgressDialog dialog=new ProgressDialog(ScanActivity.this);
                 dialog.setTitle("提示");
                 dialog.setMessage("正在上传，请稍候...");
                 dialog.show();

                 new Thread(){
                     @Override
                     public void run() {
                         try {
                             NetWorkUtils.postFileByURL(NetWorkUtils.INSERT_SONGS,musicPath);
                             dialog.dismiss();
                             handler.sendEmptyMessage(0);
                         } catch (Exception e) {
                            handler.sendEmptyMessage(1);
                            Log.i("sgk", e.getMessage().toString());
                         }
                     }
                 }.start();

                 break;
         }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==1){
            musicPath=data.getStringExtra("musicPath");
            int i=musicPath.lastIndexOf("/");
            musicName=musicPath.substring(i+1);
            songName.setText(musicName);
        }
    }
}
