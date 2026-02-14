package client.view.auth;

import client.controller.LoginController;
import client.model.ClientConnection;
import client.utils.CredentialManager;
import client.view.common.button.CloseButton;
import client.view.common.panel.DraggablePanel;
import client.view.common.field.IconInputField;
import client.view.common.button.Button;
import client.view.strings.Colors;
import client.view.strings.Fonts;
import client.view.strings.Images;
import common.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static common.Config.CONFIG_DEBUG;

public class Login extends JFrame {
    private final CardLayout formLayout;
    private final JPanel formPanel;

    public Login(ClientConnection connection) {
        setUndecorated(true);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(Images.ICON.getImage());

        LoginController controller = new LoginController(connection);
        setTitle("Login UI");
        DraggablePanel root = new DraggablePanel();
        root.setLayout(new GridBagLayout());
        root.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        // Close button
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.insets = new Insets(10, 0, 0, 10);
        root.add(new CloseButton(this), gbc);

        // Left panel logo
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.4;
        gbc.weighty = 1;
        root.add(new PanelCover(), gbc);

        // Right panel card
        formLayout = new CardLayout();
        formPanel = new JPanel(formLayout);

        LoginForm loginForm = new LoginForm(controller, connection);
        RegisterInfoPanel registerInfo = new RegisterInfoPanel();

        formPanel.add(loginForm, "login");
        formPanel.add(registerInfo, "register");

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(formPanel, gbc);

        add(root);
    }

    public void showRegisterPanel() { formLayout.show(formPanel, "register"); }
    public void showLoginPanel() { formLayout.show(formPanel, "login"); }

    public static class PanelCover extends JPanel {
        public PanelCover() {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 10, 5, 10);


            JLabel logo = new JLabel(Images.scaledLogo(300, 300));
            c.gridy = 0;
            c.insets= new Insets(-5,0,25,0);
            add(logo, c);


            JLabel title = new JLabel("Welcome to Community Feedback!");
            title.setFont(Fonts.SANS_TITLE);
            title.setForeground(Colors.WHITE);

            c.gridy = 1;
            c.insets= new Insets(-150,60,0,60);
            add(title, c);


            JLabel desc = new JLabel("Keep connected with us");
            desc.setFont(Fonts.SANS_SUB_TITLE);
            desc.setForeground(Colors.WHITE);

            c.gridy = 2;
            c.insets = new Insets(-100, 0, 0, 0);
            add(desc, c);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gra = new GradientPaint(0, 0, Colors.PRIMARY, 0, getHeight(), Colors.SECONDARY);
            g2.setPaint(gra);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static class LoginForm extends JPanel {
        public LoginForm(LoginController controller, ClientConnection connection) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(40, 50, 50, 50));
            add(Box.createVerticalStrut(200));

            JLabel title = new JLabel("Sign In");
            title.setFont(Fonts.SANS_TITLE);
            title.setForeground(Colors.PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(title);
            add(Box.createVerticalStrut(30));

            // Inputs
            IconInputField username = new IconInputField(Images.mediumPerson(), "Username", false);
            IconInputField password = new IconInputField(Images.mediumLock(), "Password", true);

            add(username);
            add(Box.createVerticalStrut(15));
            add(password);
            add(Box.createVerticalStrut(10));

            JPanel rememberMePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            rememberMePanel.setBackground(Colors.WHITE);
            rememberMePanel.setMaximumSize(new Dimension(400, 30));

            JCheckBox rememberMe = new JCheckBox("Remember me");
            rememberMe.setOpaque(false);
            rememberMe.setFont(Fonts.SansSerif(0, 14));
            rememberMe.setForeground(Colors.SUB_TEXT);
            rememberMe.setFocusPainted(false);
            rememberMe.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rememberMePanel.add(rememberMe);
            add(rememberMePanel);

            add(Box.createVerticalStrut(15));

            Button btnLogin = new Button("SIGN IN", 30, Colors.PRIMARY, Colors.WHITE, Colors.SECONDARY);
            btnLogin.setMaximumSize(new Dimension(250, 45));
            btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

            Button.applyClick(btnLogin, () -> {
                Object[] result = controller.handleLogin(username.getText(), password.getText());

                boolean success = Integer.parseInt((String) result[0]) == 200;
                User loggedInUser = (User) result[2];

                if (success && loggedInUser != null) {
                    if (rememberMe.isSelected()) {
                        CredentialManager.saveCredentials(username.getText(), password.getText());
                    } else {
                        CredentialManager.deleteCredentials();
                    }
                    SwingUtilities.getWindowAncestor(this).dispose();
                    new client.view.residents.ResidentDashboard(true, loggedInUser, connection);
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed: " + result[1], "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            add(btnLogin);
            add(Box.createVerticalStrut(5));

            JLabel goRegister = new JLabel("How to Register");
            goRegister.setFont(Fonts.SANS_NORMAL_TEXT);
            goRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

            Button.applyLinkHover(goRegister, Colors.TERTIARY, Colors.PRIMARY);

            Button.applyClick(goRegister, () -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(LoginForm.this);
                if (topFrame instanceof Login loginFrame) {
                    loginFrame.showRegisterPanel();
                }
            });

            add(goRegister);
            add(Box.createVerticalGlue());

        }
    }

    public static class RegisterInfoPanel extends JPanel {
        public RegisterInfoPanel() {
            setBackground(Color.WHITE);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(50, 50, 50, 50));

            JLabel title = new JLabel("How to Register");
            title.setFont(Fonts.SANS_TITLE);
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

            info.setFont(Fonts.SansSerif(0, 18));
            info.setAlignmentX(CENTER_ALIGNMENT);
            add(info);
            add(Box.createVerticalStrut(30));

            Button backBtn = new Button("Back To Login", 30, Colors.SECONDARY, Colors.WHITE, Colors.TERTIARY);
            backBtn.setAlignmentX(CENTER_ALIGNMENT);
            Button.applyClick(backBtn, () -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (topFrame instanceof Login loginFrame) {
                    loginFrame.showLoginPanel();
                }
            });

            add(backBtn);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientConnection testConnection = new ClientConnection();
            if (CONFIG_DEBUG) System.out.println("[DEBUG MODE] Connecting to localhost...");
            new Login(testConnection).setVisible(true);
        });
    }
}
