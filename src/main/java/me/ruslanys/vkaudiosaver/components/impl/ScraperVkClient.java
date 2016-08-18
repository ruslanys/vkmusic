package me.ruslanys.vkaudiosaver.components.impl;

import com.google.common.collect.ImmutableMap;
import com.jcodelab.http.HttpClient;
import com.jcodelab.http.Response;
import lombok.Data;
import lombok.SneakyThrows;
import me.ruslanys.vkaudiosaver.components.VkClient;
import me.ruslanys.vkaudiosaver.domain.vk.VkAudioResponse;
import me.ruslanys.vkaudiosaver.exceptions.VkException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class ScraperVkClient extends HttpClient implements VkClient {

    private final String username;
    private final String password;

    @Autowired
    public ScraperVkClient(@Value("${vk.username}") String username, @Value("${vk.password}") String password) {
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void login() throws IOException {
        Map<String, String> loginForm = getLoginForm();
        Response<String> response = sendPostForString(loginForm.get("action"), loginForm, ImmutableMap.of("referer", "https://vk.com/", "origin", "https://vk.com"));
        FileCopyUtils.copy(response.getData(), new BufferedWriter(new FileWriter("/home/ruslanys/vk.html")));
    }

    @Override
    public VkAudioResponse getAudio() throws VkException {
        return null;
    }

    @SneakyThrows
    private Map<String, String> getLoginForm() {
        Response<String> response = sendGetForString("https://vk.com");
        Document document = Jsoup.parse(response.getData());
        Element element = document.getElementById("index_login_form");

        Map<String, String> loginForm = new HashMap<>();
        loginForm.put("action", element.attr("action"));



        for (Node node : element.childNodes()) {
            if (!(node instanceof Element)) continue;
            Element childElement = (Element) node;

            loginForm.put(childElement.attr("name"), childElement.attr("value"));
        }

        loginForm.put("email", username);
        loginForm.put("pass", password);
        return loginForm;
    }

    @Data
    private static class LoginForm {

        private String action;

        private String act;
        private String role;
        private String expire;
        private String _origin;
        private String ip_h;
        private String lg_h;

        private String email;
        private String pass;

        LoginForm(String action) {
            this.action = action;
        }

    }

}
