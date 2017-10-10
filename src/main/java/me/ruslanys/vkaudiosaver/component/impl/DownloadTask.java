package me.ruslanys.vkaudiosaver.component.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.exception.DownloadException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j
public class DownloadTask implements Callable<DownloadTask.Result> {

    private final File destinationFolder;
    private final Audio audio;

    public DownloadTask(File destinationFolder, Audio audio) {
        this.destinationFolder = destinationFolder;
        this.audio = audio;
    }

    @Override
    public Result call() throws Exception {
        try {
            log.debug("Download started for {}", audio.getUrl());

            HttpURLConnection connection = (HttpURLConnection) new URL(audio.getUrl()).openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);

            File file = new File(destinationFolder, audio.getFilename());
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                byte[] buff = new byte[5120];
                int len;
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                out.flush();
                out.close();

                log.debug("Download finished for {}", audio.getUrl());

                return new Result(audio, file);
            }
        } catch (Exception e) {
            log.error("Download file error (audio #" + audio.getId() + ")", e);
            throw new DownloadException(e, audio);
        }
    }

    @Data
    public static class Result {
        private final Audio audio;
        private final File file;
    }

}
