package client.view.auth;

import client.controller.LoginController;
import client.model.ClientConnection;
import client.model.CredentialManager;
import client.view.admin.AdminDashboard;
import client.view.common.Buttons;
import client.view.common.CustomPanel;
import client.view.common.RoundedInputField;
import client.view.residents.ResidentDashboard;
import client.view.strings.Colors;
import client.view.strings.Images;
import common.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Locale;

import static client.view.strings.Colors.LIGHTEST_GRAY;
import static client.view.strings.Fonts.SansSerif;
import static client.view.strings.Images.*;
import static common.Config.CONFIG_DEBUG;

public class AuthScreen extends JFrame {
    private final CardLayout formLayout;
    private final JPanel formPanel;

    public AuthScreen(ClientConnection connection) {
        setUndecorated(true);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(Images.ICON.getImage());

        LoginController controller = new LoginController(connection);
        setTitle("Authentication");
        CustomPanel root = new CustomPanel();
        root.setDraggable(true);
        root.setLayout(new GridBagLayout());
        root.setBackgroundColor(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.insets = new Insets(10, 0, 0, 10);
        root.add(new Buttons.CloseButton(this), gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.4;
        gbc.weighty = 1;
        root.add(new PanelCover(), gbc);

        formLayout = new CardLayout();
        formPanel = new JPanel(formLayout);

        LoginForm loginForm = new LoginForm(controller, connection);
        RegisterForm registerForm = new RegisterForm(controller);

        formPanel.add(loginForm, "login");
        formPanel.add(registerForm, "register");

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(formPanel, gbc);

        add(root);
    }

    public void showLoginPanel() {
        formLayout.show(formPanel, "login");
    }

    public void showRegisterPanel() {
        formLayout.show(formPanel, "register");
    }

    public static class PanelCover extends JPanel {
        public PanelCover() {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 10, 5, 10);

            JLabel logo = new JLabel(Images.scaledLogo(300, 300));
            c.gridy = 0;
            c.insets = new Insets(-5, 0, 25, 0);
            add(logo, c);

            JLabel title = new JLabel("Welcome to Community Feedback!");
            title.setFont(SansSerif(1, 30));
            title.setForeground(Colors.WHITE);

            c.gridy = 1;
            c.insets = new Insets(-150, 60, 0, 60);
            add(title, c);

            JLabel desc = new JLabel("Keep connected with us");
            desc.setFont(SansSerif(0, 20));
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

    public class LoginForm extends JPanel {
        public LoginForm(LoginController controller, ClientConnection connection) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(40, 50, 50, 50));
            add(Box.createVerticalStrut(200));

            JLabel title = new JLabel("Sign In");
            title.setFont(SansSerif(1, 30));
            title.setForeground(Colors.PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(title);
            add(Box.createVerticalStrut(30));

            RoundedInputField username = new RoundedInputField(Images.scaled(USERNAME, 20, 20), "Username", false);
            username.setStrokeColor(LIGHTEST_GRAY);
            RoundedInputField password = new RoundedInputField(Images.scaled(LOCK, 20, 20), "Password", true);
            password.setStrokeColor(LIGHTEST_GRAY);

            add(username);
            add(Box.createVerticalStrut(15));
            add(password);
            add(Box.createVerticalStrut(10));

            JPanel rememberMePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            rememberMePanel.setBackground(Colors.WHITE);
            rememberMePanel.setMaximumSize(new Dimension(400, 30));

            JCheckBox rememberMe = new JCheckBox("Remember me");
            rememberMe.setOpaque(false);
            rememberMe.setFont(SansSerif(0, 14));
            rememberMe.setForeground(Colors.SUB_TEXT);
            rememberMe.setFocusPainted(false);
            rememberMe.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rememberMePanel.add(rememberMe);
            add(rememberMePanel);

            add(Box.createVerticalStrut(15));

            Buttons btnLogin = new Buttons("SIGN IN", 30, Colors.PRIMARY, Colors.WHITE, Colors.SECONDARY, SansSerif(0, 14));
            btnLogin.setMaximumSize(new Dimension(250, 30));
            btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

            Buttons.applyClick(btnLogin, () -> {
                Object[] result = controller.handleLogin(username.getText(), password.getText());

                boolean success = Integer.parseInt((String) result[0]) == 200;
                User loggedInUser = (User) result[2];

                if (success && loggedInUser != null) {
                    if ("PENDING".equalsIgnoreCase(loggedInUser.getVerify_status())) {
                        CredentialManager.deleteCredentials();
                        JOptionPane.showMessageDialog(
                                this,
                                "Your account is still pending admin approval.",
                                "Account Pending",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    if (rememberMe.isSelected()) {
                        CredentialManager.saveCredentials(username.getText(), password.getText());
                    } else {
                        CredentialManager.deleteCredentials();
                    }
                    SwingUtilities.getWindowAncestor(this).dispose();
                    if (loggedInUser.getAttr_type() == 0) {
                        new AdminDashboard(connection).setVisible(true);
                    } else {
                        new ResidentDashboard(true, loggedInUser, connection);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed: " + result[1], "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            add(btnLogin);
            add(Box.createVerticalStrut(5));

            Buttons goRegister = new Buttons("Register", 30, Colors.TERTIARY, Colors.WHITE, Colors.SECONDARY, SansSerif(0, 14));
            goRegister.setMaximumSize(new Dimension(250, 30));
            goRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

            Buttons.applyClick(goRegister, AuthScreen.this::showRegisterPanel);

            add(Box.createVerticalStrut(5));
            add(goRegister);
        }
    }

    public class RegisterForm extends JPanel {
        public RegisterForm(LoginController controller) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(40, 50, 50, 50));
            add(Box.createVerticalGlue());

            JLabel title = new JLabel("Create Account");
            title.setFont(SansSerif(1, 30));
            title.setForeground(Colors.PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(title);

            JLabel subtitle = new JLabel("Join your community today");
            subtitle.setFont(SansSerif(0, 14));
            subtitle.setForeground(Colors.SUB_TEXT);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(Box.createVerticalStrut(6));
            add(subtitle);
            add(Box.createVerticalStrut(25));

            RoundedInputField fullName = new RoundedInputField(Images.scaled(USERNAME, 20, 20), "Full Name", false);
            fullName.setStrokeColor(LIGHTEST_GRAY);

            RoundedInputField location = new RoundedInputField(Images.scaled(LOCATION, 20, 20), "Location / Address", false);
            location.setStrokeColor(LIGHTEST_GRAY);

            RoundedInputField phone = new RoundedInputField(Images.scaled(PHONE, 20, 20), "Phone", false);
            phone.setStrokeColor(LIGHTEST_GRAY);

            RoundedInputField username = new RoundedInputField(Images.scaled(USERNAME, 20, 20), "Username", false);
            username.setStrokeColor(LIGHTEST_GRAY);

            RoundedInputField password = new RoundedInputField(Images.scaled(LOCK, 20, 20), "Password", true);
            password.setStrokeColor(LIGHTEST_GRAY);

            RoundedInputField confirmPassword = new RoundedInputField(Images.scaled(LOCK, 20, 20), "Confirm Password", true);
            confirmPassword.setStrokeColor(LIGHTEST_GRAY);

            final File[] selectedIdFile = {null};
            JPanel uploadPanel = new JPanel(new BorderLayout(10, 0));
            uploadPanel.setOpaque(false);
            uploadPanel.setMaximumSize(new Dimension(400, 35));

            JButton uploadBtn = new JButton("Upload Valid ID");
            uploadBtn.setFocusPainted(false);
            uploadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            uploadBtn.setBackground(new Color(240, 240, 240));
            uploadBtn.setPreferredSize(new Dimension(140, 35));

            JLabel fileNameLabel = new JLabel("No file chosen");
            fileNameLabel.setForeground(Colors.SUB_TEXT);
            fileNameLabel.setFont(SansSerif(0, 12));

            uploadBtn.addActionListener(e -> {
                FileDialog fileDialog = new FileDialog(AuthScreen.this, "Select Valid ID", FileDialog.LOAD);
                fileDialog.setFile("*.jpg;*.jpeg;*.png");
                fileDialog.setFilenameFilter((dir, name) -> isImageFileName(name));
                fileDialog.setVisible(true);

                String selectedFile = fileDialog.getFile();
                String selectedDirectory = fileDialog.getDirectory();

                if (selectedFile != null && selectedDirectory != null) {
                    File candidate = new File(selectedDirectory, selectedFile);
                    if (!isImageFileName(selectedFile) || !isSupportedImageContent(candidate)) {
                        selectedIdFile[0] = null;
                        fileNameLabel.setText("No file chosen");
                        fileNameLabel.setForeground(Colors.SUB_TEXT);
                        JOptionPane.showMessageDialog(this,
                                "Please select a valid image file (JPG, JPEG, PNG only).",
                                "Invalid File Type",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    selectedIdFile[0] = candidate;
                    fileNameLabel.setText(selectedIdFile[0].getName());
                    fileNameLabel.setForeground(new Color(40, 167, 69));
                }
            });

            uploadPanel.add(uploadBtn, BorderLayout.WEST);
            uploadPanel.add(fileNameLabel, BorderLayout.CENTER);

            add(fullName);
            add(Box.createVerticalStrut(10));
            add(location);
            add(Box.createVerticalStrut(10));
            add(phone);
            add(Box.createVerticalStrut(10));
            add(username);
            add(Box.createVerticalStrut(10));
            add(password);
            add(Box.createVerticalStrut(10));
            add(confirmPassword);
            add(Box.createVerticalStrut(10));
            add(uploadPanel);
            add(Box.createVerticalStrut(18));

            Buttons createAccountBtn = new Buttons("CREATE ACCOUNT", 30, Colors.PRIMARY, Colors.WHITE, Colors.SECONDARY, SansSerif(0, 14));
            createAccountBtn.setMaximumSize(new Dimension(250, 30));
            createAccountBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            Buttons.applyClick(createAccountBtn, () -> {
                if (fullName.getText().trim().isEmpty() ||
                        location.getText().trim().isEmpty() ||
                    phone.getText().trim().isEmpty() ||
                        username.getText().trim().isEmpty() ||
                        password.getText().trim().isEmpty() ||
                        confirmPassword.getText().trim().isEmpty() ||
                        selectedIdFile[0] == null) {
                    JOptionPane.showMessageDialog(this,
                            "Please complete all fields and upload a valid ID.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!password.getText().equals(confirmPassword.getText())) {
                    JOptionPane.showMessageDialog(this,
                            "Password and Confirm Password do not match.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String idBase64 = encodeFileToBase64(selectedIdFile[0]);
                if (idBase64.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Failed to read the selected ID image.",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Object[] result = controller.handleRegister(
                        fullName.getText(),
                        location.getText(),
                        phone.getText(),
                        username.getText(),
                        password.getText(),
                        selectedIdFile[0].getName(),
                        idBase64
                );

                if (result != null && "200".equals(String.valueOf(result[0]))) {
                    JOptionPane.showMessageDialog(this,
                            String.valueOf(result[1]),
                            "Registration Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                    AuthScreen.this.showLoginPanel();
                } else {
                    String msg = (result != null && result.length > 1) ? String.valueOf(result[1]) : "Registration failed.";
                    JOptionPane.showMessageDialog(this,
                            msg,
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            add(createAccountBtn);
            add(Box.createVerticalStrut(8));

            Buttons backToLogin = new Buttons("Back to Sign In", 30, Colors.TERTIARY, Colors.WHITE, Colors.SECONDARY, SansSerif(0, 14));
            backToLogin.setMaximumSize(new Dimension(250, 30));
            backToLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

            Buttons.applyClick(backToLogin, AuthScreen.this::showLoginPanel);

            add(backToLogin);
            add(Box.createVerticalGlue());
        }

        private boolean isImageFileName(String fileName) {
            if (fileName == null || fileName.trim().isEmpty()) {
                return false;
            }
            String lower = fileName.toLowerCase(Locale.ROOT);
            return lower.endsWith(".jpg")
                    || lower.endsWith(".jpeg")
                    || lower.endsWith(".png");
        }

        private boolean isSupportedImageContent(File file) {
            try {
                if (file == null || !file.exists() || !file.isFile()) {
                    return false;
                }
                return ImageIO.read(file) != null;
            } catch (IOException e) {
                return false;
            }
        }

        private String encodeFileToBase64(File file) {
            try {
                if (file == null || !file.exists() || !file.isFile()) {
                    return "";
                }
                byte[] bytes = Files.readAllBytes(file.toPath());
                return Base64.getEncoder().encodeToString(bytes);
            } catch (IOException e) {
                return "";
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientConnection testConnection = new ClientConnection();
            if (CONFIG_DEBUG) {
                System.out.println("[DEBUG MODE] Connecting to localhost...");
            }
            new AuthScreen(testConnection).setVisible(true);
        });
    }
}
