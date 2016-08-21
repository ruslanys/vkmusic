package me.ruslanys.vkaudiosaver.exceptions;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class ApiParamsMissingException extends Exception {

    public ApiParamsMissingException() {
        super(
                "API access key must be present in the API mode.\r\n\r\n"
                + "Follow the link below to get it.\r\n"
                + "https://oauth.vk.com/authorize?client_id=5592490&redirect_uri=https://oauth.vk.com/blank.html&response_type=token&display=mobile&scope=65544"
        );
    }

}