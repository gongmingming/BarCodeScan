package com.zkc.io;

public class EmGpio {
    public static native int getGpioMaxNumber();
    
    public static native boolean gpioInit();

    public static native boolean gpioUnInit();

    public static native boolean setGpioInput(int gpioIndex);

    public static native boolean setGpioOutput(int gpioIndex);

    public static native boolean setGpioDataHigh(int gpioIndex);

    public static native boolean setGpioDataLow(int gpioIndex);

    public static native int getCurrent(int hostNumber);

    public static native int newGetCurrent(int hostNumber, int opcode);

    public static native boolean setCurrent(int hostNumber, int currentDataIdx,
            int currentCmdIdx);

    public static native boolean newSetCurrent(int hostNumber, int clkpu,
            int clkpd, int cmdpu, int cmdpd, int datapu, int datapd,
            int hopbit, int hoptime, int opcode);
    
    public static native boolean setSd30Mode(int hostNumber, int sd30Mode,
            int sd30MaxCurrent, int sd30Drive, int sd30PowerControl);

    static {
    	//System.loadLibrary()是Java的JNI机制的一个重要的函数，
    	//作用是把实现了在Java code中声明的native方法的那个libraryload进来
        System.loadLibrary("hjh-gpio");//hjh_gpio

    }
}
