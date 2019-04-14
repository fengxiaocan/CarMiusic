package com.evil.carmiusic.utils;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.id3.ID3v1_1;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2019-04-14.
 */

public class ID3v1_2 extends ID3v1_1 {
    private String defaultCharsetName = "GBK";

    @Override
    public void read(RandomAccessFile file)
            throws TagNotFoundException, IOException
    {
//        super.read(file);
        byte[] buffer = new byte[30];
        if(!this.seek(file)) {
            throw new TagNotFoundException("ID3v1.1 tag not found");
        } else {
            file.read(buffer, 0, 30);
            this.title = (new String(buffer, 0, 30, defaultCharsetName)).trim();
            file.read(buffer, 0, 30);
            this.artist = (new String(buffer, 0, 30, defaultCharsetName)).trim();
            file.read(buffer, 0, 30);
            this.album = (new String(buffer, 0, 30, defaultCharsetName)).trim();
            file.read(buffer, 0, 4);
            this.year = (new String(buffer, 0, 4, defaultCharsetName)).trim();
            file.read(buffer, 0, 28);
            this.comment = (new String(buffer, 0, 28, defaultCharsetName)).trim();
            file.read(buffer, 0, 2);
            if(buffer[0] == 0) {
                this.track = buffer[1];
                file.read(buffer, 0, 1);
                this.genre = buffer[0];
            } else {
                throw new TagNotFoundException("ID3v1.1 Tag Not found");
            }
        }
    }

    @Override
    public void write(AbstractMP3Tag tag) {
        super.write(tag);
    }

    @Override
    public void write(RandomAccessFile file)
            throws IOException
    {
        super.write(file);
    }
}
