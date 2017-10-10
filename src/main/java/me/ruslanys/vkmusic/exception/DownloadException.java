package me.ruslanys.vkmusic.exception;

import lombok.Getter;
import me.ruslanys.vkmusic.entity.Audio;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class DownloadException extends Exception {

    @Getter
    private final Audio audio;

    public DownloadException(Throwable cause, Audio audio) {
        super(cause);
        this.audio = audio;
    }
}
