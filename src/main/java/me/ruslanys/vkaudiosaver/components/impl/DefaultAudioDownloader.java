package me.ruslanys.vkaudiosaver.components.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mpatric.mp3agic.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.AudioDownloader;
import me.ruslanys.vkaudiosaver.domain.Audio;
import org.apache.commons.lang3.StringUtils;
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
        executor.awaitTermination(1, TimeUnit.HOURS);
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
            File source = new File(audio.getId() + ".mp3");

            try (InputStream in = connection.getInputStream()) {
                OutputStream out = new FileOutputStream(source);

                byte[] buff = new byte[5120];
                int len;
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                out.flush();
                out.close();

                log.debug("Download finished for {}", audio);

                String updatedFilename = updateTag(source);
                source.delete();
                return new File(updatedFilename);
            } catch (Exception e) {
                log.error("Failed to download: {}", audio);
                log.error("Download file error: {}", e);
                throw e;
            }
        }

        private String updateTag(File source) throws IOException, UnsupportedTagException, InvalidDataException, NotSupportedException {
            Mp3File mp3File = new Mp3File(source);

            mp3File.removeCustomTag();

            if (mp3File.hasId3v1Tag()) {
                ID3v1 tag = mp3File.getId3v1Tag();

                tag.setArtist(fixString(tag.getArtist()));
                tag.setTitle(fixString(tag.getTitle()));
                tag.setAlbum(fixString(tag.getAlbum()));
                tag.setComment(fixString(tag.getComment()));

                mp3File.setId3v1Tag(tag);
            }

            if (mp3File.hasId3v2Tag()) {
                ID3v2 tag = mp3File.getId3v2Tag();

                tag.setArtist(fixString(tag.getArtist()));
                tag.setTitle(fixString(tag.getTitle()));
                tag.setAlbum(fixString(tag.getAlbum()));
                tag.setComment(fixString(tag.getComment()));

                mp3File.setId3v2Tag(tag);
            }

            String filename = Audio.getFilename(destination.toString(), audio);
            mp3File.save(filename);
            return filename;
        }

        private String fixString(String string) throws UnsupportedEncodingException {
            if (StringUtils.isEmpty(string)) return string;
            return new String(string.getBytes("ISO-8859-1"), "windows-1251");
        }

    }

}
