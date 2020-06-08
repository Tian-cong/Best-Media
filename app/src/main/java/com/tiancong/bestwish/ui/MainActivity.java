package com.tiancong.bestwish.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.tiancong.base.BaseActivity;
import com.tiancong.bestwish.R;
import com.tiancong.bestwish.databinding.ActivityMainBinding;
import com.tiancong.bestwish.utils.AudioManager;
import com.tiancong.bestwish.utils.AudioPlayer;
import com.tiancong.bestwish.utils.AudioRecorder;
import com.tiancong.bestwish.utils.LogHelper;
import com.tiancong.bestwish.utils.RecorderUtil;
import com.tiancong.bestwish.view.SpeechRadarView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    ActivityMainBinding mainBinding;

    private long time = 0;
    android.media.AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        requestPermissions();

        mainBinding.bt5.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,ListActivity.class));
        });

         am = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);


        mainBinding.SpeechRadarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time = System.currentTimeMillis();
                        LogHelper.d(time);
                        AudioRecorder.getInstance().startRecorder();
                        AudioRecorder.getInstance().setOnVolumeChangeListener(per -> {
                            mainBinding.soundView.setPerHeight(per);
                        });
                        mainBinding.SpeechRadarView.startPlay(SpeechRadarView.STATE_LISTENING);
                        return true;
                    case MotionEvent.ACTION_UP:

                        if (System.currentTimeMillis() - time < 1000) {

                            Toast.makeText(getApplicationContext(),"你太短了",Toast.LENGTH_SHORT).show();
                            mainBinding.SpeechRadarView.stopPlay();
                            return true;
                        }
                        mainBinding.SpeechRadarView.stopPlay();
                        AudioRecorder.getInstance().stopRecorder();
                        return true;
                    default:
                        return true;
                }
            }
        });

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
        AudioRecorder.getInstance().startRecorder();
    }

    public void bt2(View view) {
        AudioRecorder.getInstance().stopRecorder();
    }

    public void bt3(View view) {
        AudioRecorder.getInstance().playRecorderSounds();
        AudioPlayer.getInstance().getVolume(per -> {
            mainBinding.soundView.setPerHeight(per);
        });
    }

    public void bt4(View view) {
        AudioPlayer.getInstance().play("/storage/emulated/0/AAA/back.aac");
    }
}
