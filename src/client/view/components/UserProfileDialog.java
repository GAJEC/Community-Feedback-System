package client.view.components;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.awt.geom.RoundRectangle2D;


public class UserProfileDialog extends JDialog {

    private JButton createCloseButton() {
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeBtn.setForeground(new Color(230, 230, 230));
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
                closeBtn.setForeground(new Color(230, 230, 230));
            }
        });

        return closeBtn;
    }

    private ImageIcon getScaledIcon(String path, int width, int height) {
        // This ensures it looks in the classpath resources
        URL imgURL = getClass().getResource(path);
        if (imgURL == null) {
            System.err.println("Couldn't find file: " + path);
            return null;
        }

        ImageIcon srcIcon = new ImageIcon(imgURL);
        Image srcImg = srcIcon.getImage();
        Image scaledImg = srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    public UserProfileDialog(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setSize(420, 600);
        int arc = 30;
        setShape(new RoundRectangle2D.Double(
                0, 0,
                getWidth(),
                getHeight(),
                arc, arc
        ));
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createContent(), BorderLayout.CENTER);

        add(mainPanel);
    }


    // HEADER
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(420, 150));

        header.setBackground(new Color(0, 120, 120));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("User Profile");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton closeBtn = createCloseButton();


        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(closeBtn, BorderLayout.EAST);

        header.add(top, BorderLayout.NORTH);
        header.add(createProfileInfo(), BorderLayout.CENTER);

        return header;
    }

    private JPanel createProfileInfo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        panel.setOpaque(false);

        ImageIcon profileIcon = getScaledIcon("/assets/profile_icon.png", 60, 60);
        JLabel icon = new JLabel(profileIcon);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Alan Glen Escobar");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel id = new JLabel("ID: user_001");
        id.setForeground(new Color(220, 220, 255));
        id.setFont(new Font("SansSerif", Font.PLAIN, 14));

        info.add(name);
        info.add(id);

        panel.add(icon);
        panel.add(info);

        return panel;
    }

    // CONTENT
    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 25, 15, 25));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createInfoRow("/assets/email_icon.png", "Email Address", "alan.escobarney@email.com"));
        content.add(Box.createVerticalStrut(15));
        content.add(createInfoRow("/assets/phone_icon.png", "Phone Number", "+63 912 345 6789"));
        content.add(Box.createVerticalStrut(15));
        content.add(createInfoRow("/assets/location_icon.png", "Barangay", "Luna Burgos"));


        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setForeground(new Color(230, 230, 230));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(line);

        content.add(Box.createVerticalStrut(10));

        JLabel note = new JLabel(
                "<html><b>Note:</b> This is a demonstration system. " +
                        "In a production environment, you would be able to edit your profile information here.</html>"
        );
        note.setFont(new Font("SansSerif", Font.PLAIN, 13));
        note.setForeground(Color.DARK_GRAY);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(note);

        content.add(Box.createVerticalGlue());

        return content;
    }

    private JPanel createInfoRow(String iconPath, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        row.setOpaque(false);

        ImageIcon icons = getScaledIcon(iconPath, 28, 28);
        JLabel icon = new JLabel(icons);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.PLAIN, 14));
        val.setForeground(Color.GRAY);

        text.add(lbl);
        text.add(val);

        row.add(icon);
        row.add(text);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

}

