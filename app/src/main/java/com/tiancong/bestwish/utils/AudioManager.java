package com.tiancong.bestwish.utils;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.tiancong.bestwish.R;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import io.microshow.aisound.AiSound;
import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class AudioManager {

    private static final String TAG = "AudioUtils";

    private static String mFilePath = "";
    private static MediaRecorder recorder;
    private static MediaPlayer mediaPlayer;

    private static AudioManager mInStance;

    private AudioManager(final Activity activity) {

    }

    public static AudioManager getInStance(Activity activity) {
        if (mInStance == null) {
            synchronized (AudioManager.class) {
                if(mInStance == null) {
                    mInStance = new AudioManager(activity);
                    recorder = new MediaRecorder();
                    mediaPlayer = new MediaPlayer();
                }
            }
        }
        return mInStance;
    }



    public void startRecord() {
        Log.d(TAG, "startRecord: ");

        setFileNameAndPath();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(mFilePath);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        Log.d(TAG, "stopRecord: " + mFilePath);
        if (recorder == null) {
            return;
        }
        recorder.stop();
       // recorder.reset();
       // recorder.release();
    }

    public  void playSound() {
        Log.d(TAG, "playSound: " + mFilePath);

        try {
            mediaPlayer.setDataSource(mFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Thread.sleep((long) mediaPlayer.getDuration());
            mediaPlayer.reset();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void setFileNameAndPath() {
        String mFileName = "voice"
                + "_" + (DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA))) + ".aac";
        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AAA/";
        File file = new File(mFilePath);
        if (!file.exists()) file.mkdirs();
        mFilePath += mFileName;
    }




    public static void playSound(final String path, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AiSound.playSound(path, type);
            }
        }).start();
    }

    //保存音频 注意sd开权限
    public static void saveSound(final String path, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = AiSound.saveSound(path, "/storage/emulated/0/AAA/voice666.aac", type);
            }
        }).start();
    }


    public void runFFmpegRxJava(Activity activity) {

        String[] commands = getBoxblur(mFilePath, "/storage/emulated/0/AAA/back.aac");

        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(activity);

        //开始执行FFmpeg命令
        RxFFmpegInvoke.getInstance()
                .runCommandRxJava(commands)
                .subscribe(myRxFFmpegSubscriber);

    }

    private static String[] getBoxblur(String inputPath, String outPath) {
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
            progressBar = (ProgressBar) activity.findViewById(R.id.progress_main);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish: ");
            progressBar.setVisibility(View.GONE);
            try {
                mediaPlayer.setDataSource("/storage/emulated/0/AAA/back.aac");
                mediaPlayer.prepare();
                mediaPlayer.start();
                Thread.sleep((long) mediaPlayer.getDuration());
                mediaPlayer.reset();
            } catch (IOException | InterruptedException e) {
                Log.e(TAG, "onFinish: ", e);
                e.printStackTrace();
            }
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
