package client.view.auth;

import client.view.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterInfoPanel extends JPanel {
    public RegisterInfoPanel() {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel("How to Register");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(20));

        JLabel info = new JLabel(
                "<html>" +
                        "<ol>" +
                        "<li>Go to your local barangay office.</li>" +
                        "<li>Ask to be registered in the system.</li>" +
                        "<li>Present a valid government-issued ID.</li>" +
                        "<li>The official/IT staff will register your account.</li>" +
                        "</ol>" +
                        "</html>"
        );

        info.setFont(new Font("SansSerif", Font.PLAIN, 18));
        info.setAlignmentX(CENTER_ALIGNMENT);
        add(info);
        add(Box.createVerticalStrut(30));

        RoundedButton backBtn = new RoundedButton("Back to Login", 30);
        backBtn.setAlignmentX(CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof NewLogin loginFrame) {
                loginFrame.showLoginPanel();
            }
        });
        add(backBtn);
    }
}
