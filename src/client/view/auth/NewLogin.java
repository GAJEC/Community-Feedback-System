package client.view.auth;

import client.controller.LoginController;
import client.model.ClientConnection;
import client.view.components.CloseButton;
import client.view.components.DraggablePanel;
import client.view.strings.Images;

import javax.swing.*;
import java.awt.*;

import static common.Config.CONFIG_DEBUG;

public class NewLogin extends JFrame {

    private final CardLayout formLayout;
    private final JPanel formPanel;

    public NewLogin(ClientConnection connection) {
        setUndecorated(true);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(Images.ICON.getImage());

        LoginController controller = new LoginController(connection);

        DraggablePanel root = new DraggablePanel();
        root.setLayout(new GridBagLayout());
        root.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        // Close button
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.insets = new Insets(0, 0, 0, 10);
        root.add(new CloseButton(this), gbc);

        // Left panel logo
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridheight = 2; gbc.weightx = 0.4; gbc.weighty = 1;
        root.add(new PanelCover(), gbc);

        // Right panel card
        formLayout = new CardLayout();
        formPanel = new JPanel(formLayout);

        LoginForm loginForm = new LoginForm(controller);
        RegisterInfoPanel registerInfo = new RegisterInfoPanel();

        formPanel.add(loginForm, "login");
        formPanel.add(registerInfo, "register");

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridheight = 1; gbc.weightx = 0.6; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(formPanel, gbc);

        add(root);
    }

    public void showRegisterPanel() { formLayout.show(formPanel, "register"); }
    public void showLoginPanel() { formLayout.show(formPanel, "login"); }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientConnection testConnection = new ClientConnection();
            if (CONFIG_DEBUG) System.out.println("[DEBUG MODE] Connecting to localhost...");
            new NewLogin(testConnection).setVisible(true);
        });
    }
}
