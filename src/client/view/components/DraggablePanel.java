package client.view.components;

import javax.swing.*;
import java.awt.*;

public class DraggablePanel extends JPanel {

    private Point click;

    public DraggablePanel() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                click = e.getPoint();
            }
        });

        addMouseMotionListener(new java.awt.event.MouseAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (click == null) return;
                Window w = SwingUtilities.getWindowAncestor(DraggablePanel.this);
                Point curr = w.getLocation();
                w.setLocation(
                        curr.x + e.getX() - click.x,
                        curr.y + e.getY() - click.y
                );
            }
        });
    }
}