package me.ruslanys.vkmusic.services.impl;

import lombok.NonNull;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;
import me.ruslanys.vkmusic.entity.domain.event.DownloadStatusEvent;
import me.ruslanys.vkmusic.repository.AudioRepository;
import me.ruslanys.vkmusic.services.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Transactional
@Service
public class DefaultAudioService implements AudioService {

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
    public List<Audio> findFailed() {
        return audioRepository.findByStatusOrderByPositionAsc(DownloadStatus.FAIL);
    }

    @Override
    public List<Audio> fetchAll() {
        List<Audio> audioList = vkClient.getAudio();
        int size = audioList.size();
        for (int i = 0; i < size; i++) {
            Audio audio = audioList.get(i);
            audio.setPosition(i + 1);

            Optional<Audio> audioInDb = audioRepository.findById(audio.getId());
            if (audioInDb.isPresent()) {
                Audio model = audioInDb.get();
                model.setPosition(audio.getPosition());
                audioRepository.save(model);
            } else {
                audioRepository.save(audio);
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
        Audio entity = audioRepository.findById(audio.getId()).get();
        entity.setStatus(event.getStatus());
        save(entity);
    }

}
