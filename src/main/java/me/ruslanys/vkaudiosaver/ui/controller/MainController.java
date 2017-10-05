package me.ruslanys.vkaudiosaver.ui.controller;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.services.AudioService;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.ui.view.MainFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class MainController implements Runnable {

    private final MainFrame mainFrame;

    private final VkClient vkClient;
    private final AudioService audioService;
    private final PropertyService propertyService;

    @Autowired
    public MainController(@NonNull MainFrame mainFrame,
                          @NonNull VkClient vkClient,
                          @NonNull AudioService audioService,
                          @NonNull PropertyService propertyService) {
        this.mainFrame = mainFrame;
        this.vkClient = vkClient;
        this.audioService = audioService;
        this.propertyService = propertyService;
    }

    @Override
    public void run() {
        mainFrame.setStatus(propertyService.getVkProperties().getUsername());
        mainFrame.setVisible(true);

        List<Audio> audioList = vkClient.getAudio();
        mainFrame.getModel().addEntities(audioList);
    }
}
