package client.view.components.button;

import client.view.strings.Colors;

import javax.swing.*;
import java.awt.*;

public class CloseButton extends JButton {

    private static final Color NORMAL = Colors.PRIMARY; // for dark headers
    private static final Color HOVER = Colors.RED;


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

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setForeground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
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

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setForeground(HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
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

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setForeground(HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                setForeground(NORMAL);
            }
        });
    }
}