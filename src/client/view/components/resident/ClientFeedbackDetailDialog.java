package client.view.components.resident;

import client.view.strings.Fonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ClientFeedbackDetailDialog extends JDialog {
    private static final Color background = Color.WHITE;
    private static final Color cardBackground = new Color(249, 250, 251);
    private static final Color darkTeal = new Color(0, 102, 102);


    public ClientFeedbackDetailDialog(Frame frame) {
        super(frame, "Feedback Details", true);
        setSize(850, 780);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        RoundedPanel main = new RoundedPanel(30, background);
        main.setLayout(new BorderLayout());
        main.setStroke( new Color(192, 192, 192), 1.5f);

        RoundedPanel headerRow = new RoundedPanel(15, darkTeal);
        headerRow.setLayout(new BorderLayout());
        headerRow.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerRow.setPreferredSize(new Dimension(850, 160));
        headerRow.setStroke(new Color(0, 102, 102), 2.5f);

        JPanel topHeaderRow = new JPanel(new BorderLayout());
        topHeaderRow.setOpaque(false);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badges.setOpaque(false);
        badges.add(new roundedStatusCategory("IN PROGRESS", new Color(255, 255, 255, 60), Color.WHITE));
        badges.add(new roundedStatusCategory("Infrastructure", new Color(255, 255, 255, 60), Color.WHITE));


        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeBtn.setForeground(new Color(255, 255, 255));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());


        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setForeground(Color.RED);
            }


            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setForeground(new Color(255, 255, 255));
            }
        });


        topHeaderRow.add(badges, BorderLayout.WEST);
        topHeaderRow.add(closeBtn, BorderLayout.EAST);


        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(15, 0, 0, 0));


        JLabel title = new JLabel("Feedback Details");
        title.setFont(Fonts.UI_TITLE);
        title.setForeground(Color.WHITE);


        JLabel idLabel = new JLabel("ID: ");
        idLabel.setFont(Fonts.UI_SUB_TEXT);
        idLabel.setForeground(new Color(255, 255, 255, 180));


        titlePanel.add(title);
        titlePanel.add(idLabel);


        headerRow.add(topHeaderRow, BorderLayout.NORTH);
        headerRow.add(titlePanel, BorderLayout.SOUTH);


        JPanel bodyContainer = new JPanel();
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        bodyContainer.setOpaque(false);
        bodyContainer.setBorder(new EmptyBorder(20, 30, 25, 30)); // Reduced top/bottom padding


        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setOpaque(false);
        grid.add(detailCard("Submitted By", "Maria Garcia", "user_002"));
        grid.add(detailCard("Location", "Main Road", "District 4"));
        grid.add(detailCard("Submitted On", "Feb 3, 2026", "05:56 PM"));
        grid.add(detailCard("Support", "13 upvotes", "High Priority"));


        JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gridWrapper.setOpaque(false);
        gridWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridWrapper.add(grid);
        gridWrapper.setMaximumSize(new Dimension(800, 190));


        bodyContainer.add(gridWrapper);
        bodyContainer.add(Box.createVerticalStrut(20));

        // Feedback Content
        JLabel contentLabel = new JLabel("Feedback Content");
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        bodyContainer.add(contentLabel);
        bodyContainer.add(Box.createVerticalStrut(8));

        JTextArea contentArea = new JTextArea("The street lights along Main Road have not been working for the past week.");
        styleTextArea(contentArea);

        RoundedPanel contentCard = new RoundedPanel(15, cardBackground);
        contentCard.setStroke( new Color(192, 192, 192), 1.0f);
        contentCard.setLayout(new BorderLayout());
        contentCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(contentArea);
        contentCard.setMaximumSize(new Dimension(790, 80));
        bodyContainer.add(contentCard);

        bodyContainer.add(Box.createVerticalStrut(20));

        // Official Response
        JLabel responseLabel = new JLabel("Official Response");
        responseLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        responseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyContainer.add(responseLabel);
        bodyContainer.add(Box.createVerticalStrut(8));

        RoundedPanel responseCard = new RoundedPanel(15, new Color(241, 248, 255));
        responseCard.setStroke(new Color(191, 219, 254), 1.2f);
        responseCard.setLayout(new BoxLayout(responseCard, BoxLayout.Y_AXIS));
        responseCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        responseCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        responseCard.setMaximumSize(new Dimension(790, 120));

        JPanel respTop = new JPanel(new BorderLayout());
        respTop.setOpaque(false);
        JPanel nameBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameBadge.setOpaque(false);
        JLabel name = new JLabel("Maria Santos");
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameBadge.add(name);
        nameBadge.add(new roundedStatusCategory("Barangay Official", new Color(153, 204, 255), new Color(0, 102, 204)));

        JLabel date = new JLabel("Feb 4, 2026 at 05:56 PM");
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        date.setForeground(new Color(148, 163, 184));
        respTop.add(nameBadge, BorderLayout.WEST);
        respTop.add(date, BorderLayout.EAST);

        JTextArea respMsg = new JTextArea("Maintenance is scheduled for the street lights next week.");
        styleTextArea(respMsg);
        respMsg.setBorder(new EmptyBorder(6, 0, 0, 0));

        responseCard.add(respTop);
        responseCard.add(respMsg);
        bodyContainer.add(responseCard);

        main.add(headerRow, BorderLayout.NORTH);
        main.add(bodyContainer, BorderLayout.CENTER);
        add(main);
    }

    private void styleTextArea(JTextArea ta) {
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ta.setForeground(new Color(71, 85, 105));
    }

    private JPanel detailCard(String title, String main, String sub) {
        RoundedPanel card = new RoundedPanel(15, cardBackground);
        card.setLayout(new BorderLayout());
        card.setStroke( new Color(192, 192, 192), 1.2f);
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        card.setPreferredSize(new Dimension(375, 80)); // Tighter cards
        card.setMaximumSize(new Dimension(375, 80));

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setOpaque(false);

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(new Color(0, 128, 255));

        JLabel m = new JLabel(main);
        m.setFont(new Font("Segoe UI", Font.BOLD, 15));


        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(new Color(148, 163, 184));


        txt.add(t); txt.add(m); txt.add(s);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }


    static class RoundedPanel extends JPanel {
        private final int r;
        private final Color c;
        private Color sCol = new Color(241, 245, 249);
        private float sWidth = 1.0f;


        RoundedPanel(int radius, Color color) {
            r = radius;
            c = color;
            setOpaque(false);
        }
        public void setStroke(Color col, float w) {
            this.sCol = col;
            this.sWidth = w;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), r, r));
            if (sWidth > 0) {
                g2.setStroke(new BasicStroke(sWidth));
                g2.setColor(sCol);
                g2.draw(new RoundRectangle2D.Double(sWidth/2, sWidth/2, getWidth()-sWidth, getHeight()-sWidth, r, r));
            }
            g2.dispose();
        }
    }

    static class roundedStatusCategory extends JLabel {
        private final Color bg;
        roundedStatusCategory(String t, Color bg, Color fg) {
            super(t); this.bg = bg; setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(6, 12, 6, 12));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

