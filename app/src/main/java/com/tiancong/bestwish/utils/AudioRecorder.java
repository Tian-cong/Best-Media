package com.tiancong.bestwish.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecorder {

    private MediaRecorder recorder;
    private static String mFilePath = "";
    private boolean isRecording;

    private Timer mTimer;
    private final Handler mHandler;

    private static class AudioRecorderHolder {
        private static AudioRecorder instance = new AudioRecorder();
    }

    public static AudioRecorder getInstance() {
        return AudioRecorderHolder.instance;
    }

    private AudioRecorder() {
        mTimer = new Timer();
        mHandler = new Handler();
    }

    private static void setFileNameAndPath() {
        String mFileName = "voice"
                + "_" + (DateFormat.format("MMdd_HHmmss", Calendar.getInstance(Locale.CHINA))) + ".aac";
        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AAA/";
        File file = new File(mFilePath);
        if (!file.exists()) file.mkdirs();
        mFilePath += mFileName;
    }

    public void startRecorder() {
        setFileNameAndPath();

        if (isRecording) {
            recorder.release();
            recorder = null;
        }

        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioSamplingRate(16000);
            recorder.setAudioEncodingBitRate(256000);
            recorder.setAudioChannels(2);
            recorder.setOutputFile(mFilePath);
            try {
                recorder.prepare();
                recorder.start();
                isRecording = true;

                cbkVolume();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cbkVolume() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRecording) {
                    float per;
                    try {
                        //获取音量大小
                        per = recorder.getMaxAmplitude() / 32767f;//最大32767
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        per = (float) Math.random();
                    }
                    if (mOnVolumeChangeListener != null) {
                        float finalPer = per;
                        mHandler.post(() -> {
                            mOnVolumeChangeListener.volumeChange(finalPer);
                            LogHelper.d("recorder_volumeChange: "+finalPer);
                        });
                    }

                }
            }
        },0,1000);
    }

    public void stopRecorder() {
        if (recorder == null) {
            return;
        }
        recorder.stop();
        // recorder.reset();
        recorder.release();
        recorder = null;
        isRecording = false;

        AudioPlayer.getInstance().runFFmpegRxJava(mFilePath);
    }

    public void playRecorderSounds() {
        AudioPlayer.getInstance().play(mFilePath);
    }

    public interface OnVolumeChangeListener {
        void volumeChange(float per);
    }

    private OnVolumeChangeListener mOnVolumeChangeListener;

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
    }
}
