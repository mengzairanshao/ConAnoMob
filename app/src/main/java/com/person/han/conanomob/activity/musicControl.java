package com.person.han.conanomob.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.person.han.conanomob.connection.BTClient;
import com.person.han.conanomob.connection.BTServer;
import com.person.han.conanomob.Const.BluetoothMsg;
import com.person.han.conanomob.Utility.Exc;
import com.person.han.conanomob.R;
import com.person.han.conanomob.myApplication;

public class musicControl extends AppCompatActivity implements View.OnClickListener{
    private Context mContext;
    private BTClient client;
    private BTServer server;
    private ImageButton next;
    private ImageButton pause;
    private ImageButton previous;
    private Button startMusic;
    private Button btn_lower;
    private Button btn_raise;
    private myApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_control);
        application= (myApplication) getApplication();
        mContext=this;
        if (application.getmService().isServerOK()||application.getmService().isClientOK()){
            if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.CLIENT){
                client=application.getClient();
            }
            if (BluetoothMsg.serviceOrClient == BluetoothMsg.ServerOrClient.SERVICE) {
                server = application.getServer();
            }
        }else {
            Toast.makeText(mContext,"请先连接",Toast.LENGTH_SHORT).show();
        }
        next= (ImageButton) findViewById(R.id.next);
        pause= (ImageButton) findViewById(R.id.pause);
        previous= (ImageButton) findViewById(R.id.previous);
        startMusic= (Button) findViewById(R.id.btnStartMusic);
        btn_lower= (Button) findViewById(R.id.btnLower);
        btn_raise= (Button) findViewById(R.id.btnRaise);
        previous.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        startMusic.setOnClickListener(this);
        btn_lower.setOnClickListener(this);
        btn_raise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous:{
                sendCmd(Exc.MUSIC_PREVIOUS);
                break;
            }
            case R.id.pause:{
                sendCmd(Exc.MUSIC_TOGGLEPAUSE);
                break;
            }
            case R.id.next:{
                sendCmd(Exc.MUSIC_NEXT);
                break;
            }
            case R.id.btnStartMusic:{
                sendCmd(Exc.START_MUSIC);
                break;
            }
            case R.id.btnLower:{
                sendCmd(Exc.ADJUST_LOWER);
                break;
            }
            case R.id.btnRaise:{
                sendCmd(Exc.ADJUST_RAISE);
                break;
            }
            default:break;
        }
    }

    public boolean sendCmd(String cmd){
        Boolean result=false;
        if (client!=null){
            result=client.sendMsg(cmd);
        }else if (server!=null){
            result=server.sendmsg(cmd);
        }
        return result;
    }
}
