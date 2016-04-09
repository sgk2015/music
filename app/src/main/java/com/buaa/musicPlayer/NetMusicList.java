package com.buaa.musicPlayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import com.buaa.bean.Music;
import com.buaa.utils.Global;
import com.buaa.utils.NetWorkUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NetMusicList extends Activity {
    private ListView netMusicList;
    private NetMusicAdapter adapter;
    private Handler handler;
    private int recordCount;
    private int pageCount;
    private int pagenum=1;
    private int pagesize=9;
    private int first;
    private int visCount;
    private int total;
    private List<Map<String,Object>> values=new ArrayList<Map<String,Object>>();
    private TextView footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.init(this);
        setContentView(R.layout.activity_net_music_list);
        netMusicList=(ListView)findViewById(R.id.netMusicList);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==0){
                    adapter=new NetMusicAdapter(NetMusicList.this,values);
                    netMusicList.setAdapter(adapter);
                }else if (msg.what==1){
                    adapter.notifyDataSetChanged();
                }else if (msg.what==2){
                    netMusicList.removeFooterView(footer);
                }
            }
        };
        footer=new TextView(this);
        footer.setText("数据正在加载中，请稍候...");
        netMusicList.addFooterView(footer);

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("提示信息");
        dialog.setMessage("数据正在加载中，请稍候...");
        dialog.show();

        //开子线程从服务器中获取数据
        new Thread(){
            @Override
            public void run() {
                try {
                    loadData();
                    handler.sendEmptyMessage(0);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        netMusicList.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, final int scrollState) {
                new Thread(){
                    @Override
                    public void run() {
                        if (total!=0&&scrollState==SCROLL_STATE_IDLE&&first+visCount==total){
                            if (pagenum<pageCount){
                                try {
                                    pagenum++;
                                    loadData();
                                    handler.sendEmptyMessage(1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                if (netMusicList.getFooterViewsCount()>0){
                                   handler.sendEmptyMessage(2);
                                }
                            }
                        }
                    }
                }.start();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                first=firstVisibleItem;
                visCount=visibleItemCount;
                total=totalItemCount;
            }
        });
    }

    private void loadData() throws Exception{
        JSONObject root= NetWorkUtils.getJSONObjectByURL(NetWorkUtils.LIST_SONGS + "?pagenum=" + pagenum + "&pagesize=" + pagesize);
        recordCount=root.getInt("count");
        pageCount=(recordCount-1)/pagesize+1;
        JSONArray array=root.getJSONArray("songs");
        for (int i=0;i<array.length();i++){
            JSONObject obj=array.getJSONObject(i);
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("id", obj.getString("id"));
            map.put("path", obj.getString("path"));
            values.add(map);
            /*int s1 = path.lastIndexOf("-");
            Music m = new Music(path.substring(0, path.length()), path.substring(0, s1));*/

        }


    }


}
