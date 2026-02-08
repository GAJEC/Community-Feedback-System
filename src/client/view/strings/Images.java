package client.view.strings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class Images {

    private Images() {} // prevent instantiation

    public static final URL LOGO_URL = Images.class.getResource("/assets/logo_icon.png");
    public static final URL ICON_URL = Images.class.getResource("/assets/app_icon.png");

    public static final ImageIcon LOGO = new ImageIcon(LOGO_URL);
    public static final ImageIcon ICON = new ImageIcon(ICON_URL);

    /**
     * Returns a scaled ImageIcon from a given URL
     */
    public static ImageIcon scaled(URL url, int width, int height) {
        if (url == null) return null;
        Image img = new ImageIcon(url).getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Shortcut for LOGO scaled
     */
    public static ImageIcon scaledLogo(int width, int height) {
        return scaled(LOGO_URL, width, height);
    }

    /**
     * Shortcut for ICON scaled
     */
    public static ImageIcon scaledIcon(int width, int height) {
        return scaled(ICON_URL, width, height);
    }
}