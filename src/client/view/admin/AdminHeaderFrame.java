package client.view.admin;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AdminHeaderFrame extends JFrame {

    private static final Color HEADER_COLOR = new Color(80, 0, 150);
    private static final Color NAV_COLOR = new Color(255, 255, 255);
    private static final Color ACTIVE_TEXT = new Color(102, 0, 204);
    private static final Color LOGOUT_RED = new Color(180, 40, 40);

    public JButton profileButton, logoutButton;
    public JButton viewFeedbackBtn, consoleBtn, manageFeedbackBtn, systemControlsBtn;

    private String currentActiveTab;
    private JPanel contentPanel;

    public AdminHeaderFrame(String activeTab) {
        this.currentActiveTab = activeTab;
        setTitle("Community Feedback System - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        URL iconURL = AdminHeaderFrame.class.getResource("/res/assets/app_icon.png");
        if (iconURL == null) iconURL = AdminHeaderFrame.class.getResource("/assets/app_icon.png");

        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));

        buildHeader(northContainer);
        buildNavBar(northContainer);

        add(northContainer, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 240, 240));
        add(contentPanel, BorderLayout.CENTER);
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
        } catch (Exception ignored) {}

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

        profileButton = new RoundedButton("Barangay Official", 30, new Color(255, 255, 255, 40));
        profileButton.setPreferredSize(new Dimension(180, 40));
        gbcR.insets = new Insets(0, 0, 0, 15);
        rightPnl.add(profileButton, gbcR);

        logoutButton = new RoundedButton("Logout", 20, LOGOUT_RED);
        logoutButton.setPreferredSize(new Dimension(110, 40));

        logoutButton.addActionListener(e -> System.out.println("Logout Triggered"));

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

        viewFeedbackBtn = new JButton("Summary");
        consoleBtn = new JButton("Console");
        manageFeedbackBtn = new JButton("Manage Feedback");
        systemControlsBtn = new JButton("System Controls");

        styleTab(viewFeedbackBtn, "Summary".equals(currentActiveTab));
        styleTab(consoleBtn, "Console".equals(currentActiveTab));
        styleTab(manageFeedbackBtn, "Manage Feedback".equals(currentActiveTab));
        styleTab(systemControlsBtn, "System Controls".equals(currentActiveTab));

        navBar.add(viewFeedbackBtn);
        navBar.add(consoleBtn);
        navBar.add(manageFeedbackBtn);
        navBar.add(systemControlsBtn);

        container.add(navBar);
    }

    public void switchPanel(JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateTabs(String activeTab) {
        styleTab(viewFeedbackBtn, "Summary".equals(activeTab));
        styleTab(consoleBtn, "Console".equals(activeTab));
        styleTab(manageFeedbackBtn, "Manage Feedback".equals(activeTab));
        styleTab(systemControlsBtn, "System Controls".equals(activeTab));
    }

    private void styleTab(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(active ? ACTIVE_TEXT : Color.GRAY);
        btn.setBorder(active ? BorderFactory.createMatteBorder(0, 0, 3, 0, ACTIVE_TEXT)
                : BorderFactory.createEmptyBorder(0, 0, 3, 0));
    }

    static class RoundedButton extends JButton {
        private int radius;
        private Color color;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminHeaderFrame("Console").setVisible(true));
    }
}