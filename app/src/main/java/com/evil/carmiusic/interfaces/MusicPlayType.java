package com.evil.carmiusic.interfaces;

/**
 * @项目名： Mibokids
 * @包名： com.mibokids.app.music.interfaces
 * @创建者: feng
 * @时间: 17:54
 * @描述： 播放器的类型参数
 */
public interface MusicPlayType {
    /***************** 播放模式 *******************/
    int MUSIC_PLAY_ORDER       = 0;//顺序播放模式
    int MUSIC_PLAY_RANDOM      = 1;//随机播放模式
    int MUSIC_PLAY_LOOP        = 2;//循环播放模式
    int MUSIC_PLAY_SINGLE_LOOP = 3;//单曲循环播放模式

    /***************** 播放状态 *******************/
    int MUSIC_IS_PLAYING = 10;//正在播放
    int MUSIC_IS_STOP    = 11;//停止状态
    int MUSIC_IS_PAUSE   = 12;//暂停状态
}
