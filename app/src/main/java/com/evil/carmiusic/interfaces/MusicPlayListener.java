package com.evil.carmiusic.interfaces;

/**
 * @项目名： Mibokids
 * @包名： com.mibokids.app.music.interfaces
 * @创建者: feng
 * @时间: 16:52
 * @描述： 播放器监听器
 */
public class MusicPlayListener {
    /**最大进度*/
    public void maxProgress(long max){}
    /**进度值*/
    public void progress(long progress){}
    /**当前播放的音乐*/
    public void currentMusic(Object songBean){}

    /**
     * 音乐准备完成回调方法
     */
    public void onPreparedStart(){}

    /**
     * 网络缓冲监听
     * @param percent
     */
    public void onBufferingUpdate(int maxDuration ,int percent){}

    /**
     * 播放状态发生变化
     */
    public void onPlayStatusChange(int status){}
}
