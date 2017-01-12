package com.person.han.conanomob;

import android.app.Application;

import com.person.han.conanomob.Msg.DistributeData;
import com.person.han.conanomob.connection.BTClient;
import com.person.han.conanomob.connection.BTServer;


/**
 * Created by han on 2016/12/8.
 */

public class myApplication extends Application {
    private BTClient client=null;
    private BTServer server=null;
    private myService mService=null;

    private DistributeData distributeData;

    public DistributeData getDistributeData() {
        return distributeData;
    }

    public void setDistributeData(DistributeData distributeData) {
        this.distributeData = distributeData;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public BTClient getClient() {
        return client;
    }

    public void setClient(BTClient client) {
        this.client = client;
    }

    public BTServer getServer() {
        return server;
    }

    public void setServer(BTServer server) {
        this.server = server;
    }

    public myService getmService() {
        return mService;
    }

    public void setmService(myService mService) {
        this.mService = mService;
    }
}
