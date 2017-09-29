package me.ruslanys.vkaudiosaver.components.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jcodelab.http.HttpClient;
import com.jcodelab.http.Response;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.domain.vk.VkAudioResponse;
import me.ruslanys.vkaudiosaver.domain.vk.VkError;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import me.ruslanys.vkaudiosaver.properties.VkProperties;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class ScraperVkClient extends HttpClient implements VkClient {

    private static final String PATH_BASE = "https://vk.com";

    private final ObjectMapper mapper;
    private final ScriptEngine scriptEngine;
//    private final Random random = new Random(); // toIndex += 1 + random.nextInt(10);

    private String homePage;
    private Long userId;

    @Autowired
    public ScraperVkClient(ObjectMapper mapper) {
        this.mapper = mapper;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @SneakyThrows
    @Override
    public void init(VkProperties vkProperties) throws VkException {
        Map<String, String> loginForm = getLoginForm();
        loginForm.put("email", vkProperties.getUsername());
        loginForm.put("pass", vkProperties.getPassword());

        Response<String> authResponse = sendPostForString(loginForm.get("action"), loginForm);


        Response<String> response;
        Matcher matcher;

        // fetching home page
        response = sendGetForString(authResponse.getFirstHeader("Location").getValue());

        matcher = Pattern.compile("parent.onLoginDone\\('(.+)'\\)").matcher(response.getData());
        if (!matcher.find()) {
            throw new VkException(new VkError(null, "Didn't authorized"));
        }
        homePage = matcher.group(1);

        // fetching user id
        response = sendGetForString(PATH_BASE + homePage);

        matcher = Pattern.compile("id: (\\d+)").matcher(response.getData());
        if (!matcher.find()) {
            throw new VkException(new VkError(null, "Couldn't fetch user ID"));
        }
        userId = Long.valueOf(matcher.group(1));
    }

    @SneakyThrows
    @Override
    public VkAudioResponse getAudio() throws VkException {
        List<Audio> audios = new ArrayList<>();

        Response<String> response;
        String json;

        // fetching audio list
        response = sendPostForString(
                PATH_BASE + "/al_audio.php",
                ImmutableMap.<String, String>builder()
                        .put("access_hash", "")
                        .put("act", "load_section")
                        .put("al", "1")
                        .put("claim", "0")
                        .put("offset", "1")
                        .put("owner_id", userId.toString())
                        .put("playlist_id", "-1")
                        .put("type", "playlist")
                        .build()
        );
        json = response.getData();
        json = json.substring(json.indexOf("<!json>") + "<!json>".length());

        VkAudioDto audioJson = mapping(json, VkAudioDto.class);
        for (List<String> strings : audioJson.getList()) {
            Audio audio = new Audio();
            audio.setId(Integer.valueOf(strings.get(0)));
            audio.setArtist(StringEscapeUtils.unescapeHtml4(strings.get(4)));
            audio.setTitle(StringEscapeUtils.unescapeHtml4(strings.get(3)));
            audio.setDuration(Integer.valueOf(strings.get(5)));

            audios.add(audio);
        }
        Map<Integer, Audio> audioMap = new LinkedHashMap<>();
        for (Audio audio : audios) {
            audioMap.put(audio.getId(), audio);
        }
        log.info("Audio collection size: {}", audios.size());

        // fetching urls
        int fromIndex = 0;
        int toIndex = Math.min(fromIndex + 10, audios.size());
        int sleep = 5_000;

        while (fromIndex != toIndex) {
            log.info("fromIndex {}, toIndex {}, sleep {}", fromIndex, toIndex, sleep);

            // making request
            String ids = StringUtils.join(
                    audios.subList(fromIndex, toIndex).stream()
                            .map(audio -> userId + "_" + String.valueOf(audio.getId()))
                            .collect(Collectors.toList()),
                    ","
            );
            response = sendPostForString(
                    PATH_BASE + "/al_audio.php",
                    ImmutableMap.of(
                            "act", "reload_audio",
                            "al", "1",
                            "ids", ids
                    )
            );

            json = response.getData();
            log.debug("json: {}", json);

            // parsing json
            if (!json.contains("<!json>")) {
                Thread.sleep(sleep);
                sleep += 5_000;
                continue;
            }

            json = json.substring(json.indexOf("<!json>") + "<!json>".length());
            json = json.substring(0, json.indexOf("<!>"));
            List<List> lists = mapping(json, List.class);

            // models mapping
            for (int i = fromIndex, k = 0; i < toIndex && k < lists.size(); i++, k++) {
                List obj = lists.get(k);
                Audio audio = audioMap.get(obj.get(0));
                String url = (String) obj.get(2);
                url = decrypt(url);

                audio.setUrl(url);
            }

            // increment borders
            fromIndex = toIndex;
            toIndex = Math.min(fromIndex + 10, audios.size());
            sleep = 5_000;

            // sleeping
            Thread.sleep(200);
        }

        audios.removeIf(audio -> audio.getUrl() == null);

        // --
        return new VkAudioResponse(audios.size(), audios);
    }

    @SneakyThrows
    private String decrypt(String url) {
        // read script file
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("decrypt.js")));
        scriptEngine.eval(bufferedReader);

        Invocable inv = (Invocable) scriptEngine;
// call function from script file
        return (String) inv.invokeFunction("decode", url);
    }

    @SneakyThrows
    private Map<String, String> getLoginForm() {
        Response<String> response = sendGetForString(PATH_BASE);
        Document document = Jsoup.parse(response.getData());
        Element element = document.getElementById("quick_login_form");

        Map<String, String> loginForm = new HashMap<>();
        loginForm.put("action", element.attr("action"));

        for (Node node : element.childNodes()) {
            if (!(node instanceof Element)) continue;
            Element childElement = (Element) node;

            loginForm.put(childElement.attr("name"), childElement.attr("value"));
        }

        return loginForm;
    }

    @SneakyThrows
    private <T> T mapping(String content, Class<T> clazz) {
        return mapper.readValue(content, clazz);
    }

    @SneakyThrows
    private <T> T mapping(String content, TypeReference<T> type) {
        return mapper.readValue(content, type);
    }

    @Data
    private static class VkAudioDto {
        private String type;
        private Long ownerId;
        private Integer albumId;
        private String title;
        private Boolean hasMore;
        private Integer nextOffset;
        private Integer totalCount;

        private List<List<String>> list;
    }

}
