package com.person.han.conanomob.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.person.han.conanomob.Const.BluetoothMsg;
import com.person.han.conanomob.Config;
import com.person.han.conanomob.Const.Const;
import com.person.han.conanomob.Msg.DistributeData;
import com.person.han.conanomob.R;
import com.person.han.conanomob.myApplication;
import com.person.han.conanomob.myService;

public class BTChatActivity extends Activity {
    private String TAG = "BTChatActivity";

    private ListView mListView;
    private Button sendButton;
    private Button disconnectButton;
    private EditText editMsgView;
    private ArrayAdapter<String> mAdapter;
    private List<String> msgList = new ArrayList<>();
    private Spinner modeSelect;
    private Boolean isAutoSelect = false;

    private Context mContext;
    private myApplication application;
    public myService mService;
    private DistributeData distributeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_chat);
        mContext = this;
        application = (myApplication) getApplication();
        distributeData = application.getDistributeData();
        distributeData.setCallback(new DistributeData.CallBack() {
            @Override
            public void onDataChanged(int dataId, String data, int arg1, int arg2) {
                handleMsg(dataId, data, arg1, arg2);
            }
        });
        mService = application.getmService();
        initView();
    }

    private void initView() {

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        editMsgView = (EditText) findViewById(R.id.MessageText);
        editMsgView.clearFocus();
        sendButton = (Button) findViewById(R.id.btn_msg_send);
        disconnectButton = (Button) findViewById(R.id.btn_disconnect);
        modeSelect = (Spinner) findViewById(R.id.spinner);
        if (Config.isCachedDATA(mContext, Config.CLIENT_OR_SERVER)) {
            int checkId = Config.getCachedDATA(mContext, Config.CLIENT_OR_SERVER).equals(Const.SelectServer + "") ? 1 :
                    (Config.getCachedDATA(mContext, Config.CLIENT_OR_SERVER).equals(Const.SelectClient + "") ? 0 : 2);
            isAutoSelect = true;
            modeSelect.setSelection(checkId);
        } else {
            BluetoothMsg.serviceOrClient = BluetoothMsg.ServerOrClient.SERVICE;
        }
        modeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mService.changeMode(position);
                switch (position) {
                    case Const.SelectNone:
                        Config.cacheDATA(mContext, Const.SelectNone + "", Config.CLIENT_OR_SERVER);
                        break;
                    case Const.SelectClient:
                        Config.cacheDATA(mContext, Const.SelectClient + "", Config.CLIENT_OR_SERVER);
                        break;
                    case Const.SelectServer:
                        Config.cacheDATA(mContext, Const.SelectServer + "", Config.CLIENT_OR_SERVER);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String msgText = editMsgView.getText().toString();
                if (msgText.length() > 0) {
                    mService.sendMsg(msgText, Const.SelectServerAndClient);
                    editMsgView.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "发送内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        disconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mService.closeServerAndClient();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (BluetoothMsg.isOpen) {
            Toast.makeText(getApplicationContext(), "连接已经打开，可以通信。如果要再建立连接，请先断开！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        distributeData.setCallback(null);
        distributeData = null;
        mService = null;
        mListView = null;
        sendButton = null;
        disconnectButton = null;
        editMsgView = null;
        mAdapter = null;
        msgList = null;
        mContext = null;
        application = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            //从设备列表返回
            mService.initConnecter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void handleMsg(int dataId, String data, int arg1, int arg2) {
        if (dataId == Const.SERVER_INFO || dataId == Const.CLIENT_INFO) {
            if (data.equals(Const.TestConnect)) return;
            msgList.add(data);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(msgList.size() - 1);
        }
        if (dataId == Const.SERVER_INNER_INFO) {
            Intent it = new Intent(getApplicationContext(), BTDeviceActivity.class);
            startActivityForResult(it, 100);
        }
    }
}
