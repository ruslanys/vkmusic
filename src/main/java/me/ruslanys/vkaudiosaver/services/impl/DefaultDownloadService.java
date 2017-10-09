package me.ruslanys.vkaudiosaver.services.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.domain.event.AudioUpdatedEvent;
import me.ruslanys.vkaudiosaver.domain.event.DownloadFinishedEvent;
import me.ruslanys.vkaudiosaver.property.DownloaderProperties;
import me.ruslanys.vkaudiosaver.services.AudioService;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Service
public class DefaultDownloadService implements DownloadService {

    private final ApplicationEventPublisher publisher;

    private final VkClient vkClient;
    private final AudioService audioService;
    private final PropertyService propertyService;


    @Autowired
    public DefaultDownloadService(@NonNull ApplicationEventPublisher publisher,
                                  @NonNull VkClient vkClient,
                                  @NonNull AudioService audioService,
                                  @NonNull PropertyService propertyService) {
        this.publisher = publisher;
        this.vkClient = vkClient;
        this.audioService = audioService;
        this.propertyService = propertyService;
    }

    @Override
    public void download(List<Audio> audios) {
        List<Audio> list = perform(audios);

        DownloaderProperties properties = propertyService.getDownloaderProperties();
        download(properties, list);
    }

    private List<Audio> perform(List<Audio> audios) {
        List<Audio> list = new ArrayList<>(audios);
        list.removeIf(audio -> audio.getStatus() != Audio.Status.NEW);

        vkClient.fetchUrl(list);

        Iterator<Audio> it = list.iterator();
        while (it.hasNext()) {
            Audio audio = it.next();
            if (audio.getUrl() == null) {
                audio.setStatus(Audio.Status.SKIPPED);
                audioService.save(audio);
                it.remove();

                publisher.publishEvent(new AudioUpdatedEvent(this, audio));
            }
        }
        return list;
    }

    @SneakyThrows
    private void download(DownloaderProperties properties, List<Audio> audios) {
        log.info("Download pool-size: {}", properties.getPoolSize());
        log.info("Download destination: {}", properties.getDestination());

        ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(properties.getPoolSize()));
        ExecutorService loggingExecutor = Executors.newSingleThreadExecutor();
        File destinationFolder = new File(properties.getDestination());

        if (!destinationFolder.exists() && !destinationFolder.mkdirs()) {
            throw new IllegalStateException("Could not create destination folder.");
        }

        for (Audio audio: audios) {
            String filename = destinationFolder.toString() + "/" + audio.getFilename();
            String url = audio.getUrl();

            executor.submit(new Downloader(filename, url))
                    .addListener(new Listener(audio), loggingExecutor);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        loggingExecutor.shutdown();
        loggingExecutor.awaitTermination(1, TimeUnit.MINUTES);

        publisher.publishEvent(new DownloadFinishedEvent(this, audios));
    }


    private static class Downloader implements Callable<File> {

        private final File destination;
        private final String url;

        Downloader(String destination, String url) {
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

    private class Listener implements Runnable {

        private final Audio audio;

        private Listener(Audio audio) {
            this.audio = audio;
        }

        @Override
        public void run() {
            audio.setStatus(Audio.Status.DOWNLOADED);
            audioService.save(audio);

            publisher.publishEvent(new AudioUpdatedEvent(DefaultDownloadService.this, audio));
        }
    }

}
