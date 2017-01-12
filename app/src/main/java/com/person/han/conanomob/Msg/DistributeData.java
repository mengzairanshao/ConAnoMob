package com.person.han.conanomob.Msg;

import com.person.han.conanomob.Const.Const;

/**
 * Created by han on 2016/12/10.
 */

public class DistributeData {
    private CallBack callback;

    private String batteryInfo;
    private String serverInfo;
    private String clientInfo;
    private String serverInnerInfo;

    public void setSysInfo(int dataId,String sysInfo) {
        switch (dataId){
            case Const.SYS_BATTERY_INFO:
                this.batteryInfo = sysInfo;
                notifyDataChanged(Const.SYS_BATTERY_INFO,batteryInfo,-1,-1);
                break;
            default:
                break;
        }

    }

    public String getSysInfo(int dataId) {
        switch (dataId){
            case Const.SYS_BATTERY_INFO:
                return batteryInfo;
            default:
                return null;
        }
    }

    private void notifyDataChanged(int dataId,String data,int arg1, int arg2) {
        if (callback != null) {
            callback.onDataChanged(dataId,data,arg1,arg2);
        }
    }
    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
        notifyDataChanged(Const.SERVER_INFO,serverInfo,-1,-1);
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
        notifyDataChanged(Const.CLIENT_INFO,clientInfo,-1,-1);
    }

    public void setServerInnerInfo(String serverInnerInfo) {
        this.serverInnerInfo = serverInnerInfo;
        notifyDataChanged(Const.SERVER_INNER_INFO,serverInnerInfo,-1,-1);
    }

    public void startActivityForResult() {
        notifyDataChanged(Const.SERVER_INNER_INFO,serverInnerInfo,-1,-1);
    }

    public interface CallBack {
        void onDataChanged(int dataId,String data,int arg1, int arg2);
    }
}
