package me.ruslanys.vkaudiosaver.services.impl;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.domain.event.LogoutEvent;
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
    public List<Audio> getAll() {
        List<Audio> audioList = vkClient.getAudio();
        for (int i = 0; i < audioList.size(); i++) {
            Audio audio = audioList.get(i);
            audio.setPosition(i + 1);

            Audio audioInDb = audioRepository.findOne(audio.getId());
            if (audioInDb == null || !audioInDb.equals(audio)) {
                audioRepository.save(audio);
            }
        }

        return audioRepository.findAllByOrderByPositionAsc();
    }

    @Override
    public void save(Audio audio) {
        audioRepository.save(audio);
    }

    @Override
    public void onApplicationEvent(LogoutEvent event) {
        audioRepository.deleteAll();
    }
}
