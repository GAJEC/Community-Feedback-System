package client.view.auth;

import client.controller.LoginController;
import client.model.ClientConnection;
import client.model.CredentialManager;
import client.view.admin.AdminDashboard;
import client.view.brgyofficials.BrgyOfficialDashboard;
import client.view.residents.ResidentDashboard;
import common.User;

import javax.swing.*;
import java.awt.*;

import static common.Config.CONFIG_DEBUG;

public class Loading extends JWindow {
    private float alpha = 0f;
    private Image logoImage;
    private boolean fadingOut = false;
    private ClientConnection connection;

    public Loading(ClientConnection connection) {
        this.connection = connection;
        setBackground(new Color(0, 0, 0, 0));
        setSize(600, 600);
        setLocationRelativeTo(null);

        java.net.URL imgURL = getClass().getResource("/assets/logo_icon.png");
        if (imgURL != null) {
            Image originalImage = new ImageIcon(imgURL).getImage();
            logoImage = originalImage.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        }

        // fade in at fade out timer
        Timer timer = new Timer(16, e -> {
            if (!fadingOut) {
                alpha += 0.03f;
                if (alpha > 1.0f) alpha = 1.0f;
            } else {
                alpha -= 0.05f;
                if (alpha < 0.0f) alpha = 0.0f;
            }
            repaint();
        });
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // High-quality rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Apply Alpha Composite for the fade effect
        float safeAlpha = Math.max(0f, Math.min(1f, alpha));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, safeAlpha));

        // Draw the Logo exactly in the center of the transparent window
        if (logoImage != null) {
            int imgW = 500;
            int imgH = 500;
            int x = (getWidth() - imgW) / 2;
            int y = (getHeight() - imgH) / 2;
            g2.drawImage(logoImage, x, y, imgW, imgH, null);
        }

        g2.dispose();
    }

    public void launch() {
        setVisible(true);
        new InitializationTask().execute();
    }

    private class InitializationTask extends SwingWorker<Object[], Void> {
        @Override
        protected Object[] doInBackground() throws Exception {
            String[] credentials = CredentialManager.loadCredentials();
            User loggedInUser = null;

            if (credentials != null && credentials.length == 2) {
                try {
                    LoginController controller = new LoginController(connection);
                    Object[] loginResult = controller.handleLogin(credentials[0], credentials[1]);

                    if (loginResult != null && loginResult.length >= 3) {
                        String statusCode = (String) loginResult[0];
                        if ("200".equals(statusCode) && loginResult[2] instanceof User) {
                            loggedInUser = (User) loginResult[2];
                            if (CONFIG_DEBUG) {
                                System.out.println("[AUTO-LOGIN] Successfully logged in as: " + loggedInUser.getUsername());
                            }
                        } else {
                            if (CONFIG_DEBUG) {
                                System.out.println("[AUTO-LOGIN] Failed: " + loginResult[1]);
                            }
                            CredentialManager.deleteCredentials();
                            credentials = null;
                        }
                    }
                } catch (Exception e) {
                    if (CONFIG_DEBUG) {
                        System.err.println("[AUTO-LOGIN] Error: " + e.getMessage());
                    }
                    CredentialManager.deleteCredentials();
                    credentials = null;
                }
            }

            Thread.sleep(3200);

            fadingOut = true;
            Thread.sleep(500);

            return new Object[]{credentials, loggedInUser};
        }

        @Override
        protected void done() {
            dispose();
            EventQueue.invokeLater(() -> {
                try {
                    Object[] result = get();
                    User loggedInUser = (User) result[1];

                    if (loggedInUser != null) {
                        if ("PENDING".equalsIgnoreCase(loggedInUser.getVerify_status())) {
                            CredentialManager.deleteCredentials();
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Your account is still pending admin approval.",
                                    "Account Pending",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            new AuthScreen(connection).setVisible(true);
                            return;
                        }

                        int userType = loggedInUser.getAttr_type();

                        switch (userType) {
                            case 0:
                                new AdminDashboard(connection).setVisible(true);
                                break;
                            case 1: // Barangay Official
                                new BrgyOfficialDashboard(loggedInUser, connection).setVisible(true);
                                break;
                            case 2: // Resident
                                new ResidentDashboard(true, loggedInUser, connection);
                                break;
                            default: // Other
                                if (CONFIG_DEBUG) {
                                    System.out.println("[AUTO-LOGIN] Unknown user type: " + userType);
                                }
                                new ResidentDashboard(true, loggedInUser, connection);
                                break;
                        }
                    } else {
                        new AuthScreen(connection).setVisible(true);
                    }
                } catch (Exception e) {
                    if (CONFIG_DEBUG) {
                        e.printStackTrace();
                    }
                    new AuthScreen(connection).setVisible(true);
                }
            });
        }
    }
}
