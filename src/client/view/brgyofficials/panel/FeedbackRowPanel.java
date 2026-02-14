package client.view.brgyofficials.panel;

import javax.swing.*;
import java.awt.*;

public class FeedbackRowPanel extends JPanel {

    // --- STRICT COLUMN SIZES ---
    public static final int WIDTH_STATUS = 160;
    public static final int WIDTH_RESPONSES = 120;
    public static final int WIDTH_ACTIONS = 140;

    private static final Color STATUS_PENDING = new Color(255, 193, 7);
    private static final Color STATUS_IN_PROGRESS = new Color(23, 162, 184);
    private static final Color STATUS_RESOLVED = new Color(40, 167, 69);

    public FeedbackRowPanel(String title, String category, String sender, String date, String status, int replies) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // IMPORTANT: Removed strict height limit.
        // We only set Min/Pref width, but allow height to expand.
        setMinimumSize(new Dimension(800, 75));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align top-left so long text flows down

        // --- COL 1: Details (Elastic & Wrapping) ---

        // Changed to BorderLayout so Title can grow while Meta stays at bottom
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setOpaque(false);

        // USE JTextArea INSTEAD OF JLabel FOR WRAPPING
        JTextArea titleArea = new JTextArea(title);
        titleArea.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        titleArea.setOpaque(false);
        titleArea.setEditable(false);
        titleArea.setHighlighter(null); // Removes selection capability so it looks like a label
        titleArea.setBorder(null);

        JLabel lblMeta = new JLabel("<html><font color='#888888'>" + category + " • " + sender + " • " + date + "</font></html>");
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        detailsPanel.add(titleArea, BorderLayout.CENTER);
        detailsPanel.add(lblMeta, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.weightx = 1.0; // Grows to fill space
        gbc.fill = GridBagConstraints.BOTH; // Fill both horiz and vert
        gbc.insets = new Insets(15, 20, 15, 10); // More padding top/bottom
        add(detailsPanel, gbc);

        // --- COL 2: Status (Fixed Width, Top Aligned) ---
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusContainer.setOpaque(false);
        statusContainer.setPreferredSize(new Dimension(WIDTH_STATUS, 50)); // Height doesn't strictly matter here, just width

        JButton statusButton = new RoundedBadge(status);
        styleStatusButton(statusButton, status);
        statusButton.setPreferredSize(new Dimension(100, 30));
        statusContainer.add(statusButton);

        gbc.gridx = 1;
        gbc.weightx = 0; // Fixed
        gbc.fill = GridBagConstraints.NONE;
        // Adjust top inset to align with the first line of text roughly
        gbc.insets = new Insets(15, 0, 0, 0);
        add(statusContainer, gbc);

        // --- COL 3: Responses (Fixed Width) ---
        JPanel respContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        respContainer.setOpaque(false);
        respContainer.setPreferredSize(new Dimension(WIDTH_RESPONSES, 50));

        JLabel respLabel = new JLabel(replies + " Replies");
        respLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        respLabel.setForeground(Color.DARK_GRAY);
        respContainer.add(respLabel);

        gbc.gridx = 2;
        gbc.insets = new Insets(18, 0, 0, 0); // Slight offset to match text baseline
        add(respContainer, gbc);

        // --- COL 4: Actions (Fixed Width) ---
        JPanel actionContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionContainer.setOpaque(false);
        actionContainer.setPreferredSize(new Dimension(WIDTH_ACTIONS, 50));

        JButton viewBtn = createIconBtn("👁", "View", Color.DARK_GRAY);
        JButton editBtn = createIconBtn("✎", "Edit", Color.DARK_GRAY);
        JButton archiveBtn = createIconBtn("🗑", "Archive", new Color(220, 53, 69));

        actionContainer.add(viewBtn);
        actionContainer.add(editBtn);
        actionContainer.add(archiveBtn);

        gbc.gridx = 3;
        gbc.insets = new Insets(12, 0, 0, 20); // Padding from right edge
        add(actionContainer, gbc);
    }

    // -- Helpers --
    private JButton createIconBtn(String text, String toolTip, Color color) {
        JButton btn = new JButton(text);
        btn.setToolTipText(toolTip);
        btn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
        btn.setForeground(color);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0,0,0,0));
        btn.setPreferredSize(new Dimension(30, 30));
        return btn;
    }

    private void styleStatusButton(JButton btn, String status) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        switch (status) {
            case "Pending": btn.setBackground(STATUS_PENDING); break;
            case "Resolved": btn.setBackground(STATUS_RESOLVED); break;
            case "In Progress": btn.setBackground(STATUS_IN_PROGRESS); break;
            default: btn.setBackground(Color.GRAY);
        }
    }

    static class RoundedBadge extends JButton {
        public RoundedBadge(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}