package me.ruslanys.vkmusic.component.impl;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.exception.VkException;
import me.ruslanys.vkmusic.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class ScraperVkClient implements VkClient {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
    private static final String PATH_BASE = "https://vk.com";
    
    private static final String JSON_DELIMITER = "<!json>";
    
    private static final int SLEEP_INTERVAL = 5_000;

    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    private final String script;

    private final Map<String, String> cookies = new HashMap<>();


    public ScraperVkClient() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("decrypt.js")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        script = sb.toString();
    }

    @Override
    public void addCookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    @Override
    public void setCookies(Map<String, String> cookies) {
        clearCookies();
        addCookies(cookies);
    }

    @Override
    public void clearCookies() {
        this.cookies.clear();
    }

    @Override
    @SneakyThrows
    public Long fetchUserId() {
        Connection.Response response = Jsoup.connect(PATH_BASE)
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.GET)
                .execute();
        Matcher matcher = Pattern.compile("id: (\\d+)").matcher(response.body());
        if (!matcher.find() || "0".equals(matcher.group(1).trim())) {
            throw new VkException("Не удалось получить ID пользователя.");
        }
        Long id = Long.valueOf(matcher.group(1));
        log.info("User ID: {}", id);

        return id;
    }

    @SneakyThrows
    @Override
    public List<Audio> getAudio(Long ownerId) {
        VkAudioDto audioDto;
        List<Audio> list = new ArrayList<>();
        int offset = 0;
        do {
            Connection.Response response = Jsoup.connect(PATH_BASE + "/al_audio.php")
                    .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.POST)
                    .data("access_hash", "")
                    .data("act", "load_section")
                    .data("al", "1")
                    .data("claim", "0")
                    .data("offset", String.valueOf(offset))
                    .data("owner_id", ownerId.toString())
                    .data("playlist_id", "-1")
                    .data("type", "playlist")
                    .execute();

            String body = response.body();
            String json = body.substring(body.indexOf(JSON_DELIMITER) + JSON_DELIMITER.length());
            json = json.substring(0, json.indexOf("<!>"));

            audioDto = JsonUtils.fromString(json, VkAudioDto.class);
            for (List values : audioDto.getList()) {
                Audio audio = new Audio(
                        ((Number) values.get(0)).longValue(),
                        ((Number) values.get(1)).longValue(),
                        StringEscapeUtils.unescapeHtml4((String) values.get(4)),
                        StringEscapeUtils.unescapeHtml4((String) values.get(3)),
                        (Integer) values.get(5)
                );

                list.add(audio);
            }
            offset = audioDto.getNextOffset();
        } while (audioDto.hasMore());

        log.debug("Total count: {}", audioDto.getTotalCount());
        log.debug("Fetched audio collection: {}", list.size());
        return list;
    }

    @SneakyThrows
    @Override
    public void fetchUrls(List<Audio> audioList) { // TODO: refactor
        Map<Long, Audio> audioMap = new HashMap<>(audioList.size());
        for (Audio audio : audioList) {
            audioMap.put(audio.getId(), audio);
        }

        Long userId = fetchUserId();
        int sleepInterval = SLEEP_INTERVAL;
        int fromIndex = 0;
        int toIndex = Math.min(fromIndex + 10, audioList.size());

        while (fromIndex != toIndex) {
            log.debug("Fetching urls: {} - {}", fromIndex, toIndex);

            // making request
            String ids = StringUtils.join(
                    audioList.subList(fromIndex, toIndex).stream()
                            .map(audio -> audio.getOwnerId() + "_" + audio.getId())
                            .collect(Collectors.toList()),
                    ","
            );

            Connection.Response response = Jsoup.connect(PATH_BASE + "/al_audio.php")
                    .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.POST)
                    .data("act", "reload_audio")
                    .data("al", "1")
                    .data("ids", ids)
                    .execute();

            String body = response.body();
            if (!body.contains(JSON_DELIMITER)) {
                log.info("Sleeping {} sec...", sleepInterval / 1000);
                Thread.sleep(sleepInterval);
                sleepInterval += SLEEP_INTERVAL;
                continue;
            } else if (sleepInterval != SLEEP_INTERVAL) {
                sleepInterval = SLEEP_INTERVAL;
            }

            String json = body.substring(body.indexOf(JSON_DELIMITER) + JSON_DELIMITER.length());
            json = json.substring(0, json.indexOf("<!>"));
            List<List> lists = JsonUtils.fromString(json, List.class);

            // models mapping
            for (List object : lists) {
                Audio audio = audioMap.get(((Number) object.get(0)).longValue());
                String url = (String) object.get(2);
                url = decrypt(userId, url);

                audio.setUrl(url);
            }

            // sleeping
            Thread.sleep(200);
            fromIndex = toIndex;
            toIndex = Math.min(fromIndex + 10, audioList.size());
        }
    }

    @SneakyThrows
    private String decrypt(Long vkId, String url) {
        String script = this.script.replace("${vkId}", vkId.toString()); // TODO: replace with bindings
        scriptEngine.eval(script);

        Invocable inv = (Invocable) scriptEngine;
        return (String) inv.invokeFunction("decode", url);
    }

    @Data
    private static class VkAudioDto {
        private String type;
        private Long ownerId;
        private Integer albumId;
        private String title;
        private Integer hasMore;
        private Integer nextOffset;
        private Integer totalCount;

        private List<List> list;

        boolean hasMore() {
            return Integer.valueOf(1).equals(hasMore);
        }
    }

}
