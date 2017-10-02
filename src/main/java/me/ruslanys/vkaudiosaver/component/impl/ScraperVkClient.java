package me.ruslanys.vkaudiosaver.component.impl;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.component.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.exception.VkException;
import me.ruslanys.vkaudiosaver.property.VkProperties;
import me.ruslanys.vkaudiosaver.util.JsonUtils;
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
    private static final int DEFAULT_INTERVAL = 5_000;

    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");;

    private final Map<String, String> cookies = new HashMap<>();

    private String homePage;
    private Long userId;


    @SneakyThrows
    @Override
    public void auth(VkProperties properties) throws VkException {
        cookies.clear();

        homePage = submitLoginForm(properties.getUsername(), properties.getPassword());
        userId = fetchUserId();
    }

    private void handleCookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);

        this.cookies.entrySet().removeIf(entry -> entry.getKey().isEmpty());
        this.cookies.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        this.cookies.entrySet().removeIf(entry -> "DELETED".equals(entry.getValue()));
    }

    private void handleCookies(Connection.Response response) {
        handleCookies(response.cookies());
    }

    @SneakyThrows
    private Long fetchUserId() {
        Connection.Response response = Jsoup.connect(PATH_BASE + homePage)
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.GET)
                .execute();
        Matcher matcher = Pattern.compile("id: (\\d+)").matcher(response.body());
        if (!matcher.find()) {
            throw new VkException("Не удалось получить ID пользователя.");
        }
        Long id = Long.valueOf(matcher.group(1));
        log.info("User ID: {}", id);

        return id;
    }

    @SneakyThrows
    private String submitLoginForm(String username, String password) throws VkException {
        log.info("Signing in with {}...", username);

        Map<String, String> loginForm = getLoginForm();
        loginForm.put("email", username);
        loginForm.put("pass", password);

        Connection connection = Jsoup.connect(loginForm.get("action"))
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

    @SneakyThrows
    private Map<String, String> getLoginForm() {
        Connection.Response response = Jsoup.connect(PATH_BASE)
                .userAgent(USER_AGENT).cookies(cookies).method(Connection.Method.GET)
                .execute();

        handleCookies(response);

        Document document = response.parse();
        Element element = document.getElementById("quick_login_form");

        Map<String, String> loginForm = new HashMap<>();
        loginForm.put("action", element.attr("action"));

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
    public List<Audio> getAudio() {
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
                    .data("owner_id", userId.toString())
                    .data("playlist_id", "-1")
                    .data("type", "playlist")
                    .execute();

            String body = response.body();
            String json = body.substring(body.indexOf("<!json>") + "<!json>".length());
            json = json.substring(0, json.indexOf("<!>"));

            audioDto = JsonUtils.fromString(json, VkAudioDto.class);
            for (List values : audioDto.getList()) {
                Audio audio = new Audio();
                audio.setId((Integer) values.get(0));
                audio.setArtist(StringEscapeUtils.unescapeHtml4((String) values.get(4)));
                audio.setTitle(StringEscapeUtils.unescapeHtml4((String) values.get(3)));
                audio.setDuration((Integer) values.get(5));

                list.add(audio);
            }
            offset = audioDto.getNextOffset();
        } while (audioDto.hasMore());

        log.info("Total count: {}", audioDto.getTotalCount());
        log.info("Fetched audio collection: {}", list.size());
        return list;
    }

    @Override
    public void getUrls(List<Audio> audioList) throws IOException, InterruptedException {
        Map<Integer, Audio> audioMap = new HashMap<>();
        for (Audio audio : audioList) {
            audioMap.put(audio.getId(), audio);
        }

        int interval = DEFAULT_INTERVAL;
        for (int fromIndex = 0, toIndex = Math.min(fromIndex + 10, audioList.size());
             fromIndex != toIndex;
             fromIndex = toIndex, toIndex = Math.min(fromIndex + 10, audioList.size())) {

            log.info("Fetching urls: {} - {}", fromIndex, toIndex);

            // making request
            String ids = StringUtils.join(
                    audioList.subList(fromIndex, toIndex).stream()
                            .map(audio -> userId + "_" + audio.getId())
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
            if (!body.contains("<!json>")) {
                log.info("Sleeping {} sec...", interval / 1000);
                Thread.sleep(interval);
                interval *= 2;
                continue;
            } else if (interval != DEFAULT_INTERVAL) {
                interval = DEFAULT_INTERVAL;
            }

            String json = body.substring(body.indexOf("<!json>") + "<!json>".length());
            json = json.substring(0, json.indexOf("<!>"));
            List<List> lists = JsonUtils.fromString(json, List.class);

            // models mapping
            for (List object : lists) {
                Audio audio = audioMap.get(object.get(0));
                String url = (String) object.get(2);
                url = decrypt(url);

                audio.setUrl(url);
            }

            // sleeping
            Thread.sleep(200);
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
