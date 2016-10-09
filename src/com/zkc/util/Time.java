package com.zkc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	public String getCurrentTime() {
		Date currentTime=new Date();
		SimpleDateFormat dateFormater=new SimpleDateFormat("yyyy-MM-dd");
		String strDate=dateFormater.format(currentTime);
		return strDate;
	}
}
