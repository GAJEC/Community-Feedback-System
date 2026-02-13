package client.view.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SystemControls extends JFrame {



    private final Color COLOR_CARD_BORDER = new Color(230, 233, 237);
    private final Color COLOR_BLUE_BORDER = new Color(207, 226, 255);
    private final Color COLOR_BLUE_BG = new Color(240, 247, 255);
    private final Color COLOR_TEXT_MAIN = new Color(50, 50, 60);
    private final Color COLOR_TEXT_SUB = new Color(100, 100, 110);
    private final Color COLOR_ACCENT_BLUE = new Color(45, 85, 155);



    public SystemControls() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 900);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 200, 30, 200));


        JLabel titleLabel = new JLabel("System Controls");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Administrative tools and system management");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(COLOR_TEXT_SUB);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));


        JPanel cardsWrapper = new JPanel(new GridLayout(2, 1, 0, 20));
        cardsWrapper.setOpaque(false);
        cardsWrapper.setMaximumSize(new Dimension(850, 550));
        cardsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel adminCard = createRoundedCard(Color.WHITE, COLOR_CARD_BORDER);
        adminCard.setLayout(new BorderLayout());

        JLabel adminTitle = new JLabel(" Administrator Privileges");
        adminTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        adminTitle.setForeground(COLOR_TEXT_MAIN);
        adminCard.add(adminTitle, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new GridLayout(5, 1, 0, 0));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(10, 15, 0, 0));


        String[] items = {
                "Full access to all feedback submissions",
                "Ability to update feedback status and responses",
                "Archive and delete capabilities",
                "System-wide statistics and reporting",
                "User management and oversight"
        };

        for (String item : items) {
            JPanel rowItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            rowItem.setOpaque(false);

            JLabel dotLabel = new JLabel("\u2022   ");
            dotLabel.setForeground(COLOR_ACCENT_BLUE);
            dotLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

            JLabel textLabel = new JLabel(item);
            textLabel.setForeground(COLOR_TEXT_SUB);
            textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

            rowItem.add(dotLabel);
            rowItem.add(textLabel);
            listPanel.add(rowItem);
        }
        adminCard.add(listPanel, BorderLayout.CENTER);


        JPanel sdgContainer = createRoundedCard(COLOR_BLUE_BG, COLOR_BLUE_BORDER);
        sdgContainer.setLayout(new BoxLayout(sdgContainer, BoxLayout.Y_AXIS));

        JLabel sdgHeader = new JLabel("SDG Alignment");
        sdgHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
        sdgHeader.setForeground(COLOR_ACCENT_BLUE);
        sdgHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sdgSub = new JLabel("This Community Feedback System aligns with the United Nations Sustainable Development Goals:");
        sdgSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sdgSub.setForeground(COLOR_TEXT_SUB);
        sdgSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        sdgContainer.add(sdgHeader);
        sdgContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        sdgContainer.add(sdgSub);
        sdgContainer.add(Box.createRigidArea(new Dimension(0, 15)));


        sdgContainer.add(createGoalBox("SDG 11: Sustainable Cities and Communities",
                "Making cities inclusive, safe, resilient and sustainable by enabling citizen participation in community development."));

        sdgContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        sdgContainer.add(createGoalBox("SDG 16: Peace, Justice and Strong Institutions",
                "Promoting peaceful and inclusive societies, providing access to justice and building effective, accountable institutions..."));

        cardsWrapper.add(adminCard);
        cardsWrapper.add(sdgContainer);

        mainPanel.add(cardsWrapper);
        add(new JScrollPane(mainPanel));
    }

    private JPanel createRoundedCard(Color bg, Color border) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        return card;
    }

    private JPanel createGoalBox(String title, String desc) {
        JPanel box = createRoundedCard(Color.WHITE, COLOR_CARD_BORDER);
        box.setLayout(new BorderLayout(0, 5));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setForeground(COLOR_ACCENT_BLUE);

        JTextArea d = new JTextArea(desc);
        d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        d.setForeground(COLOR_TEXT_SUB);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        d.setOpaque(false);
        d.setEditable(false);
        d.setFocusable(false);
        d.setCursor(null);

        box.add(t, BorderLayout.NORTH);
        box.add(d, BorderLayout.CENTER);
        return box;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new SystemControls().setVisible(true));
    }
}
