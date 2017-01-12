package com.person.han.conanomob.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.person.han.conanomob.Const.BluetoothMsg;
import com.person.han.conanomob.Const.Const;
import com.person.han.conanomob.Utility.Exc;


public class BTClient{

    final String TAG =getClass().getSimpleName();
    private BluetoothSocket btsocket = null;
    private BluetoothDevice btdevice = null;
    private BufferedInputStream bis=null;
    private BufferedOutputStream bos=null;
    private BluetoothAdapter mBtAdapter =null;

    private Handler detectedHandler=null;

    public BTClient(BluetoothAdapter mBtAdapter,Handler detectedHandler){
        this.mBtAdapter=mBtAdapter;
        this.detectedHandler=detectedHandler;
    }

    public void connectBTServer(String address){
        //check address is correct
        if(BluetoothAdapter.checkBluetoothAddress(address)){
            btdevice = mBtAdapter.getRemoteDevice(address);
            ThreadPool.getInstance().excuteTask(new Runnable() {
                public void run() {
                    try {
                        btsocket = btdevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        handlerMsg("请稍候，正在连接服务器:" + BluetoothMsg.BlueToothAddress, -1);
                        btsocket.connect();
                        handlerMsg("已经连接上服务端！可以发送信息。", -1);
                        receiverMessageTask();
                        sendTask();
                    } catch (IOException e) {
                        e.printStackTrace();
                        handlerMsg("连接服务端异常！请检查服务器是否正常，断开连接重新试一试。", -1);
                    }

                }
            });
        }
    }

    private void receiverMessageTask(){
        ThreadPool.getInstance().excuteTask(new Runnable() {
            public void run() {
                byte[] buffer = new byte[2048];
                int totalRead;
	            /*InputStream input = null;
	            OutputStream output=null;*/
                try {
                    bis=new BufferedInputStream(btsocket.getInputStream());
                    bos=new BufferedOutputStream(btsocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String txt;
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
                    }
                } catch(EOFException e){
                    e.printStackTrace();
                    handlerMsg("server has close!", -1);
                }catch (IOException e) {
                    e.printStackTrace();
                    handlerMsg("receiver message error! make sure server is ok,and try again connect!", -1);
                }
            }
        });
    }

    public void sendTask(){
        ThreadPool.getInstance().excuteTask(new Runnable() {
            @Override
            public void run() {
                while (isConnected()){
                    try {
                        synchronized (this){
                            wait(5000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public boolean sendMsg(String msg){
        boolean result=false;
        if(null==btsocket||bos==null){
            handlerMsg(Const.ClientSendFail,-1);
            return false;
        }
        try {
            bos.write(msg.getBytes());
            bos.flush();
            result=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) handlerMsg(msg,-1);
        else{
            handlerMsg(Const.ClientSendFail,-1);
            handlerMsg(Const.Disconnected,Const.arg_error);
        }
        return result;
    }

    public void closeBTClient(){
        try{
            if(bis!=null)
                bis.close();
            if(bos!=null)
                bos.close();
            if(btsocket!=null)
                btsocket.close();
            bis=null;
            bos=null;
            btsocket=null;
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return sendMsg(Const.TestConnect);
    }

    public void handlerMsg(Object obj, int arg1){
        Message msg = new Message();
        if (Pattern.compile(Const.isInfoPattern).matcher(obj.toString()).find()) msg.what = Const.SelectInfo;
        else msg.what = Const.SelectClient;
        msg.obj = obj;
        msg.arg1 = arg1;
        detectedHandler.sendMessage(msg);
    }
}
