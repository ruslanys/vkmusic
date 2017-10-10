package me.ruslanys.vkmusic.component.impl;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import lombok.SneakyThrows;
import me.ruslanys.vkmusic.component.Id3vTagManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class DefaultId3vTagManager implements Id3vTagManager {

    @SneakyThrows
    @Override
    public File fix(File source) {
        Mp3File mp3File = new Mp3File(source);

        mp3File.removeCustomTag();

        if (mp3File.hasId3v1Tag()) {
            ID3v1 tag = mp3File.getId3v1Tag();

            tag.setArtist(fixString(tag.getArtist()));
            tag.setTitle(fixString(tag.getTitle()));
            tag.setAlbum(fixString(tag.getAlbum()));
            tag.setComment(fixString(tag.getComment()));

            mp3File.setId3v1Tag(tag);
        }

        if (mp3File.hasId3v2Tag()) {
            ID3v2 tag = mp3File.getId3v2Tag();

            tag.setArtist(fixString(tag.getArtist()));
            tag.setTitle(fixString(tag.getTitle()));
            tag.setAlbum(fixString(tag.getAlbum()));
            tag.setComment(fixString(tag.getComment()));

            mp3File.setId3v2Tag(tag);
        }

        String filename = source.toString() + "_fixed.mp3";
        try {
            mp3File.save(filename);
        } catch (NotSupportedException e) {
            mp3File.removeId3v2Tag();
            mp3File.save(filename);
        }
        return new File(filename);
    }

    private String fixString(String string) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(string)) return string;
        return new String(string.getBytes("ISO-8859-1"), "windows-1251");
    }
}
