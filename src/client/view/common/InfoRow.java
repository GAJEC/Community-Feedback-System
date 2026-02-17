package client.view.common;

import javax.swing.*;
import java.awt.*;

import static client.view.strings.Fonts.SansSerif;

public class InfoRow extends JPanel {

    private JLabel titleLabel;
    private JLabel valueLabel;

    public InfoRow(ImageIcon icon, String label, String value,
                   int labelStyle, int labelSize,
                   int valueStyle, int valueSize) {

        setLayout(new BorderLayout(12, 0));
        setOpaque(false);

        setAlignmentX(Component.LEFT_ALIGNMENT);

        add(new JLabel(icon), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(label);
        titleLabel.setFont(SansSerif(labelStyle, labelSize));
        titleLabel.setForeground(Color.DARK_GRAY);

        valueLabel = new JLabel(value);
        valueLabel.setFont(SansSerif(valueStyle, valueSize));
        valueLabel.setForeground(Color.GRAY);

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        add(textPanel, BorderLayout.CENTER);
    }


    public InfoRow(ImageIcon icon, String label, String value) {
        this(icon, label, value, Font.BOLD, 12, Font.PLAIN, 14);
        setMaximumSize(getMaximumSize());
    }

    public InfoRow(ImageIcon icon, String label) {
        this(icon, label, "", Font.BOLD, 12, Font.PLAIN, 14);
    }

    public static JLabel createLabel(String text) {
        // We use HTML to color just the asterisk red
        return new JLabel("<html>" + text + " <font color='red'>*</font></html>");
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension pref = getPreferredSize();
        return new Dimension(Integer.MAX_VALUE, pref.height);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setTitleFont(Font font) {
        titleLabel.setFont(font);
    }

    public void setValueFont(Font font) {
        valueLabel.setFont(font);
    }
}