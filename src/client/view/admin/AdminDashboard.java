package client.view.admin;

import client.model.ClientConnection;
import client.view.common.UserProfileDialog;
import common.DeleteUserRequest;
import common.FeedbackStatusUpdateRequest;
import common.Feedbacks;
import common.User;
import common.UserStatusUpdateRequest;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {

    private static final Color HEADER_COLOR = new Color(80, 0, 150);
    private static final Color NAV_COLOR = new Color(255, 255, 255);
    private static final Color ACTIVE_TEXT = new Color(102, 0, 204);
    private static final Color LOGOUT_RED = new Color(180, 40, 40);

    private final Color bgLight = new Color(245, 246, 250);
    private final Color purple = new Color(145, 30, 235);
    private final Color blue = new Color(25, 118, 210);
    private final Color green = new Color(0, 200, 83);
    private final Color orange = new Color(255, 109, 0);

    private JButton summaryBtn;
    private JButton consoleBtn;
    private JButton manageFeedbackBtn;
    private JButton systemControlsBtn;

    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JPanel summaryPanel;
    private JPanel manageFeedbackPanel;
    private String activeTab = "Summary";
    private final ClientConnection connection;
    private User[] cachedUsers = new User[0];
    private Feedbacks[] cachedFeedbacks = new Feedbacks[0];

    public AdminDashboard() {
        this(new ClientConnection());
    }

    public AdminDashboard(ClientConnection connection) {
        this.connection = connection;
        if (!this.connection.isConnected()) {
            this.connection.connect();
        }
        refreshDataCaches();

        setTitle("Community Feedback System - Admin");
        setSize(1280, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        URL iconURL = AdminDashboard.class.getResource("/res/assets/app_icon.png");
        if (iconURL == null) iconURL = AdminDashboard.class.getResource("/assets/app_icon.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        buildHeader(northContainer);
        buildNavBar(northContainer);
        add(northContainer, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);

        summaryPanel = createSummaryPanel();
        manageFeedbackPanel = createManageFeedbackPanel();
        contentPanel.add(summaryPanel, "Summary");
        contentPanel.add(new AdminConsoleView(connection, this::refreshAllData), "Users");
        contentPanel.add(manageFeedbackPanel, "Manage Feedback");
        contentPanel.add(new SystemControlsView(), "System Controls");

        add(contentPanel, BorderLayout.CENTER);

        setActiveTab("Summary");
    }

    private void buildHeader(JPanel container) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(1000, 75));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel leftPnl = new JPanel(new GridBagLayout());
        leftPnl.setOpaque(false);

        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.anchor = GridBagConstraints.CENTER;
        gbcL.insets = new Insets(0, 20, 0, 0);

        try {
            URL logoURL = getClass().getResource("/assets/logo_icon.png");
            if (logoURL != null) {
                Image logo = new ImageIcon(logoURL).getImage().getScaledInstance(70, 65, Image.SCALE_SMOOTH);
                leftPnl.add(new JLabel(new ImageIcon(logo)), gbcL);
            }
        } catch (Exception ignored) {
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Community Feedback System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Admin Panel - Barangay Official");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(255, 255, 255, 180));

        textPanel.add(title);
        textPanel.add(tagline);

        gbcL.insets = new Insets(0, 15, 0, 0);
        leftPnl.add(textPanel, gbcL);
        headerPanel.add(leftPnl, BorderLayout.WEST);

        JPanel rightPnl = new JPanel(new GridBagLayout());
        rightPnl.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.anchor = GridBagConstraints.CENTER;

        JButton profileButton = new RoundedButton("Administrator", 30, new Color(255, 255, 255, 40));
        profileButton.setPreferredSize(new Dimension(180, 40));
        gbcR.insets = new Insets(0, 0, 0, 15);
        rightPnl.add(profileButton, gbcR);

        JButton logoutButton = new RoundedButton("Logout", 20, LOGOUT_RED);
        logoutButton.setPreferredSize(new Dimension(110, 40));
        logoutButton.addActionListener(e -> {
            dispose();
            new client.view.auth.AuthScreen(new client.model.ClientConnection()).setVisible(true);
        });

        gbcR.insets = new Insets(0, 0, 0, 20);
        rightPnl.add(logoutButton, gbcR);

        headerPanel.add(rightPnl, BorderLayout.EAST);
        container.add(headerPanel);
    }

    private void buildNavBar(JPanel container) {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        navBar.setBackground(NAV_COLOR);
        navBar.setPreferredSize(new Dimension(1000, 50));
        navBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        summaryBtn = new JButton("Summary");
        consoleBtn = new JButton("Users");
        manageFeedbackBtn = new JButton("Manage Feedback");
        systemControlsBtn = new JButton("System Controls");

        summaryBtn.addActionListener(e -> setActiveTab("Summary"));
        consoleBtn.addActionListener(e -> setActiveTab("Users"));
        manageFeedbackBtn.addActionListener(e -> setActiveTab("Manage Feedback"));
        systemControlsBtn.addActionListener(e -> setActiveTab("System Controls"));

        styleTab(summaryBtn, false);
        styleTab(consoleBtn, false);
        styleTab(manageFeedbackBtn, false);
        styleTab(systemControlsBtn, false);

        navBar.add(summaryBtn);
        navBar.add(consoleBtn);
        navBar.add(manageFeedbackBtn);
        navBar.add(systemControlsBtn);

        container.add(navBar);
    }

    private void setActiveTab(String tab) {
        activeTab = tab;
        contentLayout.show(contentPanel, tab);

        styleTab(summaryBtn, "Summary".equals(tab));
        styleTab(consoleBtn, "Users".equals(tab));
        styleTab(manageFeedbackBtn, "Manage Feedback".equals(tab));
        styleTab(systemControlsBtn, "System Controls".equals(tab));
    }

    private void refreshAllData() {
        refreshDataCaches();
        rebuildSummaryPanel();
        if (contentPanel != null) {
            if (manageFeedbackPanel != null) {
                contentPanel.remove(manageFeedbackPanel);
            }
            manageFeedbackPanel = createManageFeedbackPanel();
            contentPanel.add(manageFeedbackPanel, "Manage Feedback");
            contentLayout.show(contentPanel, activeTab);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void rebuildSummaryPanel() {
        if (contentPanel == null || summaryPanel == null) return;
        contentPanel.remove(summaryPanel);
        summaryPanel = createSummaryPanel();
        contentPanel.add(summaryPanel, "Summary");
    }

    private void refreshDataCaches() {
        cachedUsers = fetchUsers();
        cachedFeedbacks = fetchFeedbacks();
    }

    private User[] fetchUsers() {
        if (!connection.isConnected()) {
            connection.connect();
        }
        connection.sendRequest("GET_USERS");
        Object response = connection.receive();
        if (response instanceof Object[] arr && arr.length >= 3 && "200".equals(arr[0]) && arr[2] instanceof User[]) {
            return (User[]) arr[2];
        }
        return new User[0];
    }

    private Feedbacks[] fetchFeedbacks() {
        if (!connection.isConnected()) {
            connection.connect();
        }
        connection.sendRequest("GET_FEEDBACKS");
        Object response = connection.receive();
        if (response instanceof Object[] arr && arr.length >= 3 && "200".equals(arr[0]) && arr[2] instanceof Feedbacks[]) {
            return (Feedbacks[]) arr[2];
        }
        return new Feedbacks[0];
    }

    private void styleTab(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(active ? ACTIVE_TEXT : Color.GRAY);
        btn.setBorder(active
                ? BorderFactory.createMatteBorder(0, 0, 3, 0, ACTIVE_TEXT)
                : BorderFactory.createEmptyBorder(0, 0, 3, 0));
    }

    private JPanel createSummaryPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgLight);

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(bgLight);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(bgLight);
        root.add(scrollPane, BorderLayout.CENTER);

        JLabel title = new JLabel("System Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridy = 0;
        gbc.insets = new Insets(-25, 0, 10, 0);
        mainContent.add(title, gbc);

        JLabel subTitle = new JLabel("Comprehensive summary of all system data and activity");
        subTitle.setForeground(Color.GRAY);
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(-10, 0, 30, 0);
        mainContent.add(subTitle, gbc);

        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        cardPanel.setOpaque(false);

        int totalFeedbacks = cachedFeedbacks.length;
        int activeCitizens = 0;
        for (User user : cachedUsers) {
            if (user != null && user.getAttr_type() == 2 && isApproved(user.getVerify_status())) {
                activeCitizens++;
            }
        }

        int totalResponses = 0;
        int totalUpvotes = 0;
        for (Feedbacks feedback : cachedFeedbacks) {
            if (feedback == null) continue;
            String status = getFeedbackStatus(feedback);
            if ("Resolved".equalsIgnoreCase(status) || "In Progress".equalsIgnoreCase(status)) {
                totalResponses++;
            }
            if (feedback.getAttr_upvote() != null) {
                totalUpvotes += feedback.getAttr_upvote().length;
            }
        }
        double avgUpvotes = totalFeedbacks == 0 ? 0.0 : ((double) totalUpvotes / totalFeedbacks);

        cardPanel.add(new InfoCard("Total Feedbacks", String.valueOf(totalFeedbacks), "All records", purple, "res/assets/feedbacks_icon.png"));
        cardPanel.add(new InfoCard("Active Citizens", String.valueOf(activeCitizens), "Verified users", blue, "res/assets/activecitizens_icon.png"));
        cardPanel.add(new InfoCard("Total Responses", String.valueOf(totalResponses), "Handled reports", green, "res/assets/totalresponses_icon.png"));
        cardPanel.add(new InfoCard("Avg Upvotes", String.format("%.1f", avgUpvotes), "Engagement", orange, "res/assets/upvotes_icon.png"));

        gbc.gridy = 2;
        mainContent.add(cardPanel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainContent.add(createDataGrid(), gbc);

        return root;
    }

    private JPanel createDataGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 10, 5);

        gbc.gridy = 0;
        gbc.gridx = 0;
        grid.add(createStatusPanel(), gbc);
        gbc.gridx = 1;
        grid.add(createPerformancePanel(), gbc);
        gbc.gridx = 2;
        grid.add(createAlertsPanel(), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        grid.add(createCategoryPanel(), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        grid.add(createActivityPanel(), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 5, 10, 5);
        grid.add(createWeeklyActivityPanel(), gbc);

        return grid;
    }

    private boolean isApproved(String verifyStatus) {
        return verifyStatus != null && ("APPROVED".equalsIgnoreCase(verifyStatus) || "ACTIVE".equalsIgnoreCase(verifyStatus));
    }

    private String getFeedbackStatus(Feedbacks feedback) {
        if (feedback == null || feedback.getAttr_status() == null || feedback.getAttr_status().length == 0 || feedback.getAttr_status()[0] == null) {
            return "Pending";
        }
        return feedback.getAttr_status()[0].trim();
    }

    private JPanel createStatusPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel statusIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/status distribution.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            statusIcon.setIcon(icon);
        } catch (Exception e) {
            statusIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Status Distribution");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(statusIcon);
        headerPanel.add(titleLabel);

        JPanel content = new JPanel(new GridLayout(4, 1, 0, 10));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        int total = Math.max(1, cachedFeedbacks.length);
        int pending = 0;
        int inProgress = 0;
        int resolved = 0;
        int archived = 0;
        for (Feedbacks feedback : cachedFeedbacks) {
            String status = getFeedbackStatus(feedback);
            if ("Pending".equalsIgnoreCase(status)) pending++;
            else if ("In Progress".equalsIgnoreCase(status) || "In-Progress".equalsIgnoreCase(status)) inProgress++;
            else if ("Resolved".equalsIgnoreCase(status)) resolved++;
            else if ("Archived".equalsIgnoreCase(status)) archived++;
        }

        content.add(createLabeledProgress("Pending", (pending * 100) / total, orange, String.valueOf(pending)));
        content.add(createLabeledProgress("In Progress", (inProgress * 100) / total, blue, String.valueOf(inProgress)));
        content.add(createLabeledProgress("Resolved", (resolved * 100) / total, green, String.valueOf(resolved)));
        content.add(createLabeledProgress("Archived", (archived * 100) / total, Color.LIGHT_GRAY, String.valueOf(archived)));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPerformancePanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel perfIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/performance_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            perfIcon.setIcon(icon);
        } catch (Exception e) {
            perfIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Performance Metrics");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(perfIcon);
        headerPanel.add(titleLabel);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        int total = Math.max(1, cachedFeedbacks.length);
        int resolved = 0;
        int inProgress = 0;
        for (Feedbacks feedback : cachedFeedbacks) {
            String status = getFeedbackStatus(feedback);
            if ("Resolved".equalsIgnoreCase(status)) resolved++;
            if ("In Progress".equalsIgnoreCase(status) || "In-Progress".equalsIgnoreCase(status)) inProgress++;
        }
        int completionPercentage = (int) ((double) resolved / total * 100);
        int responseRate = (int) ((double) (resolved + inProgress) / total * 100);

        content.add(createLabeledProgress("Completion Rate", completionPercentage, green, completionPercentage + ".0%"));
        content.add(Box.createVerticalStrut(30));
        content.add(createLabeledProgress("Response Rate", responseRate, blue, responseRate + ".0%"));

        content.add(Box.createVerticalStrut(25));
        int activeOfficials = 0;
        for (User user : cachedUsers) {
            if (user != null && user.getAttr_type() == 1 && isApproved(user.getVerify_status())) {
                activeOfficials++;
            }
        }
        JLabel activeOff = new JLabel("Active Officials: " + activeOfficials);
        activeOff.setForeground(purple);
        activeOff.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(activeOff);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlertsPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel alertIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/priorityalert_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            alertIcon.setIcon(icon);
        } catch (Exception e) {
            alertIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Priority Alerts");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(alertIcon);
        headerPanel.add(titleLabel);

        JPanel alertBox = new JPanel(new BorderLayout());
        alertBox.setBackground(new Color(255, 252, 230));
        alertBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 243, 150)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        alertBox.setPreferredSize(new Dimension(180, 30));

        JLabel labelLeft = new JLabel("Needs Attention");
        labelLeft.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JLabel labelRight = new JLabel("2");
        labelRight.setFont(new Font("SansSerif", Font.BOLD, 12));

        alertBox.add(labelLeft, BorderLayout.WEST);
        alertBox.add(labelRight, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(alertBox);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel catIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/categories_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            catIcon.setIcon(icon);
        } catch (Exception e) {
            catIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Category Breakdown");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(catIcon);
        headerPanel.add(titleLabel);

        JPanel content = new JPanel(new GridLayout(5, 1, 0, 8));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        String[] cats = {"Infrastructure", "Sanitation", "Environmental", "Public Safety", "Health Services"};
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String c : cats) counts.put(c, 0);

        int total = 0;
        for (Feedbacks feedback : cachedFeedbacks) {
            if (feedback == null) continue;
            String category = (feedback.getCategories() != null && feedback.getCategories().length > 0 && feedback.getCategories()[0] != null)
                    ? feedback.getCategories()[0].trim()
                    : "Others";
            if (counts.containsKey(category)) {
                counts.put(category, counts.get(category) + 1);
                total++;
            }
        }

        int safeTotal = Math.max(1, total);
        for (String c : cats) {
            int count = counts.get(c);
            int percent = (count * 100) / safeTotal;
            content.add(createLabeledProgress(c, percent, purple, count + " (" + percent + "%)"));
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActivityPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel activityIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/recentActivity_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            activityIcon.setIcon(icon);
        } catch (Exception e) {
            activityIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(activityIcon);
        headerPanel.add(titleLabel);

        DefaultListModel<String[]> model = new DefaultListModel<>();
        for (String[] row : buildRecentActivities()) model.addElement(row);

        JList<String[]> list = new JList<>(model);
        list.setFixedCellHeight(55);

        list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            JLabel bulletLabel = new JLabel(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(purple);
                    g2.fillOval(x, y + 6, 8, 8);
                    g2.dispose();
                }

                @Override
                public int getIconWidth() {
                    return 12;
                }

                @Override
                public int getIconHeight() {
                    return 20;
                }
            });

            JPanel textContainer = new JPanel(new GridLayout(2, 1, 0, 2));
            textContainer.setOpaque(false);

            JLabel actionLabel = new JLabel(value[0]);
            actionLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            JLabel infoLabel = new JLabel("by " + value[1] + "  •  " + value[2]);
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            infoLabel.setForeground(Color.GRAY);

            textContainer.add(actionLabel);
            textContainer.add(infoLabel);

            itemPanel.add(bulletLabel, BorderLayout.WEST);
            itemPanel.add(textContainer, BorderLayout.CENTER);

            if (isSelected) {
                itemPanel.setBackground(new Color(245, 245, 255));
            } else {
                itemPanel.setBackground(Color.WHITE);
            }

            return itemPanel;
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(300, 180));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private List<String[]> buildRecentActivities() {
        List<Feedbacks> sorted = new ArrayList<>();
        for (Feedbacks feedback : cachedFeedbacks) {
            if (feedback != null) sorted.add(feedback);
        }

        sorted.sort((a, b) -> {
            Date da = a.getIssued_date();
            Date db = b.getIssued_date();
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        List<String[]> rows = new ArrayList<>();
        int limit = Math.min(6, sorted.size());
        for (int i = 0; i < limit; i++) {
            Feedbacks feedback = sorted.get(i);
            String author = findUserNameById(feedback.getIssued_by());
            String status = getFeedbackStatus(feedback);
            String action = "New feedback submitted";
            if ("Resolved".equalsIgnoreCase(status)) {
                action = "Feedback resolved";
            } else if ("In Progress".equalsIgnoreCase(status) || "In-Progress".equalsIgnoreCase(status)) {
                action = "Feedback in progress";
            }
            rows.add(new String[]{action, author, formatTimeAgo(feedback.getIssued_date())});
        }

        if (rows.isEmpty()) {
            rows.add(new String[]{"No recent activity", "System", "-"});
        }
        return rows;
    }

    private String formatTimeAgo(Date date) {
        if (date == null) return "-";
        long diffMs = Math.max(0, System.currentTimeMillis() - date.getTime());
        long minutes = diffMs / (60 * 1000);
        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        return days + "d ago";
    }

    private String findUserNameById(int userId) {
        for (User user : cachedUsers) {
            if (user != null && user.getAttr_id() == userId) {
                return user.getName() != null ? user.getName() : (user.getUsername() != null ? user.getUsername() : ("User #" + userId));
            }
        }
        return "User #" + userId;
    }

    private JPanel createWeeklyActivityPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(0, 15));

        JPanel northContainer = new JPanel(new BorderLayout(0, 15));
        northContainer.setOpaque(false);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel calendarIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/7daysactivity_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            calendarIcon.setIcon(icon);
        } catch (Exception e) {
            calendarIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Last 7 Days Activity");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(calendarIcon);
        headerPanel.add(titleLabel);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);

        long weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        int newFeedbacks = 0;
        int responsesGiven = 0;
        int issuesResolved = 0;
        int totalUpvotes = 0;

        for (Feedbacks feedback : cachedFeedbacks) {
            if (feedback == null || feedback.getIssued_date() == null) continue;
            if (feedback.getIssued_date().getTime() < weekAgo) continue;

            newFeedbacks++;
            String status = getFeedbackStatus(feedback);
            if ("In Progress".equalsIgnoreCase(status) || "In-Progress".equalsIgnoreCase(status) || "Resolved".equalsIgnoreCase(status)) {
                responsesGiven++;
            }
            if ("Resolved".equalsIgnoreCase(status)) {
                issuesResolved++;
            }
            if (feedback.getAttr_upvote() != null) {
                totalUpvotes += feedback.getAttr_upvote().length;
            }
        }

        statsRow.add(createStatCard("New Feedbacks", String.valueOf(newFeedbacks), new Color(70, 130, 180)));
        statsRow.add(createStatCard("Responses Given", String.valueOf(responsesGiven), new Color(60, 179, 113)));
        statsRow.add(createStatCard("Issues Resolved", String.valueOf(issuesResolved), new Color(220, 20, 60)));
        statsRow.add(createStatCard("Total Upvotes", String.valueOf(totalUpvotes), new Color(255, 165, 0)));

        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(statsRow, BorderLayout.CENTER);
        panel.add(northContainer, BorderLayout.NORTH);

        JPanel contentPlaceholder = new JPanel();
        contentPlaceholder.setOpaque(false);
        panel.add(contentPlaceholder, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createLabeledProgress(String label, int val, Color col, String text) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel r = new JLabel(text);
        r.setFont(new Font("SansSerif", Font.BOLD, 11));
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(val);
        bar.setForeground(col);
        bar.setPreferredSize(new Dimension(100, 6));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(l, BorderLayout.WEST);
        top.add(r, BorderLayout.EAST);

        p.add(top, BorderLayout.NORTH);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createBasePanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    private JPanel createManageFeedbackPanel() {
        return new ManageFeedbackView();
    }

    class ManageFeedbackView extends JPanel {
        private static final Color BG = new Color(245, 246, 250);
        private static final Color CARD_BORDER = new Color(225, 225, 230);

        private JTextField searchField;
        private JComboBox<String> categoryBox;
        private JComboBox<String> statusBox;
        private JPanel listPanel;

        ManageFeedbackView() {
            setLayout(new BorderLayout());
            setBackground(BG);

            JPanel topSection = new JPanel();
            topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
            topSection.setBackground(BG);
            topSection.setBorder(new EmptyBorder(25, 30, 12, 30));

            JLabel title = new JLabel("Manage Feedback");
            title.setFont(new Font("Segoe UI", Font.BOLD, 26));
            title.setForeground(new Color(50, 50, 60));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel subtitle = new JLabel("Review submissions and update priorities/status quickly.");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitle.setForeground(Color.GRAY);
            subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            topSection.add(title);
            topSection.add(Box.createVerticalStrut(6));
            topSection.add(subtitle);
            topSection.add(Box.createVerticalStrut(15));

            JPanel filter = createFilterPanel();
            filter.setAlignmentX(Component.LEFT_ALIGNMENT);
            topSection.add(filter);

            JPanel topWrapper = new JPanel(new BorderLayout());
            topWrapper.setOpaque(false);
            topWrapper.add(topSection, BorderLayout.CENTER);
            add(topWrapper, BorderLayout.NORTH);

            JScrollPane list = createFeedbackList();
            list.setBorder(new EmptyBorder(0, 30, 25, 30));
            add(list, BorderLayout.CENTER);

            refreshFeedbackList();
        }

        private JPanel createFilterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(new CompoundBorder(new RoundedBorder(10),
                    new EmptyBorder(12, 14, 12, 14)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = 0;

            searchField = new JTextField();
            searchField.setPreferredSize(new Dimension(280, 32));
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 225)),
                    new EmptyBorder(6, 10, 6, 10)
            ));
            searchField.setToolTipText("Search feedback...");
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { refreshFeedbackList(); }
                @Override
                public void removeUpdate(DocumentEvent e) { refreshFeedbackList(); }
                @Override
                public void changedUpdate(DocumentEvent e) { refreshFeedbackList(); }
            });

            categoryBox = new JComboBox<>(new String[]{
                    "All Categories", "Infrastructure", "Sanitation", "Environmental", "Public Safety", "Health Services", "Others"
            });
            categoryBox.addActionListener(e -> refreshFeedbackList());

            statusBox = new JComboBox<>(new String[]{
                    "All Status", "Pending", "In Progress", "Resolved", "Archived"
            });
            statusBox.addActionListener(e -> refreshFeedbackList());

            gbc.gridx = 0;
            gbc.weightx = 0.60;
            panel.add(searchField, gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.20;
            panel.add(categoryBox, gbc);
            gbc.gridx = 2;
            gbc.weightx = 0.20;
            panel.add(statusBox, gbc);
            return panel;
        }

        private JScrollPane createFeedbackList() {
            listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setOpaque(false);

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getViewport().setBackground(BG);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            return scroll;
        }

        private void refreshFeedbackList() {
            if (listPanel == null) return;

            String query = searchField == null ? "" : searchField.getText().trim().toLowerCase();
            String selectedCategory = categoryBox == null ? "All Categories" : (String) categoryBox.getSelectedItem();
            String selectedStatus = statusBox == null ? "All Status" : (String) statusBox.getSelectedItem();

            listPanel.removeAll();

            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM dd, yyyy");
            int shown = 0;

            for (Feedbacks feedback : cachedFeedbacks) {
                if (feedback == null) continue;

                String status = getFeedbackStatus(feedback);
                String category = feedback.getCategories() != null && feedback.getCategories().length > 0 ? feedback.getCategories()[0] : "Others";
                String content = feedback.getContent() == null ? "" : feedback.getContent();
                String location = feedback.getLocation() == null ? "" : feedback.getLocation();
                String author = findUserName(feedback.getIssued_by());

                if (!"All Categories".equals(selectedCategory) && !selectedCategory.equalsIgnoreCase(category)) continue;
                if (!"All Status".equals(selectedStatus) && !selectedStatus.equalsIgnoreCase(status)) continue;

                String searchBlob = (content + " " + location + " " + author + " " + category + " " + status).toLowerCase();
                if (!query.isEmpty() && !searchBlob.contains(query)) continue;

                int votes = feedback.getAttr_upvote() == null ? 0 : feedback.getAttr_upvote().length;
                String date = feedback.getIssued_date() == null ? "-" : dateFormat.format(feedback.getIssued_date());

                listPanel.add(new FeedbackCard(feedback.getAttr_id(), status, category, content, location, date, author, votes));
                listPanel.add(Box.createVerticalStrut(10));
                shown++;
            }

            if (shown == 0) {
                JLabel empty = new JLabel("No feedbacks found.");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                empty.setForeground(new Color(120, 120, 130));
                empty.setBorder(new EmptyBorder(20, 12, 20, 12));
                listPanel.add(empty);
            }

            listPanel.revalidate();
            listPanel.repaint();
        }

        private String findUserName(int userId) {
            for (User user : cachedUsers) {
                if (user != null && user.getAttr_id() == userId) {
                    return user.getName() != null ? user.getName() : ("User #" + userId);
                }
            }
            return "User #" + userId;
        }

        private Object[] updateStatus(int feedbackId, String status) {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.sendRequest(new FeedbackStatusUpdateRequest(feedbackId, status));
            Object response = connection.receive();
            if (response instanceof Object[]) {
                return (Object[]) response;
            }
            return new Object[]{"500", "Invalid server response", null};
        }

        class FeedbackCard extends JPanel {
            FeedbackCard(int feedbackId, String status, String category, String text,
                         String location, String date, String author, int votes) {
                setLayout(new BorderLayout());
                setBackground(Color.WHITE);
                setBorder(new CompoundBorder(new RoundedBorder(12),
                        new EmptyBorder(12, 15, 12, 15)));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

                JPanel mainPanel = new JPanel(new BorderLayout(15, 1));
                mainPanel.setOpaque(false);

                JLabel voteCount = new JLabel("▲ " + votes);
                voteCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
                voteCount.setForeground(new Color(70, 70, 80));
                voteCount.setBorder(new EmptyBorder(0, 4, 0, 4));
                mainPanel.add(voteCount, BorderLayout.WEST);

                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
                centerPanel.setOpaque(false);

                JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
                tagRow.setOpaque(false);
                tagRow.add(createTag(status.toUpperCase(), new Color(220, 235, 255, 50), new Color(0, 90, 180)));
                tagRow.add(createTag(category, new Color(230, 220, 255, 50), new Color(100, 60, 180)));

                JLabel body = new JLabel("<html>" + text + "</html>");
                body.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel meta = new JLabel(location + " • " + author + " • " + date);
                meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                meta.setForeground(Color.GRAY);

                JPanel quickStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                quickStatusPanel.setOpaque(false);
                quickStatusPanel.add(new JLabel("Quick Status:"));

                JButton pendingBtn = buildFlatPillButton("Pending", new Color(255, 243, 224), new Color(230, 81, 0));
                JButton startBtn = buildFlatPillButton("Start", new Color(200, 220, 255), new Color(40, 70, 140));
                JButton resolveBtn = buildFlatPillButton("Resolve", new Color(200, 255, 200), new Color(35, 105, 35));

                pendingBtn.addActionListener(e -> handleStatusUpdate(feedbackId, "Pending"));
                startBtn.addActionListener(e -> handleStatusUpdate(feedbackId, "In Progress"));
                resolveBtn.addActionListener(e -> handleStatusUpdate(feedbackId, "Resolved"));

                quickStatusPanel.add(pendingBtn);
                quickStatusPanel.add(startBtn);
                quickStatusPanel.add(resolveBtn);

                centerPanel.add(tagRow);
                centerPanel.add(Box.createVerticalStrut(6));
                centerPanel.add(body);
                centerPanel.add(Box.createVerticalStrut(5));
                centerPanel.add(meta);
                centerPanel.add(Box.createVerticalStrut(8));
                centerPanel.add(quickStatusPanel);

                mainPanel.add(centerPanel, BorderLayout.CENTER);
                add(mainPanel, BorderLayout.CENTER);
            }

            private void handleStatusUpdate(int feedbackId, String status) {
                Object[] response = updateStatus(feedbackId, status);
                if (!"200".equals(response[0])) {
                    JOptionPane.showMessageDialog(this, String.valueOf(response[1]), "Update Failed", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                refreshAllData();
                refreshFeedbackList();
            }

            private JLabel createTag(String text, Color bg, Color fg) {
                JLabel label = new JLabel(text);
                label.setOpaque(true);
                label.setBackground(bg);
                label.setForeground(fg);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
                return label;
            }

            private JButton buildFlatPillButton(String text, Color bg, Color fg) {
                JButton btn = new JButton(text);
                btn.setFocusPainted(false);
                btn.setBackground(bg);
                btn.setForeground(fg);
                btn.setBorder(new EmptyBorder(3, 8, 3, 8));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return btn;
            }
        }

        class RoundedBorder extends AbstractBorder {
            private final int radius;

            RoundedBorder(int radius) {
                this.radius = radius;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
                g2.dispose();
            }
        }
    }

    static class InfoCard extends JPanel {
        public InfoCard(String title, String val, String sub, Color c, String iconPath) {
            setLayout(new BorderLayout(15, 0));
            setBackground(c);
            setPreferredSize(new Dimension(240, 130));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel iconLabel = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(new ImageIcon(iconPath)
                        .getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
                iconLabel.setIcon(icon);
            } catch (Exception e) {
                iconLabel.setPreferredSize(new Dimension(35, 35));
            }
            iconLabel.setVerticalAlignment(SwingConstants.TOP);

            JPanel textStack = new JPanel();
            textStack.setOpaque(false);
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

            JLabel t = new JLabel(title);
            t.setForeground(new Color(255, 255, 255, 200));
            t.setFont(new Font("SansSerif", Font.BOLD, 19));

            JLabel v = new JLabel(val);
            v.setForeground(Color.WHITE);
            v.setFont(new Font("SansSerif", Font.BOLD, 28));

            JLabel s = new JLabel(sub);
            s.setForeground(new Color(255, 255, 255, 160));
            s.setFont(new Font("SansSerif", Font.BOLD, 15));

            textStack.add(t);
            textStack.add(Box.createVerticalStrut(5));
            textStack.add(v);
            textStack.add(Box.createVerticalGlue());
            textStack.add(s);

            add(iconLabel, BorderLayout.WEST);
            add(textStack, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        private final int radius;
        private final Color color;

        RoundedButton(String label, int radius, Color color) {
            super(label);
            this.radius = radius;
            this.color = color;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class AdminConsoleView extends JPanel {
        private static final Color BG_COLOR = new Color(245, 246, 250);
        private static final Color CARD_BORDER = new Color(225, 225, 230);
        private static final Color PURPLE_BAR = new Color(80, 0, 150);
        private static final Color PURPLE_LIGHT = new Color(245, 238, 255);
        private static final Color DELETE_RED = new Color(220, 20, 60);

        private static final Color ROLE_CITIZEN_BG = new Color(240, 234, 255);
        private static final Color ROLE_CITIZEN_TXT = new Color(109, 40, 217);
        private static final Color ROLE_OFFICIAL_BG = new Color(232, 245, 255);
        private static final Color ROLE_OFFICIAL_TXT = new Color(30, 96, 160);

        private final java.util.List<UserRecord> users = new java.util.ArrayList<>();
        private final java.util.List<ConsoleUserCard> cards = new java.util.ArrayList<>();
        private final ClientConnection connection;
        private final Runnable onDataChanged;
        private JPanel listPanel;
        private JTextField searchField;
        private UserRecord selectedUser;

        public AdminConsoleView(ClientConnection connection, Runnable onDataChanged) {
            this.connection = connection;
            this.onDataChanged = onDataChanged;
            setLayout(new BorderLayout(0, 15));
            setBackground(BG_COLOR);
            setBorder(new EmptyBorder(25, 30, 25, 30));

            add(buildTitleSection(), BorderLayout.NORTH);

            RoundedPanel mainCard = new RoundedPanel(20, Color.WHITE);
            mainCard.setLayout(new BorderLayout());
            mainCard.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

            mainCard.add(buildDarkCommandCenter(), BorderLayout.NORTH);
            mainCard.add(buildCardsSection(), BorderLayout.CENTER);

            add(mainCard, BorderLayout.CENTER);

            loadUsersFromServer();
        }

        private JPanel buildTitleSection() {
            JPanel pnl = new JPanel(new BorderLayout());
            pnl.setOpaque(false);
            JLabel title = new JLabel("Users");
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(new Color(50, 50, 60));
            JLabel subtitle = new JLabel("Manage user accounts and organization");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitle.setForeground(Color.GRAY);

            JPanel textGrp = new JPanel(new GridLayout(2, 1));
            textGrp.setOpaque(false);
            textGrp.add(title);
            textGrp.add(subtitle);
            pnl.add(textGrp, BorderLayout.WEST);
            return pnl;
        }

        private JPanel buildDarkCommandCenter() {
            JPanel container = new JPanel();
            container.setBackground(Color.WHITE);
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            JPanel infoBar = new JPanel(new BorderLayout());
            infoBar.setBackground(PURPLE_LIGHT);
            infoBar.setBorder(new EmptyBorder(10, 12, 10, 12));

            JPanel actionBar = new JPanel(new BorderLayout());
            actionBar.setBackground(PURPLE_BAR);
            actionBar.setBorder(new EmptyBorder(8, 12, 8, 12));

            JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            leftGroup.setOpaque(false);
            JLabel sortLabel = new JLabel("SORT BY:");
            sortLabel.setForeground(Color.WHITE);
            sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

            JButton sortNameBtn = createBadgeButton("Name (A-Z)", new Color(255, 255, 255, 40));
            JButton sortIdBtn = createBadgeButton("User ID", new Color(255, 255, 255, 40));
            JButton sortPendingBtn = createBadgeButton("Pending", new Color(255, 255, 255, 40));
            JButton sortActiveBtn = createBadgeButton("Active", new Color(255, 255, 255, 40));

            sortNameBtn.addActionListener(e -> {
                users.sort(java.util.Comparator.comparing(u -> u.name.toLowerCase()));
                selectedUser = null;
                refreshCards();
            });
            sortIdBtn.addActionListener(e -> {
                users.sort(java.util.Comparator.comparing(u -> u.userId));
                selectedUser = null;
                refreshCards();
            });
            sortPendingBtn.addActionListener(e -> {
                users.sort(java.util.Comparator
                        .comparingInt((UserRecord u) -> "Pending".equalsIgnoreCase(u.status) ? 0 : 1)
                        .thenComparing(u -> u.name.toLowerCase()));
                selectedUser = null;
                refreshCards();
            });
            sortActiveBtn.addActionListener(e -> {
                users.sort(java.util.Comparator
                        .comparingInt((UserRecord u) -> "Active".equalsIgnoreCase(u.status) ? 0 : 1)
                        .thenComparing(u -> u.name.toLowerCase()));
                selectedUser = null;
                refreshCards();
            });
            JLabel searchLabel = new JLabel("SEARCH BY:");
            searchLabel.setForeground(Color.WHITE);
            searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

            JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightGroup.setOpaque(false);
            JButton deleteBtn = createBadgeButton("Delete Selected", DELETE_RED);
            deleteBtn.addActionListener(e -> {
                if (selectedUser == null) {
                    JOptionPane.showMessageDialog(this, "Please select a user to delete.");
                    return;
                }

                Object[] response = deleteUser(selectedUser.numericId);
                if (!"200".equals(response[0])) {
                    JOptionPane.showMessageDialog(this, String.valueOf(response[1]), "Delete Failed", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedUser = null;
                loadUsersFromServer();
                if (onDataChanged != null) onDataChanged.run();
            });
            rightGroup.add(deleteBtn);

            searchField = new JTextField();
                searchField.setPreferredSize(new Dimension(260, 24));
            searchField.setToolTipText("Search by name, role, status, phone, or address");
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 225)),
                    new EmptyBorder(4, 10, 4, 10)
            ));
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    refreshCards();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    refreshCards();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refreshCards();
                }
            });

            leftGroup.add(searchLabel);
            leftGroup.add(searchField);
            leftGroup.add(sortLabel);
            leftGroup.add(sortNameBtn);
            leftGroup.add(sortIdBtn);
            leftGroup.add(sortPendingBtn);
            leftGroup.add(sortActiveBtn);

            actionBar.add(leftGroup, BorderLayout.WEST);
            actionBar.add(rightGroup, BorderLayout.EAST);

            container.add(infoBar);
            container.add(actionBar);
            return container;
        }

        private JPanel buildCardsSection() {
            JPanel pnl = new JPanel(new BorderLayout());
            pnl.setOpaque(true);
            pnl.setBackground(Color.WHITE);
            pnl.setBorder(new EmptyBorder(8, 12, 12, 12));

            listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(Color.WHITE);

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.getViewport().setBackground(Color.WHITE);
            scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            pnl.add(scroll, BorderLayout.CENTER);
            return pnl;
        }

        private void refreshCards() {
            if (listPanel == null) return;

            if (selectedUser != null && !users.contains(selectedUser)) {
                selectedUser = null;
            }

            String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
            java.util.List<UserRecord> filteredUsers = new java.util.ArrayList<>();
            for (UserRecord user : users) {
                if (query.isEmpty() || matchesSearch(user, query)) {
                    filteredUsers.add(user);
                }
            }

            listPanel.removeAll();
            cards.clear();

            if (filteredUsers.isEmpty()) {
                JLabel empty = new JLabel("No users found.");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                empty.setForeground(new Color(120, 120, 130));
                empty.setBorder(new EmptyBorder(20, 12, 20, 12));
                listPanel.add(empty);
            } else {
                for (int i = 0; i < filteredUsers.size(); i++) {
                    UserRecord user = filteredUsers.get(i);
                    ConsoleUserCard card = new ConsoleUserCard(user, user == selectedUser);
                    card.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            selectedUser = user;
                            refreshCards();
                        }
                    });
                    cards.add(card);
                    listPanel.add(card);
                    if (i < filteredUsers.size() - 1) {
                        listPanel.add(Box.createVerticalStrut(10));
                    }
                }
            }

            listPanel.revalidate();
            listPanel.repaint();
        }

        private void loadUsersFromServer() {
            users.clear();
            Object[] response = getUsers();
            if ("200".equals(response[0]) && response[2] instanceof User[]) {
                for (User user : (User[]) response[2]) {
                    if (user == null) continue;
                    String role = switch (user.getAttr_type()) {
                        case 0 -> "Admin";
                        case 1 -> "Brgy Official";
                        default -> "Citizen";
                    };
                    String status = (user.getVerify_status() != null && "PENDING".equalsIgnoreCase(user.getVerify_status()))
                            ? "Pending"
                            : "Active";
                    String phone = (user.getContact() != null && user.getContact().length > 0) ? user.getContact()[0] : "-";
                    String address = user.getAddress() == null ? "-" : user.getAddress();
                    users.add(new UserRecord(user.getAttr_id(), String.format("USR_%03d", user.getAttr_id()), role,
                            user.getName() == null ? user.getUsername() : user.getName(), phone, address, status));
                }
            }
            refreshCards();
        }

        private Object[] getUsers() {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.sendRequest("GET_USERS");
            Object response = connection.receive();
            if (response instanceof Object[]) {
                return (Object[]) response;
            }
            return new Object[]{"500", "Invalid server response", null};
        }

        private Object[] updateUserStatus(int userId, String status) {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.sendRequest(new UserStatusUpdateRequest(userId, status));
            Object response = connection.receive();
            if (response instanceof Object[]) {
                return (Object[]) response;
            }
            return new Object[]{"500", "Invalid server response", null};
        }

        private Object[] deleteUser(int userId) {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.sendRequest(new DeleteUserRequest(userId));
            Object response = connection.receive();
            if (response instanceof Object[]) {
                return (Object[]) response;
            }
            return new Object[]{"500", "Invalid server response", null};
        }

        private boolean matchesSearch(UserRecord user, String query) {
            return user.name.toLowerCase().contains(query)
                    || user.status.toLowerCase().contains(query)
                    || user.role.toLowerCase().contains(query)
                    || user.phone.toLowerCase().contains(query)
                    || user.address.toLowerCase().contains(query);
        }

        private JButton createBadgeButton(String text, Color bg) {
            final Color normal = bg;
            final Color hover = new Color(
                    Math.min(255, bg.getRed() + 18),
                    Math.min(255, bg.getGreen() + 18),
                    Math.min(255, bg.getBlue() + 18),
                    Math.min(255, bg.getAlpha() + 35)
            );
            final Color pressed = new Color(
                    Math.max(0, bg.getRed() - 18),
                    Math.max(0, bg.getGreen() - 18),
                    Math.max(0, bg.getBlue() - 18),
                    bg.getAlpha()
            );

            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    ButtonModel model = getModel();
                    if (model.isPressed()) {
                        g2.setColor(pressed);
                    } else if (model.isRollover()) {
                        g2.setColor(hover);
                    } else {
                        g2.setColor(normal);
                    }

                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(Color.WHITE);
            btn.setBorder(new EmptyBorder(5, 12, 5, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setRolloverEnabled(true);
            btn.setContentAreaFilled(false);
            btn.setOpaque(false);
            btn.setBorderPainted(false);
            return btn;
        }

        static class UserRecord {
            int numericId;
            String userId;
            String role;
            String name;
            String phone;
            String address;
            String status;

            UserRecord(int numericId, String userId, String role, String name, String phone, String address, String status) {
                this.numericId = numericId;
                this.userId = userId;
                this.role = role;
                this.name = name;
                this.phone = phone;
                this.address = address;
                this.status = status;
            }
        }

        class ConsoleUserCard extends RoundedPanel {
            ConsoleUserCard(UserRecord user, boolean selected) {
                super(14, selected ? new Color(244, 238, 255) : Color.WHITE);
                setLayout(new BorderLayout(8, 4));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(selected ? new Color(125, 80, 220) : CARD_BORDER),
                        new EmptyBorder(6, 10, 6, 10)
                ));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                JPanel left = new JPanel();
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                left.setOpaque(false);

                JPanel topBadgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                topBadgeRow.setOpaque(false);
                topBadgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel name = new JLabel(user.name + " (" + user.userId + ")");
                name.setFont(new Font("Segoe UI", Font.BOLD, 13));
                name.setForeground(new Color(45, 45, 58));
                topBadgeRow.add(name);

                topBadgeRow.add(createRoleBadge(user.role));

                Color statusBg = "Active".equalsIgnoreCase(user.status)
                    ? new Color(232, 245, 233)
                    : new Color(255, 243, 224);
                Color statusFg = "Active".equalsIgnoreCase(user.status)
                    ? new Color(46, 125, 50)
                    : new Color(230, 81, 0);
                topBadgeRow.add(createPill(user.status, statusBg, statusFg));

                JLabel meta = new JLabel("• " + user.phone + " • " + user.address);
                meta.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                meta.setForeground(new Color(120, 120, 135));

                left.add(topBadgeRow);
                left.add(meta);

                JPanel right = new JPanel();
                right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
                right.setOpaque(false);

                JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
                actionRow.setOpaque(false);

                JButton viewProfileBtn = createMiniAction("View Profile", new Color(236, 231, 253), new Color(82, 44, 171));
                viewProfileBtn.addActionListener(e -> {
                    Window window = SwingUtilities.getWindowAncestor(ConsoleUserCard.this);
                    if (window instanceof JFrame frame) {
                        UserProfileDialog dialog = new UserProfileDialog(frame, toUser(user));
                        dialog.setVisible(true);
                    }
                });
                actionRow.add(viewProfileBtn);

                if ("Pending".equalsIgnoreCase(user.status)) {
                    JButton approveBtn = createMiniAction("Approve", new Color(232, 245, 233), new Color(46, 125, 50));
                    approveBtn.addActionListener(e -> {
                        Object[] response = updateUserStatus(user.numericId, "APPROVED");
                        if (!"200".equals(response[0])) {
                            JOptionPane.showMessageDialog(this, String.valueOf(response[1]), "Approve Failed", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        user.status = "Active";
                        refreshCards();
                        if (onDataChanged != null) onDataChanged.run();
                    });
                    actionRow.add(approveBtn);
                }

                right.add(actionRow);

                add(left, BorderLayout.CENTER);
                add(right, BorderLayout.EAST);
            }

            private JButton createMiniAction(String text, Color bg, Color fg) {
                JButton btn = new JButton(text);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBackground(bg);
                btn.setForeground(fg);
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(fg, 1, true),
                    new EmptyBorder(2, 8, 2, 8)
                ));
                return btn;
            }

            private User toUser(UserRecord record) {
                int id = record.numericId;

                int type = "Brgy Official".equalsIgnoreCase(record.role) ? 1 : 2;
                String username = record.name == null ? "" : record.name.trim().replace(" ", "_").toLowerCase();
                String verifyStatus = "Pending".equalsIgnoreCase(record.status) ? "PENDING" : "APPROVED";

                return new User(
                        id,
                        type,
                        username,
                        "",
                        record.name,
                        record.address,
                        new String[]{record.phone},
                        verifyStatus,
                        0
                );
            }

            private JComponent createPill(String text, Color bg, Color fg) {
                JLabel label = new JLabel(text, SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBackground(bg);
                label.setForeground(fg);
                label.setFont(new Font("Segoe UI", Font.BOLD, 10));
                label.setBorder(new EmptyBorder(2, 9, 2, 9));
                label.setPreferredSize(new Dimension(88, 20));
                label.setMinimumSize(new Dimension(88, 20));
                return label;
            }

            private JComponent createRoleBadge(String role) {
                boolean isCitizen = role.equalsIgnoreCase("Citizen");

                Color bg = isCitizen ? new Color(244, 238, 255) : new Color(232, 245, 255);
                Color fg = isCitizen ? new Color(109, 40, 217) : new Color(30, 96, 160);

                JLabel label = new JLabel(role, SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBackground(bg);
                label.setForeground(fg);
                label.setFont(new Font("Segoe UI", Font.BOLD, 10));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(fg, 1, true),
                        new EmptyBorder(2, 9, 2, 9)
                ));
                label.setPreferredSize(new Dimension(112, 20));
                label.setMinimumSize(new Dimension(112, 20));
                return label;
            }
        }

        static class RoundedPanel extends JPanel {
            private final int r;
            private final Color c;

            public RoundedPanel(int r, Color c) {
                this.r = r;
                this.c = c;
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
                g2.dispose();
            }
        }
    }

    static class SystemControlsView extends JPanel {
        private final Color COLOR_CARD_BORDER = new Color(230, 233, 237);
        private final Color COLOR_BLUE_BORDER = new Color(207, 226, 255);
        private final Color COLOR_BLUE_BG = new Color(240, 247, 255);
        private final Color COLOR_TEXT_MAIN = new Color(50, 50, 60);
        private final Color COLOR_TEXT_SUB = new Color(100, 100, 110);
        private final Color COLOR_ACCENT_BLUE = new Color(45, 85, 155);

        public SystemControlsView() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            JPanel mainPanel = new JPanel();
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(new EmptyBorder(30, 200, 30, 200));

            JLabel titleLabel = new JLabel("System Controls");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
            titleLabel.setForeground(COLOR_TEXT_MAIN);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitleLabel = new JLabel("Administrative tools and system management");
            subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            subtitleLabel.setForeground(COLOR_TEXT_SUB);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            mainPanel.add(titleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            mainPanel.add(subtitleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

            JPanel cardsWrapper = new JPanel(new GridLayout(2, 1, 0, 20));
            cardsWrapper.setOpaque(false);
            cardsWrapper.setMaximumSize(new Dimension(850, 550));
            cardsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel adminCard = createRoundedCard(Color.WHITE, COLOR_CARD_BORDER);
            adminCard.setLayout(new BorderLayout());

            JLabel adminTitle = new JLabel(" Administrator Privileges");
            adminTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
            adminTitle.setForeground(COLOR_TEXT_MAIN);
            adminCard.add(adminTitle, BorderLayout.NORTH);

            JPanel listPanel = new JPanel(new GridLayout(5, 1, 0, 0));
            listPanel.setOpaque(false);
            listPanel.setBorder(new EmptyBorder(10, 15, 0, 0));

            String[] items = {
                    "Full access to all feedback submissions",
                    "Ability to update feedback status and responses",
                    "Archive and delete capabilities",
                    "System-wide statistics and reporting",
                    "User management and oversight"
            };

            for (String item : items) {
                JPanel rowItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                rowItem.setOpaque(false);

                JLabel dotLabel = new JLabel("\u2022   ");
                dotLabel.setForeground(COLOR_ACCENT_BLUE);
                dotLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

                JLabel textLabel = new JLabel(item);
                textLabel.setForeground(COLOR_TEXT_SUB);
                textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

                rowItem.add(dotLabel);
                rowItem.add(textLabel);
                listPanel.add(rowItem);
            }

            adminCard.add(listPanel, BorderLayout.CENTER);

            JPanel sdgContainer = createRoundedCard(COLOR_BLUE_BG, COLOR_BLUE_BORDER);
            sdgContainer.setLayout(new BoxLayout(sdgContainer, BoxLayout.Y_AXIS));

            JLabel sdgHeader = new JLabel("SDG Alignment");
            sdgHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
            sdgHeader.setForeground(COLOR_ACCENT_BLUE);
            sdgHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sdgSub = new JLabel("This Community Feedback System aligns with the United Nations Sustainable Development Goals:");
            sdgSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            sdgSub.setForeground(COLOR_TEXT_SUB);
            sdgSub.setAlignmentX(Component.CENTER_ALIGNMENT);

            sdgContainer.add(sdgHeader);
            sdgContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            sdgContainer.add(sdgSub);
            sdgContainer.add(Box.createRigidArea(new Dimension(0, 15)));

            sdgContainer.add(createGoalBox("SDG 11: Sustainable Cities and Communities",
                    "Making cities inclusive, safe, resilient and sustainable by enabling citizen participation in community development."));

            sdgContainer.add(Box.createRigidArea(new Dimension(0, 10)));

            sdgContainer.add(createGoalBox("SDG 16: Peace, Justice and Strong Institutions",
                    "Promoting peaceful and inclusive societies, providing access to justice and building effective, accountable institutions..."));

            cardsWrapper.add(adminCard);
            cardsWrapper.add(sdgContainer);

            mainPanel.add(cardsWrapper);

            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(Color.WHITE);
            add(scrollPane, BorderLayout.CENTER);
        }

        private JPanel createRoundedCard(Color bg, Color border) {
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                    g2.setColor(border);
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                    g2.dispose();
                }
            };
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(15, 20, 15, 20));
            return card;
        }

        private JPanel createGoalBox(String title, String desc) {
            JPanel box = createRoundedCard(Color.WHITE, COLOR_CARD_BORDER);
            box.setLayout(new BorderLayout(0, 5));
            box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JLabel t = new JLabel(title);
            t.setFont(new Font("SansSerif", Font.BOLD, 13));
            t.setForeground(COLOR_ACCENT_BLUE);

            JTextArea d = new JTextArea(desc);
            d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            d.setForeground(COLOR_TEXT_SUB);
            d.setLineWrap(true);
            d.setWrapStyleWord(true);
            d.setOpaque(false);
            d.setEditable(false);
            d.setFocusable(false);
            d.setCursor(null);

            box.add(t, BorderLayout.NORTH);
            box.add(d, BorderLayout.CENTER);
            return box;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
