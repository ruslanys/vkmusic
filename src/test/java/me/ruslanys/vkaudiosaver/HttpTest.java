package me.ruslanys.vkaudiosaver;

import com.jcodelab.http.HttpClient;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class HttpTest {

    @Test
    public void test() throws IOException {
        HttpClient httpClient = new HttpClient();
        String html = httpClient.sendGetForString("https://vk.com").getData();

        Map<String, String> authParams = new HashMap<>();
        authParams.put("email", "ruslanys@gmail.com");
        authParams.put("pass", "ES2D@P47k#Zz");

        System.out.println("***********************");

        FormData formData1 = getFormParams(html, "quick_login_form", authParams);
        FormData formData2 = getFormParams(html, "index_login_form", authParams);

        if (!formData1.getAction().equals(formData2.getAction())) {
            System.out.println("Action mismatched");
        }

        for (Map.Entry<String, String> entry1 : formData1.getParams().entrySet()) {

            boolean found = false;
            for (Map.Entry<String, String> entry2 : formData2.getParams().entrySet()) {
                if (!entry2.getKey().equals(entry1.getKey())) continue;

                found = true;
                if (!entry2.getValue().equals(entry1.getValue())) {
                    System.out.println("Param [" + entry1.getKey() + "] mismatched: #1_" + entry1.getValue() + ", #2_" + entry2.getValue());
                }
                break;
            }

            if (!found) {
                System.out.println("Not exist [" + entry1.getKey() + "] = " + entry1.getValue());
                formData2.getParams().put(entry1.getKey(), entry1.getValue());
            }
        }

        String authUrl1 = httpClient.sendPostForString(formData1.getAction(), formData1.getParams())
                .getFirstHeader("Location")
                .getValue();

        String authUrl2 = httpClient.sendPostForString(formData2.getAction(), formData2.getParams())
                .getFirstHeader("Location")
                .getValue();

        System.out.println("Url #1" + authUrl1);
        System.out.println("Url #2" + authUrl2);
    }

    private FormData getFormParams(String html, String formId, Map<String, String> params) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(html);

        FormData formData = new FormData();
        Element form = doc.getElementById(formId);
        formData.setAction(form.attributes().get("action"));

        Elements inputElements = form.getElementsByTag("input");

//        List<NameValuePair> paramList = new ArrayList<>();
        Map<String, String> paramList = new HashMap<>();

        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (params.get(key) != null) {
                value = params.get(key);
            }

            paramList.put(key, value);
        }

        formData.setParams(paramList);

        return formData;
    }

    @Data
    private class FormData {
        private String action;
        private Map<String, String> params;
    }

}
