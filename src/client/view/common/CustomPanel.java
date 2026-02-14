package client.view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomPanel extends JPanel {
    private int radius = 15;
    private Color backgroundColor = Color.WHITE;
    private Color strokeColor = new Color(200, 200, 200);
    private float strokeWidth = 0f; // Acts as a border

    private boolean roundTopLeft = true;
    private boolean roundTopRight = true;
    private boolean roundBottomLeft = true;
    private boolean roundBottomRight = true;

    private Color offsetShadowColor = new Color(0, 0, 0, 40);
    private int offsetShadowDistance = 0;

    private boolean draggable = false;
    private Point dragStartPoint;
    private MouseAdapter dragListener;

    public CustomPanel() {
        init();
    }

    public CustomPanel(int radius) {
        this.radius = radius;
        init();
    }

    public CustomPanel(int radius, Color backgroundColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        init();
    }

    private void init() {
        setOpaque(false);
    }

    public void setDraggable(boolean draggable) {
        if (this.draggable == draggable) return; // No change
        this.draggable = draggable;

        if (dragListener != null) {
            removeMouseListener(dragListener);
            removeMouseMotionListener(dragListener);
            dragListener = null;
        }

        if (draggable) {
            dragListener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragStartPoint = e.getPoint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragStartPoint == null) return;
                    Window w = SwingUtilities.getWindowAncestor(CustomPanel.this);
                    if (w != null) {
                        Point curr = w.getLocation();
                        w.setLocation(
                                curr.x + e.getX() - dragStartPoint.x,
                                curr.y + e.getY() - dragStartPoint.y
                        );
                    }
                }
            };
            addMouseListener(dragListener);
            addMouseMotionListener(dragListener);
        }
    }

    public void setBackgroundColor(Color color) { this.backgroundColor = color; repaint(); }
    public void setStroke(Color color, float width) { this.strokeColor = color; this.strokeWidth = width; repaint(); }

    public void setRoundedCorners(boolean tl, boolean tr, boolean bl, boolean br) {
        this.roundTopLeft = tl;
        this.roundTopRight = tr;
        this.roundBottomLeft = bl;
        this.roundBottomRight = br;
        repaint();
    }

    public void setOffsetShadow(Color color, int distance) { this.offsetShadowColor = color; this.offsetShadowDistance = distance; repaint(); }
    public void setRadius(int radius) { this.radius = radius; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int ds = offsetShadowDistance;
        int lw = (int) strokeWidth;

        if (ds > 0) {
            g2.setColor(offsetShadowColor);
            g2.fillRoundRect(ds, ds, w - ds - 1, h - ds - 1, radius, radius);
        }

        g2.setColor(backgroundColor);

        if (roundTopLeft && roundTopRight && roundBottomLeft && roundBottomRight) {
            g2.fillRoundRect(0, 0, w - ds - 1, h - ds - 1, radius, radius);

            if (lw > 0) {
                g2.setColor(strokeColor);
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.drawRoundRect(0, 0, w - ds - 1, h - ds - 1, radius, radius);
            }
        } else {
            int r = radius;
            int fw = w - ds - 1;
            int fh = h - ds - 1;

            g2.fillRoundRect(0, 0, fw, fh, r, r);

            if (!roundTopLeft)     g2.fillRect(0, 0, r, r);
            if (!roundTopRight)    g2.fillRect(fw - r, 0, r, r);
            if (!roundBottomLeft)  g2.fillRect(0, fh - r, r, r);
            if (!roundBottomRight) g2.fillRect(fw - r, fh - r, r, r);

            if (lw > 0) {
                g2.setColor(strokeColor);
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.drawRect(0, 0, fw, fh);
            }
        }

        g2.dispose();
    }
}