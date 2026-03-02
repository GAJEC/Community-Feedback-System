package client.view.brgyofficials;

import client.view.common.Buttons;
import client.view.common.RoundedInputField;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PostAnnouncementDialog extends JDialog {

    private static final Color MAIN_GREEN = new Color(34, 197, 94);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_GRAY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color INPUT_BG = new Color(248, 249, 250);

    private final String userName;
    private final int userId;
    private PostAnnouncementListener listener;
    private RoundedInputField txtSubject;
    private JTextArea txtDetails;
    private Buttons btnPost;

    public PostAnnouncementDialog(JFrame parent, String userName, int userId, PostAnnouncementListener listener) {
        super(parent, "Create Announcement", true);
        this.userName = userName;
        this.userId = userId;
        this.listener = listener;

        setResizable(true);
        setUndecorated(true);
        setSize(450, 550);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new LocalShadowBorder(5));
        setContentPane(root);

        add(createHeader(), BorderLayout.NORTH);

        add(createContent(), BorderLayout.CENTER);

        add(createFooter(), BorderLayout.SOUTH);

    }

    public void setListener(PostAnnouncementListener listener) {
        this.listener = listener;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setBusy(boolean busy) {
        if (btnPost != null) {
            btnPost.setEnabled(!busy);
        }
    }

    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void closeDialog() {
        dispose();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(MAIN_GREEN);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel("📢");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setForeground(Color.WHITE);

        JLabel title = new JLabel("Post Announcement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        topRow.add(iconLbl);
        topRow.add(title);

        JLabel tagline = new JLabel("Broadcast updates to the community");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(240, 255, 240));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(5, 45, 0, 0));

        header.add(topRow);
        header.add(tagline);
        return header;
    }

    private JPanel createContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        profilePanel.setOpaque(false);

        JLabel avatar = new JLabel(String.valueOf(userName.charAt(0)), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(233, 236, 239));
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(MAIN_GREEN);

        JPanel textInfo = new JPanel(new GridLayout(2, 1));
        textInfo.setOpaque(false);

        JLabel lblName = new JLabel(userName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(TEXT_DARK);

        JLabel lblRole = new JLabel("Brgy Official");
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblRole.setForeground(TEXT_GRAY);

        textInfo.add(lblName);
        textInfo.add(lblRole);

        profilePanel.add(avatar);
        profilePanel.add(textInfo);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        contentPanel.add(profilePanel, gbc);

        JLabel lblSubject = new JLabel("SUBJECT");
        lblSubject.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSubject.setForeground(TEXT_GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        contentPanel.add(lblSubject, gbc);

        txtSubject = new RoundedInputField(null, "Enter the topic of announcement...", false);
        txtSubject.setBackgroundColor(INPUT_BG);

        txtSubject.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(txtSubject, gbc);

        JLabel lblDetails = new JLabel("ANNOUNCEMENT DETAILS");
        lblDetails.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDetails.setForeground(TEXT_GRAY);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        contentPanel.add(lblDetails, gbc);

        txtDetails = new JTextArea();
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDetails.setForeground(TEXT_DARK);
        txtDetails.setBackground(INPUT_BG);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtDetails);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(scrollPane, gbc);

        return contentPanel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(BG_COLOR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);

        Buttons btnCancel = new Buttons(
                "Cancel",
                15,
                Color.WHITE,
                TEXT_DARK,
                new Color(245, 245, 245),
                btnFont
        );
        btnCancel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> {
            dispose();
            if (listener != null) {
                listener.onCancel();
            }
        });

        btnPost = new Buttons(
                "Post Now",
                15,
                MAIN_GREEN,
                Color.WHITE,
                new Color(25, 135, 84),
                btnFont
        );
        btnPost.setPreferredSize(new Dimension(140, 40));
        btnPost.addActionListener(e -> postAnnouncement());

        footer.add(btnCancel);
        footer.add(btnPost);
        return footer;
    }

    private void postAnnouncement() {
        String subject = txtSubject != null ? txtSubject.getText().trim() : "";
        String details = txtDetails != null ? txtDetails.getText().trim() : "";
        if (listener != null) {
            listener.onSubmit(userId, userName, subject, details);
        }
    }

    public interface PostAnnouncementListener {
        void onSubmit(int userId, String userName, String subject, String details);
        void onCancel();
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
}