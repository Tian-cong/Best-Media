package com.tiancong.bestwish.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tiancong.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
