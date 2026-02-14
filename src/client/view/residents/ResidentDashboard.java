package client.view.residents;

import client.controller.FeedbackController;
import client.model.ClientConnection;
import client.view.common.CustomPanel;
import client.view.residents.panels.FeedbackSubmitPopUp;
import client.view.residents.panels.ClientHeaderPanel;
import client.view.common.field.HintTextField;
import client.view.residents.panels.ClientFeedbackDetailDialog;
import client.view.strings.Images;
import common.BroadcastMessage;
import common.Feedbacks;
import common.User;
import common.VoteRequest;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static client.view.strings.Colors.*;
import static client.view.strings.Fonts.SegoeUi;

public class ResidentDashboard extends JFrame {

    private static final Color BG_COLOR = new Color(240, 242, 245);
    public static final Color CARD_SHADOW = new Color(200, 200, 200);
    private static final Color PRIMARY_TEAL = new Color(0, 100, 100);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color USER_BOX_BG = new Color(248, 250, 252);

    private JPanel contentContainer;
    private boolean isViewMode;
    private User user;
    private FeedbackController feedbackController;
    private ClientConnection connection;
    private Map<Integer, User> userCache = new HashMap<>();
    private Map<Integer, FeedbackCard> feedbackCards = new HashMap<>();
    private JPanel feedbackListPanel;

    private RoundedTextField titleField;
    private JComboBox<String> categoryBox;
    private RoundedTextField locationField;
    private JTextArea descArea;

    public ResidentDashboard(boolean viewMode, User user, ClientConnection connection) {
        this.connection = connection;
        this.feedbackController = new FeedbackController(connection);
        // Cache the logged-in user
        userCache.put(user.getAttr_id(), user);
        this.isViewMode = viewMode;
        this.user = user;

        connection.setBroadcastListener(this::handleBroadcast);
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

    private void handleBroadcast(BroadcastMessage message) {
        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case VOTE_UPDATE:
                    if (message.getData() instanceof Feedbacks) {
                        Feedbacks updatedFeedback = (Feedbacks) message.getData();
                        updateFeedbackCard(updatedFeedback);
                    }
                    break;
                case NEW_FEEDBACK:
                    if (isViewMode) {
                        refreshFeedbackList();
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private void updateFeedbackCard(Feedbacks feedback) {
        FeedbackCard card = feedbackCards.get(feedback.getAttr_id());
        if (card != null) {
            card.updateVotes(feedback);
            resortFeedbackListByVotes();
        } else {
            System.out.println("[Update] Card not found for feedback #" + feedback.getAttr_id());
        }
    }

    private void resortFeedbackListByVotes() {
        if (!isViewMode || feedbackListPanel == null || feedbackCards.isEmpty()) {
            return;
        }

        java.util.List<FeedbackCard> cards = new java.util.ArrayList<>(feedbackCards.values());
        cards.sort((a, b) -> Integer.compare(b.getVoteScore(), a.getVoteScore()));

        feedbackListPanel.removeAll();
        feedbackListPanel.add(Box.createVerticalStrut(5));
        for (FeedbackCard c : cards) {
            feedbackListPanel.add(c);
            feedbackListPanel.add(Box.createVerticalStrut(8));
        }

        feedbackListPanel.revalidate();
        feedbackListPanel.repaint();
    }

    private int voteScore(Feedbacks feedback) {
        int upvotes = feedback.getAttr_upvote() != null ? feedback.getAttr_upvote().length : 0;
        int downvotes = feedback.getAttr_downvote() != null ? feedback.getAttr_downvote().length : 0;
        return upvotes - downvotes;
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

        loadFeedbacks(listPanel);

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

        widget.add(new AnnouncementCard("Barangay Health Mission", "Free medical check-up for residents.", "Hon. Rivera", "Feb 10, 2026"));
        widget.add(Box.createVerticalStrut(10));
        widget.add(new AnnouncementCard("Road Maintenance", "Rizal St. closed for asphalt overlay.", "Sec. Santos", "Feb 8, 2026"));

        sidebar.add(widget, BorderLayout.NORTH);
        return sidebar;
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

        JLabel searchLabel = new JLabel("🔍 Search");
        searchLabel.setFont(SegoeUi(1, 12));
        JTextField searchField = new HintTextField("Search feedbacks...");
        searchField.setPreferredSize(new Dimension(0, 35));
        searchContainer.add(searchLabel, BorderLayout.NORTH);
        searchContainer.add(searchField, BorderLayout.CENTER);
        panel.add(searchContainer, gbc);

        gbc.gridx = 1; gbc.weightx = 0.2;
        JPanel catContainer = new JPanel(new BorderLayout(0, 5));
        catContainer.setOpaque(false);
        JLabel catLabel = new JLabel("▽ Category");
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        catContainer.add(catLabel, BorderLayout.NORTH);
        catContainer.add(dropdownBox(new String[]{"All Categories", "Infrastructure", "Health", "Safety"}), BorderLayout.CENTER);
        panel.add(catContainer, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        JPanel statContainer = new JPanel(new BorderLayout(0, 5));
        statContainer.setOpaque(false);
        JLabel statLabel = new JLabel("▽ Status");
        statLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statContainer.add(statLabel, BorderLayout.NORTH);
        statContainer.add(dropdownBox(new String[]{"All Status", "Pending", "In Progress", "Resolved"}), BorderLayout.CENTER);
        panel.add(statContainer, gbc);

        JPanel sortRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sortRow.setOpaque(false);
        JLabel sortLabel = new JLabel("⇅ Sort by:");
        sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sortRow.add(sortLabel);

        sortRow.add(new RoundedSortButton("Date"));
        sortRow.add(new RoundedSortButton("Upvotes"));

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
        card.setBackgroundColor(Color.WHITE);

        card.setOffsetShadow(new Color(0,0,0,30), 3); // light offset shadow
        card.setDropShadow(new Color(0,0,0,30), 1f); // subtle outline
        card.setRadius(20);

        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Submit Community Feedback");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitle = new JLabel("Help improve the community by reporting issues");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userBlock = createUserProfileBlock("Juan Dela Cruz", "2023-8492");

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(new Color(230, 230, 230));

        titleField = new RoundedTextField(20);
        JPanel titlePanel = createFieldPanel("Issue Title", titleField);

        String[] categories = {"Event Suggestion", "Missing Person", "Noise Complaint", "Infrastructure", "Other"};
        categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryBox.setBackground(Color.WHITE);
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
        categoryWrapper.setBackground(Color.WHITE);
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
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane areaScroll = new JScrollPane(descArea);
        areaScroll.setBorder(new RoundedBorder(10));
        areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        JPanel descPanel = createFieldPanel("Detailed Description", areaScroll);

        JLabel processingNote = new JLabel("Your feedback will be processed anonymously if preferred.");
        processingNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        processingNote.setForeground(new Color(100, 100, 100));
        processingNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        LightClickButton cancelBtn = new LightClickButton("Cancel", DANGER_RED, Color.WHITE);
        LightClickButton submitBtn = new LightClickButton("Submit Feedback", PRIMARY_TEAL, Color.WHITE);

        cancelBtn.setPreferredSize(new Dimension(120, 40));
        submitBtn.setPreferredSize(new Dimension(180, 40));

        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFeedbackSubmit();
            }
        });

        cancelBtn.addActionListener(e -> {
            clearForm();
        });

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
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));

