package client.view.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SummaryAdminDashboard extends JFrame {


    private final Color bgLight = new Color(245, 246, 250);
    private final Color logoColor = new Color(0, 80, 80);
    private final Color purple = new Color(145, 30, 235);
    private final Color blue = new Color(25, 118, 210);
    private final Color green = new Color(0, 200, 83);
    private final Color orange = new Color(255, 109, 0);
    private final Color navGray = new Color(150, 150, 150);

    public SummaryAdminDashboard() {

        setSize(1280, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgLight);
        setLayout(new BorderLayout());


        JPanel topContainer = new JPanel(new BorderLayout());

        add(topContainer, BorderLayout.NORTH);


        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(bgLight);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
        JLabel title = new JLabel("System Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridy = 0;
        gbc.insets = new Insets(-25, 0, 10, 0);
        mainContent.add(title, gbc);


        JLabel subTitle = new JLabel("Comprehensive summary of all system data and activity");
        subTitle.setForeground(Color.GRAY);
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(-10, 0, 30, 0);
        mainContent.add(subTitle, gbc);


        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        cardPanel.setOpaque(false);

        cardPanel.add(new InfoCard("Total Feedbacks", "5", "All records", purple, "res/assets/feedbacks_icon.png"));
        cardPanel.add(new InfoCard("Active Citizens", "5", "Unique users", blue, "res/assets/activecitizens_icon.png"));
        cardPanel.add(new InfoCard("Total Responses", "3", "Official replies", green, "res/assets/totalresponses_icon.png"));
        cardPanel.add(new InfoCard("Avg Upvotes", "19.8", "Engagement", orange, "res/assets/upvotes_icon.png"));

        gbc.gridy = 2;
        mainContent.add(cardPanel, gbc);


        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainContent.add(createDataGrid(), gbc);




    }









    private JPanel createDataGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 10, 5);


        gbc.gridy = 0;
        gbc.gridx = 0; grid.add(createStatusPanel(), gbc);
        gbc.gridx = 1; grid.add(createPerformancePanel(), gbc);
        gbc.gridx = 2; grid.add(createAlertsPanel(), gbc);


        gbc.gridy = 1;
        gbc.gridx = 0; gbc.gridwidth = 1; grid.add(createCategoryPanel(), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; grid.add(createActivityPanel(), gbc);


        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 5, 10, 5);
        grid.add(createWeeklyActivityPanel(), gbc);

        return grid;
    }







    private JPanel createStatusPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel statusIcon = new JLabel();
        try {

            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/status distribution.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            statusIcon.setIcon(icon);
        } catch (Exception e) {
            statusIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Status Distribution");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(statusIcon);
        headerPanel.add(titleLabel);


        JPanel content = new JPanel(new GridLayout(4, 1, 0, 10));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        content.add(createLabeledProgress("Pending", 40, orange, "2"));
        content.add(createLabeledProgress("In Progress", 40, blue, "2"));
        content.add(createLabeledProgress("Resolved", 20, green, "1"));
        content.add(createLabeledProgress("Archived", 0, Color.LIGHT_GRAY, "0"));


        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }





    private JPanel createPerformancePanel() {

        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel perfIcon = new JLabel();
        try {

            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/performance_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            perfIcon.setIcon(icon);
        } catch (Exception e) {
            perfIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Performance Metrics");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(perfIcon);
        headerPanel.add(titleLabel);


        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        int total = 5; int resolved = 1;
        int completionPercentage = (int) ((double) resolved / total * 100);

        content.add(createLabeledProgress("Completion Rate", completionPercentage, green, completionPercentage + ".0%"));
        content.add(Box.createVerticalStrut(30));
        content.add(createLabeledProgress("Response Rate", 60, blue, "60.0%"));

        content.add(Box.createVerticalStrut(25));
        JLabel activeOff = new JLabel("Active Officials: 1");
        activeOff.setForeground(purple);
        activeOff.setFont(new Font("SansSerif", Font.BOLD, 12));
        content.add(activeOff);


        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }





    private JPanel createAlertsPanel() {

        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel alertIcon = new JLabel();
        try {

            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/priorityalert_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            alertIcon.setIcon(icon);
        } catch (Exception e) {
            alertIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Priority Alerts");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(alertIcon);
        headerPanel.add(titleLabel);


        JPanel alertBox = new JPanel(new BorderLayout());
        alertBox.setBackground(new Color(255, 252, 230));
        alertBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 243, 150)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        alertBox.setPreferredSize(new Dimension(180, 30));

        JLabel labelLeft = new JLabel("Needs Attention");
        labelLeft.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JLabel labelRight = new JLabel("2");
        labelRight.setFont(new Font("SansSerif", Font.BOLD, 12));

        alertBox.add(labelLeft, BorderLayout.WEST);
        alertBox.add(labelRight, BorderLayout.EAST);


        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(alertBox);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }



    private JPanel createCategoryPanel() {

        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel catIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/categories_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            catIcon.setIcon(icon);
        } catch (Exception e) {

            catIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Category Breakdown");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(catIcon);
        headerPanel.add(titleLabel);


        JPanel content = new JPanel(new GridLayout(5, 1, 0, 8));
        content.setOpaque(false);

        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        String[] cats = {"Infrastructure", "Sanitation", "Environmental", "Public Safety", "Health Services"};
        for(String c : cats) {
            content.add(createLabeledProgress(c, 20, purple, "1 (20%)"));
        }


        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }



    private JPanel createActivityPanel() {

        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(10, 10));


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel activityIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/recentActivity_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            activityIcon.setIcon(icon);
        } catch (Exception e) {
            activityIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(activityIcon);
        headerPanel.add(titleLabel);


        String[][] activityData = {
                {"Response added to fb_1", "Admin Juan", "2m ago"},
                {"New feedback submitted", "Maria Clara", "15m ago"},
                {"New health center info", "Dr. Jose", "1h ago"},
                {"Response added to fb_2", "Admin Juan", "3h ago"},
                {"System update", "System", "5h ago"},
                {"New user registered", "Liza Sober", "6h ago"}
        };

        DefaultListModel<String[]> model = new DefaultListModel<>();
        for (String[] row : activityData) model.addElement(row);

        JList<String[]> list = new JList<>(model);
        list.setFixedCellHeight(55);


        list.setCellRenderer(new ListCellRenderer<String[]>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));


                JLabel bulletLabel = new JLabel(new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(purple);
                        g2.fillOval(x, y + 6, 8, 8);
                        g2.dispose();
                    }
                    @Override public int getIconWidth() { return 12; }
                    @Override public int getIconHeight() { return 20; }
                });

                JPanel textContainer = new JPanel(new GridLayout(2, 1, 0, 2));
                textContainer.setOpaque(false);

                JLabel actionLabel = new JLabel(value[0]);
                actionLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

                JLabel infoLabel = new JLabel("by " + value[1] + "  •  " + value[2]);
                infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                infoLabel.setForeground(Color.GRAY);

                textContainer.add(actionLabel);
                textContainer.add(infoLabel);

                itemPanel.add(bulletLabel, BorderLayout.WEST);
                itemPanel.add(textContainer, BorderLayout.CENTER);

                if (isSelected) {
                    itemPanel.setBackground(new Color(245, 245, 255));
                } else {
                    itemPanel.setBackground(Color.WHITE);
                }

                return itemPanel;
            }
        });


        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(300, 180));


        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }





    private JPanel createWeeklyActivityPanel() {
        JPanel panel = createBasePanel("");
        panel.setLayout(new BorderLayout(0, 15));


        JPanel northContainer = new JPanel(new BorderLayout(0, 15));
        northContainer.setOpaque(false);


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JLabel calendarIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("res/assets/7daysactivity_icon.png")
                    .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            calendarIcon.setIcon(icon);
        } catch (Exception e) {
            calendarIcon.setPreferredSize(new Dimension(25, 25));
        }

        JLabel titleLabel = new JLabel("Last 7 Days Activity");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(calendarIcon);
        headerPanel.add(titleLabel);


        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);
        statsRow.add(createStatCard("New Feedbacks", "24", new Color(70, 130, 180)));
        statsRow.add(createStatCard("Responses Given", "18", new Color(60, 179, 113)));
        statsRow.add(createStatCard("Issues Resolved", "5", new Color(220, 20, 60)));
        statsRow.add(createStatCard("Total Upvotes", "142", new Color(255, 165, 0)));


        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(statsRow, BorderLayout.CENTER);

        panel.add(northContainer, BorderLayout.NORTH);


        JPanel contentPlaceholder = new JPanel();
        contentPlaceholder.setOpaque(false);
        panel.add(contentPlaceholder, BorderLayout.CENTER);

        return panel;
    }





    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.SOUTH);

        return card;
    }





    private Icon createBulletIcon(Color color) {
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x + 5, y + 14, 6, 6);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 30; }
        };
    }



    private JPanel createLabeledProgress(String label, int val, Color col, String text) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label); l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel r = new JLabel(text); r.setFont(new Font("SansSerif", Font.BOLD, 11));
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(val);
        bar.setForeground(col);
        bar.setPreferredSize(new Dimension(100, 6));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(l, BorderLayout.WEST);
        top.add(r, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }



    private JPanel createBasePanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 14));
        p.add(t, BorderLayout.NORTH);
        return p;
    }





    static class InfoCard extends JPanel {
        public InfoCard(String title, String val, String sub, Color c, String iconPath) {
            setLayout(new BorderLayout(15, 0)); // 15px gap sa pagitan ng icon at text
            setBackground(c);
            setPreferredSize(new Dimension(240, 130));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


            JLabel iconLabel = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(new ImageIcon(iconPath)
                        .getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
                iconLabel.setIcon(icon);
            } catch (Exception e) {
                iconLabel.setPreferredSize(new Dimension(35, 35));
            }
            iconLabel.setVerticalAlignment(SwingConstants.TOP); // Icon sa itaas na part


            JPanel textStack = new JPanel();
            textStack.setOpaque(false);
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

            JLabel t = new JLabel(title);
            t.setForeground(new Color(255, 255, 255, 200));
            t.setFont(new Font("SansSerif", Font.BOLD, 19));

            JLabel v = new JLabel(val);
            v.setForeground(Color.WHITE);

            v.setFont(new Font("SansSerif", Font.BOLD, 28));

            JLabel s = new JLabel(sub);
            s.setForeground(new Color(255, 255, 255, 160));
            s.setFont(new Font("SansSerif", Font.BOLD, 15));

            textStack.add(t);
            textStack.add(Box.createVerticalStrut(5));
            textStack.add(v);
            textStack.add(Box.createVerticalGlue());
            textStack.add(s);

            add(iconLabel, BorderLayout.WEST);
            add(textStack, BorderLayout.CENTER);
        }



        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SummaryAdminDashboard().setVisible(true));
    }
}