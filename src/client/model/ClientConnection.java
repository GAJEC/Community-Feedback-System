package client.model;

import common.Feedbacks;
import common.User;

import java.io.*;
import java.net.Socket;

import static common.Config.CONFIG_DEBUG;
import static common.Config.SOCKET_ADDRESS;
import static common.Config.SOCKET_PORT;

public class ClientConnection {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected = false;
    private ConnectionListener listener;

    public void setConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public void connectAsync() {
        new Thread(() -> {
            if (connect()) {
                if (listener != null) {
                    listener.onConnected();
                }
            } else {
                if (listener != null) {
                    listener.onConnectionFailed("Unable to reach server");
                }
            }
        }).start();
    }

    public boolean connect() {
        try {
            socket = new Socket(SOCKET_ADDRESS, SOCKET_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            System.out.println("Connected to server");
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    public void sendUser(User user) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            out.writeObject(user);
            out.flush();
            if(CONFIG_DEBUG){
                System.out.println("Sent user: " + user);
            }
        } catch (IOException e) {
            System.err.println("Error sending user: " + e.getMessage());
        }
    }

    public void sendFeedback(Feedbacks feedback) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            out.writeObject(feedback);
            out.flush();
            if(CONFIG_DEBUG){
                System.out.println("Sent feedback: " + feedback);
            }
        } catch (IOException e) {
            System.err.println("Error sending feedback: " + e.getMessage());
        }
    }

    public void sendRequest(String request) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            out.writeObject(request);
            out.flush();
            if(CONFIG_DEBUG){
                System.out.println("Sent request: " + request);
            }
        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
        }
    }

    public Object receive() {
        if (!connected) {
            System.err.println("Not connected to server");
            return null;
        }
        
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error receiving data: " + e.getMessage());
            return null;
        }
    }

    public void disconnect() {
        try {
            if (out != null) {
                out.writeObject("DISCONNECT");
                out.flush();
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            connected = false;
            System.out.println("Disconnected from server");
            if (listener != null) {
                listener.onDisconnected();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
