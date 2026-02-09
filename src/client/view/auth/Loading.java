package client.view.auth;

import client.model.ClientConnection;

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

    private class InitializationTask extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            Thread.sleep(3200);

            fadingOut = true;
            Thread.sleep(500); // Wait for fade-out to complete
            return null;
        }

        @Override
        protected void done() {
            dispose();
            EventQueue.invokeLater(() -> {
                try {
                    new NewLogin(connection).setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientConnection testConnection = new ClientConnection();
            if (CONFIG_DEBUG) {
                System.out.println("[DEBUG MODE] Connecting to localhost...");
            }
            new Loading(testConnection).launch();
        });
    }
}
