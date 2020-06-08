package com.tiancong.bestwish.app;

import android.app.Application;

import com.tiancong.base.BaseActivity;
import com.tiancong.base.BaseApplication;
import com.tiancong.bestwish.BuildConfig;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

public class MyApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        RxFFmpegInvoke.getInstance().setDebug(BuildConfig.DEBUG);
    }
}
