package client.view.common.field;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static client.view.strings.Colors.LIGHTEST_GRAY;
import static client.view.strings.Colors.LIGHT_GRAY;
import static client.view.strings.Fonts.SansSerif;

public class IconInputField extends JPanel {

    private final JTextField field;

    public IconInputField(Icon icon, String placeholder, Color background, Color foreground, Font font, boolean password) {
        setLayout(new BorderLayout(12, 0));
        setBackground(background);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(foreground);

        if (password) {
            field = new HintPasswordField(placeholder);
        } else {
            field = new HintTextField(placeholder);
        }

        field.setBorder(null);
        field.setBackground(getBackground());
        field.setFont(font);

        add(iconLabel, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
    }

    public IconInputField(Icon icon, String placeholder, boolean password) {
        this(icon, placeholder, LIGHTEST_GRAY, LIGHT_GRAY, SansSerif(0, 14), password);
    }

    public String getText() {
        return field.getText();
    }
}