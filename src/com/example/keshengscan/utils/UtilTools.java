package com.example.keshengscan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

public class UtilTools {

	public static boolean isBlankString(String str) {
		if (str == null || str.equals("") || str.equals("null")) {
			return true;
		}
		return false;
	}
	
	public static void showToast(final Context context, Handler handler,
			final String content) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public static boolean hasNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null || !activeNetInfo.isConnected()) {
			return false;
		}
		return true;
	}
}
