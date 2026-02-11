package client.view.brgyofficials;
import client.view.components.*;
import common.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Add internal notes no UI yet
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
        panel.setBorder(new CompoundBorder(
                new RoundedBorder(10),
                new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.weightx = 0.6;
        panel.add(new HintTextField("Search feedbacks..."), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        panel.add(new JComboBox<>(new String[]{"All Categories", "Infrastructure", "Sanitation", "Environmental",
        "Public Safety", "Health Services", "Others"}), gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        panel.add(new JComboBox<>(new String[]{"All Status", "Pending", "In Progress", "Resolved", "Archived"}), gbc);
        return panel;
    }

    // Tbe, should have an option to add internal notes
    private JScrollPane createFeedbackList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        // SAMPLE SET
        list.add(new feedbackCard("IN PROGRESS", "Infrastructure", "Street lights along Main Road not working",
                "Main Road", "Feb 7, 2026", "Maria Garcia", 12 ));
        list.add(Box.createVerticalStrut(10));
        list.add(new feedbackCard("PENDING", "Safety", "Broken drainage cover near school gate",
                "Central School", "Feb 6, 2026", "Juan Reyes", 8));
        list.add(Box.createVerticalStrut(10));
        list.add(new feedbackCard("PENDING", "Safety", "Broken drainage cover near school gate",
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
            setBorder(new CompoundBorder(
                    new RoundedBorder(12),
                    new EmptyBorder(15, 15, 15, 15)
            ));

            JLabel t = new JLabel(title);
            t.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.BOLD, 26));
            v.setForeground(GREEN);

            add(t, BorderLayout.NORTH);
            add(v, BorderLayout.CENTER);
        }
    }

    static class feedbackCard extends JPanel {
        feedbackCard(String status, String category, String text,
                             String location, String date, String author, int votes) {

            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(
                    new RoundedBorder(12),
                    new EmptyBorder(12, 15, 12, 15)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

            JLabel body = new JLabel("<html><b>" + text + "</b></html>");
            body.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel meta = new JLabel("📍 " + location + " • 👤 " + author + " • 📅 " + date);
            meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            meta.setForeground(Color.GRAY);

            JPanel textStack = new JPanel();
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
            textStack.setOpaque(false);
            textStack.add(body);
            textStack.add(Box.createVerticalStrut(5));
            textStack.add(meta);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);

            RoundedButton addNotesBtn = RoundedButton.create("Add internal notes", 12, new Color(100, 100, 100), new Color(80, 80, 80));
            addNotesBtn.setPreferredSize(new Dimension(160, 32));
            addNotesBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

            actions.add(addNotesBtn);

            add(textStack, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        RoundedBorder(int radius) { this.radius = radius; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
