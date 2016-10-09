package com.zkc.util;

import java.util.ArrayList;

public class RegexSplit {
	public static String code, productcode;
	public static char signcode;
	public static String split0, split1, split3, split4;

	public static String StringSplit(String text){

		String splitCode1[]=text.split("\\?");
		//字符串text中？前的字符串
		split0=splitCode1[0];
		//字符串text中？后的字符串
		split1=splitCode1[1];
		String splitCode2[]=split1.split("\\=");
		//字符串text以？分割之后的=前面的字符串
		split3=splitCode2[0];
		//字符串text以？分割之后的=后面的字符串
		split4=splitCode2[1];
		//产品码
		productcode=split4.substring(0, 11);
		//箱码或瓶码标记。这里箱码为1，瓶码为0
		signcode=split4.charAt(12);
		//编号码
		code=split4.substring(13, split4.length());
		return code;
	}
	//从24位二维码中截取后11位
	public static String StringSplit2(String text){
		//产品码
		productcode=text.substring(0, 11);
		//箱码或瓶码标记。这里箱码为1，瓶码为0
		signcode=text.charAt(12);
		//编号码
		code=text.substring(13, split4.length());
		return code;
	}
	public static String textSplit(String text) {
		String splitCode1[]=text.split("\\?");
		//字符串text中？后的字符串
		split1=splitCode1[1];
		String splitCode2[]=split1.split("\\=");
		//字符串text以？分割之后的=后面的字符串
		split4=splitCode2[1];
		//箱码或瓶码标记。这里箱码为1，瓶码为0
		signcode=split4.charAt(12);
		return split4;
	}
	//带按钮扫描标志scansign的字符串分割
	public static ArrayList<String> textSplit1(String text) {
		String splitstr[]=text.split("\\#");
		String btnsign=splitstr[0];
		String strcode=splitstr[1];
		String splitCode1[]=strcode.split("\\?");
		//字符串text中？后的字符串
		split1=splitCode1[1];
		String splitCode2[]=split1.split("\\=");
		//字符串text以？分割之后的=后面的字符串
		split4=splitCode2[1];
		//箱码或瓶码标记。这里箱码为1，瓶码为0
		signcode=split4.charAt(12);
		ArrayList<String> strArray = new ArrayList<String> ();
		strArray.add(btnsign);
		strArray.add(split4);
		return strArray;
	}
}
