package com.person.han.conanomob.Const;

/**
 * Created by han on 2016/12/8.
 */

public class Const {
    public static String TestConnect = "Test Connect";
    public static String ClientSendFail = "Client Send Fail!";
    public static String ServerSendFail = "Server Send Fail!";
    public static String Disconnected = "Disconnected! please reconnect!";

    //
    public static final int SelectClient = 0;
    public static final int SelectServer = 1;
    public static final int SelectNone = 2;
    public static final int SelectServerAndClient = 3;
    public static final int SelectInfo = 4;

    //CPU
    public static final int MTK = 1;
    public static final int Qualcomm = 2;
    //CPU end

    //system info pattern
    public static String isInfoPattern = "^\\[info\\]";
    public static String Info = "[info]";
    public static String regex = "##";
    public static String batteryLevel = "1" + regex;
    //system info pattern end

    //dataId
    public static final int SERVER_INFO=0;
    public static final int CLIENT_INFO=1;
    public static final int SERVER_INNER_INFO=2;
    public static final int SYS_BATTERY_INFO =3;
    public static final int SYS_WIFI_INFO =4;
    public static final int SYS_BLUETOOTH_INFO =5;
    //dataId end

    //Inner Command
    public static String InnerCommand = "Inner Command";
    public static final int arg_startSearchActivity = 0;
    public static final int arg_CloseClientAndServerAndRebootAsServer = 1;
    public static final int arg_error= 2;
    //Inner Command end

}
