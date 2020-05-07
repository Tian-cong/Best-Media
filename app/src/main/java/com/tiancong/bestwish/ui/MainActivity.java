package com.tiancong.bestwish.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.fmod.FMOD;

import com.tiancong.bestwish.R;

import io.microshow.aisound.AiSound;
import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static String inputPath = "/storage/emulated/0/Music/test.mp3";
    String path = "/storage/emulated/0/Download/111.mp3";
    private static String outPath = "/storage/emulated/0/Download/111.mp3";

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!FMOD.checkInit()) {
            FMOD.init(this);
        }

        runFFmpegRxJava();
        
    }

    private void runFFmpegRxJava() {

        String[] commands = getBoxblur();

        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(this);

        //开始执行FFmpeg命令
        RxFFmpegInvoke.getInstance()
                .runCommandRxJava(commands)
                .subscribe(myRxFFmpegSubscriber);

    }

    public static String[] getBoxblur() {
        RxFFmpegCommandList cmdlist = new RxFFmpegCommandList();
        cmdlist.append("-i");
        cmdlist.append(inputPath);
        cmdlist.append("-vf");
        cmdlist.append("reverse");
        cmdlist.append("-af");
        cmdlist.append("areverse");
        cmdlist.append("-preset");
        cmdlist.append("superfast");
        cmdlist.append(outPath);
        return cmdlist.build();
    }

    public static class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        private Context context;

        MyRxFFmpegSubscriber(Context context) {
            this.context = context;
        }

        @Override
        public void onFinish() {

            Log.d(TAG, "onFinish: 66666666666");

        }

        @Override
        public void onProgress(int progress, long progressTime) {
            Log.d(TAG, "onProgress: " + progress);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(String message) {
            Log.d(TAG, "onError: " + message);
        }
    }


    public void clickFix(View view) {
        if (view.getId() == R.id.btn_normal) {
            playSound(path, AiSound.TYPE_NORMAL);

        } else if (view.getId() == R.id.btn_luoli) {
            playSound(path, AiSound.TYPE_LOLITA);

        } else if (view.getId() == R.id.btn_dashu) {
            playSound(path, AiSound.TYPE_UNCLE);

        } else if (view.getId() == R.id.btn_jingsong) {
            playSound(path, AiSound.TYPE_THRILLER);

        } else if (view.getId() == R.id.btn_gaoguai) {
            playSound(path, AiSound.TYPE_FUNNY);

        } else if (view.getId() == R.id.btn_kongling) {
            playSound(path, AiSound.TYPE_ETHEREAL);

        } else if (view.getId() == R.id.btn_gaoguai2) {
            playSound(path, AiSound.TYPE_CHORUS);

        } else if (view.getId() == R.id.btn_kongling2) {
            playSound(path, AiSound.TYPE_TREMOLO);
        }
    }

    public void playSound(final String path, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AiSound.playSound(path, type);
            }
        }).start();
    }

    //保存音频 注意sd开权限
    public void saveSound(final String path, int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = AiSound.saveSound(path, "/storage/emulated/0/1/voice666.wav", AiSound.TYPE_LOLITA);
            }
        }).start();
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
