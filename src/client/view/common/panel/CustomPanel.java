package client.view.common.panel;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class CustomPanel extends JPanel {
    private int radius = 15;
    private Color backgroundColor = Color.WHITE;
    private Color strokeColor = new Color(200, 200, 200);
    private float strokeWidth = 0f;

    private boolean roundTopLeft = true;
    private boolean roundTopRight = true;
    private boolean roundBottomLeft = true;
    private boolean roundBottomRight = true;

    private Color offsetShadowColor = new Color(0, 0, 0, 40);
    private int offsetShadowDistance = 0; // disabled by default

    private Color dropShadowColor = new Color(0, 0, 0, 30);
    private float dropShadowWidth = 0f; // disabled by default

    private boolean draggable = false;
    private Point click;

    public CustomPanel() {
        setOpaque(false);
    }

    public CustomPanel(int radius, Color backgroundColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        setOpaque(false);
    }
    public CustomPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    private void initDraggable() {
        if (draggable) {
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    click = e.getPoint();
                }
            });
            addMouseMotionListener(new java.awt.event.MouseAdapter() {
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    if (click == null) return;
                    Window w = SwingUtilities.getWindowAncestor(CustomPanel.this);
                    if (w != null) {
                        Point curr = w.getLocation();
                        w.setLocation(
                                curr.x + e.getX() - click.x,
                                curr.y + e.getY() - click.y
                        );
                    }
                }
            });
        }
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        initDraggable();
    }

    public void setBackgroundColor(Color color) { this.backgroundColor = color; repaint(); }
    public void setStroke(Color color, float width) { this.strokeColor = color; this.strokeWidth = width; repaint(); }
    public void setRoundedCorners(boolean tl, boolean tr, boolean bl, boolean br) { this.roundTopLeft = tl; this.roundTopRight = tr; this.roundBottomLeft = bl; this.roundBottomRight = br; repaint(); }
    public void setOffsetShadow(Color color, int distance) { this.offsetShadowColor = color; this.offsetShadowDistance = distance; repaint(); }
    public void setDropShadow(Color color, float width) { this.dropShadowColor = color; this.dropShadowWidth = width; repaint(); }
    public void setRadius(int radius) { this.radius = radius; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Shape shape = createRoundShape(0, 0, w - offsetShadowDistance, h - offsetShadowDistance);

        // Offset shadow
        if (offsetShadowDistance > 0) {
            Shape shadowShape = createRoundShape(
                    offsetShadowDistance,
                    offsetShadowDistance,
                    w - offsetShadowDistance,
                    h - offsetShadowDistance
            );
            g2.setColor(offsetShadowColor);
            g2.fill(shadowShape);
        }

        // Drop shadow stroke
        if (dropShadowWidth > 0) {
            g2.setStroke(new BasicStroke(dropShadowWidth));
            g2.setColor(dropShadowColor);
            g2.draw(shape);
        }

        // Background
        g2.setColor(backgroundColor);
        g2.fill(shape);

        g2.dispose();
    }

    private Shape createRoundShape(int x, int y, int width, int height) {
        int r = radius;
        Path2D path = new Path2D.Double();

        // Top-left
        if (roundTopLeft) {
            path.moveTo(x + r, y);
        } else {
            path.moveTo(x, y);
        }

        // Top edge
        if (roundTopRight) {
            path.lineTo(x + width - r, y);
            path.quadTo(x + width, y, x + width, y + r);
        } else {
            path.lineTo(x + width, y);
        }

        // Right edge
        if (roundBottomRight) {
            path.lineTo(x + width, y + height - r);
            path.quadTo(x + width, y + height, x + width - r, y + height);
        } else {
            path.lineTo(x + width, y + height);
        }

        // Bottom edge
        if (roundBottomLeft) {
            path.lineTo(x + r, y + height);
            path.quadTo(x, y + height, x, y + height - r);
        } else {
            path.lineTo(x, y + height);
        }

        // Left edge
        if (roundTopLeft) {
            path.lineTo(x, y + r);
            path.quadTo(x, y, x + r, y);
        } else {
            path.lineTo(x, y);
        }

        path.closePath();
        return path;
    }

    public static class DropShadowBorder extends AbstractBorder {
        private final int radius;
        private final int padding;
        private final Color shadowColor;
        private final int shadowAlpha;

        public DropShadowBorder(int radius, int padding, Color shadowColor, int shadowAlpha) {
            this.radius = radius;
            this.padding = padding;
            this.shadowColor = shadowColor;
            this.shadowAlpha = shadowAlpha;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < padding; i++) {
                float opacity = shadowAlpha * (1f - (i / (float) padding));
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), (int) opacity));
                g2.draw(new RoundRectangle2D.Double(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1, radius, radius));
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(padding, padding, padding, padding);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = padding;
            return insets;
        }
    }
}