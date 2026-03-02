package client.view.auth;

import javax.swing.*;
import java.awt.*;

public class Loading extends JWindow {
    private float alpha = 0f;
    private Image logoImage;
    private boolean fadingOut = false;
    private Timer timer;
    private Runnable onFadeComplete;

    public Loading() {
        setBackground(new Color(0, 0, 0, 0));
        setSize(600, 600);
        setLocationRelativeTo(null);

        java.net.URL imgURL = getClass().getResource("/assets/logo_icon.png");
        if (imgURL != null) {
            Image originalImage = new ImageIcon(imgURL).getImage();
            logoImage = originalImage.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        }

        // fade in at fade out timer
        timer = new Timer(16, e -> {
            if (!fadingOut) {
                alpha += 0.03f;
                if (alpha > 1.0f) alpha = 1.0f;
            } else {
                alpha -= 0.05f;
                if (alpha < 0.0f) alpha = 0.0f;
            }
            repaint();

            if (fadingOut && alpha <= 0.0f) {
                timer.stop();
                dispose();
                if (onFadeComplete != null) {
                    onFadeComplete.run();
                }
            }
        });
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

    public void showLoading() {
        setVisible(true);
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void startFadeOut(Runnable onComplete) {
        this.onFadeComplete = onComplete;
        fadingOut = true;
    }
}
