package client.view.common.field;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static client.view.strings.Fonts.SansSerif;
import static client.view.strings.Images.scaled;

public class InfoRow extends JPanel {
    private final JLabel valueComponent;

    public InfoRow(URL iconUrl, String label, String value, int labelStyle, int labelSize, int valueStyle, int valueSize, int iconWidth, int iconHeight) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon iconImg = scaled(iconUrl, iconWidth, iconHeight);
        JLabel icon = new JLabel(iconImg);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel labelComponent = new JLabel();
        valueComponent = new JLabel();

        valueComponent.setFont(SansSerif(valueStyle, valueSize));
        valueComponent.setForeground(Color.GRAY);

        textPanel.add(labelComponent);
        textPanel.add(valueComponent);

        add(icon);
        add(textPanel);

        labelComponent.setText(label);
        labelComponent.setFont(SansSerif(labelStyle, labelSize));

        if (value != null) {
            valueComponent.setText(value);
        }
    }

    // For UserProfileDialogue
    public InfoRow(URL iconUrl, String label, String value) {
        this(iconUrl, label, value, 1, 14, 0, 14, 28, 28);
    }

    public InfoRow(URL iconUrl, String labelText) {
        this(iconUrl, labelText, null);
    }

    public void setValue(String newValue) {
        valueComponent.setText(newValue);
    }
}
