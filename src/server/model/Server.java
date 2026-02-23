package server.model;

import common.BroadcastMessage;
import server.view.ServerUi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static common.Config.SOCKET_PORT;

public class Server {

    public static ServerUi ui;
    private static int clientCounter = 0;
    private static final List<ClientHandler> activeClients = new ArrayList<>();

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
        logEvent("System", "Server Start", "Server is starting...");
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
            logEvent("System", "Listening", "Port " + SOCKET_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientCounter++;

                    ClientHandler handler = new ClientHandler(clientSocket, clientCounter);
                    handler.start();
                    addClient(handler);

                    logEvent("System", "Client Served", "Total clients: " + clientCounter);

                } catch (IOException e) {
                    System.err.println("Connection error: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
        logEvent("System", "Server Stop", "Server has stopped");
    }
}