package client.view.brgyofficials;

import client.view.common.button.Buttons;
import client.view.brgyofficials.panel.BrgyOfficialHeaderPanel;
import client.view.common.field.HintTextField;
import common.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class BrgyOfficialDashboard extends JFrame {

    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color CARD_SHADOW = new Color(200, 200, 200);
    private static final Color GREEN = new Color(34, 153, 84);
    private static final Color USER_BOX_BG = new Color(248, 250, 252);

    private JPanel contentContainer;
    private User user;

    public BrgyOfficialDashboard(User user){
        this.user = user;
        setTitle("Barangay Official Dashboard");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        BrgyOfficialHeaderPanel header = new BrgyOfficialHeaderPanel(user);
        mainPanel.add(header, BorderLayout.NORTH);
        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.add(contentContainer, BorderLayout.CENTER);
        setContentPane(mainPanel);
        loadDashboard();
        setVisible(true);
    }

    private void loadDashboard() {
        contentContainer.removeAll();
        JPanel wrapper = new JPanel(new BorderLayout(0, 20));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Feedback Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tagline = new JLabel("Review and manage community feedbacks");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
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

    private JPanel statsSummaryRow() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 15));
        statsRow.setOpaque(false);
        statsRow.add(new StatCard("Urgent", "0", new Color(252, 232, 232)));
        statsRow.add(new StatCard("Pending", "2", new Color(255, 249, 216)));
        statsRow.add(new StatCard("In Progress", "2", new Color(233, 238, 252)));
        statsRow.add(new StatCard("High Priority", "0", new Color(246, 236, 254)));
        return statsRow;
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
        panel.add(new HintTextField("Search feedbacks..."), gbc);
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
        // SAMPLE SET
        list.add(new FeedbackCard("IN PROGRESS", "Infrastructure",
                "Street lights along Main Road not working",
                "Main Road", "Feb 7, 2026", "Maria Garcia", 12));
        list.add(Box.createVerticalStrut(10));
        list.add(new FeedbackCard("PENDING", "Safety",
                "Broken drainage cover near school gate",
                "Central School", "Feb 6, 2026", "Juan Reyes", 8));
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
            t.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.BOLD, 26));
            v.setForeground(GREEN);
            add(t, BorderLayout.NORTH);
            add(v, BorderLayout.CENTER);
        }
    }

    static class FeedbackCard extends JPanel {

        FeedbackCard(String status, String category, String text,
                     String location, String date, String author, int votes) {
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
            JPanel tagRow = createTagRow(status, category);
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
            voteCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

        private JPanel createTagRow(String status, String category) {
            JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
            tagRow.setOpaque(false);
            tagRow.add(createTag(status, new Color(220,235,255, 50), new Color(0,90,180)));
            tagRow.add(createTag(category, new Color(230,220,255, 50), new Color(100,60,180)));
            tagRow.add(createTag("No priority set", new Color(230,230,230, 50), Color.GRAY));
            return tagRow;
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

        private JPanel createTextStack(String text, String location, String date, String author) {
            JLabel body = new JLabel("<html>" + text + "</html>");
            body.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel meta = new JLabel("📍 " + location + " • 👤 " + author + " • 📅 " + date);
            meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
            Font btnFont = new Font("Segoe UI", Font.BOLD, 8);
            Color labelColor = new Color(50,50,50);
            Dimension priorityBtnSize = new Dimension(65, 22);
            Dimension statusBtnSize = new Dimension(65, 22);
            JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            priorityPanel.setOpaque(false);
            Buttons urgentBtn = Buttons.createRounded("Urgent", 6,
                    new Color(255, 180, 180), new Color(230, 100, 90));
            Buttons highBtn = Buttons.createRounded("High", 6,
                    new Color(255, 220, 180), new Color(230, 160, 100));
            Buttons mediumBtn = Buttons.createRounded("Medium", 6,
                    new Color(255, 240, 180), new Color(210, 180, 110));
            Buttons lowBtn = Buttons.createRounded("Low", 6,
                    new Color(220, 240, 200), new Color(170, 200, 140));
            for (Buttons btn : new Buttons[]{urgentBtn, highBtn, mediumBtn, lowBtn}) {
                btn.setPreferredSize(priorityBtnSize);
                btn.setFont(btnFont);
                btn.setForeground(new Color(50, 50, 50, 150));
                priorityPanel.add(btn);
            }
            JLabel priorityLabel = new JLabel("Set Priority: ");
            priorityLabel.setForeground(labelColor);
            priorityLabel.setFont(btnFont);
            priorityPanel.add(priorityLabel, 0);
            JPanel quickStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            quickStatusPanel.setOpaque(false);
            Buttons startBtn = Buttons.createRounded("Start", 6,
                    new Color(200, 220, 255), new Color(140, 160, 240));
            Buttons resolveBtn = Buttons.createRounded("Resolve", 6,
                    new Color(200, 255, 200), new Color(100, 170, 100));
            for (Buttons btn : new Buttons[]{startBtn, resolveBtn}) {
                btn.setPreferredSize(statusBtnSize);
                btn.setFont(btnFont);
                btn.setForeground(new Color(50, 50, 50, 150));
                quickStatusPanel.add(btn);
            }
            JLabel quickStatusLabel = new JLabel("Quick Status: ");
            quickStatusLabel.setForeground(labelColor);
            quickStatusLabel.setFont(btnFont);
            quickStatusPanel.add(quickStatusLabel, 0);
            panel.add(priorityPanel);
            panel.add(quickStatusPanel);
            return panel;
        }

        private JPanel addNotesPanel() {

            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setOpaque(false);
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            headerPanel.setOpaque(false);
            JLabel toggleLabel = new JLabel("Add internal note →");
            toggleLabel.setForeground(new Color(0, 128, 0));
            toggleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
            noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(noteArea);
            scrollPane.setPreferredSize(new Dimension(400, 80));
            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
            buttonRow.setOpaque(false);

            JButton saveButton = new JButton("Save");
            saveButton.setFocusPainted(false);
            saveButton.setBackground(new Color(34, 153, 84));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            buttonRow.add(saveButton);
            notePanel.add(scrollPane);
            notePanel.add(buttonRow);
            container.add(Box.createVerticalStrut(5));
            container.add(notePanel);

            final String[] savedNote = { null };

            saveButton.addActionListener(e -> {
                String text = noteArea.getText().trim();
                if (text.isEmpty()) {
                    JOptionPane.showMessageDialog(FeedbackCard.this,
                            "Note cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                savedNote[0] = text;
                JOptionPane.showMessageDialog(FeedbackCard.this,
                        "Internal note saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            });

            toggleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                boolean expanded = false;
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    expanded = !expanded;
                    notePanel.setVisible(expanded);
                    if (expanded) {
                        toggleLabel.setText("Hide note ↓");
                        noteArea.setText(savedNote[0] != null ? savedNote[0] : "");
                    } else {
                        toggleLabel.setText("Add internal note →");
                        if (savedNote[0] == null) {
                            noteArea.setText("");
                        }
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