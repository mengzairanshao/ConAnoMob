package com.person.han.conanomob.Const;

public class BluetoothMsg {

    public enum ServerOrClient {
        NONE,
        SERVICE,
        CLIENT
    };
    //蓝牙连接方式
    public static ServerOrClient serviceOrClient = ServerOrClient.NONE;
    //连接蓝牙地址
    public static String BlueToothAddress = null, lastBlueToothAddress =null;
    //通信线程是否开启
    public static boolean isOpen = false;
}
