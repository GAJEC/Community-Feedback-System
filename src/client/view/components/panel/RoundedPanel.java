package client.view.components.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private final int r;
    private final Color c;
    private Color sCol = new Color(241, 245, 249);
    private float sWidth = 1.0f;

    RoundedPanel(int radius, Color color) {
        r = radius;
        c = color;
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), r, r));
        if (sWidth > 0) {
            g2.setStroke(new BasicStroke(sWidth));
            g2.setColor(sCol);
            g2.draw(new RoundRectangle2D.Double(sWidth / 2, sWidth / 2, getWidth() - sWidth, getHeight() - sWidth, r, r));
        }
        g2.dispose();
    }
}
