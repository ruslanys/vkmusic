package me.ruslanys.vkmusic.services;

import lombok.NonNull;
import me.ruslanys.vkmusic.property.Properties;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface PropertyService {

    void set(@NonNull Properties Properties);

    <T extends Properties> T get(Class<T> clazz);

    <T extends Properties> void remove(Class<T> clazz);

}
