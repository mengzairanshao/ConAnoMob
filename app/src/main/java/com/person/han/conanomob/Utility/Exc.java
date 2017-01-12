package com.person.han.conanomob.Utility;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;


import com.person.han.conanomob.Const.Const;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Pattern;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by han on 2016/12/7.
 */

public class Exc {
    private static String TAG="Exc";
    public static final String START_MUSIC = "start_music";
    public static final String MUSIC_NEXT = "next";
    public static final String ADJUST_LOWER = "adjust_lower";
    public static final String ADJUST_RAISE = "adjust_raise";
    private static final String SERVICECMD = "com.miui.player.musicservicecommand";
    private static final String MUSIC_NAME = "command";
    public static final String MUSIC_TOGGLEPAUSE = "togglepause";
    public static final String MUSIC_STOP = "stop";
    public static final String MUSIC_PAUSE = "pause";
    public static final String MUSIC_PREVIOUS = "previous";
    public static final String GET_CPU_INFO = "cat /proc/cpuinfo";


    public static int common_exc(String cmd, Context mContext) {
        Intent intent = new Intent(SERVICECMD);
        AudioManager audioManager= (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        switch (cmd) {
            case MUSIC_STOP:
                intent.putExtra(MUSIC_NAME, MUSIC_STOP);
                break;
            case MUSIC_PREVIOUS:
                intent.putExtra(MUSIC_NAME, MUSIC_PREVIOUS);
                break;
            case MUSIC_TOGGLEPAUSE:
                intent.putExtra(MUSIC_NAME, MUSIC_TOGGLEPAUSE);
                break;
            case MUSIC_NEXT:
                intent.putExtra(MUSIC_NAME, MUSIC_NEXT);
                break;
            case START_MUSIC:
                intent = new Intent("android.intent.action.MUSIC_PLAYER");
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            case ADJUST_LOWER:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                break;
            case ADJUST_RAISE:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                break;
            default:
                break;
        }
        mContext.sendBroadcast(intent);
        return 1;
    }

    public static int common_exc(String cmd) {
        switch (cmd) {
            case GET_CPU_INFO:
                Process pp = null;
                try {
                    pp = Runtime.getRuntime().exec(GET_CPU_INFO);
                    InputStreamReader ir=new InputStreamReader(pp.getInputStream());
                    LineNumberReader input = new LineNumberReader(ir);
                    String str="";
                    int cpuInfo=0;
                    for (; null != str;)
                    {
                        str = input.readLine();
                        if (str != null)
                        {
                            str = str.trim();
                            if (str.contains("Hardware")){
                                if (Pattern.compile("MT\\d+").matcher(str).find()){
                                    cpuInfo= Const.MTK;
                                }else if (str.contains("Qualcomm")){
                                    cpuInfo= Const.Qualcomm;
                                }
                            }
                        }
                    }
                    return cpuInfo;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return 1;
    }


}
