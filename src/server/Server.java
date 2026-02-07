package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static common.Config.SOCKET_PORT;

public class Server {
    private static int clientCounter = 0;

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