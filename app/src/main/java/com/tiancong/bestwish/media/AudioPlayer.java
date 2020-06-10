package com.tiancong.bestwish.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Handler;

import com.tiancong.bestwish.utils.LogHelper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import io.reactivex.schedulers.Schedulers;

import static com.tiancong.bestwish.utils.FileUtils.setBackPath;

public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private boolean isCompletion = false;
    private boolean isPrepared = false;
    private AudioManager am;

    private Timer mTimer;
    private final Handler mHandler;
    private String outPath;

    private static class AudioPlayerHolder {
        private static AudioPlayer instance = new AudioPlayer();
    }

    public static AudioPlayer getInstance() {
        return AudioPlayerHolder.instance;
    }

    private AudioPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        }
        mTimer = new Timer();
        mHandler = new Handler();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        mediaPlayer.start();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void play(String path) {
        LogHelper.d("start--------------------------");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();

            cbkVolume();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playBack(String path) {

        outPath = setBackPath();
        runFFmpegRxJava(path,outPath);



    }

    public void replay() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        LogHelper.d("onPrepared");
        isPrepared = true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogHelper.d("onCompletion");
        isCompletion = true;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isCompletion() {
        return isCompletion;
    }

    public void getVolume(Context context, AudioPlayer.OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
//        if (am == null) {
//            am = (android.media.AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        }
    }

    public void getVolume(AudioPlayer.OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
    }

    private void cbkVolume() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying()) {
                    float per;
                    try {
                        //获取音量大小
                        per = (float) Math.random();
                       // per = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        per = (float) Math.random();
                    }
                    if (mOnVolumeChangeListener != null) {
                        float finalPer = per;
                        mHandler.post(() -> {
                            mOnVolumeChangeListener.volumeChange(finalPer);
                            LogHelper.d("player_volumeChange: " + finalPer);
                        });
                    }

                }
            }
        }, 0, 1000);
    }

    public interface OnVolumeChangeListener {
        void volumeChange(float per);
    }

    private AudioPlayer.OnVolumeChangeListener mOnVolumeChangeListener;

    public void setOnVolumeChangeListener(AudioPlayer.OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
    }


    public void runFFmpegRxJava(String FilePath) {

        String[] commands = getBoxblur(FilePath, "/storage/emulated/0/Music/back.aac");

        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();

        //开始执行FFmpeg命令
        RxFFmpegInvoke.getInstance()
                .runCommandRxJava(commands)
                .subscribe(myRxFFmpegSubscriber);

    }

    public void runFFmpegRxJava(String FilePath,String outPath) {

        String[] commands = getBoxblur(FilePath, outPath);

        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();

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

    private class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        MyRxFFmpegSubscriber() {

        }

        @Override
        public void onFinish() {
            play(outPath);
        }

        @Override
        public void onProgress(int progress, long progressTime) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(String message) {

        }
    }


}
