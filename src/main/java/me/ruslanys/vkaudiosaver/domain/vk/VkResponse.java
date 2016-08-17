package me.ruslanys.vkaudiosaver.domain.vk;

import lombok.SneakyThrows;
import me.ruslanys.vkaudiosaver.exceptions.VkException;

import java.lang.reflect.Field;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@SuppressWarnings("unused")
public abstract class VkResponse {

    private VkError error;

    @SneakyThrows
    public void setResponse(Map<String, Object> response) {
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            Field field = findField(getClass(), entry.getKey());
            if (field == null) continue;

            makeAccessible(field);
            field.set(this, entry.getValue());
        }
    }

    public void setError(Map<String, Object> error) {
        this.error = new VkError((Integer) error.get("error_code"), (String) error.get("error_msg"));
    }

    public boolean hasError() {
        return error != null;
    }

    public void throwVkException() throws VkException {
        throw new VkException(error);
    }

}
