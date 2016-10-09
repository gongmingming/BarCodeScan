package com.njupt.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.njupt.activity.CaptureActivity;
import com.njupt.utils.CheckNetState;
import com.zkc.barcodescan.R;
import com.zkc.util.FileHelper;
import com.zkc.util.RegexSplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Fragment01 extends Activity {
    public Button IxiangScan1, IpingScan1;
    private LinearLayout tlxiangscan1, tlpingscan1;
    private TextView IxiangIDe1, IpingIDe1;
    private Context mContext;
    String code1, code;
    public FileHelper fHelper;
    CheckNetState checkNet;
    String uploadHost = "http://192.168.1.104:8080/Android/realtime/sellmainin.action";
    ProgressDialog dialog;
    String fileText;
    private String userId;
    private String userName;
    private String userLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        init();
        //箱码扫描
        IxiangScan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(mContext, CaptureActivity.class);
                intent1.putExtra("scansign",1);
                startActivity(intent1);
            }
        });
        //瓶码扫描
        IpingScan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(mContext, CaptureActivity.class);
                intent2.putExtra("scansign",2);
                startActivity(intent2);
            }
        });
        IntentFilter filter = new IntentFilter(CaptureActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }
    //广播接收
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String result = intent.getExtras().getString("data");
            //扫描跳转回来之后的操作
            if (result== null||result.equals("")){
            }else if (fHelper.codeFormat1(result)==false){
                Toast.makeText(Fragment01.this,"二维码格式不符合，请重新扫描！",Toast.LENGTH_SHORT).show();
            }else{
                ArrayList<String> str= RegexSplit.textSplit1(result);
                String btnsign=str.get(0);
                code1=str.get(1);
                if (fHelper.codeFormatNum(code1)){
                    int barsin=Integer.parseInt(String.valueOf(code1.charAt(12)));
                    //箱码扫描
                    if (Integer.parseInt(String.valueOf(btnsign))==1){
                        if (barsin==1){
                            //判断网络状态决定本地存储和上传
                            checkNet(barsin);
                        }else {
                            Toast.makeText(Fragment01.this,"不是指定格式的箱码，请重新扫描！",Toast.LENGTH_SHORT).show();
                        }
                    }else if (Integer.parseInt(String.valueOf(btnsign))==2){//瓶码扫描
                        if (barsin==0){
                            //判断网络状态决定本地存储和上传
                            checkNet(barsin);
                        }else {
                            Toast.makeText(Fragment01.this,"不是指定格式的瓶码，请重新扫描！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(Fragment01.this,"二维码格式不符合，请重新扫描！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    //检查网络
    private void checkNet(final int sin) {
        checkNet = new CheckNetState();
        if (!checkNet.isNetworkConnected(Fragment01.this)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_netmessage1)
                    .setPositiveButton(R.string.popup_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = null;
                                    /**
                                     * 判断手机系统的版本！如果API大于10 就是3.0+
                                     * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                                     */
                                    if (android.os.Build.VERSION.SDK_INT > 10) {
                                        intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                                    } else {
                                        intent = new Intent();
                                        ComponentName component = new ComponentName(
                                                "com.android.settings",
                                                "com.android.settings.WirelessSettings");
                                        intent.setComponent(component);
                                        intent.setAction("android.intent.action.VIEW");
                                    }
                                    startActivity(intent);
                                    tlxiangscan1.setVisibility(View.GONE);
                                    tlpingscan1.setVisibility(View.GONE);
                                }
                            }).setNegativeButton(R.string.popup_no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    }).show();
        } else {
            if (sin==1){//联网上传箱码
                code=RegexSplit.StringSplit2(code1);
                tlxiangscan1.setVisibility(View.VISIBLE);
                IxiangIDe1.setText(code);
                tlpingscan1.setVisibility(View.GONE);
                //final String xiangma=IxiangIDe1.getText().toString();
                //开启一个新线程处理箱码存储
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        File file = new File(getFilesDir(),"userId.txt");
                        try {
                            if(file.exists()){
                                FileInputStream fis = new FileInputStream(file);
                                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                                String text =br.readLine();
                                String[] s = text.split(",");
                                userId= s[0];
                                userName=s[1];
                                userLevel=s[2];
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // 实例化HttpUtils对象， 参数设置链接超时
                        HttpUtils HTTP_UTILS = new HttpUtils(10 * 1000);
                        // 实例化RequestParams对象
                        RequestParams params = new RequestParams();
                        com.zkc.util.Time time = new com.zkc.util.Time();
                        //用户ID/用户名/用户级别/入库/箱码/11位二维码/时间
                        fileText=userId+"/"+userName+"/"+ userLevel+"/1/1/"+code+"/"+time.getCurrentTime();
                        params.addQueryStringParameter("xiangma", fileText);
                        // 通过HTTP_UTILS来发送post请求， 并书写回调函数
                        HTTP_UTILS.send(HttpRequest.HttpMethod.POST, uploadHost, params,new RequestCallBack<String>() {
                            @Override
                            public void onLoading(long total, long current,
                                                  boolean isUploading) {
                                super.onLoading(total, current, isUploading);
                                dialog.show();
                            }
                            @Override
                            public void onFailure(HttpException httpException,String arg1) {
                                Toast.makeText(Fragment01.this, "入库失败", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                String resResult = responseInfo.result.toString();

                                if(resResult.equals("success")){
                                    Toast.makeText(Fragment01.this, "入库成功", Toast.LENGTH_SHORT).show();
                                }
                                if(resResult.equals("error"))
                                {
                                    Toast.makeText(Fragment01.this, "入库失败", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        Looper.loop();
                    }
                }).start();
            }else if (sin==0){//联网上传瓶码
                code=RegexSplit.StringSplit2(code1);
                tlpingscan1.setVisibility(View.VISIBLE);
                IpingIDe1.setText(code);
                tlxiangscan1.setVisibility(View.GONE);
                //开启一个新线程处理瓶码存储
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        File file = new File(getFilesDir(),"userId.txt");
                        try {
                            if(file.exists()){
                                FileInputStream fis = new FileInputStream(file);
                                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                                String text =br.readLine();
                                String[] s = text.split(",");
                                userId= s[0];
                                userName=s[1];
                                userLevel=s[2];
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // 实例化HttpUtils对象， 参数设置链接超时
                        HttpUtils HTTP_UTILS = new HttpUtils(10 * 1000);
                        // 实例化RequestParams对象
                        RequestParams params = new RequestParams();
                        com.zkc.util.Time time = new com.zkc.util.Time();
                        //用户ID/用户名/用户级别/入库/瓶码/11位二维码/时间
                        fileText=userId+"/"+userName+"/"+ userLevel+"/1/0/"+code+"/"+time.getCurrentTime();
                        params.addQueryStringParameter("pingma", fileText);
                        // 通过HTTP_UTILS来发送post请求， 并书写回调函数
                        HTTP_UTILS.send(HttpRequest.HttpMethod.POST, uploadHost, params,new RequestCallBack<String>() {
                            @Override
                            public void onLoading(long total, long current,
                                                  boolean isUploading) {
                                super.onLoading(total, current, isUploading);
                                dialog.show();
                            }
                            @Override
                            public void onFailure(HttpException httpException,String arg1) {
                                Toast.makeText(Fragment01.this, "入库失败", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                String resResult = responseInfo.result.toString();
                                if(resResult.equals("success")){
                                    Toast.makeText(Fragment01.this, "入库成功", Toast.LENGTH_SHORT).show();
                                }
                                if(resResult.equals("error"))
                                {
                                    Toast.makeText(Fragment01.this, "入库失败", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        Looper.loop();
                    }
                }).start();
            }
        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
    private void init() {
        IxiangScan1 = (Button) findViewById(R.id.IxiangScan1);
        tlxiangscan1 = (LinearLayout) findViewById(R.id.tlxiangscan1);
        IxiangIDe1 = (TextView) findViewById(R.id.IxiangIDe1);
        IpingScan1 = (Button) findViewById(R.id.IpingScan1);
        tlpingscan1 = (LinearLayout) findViewById(R.id.tlpingscan1);
        IpingIDe1 = (TextView) findViewById(R.id.IpingIDe1);
        mContext = getApplicationContext();
        fHelper = new FileHelper(mContext);
        dialog = new ProgressDialog(mContext);
    }
    @Override
    public void onBackPressed() {
        finish();
        //exitActivity();
    }
    //退出按钮
    private void exitActivity() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.popup_title)
                .setMessage(R.string.popup_message)
                .setPositiveButton(R.string.popup_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).setNegativeButton(R.string.popup_no, null).show();
    }


}

