package client.view.common;

import client.view.auth.Login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LogoutPopUp extends JDialog {
    private final Color LOGOUT_COLOR = new Color(255, 102, 102); // Indigo
    private final Color TEXT_COLOR = new Color(30, 41, 59);
    private final Color BG_COLOR = Color.WHITE;

    public LogoutPopUp(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        setSize(350, 180);
        setLocationRelativeTo(parent);

        RoundedPanel container = new RoundedPanel(30, BG_COLOR);
        container.setLayout(new BorderLayout(0, 15));
        container.setBorder(new EmptyBorder(25, 25, 25, 25));
        container.setStroke(new Color(226, 232, 240), 1.5f);

        JLabel message = new JLabel("Are you sure you want to logout?");
        message.setFont(new Font("Inter", Font.BOLD, 14));
        message.setForeground(TEXT_COLOR);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        RoundedButton yesBtn = new RoundedButton("Logout", 15, LOGOUT_COLOR, Color.WHITE);
        RoundedButton noBtn = new RoundedButton("Cancel", 15, new Color(241, 245, 249), TEXT_COLOR);

        yesBtn.addActionListener(e -> {
            // 1. Close Logout Dialog
            dispose();

            // Close the dashboard
            if (parent != null) {
                parent.dispose();
            }
            // Open Login Screen
            client.model.ClientConnection connection = new client.model.ClientConnection();
            new Login(connection).setVisible(true);
        });
        noBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(noBtn);
        buttonPanel.add(yesBtn);

        container.add(message, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        add(container);
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

        public void setStroke(Color col, float w) {
            this.sCol = col;
            this.sWidth = w;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), r, r));
            if (sWidth > 0) {
                g2.setStroke(new BasicStroke(sWidth));
                g2.setColor(sCol);
                g2.draw(new RoundRectangle2D.Double(sWidth/2.0, sWidth/2.0, getWidth()-sWidth, getHeight()-sWidth, r, r));
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