package server.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class ServerUi extends JFrame {
    private JButton startButton;
    private JButton stopButton;
    private JTable logTable;
    private DefaultTableModel tableModel;

    public ServerUi() {
        setTitle("Server Control Panel");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        String[] columns = {"Timestamp", "User", "Transaction", "Data Affected"};
        tableModel = new DefaultTableModel(columns, 0);
        logTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addLogEntry(String timestamp, String user, String transaction, String dataAffected) {
        tableModel.addRow(new Object[]{timestamp, user, transaction, dataAffected});
    }

    public void setServerRunning(boolean running) {
        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }

    public void addStartListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }

    public void addStopListener(ActionListener listener) {
        stopButton.addActionListener(listener);
    }

    public void showView() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public static void main(String[] args) {
        ServerUi view = new ServerUi();
        view.showView();

        view.addLogEntry("2026-02-23 12:00:00", "Resident", "Feedback", "Feedback");
        view.addLogEntry("2026-02-23 12:05:00", "Official", "Announcement", "Announcement");
    }
}