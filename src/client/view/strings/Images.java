package client.view.strings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class Images {

    private Images() {} // prevent instantiation

    // Instances
    // Logo
    public static final URL LOGO_URL = Images.class.getResource("/assets/logo_icon.png");
    public static final ImageIcon LOGO = new ImageIcon(LOGO_URL);
    public static final URL ICON_URL = Images.class.getResource("/assets/app_icon.png");
    public static final ImageIcon ICON = new ImageIcon(ICON_URL);

    // Others
    public static final URL PERSON_URL = Images.class.getResource("/assets/username_icon.png");
    public static final ImageIcon PERSON = new ImageIcon(PERSON_URL);
    public static final URL LOCK_URL = Images.class.getResource("/assets/password_icon.png");
    public static final ImageIcon LOCK = new ImageIcon(LOCK_URL);

    // Methods
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

    // Others
    // Large - Info
    public static ImageIcon bigPerson(int width, int height) {
        return scaled(LOGO_URL, width, height);
    }

    // Medium - Text Fields
    public static ImageIcon mediumPerson() { // Used for Username login
        return scaled(PERSON_URL, 20, 20);
    }
    public static ImageIcon mediumLock() { // Used for password login
        return scaled(LOCK_URL, 20, 20);
    }

    // Small - Normal texts
    public static ImageIcon smallSearch(int width, int height) {
        return scaled(LOGO_URL, width, height);
    }
    public static ImageIcon smallCategory(int width, int height) {
        return scaled(LOGO_URL, width, height);
    }
    public static ImageIcon smallStatus(int width, int height) {
        return scaled(LOGO_URL, width, height);
    }
    public static ImageIcon smallSort(int width, int height) {
        return scaled(LOGO_URL, width, height);
    }

    // Common
    /**
     * Returns a scaled ImageIcon from a given URL
     */
    public static ImageIcon scaled(URL url, int width, int height) {
        if (url == null) return null;
        Image img = new ImageIcon(url).getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static ImageIcon scaled(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}