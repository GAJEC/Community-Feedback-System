package client.view.brgyofficials.panel;

import client.view.common.Buttons; // Imported your custom button

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

    private static final int COMPONENT_WIDTH = 380;

    public StatusModifierDialog(JFrame parent, String currentStatus, String feedbackDetails) {
        super(parent, "Update Status", true);
        this.selectedStatus = currentStatus;

        setUndecorated(true);
        // 1. UPDATED SIZE
        setSize(450, 550);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new LocalShadowBorder(2));
        setContentPane(root);

        // HEADER
        add(createHeader(), BorderLayout.NORTH);

        // CLOSE ON CLICK OUTSIDE
        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override public void windowGainedFocus(java.awt.event.WindowEvent e) {}
            @Override public void windowLostFocus(java.awt.event.WindowEvent e) { dispose(); }
        });

        // MAIN CONTENT PANEL
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        // -- FEEDBACK LABEL --
        JLabel lblSubtitle = new JLabel("FEEDBACK DETAILS");
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSubtitle.setForeground(TEXT_GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblSubtitle);

        contentPanel.add(createSpacer(8));

        // -- FEEDBACK TEXT AREA --
        JTextArea txtDetails = new JTextArea(feedbackDetails);
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Slightly smaller font
        txtDetails.setForeground(TEXT_DARK);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setEditable(false);
        txtDetails.setFocusable(false);
        txtDetails.setBackground(new Color(248, 249, 250));
        txtDetails.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane textScroller = new JScrollPane(txtDetails);

        textScroller.setPreferredSize(new Dimension(COMPONENT_WIDTH, 80));
        textScroller.setMaximumSize(new Dimension(COMPONENT_WIDTH, 80));
        textScroller.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        textScroller.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(textScroller);
        contentPanel.add(createSpacer(15)); // Reduced spacer

        // Reduced spacer between cards to 8px to fit height
        contentPanel.add(createOption("In Progress", "Currently being worked on", COL_PROGRESS));
        contentPanel.add(createSpacer(8));
        contentPanel.add(createOption("Pending", "Awaiting review", COL_PENDING));
        contentPanel.add(createSpacer(8));
        contentPanel.add(createOption("Resolved", "Issue has been fixed", COL_RESOLVED));
        contentPanel.add(createSpacer(8));
        contentPanel.add(createOption("Archived", "Closed and hidden", COL_ARCHIVED));

        add(contentPanel, BorderLayout.CENTER);

        // FOOTER
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
        header.setBorder(new EmptyBorder(20, 30, 20, 30)); // Slightly compressed header padding
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
        logo.setPreferredSize(new Dimension(28, 28));

        JLabel title = new JLabel("  Update Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        topRow.add(logo);
        topRow.add(title);

        JLabel tagline = new JLabel("Change the feedback status");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setForeground(new Color(240, 255, 240));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(topRow);
        header.add(Box.createVerticalStrut(2));
        header.add(tagline);
        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(BG_COLOR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);

        Buttons btnCancel = new Buttons(
                "Cancel",
                15,                   // Radius
                Color.WHITE,          // Background
                TEXT_DARK,            // Foreground
                new Color(245, 245, 245), // Hover color
                btnFont               // Font
        );
        btnCancel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.addActionListener(e -> dispose());

        // --- Update Button ---
        Buttons btnUpdate = new Buttons(
                "Update Status",
                15,                   // Radius
                MAIN_GREEN,           // Background
                Color.WHITE,          // Foreground
                new Color(25, 135, 84), // Hover color (Darker green)
                btnFont               // Font
        );
        btnUpdate.setPreferredSize(new Dimension(140, 35));
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
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));

            setMaximumSize(new Dimension(COMPONENT_WIDTH, 55));
            setPreferredSize(new Dimension(COMPONENT_WIDTH, 55));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            radioButton = new JRadioButton();
            radioButton.setOpaque(false);
            radioButton.addActionListener(e -> { updateVisuals(); selectedStatus = title; });

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            JLabel lblT = new JLabel(title);
            lblT.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel lblD = new JLabel(desc);
            lblD.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
            if (radioButton.isSelected()) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(baseColor, 2),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StatusModifierDialog(null, "Pending", "Bakit ang pogi ko.").setVisible(true);
        });
    }
}