package server;

import common.User;
import server.actions.Login;

import java.io.*;
import java.net.Socket;

import static common.Config.CONFIG_DEBUG;

public class ClientHandler extends Thread {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int clientId;

    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Client " + clientId + " connected from " + socket.getInetAddress());

            while (true) {
                Object received = in.readObject();

                switch (received) {
                    case User u -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received login request from Client " + clientId);
                        }

                        User response = Login.handleLogin(u, clientId);
                        out.writeObject(response);
                        out.flush();
                    }
//                    case Feedback f -> {
//                        System.out.println("Received feedback: " + f.getMessage());
//                    }
                    case null -> System.out.println("Received a null object");
                    default -> System.out.println("Received unknown type: " + received.getClass().getName());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client " + clientId + " disconnected");
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
