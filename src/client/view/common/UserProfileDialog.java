package client.view.common;

import client.view.UiPreview;
import client.view.strings.Colors;
import client.view.strings.Images;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static client.view.strings.Colors.*;
import static client.view.strings.Fonts.SansSerif;
import static client.view.strings.Images.*;

public class UserProfileDialog extends JDialog {
    public UserProfileDialog(JFrame parent) {
        super(parent, true);

        int radius = 25;
        CustomPanel mainPanel = new CustomPanel(radius);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(0, 0, 3, 3));
        mainPanel.setBackground(TRANSPARENT);
        mainPanel.setOffsetShadow(new Color(0,0,0,30), 4);
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createContent(), BorderLayout.CENTER);

        setContentPane(mainPanel);

        setUndecorated(true);
        pack();
        setSize(420, getPreferredSize().height+35);
        setLocationRelativeTo(parent);
        setBackground(TRANSPARENT);
    }

    // HEADER
    private JPanel createHeader() {
        CustomPanel header = new CustomPanel(25, SECONDARY);
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(420, 150));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setRoundedCorners(true, true, false, false);

        JLabel title = new JLabel("User Profile"); title.setForeground(WHITE);
        title.setFont(SansSerif(1, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(new Buttons.CloseButton(this, WHITE), BorderLayout.EAST);

        header.add(top, BorderLayout.NORTH);
        header.add(createProfileInfo(), BorderLayout.CENTER);
        return header;
    }

    private JPanel createProfileInfo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        panel.setOpaque(false);

        ImageIcon profileIcon = Images.scaled(PROFILE, 60, 60);
        JLabel icon = new JLabel(profileIcon);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Alan Glen Escobar");
        name.setForeground(WHITE);
        name.setFont(SansSerif(1, 18));

        JLabel id = new JLabel("ID: user_001");
        id.setForeground(LIGHTEST_GRAY);
        id.setFont(SansSerif(0, 14));

        info.add(name);
        info.add(id);
        panel.add(icon);
        panel.add(info);
        return panel;
    }

    // CONTENT
    private JPanel createContent() {
        CustomPanel content = new CustomPanel(25, WHITE);
        content.setBorder(new EmptyBorder(25, 25, 15, 25));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        InfoRow email = new InfoRow(Images.scaled(EMAIL,28,28), "Email Address", "alan.escobarney@email.com");
        InfoRow phone = new InfoRow(Images.scaled(PHONE,28,28), "Phone Number", "+63 912 345 6789");
        InfoRow location = new InfoRow(Images.scaled(LOCATION,28,28), "Barangay", "Luna Burgos");

        content.add(email);
        content.add(Box.createVerticalStrut(15));
        content.add(phone);
        content.add(Box.createVerticalStrut(15));
        content.add(location);

        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setForeground(Colors.LIGHT_GRAY);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(Box.createVerticalStrut(15));
        content.add(line);
        content.add(Box.createVerticalStrut(10));

        JLabel note = new JLabel(
                "<html><b>Note:</b> This is a demonstration system. " +
                        "In a production environment, you would be able to edit your profile information here.</html>"
        );
        note.setFont(SansSerif(0, 13));
        note.setForeground(Colors.BLACK);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(note);

        return content;
    }

    public static void main(String[] args) {
        UiPreview.show(UserProfileDialog::new);
    }
}