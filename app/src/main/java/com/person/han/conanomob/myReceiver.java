package com.person.han.conanomob;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by han on 2016/12/9.
 */

public class myReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "android.net.conn.CONNECTIVITY_CHANGE":
                break;
            case "android.intent.action.BOOT_COMPLETED":
                intent.setClass(context,myService.class);
                context.startService(intent);
            default:break;
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
