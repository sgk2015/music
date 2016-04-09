package com.buaa.musicPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.buaa.bean.Music;
import com.buaa.utils.Global;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/19.
 */
//自定义的适配器，用于ListView
public class NetMusicAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,Object>> values;

    public NetMusicAdapter(Context context, List<Map<String, Object>> values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.content_main,null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Global.SCREEN_HEIGHT / 9));
            viewHolder.musicItem=(TextView)convertView.findViewById(R.id.musicItem);
            viewHolder.musicItem.getLayoutParams().height = Global.SCREEN_HEIGHT / 9;
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        Map<String,Object> map=values.get(position);
        viewHolder.musicItem.setText(map.get("path").toString());
        return convertView;
    }

    static class ViewHolder{
        TextView musicItem;
    }
}
