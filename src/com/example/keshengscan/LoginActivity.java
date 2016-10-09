package com.example.keshengscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.keshengscan.utils.Url;
import com.example.keshengscan.utils.UtilTools;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zkc.barcodescan.R;

import java.io.File;
import java.io.FileOutputStream;

public class LoginActivity extends Activity{
	private BaseLoginProgressDialog baseProgressDialog;
	private EditText edit_username;
	private static EditText edit_password;
	private String username, password, baseurl;
	private Context context;
	private Handler handler;
	private SharedPreferences sharedPreferences;
	private static CheckBox rememberPW;
	private static CheckBox autoLogin;
	private Editor editor;
	private Button login,noNetworkLogin;
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		dialog = new ProgressDialog(LoginActivity.this);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		context = this;

		// 将两个CheckBox先在此对象话，并且检查其是否被选中
		rememberPW = (CheckBox) findViewById(R.id.RememberPw);
		autoLogin = (CheckBox) findViewById(R.id.AutoLogin);
		rememberPW.setChecked(true);
		autoLogin.setChecked(true);

		edit_username = (EditText) findViewById(R.id.userNameText);
		edit_password = (EditText) findViewById(R.id.passwdText);

		login = (Button) findViewById(R.id.bnLogin);
		//无网登陆
		noNetworkLogin=(Button) findViewById(R.id.noNetworkLogin);
		sharedPreferences = getSharedPreferences("myShare", 0);
		editor = sharedPreferences.edit();

		// 将url提交给sharedPreferences
		Url urlObj = new Url();
		editor.putString("BASEURL", urlObj.getUrl());//已经写入到sharedPreferences中的URL地址
		editor.commit();

		username = sharedPreferences.getString("USERNAME", "");
		password = sharedPreferences.getString("PASSWORD", "");

		if (!UtilTools.isBlankString(username)) {
			edit_username.setText(username);
		}
		if (!UtilTools.isBlankString(username) && !UtilTools.isBlankString(password)) {
			edit_password.setText(password);
			rememberPW.setChecked(true);// 记住密码符号已经被选中
		}

		handler = new Handler();

		//联网登陆
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginaction();
			}
		});
		//无网登陆
		noNetworkLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent1=new Intent(LoginActivity.this,Activity_main.class);
				startActivity(intent1);
			}
		});
	}


	private void loginaction() {
		// TODO Auto-generated method stub

		username = edit_username.getText().toString();
		password = edit_password.getText().toString();
		//之前初始化过了，不知道为啥要一直再定义一次 editor = sharedPreferences.edit();
		Log.i("TFG","---联网登陆点击了---------");
		if (rememberPW.isChecked()) {
			editor.putString("USERNAME", username);
			editor.putString("PASSWORD", password);
			editor.commit();
		}

		//将url从sharedPreferences中取出，然后赋值给baseurl
		baseurl = sharedPreferences.getString("BASEURL", "");

		if (UtilTools.isBlankString(username)) {
			UtilTools.showToast(context, handler, "用户名为空");
		} else if (UtilTools.isBlankString(password)) {
			UtilTools.showToast(context, handler, "密码为空");
		} else if (UtilTools.isBlankString(baseurl)) {
			UtilTools.showToast(context, handler, "NoURLaddress");
		} else {
			if (UtilTools.hasNetwork(context)) {
				// progressBar.show();
				login.setClickable(false);
//				baseProgressDialog = new BaseLoginProgressDialog(LoginActivity.this);
//				baseProgressDialog.progressDialog();

				String uploadHost = "http://"+baseurl+"/Android/user/login.action";

				// 实例化HttpUtils对象， 参数设置链接超时
				HttpUtils HTTP_UTILS = new HttpUtils(10 * 1000);

				// 实例化RequestParams对象
				RequestParams params = new RequestParams();
				params.addBodyParameter("userName", username);
				params.addBodyParameter("password", password);

				// 通过HTTP_UTILS来发送post请求， 并书写回调函数
				HTTP_UTILS.send(HttpMethod.POST, uploadHost, params,new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
										  boolean isUploading) {
						super.onLoading(total, current, isUploading);
						dialog.show();
					}

					@Override
					public void onFailure(HttpException httpException,String arg1) {
						Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
						Log.e("error",arg1);
						dialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String resResult = responseInfo.result.toString();
						Log.e("error","success!!!!!!!");
						String [] arrys= resResult.split(",");
						String result=arrys[0];
						Log.e("error","result----"+result);
						String userId=arrys[1];
						Log.e("error","userId---"+userId);
						String userName=arrys[2];
						Log.e("error","userName---"+userName);
						String userlevel=arrys[3];
						Log.e("error","userLevel---"+userlevel);

//								String flagFile = getFilesDir()+"//loginInfo/userId.txt";
						String logInfo = userId +","+ userName +","+ userlevel;
						try {
//					                    FileHelper fHelper = new FileHelper(context);
//										//保存文件名和内容
//					                    fHelper .saveFile(flagFile, logInfo);
							File file = new File(getFilesDir(),"userId.txt");
							try {
								FileOutputStream fos = new FileOutputStream(file);
								fos.write(logInfo.getBytes());
								fos.close();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if(result.equals("1")){
								Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(LoginActivity.this, Activity_main.class);
								context.startActivity(intent);
								finish();
							}
							if(result.equals("0"))
							{

								Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
							}

							dialog.dismiss();

						} catch (Exception e) {
							//写入异常时
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "SD卡存在,数据写入失败,请更换SD卡后重试", Toast.LENGTH_SHORT).show();
						}
					}
				});
				login.setClickable(true);
			} else {
				UtilTools.showToast(context, handler, "No Net");
			}
		}
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
