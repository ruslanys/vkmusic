package me.ruslanys.vkaudiosaver.exceptions;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class DestinationMissingException extends Exception {

    public DestinationMissingException() {
        super("Destination folder is missing");
    }
}
