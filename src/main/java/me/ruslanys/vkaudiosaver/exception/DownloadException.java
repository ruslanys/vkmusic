package me.ruslanys.vkaudiosaver.exception;

import lombok.Getter;
import me.ruslanys.vkaudiosaver.entity.Audio;

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
