package client.view.common;

import client.view.strings.Colors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Buttons extends JButton {
    private int radius;
    private Color hoverColor;
    private Color normalColor;

    public Buttons() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public Buttons(String label, int radius) {
        super(label);
        this.radius = radius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    // full constructor
    public Buttons(String label, int radius, Color background, Color foreground, Color hover, Font font) {
        this(label, radius);

        setBackground(background);
        setForeground(foreground);
        setFont(font);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyHover(this, background, hover);
    }
    public Buttons(Icon icon, int gap, String label, int radius, Color background, Color foreground, Color hover, Font font) {
        this(label, radius, background, foreground, hover, font);
        setButtonIcon(icon, gap);
        setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    public static Buttons createRounded(String text, int radius, Color normal, Color hover) {
        Buttons btn = new Buttons(text, radius);
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

    public void setLabel(String label) {
        setText(label);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setButtonIcon(Icon icon) {
        setIcon(icon);
        setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    public void setButtonIcon(Icon icon, int gap) {
        setIcon(icon);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(gap);
    }

    public void setBackgroundColor(Color background) {
        setBackground(background);
        repaint();
    }

    public void setForegroundColor(Color foreground) {
        setForeground(foreground);
        repaint();
    }

    public void setHoverColor(Color hover) {
        this.hoverColor = hover;
        this.normalColor = getBackground();

        applyHover(this, normalColor, hoverColor);
    }

    public void setFontStyle(Font font) {
        setFont(font);
        repaint();
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

    public static class CloseButton extends JButton {

        private static final Color NORMAL = Colors.PRIMARY; // for dark headers
        private static final Color HOVER = Colors.LIGHT_RED;


        // Custom foreground and hover color
        public CloseButton(Window window, Color foreground, Color hover) {
            super("✕");

            setFont(new Font("SansSerif", Font.BOLD, 18));
            setForeground(foreground);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addActionListener(e -> window.dispose());

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setForeground(hover);
                }

                public void mouseExited(MouseEvent e) {
                    setForeground(foreground);
                }
            });
        }

        // Custom Foreground
        public CloseButton(Window window, Color foreground) {
            super("✕");

            setFont(new Font("SansSerif", Font.BOLD, 18));
            setForeground(foreground);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addActionListener(e -> window.dispose());

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setForeground(HOVER);
                }

                public void mouseExited(MouseEvent e) {
                    setForeground(foreground);
                }
            });
        }

        // Default
        public CloseButton(Window window) {
            super("✕");

            setFont(new Font("SansSerif", Font.BOLD, 18));
            setForeground(NORMAL);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addActionListener(e -> window.dispose());

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setForeground(HOVER);
                }

                public void mouseExited(MouseEvent e) {
                    setForeground(NORMAL);
                }
            });
        }
    }
}