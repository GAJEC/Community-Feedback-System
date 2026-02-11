package client.view.components;

import client.view.strings.Colors;
import common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ClientHeaderPanel extends JPanel {

    // Colors
    private static final Color HEADER_COLOR = Colors.PRIMARY;
    private static final Color NAV_COLOR = Colors.WHITE;
    private static final Color ACTIVE_TEXT = Colors.SECONDARY;
    private static final Color LOGOUT_RED = Colors.RED_BUTTON;

    public JButton profileButton;
    public JButton logoutButton;
    private User user;

    public ClientHeaderPanel(User user) {
        this.user = user;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        setBackground(HEADER_COLOR);

        buildHeader();
        buildDashboard();
    }

    private void buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(1000, 100));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        leftPnl.setOpaque(false);

        try {
            URL logoURL = getClass().getResource("/assets/logo_icon.png");
            if (logoURL != null) {
                Image logo = new ImageIcon(logoURL).getImage().getScaledInstance(100, 90, Image.SCALE_SMOOTH);
                leftPnl.add(new JLabel(new ImageIcon(logo)));
            }
        } catch (Exception ignored) {
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);


        JLabel title = new JLabel("Community Feedback System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Your Voice. Our Community. Real Change");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(255, 255, 255, 180));

        textPanel.add(title);
        textPanel.add(tagline);

        leftPnl.add(textPanel);
        headerPanel.add(leftPnl, BorderLayout.WEST);

        JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 30));
        rightPnl.setOpaque(false);

        String displayName = (user != null && user.getName() != null) ? user.getName() : "User";
        profileButton = new RoundedButton(displayName, 30, new Color(255, 255, 255, 40));
        profileButton.setPreferredSize(new Dimension(200, 40));

        logoutButton = new RoundedButton("Logout", 20, LOGOUT_RED);
        logoutButton.setPreferredSize(new Dimension(100, 40));

        try {
            URL logoutIconURL = getClass().getResource("/assets/profile_icon.png");
            if (logoutIconURL != null) {
                Image img = new ImageIcon(logoutIconURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                profileButton.setIcon(new ImageIcon(img));
                profileButton.setIconTextGap(2);
            }
        } catch (Exception ignored) {
        }


        try {
            URL logoutIconURL = getClass().getResource("/assets/logout_icon.png");
            if (logoutIconURL != null) {
                Image img = new ImageIcon(logoutIconURL).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                logoutButton.setIcon(new ImageIcon(img));
                logoutButton.setIconTextGap(8);
            }
        } catch (Exception ignored) {
        }
        logoutButton.addActionListener(new ActionListener() { // REMOVE THIS LINE
                @Override
                public void actionPerformed(ActionEvent e) {
                    LogoutPopUp logoutDialog = new LogoutPopUp((JFrame) SwingUtilities.getWindowAncestor(headerPanel));
                    logoutDialog.setVisible(true);
                }
            });
        ;

        rightPnl.add(profileButton);
        rightPnl.add(logoutButton);

        headerPanel.add(rightPnl, BorderLayout.EAST);
        add(headerPanel);
    }
    public JButton viewBtn;
    public JButton submitBtn;
    // ================= NAV BAR =================
    private void buildDashboard() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        navBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        navBar.setBackground(NAV_COLOR);
        navBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navBar.setPreferredSize(new Dimension(1000, 50));

        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        viewBtn = new JButton("View Feedbacks");
        submitBtn = new JButton("Submit Feedback");



        navBar.add(viewBtn);
        navBar.add(submitBtn);

        add(navBar);
    }

    // Utility method to style nav buttons
    public void styleTab(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(active ? ACTIVE_TEXT : Color.GRAY);

        btn.setBorderPainted(true);

        if (active) {
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ACTIVE_TEXT));
        } else {
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0,0,0,0)));
        }
    }

    // ================= ROUNDED BUTTON (Header Specific) =================
    static class RoundedButton extends JButton {
        private final int radius;
        private final Color color;

        RoundedButton(String txt, int r, Color c){
            super(txt);
            radius = r;
            color = c;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }
}