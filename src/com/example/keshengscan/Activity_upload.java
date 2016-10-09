package com.example.keshengscan;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.keshengscan.utils.FileUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zkc.barcodescan.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Activity_upload extends Activity{

	int FILE_SELECT_CODE = 0;
	ProgressDialog dialog;
	String fileUrl;
	EditText savePath;
	uploadThread udThread;
	NotificationManager nManager;
	Context context;
	static final int NOTIFICATION_ID1 = 1;
	static final int NOTIFICATION_ID2 = 2;
	static final int NOTIFICATION_ID3 = 3;
	private String userId;
	private String userName;
	private String userLevel;

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				//fs.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

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
						String fileUrl1 = msg.getData().getString("textFileUrl");
						String userId1 = msg.getData().getString("userId");
						String userName1 = msg.getData().getString("userName");
						String userLevel1 = msg.getData().getString("userLevel");

						String uploadHost = "http://192.168.1.105:8080/Android/upload/file.action";
						// 实例化HttpUtils对象， 参数设置链接超时
						HttpUtils HTTP_UTILS = new HttpUtils(10 * 1000);
						// 实例化RequestParams对象
						RequestParams params = new RequestParams();
						// requestParams.setContentType("multipart/form-data");
						//选中本地存储的那个文件
						File textFile = new File(fileUrl1);
						// imageFile是File格式的对象， 将此File传递给RequestParams
						params.addBodyParameter("textFile", textFile);
						params.addBodyParameter("userId", userId1);
						params.addBodyParameter("userName", userName1);
						params.addBodyParameter("userLevel", userLevel1);

						// 通过HTTP_UTILS来发送post请求， 并书写回调函数
						HTTP_UTILS.send(HttpMethod.POST, uploadHost, params,
								new RequestCallBack<String>() {
									@Override
									public void onLoading(long total, long current,
														  boolean isUploading) {
										super.onLoading(total, current, isUploading);
										nManager.notify(NOTIFICATION_ID1,notify1);
									}

									@Override
									public void onFailure(HttpException httpException,
														  String arg1) {
										nManager.cancel(NOTIFICATION_ID1);
										nManager.notify(NOTIFICATION_ID3,notify3);
										Log.e("error",arg1);
									}

									@Override
									public void onSuccess(ResponseInfo<String> responseInfo) {
										String resResult = responseInfo.result.toString();

										if(resResult.equals("1")){
											EditText savePath = (EditText) findViewById(R.id.savePath);
											fileUrl=savePath.getText().toString();
											File delFile= new File(fileUrl);
											String backupUrl =  Environment.getExternalStorageDirectory().getPath()+"//kesheng/backupFile";
											if (delFile.exists()){
												if (delFile.isFile()){
													copyFile(fileUrl,backupUrl);
													delFile.delete();
												}
											}
											nManager.cancel(NOTIFICATION_ID1);
											nManager.notify(NOTIFICATION_ID2,notify2);

											savePath.setText("");

										}
										if(resResult.equals("0")){

											nManager.cancel(NOTIFICATION_ID1);
											nManager.notify(NOTIFICATION_ID3,notify3);

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
		setContentView(R.layout.activity_upload);
		context = this;
		dialog = new ProgressDialog(Activity_upload.this);
		udThread = new uploadThread();
		udThread.start();
		nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}

	// 选择文件
	public void choosePath(View v) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("text/plain");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(Intent.createChooser(intent, "选择上传文件"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "文件选择失败，请重新选择", Toast.LENGTH_SHORT).show();
		}

	}

	// 路径回显
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case 0:
				if (resultCode == RESULT_OK) {
					// Get the Uri of the selected file
					Uri uri = data.getData();
					String path = FileUtils.getPath(this, uri);
					savePath = (EditText) findViewById(R.id.savePath);
					savePath.setText(path);

				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 上传文件
	public int okUpload(View v) {

		savePath = (EditText) findViewById(R.id.savePath);
		//(1)需要上传的本地文件路径
		fileUrl = savePath.getText().toString();
		//(2)用户名和密码的那个文件
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
		if (fileUrl.length() == 0 || fileUrl == "") {
			Toast.makeText(this, "请选择需要上传的文件", Toast.LENGTH_SHORT).show();
			return 0;
		}
		else
		{
			Message message = new Message();
			message.what = 0x123;
			Bundle bundle = new Bundle();
			//(1)
			bundle.putCharSequence("textFileUrl", fileUrl);
			//(2)
			bundle.putCharSequence("userId", userId);
			bundle.putCharSequence("userName", userName);
			bundle.putCharSequence("userLevel",userLevel);
			message.setData(bundle);
			udThread.mHandler.sendMessage(message);
			return 1;
		}





	}

	// 清空选择路径
	public void uploadBack(View v) {
		savePath = (EditText) findViewById(R.id.savePath);
		savePath.setText("");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("系统提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗");
			// 添加选择按钮并注册监听
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			// 显示对话框
			isExit.show();

		}

		return false;

	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
					finish();
					break;
				case AlertDialog.BUTTON_NEGATIVE:// "取消"对话框
					break;
				default:
					break;
			}
		}
	};
}
	
	


