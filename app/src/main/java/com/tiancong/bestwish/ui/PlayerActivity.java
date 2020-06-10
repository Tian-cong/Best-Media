package com.tiancong.bestwish.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.tiancong.base.BaseActivity;
import com.tiancong.bestwish.R;
import com.tiancong.bestwish.databinding.ActivityMain2Binding;
import com.tiancong.bestwish.media.VideoPlayerIJK;
import com.tiancong.bestwish.media.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends BaseActivity {

    ActivityMain2Binding main2Binding;

    VideoPlayerIJK ijkPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main2Binding = DataBindingUtil.setContentView(this, R.layout.activity_main2);

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        main2Binding.surface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        main2Binding.surface.getHolder().setFixedSize(176, 144);
        main2Binding.surface.getHolder().setKeepScreenOn(true);
        main2Binding.surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("sss", "1: ");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("sss", "2: ");
                ijkPlayer.load(main2Binding.surface.getHolder());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("sss", "3: ");
            }
        });


        ijkPlayer = new VideoPlayerIJK(this);

        ijkPlayer.setVideoPath("/storage/emulated/0/AAA/s.mp4");
        ijkPlayer.start();
        ijkPlayer.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                Log.d("sss", "onError: ");
            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.d("sss", "onError: ");
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.d("sss", "onError: ");
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.d("sss", "onError: ");
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                Log.d("sss", "onError: ");
                iMediaPlayer.start();
            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                Log.d("sss", "onError: ");
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                Log.d("sss", "onError: ");
            }
        });

        main2Binding.btAaa.setOnClickListener(v -> {
            startActivity(new Intent(this,ListActivity.class));
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkPlayer.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
    }
}
