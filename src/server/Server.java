package server;

import common.BroadcastMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static common.Config.SOCKET_PORT;

public class Server {
    private static int clientCounter = 0;
    private static final List<ClientHandler> activeClients = new ArrayList<>();

    public static synchronized void addClient(ClientHandler client) {
        activeClients.add(client);
        System.out.println("Client added. Active clients: " + activeClients.size());
    }

    public static synchronized void removeClient(ClientHandler client) {
        activeClients.remove(client);
        System.out.println("Client removed. Active clients: " + activeClients.size());
    }

    public static synchronized void broadcast(BroadcastMessage message, ClientHandler sender) {
        List<ClientHandler> disconnected = new ArrayList<>();
        
        // To all active clients, makakareceive ng broadcast message. If may error sa pag send, i-mark as disconnected
        for (ClientHandler client : activeClients) {
            if (!client.sendBroadcast(message)) {
                disconnected.add(client);
            }
        }
        
        // Pang clean ng client. Remove clients that failed to receive the broadcast
        for (ClientHandler client : disconnected) {
            removeClient(client);
        }
    }

    public static void main(String[] args) {
        System.out.println("Community Feedback System");
        
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
            System.out.println("Listening to " + SOCKET_PORT);

            while (true) {
                try{
                    Socket clientSocket = serverSocket.accept();
                    clientCounter++;

                    ClientHandler handler = new ClientHandler(clientSocket, clientCounter);
                    handler.start();

                    System.out.println("Total clients served: " + clientCounter);
                }catch (IOException e) {
                    System.err.println("Connection lost or stream closed: " + e.getMessage());
                    break;
                } catch (Exception e) {
                    System.err.println("Server error: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}