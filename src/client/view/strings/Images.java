package client.view.strings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class Images {

    private Images() {} // prevent instantiation

    // Tinry ko kase yung bat, eto naman para sa images. Ginawa ko na static final para parang constants na siya, tapos may helper method para sa scaling. Yung resource method is to handle the case na hindi mahanap yung image sa isang path, so it tries multiple paths (e.g. for different build setups). Yung safeIcon naman is to return an empty icon instead of null if hindi mahanap yung image, para maiwasan yung null pointer exceptions sa UI.
    private static URL resource(String... paths) {
        for (String path : paths) {
            URL url = Images.class.getResource(path);
            if (url != null) return url;
        }
        return null;
    }

    private static ImageIcon safeIcon(URL url) {
        return (url == null) ? new ImageIcon() : new ImageIcon(url);
    }

    // Instances
    // Logo
    public static final URL LOGO_URL = resource("/assets/logo_icon.png", "/res/assets/logo_icon.png");
    public static final ImageIcon LOGO = safeIcon(LOGO_URL);
    public static final URL ICON_URL = resource("/assets/app_icon.png", "/res/assets/app_icon.png");
    public static final ImageIcon ICON = safeIcon(ICON_URL);

    // Others
    public static final URL PERSON_URL = resource("/assets/username_icon.png", "/res/assets/username_icon.png");
    public static final ImageIcon PERSON = safeIcon(PERSON_URL);
    public static final URL LOCK_URL = resource("/assets/password_icon.png", "/res/assets/password_icon.png");
    public static final ImageIcon LOCK = safeIcon(LOCK_URL);

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