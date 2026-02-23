package client.view.common;

import client.view.auth.AuthScreen;
import client.view.strings.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static client.view.strings.Fonts.Inter;

public class LogoutPopUp extends JDialog {
    public LogoutPopUp(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);

        setSize(350, 180);
        setLocationRelativeTo(parent);

        CustomPanel container = new CustomPanel(25);
        container.setLayout(new BorderLayout(0, 15));
        container.setBorder(new EmptyBorder(25, 25, 25, 25));
        container.setStroke(Colors.LIGHTEST_GRAY, 1f);
        container.setOffsetShadow(new Color(0, 0, 0, 30), 4);

        JLabel message = new JLabel("Are you sure you want to logout?");
        message.setFont(new Font("Inter", Font.BOLD, 14));
        message.setForeground(Colors.NORMAL_TEXT);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        Buttons yesBtn = new Buttons("Logout", 15, Colors.LIGHTER_RED, Colors.WHITE, Colors.LIGHT_RED, Inter(Font.BOLD, 12));
        yesBtn.setPreferredSize(new Dimension(110, 35));
        Buttons noBtn = new Buttons("Cancel", 15, Colors.LIGHTER_GRAY, Colors.NORMAL_TEXT, Colors.LIGHT_GRAY, Inter(Font.BOLD, 12));
        noBtn.setPreferredSize(new Dimension(110, 35));

        Buttons.applyClick(yesBtn, () -> {
            // 1. Close Logout Dialog
            dispose();

            // Close the dashboard
            if (parent != null) {
                parent.dispose();
            }
            // Open Login Screen
            client.model.ClientConnection connection = new client.model.ClientConnection();
            new AuthScreen(connection).setVisible(true);
        });

        Buttons.applyClick(noBtn, this::dispose);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(noBtn);
        buttonPanel.add(yesBtn);

        container.add(message, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        add(container);
    }
}