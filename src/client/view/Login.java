package client.view;
import client.controller.LoginController;
import client.model.ClientConnection;
import client.view.components.IconInputField;
import client.view.components.RoundedButton;
import common.User;

import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;


import static common.Config.CONFIG_DEBUG;

public class Login extends JFrame {
    private final LoginController controller;

    public Login(ClientConnection connection) {
        URL iconURL = Login.class.getResource("/assets/app_icon.png");
        if (iconURL == null) {
            iconURL = Login.class.getResource("/assets/app_icon.png");
        }

        if (iconURL != null) {
            this.setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Could not find app_icon.png");
        }
        this.controller = new LoginController(connection);
        setTitle("Login UI");
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);


        final Point[] mouseClickPoint = {null};
        mainPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                mouseClickPoint[0] = e.getPoint();
            }
        });
        mainPanel.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                Point curr = getLocation();
                int x = curr.x + e.getX() - mouseClickPoint[0].x;
                int y = curr.y + e.getY() - mouseClickPoint[0].y;
                setLocation(x, y);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();


        JButton closeBtn = createCloseButton();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.insets = new Insets(10, 0, 0, 10);
        mainPanel.add(closeBtn, gbc);


        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        mainPanel.add(new PanelCover(), gbc);


        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        mainPanel.add(createLoginForm(), gbc);

        add(mainPanel);
    }

    private JButton createCloseButton() {
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeBtn.setForeground(new Color(0, 80, 80)); // Matching your theme
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeBtn.addActionListener(e -> System.exit(0));


        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setForeground(Color.RED);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setForeground(new Color(0, 80, 80));
            }
        });

        return closeBtn;
    }

    private JPanel createLoginForm() {
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(0, 80, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);


        int iconSize = 20;


        Icon usernameIcon = getScaledIcon("/assets/username_icon.png", iconSize, iconSize);
        Icon passwordIcon = getScaledIcon("/assets/password_icon.png", iconSize, iconSize);


        IconInputField username =
                new IconInputField(usernameIcon, "Username", false);

        IconInputField passField =
                new IconInputField(passwordIcon, "Password", true);


        RoundedButton btnLogin = new RoundedButton("SIGN IN", 30); // 30 is the roundness
        Color primaryColor = new Color(0, 80, 80);
        Color hoverColor = new Color(0, 120, 120); // A slightly lighter teal

        btnLogin.setBackground(new Color(0, 80, 80));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(250, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(primaryColor);
            }
        });

        btnLogin.addActionListener(e -> {
            String usernameText = username.getText();
            String passwordText = passField.getText();
            Object[] result = controller.handleLogin(usernameText, passwordText);
            boolean success = (Integer.parseInt((String) result[0]) == 200);
            User loggedInUser = (User) result[2];

            if (success && loggedInUser != null) {
                dispose();
                SwingUtilities.invokeLater(() -> new client.view.residents.ResidentDashboard(true, loggedInUser));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Login failed: " + result[1],
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        form.add(Box.createVerticalGlue());
        form.add(title);
        form.add(Box.createVerticalStrut(30));
        form.add(username);
        form.add(Box.createVerticalStrut(15));
        form.add(passField);
        form.add(Box.createVerticalStrut(20));
        form.add(btnLogin);
        form.add(Box.createVerticalGlue());

        return form;
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


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientConnection testConnection = new ClientConnection();
            if (CONFIG_DEBUG) {
                System.out.println("[DEBUG MODE] Connecting to localhost...");
            }
            new Login(testConnection).setVisible(true);
        });
    }


    class PanelCover extends JPanel {
        public PanelCover() {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 10, 5, 10); // Spacing between elements


            ImageIcon logoIcon = getScaledIcon("/assets/logo_icon.png", 300, 300);
            JLabel logoLabel = new JLabel(logoIcon);

            c.gridy = 0;
            c.insets= new Insets(-10,0,0,0);
            add(logoLabel, c);


            JLabel title = new JLabel("Welcome to Community Feedback!");
            title.setFont(new Font("SansSerif", Font.BOLD, 30));
            title.setForeground(Color.WHITE);

            c.gridy = 1;
            c.insets= new Insets(-150,0,0,0);
            add(title, c);


            JLabel desc = new JLabel("Keep connected with us");
            desc.setFont(new Font("SansSerif", Font.PLAIN, 20));
            desc.setForeground(Color.WHITE);

            c.gridy = 2;
            c.insets = new Insets(-100, 0, 0, 0);
            add(desc, c);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gra = new GradientPaint(
                    0, 0, new Color(0, 80, 80),
                    0, getHeight(), new Color(0, 120, 120)
            );
            g2.setPaint(gra);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
