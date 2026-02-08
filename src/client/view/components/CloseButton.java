package client.view.components;

import javax.swing.*;
import java.awt.*;

public class CloseButton extends JButton {

    public CloseButton(JFrame frame) {
        super("✕");
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setForeground(new Color(0, 80, 80));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> frame.dispose());

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                setForeground(new Color(0, 80, 80));
            }
        });
    }
}