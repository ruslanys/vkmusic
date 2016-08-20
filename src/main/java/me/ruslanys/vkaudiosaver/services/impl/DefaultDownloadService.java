package me.ruslanys.vkaudiosaver.services.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.impl.DefaultFileDownloader;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Service
public class DefaultDownloadService implements DownloadService {

    private static final String STATUS_TEMPLATE = "{} files in the queue to download.";

    private final ListeningExecutorService executor;
    private final ExecutorService listenerExecutor;

    private final File destinationFolder;

    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    public DefaultDownloadService(@Value("${downloader.pool-size}") int poolSize,
                                  @Value("${downloader.destination}") String destination) {
        this.executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(poolSize));
        this.listenerExecutor = Executors.newSingleThreadExecutor();
        this.destinationFolder = new File(destination);

        if (!destinationFolder.exists()) {
            if (!destinationFolder.mkdirs()) {
                throw new IllegalStateException("Could not create destination folder.");
            }
        }
    }

    @SneakyThrows
    @Override
    public void download(Collection<Audio> audios) {
        log.info(STATUS_TEMPLATE, counter.addAndGet(audios.size()));

        for (Audio audio: audios) {
            DefaultFileDownloader downloader = new DefaultFileDownloader(
                    Audio.getFilename(destinationFolder.toString(), audio),
                    audio.getUrl()
            );

            executor.submit(downloader)
                    .addListener(() -> log.info(STATUS_TEMPLATE, counter.decrementAndGet()), listenerExecutor);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        listenerExecutor.shutdown();
        listenerExecutor.awaitTermination(1, TimeUnit.MINUTES);
    }

}
