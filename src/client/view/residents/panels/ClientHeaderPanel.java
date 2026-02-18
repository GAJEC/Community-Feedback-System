package client.view.residents.panels;

import client.view.common.LogoutPopUp;
import client.view.common.UserProfileDialog;
import client.view.common.Buttons;
import client.view.strings.Colors;
import client.view.strings.Images;
import common.User;

import javax.swing.*;
import java.awt.*;

import static client.view.strings.Colors.*;
import static client.view.strings.Fonts.SegoeUi;
import static client.view.strings.Images.PROFILE;

public class ClientHeaderPanel extends JPanel {
    // Colors
    private static final Color HEADER_COLOR = Colors.PRIMARY;
    private static final Color NAV_COLOR = WHITE;
    public static final Color ACTIVE_TEXT = Colors.SECONDARY;

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

        leftPnl.add(new JLabel(Images.scaledLogo(100, 90)));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);


        JLabel title = new JLabel("Community Feedback System");
        title.setFont(SegoeUi(1, 30));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Your Voice. Our Community. Real Change");
        tagline.setFont(SegoeUi(2, 14));
        tagline.setForeground(new Color(255, 255, 255, 180));

        textPanel.add(title);
        textPanel.add(tagline);

        leftPnl.add(textPanel);
        headerPanel.add(leftPnl, BorderLayout.WEST);

        JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 30));
        rightPnl.setOpaque(false);

        String displayName = (user != null && user.getName() != null) ? user.getName() : "User";
        Buttons profileButton = new Buttons(displayName, 30, SUB_PRIMARY, WHITE, SECONDARY, SegoeUi(1,  12));
        profileButton.setPreferredSize(new Dimension(200, 40));
        profileButton.setButtonIcon(Images.scaled(PROFILE, 25, 25), 2);

        Buttons.applyClick(profileButton, () -> {
            UserProfileDialog UserProfileDialog =
                    new UserProfileDialog((JFrame) SwingUtilities.getWindowAncestor(headerPanel), user);
            UserProfileDialog.setVisible(true);
        });

        Buttons logoutButton = new Buttons("Logout", 20, DARK_BUTTON, WHITE, RED, SegoeUi(1, 12));
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
        btn.setFont(SegoeUi(1, 14));
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