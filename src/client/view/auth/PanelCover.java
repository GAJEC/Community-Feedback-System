//package client.view.auth;
//
//import client.view.strings.Colors;
//import client.view.strings.Images;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class PanelCover extends JPanel {
//
//    public PanelCover() {
//        setLayout(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        c.insets = new Insets(5, 10, 5, 10);
//
//
//        JLabel logo = new JLabel(Images.scaledLogo(300, 300));
//        c.gridy = 0;
//        c.insets= new Insets(-5,0,0,0);
//        add(logo, c);
//
//
//        JLabel title = new JLabel("Welcome to Community Feedback!");
//        title.setFont(new Font("SansSerif", Font.BOLD, 30));
//        title.setForeground(Color.WHITE);
//
//        c.gridy = 1;
//        c.insets= new Insets(-150,60,0,60);
//        add(title, c);
//
//
//        JLabel desc = new JLabel("Keep connected with us");
//        desc.setFont(new Font("SansSerif", Font.PLAIN, 20));
//        desc.setForeground(Color.WHITE);
//
//        c.gridy = 2;
//        c.insets = new Insets(-100, 0, 0, 0);
//        add(desc, c);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        GradientPaint gra = new GradientPaint(0, 0, Colors.PRIMARY, 0, getHeight(), Colors.SECONDARY);
//        g2.setPaint(gra);
//        g2.fillRect(0, 0, getWidth(), getHeight());
//    }
//}