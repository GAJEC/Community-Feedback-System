package client.view.residents.panels;

import client.view.common.button.Button;
import client.view.common.panel.RoundedPanel;
import client.view.strings.Colors;
import client.view.strings.Fonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FeedbackSubmitPopUp extends JDialog {
    public FeedbackSubmitPopUp(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setSize(400, 200);
        setLocationRelativeTo(parent);

        RoundedPanel main = new RoundedPanel(30, Color.WHITE);
        main.setLayout(new BorderLayout(0, 15));
        main.setBorder(new EmptyBorder(30, 30, 20, 30));

        JLabel message = new JLabel("Your feedback has been submitted successfully!");
        message.setFont(Fonts.INTER_HEADING_3);
        message.setForeground(new Color(30, 41, 59));
        message.setHorizontalAlignment(SwingConstants.CENTER);

        Button okBtn = new Button("OK", 15, Colors.SUB_PRIMARY, Color.WHITE, Colors.TERTIARY);
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
