package client.view.strings;

import java.awt.*;

public class Fonts {
    // Style: 0=plain, 1=bold, 2=italic
    public static Font SansSerif(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }
    public static final Font LOGO = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
}
