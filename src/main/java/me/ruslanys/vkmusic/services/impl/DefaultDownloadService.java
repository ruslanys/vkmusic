package me.ruslanys.vkmusic.services.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.component.impl.DownloadTask;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadResult;
import me.ruslanys.vkmusic.entity.domain.event.DownloadFailEvent;
import me.ruslanys.vkmusic.entity.domain.event.DownloadFinishEvent;
import me.ruslanys.vkmusic.entity.domain.event.DownloadSuccessEvent;
import me.ruslanys.vkmusic.exception.DownloadException;
import me.ruslanys.vkmusic.property.DownloaderProperties;
import me.ruslanys.vkmusic.services.DownloadService;
import me.ruslanys.vkmusic.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Service
public class DefaultDownloadService implements DownloadService {

    private final ApplicationEventPublisher publisher;

    private final VkClient vkClient;
    private final PropertyService propertyService;


    @Autowired
    public DefaultDownloadService(@NonNull ApplicationEventPublisher publisher,
                                  @NonNull VkClient vkClient,
                                  @NonNull PropertyService propertyService) {
        this.publisher = publisher;
        this.vkClient = vkClient;
        this.propertyService = propertyService;
    }

    @Async
    @Override
    public void downloadAsync(List<Audio> audios) {
        download(audios);
    }

    @Override
    public void download(List<Audio> audios) {
        log.info("Download queue: {}", audios.size());
        vkClient.fetchUrls(audios);

        DownloaderProperties properties = propertyService.get(DownloaderProperties.class);
        download(properties, audios);
    }

    @SneakyThrows
    private void download(DownloaderProperties properties, List<Audio> audios) {
        log.info("Download pool-size: {}", properties.getPoolSize());
        log.info("Download destination: {}", properties.getDestination());

        File destinationFolder = new File(properties.getDestination());
        if (!destinationFolder.exists() && !destinationFolder.mkdirs()) {
            throw new IllegalStateException("Can not create destination folder.");
        }

        ExecutorService executor = Executors.newFixedThreadPool(properties.getPoolSize());
        CompletionService<DownloadResult> completionService = new ExecutorCompletionService<>(executor);

        for (Audio audio : audios) {
            completionService.submit(new DownloadTask(destinationFolder, audio));
        }

        int n = audios.size();
        for (int i = 0; i < n; i++) {
            try {
                DownloadResult result = completionService.take().get();
                publisher.publishEvent(new DownloadSuccessEvent(this, result));
            } catch (ExecutionException ex) {
                publisher.publishEvent(new DownloadFailEvent(this, (DownloadException) ex.getCause()));
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        publisher.publishEvent(new DownloadFinishEvent(this, audios));
    }


}
