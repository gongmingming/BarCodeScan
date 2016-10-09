package com.njupt.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.njupt.activity.CaptureActivity;
import com.njupt.activity.UserInfo;
import com.zkc.barcodescan.R;
import com.zkc.util.FileHelper;
import com.zkc.util.RegexSplit;

import java.util.ArrayList;
import java.util.List;

public class Fragment02 extends Activity {

	private TableLayout pingNumber;
	private EditText SnumIDe2, temp;
	private TextView countping;
	private Button sure, SpingIDb2, soldnext;
	private GridLayout grid1;
	private Context mContext;
	int count_temp;
	int pingshu_global;
	String code1, code;
	public FileHelper fHelper;
	private ScanBroadcastReceiver3 scanBroadcastReceiver;
	String filename;
	String filetext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sold);
		init();
		if (!SnumIDe2.getText().toString().equals("")) {
			initGrid();
		}
		//点击“确定”按钮
		sure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (SnumIDe2.getText().toString().equals("")) {
					Toast.makeText(Fragment02.this, "请输入瓶数", Toast.LENGTH_LONG).show();
				} else {
					if (sure.getText().toString().equals("确定")) {
						initGrid();
						SnumIDe2.setFocusable(false);
						//强制关闭SnumIDe2的软键盘
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(SnumIDe2.getWindowToken(), 0);
						sure.setText("修改");
					} else {
						SnumIDe2.setFocusable(true);
						SnumIDe2.setFocusableInTouchMode(true);
						SnumIDe2.requestFocus();
						SnumIDe2.findFocus();
						countping.setText("");
						sure.setText("确定");
					}
				}
			}
		});
		//瓶码扫描
		SpingIDb2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SnumIDe2.getText().toString().equals("")) {
					Toast.makeText(Fragment02.this, "请输入用户购买的瓶数", Toast.LENGTH_LONG).show();
				} else if (sure.getText().toString().equals("修改")) {
					Intent intent3 = new Intent(mContext, CaptureActivity.class);
					intent3.putExtra("scansign", 3);
					startActivity(intent3);
				}
			}
		});
		//发送广播
		IntentFilter filter = new IntentFilter(CaptureActivity.action3);
		scanBroadcastReceiver = new ScanBroadcastReceiver3();
		registerReceiver(scanBroadcastReceiver, filter);
		//点击“下一步”按钮
		soldnext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				count_temp = 0;
				Boolean flag = false;
				while (count_temp < pingshu_global) {
					temp = (EditText) findViewById(count_temp);
					flag = flag || temp.getText().toString().equals("");
					count_temp++;
				}
				if (flag || SnumIDe2.getText().toString().equals("") || Integer.valueOf(SnumIDe2.getText().toString()) <= 0) {//||
					Toast.makeText(Fragment02.this, "请先扫描完瓶码再填写用户信息", Toast.LENGTH_SHORT).show();
				} else {
					sure.setText("确定");
					Intent intent2 = new Intent(Fragment02.this, UserInfo.class);
					startActivity(intent2);
				}
			}
		});
	}

	//广播接收
	class ScanBroadcastReceiver3 extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String result = intent.getExtras().getString("data3");
			//扫描跳转回来之后的操作
			if (result == null || result.equals("")) {
			} else if (fHelper.codeFormat2(result) == false) {
				Toast.makeText(Fragment02.this, "二维码格式不符合，请重新扫描！", Toast.LENGTH_LONG).show();
			} else {
				ArrayList<String> str = RegexSplit.textSplit1(result);
				String btnsign = str.get(0);
				code1 = str.get(1);
				if (fHelper.codeFormatNum(code1)) {
					int sin = Integer.parseInt(String.valueOf(code1.charAt(12)));
					if (Integer.parseInt(String.valueOf(btnsign)) == 3 && sin == 0) {//瓶码扫描
						code = RegexSplit.StringSplit2(code1);
						//得到瓶码之后显示
						count_temp = 0;
						while (count_temp < pingshu_global) {
							//count_temp为数字，这个id已经在initGrid()方法中定义了
							temp = (EditText) findViewById(count_temp);
							if (temp.getText().toString().equals(code)) {
								Toast.makeText(Fragment02.this, "该瓶码已经扫过了！", Toast.LENGTH_SHORT).show();
								break;
							}
							if (temp.getText().toString().equals("")) {
								temp.setText(code);
								countping.setText(count_temp + 1 + "/" + pingshu_global);
								break;
							}
							count_temp++;
						}
						if (count_temp >= pingshu_global - 1) {
							//本地存储
							new Thread(new Runnable() {
								@Override
								public void run() {
									saveSharedPreferences();
								}
							}).start();
							sure.setText("修改");
							countping.setText("");
						}
					} else {
						Toast.makeText(Fragment02.this, "不是指定格式的瓶码，请重新扫描！", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(Fragment02.this, "二维码格式不符合，请重新扫描！", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void saveSharedPreferences() {
		SharedPreferences.Editor editor=getSharedPreferences("soldData", MODE_APPEND).edit();
		count_temp = 0;
		List list = new ArrayList();//定义一个list
		while (count_temp < pingshu_global) {
			temp = (EditText) findViewById(count_temp);
			list.add(temp.getText().toString() + "/");
			count_temp++;
		}
		String str = "";
		for(int i=0;i<list.size();i++){
			str+=list.get(i);
		}
		editor.putString("soldpingma",str);
		editor.commit();
	}
	private void initGrid() {
		int count = grid1.getChildCount();
		if (count > 0) {
			for (count_temp = count - 1; count_temp >= 0; count_temp--) {
				grid1.removeViewAt(count_temp);
			}
		}
		int rows = Integer.parseInt(SnumIDe2.getText().toString());
		grid1.setRowCount(rows);
		grid1.setColumnCount(2);
		for (count_temp = 0; count_temp < rows; count_temp++) {
			TextView textView = new TextView(Fragment02.this);
			textView.setText("瓶码" + (count_temp + 1));
			textView.setTextSize(15);
			textView.setTextColor(Color.BLACK);
			GridLayout.Spec rowSpec = GridLayout.spec(count_temp);
			GridLayout.Spec columnSpec = GridLayout.spec(0);
			GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
			params.setGravity(Gravity.FILL);
			grid1.addView(textView, params);
			EditText edittext = new EditText(Fragment02.this);
			edittext.setFocusable(false);
			//为edittext.setId(count_temp);这一步在显示扫描结果时会被定位用到
			edittext.setId(count_temp);
			edittext.setTextSize(15);
			edittext.setTextColor(Color.BLACK);
			edittext.setSingleLine(true);
			edittext.setBackgroundResource(R.drawable.a);
			GridLayout.Spec rowSpec_1 = GridLayout.spec(count_temp);
			GridLayout.Spec columnSpec_1 = GridLayout.spec(1);
			GridLayout.LayoutParams params_1 = new GridLayout.LayoutParams(rowSpec_1, columnSpec_1);
			params.setGravity(Gravity.FILL);
			grid1.addView(edittext, params_1);
		}
		pingshu_global = rows;
	}
	private void init() {
		pingNumber = (TableLayout) findViewById(R.id.pingNumber);
		SnumIDe2 = (EditText) findViewById(R.id.SnumIDe2);
		sure = (Button) findViewById(R.id.sure);
		SpingIDb2 = (Button) findViewById(R.id.SpingIDb2);
		grid1 = (GridLayout) findViewById(R.id.SpingmaLayout);
		soldnext = (Button) findViewById(R.id.soldnext);
		countping = (TextView)findViewById(R.id.countping);
		mContext = getApplicationContext();
		fHelper = new FileHelper(mContext);
	}
	@Override
	public void onBackPressed() {
		//finish();
		exitActivity();
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
								//删除SharedPreferences中的soldData文件
								SharedPreferences.Editor editor=getSharedPreferences("soldData",MODE_APPEND).edit();
								editor.clear().commit();
								finish();
							}
						}).setNegativeButton(R.string.popup_no, null).show();
	}
}

