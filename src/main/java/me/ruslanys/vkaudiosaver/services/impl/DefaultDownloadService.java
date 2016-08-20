package me.ruslanys.vkaudiosaver.services.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.PlaylistCreator;
import me.ruslanys.vkaudiosaver.components.impl.DefaultFileDownloader;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.properties.DownloaderProperties;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ListeningExecutorService executor;
    private ExecutorService listenerExecutor;

    private final PlaylistCreator playlistCreator;
    private File destinationFolder;

    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    public DefaultDownloadService(PlaylistCreator playlistCreator) {
        this.playlistCreator = playlistCreator;
    }

    @Override
    public void init(DownloaderProperties properties) {
        log.info("Download pool-size: {}", properties.getPoolSize());
        log.info("Download destination: {}", properties.getDestination());

        executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(properties.getPoolSize()));
        listenerExecutor = Executors.newSingleThreadExecutor();
        destinationFolder = new File(properties.getDestination());

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

        log.info("Making playlist...");
        playlistCreator.playlist(destinationFolder.toString(), audios);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        listenerExecutor.shutdown();
        listenerExecutor.awaitTermination(1, TimeUnit.MINUTES);
    }

}
