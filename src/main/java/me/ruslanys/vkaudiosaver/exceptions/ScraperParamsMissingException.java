package me.ruslanys.vkaudiosaver.exceptions;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class ScraperParamsMissingException extends Exception {

    public ScraperParamsMissingException() {
        super("VK username and password must present in the scraper mode.");
    }
}
