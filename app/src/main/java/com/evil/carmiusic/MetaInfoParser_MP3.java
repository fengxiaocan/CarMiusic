package com.evil.carmiusic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2019-04-14.
 */

public class MetaInfoParser_MP3 {
    // Stores the data
    private String m_artist;
    private String m_title;
    private String m_floder;
    private String m_tyer;
    private String m_comm;

    public String getArtist()
    {
        return m_artist == null
               ? "Unknown"
               : m_artist;
    }

    public String getTitle()
    {
        return m_title == null
               ? "Unknown"
               : m_title;
    }

    // Same as parse but doesn't throw exceptions
    public boolean parseNoThrow(File mp3file)
    {
        try {
            return parse(mp3file);
        } catch (Exception ex) {
            return false;
        }
    }

    // Parses the MP3 file, and sets up artist and/or title.
    // Returns true if the file is MP3 file, it contains the ID3 tag, and at least artist or title is parsed
    public boolean parse(File mp3file)
            throws IOException
    {
        m_artist = null;
        m_title = null;

        // Read the first five bytes of the ID3 header - http://id3.org/id3v2-00
        RandomAccessFile file      = new RandomAccessFile(mp3file, "r");
        byte[]           headerbuf = new byte[10];
        file.read(headerbuf);

        // Parse it quickly
        if (headerbuf[0] != 'I' || headerbuf[1] != 'D' || headerbuf[2] != '3') {
            getID3V1(file);
            file.close();
            return false;
        }

        // True if the tag is pre-V3 tag (shorter headers)
        final int TagVersion = headerbuf[3];

        // Check the version
        if (TagVersion < 0 || TagVersion > 4) {
            file.close();
            return false;
        }

        // Get the ID3 tag size and flags; see 3.1
        int tagsize = (headerbuf[9] & 0xFF) | ((headerbuf[8] & 0xFF) << 7) | ((headerbuf[7] & 0xFF) << 14) | ((headerbuf[6] & 0xFF) << 21) + 10;
        boolean uses_synch = (headerbuf[5] & 0x80) != 0
                             ? true
                             : false;
        boolean has_extended_hdr = (headerbuf[5] & 0x40) != 0
                                   ? true
                                   : false;

        // Read the extended header length and skip it
        if (has_extended_hdr) {
            int headersize = file.read() << 21 | file.read() << 14 | file.read() << 7 | file.read();
            file.skipBytes(headersize - 4);
        }

        // Read the whole tag
        byte[] buffer = new byte[tagsize];
        file.read(buffer);
        file.close();

        // Prepare to parse the tag
        int length = buffer.length;

        // Recreate the tag if desynchronization is used inside; we need to replace 0xFF 0x00 with 0xFF
        if (uses_synch) {
            int    newpos    = 0;
            byte[] newbuffer = new byte[tagsize];

            for (int i = 0; i < buffer.length; i++) {
                if (i < buffer.length - 1 && (buffer[i] & 0xFF) == 0xFF && buffer[i + 1] == 0) {
                    newbuffer[newpos++] = (byte) 0xFF;
                    i++;
                    continue;
                }

                newbuffer[newpos++] = buffer[i];
            }

            length = newpos;
            buffer = newbuffer;
        }

        // Set some params
        int pos = 0;
        final int ID3FrameSize = TagVersion < 3
                                 ? 6
                                 : 10;

        // Parse the tags
        while (true) {
            int rembytes = length - pos;

            // Do we have the frame header?
            if (rembytes < ID3FrameSize) { break; }

            // Is there a frame?
            if (buffer[pos] < 'A' || buffer[pos] > 'Z') { break; }

            // Frame name is 3 chars in pre-ID3v3 and 4 chars after
            String framename;
            int    framesize;

            if (TagVersion < 3) {
                framename = new String(buffer, pos, 3);
                framesize = ((buffer[pos + 5] & 0xFF) << 8) | ((buffer[pos + 4] & 0xFF) << 16) | ((buffer[pos + 3] & 0xFF) << 24);
            } else {
                framename = new String(buffer, pos, 4);
                framesize = (buffer[pos + 7] & 0xFF) | ((buffer[pos + 6] & 0xFF) << 8) | ((buffer[pos + 5] & 0xFF) << 16) | ((buffer[pos + 4] & 0xFF) << 24);
            }

            if (pos + framesize > length) { break; }

            if (framename.equals("TPE1") || framename.equals("TPE2") || framename.equals("TPE3") || framename.equals(
                    "TPE"))
            {
                if (m_artist == null) {
                    m_artist = parseTextField(buffer, pos + ID3FrameSize, framesize);
                }
            }

            if (framename.equals("TIT2") || framename.equals("TIT")) {
                if (m_title == null) {
                    m_title = parseTextField(buffer, pos + ID3FrameSize, framesize);
                }
            }

            pos += framesize + ID3FrameSize;
            continue;
        }

        return m_title != null || m_artist != null;
    }

    private String parseTextField(final byte[] buffer, int pos, int size)
    {
        if (size < 2) { return null; }

        Charset charset;
        int     charcode = buffer[pos];

        if (charcode == 0) { charset = Charset.forName("GBK"); } else if (charcode == 3) {
            charset = Charset.forName("UTF-8");
        } else {
            charset = Charset.forName("UTF-16");
        }

        return charset.decode(ByteBuffer.wrap(buffer, pos + 1, size - 1))
                      .toString();
    }

    private void getID3V1(RandomAccessFile file) {
        byte[] buf = new byte[128];// 初始化标签信息的byte数组
        try {
            file.seek(file.length() - 128);// 移动到文件MP3末尾
            file.read(buf);// 读取标签信息
            file.close();// 关闭文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }// 随机读写方式打开MP3文件
        catch (IOException e) {
            e.printStackTrace();
        }

        if (buf.length != 128) {// 数据长度是否合法
            try {
                throw new Exception("MP3标签信息数据长度不合法!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        String tag = "";
        try {

            tag = new String(buf, 0, 3, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        if (!"TAG".equalsIgnoreCase(tag)) {// 标签头是否存在
            try {
                throw new Exception("MP3标签信息数据格式不正确!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            m_title = new String(buf, 3, 30, "gbk").trim();
            m_artist = new String(buf, 33, 30, "gbk").trim();// 歌手名字
            m_floder = new String(buf, 63, 30, "gbk").trim();// 专辑名称
            m_tyer = new String(buf, 93, 4, "gbk").trim();// 出品年份
            m_comm = new String(buf, 97, 28, "gbk").trim();// 备注信息
            System.out.println("作者:" + m_artist);
            System.out.println("标题:" + m_title);
            System.out.println("专集:" + m_floder);
//            System.out.println("音轨:" + m_trck);
            System.out.println("年代:" + m_tyer);
//            System.out.println("类型:" + m_tcon);
            System.out.println("备注1:" + m_comm);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
