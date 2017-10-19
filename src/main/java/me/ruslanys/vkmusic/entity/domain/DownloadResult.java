package me.ruslanys.vkmusic.entity.domain;

import lombok.Data;
import me.ruslanys.vkmusic.entity.Audio;

import java.io.File;
import java.io.Serializable;

@Data
public class DownloadResult implements Serializable {
    private final Audio audio;
    private final File file;
}