        JLabel idLabel = new JLabel("ID: " + userId);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        idLabel.setForeground(Color.GRAY);

        textPanel.add(nameLabel);
        textPanel.add(idLabel);

        JLabel iconLabel = new JLabel("👤 ");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("<html>" + labelText + " <span style='color:#E74C3C;'>*</span></html>");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a title for your feedback.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a location.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please provide a detailed description.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fullContent = title + "\n\n" + description;

        Object[] response = feedbackController.submitFeedback(
            user.getAttr_id(),
            category,
            title,
            location,
            fullContent
        );

        String statusCode = (String) response[0];
        String message = (String) response[1];

        if ("200".equals(statusCode)) {
            FeedbackSubmitPopUp dialog = new FeedbackSubmitPopUp(ResidentDashboard.this);
            dialog.setLocationRelativeTo(ResidentDashboard.this);
            dialog.setVisible(true);

            clearForm();
            switchToView();
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

    private void loadFeedbacks(JPanel listPanel) {
        feedbackCards.clear();
        Object[] response = feedbackController.getAllFeedbacks();
        String statusCode = (String) response[0];
        String message = (String) response[1];

        if ("200".equals(statusCode) && response[2] != null) {
            Feedbacks[] feedbacks = (Feedbacks[]) response[2];

            if (feedbacks.length == 0) {
                JLabel noDataLabel = new JLabel("No feedbacks available yet. Be the first to submit!");
                noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noDataLabel.setForeground(Color.GRAY);
                noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(noDataLabel);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

                java.util.Arrays.sort(feedbacks, (a, b) -> Integer.compare(voteScore(b), voteScore(a)));

                for (Feedbacks feedback : feedbacks) {
                    String[] statuses = feedback.getAttr_status();
                    String status = (statuses != null && statuses.length > 0 && statuses[0] != null && !statuses[0].trim().isEmpty())
                            ? statuses[0]
                            : "Pending";
                    String category = feedback.getCategories() != null && feedback.getCategories().length > 0
                        ? feedback.getCategories()[0] : "General";
                    String content = feedback.getContent();
                    String location = feedback.getLocation();
                    String date = dateFormat.format(feedback.getIssued_date());

                    String authorName = getUserName(feedback.getIssued_by());

                    FeedbackCard card = new FeedbackCard(feedback, status, category, content, location, date, authorName);
                    feedbackCards.put(feedback.getAttr_id(), card);
                    listPanel.add(card);
                    listPanel.add(Box.createVerticalStrut(8));
                }
            }
        } else {
            JLabel errorLabel = new JLabel("Error loading feedbacks: " + message);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(errorLabel);
        }
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
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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

    static class LightClickButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;
        public LightClickButton(String text, Color baseColor, Color textColor) {
            super(text);
            this.normalColor = baseColor;
            this.hoverColor = baseColor.brighter();
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(textColor);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e) { setBackground(normalColor); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isRollover()) g2.setColor(hoverColor);
            else g2.setColor(normalColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedSortButton extends JButton {
        public RoundedSortButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(240, 240, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
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

        FeedbackCard(Feedbacks feedback, String status, String category, String text, String location, String date, String author) {
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
            badgeRow.add(new FilterButton(status, 8, new Color(210, 235, 255)));
            badgeRow.add(new FilterButton(category, 8, new Color(245, 240, 250)));
            gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 5, 0);
            add(badgeRow, gbc);

            JPanel textStack = new JPanel();
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
            textStack.setOpaque(false);
            JLabel bodyLabel = new JLabel("<html><body style='width: 350px;'><b>" + text + "</b></body></html>");
            bodyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JLabel meta = new JLabel(String.format("📍 %s  •  👤 %s  •  📅 %s", location, author, date));
            meta.setForeground(Color.GRAY);
            meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
                    ClientFeedbackDetailDialog dialog = new ClientFeedbackDetailDialog((Frame) parentWindow);
                    dialog.setVisible(true);
                }
            });

            gbc.gridx = 1; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.SOUTHEAST;
            add(detailsBtn, gbc);
        }

        private int calculateVotes() {
            int upvotes = feedback.getAttr_upvote() != null ? feedback.getAttr_upvote().length : 0;
            int downvotes = feedback.getAttr_downvote() != null ? feedback.getAttr_downvote().length : 0;
            return upvotes - downvotes;
        }

        public int getVoteScore() {
            return calculateVotes();
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
                    Object[] response = feedbackController.vote(feedback.getAttr_id(), user.getAttr_id(), voteType);

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
                    Object[] response = feedbackController.vote(feedback.getAttr_id(), user.getAttr_id(), voteType);

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

        public void updateVotes(Feedbacks updatedFeedback) {
            feedback.setAttr_upvote(updatedFeedback.getAttr_upvote());
            feedback.setAttr_downvote(updatedFeedback.getAttr_downvote());

            int userId = user.getAttr_id();

            boolean newUpvoted = false;
            boolean newDownvoted = false;

            if (updatedFeedback.getAttr_upvote() != null) {
                String uid = String.valueOf(userId);
                for (String id : updatedFeedback.getAttr_upvote()) {
                    if (uid.equals(id)) {
                        newUpvoted = true;
                        break;
                    }
                }
            }
            if (updatedFeedback.getAttr_downvote() != null) {
                String uid = String.valueOf(userId);
                for (String id : updatedFeedback.getAttr_downvote()) {
                    if (uid.equals(id)) {
                        newDownvoted = true;
                        break;
                    }
                }
            }

            hasUpvoted = newUpvoted;
            hasDownvoted = newDownvoted;

            voteInFlight = false;
            setVoteButtonsEnabled(true);

            int totalVotes = calculateVotes();

            voteCountLabel.setText(String.valueOf(totalVotes));
            updateButtonColor(upvoteBtn, hasUpvoted, UPVOTE_COLOR);
            updateButtonColor(downvoteBtn, hasDownvoted, DOWNVOTE_COLOR);

            revalidate();
            repaint();
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
            info.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            JLabel bl = new JLabel("<html><body style='width: 180px;'>" + d + "</body></html>");
            bl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            add(tl);
            add(info);
            add(Box.createVerticalStrut(3));
            add(bl);
        }
    }

    static class FilterButton extends JButton {
        private int r;
        private Color c;
        FilterButton(String l, int r, Color c) {
            super(l);
            this.r = r;
            this.c = c;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static JButton createArrowButton(String a, Color h) {
        JButton b = new JButton(a);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(h); }
            public void mouseExited(MouseEvent e) { b.setForeground(Color.BLACK); }
        });
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientConnection testConnection = new ClientConnection();
            testConnection.connect();
            new ResidentDashboard(true, new User("testuser", "password"), testConnection);
        });
    }
}
