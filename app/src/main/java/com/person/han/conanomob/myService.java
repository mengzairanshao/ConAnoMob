package com.person.han.conanomob;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.person.han.conanomob.connection.BTClient;
import com.person.han.conanomob.connection.BTManage;
import com.person.han.conanomob.connection.BTServer;
import com.person.han.conanomob.Const.BluetoothMsg;
import com.person.han.conanomob.Const.Const;
import com.person.han.conanomob.Msg.DistributeData;
import com.person.han.conanomob.Utility.Exc;

import java.util.regex.Pattern;

/**
 * Created by han on 2016/12/9.
 */

public class myService extends Service {

    private String TAG = "myService";
    public Boolean isAutoBreak = true;
    private BTClient client;
    private BTServer server;
    private Context mContext;
    private myApplication application;
    private DistributeData distributeData=new DistributeData();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        application = (myApplication) getApplication();
        Exc.common_exc(Exc.GET_CPU_INFO, mContext);
        BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.SERVICE;
        initConnecter();
        register();
        application.setDistributeData(distributeData);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeServerAndClient();
        unregister();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new myBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    public class myBinder extends Binder {
        public myService getService() {
            return myService.this;
        }
    }

    private Handler detectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleMsg(msg);
        }
    };

    public void initConnecter() {
        if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.CLIENT) {
            String address = BluetoothMsg.BlueToothAddress;
            if (!TextUtils.isEmpty(address)) {
                if (null == client)
                    client = new BTClient(BTManage.getInstance().getBtAdapter(), detectedHandler);
                client.connectBTServer(address);
                BluetoothMsg.isOpen = true;
                application.setClient(client);
            } else {
                Toast.makeText(getApplicationContext(), "address is empty please choose server address !",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.SERVICE) {
            if (null == server)
                server = new BTServer(BTManage.getInstance().getBtAdapter(), detectedHandler);
            server.startBTServer();
            BluetoothMsg.isOpen = true;
            application.setServer(server);
        }
    }

    public void closeServerAndClient() {
        if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.CLIENT) {
            if (null == client)
                return;
            client.closeBTClient();
        } else if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.SERVICE) {
            if (null == server)
                return;
            server.closeBTServer();
        }
        BluetoothMsg.isOpen = false;
        BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.NONE;
        client = null;
        server = null;
        Toast.makeText(getApplicationContext(), "已断开连接！", Toast.LENGTH_SHORT).show();
    }

    public void sendMsg(String msgText, int whichWorking) {
        if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.CLIENT && (whichWorking == Const.SelectClient || whichWorking == Const.SelectServerAndClient)) {
            if (!isClientOK()) return;
            client.sendMsg(msgText);
        } else if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.SERVICE && (whichWorking == Const.SelectServer || whichWorking == Const.SelectServerAndClient || whichWorking == Const.SelectInfo)) {
            if (!isServerOK()) return;
            server.sendmsg(msgText);
        }
    }

    public void register() {
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void unregister() {
        unregisterReceiver(batteryReceiver);
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            sendMsg(Const.Info + Const.batteryLevel + level + "%", Const.SelectInfo);
        }
    };

    public void changeMode(int checkedId) {
        switch (checkedId) {
            case Const.SelectNone:
                isAutoBreak = false;
                closeServerAndClient();
                BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.NONE;
                isAutoBreak = true;
                break;
            case Const.SelectClient:
                if (isClientOK()) return;
                isAutoBreak = false;
                closeServerAndClient();
                BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.CLIENT;
                handlerMsg(Const.InnerCommand, Const.SelectServer, Const.arg_startSearchActivity);
                isAutoBreak = true;
                break;
            case Const.SelectServer:
                if (isServerOK()) return;
                isAutoBreak = false;
                closeServerAndClient();
                BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.SERVICE;
                initConnecter();
                isAutoBreak = true;
                break;
        }
    }

    public Boolean isClientOK() {
        return null != client && client.isConnected();
    }

    public Boolean isServerOK() {
        return null != server && server.isConnected();
    }

    public void handleMsg(Message msg) {
        if (msg.what == Const.SelectClient){
            switch (msg.arg1){
                case Const.arg_error:
                    closeServerAndClient();
                    distributeData.setClientInfo(msg.obj.toString());
                    break;
                default:
                    distributeData.setClientInfo(msg.obj.toString());
                    break;
            }
        }else if (msg.what == Const.SelectServer){
            Exc.common_exc(msg.obj.toString(), mContext);
            switch (msg.arg1){
                case Const.arg_startSearchActivity:
                    distributeData.startActivityForResult();
                    break;
                case Const.arg_CloseClientAndServerAndRebootAsServer:
                    closeServerAndClient();
                    if (isAutoBreak) {
                        BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.SERVICE;
                        initConnecter();
                    } else isAutoBreak = true;
                    distributeData.setServerInfo(msg.obj.toString());
                    break;
                default:
                    distributeData.setServerInfo(msg.obj.toString());
                    break;
            }
        }else if (msg.what == Const.SelectInfo && BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.CLIENT) {
            String temp = Pattern.compile(Const.isInfoPattern).matcher(msg.obj.toString()).replaceAll("");
            String[] result = temp.split(Const.regex);
            switch (Integer.parseInt(result[0])) {
                case 1:
                    distributeData.setSysInfo(Const.SYS_BATTERY_INFO,result[1]);
                    break;
                default:
                    break;
            }
        }
    }

    public void handlerMsg(Object obj,int what, int arg1){
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        detectedHandler.sendMessage(msg);
    }
}
