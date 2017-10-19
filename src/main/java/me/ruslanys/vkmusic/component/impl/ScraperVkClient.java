package me.ruslanys.vkmusic.component.impl;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.component.VkClient;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.exception.VkException;
import me.ruslanys.vkmusic.util.JsonUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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
public class ScraperVkClient implements VkClient {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
    private static final String PATH_BASE = "https://vk.com";
    
    private static final String FORM_ACTION_KEY = "action";
    private static final String JSON_DELIMITER = "<!json>";
    
    private static final int SLEEP_INTERVAL = 5_000;

    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    private final Map<String, String> cookies = new HashMap<>();

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

    private void handleCookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);

        Set<Map.Entry<String, String>> entries = cookies.entrySet();
        entries.removeIf(entry -> entry.getKey().isEmpty());
        entries.removeIf(entry -> entry.getValue().isEmpty());
        entries.removeIf(entry -> "DELETED".equals(entry.getValue()));
    }

    private void handleCookies(Connection.Response response) {
        handleCookies(response.cookies());
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

    /**
     * @deprecated This method is unsecured and not reliable because of ReCaptcha on the VK side.
     */
    @Deprecated
    @SneakyThrows
    private String submitLoginForm(String username, String password) throws VkException {
        log.info("Signing in with {}...", username);

        Map<String, String> loginForm = getLoginForm();
        loginForm.put("email", username);
        loginForm.put("pass", password);

        Connection connection = Jsoup.connect(loginForm.get(FORM_ACTION_KEY))
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.POST)
                .data(loginForm);
        Connection.Response response = connection.execute();
        handleCookies(response);

        Matcher matcher = Pattern.compile("parent.onLoginDone\\('(.+)'\\)").matcher(response.body());
        if (!matcher.find()) {
            throw new VkException("Не удалось пройти авторизацию.");
        }
        String path = matcher.group(1);
        log.info("Home page path: {}", path);

        return path;
    }

    /**
     * @deprecated This method is unsecured and not reliable because of ReCaptcha on the VK side.
     */
    @Deprecated
    @SneakyThrows
    private Map<String, String> getLoginForm() {
        Connection.Response response = Jsoup.connect(PATH_BASE)
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.GET)
                .execute();

        handleCookies(response);

        Document document = response.parse();
        Element element = document.getElementById("quick_login_form");

        Map<String, String> loginForm = new HashMap<>();
        loginForm.put(FORM_ACTION_KEY, element.attr(FORM_ACTION_KEY));

        for (Node node : element.childNodes()) {
            if (!(node instanceof Element)) continue;
            Element childElement = (Element) node;

            String key = childElement.attr("name");
            String value = childElement.attr("value");
            if (!key.isEmpty()) {
                loginForm.put(key, value);
            }
        }

        return loginForm;
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
    public void fetchUrls(List<Audio> audioList) {
        Map<Long, Audio> audioMap = new HashMap<>(audioList.size());
        for (Audio audio : audioList) {
            audioMap.put(audio.getId(), audio);
        }

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
                url = decrypt(url);

                audio.setUrl(url);
            }

            // sleeping
            Thread.sleep(200);
            fromIndex = toIndex;
            toIndex = Math.min(fromIndex + 10, audioList.size());
        }
    }

    @SneakyThrows
    private String decrypt(String url) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("decrypt.js")))) {
            scriptEngine.eval(bufferedReader);

            Invocable inv = (Invocable) scriptEngine;
            return (String) inv.invokeFunction("decode", url);
        }
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

        public boolean hasMore() {
            return Integer.valueOf(1).equals(hasMore);
        }
    }

}
