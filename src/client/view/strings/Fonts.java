package client.view.strings;

import java.awt.*;

public class Fonts {
    // Style: 0=plain, 1=bold, 2=italic

    // Sans Serif
    public static Font SansSerif(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }
    public static final Font SANS_TITLE = SansSerif(Font.BOLD, 30); // Welcome to Community Feedback! and Sign In
    public static final Font SANS_TITLE_2 = SansSerif(Font.BOLD, 26);
    public static final Font SANS_SUB_TITLE = SansSerif(Font.PLAIN, 20); // Keep connected with us
    public static final Font SANS_HEADING_1 = SansSerif(Font.PLAIN, 18);
    public static final Font SANS_HEADING_2 = SansSerif(Font.PLAIN, 16);
    public static final Font SANS_HEADING_3 = SansSerif(Font.PLAIN, 14);
    public static final Font SANS_NORMAL_TEXT = SansSerif(Font.PLAIN, 12);


    // Segoe UI
    public static Font SegoeUi(int style, int size) {
        return new Font("Segoe UI", style, size);
    }
    public static final Font UI_TITLE = SegoeUi(Font.BOLD, 30); // Welcome to Community Feedback! and Sign In
    public static final Font UI_TITLE_2 = SegoeUi(Font.BOLD, 26);
    public static final Font UI_SUB_TITLE = SegoeUi(Font.PLAIN, 20); // Keep connected with us
    public static final Font UI_HEADING_1 = SegoeUi(Font.BOLD, 18);
    public static final Font UI_HEADING_2 = SegoeUi(Font.BOLD, 16);
    public static final Font UI_HEADING_3 = SegoeUi(Font.BOLD, 14);
    public static final Font UI_HEADING_4 = SegoeUi(Font.ITALIC, 14);
    public static final Font UI_NORMAL_TEXT = SegoeUi(Font.PLAIN, 12);
    public static final Font UI_SUB_TEXT = SegoeUi(Font.PLAIN, 10);

    // Inter
    public static Font Inter(int style, int size) {
        return new Font("Inter", style, size);
    }
    public static final Font INTER_TITLE = Inter(Font.BOLD, 30); // Welcome to Community Feedback! and Sign In
    public static final Font INTER_TITLE_2 = Inter(Font.BOLD, 26);
    public static final Font INTER_SUB_TITLE = Inter(Font.PLAIN, 20); // Keep connected with us
    public static final Font INTER_HEADING_1 = Inter(Font.BOLD, 18);
    public static final Font INTER_HEADING_2 = Inter(Font.BOLD, 16);
    public static final Font INTER_HEADING_3 = Inter(Font.BOLD, 14);
    public static final Font INTER_HEADING_4 = Inter(Font.BOLD, 12);
    public static final Font INTER_NORMAL_TEXT = Inter(Font.PLAIN, 12);
}
