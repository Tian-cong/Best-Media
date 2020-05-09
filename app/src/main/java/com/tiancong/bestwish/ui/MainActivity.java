package com.tiancong.bestwish.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.tiancong.bestwish.R;
import com.tiancong.bestwish.utils.AudioManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    public void bt1(View view) {
        AudioManager.getInStance(this).startRecord();
    }

    public void bt2(View view) {
        AudioManager.getInStance(this).stopRecord();
    }

    public void bt3(View view) {
        AudioManager.getInStance(this).playSound();
    }

    public void bt4(View view) {
        AudioManager.getInStance(this).runFFmpegRxJava(this);
    }
}
