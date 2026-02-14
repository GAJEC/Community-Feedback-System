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

    // Logo
    public static final URL LOGO_URL = resource("/assets/logo_icon.png", "/res/assets/logo_icon.png");
    public static final ImageIcon LOGO = safeIcon(LOGO_URL);

    public static final URL ICON_URL = resource("/assets/app_icon.png", "/res/assets/app_icon.png");
    public static final ImageIcon ICON = safeIcon(ICON_URL);

    // Others
    public static final URL USERNAME_URL = resource("/assets/username_icon.png", "/res/assets/username_icon.png");
    public static final ImageIcon USERNAME = safeIcon(USERNAME_URL);

    public static final URL LOCK_URL = resource("/assets/password_icon.png", "/res/assets/password_icon.png");
    public static final ImageIcon LOCK = safeIcon(LOCK_URL);

    public static final URL PROFILE_URL = resource("/assets/profile_icon.png", "/res/assets/profile_icon.png");
    public static final ImageIcon PROFILE = safeIcon(PROFILE_URL);

    public static final URL EMAIL_URL = resource("/assets/email_icon.png", "/res/assets/email_icon.png");
    public static final ImageIcon EMAIL = safeIcon(EMAIL_URL);

    public static final URL PHONE_URL = resource("/assets/phone_icon.png", "/res/assets/phone_icon.png");
    public static final ImageIcon PHONE = safeIcon(PHONE_URL);

    public static final URL LOCATION_URL = resource("/assets/location_icon.png", "/res/assets/location_icon.png");
    public static final ImageIcon LOCATION = safeIcon(LOCATION_URL);

    public static final URL LOGOUT_URL = resource("/assets/logout_icon.png", "/res/assets/logout_icon.png");
    public static final ImageIcon LOGOUT = safeIcon(LOGOUT_URL);

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

    private static ImageIcon scaled(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}