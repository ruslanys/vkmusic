package me.ruslanys.vkaudiosaver.components.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jcodelab.http.HttpClient;
import lombok.SneakyThrows;
import me.ruslanys.vkaudiosaver.components.VkApi;
import me.ruslanys.vkaudiosaver.domain.vk.VkAudioResponse;
import me.ruslanys.vkaudiosaver.domain.vk.VkResponse;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class DefaultVkApi extends HttpClient implements VkApi {

    private static final String PATH_BASE = "https://api.vk.com/method/";
    private static final String VERSION = "5.53";

    private final String accessToken;
    private final ObjectMapper mapper;

    @Autowired
    public DefaultVkApi(@Value("${vk.access-key}") String accessToken, ObjectMapper mapper) {
        this.accessToken = accessToken;
        this.mapper = mapper;
    }

    @Override
    public VkAudioResponse getAudio() throws VkException {
        return doRequest("audio.get", VkAudioResponse.class);
    }

    private <T extends VkResponse> T doRequest(String methodName, Class<T> clazz) throws VkException {
        return doRequest(methodName, Collections.emptyMap(), clazz);
    }

    private <T extends VkResponse> T doRequest(String methodName, Map<String, String> params, Class<T> clazz) throws VkException {
        String json = apiCall(methodName, params);
        T response = mapping(json, clazz);
        if (response.hasError()) {
            response.throwVkException();
        }

        return response;
    }

    @SneakyThrows
    private String apiCall(String methodName, Map<String, String> params) {
        return sendPostForString(
                PATH_BASE + methodName,
                ImmutableMap.<String, String>builder()
                        .putAll(params)
                        .put("access_token", accessToken)
                        .put("v", VERSION)
                        .build()
        ).getData();
    }

    @SneakyThrows
    private <T> T mapping(String content, Class<T> clazz) {
        return mapper.readValue(content, clazz);
    }

}
