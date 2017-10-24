package me.ruslanys.vkmusic.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class JsonUtils implements ApplicationContextAware {

    private static ObjectMapper mapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        mapper = applicationContext.getBean(ObjectMapper.class);
    }

    @SneakyThrows
    public static String toString(Object obj) {
        return mapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T fromString(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }
}
