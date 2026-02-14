package client.view.common.field;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class IconInputField extends JPanel {
    private JTextField field;
    private final String placeholder;
    private final boolean isPassword;

    public IconInputField(Icon icon, String placeholder, boolean password) {
        this.placeholder = placeholder;
        this.isPassword = password;

        setLayout(new BorderLayout(12, 0));
        setBackground(new Color(232, 247, 243));
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(new Color(160, 180, 175));

        if (password) {
            field = new JPasswordField();
            ((JPasswordField) field).setEchoChar((char) 0);
        } else {
            field = new JTextField();
        }


        field.setText(placeholder);
        field.setForeground(new Color(170, 170, 170));
        field.setBorder(null);
        field.setBackground(getBackground());
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (isPassword) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(170, 170, 170));
                    if (isPassword) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });



        add(iconLabel, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
    }

    public String getText() {
        String text = field.getText();
        if (text.equals(placeholder)) {
            return "";
        }
        return text;
    }
}
