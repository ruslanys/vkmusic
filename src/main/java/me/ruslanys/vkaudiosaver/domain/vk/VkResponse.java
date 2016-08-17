package me.ruslanys.vkaudiosaver.domain.vk;

import lombok.SneakyThrows;
import me.ruslanys.vkaudiosaver.exceptions.VkException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@SuppressWarnings("unused")
public abstract class VkResponse {

    private VkError error;

    public void setResponse(Map<String, Object> response) {
        setObject(this, response);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void setObject(Object object, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Field field = findField(object.getClass(), entry.getKey());
            if (field == null) continue;

            makeAccessible(field);

            Class<?> fieldType = field.getType();
            Type fieldGenericType = field.getGenericType();

            if (List.class.isAssignableFrom(fieldType) && (fieldGenericType instanceof ParameterizedType)) {
                Type valueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

                ArrayList newList = ArrayList.class.newInstance();

                List<Map<String, Object>> values = ((List<Map<String, Object>>) entry.getValue());
                for (Map<String, Object> valueMap : values) {
                    Object newInstance = Class.forName(valueType.getTypeName()).newInstance();
                    setObject(newInstance, valueMap);

                    newList.add(newInstance);
                }

                field.set(object, newList);
            } else {
                field.set(object, entry.getValue());
            }

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
