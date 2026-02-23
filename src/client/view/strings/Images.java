package client.view.strings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class Images {

    private Images() {} // prevent instantiation

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

    // Logo
    private static final URL LOGO_URL = resource("/assets/logo_icon.png", "/res/assets/logo_icon.png");
    public static final ImageIcon LOGO = safeIcon(LOGO_URL);

    private static final URL ICON_URL = resource("/assets/app_icon.png", "/res/assets/app_icon.png");
    public static final ImageIcon ICON = safeIcon(ICON_URL);

    // Others
    private static final URL NULL_URL = resource("/assets/null_icon.png", "/res/assets/null_icon.png");
    public static final ImageIcon NULL = safeIcon(NULL_URL);

    private static final URL USERNAME_URL = resource("/assets/username_icon.png", "/res/assets/username_icon.png");
    public static final ImageIcon USERNAME = safeIcon(USERNAME_URL);

    private static final URL LOCK_URL = resource("/assets/password_icon.png", "/res/assets/password_icon.png");
    public static final ImageIcon LOCK = safeIcon(LOCK_URL);

    private static final URL PROFILE_URL = resource("/assets/profile_icon.png", "/res/assets/profile_icon.png");
    public static final ImageIcon PROFILE = safeIcon(PROFILE_URL);

    private static final URL PROFILE2_URL = resource("/assets/username_icon.png", "/res/assets/username_icon.png");
    public static final ImageIcon PROFILE2 = safeIcon(PROFILE2_URL);

    private static final URL EMAIL_URL = resource("/assets/email_icon.png", "/res/assets/email_icon.png");
    public static final ImageIcon EMAIL = safeIcon(EMAIL_URL);

    private static final URL PHONE_URL = resource("/assets/phone_icon.png", "/res/assets/phone_icon.png");
    public static final ImageIcon PHONE = safeIcon(PHONE_URL);

    private static final URL LOCATION_URL = resource("/assets/location_icon.png", "/res/assets/location_icon.png");
    public static final ImageIcon LOCATION = safeIcon(LOCATION_URL);

    private static final URL LOGOUT_URL = resource("/assets/logout_icon.png", "/res/assets/logout_icon.png");
    public static final ImageIcon LOGOUT = safeIcon(LOGOUT_URL);

    private static final URL SEARCH_URL = resource("/assets/search_icon.png", "/res/assets/search_icon.png");
    public static final ImageIcon SEARCH = safeIcon(SEARCH_URL);

    private static final URL SORT_URL = resource("/assets/sort_icon.png", "/res/assets/sort_icon.png");
    public static final ImageIcon SORT = safeIcon(SORT_URL);

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

    public static ImageIcon scaled(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}