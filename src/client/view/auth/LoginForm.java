package client.view.auth;

import client.controller.LoginController;
import client.view.components.IconInputField;
import client.view.components.RoundedButton;
import client.view.strings.Colors;
import client.view.strings.Images;
import common.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JPanel {

    public LoginForm(LoginController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(40, 50, 50, 50));
        add(Box.createVerticalStrut(200));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(0, 80, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(30));

        // Inputs
        IconInputField username = new IconInputField(
                Images.scaled(Images.class.getResource("/assets/username_icon.png"), 20, 20),
                "Username", false
        );
        IconInputField password = new IconInputField(
                Images.scaled(Images.class.getResource("/assets/password_icon.png"), 20, 20),
                "Password", true
        );

        add(username);
        add(Box.createVerticalStrut(15));
        add(password);
        add(Box.createVerticalStrut(25));

        RoundedButton btnLogin = createButton("SIGN IN", Colors.PRIMARY, Colors.SECONDARY);
        btnLogin.setMaximumSize(new Dimension(250, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addActionListener(e -> {
            Object[] result = controller.handleLogin(username.getText(), password.getText());
            boolean success = Integer.parseInt((String) result[0]) == 200;
            User loggedInUser = (User) result[2];

            if (success && loggedInUser != null) {
                SwingUtilities.getWindowAncestor(this).dispose();
                new client.view.residents.ResidentDashboard(true, loggedInUser);
            } else {
                JOptionPane.showMessageDialog(this, "Login failed: " + result[1], "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(btnLogin);
        add(Box.createVerticalStrut(10));

        RoundedButton goRegister = createButton("HOW TO REGISTER", Colors.SECONDARY, Colors.TERTIARY);
        goRegister.setMaximumSize(new Dimension(250, 45));
        goRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        goRegister.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof NewLogin loginFrame) {
                loginFrame.showRegisterPanel();
            }
        });

        add(goRegister);
        add(Box.createVerticalGlue());
    }

    private RoundedButton createButton(String text, Color primary, Color hover) {
        RoundedButton btn = new RoundedButton(text, 30);
        btn.setBackground(primary);
        btn.setForeground(Color.WHITE);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(primary); }
        });
        return btn;
    }
}
