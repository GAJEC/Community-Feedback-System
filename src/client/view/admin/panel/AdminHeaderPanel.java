package client.view.admin.panel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AdminHeaderPanel extends JPanel {

    private static final Color HEADER_COLOR = new Color(0, 80, 80);
    private static final Color NAV_COLOR = new Color(255, 255, 255);
    private static final Color ACTIVE_TEXT = new Color(0, 102, 102);
    private static final Color LOGOUT_RED = new Color(180, 40, 40);

    public JButton profileButton;
    public JButton logoutButton;

    public JButton viewFeedbackBtn;
    public JButton manageFeedbackBtn;
    public JButton statsBtn;


    private String currentActiveTab;


    public AdminHeaderPanel(String activeTab) {
        this.currentActiveTab = activeTab;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(HEADER_COLOR);

        buildHeader();
        buildNavBar();
    }

    private void buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
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
        } catch (Exception ignored) {}

        // Text Panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Community Feedback System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Admin Panel - Barangay Official"); // Admin specific sub-header
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(255,255,255,180));

        textPanel.add(title);
        textPanel.add(tagline);

        leftPnl.add(textPanel);
        headerPanel.add(leftPnl, BorderLayout.WEST);

        // --- Right Side (Profile + Logout) ---
        JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 45));
        rightPnl.setOpaque(false);

        // Profile Button with Admin Name
        // You can add an icon here if you have one, e.g., setIcon(...)
        profileButton = new RoundedButton("Barangay Official", 30, new Color(255,255,255,40));
        profileButton.setPreferredSize(new Dimension(180, 40));

        logoutButton = new RoundedButton("Logout", 20, LOGOUT_RED);
        logoutButton.setPreferredSize(new Dimension(100, 40));

        rightPnl.add(profileButton);
        rightPnl.add(logoutButton);

        headerPanel.add(rightPnl, BorderLayout.EAST);
        add(headerPanel);
    }

    private void buildNavBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        navBar.setBackground(NAV_COLOR);
        navBar.setPreferredSize(new Dimension(1000, 50));
        navBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Initialize Buttons
        viewFeedbackBtn = new JButton("View Feedbacks");
        manageFeedbackBtn = new JButton("Manage Feedback");
        statsBtn = new JButton("Statistics");

        // Apply Styling based on which tab is currently active
        styleTab(viewFeedbackBtn, "View Feedbacks".equals(currentActiveTab));
        styleTab(manageFeedbackBtn, "Manage Feedback".equals(currentActiveTab));
        styleTab(statsBtn, "Statistics".equals(currentActiveTab));

        navBar.add(viewFeedbackBtn);
        navBar.add(manageFeedbackBtn);
        navBar.add(statsBtn);

        add(navBar);
    }

    private void styleTab(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Active vs Inactive colors
        btn.setForeground(active ? ACTIVE_TEXT : Color.GRAY);

        if (active) {
            // Green underline for the active tab
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ACTIVE_TEXT));
            btn.setBorderPainted(true);
        }
    }

    // ================= ROUNDED BUTTON CLASS =================
    // Copied from your reference to ensure visual consistency
    static class RoundedButton extends JButton {
        private final int radius;
        private final Color color;

        RoundedButton(String txt, int r, Color c) {
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }
}