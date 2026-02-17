package client.view.brgyofficials.panel;

import client.view.common.Buttons;
import client.view.common.LogoutPopUp;
import client.view.common.UserProfileDialog;
import client.view.strings.Colors;
import client.view.strings.Images;
import common.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static client.view.strings.Colors.*;
import static client.view.strings.Fonts.SegoeUi;
import static client.view.strings.Images.PROFILE;

public class BrgyOfficialHeaderPanel extends JPanel {

    private static final Color HEADER_COLOR = new Color(34, 153, 84);
    private static final Color NAV_COLOR = Colors.WHITE;
    private static final Color ACTIVE_TEXT = new Color(114, 181, 112);

    public Buttons profileButton, logoutButton, viewFeedbackBtn, manageFeedbackBtn, statsBtn, postAnnouncementBtn;

    private String currentActiveTab;
    private User user;

    public BrgyOfficialHeaderPanel(User user, String activeTab) {
        this.user = user;
        this.currentActiveTab = activeTab;
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

        leftPnl.add(new JLabel(Images.scaledLogo(100, 90)));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Community Feedback System");
        title.setFont(SegoeUi(Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Barangay Official Portal");
        tagline.setFont(SegoeUi(Font.ITALIC, 14));
        tagline.setForeground(new Color(255,255,255,180));

        textPanel.add(title);
        textPanel.add(tagline);

        leftPnl.add(textPanel);
        headerPanel.add(leftPnl, BorderLayout.WEST);

        JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 30));
        rightPnl.setOpaque(false);

        String displayName = (user != null && user.getName() != null) ? user.getName() : "Barangay Official";
        profileButton = new Buttons(displayName, 30, LIGHT_GREEN, WHITE, YELLOW_GREEN, SegoeUi(1,  12));
        profileButton.setPreferredSize(new Dimension(200, 40));
        profileButton.setButtonIcon(Images.scaled(PROFILE, 25, 25), 2);

        Buttons.applyClick(profileButton, () -> {
            UserProfileDialog UserProfileDialog =
                    new UserProfileDialog((JFrame) SwingUtilities.getWindowAncestor(headerPanel));
            UserProfileDialog.setVisible(true);
        });

        logoutButton = new Buttons("Logout", 20, DARK_BUTTON, WHITE, RED, SegoeUi(1, 12));
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.setButtonIcon(Images.scaled(Images.LOGOUT, 18, 18), 8);

        Buttons.applyClick(logoutButton, () -> {
            LogoutPopUp logoutDialog =
                    new LogoutPopUp((JFrame) SwingUtilities.getWindowAncestor(headerPanel));
            logoutDialog.setVisible(true);
        });

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
        viewFeedbackBtn = new Buttons("View Feedbacks",0);
        manageFeedbackBtn = new Buttons("Manage Feedback",0);
        statsBtn = new Buttons("Statistics",0);
        postAnnouncementBtn = new  Buttons("Post Announcement",0);

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
        btn.setFont(SegoeUi(Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
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
