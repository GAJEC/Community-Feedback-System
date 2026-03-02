package client.view.residents;
import client.controller.ResidentDashboardController;
import client.view.common.Buttons;
import client.view.common.CustomPanel;
import client.view.common.InfoRow;
import client.view.common.RoundedInputField;
import client.view.residents.panels.FeedbackSubmitPopUp;
import client.view.residents.panels.ClientHeaderPanel;
import client.view.residents.panels.ClientFeedbackDetailDialog;
import client.view.strings.Colors;
import client.view.strings.Fonts;
import client.view.strings.Images;
import common.Announcement;
import common.Feedbacks;
import common.User;
import common.VoteRequest;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static client.view.common.Buttons.createArrowButton;
import static client.view.strings.Colors.*;
import static client.view.strings.Fonts.SegoeUi;

public class ResidentDashboard extends JFrame {

    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color PRIMARY_TEAL = SUB_PRIMARY;
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color USER_BOX_BG = new Color(248, 250, 252);

    private JPanel contentContainer;
    private boolean isViewMode;
    private User user;
    private ResidentDashboardListener listener;
    private Map<Integer, User> userCache = new HashMap<>();
    private Map<Integer, FeedbackCard> feedbackCards = new HashMap<>();
    private JPanel feedbackListPanel;
    private JPanel announcementCardsPanel;
    private RoundedInputField feedbackSearchField;
    private JComboBox<String> categoryFilterBox;
    private JComboBox<String> statusFilterBox;
    private static ResidentDashboardController residentDashboardController;

    private RoundedTextField titleField;
    private JComboBox<String> categoryBox;
    private RoundedTextField locationField;
    private JTextArea descArea;

    public ResidentDashboard(boolean viewMode, User user, ResidentDashboardListener listener) {
        this.listener = listener;
        // Cache the logged-in user
        userCache.put(user.getAttr_id(), user);
        this.isViewMode = viewMode;
        this.user = user;
        setTitle("Community Feedback System");

        this.setIconImage(Images.ICON.getImage());

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        ClientHeaderPanel header = new ClientHeaderPanel(user);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(header, BorderLayout.NORTH);

        header.setListener(new ClientHeaderPanel.HeaderListener() {
            @Override
            public void onProfileRequested(User user) {
                if (listener != null) {
                    Window window = SwingUtilities.getWindowAncestor(ResidentDashboard.this);
                    listener.onProfileRequested(window instanceof JFrame ? (JFrame) window : null, user);
                }
            }

            @Override
            public void onLogoutRequested() {
                if (listener != null) {
                    Window window = SwingUtilities.getWindowAncestor(ResidentDashboard.this);
                    listener.onLogoutRequested(window instanceof JFrame ? (JFrame) window : null);
                }
            }
        });

        header.viewBtn.addActionListener(e -> switchToView());
        header.submitBtn.addActionListener(e -> switchToSubmit());


        header.styleTab(header.viewBtn, viewMode);
        header.styleTab(header.submitBtn, !viewMode);

        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        mainPanel.add(contentContainer, BorderLayout.CENTER);

        setContentPane(mainPanel);

        if (viewMode) {
            loadViewContent();
        } else {
            loadSubmitContent();
        }

        setVisible(true);
    }

    public boolean isViewMode() {
        return isViewMode;
    }

    public void onApplyFiltersRequested() {
        applyFilters();
    }

    public void onRefreshFeedbacksRequested() {
        refreshFeedbackList();
    }

    public void onRefreshAnnouncementsRequested() {
        refreshAnnouncementWidget();
    }

    private void refreshAnnouncementWidget() {
        if (announcementCardsPanel == null) {
            refreshFeedbackList();
            return;
        }
        loadAnnouncements(announcementCardsPanel);
    }

    private void refreshFeedbackList() {
        if (isViewMode) {
            loadViewContent();
        }
    }

    private void switchToView() {
        if (!isViewMode) {
            isViewMode = true;
            ClientHeaderPanel header = (ClientHeaderPanel) ((JPanel) getContentPane()).getComponent(0);
            header.styleTab(header.viewBtn, true);
            header.styleTab(header.submitBtn, false);
            loadViewContent();
        }
    }

