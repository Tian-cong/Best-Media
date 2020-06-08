package com.tiancong.base;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {

    protected Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
    }
}
