package client.view.common.field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class HintPasswordField extends JPasswordField {

    private final String hint;
    private boolean showingHint = true;

    public HintPasswordField(String hint) {
        super(hint);
        this.hint = hint;

        setEchoChar((char) 0);
        setForeground(Color.GRAY);

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (showingHint) {
                    setText("");
                    setForeground(Color.BLACK);
                    setEchoChar('•');
                    showingHint = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    setText(hint);
                    setForeground(Color.GRAY);
                    setEchoChar((char) 0);
                    showingHint = true;
                }
            }
        });
    }

    @Override
    public String getText() {
        return showingHint ? "" : new String(getPassword());
    }
}