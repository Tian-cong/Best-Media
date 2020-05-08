package com.tiancong.bestwish.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import org.fmod.FMOD;

import com.tiancong.bestwish.R;
import com.tiancong.bestwish.utils.AudioUtils;

import io.microshow.aisound.AiSound;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        if (!FMOD.checkInit()) {
            FMOD.init(this);
        }

        //AudioUtils.runFFmpegRxJava(this,inputPath,outPath);

        try {
            AudioUtils.startRecord();
            Thread.sleep(4000);
            AudioUtils.stopRecord();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        AiSound.pauseSound();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AiSound.resumeSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FMOD.checkInit()) {
            FMOD.close();
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO };
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
}
