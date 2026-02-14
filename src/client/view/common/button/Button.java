package client.view.common.button;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {
    private int radius;

    public Button(String label, int radius) {
        super(label);
        this.radius = radius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    // no hover effect and font
    public Button(String label, int radius, Color background, Color foreground) {
        this(label, radius);

        setBackground(background);
        setForeground(foreground);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(110, 35));
    }

    // no hover effect
    public Button(String label, int radius, Color background, Color foreground, Font font) {
        this(label, radius);

        setBackground(background);
        setForeground(foreground);
        setFont(font);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // default font + hover
    public Button(String label, int radius, Color background, Color foreground, Color hover) {
        this(label, radius);

        setBackground(background);
        setForeground(foreground);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyHover(this, background, hover);
    }

    // full constructor
    public Button(String label, int radius, Color background, Color foreground, Color hover, Font font) {
        this(label, radius);

        setBackground(background);
        setForeground(foreground);
        setFont(font);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyHover(this, background, hover);
    }

    public static Button createRounded(String text, int radius, Color normal, Color hover) {
        Button btn = new Button(text, radius);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);

        applyHover(btn, normal, hover);

        return btn;
    }

    public static void applyHover(JComponent component, Color normal, Color hover) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBackground(normal);
            }
        });
    }

    public static void applyLinkHover(JLabel label, Color normal, Color hover) {
        label.setForeground(normal);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border empty = BorderFactory.createEmptyBorder(0, 0, 1, 0);
        Border underline = BorderFactory.createMatteBorder(0, 0, 1, 0, hover);

        label.setBorder(empty);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(hover);
                label.setBorder(underline);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(normal);
                label.setBorder(empty);
            }
        });
    }

    public static void applyClick(JComponent component, Runnable onClick) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        super.paintComponent(g);
        g2.dispose();
    }
}