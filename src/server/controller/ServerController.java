package server.controller;

import server.model.Server;
import server.view.ServerUi;

public class ServerController {

    private final ServerUi view;
    private Thread serverThread;

    public ServerController(ServerUi view) {
        this.view = view;

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
            Server.ui = view;
            Server.startServer();
        });
        serverThread.start();
        System.out.println("Server started!");
    }

    private void stopServer() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt(); // interrupt server thread
            System.out.println("Server stopped!");
        }
    }

    public void showUI() {
        view.showView();
    }
}