package me.ruslanys.vkmusic.util;

import javafx.scene.image.Image;

import java.io.InputStream;

public class IconUtils {

    public static Image getLoadingIcon() {
        InputStream resource = IconUtils.class.getClassLoader().getResourceAsStream("images/loading-heart.gif");
        Image image = new Image(resource);

        return image;
    }

}
