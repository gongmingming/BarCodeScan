package com.njupt.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.zkc.barcodescan.R;

public class Fragment03 extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduct);

    }

	@Override
	public void onBackPressed() {
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
								finish();
							}
						}).setNegativeButton(R.string.popup_no, null).show();
	}
}
