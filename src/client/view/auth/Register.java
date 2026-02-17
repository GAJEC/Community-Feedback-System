package client.view.auth;

import client.model.ClientConnection;
import client.view.common.CustomPanel;
import client.view.common.InfoRow;
import client.view.common.RoundedInputField;
import client.view.strings.Colors;
import client.view.common.Buttons;
import client.view.strings.Images;

import javax.swing.*;
import javax.swing.text.Style;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


public class Register extends JFrame {
    private ClientConnection connection; // Store the connection here
    private RoundedInputField nameField, ageField, locationField, passField, confirmPassField;
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
        CustomPanel formCard = new CustomPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBackgroundColor(Color.WHITE);
        formCard.setRadius(20);
        formCard.setOffsetShadow(new Color(0, 0, 0, 30), 4);
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
        formCard.add(InfoRow.createLabel("Full Name"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        nameField = new RoundedInputField(null, "John", false);
        nameField.setPreferredSize(FIELD_SIZE);
        formCard.add(nameField, gbc);

        // Age
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(InfoRow.createLabel("Age"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        ageField = new RoundedInputField(null, "18", false);
        ageField.setPreferredSize(FIELD_SIZE);
        formCard.add(ageField, gbc);

        // Location
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(InfoRow.createLabel("Location / Address"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        locationField = new RoundedInputField(null, "Baguio", false);
        locationField.setPreferredSize(FIELD_SIZE);
        formCard.add(locationField, gbc);

        // Password
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(InfoRow.createLabel("Password"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        passField = new RoundedInputField(null, "", true);
        passField.setPreferredSize(FIELD_SIZE);
        formCard.add(passField, gbc);

        // Confirm Password
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(InfoRow.createLabel("Confirm Password"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 15, sidePadding);
        confirmPassField = new RoundedInputField(null, "", true);
        confirmPassField.setPreferredSize(FIELD_SIZE);
        formCard.add(confirmPassField, gbc);

        // Upload ID
        gbc.gridy++; gbc.insets = new Insets(0, sidePadding, 5, sidePadding);
        formCard.add(InfoRow.createLabel("Upload Valid ID"), gbc);

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
        Buttons submitBtn = new Buttons("Create Account",15);
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

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginRow.setOpaque(false);

        JLabel promptText = new JLabel("Already have an account?");
        promptText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        promptText.setForeground(new Color(80, 80, 80)); // Subtle gray

        JLabel loginLink = new JLabel("Login here");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));

        Buttons.applyLinkHover(loginLink, new Color(0, 123, 255), new Color(0, 86, 179));

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Window regFrame = SwingUtilities.getWindowAncestor(loginLink);
                new Login(connection).setVisible(true);
                if (regFrame != null) {
                    regFrame.dispose();
                }
            }
        });

        loginRow.add(promptText);
        loginRow.add(loginLink);

        gbc.gridy++;
        gbc.insets = new Insets(0, sidePadding, 35, sidePadding);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formCard.add(loginRow, gbc);

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

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedIdFile = fileChooser.getSelectedFile();
            fileNameLabel.setText(selectedIdFile.getName());
            fileNameLabel.setForeground(new Color(40, 167, 69));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Register().setVisible(true));
    }

    // --- HEADER PANEL CLASS ---
    static class RegistrationHeaderPanel extends JPanel {
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
            JLabel logoLbl = new JLabel(Images.scaledLogo(90, 90));
            logoLbl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            contentPanel.add(logoLbl);

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
}

