package client.view.components.resident;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FeedbackSubmitPopUp extends JDialog {
    public FeedbackSubmitPopUp(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new java.awt.Color(0, 0, 0, 0));
        setSize(400, 200);
        setLocationRelativeTo(parent);

        RoundedPanel main = new RoundedPanel(30, Color.WHITE);
        main.setLayout(new BorderLayout(0, 15));
        main.setBorder(new EmptyBorder(30, 30, 20, 30));

        JLabel message = new JLabel("Your feedback has been submitted successfully!");
        message.setFont(new Font("Inter", Font.BOLD, 14));
        message.setForeground(new Color(30, 41, 59));
        message.setHorizontalAlignment(SwingConstants.CENTER);

        RoundedButton okBtn = new RoundedButton("OK", 15, new Color(0, 102, 102), Color.WHITE);
        okBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okBtn);

        main.add(message, BorderLayout.CENTER);
        main.add(buttonPanel, BorderLayout.SOUTH);

        add(main);
    }

    static class RoundedPanel extends JPanel {
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

    static class RoundedButton extends JButton {
        private final int radius;
        private final Color bgColor;

        RoundedButton(String label, int radius, Color bgColor, Color textColor) {
            super(label);
            this.radius = radius;
            this.bgColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);

            setForeground(textColor);
            setFont(new Font("Inter", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(110, 35));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(bgColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(bgColor.brighter());
            } else {
                g2.setColor(bgColor);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
