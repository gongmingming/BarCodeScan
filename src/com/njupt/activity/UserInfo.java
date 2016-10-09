package com.njupt.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.njupt.utils.CheckNetState;
import com.zkc.barcodescan.R;
import com.zkc.util.FileHelper;

import cascade.activity.BaseActivity;
import widget.OnWheelChangedListener;
import widget.WheelView;

public class UserInfo extends BaseActivity implements View.OnClickListener, OnWheelChangedListener {
    private EditText id,name, telnum,birthday,registtime,points;
    private EditText lastfillformtime;
    private Button Iupload3;
    public FileHelper fHelper;
    private Context mContext;
    CheckNetState checkNet;
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    String uploadHost = "http://192.168.1.104:8080/Android/realtime/sellmainout.action";
    Context context;
    static final int NOTIFICATION_ID1 = 1;
    static final int NOTIFICATION_ID2 = 2;
    static final int NOTIFICATION_ID3 = 3;
    NotificationManager nManager;
    uploadThread udThread;
    ProgressDialog dialog;

    class uploadThread extends Thread{
        public Handler mHandler;
        Notification notify1 = new Notification.Builder(context)
                .setAutoCancel(true)
                .setTicker("有新消息！")
                .setSmallIcon(R.drawable.upload)
                .setContentTitle("注意！！")
                .setContentText("正在上传······")
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        Notification notify2 = new Notification.Builder(context)
                .setAutoCancel(true)
                .setTicker("有新消息！")
                .setSmallIcon(R.drawable.upload)
                .setContentTitle("恭喜！！")
                .setContentText("上传成功")
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        Notification notify3 = new Notification.Builder(context)
                .setAutoCancel(true)
                .setTicker("有新消息！")
                .setSmallIcon(R.drawable.upload)
                .setContentTitle("警告！！")
                .setContentText("上传失败")
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        public void run()
        {
            Looper.prepare();
            mHandler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    if (msg.what == 0x123) {
                        String farmInfo = msg.getData().getString("farmInfo");
                        // 实例化HttpUtils对象， 参数设置链接超时
                        HttpUtils HTTP_UTILS = new HttpUtils(10 * 1000);
                        // 实例化RequestParams对象
                        RequestParams params = new RequestParams();
                        // requestParams.setContentType("multipart/form-data");
                        SharedPreferences pref=getSharedPreferences("soldData", MODE_APPEND);
                        com.zkc.util.Time time = new com.zkc.util.Time();
                        String soldpingma=pref.getString("soldpingma", "");
                        params.addQueryStringParameter("soldtime", time.getCurrentTime()); //售出瓶码时间
                        params.addQueryStringParameter("farmInfo", farmInfo); //农户信息字符串
                        params.addQueryStringParameter("soldpingma", soldpingma); //售出瓶码信息字符串
                        //params.addBodyParameter("soldtime", time.getCurrentTime()); //售出瓶码时间
                        // 通过HTTP_UTILS来发送post请求， 并书写回调函数
                        HTTP_UTILS.send(HttpRequest.HttpMethod.POST, uploadHost, params,
                                new RequestCallBack<String>() {
                                    @Override
                                    public void onLoading(long total, long current,
                                                          boolean isUploading) {
                                        super.onLoading(total, current, isUploading);
                                        nManager.notify(NOTIFICATION_ID1, notify1);
                                    }

                                    @Override
                                    public void onFailure(HttpException httpException,
                                                          String arg1) {
                                        nManager.cancel(NOTIFICATION_ID1);
                                        //nManager.notify(NOTIFICATION_ID3,notify3);
                                        Toast.makeText(UserInfo.this, "上传失败！", Toast.LENGTH_SHORT).show();
                                        Log.e("error", arg1);
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        String resResult = responseInfo.result.toString();
                                        if (resResult.equals("success")) {
                                            nManager.cancel(NOTIFICATION_ID1);
                                            //nManager.notify(NOTIFICATION_ID2, notify2);
                                            Toast.makeText(UserInfo.this, "上传成功！", Toast.LENGTH_SHORT).show();
                                            //清空SharedPreferences中的soldpingma数据，文件在Fragment02退出时删除
                                            SharedPreferences.Editor editor = getSharedPreferences("soldData", MODE_APPEND).edit();
                                            editor.remove("soldpingma");
                                            editor.commit();
                                        }
                                        if (resResult.equals("error")) {
                                            nManager.cancel(NOTIFICATION_ID1);
                                            //nManager.notify(NOTIFICATION_ID3,notify3);
                                            Toast.makeText(UserInfo.this, "上传失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            };
            Looper.loop();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);
        init();
        setUpListener();
        setUpData();

    }
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Iupload3:   //点击联网上传
                sureaction();
                break;
            default:
                break;
        }
    }

    private void sureaction() {
        Boolean bphone = fHelper.phoneFormat(telnum.getText().toString());
        Boolean bname = fHelper.nameFormat(name.getText().toString());
        if(bname==false){
            Toast.makeText(UserInfo.this, "请输入正确的用户名字", Toast.LENGTH_SHORT).show();
        }else if(bphone==false){
            Toast.makeText(UserInfo.this,"请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
        }else if (name.getText().toString().equals("")||telnum.getText().toString().equals("")){
            Toast.makeText(UserInfo.this,"请输入完整的用户信息", Toast.LENGTH_SHORT).show();
        }else {
            checkNet();
        }
    }
    //检查网络
    private void checkNet() {
        checkNet=new CheckNetState();
        if (!checkNet.isNetworkConnected(UserInfo.this)){
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
                                }
                            }).setNegativeButton(R.string.popup_no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Toast.makeText(UserInfo.this, "请联网上传！", Toast.LENGTH_SHORT).show();
                        }}).show();
        }else {
            //联网操作。正在进行联网上传，请稍后
            Toast.makeText(UserInfo.this, "正在进行联网上传，请稍后", Toast.LENGTH_SHORT).show();
            Message message = new Message();
            message.what = 0x123;
            Bundle bundle = new Bundle();
            String farmInfo=name.getText().toString()+"/"+telnum.getText().toString()+"/"
                    +mCurrentProviceName +"/"+mCurrentCityName +"/"
                    +mCurrentDistrictName;
            bundle.putCharSequence("farmInfo", farmInfo); //农户信息
            message.setData(bundle);
            udThread.mHandler.sendMessage(message);
        }
    }
    //设置事件监听
    private void setUpListener() {
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);
        Iupload3.setOnClickListener(this);
    }
    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new widget.adapters.ArrayWheelAdapter<String>(UserInfo.this, mProvinceDatas));
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
        mViewDistrict.setViewAdapter(new widget.adapters.ArrayWheelAdapter<String>(this, areas));
        mViewDistrict.setCurrentItem(0);
    }

    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new widget.adapters.ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    private void init() {
        name=(EditText)findViewById(R.id.name);
        telnum=(EditText)findViewById(R.id.telnum);
        Iupload3=(Button)findViewById(R.id.Iupload3);
        mContext = getApplicationContext();
        fHelper = new FileHelper(mContext);
        context=this;
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        dialog = new ProgressDialog(UserInfo.this);
        udThread = new uploadThread();
        udThread.start();
        nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

