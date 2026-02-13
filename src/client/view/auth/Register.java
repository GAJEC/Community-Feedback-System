package client.view.auth;

import client.view.components.DropShadowBorder;
import client.view.components.RoundedTextField;
import client.view.components.Button;
import client.view.strings.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

public class Register extends JFrame {

    private JTextField nameField, ageField, locationField;
    private JPasswordField passField, confirmPassField;
    private JLabel fileNameLabel;
    private File selectedIdFile;

    private final Dimension FIELD_SIZE = new Dimension(550, 45);

    public Register() {
        setTitle("Community Feedback - Register");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. MAIN CONTAINER (Holds Header + Form) ---
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbcMain = new GridBagConstraints();

        // --- 2. ADD HEADER TO MAIN CONTAINER ---
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.anchor = GridBagConstraints.NORTH;
        gbcMain.insets = new Insets(0, 0, 40, 0);

        mainContainer.add(new RegistrationHeaderPanel(), gbcMain);

        // --- 3. BUILD FORM CARD ---
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new DropShadowBorder(10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        int sidePadding = 60;

        // Form Title
        JLabel title = new JLabel("Create an Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(33, 37, 41));

        JLabel subtitle = new JLabel("Join your community today", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        gbc.gridy = 0; gbc.insets = new Insets(35, sidePadding, 5, sidePadding);
        formCard.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, sidePadding, 30, sidePadding);
        formCard.add(subtitle, gbc);

        // --- FIELDS ---

        // Full Name
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(createLabel("Full Name"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        nameField = new RoundedTextField(15);
        nameField.setPreferredSize(FIELD_SIZE);
        formCard.add(nameField, gbc);

        // Age
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(createLabel("Age"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        ageField = new RoundedTextField(15);
        ageField.setPreferredSize(FIELD_SIZE);
        formCard.add(ageField, gbc);

        // Location
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(createLabel("Location / Address"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        locationField = new RoundedTextField(15);
        locationField.setPreferredSize(FIELD_SIZE);
        formCard.add(locationField, gbc);

        // Password
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(createLabel("Password"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        passField = new RoundedPasswordField(15);
        passField.setPreferredSize(FIELD_SIZE);
        formCard.add(passField, gbc);

        // Confirm Password
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(createLabel("Confirm Password"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        confirmPassField = new RoundedPasswordField(15);
        confirmPassField.setPreferredSize(FIELD_SIZE);
        formCard.add(confirmPassField, gbc);

        // Upload ID
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(createLabel("Upload Valid ID"), gbc);

        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 25, sidePadding);
        JPanel uploadPanel = new JPanel(new BorderLayout(10, 0));
        uploadPanel.setOpaque(false);

        JButton uploadBtn = new JButton("Choose File");
        uploadBtn.setBackground(new Color(230, 230, 230));
        uploadBtn.setFocusPainted(false);
        uploadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        uploadBtn.setPreferredSize(new Dimension(100, 40));
        uploadBtn.addActionListener(e -> chooseFile());

        fileNameLabel = new JLabel("No file chosen");
        fileNameLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        fileNameLabel.setForeground(Color.GRAY);

        uploadPanel.add(uploadBtn, BorderLayout.WEST);
        uploadPanel.add(fileNameLabel, BorderLayout.CENTER);
        formCard.add(uploadPanel, gbc);

        // Submit Button (With Hover)
        gbc.gridy++; gbc.insets = new Insets(10, sidePadding, 10, sidePadding);
        Button submitBtn = new Button("Create Account", 15);
        submitBtn.setPreferredSize(new Dimension(FIELD_SIZE.width, 50));
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        Color defaultColor = new Color(33, 37, 41);
        Color hoverColor = new Color(70, 75, 80);

        submitBtn.setBackground(defaultColor);
        submitBtn.setForeground(Color.WHITE);

        submitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitBtn.setBackground(hoverColor);
                submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                submitBtn.setBackground(defaultColor);
            }
        });

        formCard.add(submitBtn, gbc);

        // Login Link
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 35, sidePadding);
        JLabel loginLink = new JLabel("<html>Already have an account? <font color='#007bff'>Login here</font></html>");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(loginLink, gbc);

        // --- 4. ADD FORM CARD TO MAIN CONTAINER ---
        gbcMain.gridx = 0;
        gbcMain.gridy = 1;
        gbcMain.weightx = 0;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 50, 0);

        mainContainer.add(formCard, gbcMain);

        // --- 5. SCROLL PANE SETUP ---
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates a label with a red asterisk to indicate a required field.
     */
    private JLabel createLabel(String text) {
        // We use HTML to color just the asterisk red
        JLabel lbl = new JLabel("<html>" + text + " <font color='red'>*</font></html>");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedIdFile = fileChooser.getSelectedFile();
            fileNameLabel.setText(selectedIdFile.getName());
            fileNameLabel.setForeground(new Color(40, 167, 69));
        }
    }

    class RoundedPasswordField extends JPasswordField {
        private int radius;
        public RoundedPasswordField(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Register().setVisible(true));
    }
}

// --- HEADER PANEL CLASS ---
class RegistrationHeaderPanel extends JPanel {
    private static final Color HEADER_COLOR = Colors.PRIMARY;

    public RegistrationHeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(HEADER_COLOR);
        setPreferredSize(new Dimension(1000, 100)); // Fixed height

        buildHeader();
    }

    private void buildHeader() {
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        contentPanel.setOpaque(false);

        // 1. LOGO SECTION
        try {
            URL logoURL = getClass().getResource("/assets/logo_icon.png");
            if (logoURL != null) {
                Image logo = new ImageIcon(logoURL).getImage().getScaledInstance(90, 80, Image.SCALE_SMOOTH);
                JLabel logoLbl = new JLabel(new ImageIcon(logo));
                logoLbl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                contentPanel.add(logoLbl);
            }
        } catch (Exception ignored) {
            System.out.println("Logo not found");
        }

        // 2. TEXT SECTION
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel title = new JLabel("Community Feedback System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Your Voice. Our Community. Real Change");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(255, 255, 255, 180));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(tagline);

        contentPanel.add(textPanel);
        add(contentPanel, BorderLayout.WEST);
    }
}