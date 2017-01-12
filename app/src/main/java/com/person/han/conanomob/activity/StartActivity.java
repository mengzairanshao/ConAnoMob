package com.person.han.conanomob.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.person.han.conanomob.Const.BluetoothMsg;
import com.person.han.conanomob.Const.Const;
import com.person.han.conanomob.Msg.DistributeData;
import com.person.han.conanomob.R;
import com.person.han.conanomob.myApplication;
import com.person.han.conanomob.myService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StartActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private String TAG="StartActivity";
    private GridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.setting,R.drawable.bluetooth,R.drawable.log,R.drawable.music};
    private String[] iconName = {"设置","蓝牙Chat","日志","音乐"};
    private Context mContext;
    private TextView batteryInfo;
    private myApplication application;
    private DistributeData distributeData;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService.myBinder binder = (myService.myBinder) service;
            myService myservice = binder.getService();
            application.setmService(myservice);
            distributeData=application.getDistributeData();
            distributeData.setCallback(new DistributeData.CallBack() {
                @Override
                public void onDataChanged(int dataId, String data, int arg1, int arg2) {
                    handleMsg(dataId,data,arg1,arg2);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext=this;
        application= (myApplication) getApplication();
        Intent intent = new Intent(this, myService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        batteryInfo= (TextView) findViewById(R.id.battery);
        gview = (GridView) findViewById(R.id.grid);
        //新建List
        data_list = new ArrayList<>();
        //获取数据
        data_list=getData();
        //新建适配器
        String [] from ={"image","text"};
        int [] to = {R.id.img,R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.grid_item, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);

        gview.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
        switch (item.get("text").toString()){
            case "设置":
                startActivity(new Intent(this,BTChatActivity.class));
                break;
            case "日志":
                break;
            case "音乐":
                Intent intent = new Intent(mContext, musicControl.class);
                startActivity(intent);
                break;
            case "蓝牙Chat":
                startActivity(new Intent(this,BTChatActivity.class));
                break;
            default:break;
        }
    }

    public void handleMsg(int dataId, String data,  int arg1,int arg2) {
            switch (dataId) {
                case Const.SYS_BATTERY_INFO:
                    batteryInfo.setText(data);
                    break;
                default:
                    break;
            }
        }
}
