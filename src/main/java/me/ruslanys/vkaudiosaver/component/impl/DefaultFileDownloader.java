package me.ruslanys.vkaudiosaver.component.impl;

import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.FileDownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j
public class DefaultFileDownloader implements FileDownloader {

    private final File destination;
    private final String url;

    public DefaultFileDownloader(String destination, String url) {
        this.destination = new File(destination);
        this.url = url;
    }

    @Override
    public File call() throws Exception {
        log.debug("Download started for {}", url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        try (InputStream in = connection.getInputStream();
             OutputStream out = new FileOutputStream(destination)) {
            byte[] buff = new byte[5120];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.flush();
            out.close();

            log.debug("Download finished for {}", url);

            return destination;
        } catch (Exception e) {
            log.error("Download file error: {}", e);
            throw e;
        }
    }

}
