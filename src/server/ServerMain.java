package server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerMain {
    public static void main(String[] args) {
        ServerUi view = new ServerUi();
        ServerUi.ServerController controller = new ServerUi.ServerController(view);
        controller.showUI();
    }

    public static class ServerUi extends JFrame {

        private final JButton startButton;
        private final JButton stopButton;
        private final JTable logTable;
        private final DefaultTableModel tableModel;
        private final JTextArea consoleTextArea;

        public ServerUi() {
            setTitle("Server Control Panel");
            setSize(800, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Control panel
            JPanel controlPanel = new JPanel();
            startButton = new JButton("Start Server");
            stopButton = new JButton("Stop Server");
            stopButton.setEnabled(false);
            controlPanel.add(startButton);
            controlPanel.add(stopButton);
            add(controlPanel, BorderLayout.SOUTH);

            // Tabbed Pane
            JTabbedPane tabbedPane = new JTabbedPane();

            // Logs Table
            String[] columns = {"Timestamp", "User", "Transaction", "Data Affected"};
            tableModel = new DefaultTableModel(columns, 0);
            logTable = new JTable(tableModel);
            JScrollPane logScrollPane = new JScrollPane(logTable);
            tabbedPane.addTab("Logs", logScrollPane);

            // Console TextArea
            consoleTextArea = new JTextArea();
            consoleTextArea.setEditable(false);
            consoleTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
            tabbedPane.addTab("Console", consoleScrollPane);

            add(tabbedPane, BorderLayout.CENTER);

            // Redirect System.out and System.err
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

        public static class ServerController {

            private final ServerUi view;
            private Thread serverThread;

            public ServerController(ServerUi view) {
                this.view = view;

                view.addStartListener(e -> {
                    view.setServerRunning(true);
                    startServer();
                });

                view.addStopListener(e -> {
                    stopServer();
                    view.setServerRunning(false);
                });
            }

            private void startServer() {
                serverThread = new Thread(() -> {
                    try {
                        Server.ui = view;
                        Server.startServer();
                    } catch (Exception ex) {
                        System.err.println("Error starting server: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                serverThread.start();
                System.out.println("Server started!");
            }

            private void stopServer() {
                if (serverThread != null && serverThread.isAlive()) {
                    Server.stopServer();
                    serverThread.interrupt();
                    System.out.println("Server stopped!");
                }
            }

            public void showUI() {
                view.showView();
            }
        }
    }
}