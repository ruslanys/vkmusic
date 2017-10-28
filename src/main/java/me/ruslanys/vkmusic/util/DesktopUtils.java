package me.ruslanys.vkmusic.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkmusic.Application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j
public class DesktopUtils {

    private static final Image ICON;
    private static final Desktop DESKTOP = Desktop.getDesktop();

    static {
        Image image;
        try {
            URL resource = Application.class.getClassLoader().getResource("images/icon.png");
            image = ImageIO.read(resource);
        } catch (IOException e) {
            log.error("Can not load an application icon", e);
            image = null;
        }
        ICON = image;
    }

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

    public static Image getIcon() {
        return ICON;
    }

}
