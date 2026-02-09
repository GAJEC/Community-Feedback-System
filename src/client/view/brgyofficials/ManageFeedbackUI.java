package client.view.brgyofficials;

import client.view.components.AdminHeaderPanel;
import client.view.components.FeedbackRowPanel;
import client.view.components.HintTextField;
import client.view.strings.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ManageFeedbackUI extends JFrame {

    public HintTextField searchField;
    public JComboBox<String> statusFilter;
    public JPanel listContainer;

    // Colors
    private static final Color BG_COLOR = new Color(244, 247, 246);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);

    // Dimensions
    private static final int MAIN_PADDING_X = 60;
    private static final int CONTENT_PADDING = 30;

    public ManageFeedbackUI() {
        setTitle("Admin - Manage Feedbacks");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminHeaderPanel("Manage Feedback"), BorderLayout.NORTH);

        // --- MAIN BODY WRAPPER ---
        // This panel holds the Title, Search Card, and List Card.
        // It will grow as tall as it needs to be.
        JPanel mainBody = new JPanel();
        mainBody.setLayout(new BoxLayout(mainBody, BoxLayout.Y_AXIS));
        mainBody.setBackground(BG_COLOR);
        // Padding is applied here so the content doesn't touch the scrollbar edges
        mainBody.setBorder(new EmptyBorder(30, MAIN_PADDING_X, 30, MAIN_PADDING_X));

        // --- TITLE SECTION ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 10, 20, 0));

        JLabel title = new JLabel("Manage Feedbacks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Colors.PRIMARY);

        JLabel subtitle = new JLabel("View, filter, and respond to resident concerns.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(BG_COLOR);
        titleWrapper.add(titlePanel, BorderLayout.WEST);
        titleWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        mainBody.add(titleWrapper);

        // --- CARD 1: CONTROLS (Search & Filter) ---
        JPanel controlCard = new CardPanel();
        controlCard.setLayout(new BorderLayout());
        controlCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        controlCard.setPreferredSize(new Dimension(1000, 110));

        JPanel inputsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        inputsPanel.setOpaque(false);
        inputsPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        searchField = new HintTextField("Search feedbacks");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String[] filters = {"All Statuses", "Pending", "In Progress", "Resolved"};
        statusFilter = new JComboBox<>(filters);
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setBackground(Color.WHITE);
        ((JComponent) statusFilter.getRenderer()).setBorder(new EmptyBorder(0, 10, 0, 0));

        inputsPanel.add(createLabeledField("Search by keyword", searchField));
        inputsPanel.add(createLabeledField("Filter by Status", statusFilter));

        controlCard.add(inputsPanel, BorderLayout.CENTER);

        mainBody.add(controlCard);
        mainBody.add(Box.createVerticalStrut(25));

        // --- CARD 2: LIST CONTENT ---
        // We do NOT use a JScrollPane inside here anymore.
        JPanel listCard = new CardPanel();
        listCard.setLayout(new BorderLayout());

        // A. Table Header
        listCard.add(createTableHeader(), BorderLayout.NORTH);

        // B. List Container (This grows dynamically now)
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);
        listContainer.setBorder(new EmptyBorder(10, CONTENT_PADDING, 20, CONTENT_PADDING));

        listCard.add(listContainer, BorderLayout.CENTER);

        mainBody.add(listCard);

        // --- OUTER SCROLL PANE ---
        JScrollPane mainScroll = new JScrollPane(mainBody);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(20);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.getViewport().setBackground(BG_COLOR);

        add(mainScroll, BorderLayout.CENTER);

        addDummyData(); // Add lots of data to test the scroll
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            mainScroll.getVerticalScrollBar().setValue(0);
        });
    }

    // --- HELPER METHODS ---

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(100, 100, 100));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTableHeader() {
        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setBackground(new Color(250, 250, 250));

        // Border matching the logic of the list
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(0, CONTENT_PADDING, 0, CONTENT_PADDING)
        ));

        tableHeader.setPreferredSize(new Dimension(1000, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- COL 1: Details ---
        JLabel h1 = new JLabel("FEEDBACK DETAILS");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        h1.setForeground(Color.GRAY);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 5);
        tableHeader.add(h1, gbc);

        // --- COL 2: Status ---
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        tableHeader.add(createHeaderLabel("STATUS", FeedbackRowPanel.WIDTH_STATUS, SwingConstants.LEFT), gbc);

        // --- COL 3: Responses ---
        gbc.gridx = 2;
        tableHeader.add(createHeaderLabel("RESPONSES", FeedbackRowPanel.WIDTH_RESPONSES, SwingConstants.LEFT), gbc);

        // --- COL 4: Actions ---
        gbc.gridx = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        tableHeader.add(createHeaderLabel("ACTIONS", FeedbackRowPanel.WIDTH_ACTIONS, SwingConstants.RIGHT), gbc);

        return tableHeader;
    }

    private JPanel createHeaderLabel(String text, int width, int alignment) {
        JLabel lbl = new JLabel(text, alignment);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Color.GRAY);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(width, 50));
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    private void addDummyData() {
        // Added 20 items so you can see the page scroll
        for(int i=0; i<20; i++) {
            String shortText = "Water pressure low";
            String longText = "The water pressure in Building A, 3rd floor has been extremely low for the past 2 days. It is difficult to shower.";

            listContainer.add(new FeedbackRowPanel(
                    i % 3 == 0 ? longText : shortText,
                    "Maintenance", "Resident " + i, "2023-10-10",
                    i%2==0?"Pending":"Resolved", i
            ));
        }
    }

    // --- CUSTOM COMPONENTS ---

    static class CardPanel extends JPanel {
        public CardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 20;
            int w = getWidth() - 1;
            int h = getHeight() - 1;

            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

            g2.setColor(BORDER_COLOR);
            g2.draw(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class RoundedTextField extends JTextField {
        private int radius;
        public RoundedTextField(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(5, 15, 5, 15));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);

            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);

            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageFeedbackUI::new);
    }
}