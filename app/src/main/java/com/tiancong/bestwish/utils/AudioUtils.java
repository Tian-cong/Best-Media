package com.tiancong.bestwish.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.tiancong.bestwish.R;

import io.microshow.aisound.AiSound;
import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class AudioUtils {

    private static final String TAG = "AudioUtils";


    public static void playSound(final String path, final int type) {
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


    public static void runFFmpegRxJava(Activity activity,String inputPath,String outPath) {

        String[] commands = getBoxblur(inputPath,outPath);

        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(activity);

        //开始执行FFmpeg命令
        RxFFmpegInvoke.getInstance()
                .runCommandRxJava(commands)
                .subscribe(myRxFFmpegSubscriber);

    }

    private static String[] getBoxblur(String inputPath,String outPath) {
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

        private ProgressBar progressBar;

        MyRxFFmpegSubscriber(Activity activity) {
            progressBar = (ProgressBar)activity.findViewById(R.id.progress_main);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish: ");
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            Log.d(TAG, "onProgress: " + progress);

            progressBar.setProgress(progress);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(String message) {
            Log.d(TAG, "onError: " + message);
        }
    }
}
