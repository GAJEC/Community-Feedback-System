package client.view.components;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class StatusModifierDialog extends JDialog {

    private static final Color MAIN_GREEN = new Color(34, 197, 94);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_GRAY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    private static final Color COL_PROGRESS = new Color(13, 110, 253);
    private static final Color COL_PENDING  = new Color(255, 193, 7);
    private static final Color COL_RESOLVED = new Color(25, 135, 84);
    private static final Color COL_ARCHIVED = new Color(108, 117, 125);

    private final List<StatusCard> cards = new ArrayList<>();
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private String selectedStatus;

    public StatusModifierDialog(JFrame parent, String currentStatus, String feedbackDetails) {
        super(parent, "Update Status", true);
        this.selectedStatus = currentStatus;

        setUndecorated(true);
        setSize(550, 700);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new LocalShadowBorder(2));
        setContentPane(root);

        // 1. HEADER
        add(createHeader(), BorderLayout.NORTH);

        // 2. CLOSE ON CLICK OUTSIDE
        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override public void windowGainedFocus(java.awt.event.WindowEvent e) {}
            @Override public void windowLostFocus(java.awt.event.WindowEvent e) { dispose(); }
        });

        // 3. MAIN CONTENT PANEL
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // -- FEEDBACK LABEL --
        JLabel lblSubtitle = new JLabel("FEEDBACK DETAILS");
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSubtitle.setForeground(TEXT_GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblSubtitle);

        contentPanel.add(createSpacer(10));

        // -- FEEDBACK TEXT AREA --
        JTextArea txtDetails = new JTextArea(feedbackDetails);
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDetails.setForeground(TEXT_DARK);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setEditable(false);
        txtDetails.setFocusable(false);
        txtDetails.setBackground(new Color(248, 249, 250));

        // *** ADDED PADDING HERE ***
        // Top: 15, Left: 20, Bottom: 15, Right: 20
        txtDetails.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JScrollPane textScroller = new JScrollPane(txtDetails);
        textScroller.setPreferredSize(new Dimension(490, 120));
        textScroller.setMaximumSize(new Dimension(490, 120));
        // Only the line border on the outside now, padding is inside the text area
        textScroller.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        textScroller.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(textScroller);
        contentPanel.add(createSpacer(25));

        // -- OPTIONS --
        contentPanel.add(createOption("In Progress", "Currently being worked on", COL_PROGRESS));
        contentPanel.add(createSpacer(10));
        contentPanel.add(createOption("Pending", "Awaiting review", COL_PENDING));
        contentPanel.add(createSpacer(10));
        contentPanel.add(createOption("Resolved", "Issue has been fixed", COL_RESOLVED));
        contentPanel.add(createSpacer(10));
        contentPanel.add(createOption("Archived", "Closed and hidden", COL_ARCHIVED));

        add(contentPanel, BorderLayout.CENTER);

        // 4. FOOTER
        add(createFooter(), BorderLayout.SOUTH);

        selectOption(currentStatus);
    }

    private Component createSpacer(int height) {
        Box.Filler filler = (Box.Filler) Box.createVerticalStrut(height);
        filler.setAlignmentX(Component.LEFT_ALIGNMENT);
        return filler;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(MAIN_GREEN);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));
        // Ensure header aligns left so children inherit alignment logic
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logo = new JLabel("↑", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(MAIN_GREEN);
        logo.setPreferredSize(new Dimension(30, 30));

        JLabel title = new JLabel("  Update Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        topRow.add(logo);
        topRow.add(title);

        JLabel tagline = new JLabel("Change the feedback status");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(240, 255, 240));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(topRow);
        header.add(Box.createVerticalStrut(4));
        header.add(tagline);
        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(BG_COLOR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        ModernButton btnCancel = new ModernButton("Cancel", Color.WHITE, TEXT_DARK, BORDER_COLOR);
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());

        ModernButton btnUpdate = new ModernButton("Update Status", MAIN_GREEN, Color.WHITE, MAIN_GREEN);
        btnUpdate.setPreferredSize(new Dimension(160, 40));
        btnUpdate.addActionListener(e -> dispose());

        footer.add(btnCancel);
        footer.add(btnUpdate);
        return footer;
    }

    private StatusCard createOption(String title, String desc, Color baseColor) {
        StatusCard card = new StatusCard(title, desc, baseColor);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.add(card);
        buttonGroup.add(card.radioButton);
        return card;
    }

    private void selectOption(String statusTitle) {
        for (StatusCard card : cards) {
            if (card.title.equalsIgnoreCase(statusTitle)) {
                card.setSelected(true);
                selectedStatus = statusTitle;
            }
        }
    }

    // --- HELPER CLASSES ---

    static class LocalShadowBorder extends AbstractBorder {
        private final int padding;
        public LocalShadowBorder(int padding) { this.padding = padding; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 40));
            g2.drawRoundRect(x, y, width - 1, height - 1, 12, 12);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(padding, padding, padding, padding); }
    }

    static class ModernButton extends JButton {
        private Color normalColor, hoverColor, textColor, borderColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color bg, Color fg, Color border) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg == Color.WHITE ? new Color(245, 245, 245) : bg.darker();
            this.textColor = fg;
            this.borderColor = border;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? hoverColor : normalColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class StatusCard extends JPanel {
        String title;
        JRadioButton radioButton;
        Color baseColor;

        public StatusCard(String title, String desc, Color baseColor) {
            this.title = title;
            this.baseColor = baseColor;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)));
            setMaximumSize(new Dimension(490, 70));
            setPreferredSize(new Dimension(490, 70));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            radioButton = new JRadioButton();
            radioButton.setOpaque(false);
            radioButton.addActionListener(e -> { updateVisuals(); selectedStatus = title; });

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            JLabel lblT = new JLabel(title);
            lblT.setFont(new Font("Segoe UI", Font.BOLD, 15));
            JLabel lblD = new JLabel(desc);
            lblD.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblD.setForeground(TEXT_GRAY);
            textPanel.add(lblT);
            textPanel.add(lblD);

            add(radioButton, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { radioButton.setSelected(true); updateVisuals(); selectedStatus = title; }
            });
        }

        public void setSelected(boolean b) { radioButton.setSelected(b); updateVisuals(); }

        private void updateVisuals() {
            for (StatusCard c : cards) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)));
            }
            if (radioButton.isSelected()) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(baseColor, 2),
                        BorderFactory.createEmptyBorder(11, 14, 11, 14)));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StatusModifierDialog(null, "Pending", "Sample report: The street light on 5th Avenue is flickering constantly, causing distraction to drivers at night.").setVisible(true);
        });
    }
}