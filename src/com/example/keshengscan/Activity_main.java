package com.example.keshengscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.njupt.Fragment.Fragment01;
import com.njupt.Fragment.Fragment02;
import com.njupt.Fragment.Fragment03;
import com.njupt.Fragment.MyPagerAdapter;
import com.zkc.barcodescan.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_main extends Activity {
	private ViewPager mViewPager;
	private PagerAdapter mAdapter;
	LocalActivityManager manager = null;
	Context context = null;
	private List<Activity> fragments = new ArrayList<Activity>();
	public static RadioGroup radioderGroup;
	private RadioButton tab1_tv,tab2_tv,tab3_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		initView();
		context = Activity_main.this;
		manager = new LocalActivityManager(this , true);
		manager.dispatchCreate(savedInstanceState);
		initPagerViewer();
	}
	//初始化PageViewer
	private void initPagerViewer() {
		final ArrayList<View> list = new ArrayList<View>();
		Intent intent = new Intent(context, Fragment01.class);
		list.add(getView("A", intent));
		Intent intent2 = new Intent(context, Fragment02.class);
		list.add(getView("B", intent2));
		Intent intent3 = new Intent(context, Fragment03.class);
		list.add(getView("C", intent3));

		mViewPager.setAdapter(new MyPagerAdapter(list));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new TabOnPageChangeListener());
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}


	private void initView() {
		mViewPager=(ViewPager)findViewById(R.id.id_viewpager);
		radioderGroup=(RadioGroup)findViewById(R.id.main_radio);
		tab1_tv=(RadioButton)findViewById(R.id.mainTabs_radio_input);
		tab2_tv=(RadioButton)findViewById(R.id.mainTabs_radio_sold);
		tab3_tv=(RadioButton)findViewById(R.id.mainTabs_radio_introduct);
		tab1_tv.setOnClickListener(new TabOnClickListener(0));
		tab2_tv.setOnClickListener(new TabOnClickListener(1));
		tab3_tv.setOnClickListener(new TabOnClickListener(2));
		tab1_tv.setChecked(true);
	}
	//功能：Fragment页面改变事件
	public class TabOnPageChangeListener implements ViewPager.OnPageChangeListener {
		//当滑动状态改变时调用
		public void onPageScrollStateChanged(int state) {

		}
		//当前页面被滑动时调用
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

		}
		//当新的页面被选中时调用
		public void onPageSelected(int position) {
			switch (position) {
				case 0:
					tab1_tv.setChecked(true);
					tab2_tv.setChecked(false);
					tab3_tv.setChecked(false);
					break;
				case 1:
					tab2_tv.setChecked(true);
					tab1_tv.setChecked(false);
					tab3_tv.setChecked(false);
					break;
				case 2:
					tab3_tv.setChecked(true);
					tab1_tv.setChecked(false);
					tab2_tv.setChecked(false);
					break;
			}
			mViewPager.setCurrentItem(position);
		}
	}
	// 功能：点击主页TAB事件
	public class TabOnClickListener implements View.OnClickListener{
		private int index=0;
		public TabOnClickListener(int i){
			index=i;
		}
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);//选择某一页
		}
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


	@Override
	protected void onResume() {
		super.onResume();
		Log.i("TDG", "result resume    daole---------");

	}

}
