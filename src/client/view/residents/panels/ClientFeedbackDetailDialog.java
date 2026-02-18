package client.view.residents.panels;

import client.view.UiPreview;
import client.view.common.Buttons;
import client.view.common.CustomPanel;
import client.view.strings.Colors;
import common.Feedbacks;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static client.view.strings.Fonts.SegoeUi;
import static client.view.strings.Fonts.SegoeUiSemibold;

public class ClientFeedbackDetailDialog extends JDialog {
    private static final Color background = Colors.WHITE;
    private static final Color cardBackground = new Color(249, 250, 251);
    private static final Color darkTeal = Colors.SUB_PRIMARY;


    public ClientFeedbackDetailDialog(Frame frame, Feedbacks feedback, String status, String category, String authorName) {
        super(frame, "Feedback Details", true);
        setSize(850, 780);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        if (feedback == null) {
            throw new IllegalArgumentException("feedback must not be null");
        }

        String safeStatus = status != null && !status.trim().isEmpty() ? status.trim() : "Pending";
        String safeCategory = category != null && !category.trim().isEmpty() ? category.trim() : "General";

        String safeAuthorName = authorName != null && !authorName.trim().isEmpty()
                ? authorName.trim()
                : (feedback.getIssued_by() > 0 ? "User #" + feedback.getIssued_by() : "Unknown");

        Date issued = feedback.getIssued_date();
        String issuedDate = issued != null ? new SimpleDateFormat("MMM dd, yyyy").format(issued) : "—";
        String issuedTime = issued != null ? new SimpleDateFormat("hh:mm a").format(issued) : "—";

        int upvotes = feedback.getAttr_upvote() != null ? feedback.getAttr_upvote().length : 0;
        int downvotes = feedback.getAttr_downvote() != null ? feedback.getAttr_downvote().length : 0;

        String headerTitle = feedback.getTitle() != null && !feedback.getTitle().trim().isEmpty()
            ? feedback.getTitle().trim()
            : "Feedback Details";
        String contentBody = feedback.getContent() != null ? feedback.getContent() : "";

        CustomPanel main = new CustomPanel(30, background);
        main.setLayout(new BorderLayout());

        CustomPanel headerRow = new CustomPanel(30, darkTeal);
        headerRow.setRoundedCorners(true, true, false, false);
        headerRow.setLayout(new BorderLayout());
        headerRow.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerRow.setPreferredSize(new Dimension(850, 160));

        JPanel topHeaderRow = new JPanel(new BorderLayout());
        topHeaderRow.setOpaque(false);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badges.setOpaque(false);
        badges.add(new roundedStatusCategory(safeStatus.toUpperCase(), new Color(255, 255, 255, 60), Colors.WHITE));
        badges.add(new roundedStatusCategory(safeCategory, new Color(255, 255, 255, 60), Colors.WHITE));


        topHeaderRow.add(badges, BorderLayout.WEST);
        topHeaderRow.add(new Buttons.CloseButton(this, Colors.WHITE), BorderLayout.EAST);


        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(15, 0, 0, 0));


        JLabel title = new JLabel(headerTitle);
        title.setFont(SegoeUi(1, 30));
        title.setForeground(Color.WHITE);


        JLabel idLabel = new JLabel("ID: " + feedback.getAttr_id());
        idLabel.setFont(SegoeUi(0, 10));
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
        grid.add(detailCard("Submitted By", safeAuthorName, "ID: " + feedback.getIssued_by()));
        grid.add(detailCard("Location", feedback.getLocation() != null && !feedback.getLocation().trim().isEmpty() ? feedback.getLocation().trim() : "—", ""));
        grid.add(detailCard("Submitted On", issuedDate, issuedTime));
        grid.add(detailCard("Support", upvotes + " upvotes", downvotes + " downvotes"));


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
        contentLabel.setFont(SegoeUiSemibold(1, 18));
        bodyContainer.add(contentLabel);
        bodyContainer.add(Box.createVerticalStrut(8));

        JTextArea contentArea = new JTextArea(contentBody);
        styleTextArea(contentArea);

        CustomPanel contentCard = new CustomPanel(15, cardBackground);
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
        responseLabel.setFont(SegoeUiSemibold(1, 18));
        responseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyContainer.add(responseLabel);
        bodyContainer.add(Box.createVerticalStrut(8));

        CustomPanel responseCard = new CustomPanel(15, new Color(241, 248, 255));
        responseCard.setStroke(new Color(191, 219, 254), 1.2f);
        responseCard.setLayout(new BoxLayout(responseCard, BoxLayout.Y_AXIS));
        responseCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        responseCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        responseCard.setMaximumSize(new Dimension(790, 120));

        JPanel respTop = new JPanel(new BorderLayout());
        respTop.setOpaque(false);
        JPanel nameBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameBadge.setOpaque(false);
        String officialName = feedback.getAttr_approvedby() > 0 ? ("Official #" + feedback.getAttr_approvedby()) : "—";
        JLabel name = new JLabel(officialName);
        name.setFont(SegoeUi(1, 15));
        nameBadge.add(name);
        nameBadge.add(new roundedStatusCategory("Barangay Official", new Color(153, 204, 255), new Color(0, 102, 204)));

        JLabel date = new JLabel("");
        date.setFont(SegoeUi(0, 12));
        date.setForeground(new Color(148, 163, 184));
        respTop.add(nameBadge, BorderLayout.WEST);
        respTop.add(date, BorderLayout.EAST);

        JTextArea respMsg = new JTextArea("No official response yet.");
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
        ta.setFont(SegoeUi(0, 14));
        ta.setForeground(new Color(71, 85, 105));
    }

    private JPanel detailCard(String title, String main, String sub) {
        CustomPanel card = new CustomPanel(15, cardBackground);
        card.setLayout(new BorderLayout());
        card.setStroke( new Color(192, 192, 192), 1.2f);
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        card.setPreferredSize(new Dimension(375, 80)); // Tighter cards
        card.setMaximumSize(new Dimension(375, 80));

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setOpaque(false);

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(SegoeUi(1, 10));
        t.setForeground(new Color(0, 128, 255));

        JLabel m = new JLabel(main);
        m.setFont(SegoeUi(1, 15));


        JLabel s = new JLabel(sub);
        s.setFont(SegoeUi(0, 12));
        s.setForeground(new Color(148, 163, 184));


        txt.add(t); txt.add(m); txt.add(s);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    static class roundedStatusCategory extends JLabel {
        private final Color bg;
        roundedStatusCategory(String t, Color bg, Color fg) {
            super(t); this.bg = bg; setForeground(fg);
            setFont(SegoeUi(1, 11));
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

    public static void main(String[] args) {
        UiPreview.show(frame -> {
            Feedbacks demo = new Feedbacks(
                    1,
                    2,
                    0,
                    new String[]{"Pending"},
                    new String[]{"Infrastructure"},
                    "Street lights are not working",
                    "Main Road",
                    "They've been out for a week.",
                    new Date()
            );
            return new ClientFeedbackDetailDialog(frame, demo, "Pending", "Infrastructure", "User #2");
        });
    }
}

