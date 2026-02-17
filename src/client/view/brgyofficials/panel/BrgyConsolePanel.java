package client.view.brgyofficials.panel;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;


public class BrgyConsolePanel extends JPanel {

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color DARK_NAVY = new Color(18, 22, 33);
    private static final Color PURPLE_BAR = new Color(80, 0, 150);
    private static final Color DELETE_RED = new Color(220, 20, 60);

    private static final Color ROLE_CITIZEN_BG = new Color(224, 242, 241);
    private static final Color ROLE_CITIZEN_TXT = new Color(0, 121, 107);
    private static final Color ROLE_OFFICIAL_BG = new Color(232, 245, 233);
    private static final Color ROLE_OFFICIAL_TXT = new Color(56, 142, 60);

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public BrgyConsolePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(buildTitleSection(), BorderLayout.NORTH);

        RoundedPanel mainCard = new RoundedPanel(20, Color.WHITE);
        mainCard.setLayout(new BorderLayout());

        mainCard.add(buildDarkCommandCenter(), BorderLayout.NORTH);
        mainCard.add(buildTableSection(), BorderLayout.CENTER);

        add(mainCard, BorderLayout.CENTER);
    }

    private JPanel buildTitleSection() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        JLabel title = new JLabel("User Database Console");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Manage user accounts and organization");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        JPanel textGrp = new JPanel(new GridLayout(2, 1));
        textGrp.setOpaque(false);
        textGrp.add(title);
        textGrp.add(subtitle);
        pnl.add(textGrp, BorderLayout.WEST);
        return pnl;
    }

    private JPanel buildDarkCommandCenter() {
        JPanel container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_NAVY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel infoBar = new JPanel(new BorderLayout());
        infoBar.setOpaque(false);
        infoBar.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel breadcrumbs = new JLabel("community_feedback_system • Table: users • Account Records");
        breadcrumbs.setForeground(new Color(150, 160, 175));
        infoBar.add(breadcrumbs, BorderLayout.WEST);

        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(PURPLE_BAR);
        actionBar.setBorder(new EmptyBorder(8, 15, 8, 15));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftGroup.setOpaque(false);
        JLabel sortLabel = new JLabel("SORT BY:");
        sortLabel.setForeground(Color.WHITE);
        sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JButton sortNameBtn = createBadgeButton("Name (A-Z)", new Color(255,255,255,40));
        JButton sortIdBtn = createBadgeButton("User ID", new Color(255,255,255,40));

        sortNameBtn.addActionListener(e -> sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING))));
        sortIdBtn.addActionListener(e -> sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING))));

        leftGroup.add(sortLabel);
        leftGroup.add(sortNameBtn);
        leftGroup.add(sortIdBtn);

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightGroup.setOpaque(false);
        JButton deleteBtn = createBadgeButton("Delete Selected", DELETE_RED);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                model.removeRow(table.convertRowIndexToModel(row));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            }
        });
        rightGroup.add(deleteBtn);

        actionBar.add(leftGroup, BorderLayout.WEST);
        actionBar.add(rightGroup, BorderLayout.EAST);

        container.add(infoBar);
        container.add(actionBar);
        return container;
    }

    private JPanel buildTableSection() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        String[] cols = {"User ID", "Role", "Name", "Phone Number", "Address", "Account Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        model.addRow(new Object[]{"USR_001", "Citizen", "Maria Garcia", "0912345678", "Main Road", "Active"});
        model.addRow(new Object[]{"USR_002", "Brgy Official", "Juan Cruz", "09981122478", "Brgy Hall", "Active"});

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        table.getColumnModel().getColumn(1).setCellRenderer(new RolePillRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        pnl.add(scroll, BorderLayout.CENTER);
        return pnl;
    }

    private JButton createBadgeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class RolePillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isS, boolean hasF, int row, int col) {
            String text = (value != null) ? value.toString() : "";
            JLabel label = new JLabel(text, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color bg = getText().equalsIgnoreCase("Citizen") ? ROLE_CITIZEN_BG : ROLE_OFFICIAL_BG;
                    Color txt = getText().equalsIgnoreCase("Citizen") ? ROLE_CITIZEN_TXT : ROLE_OFFICIAL_TXT;

                    FontMetrics fm = g2.getFontMetrics();
                    int w = fm.stringWidth(getText()) + 24;
                    int h = fm.getHeight() + 8;
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() - h) / 2;

                    g2.setColor(bg);
                    g2.fillRoundRect(x, y, w, h, 20, 20);
                    g2.setColor(txt);
                    g2.drawString(getText(), x + 12, y + fm.getAscent() + 4);
                    g2.dispose();
                }
            };
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setOpaque(false); // Fixes the white box
            return label;
        }
    }

    static class RoundedPanel extends JPanel {
        private int r; private Color c;
        public RoundedPanel(int r, Color c) {
            this.r = r;
            this.c = c;
            setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            g2.dispose();
        }
    }
}