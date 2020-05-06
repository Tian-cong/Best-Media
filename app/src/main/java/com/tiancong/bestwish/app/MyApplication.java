package com.tiancong.bestwish.app;

import android.app.Application;
import android.os.Debug;

import com.tiancong.bestwish.BuildConfig;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.reactivex.android.plugins.RxAndroidPlugins;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxFFmpegInvoke.getInstance().setDebug(BuildConfig.DEBUG);
    }
}
