package client.view.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.OutputStream;
import java.io.PrintStream;

public class RoundedInputField extends JPanel {
    private static final Object CLIPBOARD_ERR_LOCK = new Object();

    private JTextField field;
    private String hint;
    private boolean showingHint = true;

    private int radius = 15;
    private int strokeWidth = 1;

    private Color backgroundColor = new Color(245,245,245);
    private Color borderColor = new Color(220,220,220);
    private Color textColor = Color.BLACK;
    private Color hintColor = Color.GRAY;

    public RoundedInputField(Icon icon, String placeholder, boolean password) {
        this.hint = placeholder;

        setLayout(new BorderLayout(12, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        if (icon != null) {
            add(new JLabel(icon), BorderLayout.WEST);
        }

        if (password) {
            field = new JPasswordField(hint);
            ((JPasswordField) field).setEchoChar((char) 0);
        } else {
            field = new JTextField(hint);
        }

        field.setBorder(null);
        field.setOpaque(false);
        field.setForeground(hintColor);

        installSafePasteHandler();

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingHint) {
                    field.setText("");
                    field.setForeground(textColor);
                    if (field instanceof JPasswordField)
                        ((JPasswordField) field).setEchoChar('•');
                    showingHint = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(hintColor);
                    if (field instanceof JPasswordField)
                        ((JPasswordField) field).setEchoChar((char) 0);
                    showingHint = true;
                }
            }
        });

        add(field, BorderLayout.CENTER);
    }

    private void installSafePasteHandler() {
        Action safePasteAction = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pastePlainTextFromClipboard();
            }
        };

        field.getActionMap().put(DefaultEditorKit.pasteAction, safePasteAction);

        InputMap inputMap = field.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), DefaultEditorKit.pasteAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.SHIFT_DOWN_MASK), DefaultEditorKit.pasteAction);
    }

    private void pastePlainTextFromClipboard() {
        try {
            Object data;
            synchronized (CLIPBOARD_ERR_LOCK) {
                PrintStream originalErr = System.err;
                try {
                    System.setErr(new PrintStream(OutputStream.nullOutputStream()));
                    data = Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .getData(DataFlavor.stringFlavor);
                } finally {
                    System.setErr(originalErr);
                }
            }

            if (!(data instanceof String)) {
                return;
            }

            String pasted = (String) data;
            if (pasted == null) {
                return;
            }

            if (showingHint) {
                field.setText("");
                field.setForeground(textColor);
                if (field instanceof JPasswordField) {
                    ((JPasswordField) field).setEchoChar('•');
                }
                showingHint = false;
            }

            field.replaceSelection(pasted);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        Shape shape = new RoundRectangle2D.Double(strokeWidth, strokeWidth, w - strokeWidth * 2, h - strokeWidth * 2, radius, radius);

        g2.setColor(backgroundColor);
        g2.fill(shape);

        g2.setStroke(new BasicStroke(strokeWidth));
        g2.setColor(borderColor);
        g2.draw(shape);

        g2.dispose();
        super.paintComponent(g);
    }

    public String getText() {
        return showingHint ? "" : field.getText();
    }

    public void setText(String text) {
        showingHint = (text == null || text.isEmpty());
        field.setText(showingHint ? hint : text);
        field.setForeground(showingHint ? hintColor : textColor);
    }

    // Styling setters
    public void setCornerRadius(int r) { radius = r; repaint(); }
    public void setBackgroundColor(Color c) { backgroundColor = c; repaint(); }
    public void setStrokeColor(Color c) { borderColor = c; repaint(); }
    public void setStrokeWidth(int w) { strokeWidth = w; repaint(); }
    public void setTextFont(Font f) { field.setFont(f); }
    public void setTextColor(Color c) { textColor = c; field.setForeground(c); }
    public void setFieldSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }
    public void setFieldHeight(int height) {
        setPreferredSize(new Dimension(Short.MAX_VALUE, height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }
}