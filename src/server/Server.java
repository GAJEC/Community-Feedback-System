package server;

import common.BroadcastMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static common.Config.SOCKET_PORT;

public class Server {

    public static ServerMain.ServerUi ui;
    private static int clientCounter = 0;
    private static final List<ClientHandler> activeClients = new ArrayList<>();
    private static ServerSocket serverSocket;

    public static synchronized void addClient(ClientHandler client) {
        activeClients.add(client);
        logEvent("System", "Client Connect", "Client #" + client.getId());
    }

    public static synchronized void removeClient(ClientHandler client) {
        activeClients.remove(client);
        logEvent("System", "Client Disconnect", "Client #" + client.getId());
    }

    public static synchronized void broadcast(Object message, ClientHandler sender) {
        List<ClientHandler> disconnected = new ArrayList<>();
        for (ClientHandler client : activeClients) {
            if (!client.sendBroadcast((BroadcastMessage) message)) {
                disconnected.add(client);
            }
        }
        for (ClientHandler client : disconnected) {
            removeClient(client);
        }
    }

    public static void logEvent(String user, String transaction, String dataAffected) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("[" + timestamp + "] " + user + " - " + transaction + " - " + dataAffected);
        if (ui != null) {
            ui.addLogEntry(timestamp, user, transaction, dataAffected);
        }
    }

    public static void startServer() {
        try {
            serverSocket = new ServerSocket(SOCKET_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientCounter++;

                    ClientHandler handler = new ClientHandler(clientSocket, clientCounter);
                    handler.start();
                    addClient(handler);


                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        logEvent("System", "Server Stop", "ServerSocket closed, stopping server loop");
                        break;
                    }
                    System.err.println("Connection error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    public static void stopServer() {
        try {
            for (ClientHandler client : new ArrayList<>(activeClients)) {
                client.shutdown();
            }
            activeClients.clear();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
}