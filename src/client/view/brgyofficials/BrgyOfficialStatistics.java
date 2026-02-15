package client.view.brgyofficials;

import client.view.brgyofficials.panel.BrgyOfficialHeaderPanel;
import client.view.strings.Colors;
import common.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// I'm not sure how to merge this with the dashboard
public class BrgyOfficialStatistics extends JFrame {
    private static final Color BG_COLOR = Colors.LIGHTER_GRAY;
    private static final Color CARD_SHADOW = Colors.LIGHT_GRAY;
    private JPanel contentContainer;
    private User user;

    public BrgyOfficialStatistics(User user) {
        this.user = user;
        setTitle("Barangay Official Statistics");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        BrgyOfficialHeaderPanel header = new BrgyOfficialHeaderPanel(user, "Statistics");
        mainPanel.add(header, BorderLayout.NORTH);
        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.add(contentContainer, BorderLayout.CENTER);
        setContentPane(mainPanel);
        loadStatistics();
        setVisible(true);
    }

    private void loadStatistics() {
        contentContainer.removeAll();
        JPanel wrapper = new JPanel(new BorderLayout(0, 20));
        wrapper.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Feedback Statistics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Colors.NORMAL_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tagline = new JLabel("Overview of community feedback status");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(Colors.SUB_TEXT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(tagline);
        wrapper.add(titlePanel, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(statsSummaryRow(), BorderLayout.NORTH);
        center.add(categoryPanel(), BorderLayout.CENTER);
        wrapper.add(center, BorderLayout.CENTER);
        contentContainer.add(wrapper);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private JPanel statsSummaryRow() {
        JPanel statsRow = new JPanel(new GridLayout(1, 5, 15, 15));
        statsRow.setOpaque(false);
        statsRow.add(new BrgyOfficialDashboard.StatCard("Total Feedbacks", "20", Colors.LIGHTEST_GRAY));
        statsRow.add(new BrgyOfficialDashboard.StatCard("Pending", "2", new Color(255, 249, 216)));
        statsRow.add(new BrgyOfficialDashboard.StatCard("In Progress", "2", new Color(233, 238, 252)));
        statsRow.add(new BrgyOfficialDashboard.StatCard("Resolved", "5", new Color(220, 240, 200)));
        statsRow.add(new StatCard("Archived", "0", Colors.LIGHTEST_GRAY));
        return statsRow;
    }

    private JPanel categoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colors.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(new CompoundBorder(
                new RoundedBorder(12),
                new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel title = new JLabel("Feedbacks by Category");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Colors.NORMAL_TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createCategoryRow("Infrastructure", 4));
        panel.add(createCategoryRow("Sanitation", 0));
        panel.add(createCategoryRow("Environmental", 6));
        panel.add(createCategoryRow("Public Safety", 3));
        panel.add(createCategoryRow("Health Services", 1));
        return panel;
    }


    private JPanel createCategoryRow(String category, int value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 0, 15, 0));
        JLabel name = new JLabel(category);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        name.setForeground(Colors.NORMAL_TEXT);
        JLabel count = new JLabel(String.valueOf(value));
        count.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
            t.setForeground(Colors.NORMAL_TEXT);
            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.BOLD, 26));
            v.setForeground(Colors.SECONDARY);
            add(t, BorderLayout.NORTH);
            add(v, BorderLayout.CENTER);
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Colors.LIGHT_GRAY);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new BrgyOfficialStatistics(new User("brgyofficial", "password"))
        );
    }
}
