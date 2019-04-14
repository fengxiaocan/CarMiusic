package com.evil.carmiusic.utils;

import org.farng.mp3.object.AbstractMP3Object;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2019-04-14.
 */

public class ID3v2MP3Object
        extends AbstractMP3Object
{
    @Override
    public void readByteArray(byte[] arr, int offset) {
        //        super.readByteArray(arr, offset);
        String charset;
        int    charcode = arr[0];//获取编码标志？
        if (charcode == 0) {
            charset = "GBK";
        } else if (charcode == 3) {
            charset = "UTF-8";
        } else {
            charset = "UTF-16";
        }
        try {
            String con = new String(arr, 1, arr.length - 1, charset);//编码
            //这以下都是为了解码不错而写的，没有依据
            offset--;
            if (offset >= con.length()) {
                offset = con.length() - 1;
            }

            if (offset < 0) {
                offset = 0;
            }

            if ((offset == 0) && (con.length() == 0)) {
                con = " ";
            }
            //这以上都是为了解码不错而写的，没有依据

            readString(con, offset);//jar里面 的方法
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
