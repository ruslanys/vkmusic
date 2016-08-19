package me.ruslanys.vkaudiosaver.components.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.AudioDownloader;
import me.ruslanys.vkaudiosaver.domain.Audio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class DefaultAudioDownloader implements AudioDownloader {

    private static final String STATUS_TEMPLATE = "{} files in the queue to download.";

    private final ListeningExecutorService executor;
    private final ExecutorService listenerExecutor;

    private final String defaultDestination;

    private final AtomicInteger counter = new AtomicInteger();

    public DefaultAudioDownloader(@Value("${downloader.pool-size}") int poolSize,
                                  @Value("${downloader.destination}") String destination) {
        this.executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(poolSize));
        this.listenerExecutor = Executors.newSingleThreadExecutor();
        this.defaultDestination = destination;
    }

    @SneakyThrows
    @Override
    public void download(String destination, Collection<Audio> audios) {
        File destinationFolder = new File(destination);
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        log.info(STATUS_TEMPLATE, counter.addAndGet(audios.size()));

        for (Audio audio: audios) {
            executor.submit(new Processor(destination, audio))
                    .addListener(() -> log.info(STATUS_TEMPLATE, counter.decrementAndGet()), listenerExecutor);
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        listenerExecutor.shutdown();
        listenerExecutor.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Override
    public void download(Collection<Audio> audios) {
        download(defaultDestination, audios);
    }

    @Slf4j
    private static class Processor implements Callable<File> {

        private final File destination;
        private final Audio audio;

        private Processor(String destination, Audio audio) {
            this.destination = new File(destination);
            this.audio = audio;
        }

        @Override
        public File call() throws Exception {
            log.debug("Download started for {}", audio);

            HttpURLConnection connection = (HttpURLConnection) new URL(audio.getUrl()).openConnection();
            File file = new File(Audio.getFilename(destination.toString(), audio));

            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(file)) {

                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                out.flush();

                log.debug("Download finished for {}", audio);
                return file;
            } catch (IOException e) {
                log.error("Failed to download: {}", audio);
                log.error("Download file error: {}", e);
                throw e;
            }
        }

    }

}
