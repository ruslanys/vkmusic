package me.ruslanys.vkaudiosaver.services.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.component.impl.DownloadTask;
import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.entity.domain.DownloadStatus;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadFailEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadFinishEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadSuccessEvent;
import me.ruslanys.vkaudiosaver.exception.DownloadException;
import me.ruslanys.vkaudiosaver.property.DownloaderProperties;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
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

    @Override
    public void download(List<Audio> audios) {
        List<Audio> list = fetchUrls(audios);

        DownloaderProperties properties = propertyService.get(DownloaderProperties.class);
        download(properties, list);
    }

    private List<Audio> fetchUrls(List<Audio> audios) {
        List<Audio> list = new ArrayList<>(audios);
        list.removeIf(audio -> audio.getStatus() != DownloadStatus.NEW);

        vkClient.fetchUrls(list);
        return list;
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
        CompletionService<DownloadTask.Result> completionService = new ExecutorCompletionService<>(executor);

        for (Audio audio : audios) {
            completionService.submit(new DownloadTask(destinationFolder, audio));
        }

        int n = audios.size();
        for (int i = 0; i < n; i++) {
            try {
                DownloadTask.Result result = completionService.take().get();
                publisher.publishEvent(new DownloadSuccessEvent(this, result));
            } catch (ExecutionException ex) {
                publisher.publishEvent(new DownloadFailEvent(this, (DownloadException) ex.getCause()));
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        publisher.publishEvent(new DownloadFinishEvent(this, audios));
    }


}
