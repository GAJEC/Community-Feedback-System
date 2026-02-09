package client.view.residents;

import client.view.components.DropShadowBorder;
import client.view.components.FeedbackSubmitPopUp;
import client.view.components.ClientHeaderPanel;
import client.view.components.HintTextField;
import common.User;

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
import java.awt.geom.RoundRectangle2D;

public class ResidentDashboard extends JFrame {

    private static final Color BG_COLOR = new Color(240, 242, 245);
    public static final Color CARD_SHADOW = new Color(200, 200, 200);
    private static final Color PRIMARY_TEAL = new Color(0, 100, 100);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color USER_BOX_BG = new Color(248, 250, 252);

    private JPanel contentContainer;
    private boolean isViewMode;
    private User user;

    public ResidentDashboard(boolean viewMode, User user) {
        this.isViewMode = viewMode;
        this.user = user;
        setTitle("Community Feedback System");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        ClientHeaderPanel header = new ClientHeaderPanel(user);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        container.add(title, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new BorderLayout(0, 10));
        centerContent.setOpaque(false);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        listPanel.add(Box.createVerticalStrut(5));
        listPanel.add(new FeedbackCard("IN PROGRESS", "Infrastructure", "Deep pothole at the intersection of 5th Ave and Rizal St. causing heavy traffic.", "5th Avenue", "Feb 5, 2026", "Carlos Tecson", 45));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(new FeedbackCard("PENDING", "Health Services", "The health center is currently out of stock for basic flu vaccines.", "District Health Office", "Feb 4, 2026", "Elena Cruz", 82));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(new FeedbackCard("RESOLVED", "Environment", "Illegal dumping of waste near the riverside. Requesting immediate cleanup.", "Riverside Drive", "Feb 3, 2026", "Roberto Gomez", 120));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(new FeedbackCard("IN PROGRESS", "Public Safety", "Street lights are flickering or completely dead along the main highway.", "Maharlika Highway", "Feb 2, 2026", "Jenina Santos", 34));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(new FeedbackCard("PENDING", "Utilities", "Water supply has been low-pressure for the past 48 hours in Block 7.", "Greenview Subd.", "Feb 1, 2026", "Mark Anthony", 15));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(new FeedbackCard("RESOLVED", "Waste Management", "Garbage truck missed the collection schedule last Tuesday.", "Purok 4, Brgy. Central", "Jan 30, 2026", "Liza Maza", 56));

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

        RoundedPanel widget = new RoundedPanel(20, new Color(255, 255, 255, 200));
        widget.setLayout(new BoxLayout(widget, BoxLayout.Y_AXIS));
        widget.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Latest Announcements");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
        panel.setBackground(Color.WHITE);
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
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(40, 0, 40, 0));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(750, 780));
        card.setBorder(new DropShadowBorder(35));

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

        RoundedTextField titleField = new RoundedTextField(20);
        JPanel titlePanel = createFieldPanel("Issue Title", titleField);

        String[] categories = {"Event Suggestion", "Missing Person", "Noise Complaint", "Infrastructure", "Other"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
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

        RoundedTextField locationField = new RoundedTextField(20);
        JPanel locationPanel = createFieldPanel("Location / Landmark", locationField);

        JTextArea descArea = new JTextArea(8, 20);
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
                FeedbackSubmitPopUp dialog = new FeedbackSubmitPopUp(ResidentDashboard.this);
                dialog.setLocationRelativeTo(ResidentDashboard.this);
                dialog.setVisible(true);
                switchToView();
            }
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

    static class FeedbackCard extends JPanel {
        FeedbackCard(String status, String category, String text, String location, String date, String author, int votes) {
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
            GridBagConstraints gbc = new GridBagConstraints();

            JPanel voteStack = new JPanel(new GridLayout(3, 1, 0, -5));
            voteStack.setOpaque(false);
            voteStack.add(createArrowButton("▲", new Color(0, 150, 0)));
            JLabel vc = new JLabel(String.valueOf(votes), SwingConstants.CENTER);
            vc.setFont(new Font("Segoe UI", Font.BOLD, 14));
            voteStack.add(vc);
            voteStack.add(createArrowButton("▼", new Color(150, 0, 0)));
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

            gbc.gridx = 1; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.SOUTHEAST;
            add(detailsBtn, gbc);
        }
    }

    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bg;
        public RoundedPanel(int r, Color c) {
            this.radius = r;
            this.bg = c;
            setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
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
        SwingUtilities.invokeLater(() -> new ResidentDashboard(true, new User("testuser", "password")));
    }
}
