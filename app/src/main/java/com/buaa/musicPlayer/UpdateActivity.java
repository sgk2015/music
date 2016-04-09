package com.buaa.musicPlayer;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.utils.NetWorkUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateActivity extends AppCompatActivity {
    private TextView versionInfo;
    private int versionCode;
    private String versionName;
    private Handler handler;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        versionInfo=(TextView)findViewById(R.id.versionInfo);
        try {
            //获取某个包下的信息
            PackageInfo info = getPackageManager().getPackageInfo("com.buaa.musicPlayer",0);
            versionCode=info.versionCode;
            versionName=info.versionName;
            versionInfo.setText("当前版本号是：" + versionCode +"\n"+ "当前版本名称是：" + versionName);
            Log.i("sgk","当前版本号是：" + versionCode +"\n"+ "当前版本名称是：" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //主线程进行版本更新的具体操作（注意具体的网络操作应该在子线程中进行）
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==0){
                    Builder builder=new Builder(UpdateActivity.this);
                    builder.setTitle("提示信息");
                    builder.setMessage(msg.obj.toString());

                    pd=new ProgressDialog(UpdateActivity.this);
                    pd.setTitle("正在下载，请稍候...");
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //与网络有关的操作在子线程中进行
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        //创建网络连接对象
                                        URL url = new URL(NetWorkUtils.BASE_URL+"androidVersion/app-debug.apk");
                                    //打开网络连接
                                    URLConnection conn = url.openConnection();
                                    //创建读取数据的进度条，方便在下载时使用
                                    pd.setMax(conn.getContentLength());
                                    handler.sendEmptyMessage(1);

                                    InputStream in = conn.getInputStream();
                                    File file = new File(Environment.getExternalStorageDirectory(), "IMUSIC.apk");
                                    FileOutputStream out = new FileOutputStream(file);
                                    byte[] data = new byte[1024];
                                    int length;//读数据时使用
                                    int pdLength = 0;//读进度条时使用
                                    while ((length = in.read(data)) != -1) {
                                        out.write(data, 0, length);
                                        pdLength += length;
                                        if (pdLength > 10240) {
                                            pd.incrementProgressBy(pdLength);
                                            pdLength = 0;
                                        }
                                    }
                                        out.close();
                                        in.close();
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    });
                    builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }else if (msg.what==1){
                    pd.show();
                }else if (msg.what==2){
                    Toast.makeText(UpdateActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                }else if (msg.what==3){
                    Toast.makeText(UpdateActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        };



        //开一个子线程通过访问网络检查系统是否需要更新
        new Thread(){
            @Override
            public void run() {
                super.run();
                //获取手机当前网络信息
                ConnectivityManager manager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if (networkInfo==null){
                    Message msg=new Message();
                    msg.what=3;
                    msg.obj="当前无网络，请检查网络连接!";
                    handler.sendMessage(msg);
                }else{
                    //使用使用URL方式
                    try {//创建网络连接对象
                    URL url = new URL(NetWorkUtils.BASE_URL+"androidVersion/version.txt");
                    URLConnection conn=url.openConnection();//打开连接
                    InputStream in=conn.getInputStream();//读取服务端的version.text流
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
                    String line=bufferedReader.readLine();
                        Log.i("sgk","从服务器中取得的数据为："+line);
                    bufferedReader.close();
                    String[] str=line.split(",");
                    String message = "";
                    //对是否要进行版本升级做判断（只有当前版本号低于服务端版本号事才做更新提示）
                    if (versionCode<Integer.parseInt(str[0])){
                        if (networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                            //Toast.makeText(UpdateActivity.this,"当前网络为2G/3G/4G，请注意您的流量!",Toast.LENGTH_SHORT).show();
                            message="发现新版本（"+str[1]+"),是否更新？（建议在wifi环境下更新）";
                        }else if (networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
                            message="发现新版本（"+str[1]+"),是否更新？";
                        }
                        Message msg=new Message();
                        msg.what=0;
                        msg.obj=message;
                        handler.sendMessage(msg);
                    }else if (versionCode==Integer.parseInt(str[0])){
                        message="当前已是最新版本!";
                        Message msg=new Message();
                            msg.what=2;
                            msg.obj=message;
                            handler.sendMessage(msg);
                    }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

       finish();
    }
}
