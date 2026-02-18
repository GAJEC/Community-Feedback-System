package client.view.brgyofficials;

import client.model.ClientConnection;
import client.view.admin.AdminDashboard;
import client.view.common.Buttons;
import client.view.common.LogoutPopUp;
import client.view.common.RoundedInputField;
import client.view.common.UserProfileDialog;
import client.view.brgyofficials.panel.PostAnnouncementDialog;
import client.view.strings.Colors;
import client.view.strings.Images;
import common.BroadcastMessage;
import common.FeedbackInternalNoteUpdateRequest;
import common.FeedbackPriorityUpdateRequest;
import common.FeedbackStatusUpdateRequest;
import common.Feedbacks;
import common.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static client.view.strings.Fonts.SegoeUi;

public class BrgyOfficialDashboard extends JFrame {

    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color CARD_SHADOW = new Color(200, 200, 200);
    private static final Color GREEN = new Color(34, 153, 84);
    private static final Color USER_BOX_BG = new Color(248, 250, 252);

    private final JPanel contentContainer;
    private User user;
    private BrgyOfficialHeaderPanel header;
    private final ClientConnection connection;
    private Feedbacks[] cachedFeedbacks = new Feedbacks[0];
    private User[] cachedUsers = new User[0];
    private AdminDashboard.AdminConsoleView usersView;
    private String activeTab = "View Feedbacks";

    public BrgyOfficialDashboard(User user){
        this(user, new ClientConnection());
    }

    public BrgyOfficialDashboard(User user, ClientConnection connection){
        this.user = user;
        this.connection = connection;
        if (!this.connection.isConnected()) {
            this.connection.connect();
        }
        this.connection.setBroadcastListener(this::handleBroadcast);
        refreshDataCaches();

        setTitle("Barangay Official Dashboard");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        header = new BrgyOfficialHeaderPanel(user, "View Feedbacks");
        mainPanel.add(header, BorderLayout.NORTH);

        header.viewFeedbackBtn.addActionListener(e -> {
            activeTab = "View Feedbacks";
            header.setActiveTab("View Feedbacks");
            loadDashboard();
        });
        header.statsBtn.addActionListener(e -> {
            activeTab = "Statistics";
            header.setActiveTab("Statistics");
            loadStatisticsView();
        });
        header.usersBtn.addActionListener(e -> {
            activeTab = "Users";
            header.setActiveTab("Users");
            loadUsersView();
        });
        header.postAnnouncementBtn.addActionListener(e -> {
            String displayName = (user != null && user.getName() != null) ? user.getName() : "Barangay Official";
            int userId = user != null ? user.getAttr_id() : 0;
            PostAnnouncementDialog dialog = new PostAnnouncementDialog(this, displayName, this.connection, userId);
            dialog.setVisible(true);
        });

        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.add(contentContainer, BorderLayout.CENTER);
        setContentPane(mainPanel);
        loadDashboard();
        setVisible(true);
    }

