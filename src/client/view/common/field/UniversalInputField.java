package client.view.common.field;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class UniversalInputField extends JPanel {

    public enum FieldMode { TEXT, PASSWORD, INFO }

    private JTextField inputField;
    private JLabel infoTitleLabel;
    private JLabel infoValueLabel;

    private final String hint;
    private boolean showingHint = true;
    private final FieldMode mode;
    private int cornerRadius = 15;

    private Color hintColor = Color.GRAY;
    private Color textColor = Color.BLACK;
    private Color backgroundColor = new Color(245, 245, 245); // Lightest Gray

    /**
     * Primary Constructor
     *
     * @param icon        Optional icon (can be null)
     * @param text        The hint text (for fields) or Title text (for Info mode)
     * @param mode        TEXT, PASSWORD, or INFO
     */
    public UniversalInputField(Icon icon, String text, FieldMode mode) {
        this.mode = mode;
        this.hint = text;

        setLayout(new BorderLayout(12, 0));
        setOpaque(false); // We paint the background manually for rounded corners
        setBorder(new EmptyBorder(10, 15, 10, 15));

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            add(iconLabel, BorderLayout.WEST);
        }

        if (mode == FieldMode.INFO) {
            setupInfoMode(text);
        } else {
            setupInputMode(mode);
        }
    }

    private void setupInputMode(FieldMode mode) {
        if (mode == FieldMode.PASSWORD) {
            inputField = new JPasswordField(hint);
            ((JPasswordField) inputField).setEchoChar((char) 0); // Hide dots initially for hint
        } else {
            inputField = new JTextField(hint);
        }

        inputField.setBorder(null);
        inputField.setOpaque(false);
        inputField.setForeground(hintColor);

        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingHint) {
                    inputField.setText("");
                    inputField.setForeground(textColor);
                    if (mode == FieldMode.PASSWORD) {
                        ((JPasswordField) inputField).setEchoChar('•');
                    }
                    showingHint = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText(hint);
                    inputField.setForeground(hintColor);
                    if (mode == FieldMode.PASSWORD) {
                        ((JPasswordField) inputField).setEchoChar((char) 0);
                    }
                    showingHint = true;
                }
            }
        });

        add(inputField, BorderLayout.CENTER);
    }

    private void setupInfoMode(String title) {
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        infoTitleLabel = new JLabel(title);
        infoTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 12)); // Replace with Fonts.SansSerif
        infoTitleLabel.setForeground(Color.DARK_GRAY);

        infoValueLabel = new JLabel(" "); // Empty initially
        infoValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoValueLabel.setForeground(Color.GRAY);

        textPanel.add(infoTitleLabel);
        textPanel.add(infoValueLabel);

        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        g2.setColor(Color.LIGHT_GRAY);
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        g2.dispose();
        super.paintComponent(g);
    }

    public String getText() {
        if (mode == FieldMode.INFO) {
            return infoValueLabel.getText();
        }
        return showingHint ? "" : inputField.getText();
    }

    public void setText(String text) {
        if (mode == FieldMode.INFO) {
            infoValueLabel.setText(text);
        } else {
            showingHint = (text == null || text.isEmpty());
            inputField.setText(showingHint ? hint : text);
            inputField.setForeground(showingHint ? hintColor : textColor);

            if (mode == FieldMode.PASSWORD && inputField instanceof JPasswordField) {
                ((JPasswordField) inputField).setEchoChar(showingHint ? (char) 0 : '•');
            }
        }
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setFieldFont(Font font) {
        if (inputField != null) inputField.setFont(font);
        if (infoValueLabel != null) infoValueLabel.setFont(font);
    }
}