package client.view;

import client.view.strings.Colors;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLOutput;
import java.util.function.Function;

public class UiPreview {
    /* Ganto gamitin

        public static void main(String[] args) {
            UiPreview.show(new Login());
        }

    */
    public static void show(JComponent component) {
        System.out.println("[PREVIEW MODE]");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("UI Preview - "
                    + component.getClass().getSimpleName());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(component, BorderLayout.CENTER);

            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }


    /*

        public static void main(String[] args) {
            UiPreview.show(FeedbackSubmitPopUp::new);
        }

    */
    public static void show(Function<JFrame, JDialog> dialogSupplier) {
        System.out.println("[PREVIEW MODE]");
        SwingUtilities.invokeLater(() -> {
            // Fake parent frame (hidden)
            JFrame parent = new JFrame("Dialog Preview Host");
            parent.setSize(800, 600);
            parent.setLocationRelativeTo(null);
            parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            parent.setVisible(true);

            // Create dialog using supplied constructor
            JDialog dialog = dialogSupplier.apply(parent);

            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        });
    }


    /* Ganto gamit

        public static void main(String[] args) {
            // dummy User
            User dummyUser = new User(
                    "DUMBell",
                    "passpass"
            );
            UiPreview.show(v -> new ClientHeaderPanel(dummyUser), "ClientHeaderPanel Preview");
        }

    */
    public static void show(Function<Void, JPanel> panelSupplier, String title) {
        System.out.println("[PREVIEW MODE]");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            JPanel panel = panelSupplier.apply(null);
            frame.setContentPane(panel);

            frame.setVisible(true);
        });
    }
}