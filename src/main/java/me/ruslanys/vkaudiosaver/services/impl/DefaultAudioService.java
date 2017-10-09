package me.ruslanys.vkaudiosaver.services.impl;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.entity.Audio;
import me.ruslanys.vkaudiosaver.entity.domain.event.DownloadStatusEvent;
import me.ruslanys.vkaudiosaver.entity.domain.event.LogoutEvent;
import me.ruslanys.vkaudiosaver.repository.AudioRepository;
import me.ruslanys.vkaudiosaver.services.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Transactional
@Service
public class DefaultAudioService implements AudioService, ApplicationListener<LogoutEvent> {

    private final VkClient vkClient;
    private final AudioRepository audioRepository;

    @Autowired
    public DefaultAudioService(@NonNull VkClient vkClient,
                               @NonNull AudioRepository audioRepository) {
        this.vkClient = vkClient;
        this.audioRepository = audioRepository;
    }

    @Override
    public List<Audio> findAll() {
        return audioRepository.findAllByOrderByPositionAsc();
    }

    @Override
    public List<Audio> fetchAll() {
        List<Audio> audioList = vkClient.getAudio();
        int size = audioList.size();
        for (int i = 0; i < size; i++) {
            Audio audio = audioList.get(i);
            audio.setPosition(i + 1);

            Audio audioInDb = audioRepository.findOne(audio.getId());
            if (audioInDb == null) {
                audioRepository.save(audio);
            } else if (!audioInDb.getPosition().equals(audio.getPosition())) {
                audioInDb.setPosition(audio.getPosition());
                audioRepository.save(audioInDb);
            }
        }

        return findAll();
    }

    @Override
    public void save(Audio audio) {
        audioRepository.save(audio);
    }

    @Override
    public void onDownloadStatusEvent(DownloadStatusEvent event) {
        Audio audio = event.getAudio();
        Audio entity = audioRepository.findOne(audio.getId());
        entity.setStatus(event.getStatus());
        save(entity);
    }

    @Override
    public void onApplicationEvent(LogoutEvent event) {
        audioRepository.deleteAll();
    }
}
