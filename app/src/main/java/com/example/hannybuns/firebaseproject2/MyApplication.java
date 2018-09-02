package com.example.hannybuns.firebaseproject2;

import android.app.Application;
import android.content.Context;
/**
 * Created by HannyBuns on 8/25/2018.
 */

public class MyApplication extends Application{
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
