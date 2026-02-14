package client.view.common.panel;

import javax.swing.border.AbstractBorder;
import java.awt.*;

import static client.view.residents.ResidentDashboard.CARD_SHADOW;

public class DropShadowBorder extends AbstractBorder {
    private final int padding;
    public DropShadowBorder(int padding) { this.padding = padding; }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(CARD_SHADOW);
        g2.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
        g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 15, 15);
        g2.dispose();
    }
    @Override
    public Insets getBorderInsets(Component c) { return new Insets(padding, padding, padding, padding); }
}
