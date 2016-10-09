package com.zkc.pc700.helper;


import com.zkc.io.EmGpio;

public class ScanGpio {

	// 打开电池
	public void openPower() {
		try {
			if (true == EmGpio.gpioInit()) { //初始化了一个GPIO结构体
				// ��Դ����
				EmGpio.setGpioOutput(111);
				EmGpio.setGpioDataLow(111);
				Thread.sleep(100);
				// ��Դ����
				EmGpio.setGpioOutput(111);
				EmGpio.setGpioDataHigh(111);
				Thread.sleep(100);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void closePower() {
		try {
			if (true == EmGpio.gpioInit()) {
				// ��Դ����
				EmGpio.setGpioOutput(111);
				EmGpio.setGpioDataLow(111);
				Thread.sleep(100);
				EmGpio.setGpioInput(111);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 打开扫描
	public void openScan() {
		// ����ɨ��
		try {
			if (true == EmGpio.gpioInit()) {
				EmGpio.setGpioOutput(110);
				EmGpio.setGpioDataHigh(110);
				Thread.sleep(100);
				EmGpio.setGpioDataLow(110);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	//  关闭扫描
	public void closeScan() {
		// ����ɨ��
		try {
			if (true == EmGpio.gpioInit()) {
				EmGpio.setGpioOutput(110);
				EmGpio.setGpioDataHigh(110);
			}
			EmGpio.gpioUnInit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}