package com.example.keshengscan;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BaseLoginProgressDialog {
	private ProgressDialog progressDialog;
	private Context context;
	private static final int SHOW_TIME_MAX = 10000;// 最长显示时间  
    private long mStartTime;// 开始时间
    private Timer timer ;
    int i = 0;
    
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}


	private Handler myhandler  = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0x1233){
				if(progressDialog.isShowing()){
					 long loadingTime = System.currentTimeMillis() - mStartTime;// 计算一下总共花费的时间  
		             if (loadingTime < SHOW_TIME_MAX) {// 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入  
		 
		             } else {  
		            	 if(progressDialog.isShowing()){
		            		 progressDialog.dismiss();
			            	 Toast.makeText(context, "加载超时,请查看网络连接状态", Toast.LENGTH_SHORT).show();
			            	 timer.cancel();
		            	 }else{
		            		 timer.cancel();
		            	 }
		             }  
				}else{
					 timer.cancel();
				}
			}
//			if(msg.what == 0){
//				System.out.println("-----------------0");
//				progressDialog.dismiss();
//				timer.cancel();
//				
//			}
		}
		

	};

	
	
	public BaseLoginProgressDialog( Context context) {
		this.context = context;
	}
	
	public void timer(){
		timer = new Timer();
		timer.schedule(new TimerTask() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 0x1233;
				myhandler.sendMessage(msg);
				System.out.println("time----------" + i);
				i++;
			}
			},0,1000);
	}

	public void progressDialog(){
		mStartTime = System.currentTimeMillis();//记录开始时间
		timer();
		progressDialog = ProgressDialog.show(context, "加载中", "请稍等。。。", true, false); 
	}
	
}