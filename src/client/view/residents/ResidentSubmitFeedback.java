package client.view.residents;

import client.view.components.ClientHeaderPanel;
import common.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class ResidentSubmitFeedback extends JFrame {

    // Colors
    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color CARD_SHADOW = new Color(200, 200, 200);
    private static final Color PRIMARY_TEAL = new Color(0, 100, 100);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color USER_BOX_BG = new Color(248, 250, 252);

    public ResidentSubmitFeedback(User user) {
        setTitle("Community Feedback");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (d.width * 0.8), (int) (d.height * 0.85));
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBackground(BG_COLOR);

        mainPanel.add(new ClientHeaderPanel(user), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createContentPanel() {
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
                b.setBorder(BorderFactory.createEmptyBorder()); // Remove button border
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
        // --------------------------------------

        RoundedTextField locationField = new RoundedTextField(20);
        JPanel locationPanel = createFieldPanel("Location / Landmark", locationField);

        // Description
        JTextArea descArea = new JTextArea(8, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane areaScroll = new JScrollPane(descArea);
        areaScroll.setBorder(new RoundedBorder(10));
        areaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        // HIDE THE SCROLLBAR as requested
        areaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        JPanel descPanel = createFieldPanel("Detailed Description", areaScroll);

        JLabel processingNote = new JLabel("Your feedback will be processed anonymously if preferred.");
        processingNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        processingNote.setForeground(new Color(100, 100, 100));
        processingNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 4. Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        LightClickButton cancelBtn = new LightClickButton("Cancel", DANGER_RED, Color.WHITE);
        LightClickButton submitBtn = new LightClickButton("Submit Feedback", PRIMARY_TEAL, Color.WHITE);

        cancelBtn.setPreferredSize(new Dimension(120, 40));
        submitBtn.setPreferredSize(new Dimension(180, 40));

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);

        // --- ASSEMBLY ---
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subTitle);
        card.add(Box.createVerticalStrut(20));

        card.add(userBlock); // User Info here

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

    /**
     * Creates the rectangular user block below the title.
     */
    private JPanel createUserProfileBlock(String username, String userId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(USER_BOX_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Border: 1px Gray Line + Padding inside
        panel.setBorder(new CompoundBorder(
                new RoundedBorder(8), // Outer line
                new EmptyBorder(10, 15, 10, 15) // Inner padding
        ));

        // Use a grid to stack Name and ID
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

    // --- CUSTOM COMPONENTS ---

    static class DropShadowBorder extends AbstractBorder {
        private final int padding;
        public DropShadowBorder(int padding) { this.padding = padding; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_SHADOW);
            g2.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
            g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 15, 15);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(padding, padding, padding, padding); }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResidentDashboard(false, new User("testuser", "password")));
    }
}