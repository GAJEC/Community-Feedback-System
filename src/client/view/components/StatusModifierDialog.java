package client.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class StatusModifierDialog extends JDialog {

    // --- COLORS ---
    private static final Color MAIN_GREEN = new Color(34, 197, 94); // Vibrant Green
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_GRAY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    // Status Indicator Colors
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

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // 1. GREEN HEADER
        add(createHeader(), BorderLayout.NORTH);
        setUndecorated(true);

        //Border
        JPanel root = (JPanel) getContentPane();
        root.setBorder(new client.view.components.DropShadowBorder(2));

        setSize(500, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // 2. EXIT IF MOUSE PRESSES OUTSIDE (WINDOW LOSES FOCUS)
        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {}

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                // Closes the dialog if the user clicks away
                dispose();
            }
        });

        // 2. SCROLLABLE CONTENT
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // -- Feedback Details --
        JLabel lblSubtitle = new JLabel("FEEDBACK DETAILS");
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSubtitle.setForeground(TEXT_GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblSubtitle);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextArea txtDetails = new JTextArea(feedbackDetails);
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDetails.setForeground(TEXT_DARK);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        //Hide cursor caret
        txtDetails.setCaretColor(new Color(0, 0, 0, 0));
        txtDetails.setEditable(false);
        txtDetails.setBackground(new Color(249, 250, 251));
        txtDetails.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        txtDetails.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane textScroller = new JScrollPane(txtDetails);
        textScroller.setPreferredSize(new Dimension(400, 80)); // Keeps the box small
        textScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
        textScroller.setBorder(null); // Clean look

        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(textScroller);

        // -- Display Options --
        JLabel lblStatus = new JLabel("DISPLAY FOR");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(TEXT_GRAY);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblStatus);
        contentPanel.add(Box.createVerticalStrut(10));

        // Add Options
        contentPanel.add(createOption("In Progress", "Currently being worked on", COL_PROGRESS));
        contentPanel.add(Box.createVerticalStrut(10));

        contentPanel.add(createOption("Pending", "Awaiting review", COL_PENDING));
        contentPanel.add(Box.createVerticalStrut(10));

        contentPanel.add(createOption("Resolved", "Issue has been fixed", COL_RESOLVED));
        contentPanel.add(Box.createVerticalStrut(10));

        contentPanel.add(createOption("Archived", "Closed and hidden", COL_ARCHIVED));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 3. FOOTER
        add(createFooter(), BorderLayout.SOUTH);

        // Initial Selection
        selectOption(currentStatus);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(MAIN_GREEN);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top Row: Logo + Title
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logo Placeholder (White Box with 'L')
        JLabel logo = new JLabel("LOGO", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        logo.setForeground(MAIN_GREEN);
        logo.setBackground(Color.WHITE);
        logo.setOpaque(true);
        logo.setPreferredSize(new Dimension(40, 20));
        logo.setBorder(new EmptyBorder(2, 5, 2, 5)); // rounded look simulated via border

        // Main Title
        JLabel title = new JLabel(" Update Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        topRow.add(logo);
        topRow.add(title);

        // Tagline
        JLabel tagline = new JLabel("Change the feedback status");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(230, 255, 230)); // Light Green/White
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(topRow);
        header.add(Box.createVerticalStrut(5));
        header.add(tagline);

        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        footer.setBackground(BG_COLOR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 240, 240)));

        ModernButton btnCancel = new ModernButton("Cancel", Color.WHITE, TEXT_DARK, new Color(200, 200, 200));
        btnCancel.setPreferredSize(new Dimension(100, 45)); // Smaller Width
        btnCancel.addActionListener(e -> dispose());

        ModernButton btnUpdate = new ModernButton("Update Status", MAIN_GREEN, Color.WHITE, MAIN_GREEN);
        btnUpdate.setPreferredSize(new Dimension(180, 45)); // Larger Width
        btnUpdate.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Status updated to: " + selectedStatus);
            dispose();
        });

        footer.add(btnCancel);
        footer.add(btnUpdate);
        return footer;
    }

    private StatusCard createOption(String title, String desc, Color baseColor) {
        StatusCard card = new StatusCard(title, desc, baseColor);
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

    // --- CUSTOM BUTTON CLASS ---
    static class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private Color textColor;
        private Color borderColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color bg, Color fg, Color border) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg == Color.WHITE ? new Color(245, 245, 245) : bg.darker();
            this.textColor = fg;
            this.borderColor = border;

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(textColor);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
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

            // Background
            g2.setColor(isHovered ? hoverColor : normalColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            // Border
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            // Text
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int x = (getWidth() - stringBounds.width) / 2;
            int y = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    // --- CARD COMPONENT ---
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
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Radio
            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
            radioPanel.setOpaque(false);
            radioButton = new JRadioButton();
            radioButton.setOpaque(false);
            radioButton.addActionListener(e -> {
                updateVisuals();
                selectedStatus = title;
            });
            radioPanel.add(radioButton);
            radioPanel.setBorder(new EmptyBorder(0, 0, 0, 15));
            add(radioPanel, BorderLayout.WEST);

            // Text
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblTitle.setForeground(TEXT_DARK);

            JLabel lblDesc = new JLabel(desc);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblDesc.setForeground(TEXT_GRAY);

            textPanel.add(lblTitle);
            textPanel.add(lblDesc);
            add(textPanel, BorderLayout.CENTER);

            // Strip
            JPanel strip = new JPanel();
            strip.setPreferredSize(new Dimension(5, 0));
            strip.setBackground(baseColor);
            add(strip, BorderLayout.EAST);

            // Listener
            MouseAdapter adapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    radioButton.setSelected(true);
                    updateVisuals();
                    selectedStatus = title;
                }
            };
            addMouseListener(adapter);
            textPanel.addMouseListener(adapter);
            lblTitle.addMouseListener(adapter);
            lblDesc.addMouseListener(adapter);
        }

        public void setSelected(boolean b) {
            radioButton.setSelected(b);
            updateVisuals();
        }

        private void updateVisuals() {
            for(StatusCard c : cards) {
                if(c != this) {
                    c.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
            if (radioButton.isSelected()) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(baseColor, 2),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)
                ));
            }
            revalidate();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new StatusModifierDialog(null, "In Progress", "User reported a pothole at Main St.").setVisible(true);
        });
    }
}