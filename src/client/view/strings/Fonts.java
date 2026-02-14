package client.view.strings;

import java.awt.*;

public class Fonts {
    // Style: 0=plain, 1=bold, 2=italic

    // Sans Serif
    public static Font SansSerif(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    // Segoe UI
    public static Font SegoeUi(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    // Segoe UI Semibold
    public static Font SegoeUiSemibold(int style, int size) {
        return new Font("Segoe UI Semibold", style, size);
    }

    // Inter
    public static Font Inter(int style, int size) {
        return new Font("Inter", style, size);
    }
}
