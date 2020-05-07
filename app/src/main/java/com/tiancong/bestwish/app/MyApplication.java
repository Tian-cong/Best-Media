package com.tiancong.bestwish.app;

import android.app.Application;

import com.tiancong.bestwish.BuildConfig;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxFFmpegInvoke.getInstance().setDebug(BuildConfig.DEBUG);
    }
}
