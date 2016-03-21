package com.example.videodemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.IOException;

/*
视频的屏幕适配问题
params设置
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private SurfaceView mSurfaceView;
    private MediaPlayer mPlayer;
    private int currentPosition;
    private Button mButton_change;
    private Button mButton_pause;
    private SeekBar mSeekBar_Pro;
    private SeekBar mSeekBar_Vol;
    private MyReceiver myReceiver;
    private AudioManager am;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_video);
        mButton_change = (Button) findViewById(R.id.bt_change);
        mButton_pause = (Button) findViewById(R.id.bt_play_pause);
        mSeekBar_Pro = (SeekBar) findViewById(R.id.sb_video);
        mSeekBar_Vol = (SeekBar) findViewById(R.id.sb_vol);
        mSeekBar_Pro.setOnSeekBarChangeListener(this);
        mButton_change.setOnClickListener(this);
        mButton_pause.setOnClickListener(this);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.sv_video) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mButton_pause.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }
        });
        //Android3.0之后默认，3.0之前需要设置
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                playVideo();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    currentPosition = mPlayer.getCurrentPosition();
                }
                stop();
            }
        });
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSeekBar_Vol.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mSeekBar_Vol.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        mSeekBar_Vol.setOnSeekBarChangeListener(this);
    }

    private void initData() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(myReceiver, filter);
    }

    private void playVideo() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPlayer = new MediaPlayer();
        try {
            //获取raw中的文件 1
            AssetFileDescriptor afd = getResources()
                    .openRawResourceFd(R.raw.land);
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            //2
//            mPlayer.setDataSource(MainActivity.this, Uri.parse("android.resource://" + getPackageName() + "/"
//                     + R.raw.video));
            mPlayer.setLooping(true);
            mPlayer.setDisplay(mSurfaceView.getHolder());
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    setVideoParams(mp);
                    mp.start();
                    mp.seekTo(currentPosition);
                    mSeekBar_Pro.setMax(mPlayer.getDuration());
                    startSeek();
                }
            });
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //动态设置屏幕分辨率不同时视频的显示
    private void setVideoParams(MediaPlayer mp) {
        float videoWidth = mp.getVideoWidth();
        float videoHeight = mp.getVideoHeight();
        float screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        float screenHeight = screenWidth / 16 * 9;
        float videoPor = videoWidth / videoHeight;
        float screenPor = screenWidth / screenHeight;
        ViewGroup.LayoutParams pa = mSurfaceView.getLayoutParams();
        if (videoPor <= screenPor) {

            pa.height = (int) screenHeight;
            pa.width = (int) (screenHeight * videoPor);

        } else {
            //视频20:9之类的
            pa.width = (int) screenWidth;
            pa.height = (int) (screenWidth / videoPor);

        }
        mSurfaceView.setLayoutParams(pa);
    }

    private void stop() {
        if (mPlayer != null) {
            try {
                mPlayer.pause();
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //用来监控横竖屏的改变
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
        setVideoParams(mPlayer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_change:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //当前为竖屏切换为横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.bt_play_pause:
                if(isPlaying){
                    mPlayer.pause();
                    mButton_pause.setText("播放");
                    mButton_pause.setVisibility(View.GONE);
                    isPlaying=false;
                }else {
                    mPlayer.start();
                    mButton_pause.setText("暂停");
                    mButton_pause.setVisibility(View.GONE);
                    isPlaying=true;
                }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        switch (seekBar.getId()) {
            case R.id.sb_video:
                try {
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        mPlayer.seekTo(progress);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sb_vol:
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGE_ACTION")) {
                mSeekBar_Vol.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        }
    }

    private void startSeek() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(500);
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mSeekBar_Pro.setProgress(mPlayer.getCurrentPosition());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }
}
