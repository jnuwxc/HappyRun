package com.example.happyrun;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public final static String BASEURL = "http://121.4.20.8/happyRun/";
    public final static long SERVICE_ID = 226786L;

    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 并修改manifests中的android:name属性为该类。
        SDKInitializer.initialize(this);
        context = getApplicationContext();
    }
}
