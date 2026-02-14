package client.view.brgyofficials.panel;

import client.view.common.button.Buttons;
import common.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BrgyOfficialHeaderPanel extends JPanel {

    private static final Color HEADER_COLOR = new Color(34, 153, 84);
    private static final Color NAV_COLOR = new Color(255, 255, 255);
    private static final Color ACTIVE_TEXT = new Color(114, 181, 112);
    private static final Color LOGOUT_RED = new Color(180, 40, 40);

    public JButton profileButton;
    public JButton logoutButton;

    public JButton viewFeedbackBtn;
    public JButton manageFeedbackBtn;
    public JButton statsBtn;
    public JButton postAnnouncementBtn;

    private String currentActiveTab;
    private User user;

    public BrgyOfficialHeaderPanel(User user) {
        this.user = user;
        this.currentActiveTab = "View Feedbacks";
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(HEADER_COLOR);

        buildHeader();
        buildDashboard();
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

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Community Feedback System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Barangay Official Portal");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(255,255,255,180));

        textPanel.add(title);
        textPanel.add(tagline);

        leftPnl.add(textPanel);
        headerPanel.add(leftPnl, BorderLayout.WEST);

        JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 45));
        rightPnl.setOpaque(false);

        profileButton = Buttons.createRounded("Barangay Official", 30, new Color(255, 255, 255, 40), new Color(255, 255, 255, 70));
        profileButton.setPreferredSize(new Dimension(180, 40));

        logoutButton = Buttons.createRounded("Logout", 20, LOGOUT_RED, LOGOUT_RED.brighter());
        logoutButton.setPreferredSize(new Dimension(100, 40));

        try {
            URL logoutIconURL = getClass().getResource("/assets/profile_icon.png");
            if (logoutIconURL != null) {
                Image img = new ImageIcon(logoutIconURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                profileButton.setIcon(new ImageIcon(img));
                profileButton.setIconTextGap(2);
            }
        } catch (Exception ignored) {}


        try {
            URL logoutIconURL = getClass().getResource("/assets/logout_icon.png");
            if (logoutIconURL != null) {
                Image img = new ImageIcon(logoutIconURL).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                logoutButton.setIcon(new ImageIcon(img));
                logoutButton.setIconTextGap(8);
            }
        } catch (Exception ignored) {}

        rightPnl.add(profileButton);
        rightPnl.add(logoutButton);

        headerPanel.add(rightPnl, BorderLayout.EAST);
        add(headerPanel);
    }

    private void buildDashboard() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        navBar.setBackground(NAV_COLOR);
        navBar.setPreferredSize(new Dimension(1000, 50));
        navBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Initialize Buttons
        viewFeedbackBtn = new JButton("View Feedbacks");
        manageFeedbackBtn = new JButton("Manage Feedback");
        statsBtn = new JButton("Statistics");
        postAnnouncementBtn = new  JButton("Post Announcement");

        // Apply Styling based on which tab is currently active
        styleTab(viewFeedbackBtn, "View Feedbacks".equals(currentActiveTab));
        styleTab(manageFeedbackBtn, "Manage Feedback".equals(currentActiveTab));
        styleTab(statsBtn, "Statistics".equals(currentActiveTab));
        styleTab(postAnnouncementBtn, "Post Announcement".equals(currentActiveTab));

        navBar.add(viewFeedbackBtn);
        navBar.add(manageFeedbackBtn);
        navBar.add(statsBtn);
        navBar.add(postAnnouncementBtn);

        add(navBar);

        viewFeedbackBtn.addActionListener(e -> setActiveTab("View Feedbacks"));
        manageFeedbackBtn.addActionListener(e -> setActiveTab("Manage Feedback"));
        statsBtn.addActionListener(e -> setActiveTab("Statistics"));
        postAnnouncementBtn.addActionListener(e -> setActiveTab("Post Announcement"));
    }

    private void setActiveTab(String tabName) {
        currentActiveTab = tabName;

        styleTab(viewFeedbackBtn, "View Feedbacks".equals(currentActiveTab));
        styleTab(manageFeedbackBtn, "Manage Feedback".equals(currentActiveTab));
        styleTab(statsBtn, "Statistics".equals(currentActiveTab));
        styleTab(postAnnouncementBtn, "Post Announcement".equals(currentActiveTab));

        repaint();
    }

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
}
