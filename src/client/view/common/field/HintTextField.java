package client.view.common.field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A JTextField with placeholder (hint) text functionality.
 * The hint appears in gray when the field is empty and unfocused.
 */
public class HintTextField extends JTextField {
    private final String hint;
    private boolean showingHint;

    public HintTextField(String hint) {
        super(hint);
        this.hint = hint;
        this.showingHint = true;
        setForeground(Color.GRAY);

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingHint) {
                    setText("");
                    setForeground(Color.BLACK);
                    showingHint = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(hint);
                    setForeground(Color.GRAY);
                    showingHint = true;
                }
            }
        });
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }
}
