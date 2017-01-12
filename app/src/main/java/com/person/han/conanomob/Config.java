package com.person.han.conanomob;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Created by han on 2016/9/25.
 */

public class Config {
    private static String APP_ID = "com.person.han.conanomob";
    public static String CLIENT_OR_SERVER="client_or_server";

    public static String getCachedDATA(Context context, String key) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getString(key, null);
    }


    public static void cacheDATA(Context context, String data, String key) {
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        e.putString(key, data);
        e.apply();
    }

    public static Boolean isCachedDATA(Context context, String key) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).contains(key);
    }

    public static void removeCachedData(Context context, String key) {
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        e.remove(key);
        e.apply();
    }
}

