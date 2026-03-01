package server.controller;

import server.model.Server;
import server.view.ServerUi;

import static common.Config.SOCKET_ADDRESS;
import static common.Config.SOCKET_PORT;

public class ServerController {

    private final ServerUi view;
    private Thread serverThread;

    public ServerController(ServerUi view) {
        this.view = view;

        Server.setLogListener((timestamp, user, transaction, dataAffected) -> {
            view.addLogEntry(timestamp, user, transaction, dataAffected);
        });

        view.addStartListener(e -> {
            view.setServerRunning(true);
            startServer();
        });

        view.addStopListener(e -> {
            view.setServerRunning(false);
            stopServer();
        });
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            Server.startServer();
        });
        serverThread.start();
        System.out.println("Server started!");
        System.out.println("\tPort: " + SOCKET_PORT);
        System.out.println("\tAddress: " + SOCKET_ADDRESS);
    }

    private void stopServer() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            System.out.println("Server stopped!");
        }
    }

    public void showUI() {
        view.showView();
    }
}