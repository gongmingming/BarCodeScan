package com.zkc.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileHelper {

	private Context mContext;

	public FileHelper(Context mContext) {
		super();
		this.mContext = mContext;
	}

	//空参数构造函数，传入的值为空时，不出错
	public FileHelper() {
	}

	//在sd卡下创建文件夹
	public void sd_mkdir(String str) {

		File sd=Environment.getExternalStorageDirectory();
		String path=sd.getPath()+"/"+str;
		File file=new File(path);
		if(!file.exists())
			file.mkdir();

	}

	//扫描保存到文件，所以是输出流
	public void scanSaveFile(String filename, String str) throws IOException {
		File f = new File(filename);
		//判断文件是否存在，若不存在则创建
		if(!f.exists()){
			f.createNewFile();
		}
		//判断文件中是否有该字符串
		if(haveString(filename, str)){
			Toast.makeText(mContext, "已经扫描过了!", Toast.LENGTH_SHORT).show();
		}else {
			try {
				//Context.MODE_APPEND模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件
				FileWriter writer = new FileWriter(filename, true);
				//将String字符串以字节流的形式写入到输出流中
				writer.write(str);
				writer.write("\r\n");
				writer.flush();
				writer.close();
				//Toast.makeText(mContext, "扫描完成", Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	//保存到文件，所以是输出流
	public void saveFile(String filename, String str) throws IOException {
		File f = new File(filename);
		//判断文件是否存在，若不存在则创建
		if(!f.exists()){
			f.createNewFile();
		}
		//判断文件中是否有该字符串
		if(haveString(filename, str)){
			Toast.makeText(mContext, "已经扫描过了!", Toast.LENGTH_SHORT).show();
		}else {
			try {
				//Context.MODE_APPEND模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件
				//FileWriter writer = new FileWriter(filename, true);
				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename,true),"UTF-8");
				//将String字符串以字节流的形式写入到输出流中
				out.write(str);
				out.write("\r\n");
				out.flush();
				out.close();
				Toast.makeText(mContext, "本地保存完成，请点击下一步，输入用户信息完成上传！", Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	//保存到文件，所以是输出流
	public void saveFile3(String filename, String str) throws IOException {
		File f = new File(filename);
		//判断文件是否存在，若不存在则创建
		if(!f.exists()){
			f.createNewFile();
		}
		//判断文件中是否有该字符串
		if(haveString(filename, str)){
			Toast.makeText(mContext, "已经确认过了!", Toast.LENGTH_SHORT).show();
		}else {
			try {
				//Context.MODE_APPEND模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件
				//OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename,true),"UTF-8");
				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename,true));
				//将String字符串以字节流的形式写入到输出流中
				out.write(str);
				out.write("\r\n");
				out.write("\r\n");
				out.flush();
				out.close();
				Toast.makeText(mContext, "信息确认完成，请点击联网上传！", Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	//FileInputStream读取数据（filename是读取文件的名称，reads是返回读取的字符串）
	public String readFile1(String filename) {
		StringBuilder sb = null;
		try {
			//打开文件输入流
			FileInputStream fis = mContext.openFileInput(filename);
			//定义1M的缓冲区
			byte[] b = new byte[1024];
			//定义字符串变量
			sb = new StringBuilder("");
			//ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			//读取文件内容，当文件内容长度大于0时，
			while ((len=fis.read(b)) > 0) {
				//baos.write(b, 0, b.length);
				//把字条串连接到尾部
				sb.append(new String(b, 0, len));
			}
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//返回字符串
		return sb.toString();
	}

	//使用BufferReader读取文件
	public String readFile(String filename) {
		StringBuilder sb = null;
		try {
			File file = new File(filename);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";
			sb = new StringBuilder("");
			while ((readline = br.readLine()) != null) {
				sb.append(readline);
			}
			br.close();
			//Toast.makeText(mContext, "读取成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public boolean haveString(String filename, String str) throws IOException{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			LinkedList<String> list = new LinkedList<String>();
			while ((line = reader.readLine()) != null) {
				if (line.equals(str)) {
					return true;
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	//判断扫描出的二维码是否为24位数字
	public boolean codeFormatNum(String string) {
		//正则表达式
		Pattern pattern = Pattern.compile("[0-9]{24}");
		Matcher matcher = pattern.matcher((CharSequence) string);
		return matcher.matches();
	}

	//判断箱码是否为11位数字
	public boolean codeFormat11(String string) {
		//正则表达式
		Pattern pattern = Pattern.compile("[0-9]{11}");
		Matcher matcher = pattern.matcher((CharSequence) string);
		return matcher.matches();
	}

	//判断扫描出的二维码是否为指定格式
	public boolean codeFormat(String string) {
		if (string.contains("www.kesheng.com/")&&string.contains("?id=")) {
			return true;
		} else {
			return false;
		}
	}
	//判断扫描出的二维码是否为最初判断格式
	public boolean codeFormat1(String string) {
		if (string.contains("#www.kesheng.com/")&&string.contains("?id=")) {
			return true;
		} else {
			return false;
		}
	}
	public boolean codeFormat2(String string) {
		if (string.contains("#www.kesheng.com/")&&string.contains("?id=")) {
			return true;
		} else {
			return false;
		}
	}
	//判断用户手机格式
	public Boolean phoneFormat(String s) {
		return s.matches("^(13|14|15|17|18|19)\\d{9}$");
	}
	//判断用户地址格式
	public Boolean addressFormat(String s) {
		return s.matches("^[0-9\u4e00-\u9fa5]+$");
	}

	public Boolean nameFormat(String s) {
		//return s.matches("^[a-zA-Z\u4e00-\u9fa5]+$");
		return s.matches("[\u4e00-\u9fa5]{2,4}");
	}
}