    private void handleBroadcast(BroadcastMessage message) {
        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case NEW_FEEDBACK:
                case VOTE_UPDATE:
                case STATUS_UPDATE:
                case PRIORITY_UPDATE:
                case INTERNAL_NOTE_UPDATE:
                    if (message.getData() instanceof Feedbacks feedback) {
                        upsertFeedbackCache(feedback);
                        reloadActiveTab();
                    }
                    break;
                case NEW_USER:
                    if (message.getData() instanceof User newUser) {
                        upsertUserCache(newUser);
                        if (usersView != null) {
                            usersView.onUsersUpdated(cachedUsers);
                        }
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private void reloadActiveTab() {
        if ("Statistics".equals(activeTab)) {
            loadStatisticsView();
        } else if ("Users".equals(activeTab)) {
            loadUsersView();
        } else {
            loadDashboard();
        }
    }

    private void refreshDataCaches() {
        cachedFeedbacks = fetchFeedbacks();
        cachedUsers = fetchUsers();
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

    private void upsertFeedbackCache(Feedbacks incoming) {
        if (incoming == null) return;
        for (int i = 0; i < cachedFeedbacks.length; i++) {
            Feedbacks existing = cachedFeedbacks[i];
            if (existing != null && existing.getAttr_id() == incoming.getAttr_id()) {
                cachedFeedbacks[i] = incoming;
                return;
            }
        }
        Feedbacks[] expanded = new Feedbacks[cachedFeedbacks.length + 1];
        System.arraycopy(cachedFeedbacks, 0, expanded, 0, cachedFeedbacks.length);
        expanded[cachedFeedbacks.length] = incoming;
        cachedFeedbacks = expanded;
    }

    private void upsertUserCache(User incoming) {
        if (incoming == null) return;
        for (int i = 0; i < cachedUsers.length; i++) {
            User existing = cachedUsers[i];
            if (existing != null && existing.getAttr_id() == incoming.getAttr_id()) {
                cachedUsers[i] = incoming;
                return;
            }
        }
        User[] expanded = new User[cachedUsers.length + 1];
        System.arraycopy(cachedUsers, 0, expanded, 0, cachedUsers.length);
        expanded[cachedUsers.length] = incoming;
        cachedUsers = expanded;
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

    private Object[] updatePriority(int feedbackId, String priority) {
        if (!connection.isConnected()) {
            connection.connect();
        }
        connection.sendRequest(new FeedbackPriorityUpdateRequest(feedbackId, priority));
        Object response = connection.receive();
        if (response instanceof Object[]) {
            return (Object[]) response;
        }
        return new Object[]{"500", "Invalid server response", null};
    }

    private Object[] updateInternalNote(int feedbackId, String internalNote) {
        if (!connection.isConnected()) {
            connection.connect();
        }
        connection.sendRequest(new FeedbackInternalNoteUpdateRequest(feedbackId, internalNote));
        Object response = connection.receive();
        if (response instanceof Object[]) {
            return (Object[]) response;
        }
        return new Object[]{"500", "Invalid server response", null};
    }

    private String getFeedbackStatus(Feedbacks feedback) {
        if (feedback == null || feedback.getAttr_status() == null || feedback.getAttr_status().length == 0 || feedback.getAttr_status()[0] == null) {
            return "Pending";
        }
        return feedback.getAttr_status()[0].trim();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return new SimpleDateFormat("MMM dd, yyyy").format(date);
    }

    private void loadDashboard() {
        refreshDataCaches();
        contentContainer.removeAll();
        JPanel wrapper = new JPanel(new BorderLayout(0, 20));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Feedback Overview");
        title.setFont(SegoeUi(Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tagline = new JLabel("Review and manage community feedbacks");
        tagline.setFont(SegoeUi(Font.ITALIC, 14));
        tagline.setForeground(new Color(120, 120, 120));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(tagline);
        wrapper.add(titlePanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(statsSummaryRow(), BorderLayout.NORTH);
        center.add(feedbackPanel(), BorderLayout.CENTER);
        wrapper.add(center, BorderLayout.CENTER);
        contentContainer.add(wrapper);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private void loadManageFeedbackView() {
        refreshDataCaches();
        contentContainer.removeAll();

        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Manage Feedback");
        title.setFont(SegoeUi(Font.BOLD, 26));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        body.add(createFilterPanel());
        body.add(Box.createVerticalStrut(12));

        JScrollPane list = createFeedbackList();
        list.setPreferredSize(new Dimension(1000, 520));
        body.add(list);

        wrapper.add(body, BorderLayout.CENTER);
        contentContainer.add(wrapper, BorderLayout.CENTER);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private void loadStatisticsView() {
        refreshDataCaches();
        contentContainer.removeAll();

        JPanel wrapper = new JPanel(new BorderLayout(0, 20));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Feedback Statistics");
        title.setFont(SegoeUi(Font.BOLD, 26));
        title.setForeground(Colors.NORMAL_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Overview of community feedback status");
        tagline.setFont(SegoeUi(Font.ITALIC, 14));
        tagline.setForeground(Colors.SUB_TEXT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(tagline);
        wrapper.add(titlePanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(statisticsSummaryRow(), BorderLayout.NORTH);
        center.add(statisticsCategoryPanel(), BorderLayout.CENTER);

        wrapper.add(center, BorderLayout.CENTER);
        contentContainer.add(wrapper, BorderLayout.CENTER);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private void loadUsersView() {
        refreshDataCaches();
        contentContainer.removeAll();

        usersView = new AdminDashboard.AdminConsoleView(connection, this::refreshDataCaches);
        usersView.onUsersUpdated(cachedUsers);

        contentContainer.add(usersView, BorderLayout.CENTER);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private JPanel statisticsSummaryRow() {
        int total = cachedFeedbacks.length;
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

        JPanel statsRow = new JPanel(new GridLayout(1, 5, 15, 15));
        statsRow.setOpaque(false);
        statsRow.add(new StatisticsStatCard("Total Feedbacks", String.valueOf(total), Colors.LIGHTEST_GRAY));
        statsRow.add(new StatisticsStatCard("Pending", String.valueOf(pending), new Color(255, 249, 216)));
        statsRow.add(new StatisticsStatCard("In Progress", String.valueOf(inProgress), new Color(233, 238, 252)));
        statsRow.add(new StatisticsStatCard("Resolved", String.valueOf(resolved), new Color(220, 240, 200)));
        statsRow.add(new StatisticsStatCard("Archived", String.valueOf(archived), Colors.LIGHTEST_GRAY));
        return statsRow;
    }

    private JPanel statisticsCategoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colors.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(new CompoundBorder(
                new RoundedBorder(12),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Feedbacks by Category");
        title.setFont(SegoeUi(Font.BOLD, 18));
        title.setForeground(Colors.NORMAL_TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createStatisticsCategoryRow("Event Suggestion", countCategory("Event Suggestion")));
        panel.add(createStatisticsCategoryRow("Missing Person", countCategory("Missing Person")));
        panel.add(createStatisticsCategoryRow("Noise Complaint", countCategory("Noise Complaint")));
        panel.add(createStatisticsCategoryRow("Infrastructure", countCategory("Infrastructure")));
        panel.add(createStatisticsCategoryRow("Other", countCategory("Other")));

        return panel;
    }

    private JPanel createStatisticsCategoryRow(String category, int value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 0, 15, 0));

        JLabel name = new JLabel(category);
        name.setFont(SegoeUi(Font.PLAIN, 14));
        name.setForeground(Colors.NORMAL_TEXT);

        JLabel count = new JLabel(String.valueOf(value));
        count.setFont(SegoeUi(Font.BOLD, 14));
        count.setForeground(Colors.DARK_GRAY);

        RoundedProgressBar bar = new RoundedProgressBar(0, 5);
        bar.setValue(value);
        bar.setForeground(Colors.SECONDARY);
        bar.setBackground(Colors.LIGHT_GRAY);
        bar.setPreferredSize(new Dimension(100, 8));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(name, BorderLayout.WEST);
        topRow.add(count, BorderLayout.EAST);

        row.add(topRow, BorderLayout.NORTH);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private void loadPostAnnouncementView() {
        contentContainer.removeAll();

        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Post Announcement");
        title.setFont(SegoeUi(Font.BOLD, 26));
        wrapper.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        JTextField announcementTitle = new JTextField();
        announcementTitle.setFont(SegoeUi(Font.PLAIN, 14));
        announcementTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JTextArea announcementBody = new JTextArea(8, 20);
        announcementBody.setLineWrap(true);
        announcementBody.setWrapStyleWord(true);
        announcementBody.setFont(SegoeUi(Font.PLAIN, 14));

        Buttons publishBtn = new Buttons("Publish", 10, new Color(34, 153, 84), Color.WHITE, new Color(28, 130, 72), SegoeUi(Font.BOLD, 12));
        publishBtn.setPreferredSize(new Dimension(120, 36));
        publishBtn.addActionListener(e -> {
            String titleText = announcementTitle.getText().trim();
            String bodyText = announcementBody.getText().trim();
            if (titleText.isEmpty() || bodyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete title and message.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Announcement posted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            announcementTitle.setText("");
            announcementBody.setText("");
        });

        form.add(new JLabel("Title"));
        form.add(Box.createVerticalStrut(6));
        form.add(announcementTitle);
        form.add(Box.createVerticalStrut(12));
        form.add(new JLabel("Message"));
        form.add(Box.createVerticalStrut(6));
        form.add(new JScrollPane(announcementBody));
        form.add(Box.createVerticalStrut(12));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(publishBtn);
        form.add(actions);

        wrapper.add(form, BorderLayout.CENTER);
        contentContainer.add(wrapper, BorderLayout.CENTER);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private JPanel statsSummaryRow() {
        int urgent = 0;
        int pending = 0;
        int inProgress = 0;

        for (Feedbacks feedback : cachedFeedbacks) {
            String status = getFeedbackStatus(feedback);
            if ("Pending".equalsIgnoreCase(status)) pending++;
            if ("In Progress".equalsIgnoreCase(status) || "In-Progress".equalsIgnoreCase(status)) inProgress++;
            if (("Pending".equalsIgnoreCase(status) || "In Progress".equalsIgnoreCase(status) || "In-Progress".equalsIgnoreCase(status))
                    && feedback != null && feedback.getAttr_upvote() != null && feedback.getAttr_upvote().length >= 10) {
                urgent++;
            }
        }

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 15));
        statsRow.setOpaque(false);
        statsRow.add(new StatCard("Urgent", String.valueOf(urgent), new Color(252, 232, 232)));
        statsRow.add(new StatCard("Pending", String.valueOf(pending), new Color(255, 249, 216)));
        statsRow.add(new StatCard("In Progress", String.valueOf(inProgress), new Color(233, 238, 252)));
        statsRow.add(new StatCard("High Priority", String.valueOf(Math.max(0, urgent - 1)), new Color(246, 236, 254)));
        return statsRow;
    }

    private int countCategory(String categoryName) {
        int count = 0;
        for (Feedbacks feedback : cachedFeedbacks) {
            if (feedback == null || feedback.getCategories() == null || feedback.getCategories().length == 0 || feedback.getCategories()[0] == null) {
                continue;
            }
            if (categoryName.equalsIgnoreCase(feedback.getCategories()[0].trim())) {
                count++;
            }
        }
        return count;
    }

    private String getUserLabel(int userId) {
        for (User cachedUser : cachedUsers) {
            if (cachedUser != null && cachedUser.getAttr_id() == userId) {
                String fullName = cachedUser.getName();
                if (fullName != null && !fullName.trim().isEmpty()) {
                    return fullName.trim();
                }

                String username = cachedUser.getUsername();
                if (username != null && !username.trim().isEmpty()) {
                    return username.trim();
                }
            }
        }
        return ""+userId;
    }

    static class BrgyOfficialHeaderPanel extends JPanel {

        private static final Color HEADER_COLOR = new Color(34, 153, 84);
        private static final Color NAV_COLOR = Colors.WHITE;
        private static final Color ACTIVE_TEXT = new Color(114, 181, 112);

        public Buttons profileButton, logoutButton, viewFeedbackBtn, statsBtn, usersBtn, postAnnouncementBtn;

        private String currentActiveTab;
        private User user;

        BrgyOfficialHeaderPanel(User user, String activeTab) {
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
            tagline.setForeground(new Color(255, 255, 255, 180));

            textPanel.add(title);
            textPanel.add(tagline);

            leftPnl.add(textPanel);
            headerPanel.add(leftPnl, BorderLayout.WEST);

            JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 30));
            rightPnl.setOpaque(false);

            String displayName = (user != null && user.getName() != null) ? user.getName() : "Barangay Official";
            profileButton = new Buttons(displayName, 30, Colors.LIGHT_GREEN, Colors.WHITE, Colors.YELLOW_GREEN, SegoeUi(1, 12));
            profileButton.setPreferredSize(new Dimension(200, 40));
            profileButton.setButtonIcon(Images.scaled(Images.PROFILE, 25, 25), 2);

            Buttons.applyClick(profileButton, () -> {
                UserProfileDialog userProfileDialog =
                        new UserProfileDialog((JFrame) SwingUtilities.getWindowAncestor(headerPanel), user);
                userProfileDialog.setVisible(true);
            });

            logoutButton = new Buttons("Logout", 20, Colors.DARK_BUTTON, Colors.WHITE, Colors.RED, SegoeUi(1, 12));
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

            viewFeedbackBtn = new Buttons("View Feedbacks", 0);
            statsBtn = new Buttons("Statistics", 0);
            usersBtn = new Buttons("Users", 0);
            postAnnouncementBtn = new Buttons("Post Announcement", 0);

            styleTab(viewFeedbackBtn, "View Feedbacks".equals(currentActiveTab));
            styleTab(statsBtn, "Statistics".equals(currentActiveTab));
            styleTab(usersBtn, "Users".equals(currentActiveTab));
            styleTab(postAnnouncementBtn, "Post Announcement".equals(currentActiveTab));

            navBar.add(viewFeedbackBtn);
            navBar.add(statsBtn);
            navBar.add(usersBtn);
            navBar.add(postAnnouncementBtn);

            add(navBar);
        }

        public void setActiveTab(String tabName) {
            currentActiveTab = tabName;
            styleTab(viewFeedbackBtn, "View Feedbacks".equals(currentActiveTab));
            styleTab(statsBtn, "Statistics".equals(currentActiveTab));
            styleTab(usersBtn, "Users".equals(currentActiveTab));
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
                btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 0, 0, 0)));
            }
        }
    }

    private JPanel feedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(createFilterPanel(), BorderLayout.NORTH);
        panel.add(createFeedbackList(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(USER_BOX_BG);
        panel.setBorder(new CompoundBorder(new RoundedBorder(10),
                new EmptyBorder(15, 20, 15, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        panel.add(new RoundedInputField(Images.scaled(Images.SEARCH, 15, 15), "Search feedbacks...", false), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        panel.add(new JComboBox<>(new String[]{"All Categories", "Infrastructure", "Sanitation",
                "Environmental", "Public Safety", "Health Services", "Others"}), gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        panel.add(new JComboBox<>(new String[]{"All Status", "Pending", "In Progress",
                "Resolved", "Archived"}), gbc);
        return panel;
    }

    private JScrollPane createFeedbackList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        Feedbacks[] ordered = Arrays.copyOf(cachedFeedbacks, cachedFeedbacks.length);
        Arrays.sort(ordered, (a, b) -> {
            Date da = a != null ? a.getIssued_date() : null;
            Date db = b != null ? b.getIssued_date() : null;
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        int rendered = 0;
        for (Feedbacks feedback : ordered) {
            if (feedback == null) continue;

            String status = getFeedbackStatus(feedback);
            String category = (feedback.getCategories() != null && feedback.getCategories().length > 0 && feedback.getCategories()[0] != null)
                    ? feedback.getCategories()[0]
                    : "Others";
            String title = feedback.getTitle() != null && !feedback.getTitle().trim().isEmpty()
                    ? feedback.getTitle().trim()
                    : "Untitled Feedback";
                String priority = feedback.getPriority() != null && !feedback.getPriority().trim().isEmpty()
                    ? feedback.getPriority().trim()
                    : "No priority set";
                    String internalNote = feedback.getInternal_note() != null ? feedback.getInternal_note() : "";
            String content = feedback.getContent() != null ? feedback.getContent() : "";
            String text = "<b>" + title + "</b><br/>" + content;
            String location = feedback.getLocation() != null ? feedback.getLocation() : "-";
                    String author = getUserLabel(feedback.getIssued_by());
            String date = formatDate(feedback.getIssued_date());
            int votes = feedback.getAttr_upvote() != null ? feedback.getAttr_upvote().length : 0;

                    list.add(new FeedbackCard(feedback.getAttr_id(), status, category, priority, internalNote, text, location, date, author, votes));
            list.add(Box.createVerticalStrut(10));
            rendered++;
        }

        if (rendered == 0) {
            JLabel empty = new JLabel("No feedbacks available.");
            empty.setFont(SegoeUi(Font.PLAIN, 13));
            empty.setForeground(Color.GRAY);
            list.add(empty);
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    static class StatCard extends JPanel {
        StatCard(String title, String value, Color bg) {
            setLayout(new BorderLayout());
            setBackground(bg);
            setBorder(new CompoundBorder(new RoundedBorder(12),
                    new EmptyBorder(15, 15, 15, 15)));
            JLabel t = new JLabel(title);
            t.setFont(SegoeUi(Font.BOLD, 14));
            JLabel v = new JLabel(value);
            v.setFont(SegoeUi(Font.BOLD, 26));
            v.setForeground(GREEN);
            add(t, BorderLayout.NORTH);
            add(v, BorderLayout.CENTER);
        }
    }

    static class StatisticsStatCard extends JPanel {
        StatisticsStatCard(String title, String value, Color bg) {
            setLayout(new BorderLayout());
            setBackground(bg);
            setBorder(new CompoundBorder(
                    new RoundedBorder(12),
                    new EmptyBorder(15, 15, 15, 15)
            ));

            JLabel t = new JLabel(title);
            t.setFont(SegoeUi(Font.BOLD, 14));
            t.setForeground(Colors.NORMAL_TEXT);

            JLabel v = new JLabel(value);
            v.setFont(SegoeUi(Font.BOLD, 26));
            v.setForeground(Colors.SECONDARY);

            add(t, BorderLayout.NORTH);
            add(v, BorderLayout.CENTER);
        }
    }

    static class RoundedProgressBar extends JProgressBar {
        private final int radius = 10;
        private final int barHeight = 8;

        public RoundedProgressBar(int min, int max) {
            super(min, max);
            setOpaque(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int yOffset = (height - barHeight) / 2;

            g2.setColor(getBackground());
            g2.fillRoundRect(0, yOffset, width, barHeight, radius, radius);

            int progressWidth = (int) (width * ((double) getValue() / getMaximum()));
            g2.setColor(getForeground());
            g2.fillRoundRect(0, yOffset, progressWidth, barHeight, radius, radius);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = barHeight + 4;
            return d;
        }
    }

    class FeedbackCard extends JPanel {

        private final int feedbackId;
        private String currentPriority;
        private String currentInternalNote;

        FeedbackCard(int feedbackId, String status, String category, String priority, String internalNote, String text,
                     String location, String date, String author, int votes) {
            this.feedbackId = feedbackId;
            this.currentPriority = (priority != null && !priority.trim().isEmpty()) ? priority.trim() : "No priority set";
            this.currentInternalNote = internalNote != null ? internalNote : "";
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(new RoundedBorder(12),
                    new EmptyBorder(12, 15, 12, 15)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            JPanel mainPanel = new JPanel(new BorderLayout(15, 1));
            mainPanel.setOpaque(false);
            mainPanel.add(voteWrapper(votes), BorderLayout.WEST);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            JPanel tagRow = createTagRow(status, category, currentPriority);
            JPanel textStack = createTextStack(text, location, date, author);
            tagRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            textStack.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(tagRow);
            centerPanel.add(Box.createVerticalStrut(6));
            centerPanel.add(textStack);
            centerPanel.add(Box.createVerticalStrut(10));

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            bottomPanel.setOpaque(false);
            bottomPanel.add(priorityAndStatusPanel());
            bottomPanel.add(Box.createVerticalStrut(6));
            bottomPanel.add(addNotesPanel());
            bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(bottomPanel);;

            mainPanel.add(centerPanel, BorderLayout.CENTER);
            add(mainPanel, BorderLayout.CENTER);
        }

        private JPanel voteWrapper(int votes) {
            JPanel votePanel = new JPanel();
            votePanel.setLayout(new BoxLayout(votePanel, BoxLayout.Y_AXIS));
            votePanel.setOpaque(false);
            JLabel upVote = new JLabel();
            upVote.setAlignmentX(Component.CENTER_ALIGNMENT);
            try {
                URL iconURL = getClass().getResource("/assets/upvotes_icon.png");
                if (iconURL != null) {
                    Image img = new ImageIcon(iconURL).getImage()
                            .getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    upVote.setIcon(new ImageIcon(img));
                }
            } catch (Exception ignored) {}
            JLabel voteCount = new JLabel(String.valueOf(votes));
            voteCount.setFont(SegoeUi(Font.BOLD, 14));
            voteCount.setAlignmentX(Component.CENTER_ALIGNMENT);
            votePanel.add(upVote);
            votePanel.add(Box.createVerticalStrut(2));
            votePanel.add(voteCount);
            JPanel voteWrapper = new JPanel(new GridBagLayout());
            voteWrapper.setOpaque(false);
            voteWrapper.setPreferredSize(new Dimension(50, 0));
            voteWrapper.add(votePanel);
            return voteWrapper;
        }

        private JPanel createTagRow(String status, String category, String priority) {
            JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
            tagRow.setOpaque(false);
            tagRow.add(createTag(status, new Color(220,235,255, 50), new Color(0,90,180)));
            tagRow.add(createTag(category, new Color(230,220,255, 50), new Color(100,60,180)));
            tagRow.add(createPriorityTag(priority));
            return tagRow;
        }

        private JLabel createPriorityTag(String priority) {
            String normalized = priority == null ? "No priority set" : priority.trim();
            return switch (normalized.toLowerCase()) {
                case "urgent" -> createTag("Urgent", new Color(255, 225, 225), new Color(200, 50, 50));
                case "high" -> createTag("High", new Color(255, 235, 220), new Color(190, 95, 20));
                case "medium" -> createTag("Medium", new Color(255, 245, 210), new Color(160, 120, 20));
                case "low" -> createTag("Low", new Color(230, 248, 220), new Color(80, 130, 40));
                default -> createTag("No priority set", new Color(230,230,230, 50), Color.GRAY);
            };
        }

        private JLabel createTag(String text, Color bg, Color fg) {
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setBackground(bg);
            label.setForeground(fg);
            label.setFont(SegoeUi(Font.BOLD, 11));
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            return label;
        }

        private JPanel createTextStack(String text, String location, String date, String author) {
            JLabel body = new JLabel("<html>" + text + "</html>");
            body.setFont(SegoeUi(Font.BOLD, 14));
            JLabel meta = new JLabel(String.format("📍 %s  •  👤 %s  •  📅 %s", location, author, date));
            meta.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            meta.setForeground(Color.GRAY);
            JPanel textStack = new JPanel();
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
            textStack.setOpaque(false);
            textStack.setBorder(new EmptyBorder(0, 0, 3, 0));
            textStack.add(body);
            textStack.add(Box.createVerticalStrut(5));
            textStack.add(meta);
            return textStack;
        }

        private JPanel priorityAndStatusPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panel.setOpaque(false);
            Font btnFont = SegoeUi(Font.BOLD, 8);
            Color labelColor = new Color(50,50,50);
            Dimension priorityBtnSize = new Dimension(65, 22);
            Dimension statusBtnSize = new Dimension(65, 22);
            JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            priorityPanel.setOpaque(false);
            Buttons urgentBtn = new Buttons("Urgent", 6, new Color(255, 180, 180), new Color(230, 100, 90));
            Buttons highBtn = new Buttons("High", 6, new Color(255, 220, 180), new Color(230, 160, 100));
            Buttons mediumBtn = new Buttons("Medium", 6, new Color(255, 240, 180), new Color(210, 180, 110));
            Buttons lowBtn = new Buttons("Low", 6, new Color(220, 240, 200), new Color(170, 200, 140));

            urgentBtn.addActionListener(e -> applyPriority("Urgent"));
            highBtn.addActionListener(e -> applyPriority("High"));
            mediumBtn.addActionListener(e -> applyPriority("Medium"));
            lowBtn.addActionListener(e -> applyPriority("Low"));

            for (Buttons btn : new Buttons[]{urgentBtn, highBtn, mediumBtn, lowBtn}) {
                btn.setPreferredSize(priorityBtnSize);
                btn.setFont(btnFont);
                btn.setForeground(Color.DARK_GRAY);
                priorityPanel.add(btn);
            }
            JLabel priorityLabel = new JLabel("Set Priority: ");
            priorityLabel.setForeground(labelColor);
            priorityLabel.setFont(btnFont);
            priorityPanel.add(priorityLabel, 0);
            JPanel quickStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            quickStatusPanel.setOpaque(false);
            Buttons startBtn = new Buttons("Start", 6,
                    new Color(200, 220, 255), new Color(140, 160, 240));
            Buttons resolveBtn = new Buttons("Resolve", 6,
                    new Color(200, 255, 200), new Color(100, 170, 100));
            Buttons pendingBtn = new Buttons("Pending", 6,
                    new Color(255, 243, 224), new Color(230, 81, 0));

            pendingBtn.addActionListener(e -> applyStatus("Pending"));
            startBtn.addActionListener(e -> applyStatus("In Progress"));
            resolveBtn.addActionListener(e -> applyStatus("Resolved"));

            for (Buttons btn : new Buttons[]{startBtn, resolveBtn}) {
                btn.setPreferredSize(statusBtnSize);
                btn.setFont(btnFont);
                btn.setForeground(new Color(50, 50, 50, 150));
                quickStatusPanel.add(btn);
            }
            pendingBtn.setPreferredSize(statusBtnSize);
            pendingBtn.setFont(btnFont);
            pendingBtn.setForeground(new Color(50, 50, 50, 150));
            quickStatusPanel.add(pendingBtn);
            JLabel quickStatusLabel = new JLabel("Quick Status: ");
            quickStatusLabel.setForeground(labelColor);
            quickStatusLabel.setFont(btnFont);
            quickStatusPanel.add(quickStatusLabel, 0);
            panel.add(priorityPanel);
            panel.add(quickStatusPanel);
            return panel;
        }

        private void applyStatus(String nextStatus) {
            Object[] response = updateStatus(feedbackId, nextStatus);
            if (!"200".equals(response[0])) {
                JOptionPane.showMessageDialog(BrgyOfficialDashboard.this,
                        String.valueOf(response[1]),
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            refreshDataCaches();
            reloadActiveTab();
        }

        private void applyPriority(String nextPriority) {
            if (nextPriority.equalsIgnoreCase(currentPriority)) {
                return;
            }
            Object[] response = updatePriority(feedbackId, nextPriority);
            if (!"200".equals(response[0])) {
                JOptionPane.showMessageDialog(BrgyOfficialDashboard.this,
                        String.valueOf(response[1]),
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentPriority = nextPriority;
            refreshDataCaches();
            reloadActiveTab();
        }

        private JPanel addNotesPanel() {

            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setOpaque(false);
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            headerPanel.setOpaque(false);
            JLabel toggleLabel = new JLabel("Add internal note →");
            toggleLabel.setForeground(new Color(0, 128, 0));
            toggleLabel.setFont(SegoeUi(Font.BOLD, 12));
            toggleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            headerPanel.add(toggleLabel);
            container.add(headerPanel);

            JPanel notePanel = new JPanel();
            notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.Y_AXIS));
            notePanel.setOpaque(false);
            notePanel.setVisible(false);
            JTextArea noteArea = new JTextArea(4, 30);
            noteArea.setLineWrap(true);
            noteArea.setWrapStyleWord(true);
            noteArea.setFont(SegoeUi(Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(noteArea);
            scrollPane.setPreferredSize(new Dimension(400, 80));
            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
            buttonRow.setOpaque(false);

            JButton saveButton = new JButton("Save");
            saveButton.setFocusPainted(false);
            saveButton.setBackground(new Color(34, 153, 84));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFont(SegoeUi(Font.BOLD, 12));
            buttonRow.add(saveButton);
            notePanel.add(scrollPane);
            notePanel.add(buttonRow);
            container.add(Box.createVerticalStrut(5));
            container.add(notePanel);

            if (!currentInternalNote.trim().isEmpty()) {
                noteArea.setText(currentInternalNote);
            }

            saveButton.addActionListener(e -> {
                String text = noteArea.getText().trim();
                Object[] response = updateInternalNote(feedbackId, text);
                if (!"200".equals(response[0])) {
                    JOptionPane.showMessageDialog(FeedbackCard.this,
                            String.valueOf(response[1]), "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                currentInternalNote = text;
                JOptionPane.showMessageDialog(FeedbackCard.this,
                        "Internal note saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                refreshDataCaches();
                reloadActiveTab();
            });

            toggleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                boolean expanded = false;
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    expanded = !expanded;
                    notePanel.setVisible(expanded);
                    if (expanded) {
                        toggleLabel.setText("Hide note ↓");
                        noteArea.setText(currentInternalNote);
                    } else {
                        toggleLabel.setText("Add internal note →");
                    }
                    FeedbackCard.this.revalidate();
                    FeedbackCard.this.repaint();
                }
            });
            return container;
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        RoundedBorder(int radius) { this.radius = radius; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_SHADOW);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new BrgyOfficialDashboard(new User("brgyofficial", "password"))
        );
    }
}