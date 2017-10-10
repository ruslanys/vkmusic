package me.ruslanys.vkmusic.util;

import lombok.SneakyThrows;

import java.awt.*;
import java.net.URI;
import java.net.URL;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class DesktopUtils {

    private static final Desktop DESKTOP = Desktop.getDesktop();

    private DesktopUtils() {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    public static void browse(URI uri) {
        DESKTOP.browse(uri);
    }

    @SneakyThrows
    public static void browse(URL url) {
        browse(url.toURI());
    }

}
