package client.view.components.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {

    private int radius;
    private Color backgroundColor;
    private Color strokeColor = new Color(241, 245, 249);
    private float strokeWidth = 0f; // default no border

    public RoundedPanel(int radius, Color backgroundColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        setOpaque(false);
    }

    // change background color
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    // Enable border stroke
    public void setStroke(Color color, float width) {
        this.strokeColor = color;
        this.strokeWidth = width;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(
                0, 0,
                getWidth(),
                getHeight(),
                radius,
                radius
        ));

        // Draw border if enabled
        if (strokeWidth > 0) {
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.setColor(strokeColor);
            g2.draw(new RoundRectangle2D.Double(
                    strokeWidth / 2,
                    strokeWidth / 2,
                    getWidth() - strokeWidth,
                    getHeight() - strokeWidth,
                    radius,
                    radius
            ));
        }

        g2.dispose();
    }
}