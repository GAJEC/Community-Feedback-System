package client;

import client.controller.LoadingController;
import client.model.ClientConnection;
import client.model.ConnectionListener;

import javax.swing.*;

public class Client {
    private static ClientConnection connection;

    public static void main(String[] args) {
        connection = new ClientConnection();
        
        connection.setConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected() {
                System.out.println("Client auto-connected to server successfully");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Client disconnected from server");
            }

            @Override
            public void onConnectionFailed(String error) {
                System.err.println("Auto-connection failed: " + error);
                System.err.println("You can try connecting manually from the login screen");
            }
        });
        
        connection.connectAsync();
        
        SwingUtilities.invokeLater(() -> {
            LoadingController controller = new LoadingController(connection);
            controller.show();
        });
    }

    public static ClientConnection getConnection() {
        return connection;
    }
}
