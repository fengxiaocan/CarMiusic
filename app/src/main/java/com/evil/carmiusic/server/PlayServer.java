package com.evil.carmiusic.server;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;

import com.evil.carmiusic.interfaces.PlayListener;


/**
 * @项目名： Mibokids
 * @包名： com.mibokids.app.music
 * @创建者: feng
 * @时间: 9:32
 * @描述： 音乐服务
 */
public class PlayServer extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private static MediaPlayer mPlayer;
    private Handler mHandler = new Handler();
    private Thread       mThread;
    private PlayListener mPlayListener;

    /**
     * 音乐播放完成事件监听
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPlayListener != null) {
            mPlayListener.onCompletion();
        }
    }

    /**
     * MediaPlayer进入prepared状态开始播放
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mPlayer != null) {
            mPlayer.start();
        }
        if (mPlayListener != null) {
            mPlayListener.onPrepared();
        }
    }

    /**
     * 网络缓冲器
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mPlayListener != null) {
            mPlayListener.onBuffering(mPlayer.getDuration(), percent);
        }
    }

    /**
     * 设置循环播放
     *
     * @param looping
     */
    public void setLooping(boolean looping) {
        if (mPlayer != null) {
            mPlayer.setLooping(looping);
        }
    }

    /**
     * 获取服务的实例
     */
    public class MusicBinder extends Binder {

        public PlayServer getMusicServer() {
            return PlayServer.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }



    //    private void registerScreenActionReceiver(){


    @Override
    public MusicBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onDestroy() {
        //释放资源
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //服务被kill时，不会重启服务
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public void start(Uri uri) {
        synchronized (PlayServer.class) {
            try {
                if (mPlayer != null) {
                    mPlayer.reset();//把各项参数恢复到初始状态
                    mPlayer.release();
                    mPlayer = null;
                }
                mPlayer = MediaPlayer.create(PlayServer.this, uri);
                //设置加载完成事件监听
                mPlayer.setOnPreparedListener(PlayServer.this);
                //播放完成事件监听
                mPlayer.setOnCompletionListener(PlayServer.this);
                //网络缓冲
                mPlayer.setOnBufferingUpdateListener(PlayServer.this);
                mPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void play() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }


    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }


    /**
     * 获取音乐时长
     *
     * @return
     */
    public long getDuration() {
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getDuration();
    }

    /**
     * 以毫秒秒为单位
     *
     * @return
     */
    public long getCurrentPosition() {
        if (mPlayer == null) {
            return 0;
        }
        try {
            return mPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    public void seekTo(long seek) {
        if (mPlayer == null) {
            return;
        }
        mPlayer.seekTo((int) seek);
    }

    /**
     * 设置音乐播放器监听器
     *
     * @param playListener
     */
    public void setPlayListener(PlayListener playListener) {
        mPlayListener = playListener;

        stopListener = false;
        mThread = new Thread(mRunnable);
        mThread.start();
    }


    private boolean stopListener = false;//监听线程停止标记


    private void musicPlayListener() {
        mHandler.postDelayed(mRunnable, 150);
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if (stopListener) {//停止监听
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /**
                     * 设置线程监听回调
                     */
                    if (mPlayListener != null && mPlayer != null) {
                        try {
                            int duration = mPlayer.getDuration();
                            if (duration > 10000000) {
                                mPlayListener.maxProgress(0);
                            }else{
                                mPlayListener.maxProgress(duration);
                            }
                            int currentPosition = mPlayer.getCurrentPosition();
                            if (currentPosition < duration) {
                                mPlayListener.onProgress(currentPosition);
                            }else{
                                mPlayListener.onProgress(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            /**
             * 重复监听动作
             */
            musicPlayListener();
        }
    };


    /**
     * 发送播放状态
     */
//    private void sendPlayStatu() {
//        mHandler.postDelayed(mPlayStatuRunnable, 1000);
//    }
//
//    private void stopPlayStatu(){
//        mHandler.removeCallbacks(mPlayStatuRunnable);
//    }

    /**
     * 播放状态
     */
//    private Runnable mPlayStatuRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (mPlayer != null) {
//                Intent intent = new Intent(Constants.PLAY_STATU);
//                if (mPlayer.isPlaying()) {
//
//                }
//                boolean playing = false;
//                try {
//                    playing = mPlayer.isPlaying();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                intent.putExtra("isPlay", playing);
//                sendBroadcast(intent);
//            }
//            sendPlayStatu();
//        }
//    };
}
