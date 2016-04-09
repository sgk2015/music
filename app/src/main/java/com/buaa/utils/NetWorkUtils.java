package com.buaa.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Administrator on 2016/4/4.
 */
public class NetWorkUtils {
    public static final String BASE_URL = "http://192.168.2.174:8080/IMusicServer/";
    public static final String LIST_SONGS = BASE_URL + "selectMusic.action";
    public static final String INSERT_SONGS = BASE_URL + "uploadMusic.action";


    public static String getDataByURL(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        InputStream in = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = "";
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static void postFileByURL(String urlStr, String musicPath) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlStr);
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("song", new FileBody(new File(musicPath)));
            post.setEntity(entity);
        try {
            client.execute(post);
        } catch (IOException e) {
            Log.i("sgk","post=============="+e.getMessage().toString());
        }
    }

    public static JSONArray getJSONArrayByURL(String urlStr) throws Exception {
        return new JSONArray(getDataByURL(urlStr));
    }

    public static JSONObject getJSONObjectByURL(String urlStr) throws Exception {
        return new JSONObject(getDataByURL(urlStr));
    }
}
