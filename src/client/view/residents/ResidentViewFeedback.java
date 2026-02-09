package client.view.residents;

import client.view.components.ClientHeaderPanel;
import common.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ResidentViewFeedback extends JFrame {

    public ResidentViewFeedback(User user) {
        setTitle("Community Feedback System");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setBackground(new Color(240, 242, 245));

        mainLayout.add(new ClientHeaderPanel(user), BorderLayout.NORTH);

        mainLayout.add(createFeedContentPanel(), BorderLayout.CENTER);

        setContentPane(mainLayout);
        setVisible(true);
    }

    private JPanel createFeedContentPanel() {
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


        // MOCKUP DATA
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
        container.add(createAnnouncementWidget(), BorderLayout.EAST); // added the announcement widget on the right side (EAST)

        return container;
    }

    private JPanel createAnnouncementWidget() { // widget panel for announcements from brgy official
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

    private JPanel createFilterPanel() { // pnel for searching, filtering and sorting ng feedbacks
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints(); //
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6;
        JPanel searchContainer = new JPanel(new BorderLayout(0, 5));
        searchContainer.setOpaque(false);
        JLabel searchLabel = new JLabel("🔍 Search");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField searchField = new JTextField("Search feedbacks...");
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

            // Background color (light gray like the screenshot)
            g2.setColor(new Color(240, 240, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // Border
            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JComboBox<String> dropdownBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(150, 30));
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return combo;
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

            // 1. Voting Column
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
            meta.setForeground(Color.GRAY); meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            textStack.add(bodyLabel); textStack.add(meta);
            gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);
            add(textStack, gbc);

            // 4. View Details Button (Bottom Right)
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
        private int radius; private Color bg;
        public RoundedPanel(int r, Color c) { this.radius = r; this.bg = c; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose(); super.paintComponent(g);
        }
    }

    static class AnnouncementCard extends JPanel {
        AnnouncementCard(String t, String d, String o, String dt) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 20)),
                    BorderFactory.createEmptyBorder(5, 0, 10, 0)));
            JLabel tl = new JLabel("<html><b>" + t + "</b></html>");
            tl.setForeground(new Color(0, 102, 102));
            JLabel info = new JLabel("👤 " + o + " | " + dt);
            info.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            JLabel bl = new JLabel("<html><body style='width: 180px;'>" + d + "</body></html>");
            bl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            add(tl); add(info); add(Box.createVerticalStrut(3)); add(bl);
        }
    }

    static class FilterButton extends JButton {
        private int r; private Color c;
        FilterButton(String l, int r, Color c) {
            super(l); this.r = r; this.c = c;
            setContentAreaFilled(false); setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            g2.dispose(); super.paintComponent(g);
        }
    }

    private static JButton createArrowButton(String a, Color h) {
        JButton b = new JButton(a); b.setContentAreaFilled(false); b.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(h); }
            public void mouseExited(MouseEvent e) { b.setForeground(Color.BLACK); }
        });
        return b;
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ResidentDashboard(true, new User("test", "test123"))); }
}