    private void switchToSubmit() {
        if (isViewMode) {
            isViewMode = false;
            ClientHeaderPanel header = (ClientHeaderPanel) ((JPanel) getContentPane()).getComponent(0);
            header.styleTab(header.viewBtn, false);
            header.styleTab(header.submitBtn, true);
            loadSubmitContent();
        }
    }

    private void loadViewContent() {
        contentContainer.removeAll();
        contentContainer.add(createViewFeedbackPanel());
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private void loadSubmitContent() {
        contentContainer.removeAll();
        JScrollPane scrollPane = new JScrollPane(createSubmitFeedbackPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentContainer.add(scrollPane);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private JPanel createViewFeedbackPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 0));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        JLabel title = new JLabel("Community Feedbacks");
        title.setFont(SegoeUi(1, 26));
        container.add(title, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new BorderLayout(0, 10));
        centerContent.setOpaque(false);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        feedbackListPanel = listPanel;

        listPanel.add(Box.createVerticalStrut(5));

        if (listener != null) {
            listener.onLoadFeedbacks();
        }
        applyFilters();

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        centerContent.add(createFilterPanel(), BorderLayout.NORTH);
        centerContent.add(scroll, BorderLayout.CENTER);

        container.add(centerContent, BorderLayout.CENTER);
        container.add(createAnnouncementWidget(), BorderLayout.EAST);

        return container;
    }

