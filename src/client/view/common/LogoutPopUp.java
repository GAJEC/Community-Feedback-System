package client.view.common;

import client.view.UiPreview;
import client.view.auth.Login;
import client.view.common.button.Button;
import client.view.common.panel.RoundedPanel;
import client.view.strings.Colors;
import client.view.strings.Fonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LogoutPopUp extends JDialog {
    public LogoutPopUp(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);

        setSize(350, 180);
        setLocationRelativeTo(parent);

        RoundedPanel container = new RoundedPanel(30, Colors.WHITE);
        container.setLayout(new BorderLayout(0, 15));
        container.setBorder(new EmptyBorder(25, 25, 25, 25));
        container.setStroke(Colors.LIGHTEST_GRAY, 1.5f);

        JLabel message = new JLabel("Are you sure you want to logout?");
        message.setFont(new Font("Inter", Font.BOLD, 14));
        message.setForeground(Colors.NORMAL_TEXT);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        Button yesBtn = new Button("Logout", 15, Colors.LIGHT_RED, Colors.WHITE, Colors.RED, Fonts.INTER_HEADING_4);
        yesBtn.setPreferredSize(new Dimension(110, 35));
        Button noBtn = new Button("Cancel", 15, Colors.LIGHTER_GRAY, Colors.NORMAL_TEXT, Colors.LIGHT_GRAY, Fonts.INTER_HEADING_4);
        noBtn.setPreferredSize(new Dimension(110, 35));

        Button.applyClick(yesBtn, () -> {
            // 1. Close Logout Dialog
            dispose();

            // Close the dashboard
            if (parent != null) {
                parent.dispose();
            }
            // Open Login Screen
            client.model.ClientConnection connection = new client.model.ClientConnection();
            new Login(connection).setVisible(true);
        });

        Button.applyClick(noBtn, this::dispose);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(noBtn);
        buttonPanel.add(yesBtn);

        container.add(message, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        add(container);
    }

    public static void main(String[] args) {
        UiPreview.show(LogoutPopUp::new);
    }
}