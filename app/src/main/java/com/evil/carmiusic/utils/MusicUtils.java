package com.evil.carmiusic.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;


import java.io.File;

/**
 * @项目名： Mibokids
 * @包名： com.mibokids.app.music
 * @创建者: feng
 * @时间: 13:55
 * @描述： 音乐相关的工具类
 */
public class MusicUtils {
    /**
     * 获取音乐文件夹
     *
     * @return
     */
    public static File getMusicDir() {
        File dir = null;
//        if (CacheUtils.hasSdcard()) {
            dir = new File(Environment.getExternalStorageDirectory(), "mibokids/music/");
//        } else {
//            dir = new File(UiUtils.getContext().getFilesDir(), "music");
//        }
        dir.mkdirs();

        return dir;
    }

    /**
     * 获取音乐缓存文件夹
     *
     * @return
     */
    public static File getMusicCacheDir() {
        File dir = new File(getMusicDir(), "/cache/");
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取音乐歌词文件夹
     *
     * @return
     */
    public static File getMusicLrcDir() {
        File dir = new File(getMusicDir(), "/lrc/");
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取音乐下载文件夹
     *
     * @return
     */
    public static File getMusicDownDir() {
        File dir = new File(getMusicDir(), "/download/");
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取本地保存的亲子秀文件夹
     *
     * @return
     */
    public static File getSaveRecordDir() {
        File dir = new File(getMusicDir(), "/saveRecord/");
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取亲子秀下载文件夹
     *
     * @return
     */
    public static File getQinzixiuDownDir() {
        File dir = new File(getMusicDir(), "/qinzixiu/");
        dir.mkdirs();
        return dir;
    }

    /**
     * 格式化音乐时间: 120 000 --> 02:00
     *
     * @param time
     *
     * @return
     */
    public static String formatMusicTime(long time) {
        time = time / 1000;
        String formatTime;
        if (time < 10) {
            formatTime = "00:0" + time;
            return formatTime;
        } else if (time < 60) {
            formatTime = "00:" + time;
            return formatTime;
        } else {
            long i = time / 60;
            if (i < 10) {
                formatTime = "0" + i + ":";
            } else {
                formatTime = i + ":";
            }
            long j = time % 60;
            if (j < 10) {
                formatTime += "0" + j;
            } else {
                formatTime += j;
            }

            return formatTime;
        }
    }

    /**
     * 获取音频时长
     *
     * @param uri
     *
     * @return
     */
    public static String getAudioDuring(Context context, Uri uri) {
        String                               duration = null;
        android.media.MediaMetadataRetriever mmr      = new android.media.MediaMetadataRetriever();
        try {
            if (uri != null) {
                mmr.setDataSource(context, uri);
            }

            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }
}
