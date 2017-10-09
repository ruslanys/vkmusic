package me.ruslanys.vkaudiosaver.services;

import lombok.NonNull;
import me.ruslanys.vkaudiosaver.property.Properties;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface PropertyService {

    void set(@NonNull Properties Properties);

    <T extends Properties> T get(Class<T> clazz);

    <T extends Properties> void remove(Class<T> clazz);

}
