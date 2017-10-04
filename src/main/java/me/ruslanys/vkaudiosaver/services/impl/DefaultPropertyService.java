package me.ruslanys.vkaudiosaver.services.impl;

import me.ruslanys.vkaudiosaver.domain.Property;
import me.ruslanys.vkaudiosaver.property.DownloaderProperties;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.repository.PropertyRepository;
import me.ruslanys.vkaudiosaver.services.PropertyService;
import me.ruslanys.vkaudiosaver.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Transactional
@Service
public class DefaultPropertyService implements PropertyService {

    private static final String KEY_VK = "VK";
    private static final String KEY_DOWNLOADER = "DOWNLOADER";

    private final PropertyRepository propertyRepository;

    @Autowired
    public DefaultPropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void save(VkProperties vkProperties) {
        Property property = new Property(KEY_VK, JsonUtils.toString(vkProperties));
        propertyRepository.save(property);
    }

    @Override
    public void save(DownloaderProperties downloaderProperties) {
        Property property = new Property(KEY_DOWNLOADER, JsonUtils.toString(downloaderProperties));
        propertyRepository.save(property);
    }

    @Override
    public VkProperties getVkProperties() {
        Property property = propertyRepository.findOne(KEY_VK);
        if (property == null) {
            return null;
        }
        return JsonUtils.fromString(property.getJson(), VkProperties.class);
    }

    @Override
    public DownloaderProperties getDownloaderProperties() {
        Property property = propertyRepository.findOne(KEY_DOWNLOADER);
        if (property == null) {
            return null;
        }
        return JsonUtils.fromString(property.getJson(), DownloaderProperties.class);
    }

    @Override
    public void cleanVkProperties() {
        if (propertyRepository.exists(KEY_VK)) {
            propertyRepository.delete(KEY_VK);
        }
    }
}
