package com.evil.carmiusic.interfaces;

/**
 * @项目名： Mibokids
 * @包名： com.mibokids.app.music.interfaces
 * @创建者: feng
 * @时间: 18:48
 * @描述： 播放接口
 */
public interface PlayListener {
    /**
     * 播放完成
     */
    void onCompletion();

    /**
     * 缓冲完成
     */
    void onPrepared();

    /**
     * 缓冲监听
     * @param max 最大值
     * @param percent 现在值的百分比
     */
    void onBuffering(int max, int percent);

    /**
     * 进度监听
     * @param progress
     */
    void onProgress(int progress);

    /**
     * 最大进度值
     * @param max
     */
    void maxProgress(int max);
}
