package com.tiancong.bestwish.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.fmod.FMOD;

import com.tiancong.bestwish.R;
import com.tiancong.bestwish.utils.AudioUtils;

import io.microshow.aisound.AiSound;
import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static String inputPath = "/storage/emulated/0/Music/111.mp4";
    String path = "/storage/emulated/0/Download/111.mp3";
    private static String outPath = "/storage/emulated/0/Download/33.mp4";

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!FMOD.checkInit()) {
            FMOD.init(this);
        }

        AudioUtils.runFFmpegRxJava(this,inputPath,outPath);
        
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
}
