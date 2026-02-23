package client.view.residents.panels;

import client.view.common.Buttons;
import client.view.common.CustomPanel;
import client.view.strings.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static client.view.strings.Fonts.Inter;

public class FeedbackSubmitPopUp extends JDialog {
    public FeedbackSubmitPopUp(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setSize(400, 200);
        setLocationRelativeTo(parent);

        CustomPanel main = new CustomPanel(30);
        main.setLayout(new BorderLayout(0, 15));
        main.setBorder(new EmptyBorder(30, 30, 20, 30));

        main.setOffsetShadow(new Color(0, 0, 0, 40), 3);

        JLabel message = new JLabel("Your feedback has been submitted successfully!");
        message.setFont(Inter(1, 14));
        message.setForeground(new Color(30, 41, 59));
        message.setHorizontalAlignment(SwingConstants.CENTER);

        Buttons okBtn = new Buttons("OK", 15);
        okBtn.setBackgroundColor(Colors.SUB_PRIMARY);
        okBtn.setForeground(Color.WHITE);
        okBtn.setHoverColor(Colors.TERTIARY);
        okBtn.setPreferredSize(new Dimension(110, 35));
        okBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okBtn);

        main.add(message, BorderLayout.CENTER);
        main.add(buttonPanel, BorderLayout.SOUTH);

        add(main);
    }
}
