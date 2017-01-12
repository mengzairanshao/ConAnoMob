package com.person.han.conanomob.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.person.han.conanomob.Const.Const;
import com.person.han.conanomob.Utility.Exc;

public class BTServer {
    private String TAG = "BTServer";

    /* 一些常量，代表服务器的名称 */
    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
    public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

    private BluetoothServerSocket btServerSocket = null;
    private BluetoothSocket btsocket = null;
    private BluetoothAdapter mBtAdapter = null;
    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Handler detectedHandler = null;

    public BTServer(BluetoothAdapter mBtAdapter, Handler detectedHandler) {
        this.mBtAdapter = mBtAdapter;
        this.detectedHandler = detectedHandler;
    }

    public void startBTServer() {
        ThreadPool.getInstance().excuteTask(new Runnable() {
            public void run() {
                try {

                    btServerSocket = mBtAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    handlerMsg("请稍候，正在等待客户端的连接...", -1);
                    btsocket = btServerSocket.accept();
                    handlerMsg("客户端已经连接上！可以发送信息。", -1);
                    receiverMessageTask();
                } catch (EOFException e) {
                    e.printStackTrace();
                    handlerMsg("client has close!",  Const.arg_error);
                } catch (IOException e) {
                    e.printStackTrace();
                    handlerMsg("receiver message error! please make client try again connect!",  Const.arg_error);
                }
            }
        });
    }

    private void receiverMessageTask() {
        ThreadPool.getInstance().excuteTask(new Runnable() {
            public void run() {
                byte[] buffer = new byte[2048];
                int totalRead;
                try {
                    bis = new BufferedInputStream(btsocket.getInputStream());
                    bos = new BufferedOutputStream(btsocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String txt = "";
                    if (Exc.common_exc(Exc.GET_CPU_INFO)== Const.Qualcomm){
                        while((totalRead = bis.read(buffer)) >0){
                            //		 arrayOutput=new ByteArrayOutputStream();
                            txt = new String(buffer, 0, totalRead, "UTF-8");
                            handlerMsg(txt, -1);
                        }
                    }
                    if (Exc.common_exc(Exc.GET_CPU_INFO)== Const.MTK){
                        while (bis.available() > 0) {
                            totalRead = bis.available();
                            bis.read(buffer, 0, totalRead);
                            txt = new String(buffer, 0, totalRead, "UTF-8");
                            handlerMsg(txt, -1);
                        }
                        txt = "连接断开,正在作为服务器重启!";
                        handlerMsg(txt,  Const.arg_CloseClientAndServerAndRebootAsServer);
                    }

                } catch (IOException e) {
                    if (Exc.common_exc(Exc.GET_CPU_INFO)== Const.Qualcomm) {
                        String txt = "连接断开,正在作为服务器重启!";
                        handlerMsg(txt,  Const.arg_CloseClientAndServerAndRebootAsServer);
                    }
                        e.printStackTrace();
                }
            }
        });
    }

    public boolean sendmsg(String msg) {
        boolean result = false;
        if(null==btsocket||bos==null){
            handlerMsg(Const.ServerSendFail,-1);
            return false;
        }
        try {
            bos.write(msg.getBytes());
            bos.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) handlerMsg(msg,-1);
        else handlerMsg(Const.ServerSendFail,-1);
        return result;
    }

    public void closeBTServer() {
        try {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
            if (btServerSocket != null)
                btServerSocket.close();
            if (btsocket != null)
                btsocket.close();
            bis=null;
            bos=null;
            btServerSocket=null;
            btsocket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return sendmsg(Const.TestConnect);
    }

    public void handlerMsg(Object obj,int arg1){
        Message msg = new Message();
        if (Pattern.compile(Const.isInfoPattern).matcher(obj.toString()).find()) msg.what= Const.SelectInfo;
        else msg.what = Const.SelectServer;
        msg.obj = obj;
        msg.arg1 = arg1;
        detectedHandler.sendMessage(msg);
    }
}
