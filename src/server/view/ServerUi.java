package server.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerUi extends JFrame {

    private JButton startButton;
    private JButton stopButton;
    private JTable logTable;
    private DefaultTableModel tableModel;
    private JTextArea consoleTextArea;

    public ServerUi() {
        setTitle("Server Control Panel");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        String[] columns = {"Timestamp", "User", "Transaction", "Data Affected"};
        tableModel = new DefaultTableModel(columns, 0);
        logTable = new JTable(tableModel);
        JScrollPane logScrollPane = new JScrollPane(logTable);
        tabbedPane.addTab("Logs", logScrollPane);

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
        tabbedPane.addTab("Console", consoleScrollPane);

        add(tabbedPane, BorderLayout.CENTER);

        redirectSystemStreams();
    }

    public void addLogEntry(String timestamp, String user, String transaction, String dataAffected) {
        SwingUtilities.invokeLater(() -> tableModel.addRow(new Object[]{timestamp, user, transaction, dataAffected}));
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

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                updateConsole(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                updateConsole(new String(b, off, len));
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void updateConsole(final String text) {
        SwingUtilities.invokeLater(() -> consoleTextArea.append(text));
    }
}