package client.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String label, int radius) {
        super(label);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

     public static RoundedButton create(String text, int radius, Color primary, Color hover) {
         RoundedButton btn = new RoundedButton(text, radius);
         btn.setBackground(primary);
         btn.setForeground(Color.WHITE);

         btn.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent e) {
                 btn.setBackground(hover);
             }

             @Override
             public void mouseExited(MouseEvent e) {
                 btn.setBackground(primary);
             }
         });

         return btn;
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