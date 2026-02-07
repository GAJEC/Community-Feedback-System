package client;

import client.model.ClientConnection;
import client.model.ConnectionListener;
import client.view.Loading;
import client.view.Login;
import common.User;

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
//            Login loginView = new Login(connection);
//            loginView.setVisible(true);

            Loading loadingView = new Loading(connection);
            loadingView.launch();
        });
    }

    public static ClientConnection getConnection() {
        return connection;
    }
}