    private JPanel createAnnouncementWidget() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(280, 600));

        CustomPanel widget = new CustomPanel(20, WHITE);
        widget.setOffsetShadow(LIGHTEST_GRAY,3);
        widget.setLayout(new BoxLayout(widget, BoxLayout.Y_AXIS));
        widget.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Latest Announcements");
        label.setFont(SegoeUi(1, 16));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        widget.add(label);

        announcementCardsPanel = new JPanel();
        announcementCardsPanel.setLayout(new BoxLayout(announcementCardsPanel, BoxLayout.Y_AXIS));
        announcementCardsPanel.setOpaque(false);
        widget.add(announcementCardsPanel);
        loadAnnouncements(announcementCardsPanel);

        sidebar.add(widget, BorderLayout.NORTH);
        return sidebar;
    }

    private void loadAnnouncements(JPanel targetPanel) {
        if (targetPanel == null) return;
        targetPanel.removeAll();

        Announcement[] announcements = listener != null ? listener.getLatestAnnouncements(5) : new Announcement[0];
        if (announcements.length == 0) {
            JLabel empty = new JLabel("No announcements yet.");
            empty.setFont(SegoeUi(Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            targetPanel.add(empty);
            targetPanel.revalidate();
            targetPanel.repaint();
            return;
        }

        for (int i = 0; i < announcements.length; i++) {
            Announcement ann = announcements[i];
            if (ann == null) continue;

            String title = ann.getTitle() != null && !ann.getTitle().trim().isEmpty()
                    ? ann.getTitle().trim()
                    : "Announcement";
            String message = ann.getMessage() != null ? ann.getMessage().trim() : "";
            String postedBy = ann.getPosted_by_name() != null && !ann.getPosted_by_name().trim().isEmpty()
                    ? ann.getPosted_by_name().trim()
                    : (ann.getPosted_by() > 0 ? "Official #" + ann.getPosted_by() : "Barangay Official");
            String postedAt = listener != null ? listener.formatTimeAgo(ann.getPosted_at()) : "-";

            targetPanel.add(new AnnouncementCard(title, message, postedBy, postedAt));
            if (i < announcements.length - 1) {
                targetPanel.add(Box.createVerticalStrut(10));
            }
        }

        targetPanel.revalidate();
        targetPanel.repaint();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6;
        JPanel searchContainer = new JPanel(new BorderLayout(0, 5));
        searchContainer.setOpaque(false);

        feedbackSearchField = new RoundedInputField(Images.scaled(Images.SEARCH, 15, 15), "Search feedbacks...", false);
        feedbackSearchField.setFieldSize(0, 35);
        feedbackSearchField.addTextChangeListener(this::applyFilters);
        searchContainer.add(feedbackSearchField, BorderLayout.CENTER);
        panel.add(searchContainer, gbc);

        gbc.gridx = 1; gbc.weightx = 0.2;
        JPanel catContainer = new JPanel(new BorderLayout(0, 5));
        catContainer.setOpaque(false);
        categoryFilterBox = dropdownBox(new String[]{"All Categories", "Infrastructure", "Health", "Safety", "Environment", "Public Services", "Transportation", "Utilities", "Community Welfare", "Missing Person", "Others"});
        categoryFilterBox.addActionListener(e -> applyFilters());
        catContainer.add(categoryFilterBox, BorderLayout.CENTER);
        panel.add(catContainer, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        JPanel statContainer = new JPanel(new BorderLayout(0, 5));
        statContainer.setOpaque(false);
        statusFilterBox = dropdownBox(new String[]{"All Status", "Pending", "In Progress", "Resolved"});
        statusFilterBox.addActionListener(e -> applyFilters());
        statContainer.add(statusFilterBox, BorderLayout.CENTER);
        panel.add(statContainer, gbc);

        JPanel sortRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sortRow.setOpaque(false);
        InfoRow sortLabel = new InfoRow(Images.scaled(Images.SORT, 15, 15),"Sort by:");
        sortRow.add(sortLabel);

        Buttons sortBtn = new Buttons(
                "Date",
                10,                    // radius
                new Color(240, 240, 240), // normal
                BLACK,
                new Color(210, 210, 210), // hover
                SegoeUi(Font.PLAIN, 12)
        );
        sortRow.add(sortBtn);

        Buttons upVotesBtn = new Buttons(
                "Upvotes",
                10,                    // radius
                new Color(240, 240, 240), // normal
                BLACK,
                new Color(210, 210, 210), // hover
                SegoeUi(Font.PLAIN, 12)
        );
        sortRow.add(upVotesBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.weightx = 1.0; gbc.insets = new Insets(15, 5, 0, 5);
        panel.add(sortRow, gbc);

        return panel;
    }

    private JPanel createSubmitFeedbackPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(20, 0));
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(15, 40, 15, 40));

        CustomPanel card = new CustomPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(750, 780));
        card.setBackgroundColor(Colors.WHITE);

        card.setOffsetShadow(new Color(0,0,0,30), 4);
        card.setRadius(20);

        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Submit Community Feedback");
        title.setFont(SegoeUi(Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitle = new JLabel("Help improve the community by reporting issues");
        subTitle.setFont(SegoeUi(Font.PLAIN, 14));
        subTitle.setForeground(Colors.GRAY);
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String displayName = (user != null && user.getName() != null && !user.getName().trim().isEmpty())
            ? user.getName().trim()
            : (user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty()
                ? user.getUsername().trim()
                : "Resident");
        String displayId = (user != null) ? String.valueOf(user.getAttr_id()) : "N/A";
        JPanel userBlock = createUserProfileBlock(displayName, displayId);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(new Color(230, 230, 230));

        titleField = new RoundedTextField(20);
        JPanel titlePanel = createFieldPanel("Issue Title", titleField);

        String[] categories = {"Event Suggestion", "Missing Person", "Noise Complaint", "Infrastructure", "Other"};
        categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(SegoeUi(Font.PLAIN, 13));
        categoryBox.setBackground(Colors.WHITE);
        categoryBox.setFocusable(false);

        categoryBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = super.createArrowButton();
                b.setContentAreaFilled(false);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });

        JPanel categoryWrapper = new JPanel(new BorderLayout());
        categoryWrapper.setBackground(Colors.WHITE);
        categoryWrapper.setBorder(new RoundedBorder(10));
        categoryWrapper.setPreferredSize(new Dimension(200, 40));
        categoryBox.setBorder(new EmptyBorder(0, 5, 0, 0));
        categoryWrapper.add(categoryBox, BorderLayout.CENTER);

        JPanel categoryPanel = createFieldPanel("Category", categoryWrapper);

        locationField = new RoundedTextField(20);
        JPanel locationPanel = createFieldPanel("Location / Landmark", locationField);

        descArea = new JTextArea(8, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(SegoeUi(Font.PLAIN, 13));
        descArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane areaScroll = new JScrollPane(descArea);
        areaScroll.setBorder(new RoundedBorder(10));
        areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        JPanel descPanel = createFieldPanel("Detailed Description", areaScroll);

        JLabel processingNote = new JLabel("Your feedback will be processed anonymously if preferred.");
        processingNote.setFont(Fonts.SegoeUi(Font.ITALIC, 12));
        processingNote.setForeground(new Color(100, 100, 100));
        processingNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        Buttons submitBtn = new Buttons(
                "Submit Feedback",
                12,                  // radius
                PRIMARY_TEAL,        // normal background
                Color.WHITE,         // foreground
                PRIMARY_TEAL.brighter(), // hover color
                Fonts.SegoeUi(Font.BOLD, 13) // font
        );

        Buttons cancelBtn = new Buttons(
                "Cancel",
                12,
                DANGER_RED,
                Color.WHITE,
                DANGER_RED.brighter(),
                Fonts.SegoeUi(Font.BOLD, 13)
        );


        cancelBtn.setPreferredSize(new Dimension(120, 40));
        submitBtn.setPreferredSize(new Dimension(180, 40));

        submitBtn.addActionListener(e -> handleFeedbackSubmit());

        cancelBtn.addActionListener(e -> clearForm());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subTitle);
        card.add(Box.createVerticalStrut(20));
        card.add(userBlock);
        card.add(Box.createVerticalStrut(20));
        card.add(separator);
        card.add(Box.createVerticalStrut(25));
        card.add(titlePanel);
        card.add(Box.createVerticalStrut(15));
        card.add(categoryPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(locationPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(descPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(processingNote);
        card.add(Box.createVerticalStrut(30));
        card.add(buttonPanel);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createUserProfileBlock(String username, String userId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(USER_BOX_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setBorder(new CompoundBorder(
                new RoundedBorder(8),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(SegoeUi(Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));

        JLabel idLabel = new JLabel("ID: " + userId);
        idLabel.setFont(SegoeUi(Font.PLAIN, 12));
        idLabel.setForeground(Colors.GRAY);

        textPanel.add(nameLabel);
        textPanel.add(idLabel);

        JLabel iconLabel = new JLabel("👤 ");
        iconLabel.setFont(new Font("Segoe Ui Emoji", Font.PLAIN, 24));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Colors.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("<html>" + labelText + " <span style='color:#E74C3C;'>*</span></html>");
        label.setFont(SegoeUi(Font.BOLD, 13));
        label.setForeground(new Color(80, 80, 80));

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void handleFeedbackSubmit() {
        String title = titleField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String location = locationField.getText().trim();
        String description = descArea.getText().trim();

        Object[] response = listener != null
            ? listener.submitFeedback(title, category, location, description)
            : new Object[]{"500", "No handler available", null};

        String statusCode = (String) response[0];
        String message = (String) response[1];

        if ("200".equals(statusCode)) {
            FeedbackSubmitPopUp dialog = new FeedbackSubmitPopUp(ResidentDashboard.this);
            dialog.setLocationRelativeTo(ResidentDashboard.this);
            dialog.setVisible(true);

            clearForm();
            switchToView();
        } else if ("400".equals(statusCode)) {
            JOptionPane.showMessageDialog(this,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                message,
                "Submission Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        if (titleField != null) titleField.setText("");
        if (categoryBox != null) categoryBox.setSelectedIndex(0);
        if (locationField != null) locationField.setText("");
        if (descArea != null) descArea.setText("");
    }

    private void applyFilters() {
        if (feedbackListPanel == null) {
            return;
        }

        feedbackListPanel.removeAll();
        feedbackCards.clear();
        feedbackListPanel.add(Box.createVerticalStrut(5));

        String feedbackLoadErrorMessage = listener != null
            ? listener.getFeedbackLoadErrorMessage()
            : "Unable to load feedbacks.";
        if (feedbackLoadErrorMessage != null) {
            JLabel errorLabel = new JLabel("Error loading feedbacks: " + feedbackLoadErrorMessage);
            errorLabel.setFont(SegoeUi(Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            feedbackListPanel.add(errorLabel);
            feedbackListPanel.revalidate();
            feedbackListPanel.repaint();
            return;
        }

        if (listener == null || !listener.hasFeedbacks()) {
            JLabel noDataLabel = new JLabel("No feedbacks available yet. Be the first to submit!");
            noDataLabel.setFont(SegoeUi(Font.ITALIC, 14));
            noDataLabel.setForeground(Color.GRAY);
            noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            feedbackListPanel.add(noDataLabel);
            feedbackListPanel.revalidate();
            feedbackListPanel.repaint();
            return;
        }

        String selectedCategory = categoryFilterBox != null && categoryFilterBox.getSelectedItem() != null
            ? categoryFilterBox.getSelectedItem().toString().trim()
            : "All Categories";
        String selectedStatus = statusFilterBox != null && statusFilterBox.getSelectedItem() != null
            ? statusFilterBox.getSelectedItem().toString().trim()
            : "All Status";
        String query = feedbackSearchField != null ? feedbackSearchField.getText() : "";

        java.util.List<Feedbacks> filteredFeedbacks = listener != null
            ? listener.getFilteredAndSortedFeedbacks(
            selectedCategory,
            selectedStatus,
            query,
            this::getUserName
        )
            : new java.util.ArrayList<>();

        if (filteredFeedbacks.isEmpty()) {
            JLabel emptyFilterLabel = new JLabel("No feedbacks matched your filters.");
            emptyFilterLabel.setFont(SegoeUi(Font.ITALIC, 14));
            emptyFilterLabel.setForeground(Color.GRAY);
            emptyFilterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            feedbackListPanel.add(emptyFilterLabel);
            feedbackListPanel.revalidate();
            feedbackListPanel.repaint();
            return;
        }

        for (Feedbacks feedback : filteredFeedbacks) {
            FeedbackCard card = buildFeedbackCard(feedback);
            feedbackCards.put(feedback.getAttr_id(), card);
            feedbackListPanel.add(card);
            feedbackListPanel.add(Box.createVerticalStrut(8));
        }

        feedbackListPanel.revalidate();
        feedbackListPanel.repaint();
    }

    private FeedbackCard buildFeedbackCard(Feedbacks feedback) {
        String status = firstNonEmpty(feedback.getAttr_status(), "Pending");
        String category = firstNonEmpty(feedback.getCategories(), "General");
        String title = feedback.getTitle() != null && !feedback.getTitle().trim().isEmpty()
                ? feedback.getTitle().trim()
                : "Untitled Feedback";
        String priority = feedback.getPriority() != null && !feedback.getPriority().trim().isEmpty()
                ? feedback.getPriority().trim()
                : "No priority set";
        String content = feedback.getContent();
        String location = feedback.getLocation();
        String authorName = getUserName(feedback.getIssued_by());

        Date issuedDate = feedback.getIssued_date();
        String date = issuedDate != null
                ? new SimpleDateFormat("MMM dd, yyyy").format(issuedDate)
                : "-";

        return new FeedbackCard(feedback, status, category, priority, title, content, location, date, authorName);
    }

    private String firstNonEmpty(String[] values, String fallback) {
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return fallback;
    }

    private String getUserName(int userId) {
        if (userCache.containsKey(userId)) {
            User cachedUser = userCache.get(userId);
            return cachedUser.getName() != null ? cachedUser.getName() : "User #" + userId;
        }
        return "User #" + userId;
    }

    private JComboBox<String> dropdownBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(150, 30));
        combo.setBackground(Color.WHITE);
        combo.setFont(SegoeUi(Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return combo;
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(5, 10, 5, 10); }
    }

    static class RoundedTextField extends JTextField {
        private static final int RADIUS = 10;
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setPreferredSize(new Dimension(getPreferredSize().width, 40));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
            super.paintComponent(g2);
            g2.dispose();
        }
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
            g2.dispose();
        }
    }

    class FeedbackCard extends JPanel {
        private final Feedbacks feedback;
        private final JLabel voteCountLabel;
        private final JButton upvoteBtn;
        private final JButton downvoteBtn;
        private boolean hasUpvoted = false;
        private boolean hasDownvoted = false;
        private volatile boolean voteInFlight = false;

        private static final Color UPVOTE_COLOR = new Color(0, 150, 0);
        private static final Color DOWNVOTE_COLOR = new Color(150, 0, 0);

        FeedbackCard(Feedbacks feedback, String status, String category, String priority, String title, String text, String location, String date, String author) {
            this.feedback = feedback;

            int userId = user.getAttr_id();
            if (feedback.getAttr_upvote() != null) {
                String uid = String.valueOf(userId);
                for (String id : feedback.getAttr_upvote()) {
                    if (uid.equals(id)) {
                        hasUpvoted = true;
                        break;
                    }
                }
            }
            if (feedback.getAttr_downvote() != null) {
                String uid = String.valueOf(userId);
                for (String id : feedback.getAttr_downvote()) {
                    if (uid.equals(id)) {
                        hasDownvoted = true;
                        break;
                    }
                }
            }

            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
            GridBagConstraints gbc = new GridBagConstraints();

            JPanel voteStack = new JPanel(new GridLayout(3, 1, 0, -5));
            voteStack.setOpaque(false);

            upvoteBtn = createArrowButton("▲", UPVOTE_COLOR);
            updateButtonColor(upvoteBtn, hasUpvoted, UPVOTE_COLOR);
            upvoteBtn.addActionListener(e -> handleUpvote());

            int votes = calculateVotes();
            voteCountLabel = new JLabel(String.valueOf(votes), SwingConstants.CENTER);
            voteCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            downvoteBtn = createArrowButton("▼", DOWNVOTE_COLOR);
            updateButtonColor(downvoteBtn, hasDownvoted, DOWNVOTE_COLOR);
            downvoteBtn.addActionListener(e -> handleDownvote());

            // Please wag nyo lagay yung generic hindi gumagana e
            upvoteBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (upvoteBtn.isEnabled()) {
                        upvoteBtn.setForeground(UPVOTE_COLOR);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (upvoteBtn.isEnabled()) {
                        upvoteBtn.setForeground(getUpvoteNormalColor());
                    }
                }
            });

            downvoteBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (downvoteBtn.isEnabled()) {
                        downvoteBtn.setForeground(DOWNVOTE_COLOR);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (downvoteBtn.isEnabled()) {
                        downvoteBtn.setForeground(getDownvoteNormalColor());
                    }
                }
            });

            voteStack.add(upvoteBtn);
            voteStack.add(voteCountLabel);
            voteStack.add(downvoteBtn);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2; gbc.insets = new Insets(0, 0, 0, 15);
            add(voteStack, gbc);

            JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            badgeRow.setOpaque(false);

            Buttons statusBtn = new Buttons(
                    status,
                    8,                    // radius
                    new Color(210, 235, 255), // normal
                    BLACK,
                    new Color(210, 235, 255),
                    SegoeUi(Font.PLAIN, 12)
            );
            badgeRow.add(statusBtn);

            Buttons categoryBtn = new Buttons(
                    category,
                    8,
                    new Color(245, 240, 250),
                    BLACK,
                    new Color(245, 240, 250),
                    SegoeUi(Font.PLAIN, 12)
            );
            badgeRow.add(categoryBtn);

                Buttons priorityBtn = createPriorityBadge(priority);
                badgeRow.add(priorityBtn);

            gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 5, 0);
            add(badgeRow, gbc);

            JPanel textStack = new JPanel();
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
            textStack.setOpaque(false);
            JLabel bodyLabel = new JLabel("<html><body style='width: 350px;'><b>" + title + "</b><br/>" + text + "</body></html>");
            bodyLabel.setFont(SegoeUi(Font.PLAIN, 14));
            JLabel meta = new JLabel(String.format("📍 %s  •  👤 %s  •  📅 %s", location, author, date));
            meta.setForeground(Color.GRAY);
            meta.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            textStack.add(bodyLabel);
            textStack.add(meta);
            gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);
            add(textStack, gbc);

            JButton detailsBtn = new JButton("View Details →");
            detailsBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            detailsBtn.setForeground(new Color(0, 102, 204));
            detailsBtn.setContentAreaFilled(false);
            detailsBtn.setBorderPainted(false);
            detailsBtn.setFocusPainted(false);
            detailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            detailsBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { detailsBtn.setForeground(new Color(0, 50, 150)); }
                public void mouseExited(MouseEvent e) { detailsBtn.setForeground(new Color(0, 102, 204)); }
            });

            detailsBtn.addActionListener(e -> {
                Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(detailsBtn);

                if (parentWindow instanceof Frame) {
                    ClientFeedbackDetailDialog dialog = new ClientFeedbackDetailDialog(
                            (Frame) parentWindow,
                            feedback,
                            status,
                            category,
                            author
                    );
                    dialog.setVisible(true);
                }
            });

            if (feedback.getIssued_by() == user.getAttr_id()) {
                JButton functionOpt = new JButton("...");
                functionOpt.setFont(new Font("Segoe UI", Font.BOLD, 18));
                functionOpt.setForeground(new Color(100, 100, 100));
                functionOpt.setContentAreaFilled(false);
                functionOpt.setBorderPainted(false);
                functionOpt.setFocusPainted(false);
                functionOpt.setCursor(new Cursor(Cursor.HAND_CURSOR));

                functionOpt.addActionListener(e -> {
                    FeedbackOptionsPopup popup = new FeedbackOptionsPopup(
                            SwingUtilities.getWindowAncestor(functionOpt),
                            feedback
                    );
                    Point screenPos = functionOpt.getLocationOnScreen();

                    popup.setLocation(screenPos.x - 120, screenPos.y + functionOpt.getHeight());
                    popup.setVisible(true);
                });
                add(functionOpt);
            }


            gbc.gridx = 1; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.SOUTHEAST;

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            actionPanel.setOpaque(false);
            actionPanel.add(detailsBtn);

            add(actionPanel, gbc);
        }

        class FeedbackOptionsPopup extends JWindow {
            public FeedbackOptionsPopup(Window owner, Feedbacks feedback) {
                super(owner);

                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.setBackground(Color.WHITE);
                container.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

                Font menuFont = new Font("Segoe UI", Font.PLAIN, 14);
                Color textColor = new Color(70, 70, 70);

                JButton editBtn = createMenuButton("Edit Feedback", menuFont, textColor);
                JButton deleteBtn = createMenuButton("Delete", menuFont, textColor);
                deleteBtn.setForeground(Color.RED);

                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to delete this feedback?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION

                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        Object[] response = residentDashboardController.deleteFeedback(feedback.getAttr_id());

                        if ("200".equals(response[0])) {
                            JOptionPane.showMessageDialog(this, "Feedback deleted successfully.");
                            refreshFeedbackList();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed: " + response[1]);
                        }
                    }
                });


                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(200, 200, 200));
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

                container.add(editBtn);
                container.add(sep);
                container.add(deleteBtn);

                add(container);
                pack();
                setSize(150, getPreferredSize().height);

                // Close the popup when focus is lost
                this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
                    public void windowGainedFocus(java.awt.event.WindowEvent e) {}
                    public void windowLostFocus(java.awt.event.WindowEvent e) {
                        dispose();
                    }
                });
            }

            private JButton createMenuButton(String text, Font font, Color color) {
                JButton btn = new JButton(text);
                btn.setFont(font);
                btn.setForeground(color);
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                // Hover effect
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btn.setBackground(new Color(245, 245, 245));
                        btn.setOpaque(true);
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        btn.setOpaque(false);
                    }
                });
                return btn;
            }
        }

        private Buttons createPriorityBadge(String priority) {
            String normalized = priority == null ? "No priority set" : priority.trim();
            Color bg;
            Color fg;
            String label;

            switch (normalized.toLowerCase()) {
                case "urgent" -> {
                    label = "Urgent";
                    bg = new Color(255, 225, 225);
                    fg = new Color(185, 45, 45);
                }
                case "high" -> {
                    label = "High";
                    bg = new Color(255, 236, 220);
                    fg = new Color(176, 95, 20);
                }
                case "medium" -> {
                    label = "Medium";
                    bg = new Color(255, 245, 210);
                    fg = new Color(150, 120, 20);
                }
                case "low" -> {
                    label = "Low";
                    bg = new Color(230, 248, 220);
                    fg = new Color(80, 130, 40);
                }
                default -> {
                    label = "No priority set";
                    bg = new Color(235, 235, 235);
                    fg = new Color(110, 110, 110);
                }
            }

            return new Buttons(
                    label,
                    8,
                    bg,
                    fg,
                    bg,
                    SegoeUi(Font.PLAIN, 12)
            );
        }

        private int calculateVotes() {
            int upvotes = feedback.getAttr_upvote() != null ? feedback.getAttr_upvote().length : 0;
            int downvotes = feedback.getAttr_downvote() != null ? feedback.getAttr_downvote().length : 0;
            return upvotes - downvotes;
        }

        private void updateButtonColor(JButton btn, boolean active, Color baseColor) {
            if (active) {
                btn.setForeground(baseColor);
            } else {
                btn.setForeground(Color.GRAY);
            }
        }

        private Color getUpvoteNormalColor() {
            return hasUpvoted ? UPVOTE_COLOR : Color.GRAY;
        }

        private Color getDownvoteNormalColor() {
            return hasDownvoted ? DOWNVOTE_COLOR : Color.GRAY;
        }

        private void setVoteButtonsEnabled(boolean enabled) {
            upvoteBtn.setEnabled(enabled);
            downvoteBtn.setEnabled(enabled);
        }

        private void handleUpvote() {
            if (voteInFlight) {
                return;
            }
            VoteRequest.VoteType voteType;
            if (hasUpvoted) {
                voteType = VoteRequest.VoteType.REMOVE_UPVOTE;
            } else {
                voteType = VoteRequest.VoteType.UPVOTE;
            }

            voteInFlight = true;
            setVoteButtonsEnabled(false);

            new Thread(() -> {
                try {
                        Object[] response = listener != null
                            ? listener.vote(feedback.getAttr_id(), user.getAttr_id(), voteType)
                            : new Object[]{"500", "No handler available", null};

                    if (response == null) {
                        throw new IllegalStateException("No response from server");
                    }

                    if (!"200".equals(response[0])) {
                        voteInFlight = false;
                        SwingUtilities.invokeLater(() -> {
                            setVoteButtonsEnabled(true);
                            JOptionPane.showMessageDialog(ResidentDashboard.this,
                                    "Failed to process vote: " + response[1],
                                    "Vote Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                } catch (Exception ex) {
                    voteInFlight = false;
                    SwingUtilities.invokeLater(() -> {
                        setVoteButtonsEnabled(true);
                        JOptionPane.showMessageDialog(ResidentDashboard.this,
                                "Failed to process vote: " + ex.getMessage(),
                                "Vote Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }, "vote-upvote-" + feedback.getAttr_id()).start();
        }

        private void handleDownvote() {
            if (voteInFlight) {
                return;
            }
            VoteRequest.VoteType voteType;
            if (hasDownvoted) {
                voteType = VoteRequest.VoteType.REMOVE_DOWNVOTE;
            } else {
                voteType = VoteRequest.VoteType.DOWNVOTE;
            }

            voteInFlight = true;
            setVoteButtonsEnabled(false);

            // Never block the Swing EDT with network I/O. UI will update via broadcast (VOTE_UPDATE).
            new Thread(() -> {
                try {
                        Object[] response = listener != null
                            ? listener.vote(feedback.getAttr_id(), user.getAttr_id(), voteType)
                            : new Object[]{"500", "No handler available", null};

                    if (response == null) {
                        throw new IllegalStateException("No response from server");
                    }

                    if (!"200".equals(response[0])) {
                        voteInFlight = false;
                        SwingUtilities.invokeLater(() -> {
                            setVoteButtonsEnabled(true);
                            JOptionPane.showMessageDialog(ResidentDashboard.this,
                                    "Failed to process vote: " + response[1],
                                    "Vote Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                } catch (Exception ex) {
                    voteInFlight = false;
                    SwingUtilities.invokeLater(() -> {
                        setVoteButtonsEnabled(true);
                        JOptionPane.showMessageDialog(ResidentDashboard.this,
                                "Failed to process vote: " + ex.getMessage(),
                                "Vote Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }, "vote-downvote-" + feedback.getAttr_id()).start();
        }

    }

    static class AnnouncementCard extends JPanel {
        AnnouncementCard(String t, String d, String o, String dt) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 20)),
                    BorderFactory.createEmptyBorder(5, 0, 10, 0)));
            JLabel tl = new JLabel("<html><b>" + t + "</b></html>");
            tl.setForeground(new Color(0, 102, 102));
            JLabel info = new JLabel("👤 " + o + " | " + dt);
            info.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
            JLabel bl = new JLabel("<html><body style='width: 180px;'>" + d + "</body></html>");
            bl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            add(tl);
            add(info);
            add(Box.createVerticalStrut(3));
            add(bl);
        }
    }

    public interface ResidentDashboardListener {
        void onLoadFeedbacks();
        Object[] submitFeedback(String title, String category, String location, String description);
        Object[] vote(int feedbackId, int userId, VoteRequest.VoteType voteType);
        Announcement[] getLatestAnnouncements(int limit);
        String formatTimeAgo(Date date);
        String getFeedbackLoadErrorMessage();
        boolean hasFeedbacks();
        java.util.List<Feedbacks> getFilteredAndSortedFeedbacks(
                String selectedCategory,
                String selectedStatus,
                String query,
                java.util.function.IntFunction<String> userNameProvider
        );
        void onLogoutRequested(JFrame parent);
        void onProfileRequested(JFrame parent, User user);
    }
}
