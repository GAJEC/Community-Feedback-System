package server;

import common.Feedbacks;
import common.User;
import org.xml.sax.SAXException;
import server.actions.FeedbackActions;
import server.actions.LoginActions;

import javax.xml.parsers.ParserConfigurationException;
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

                        Object[] response = LoginActions.handleLogin(u, clientId);
                        out.writeObject(response);
                        out.flush();
                    }
                    case Feedbacks f -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received feedback submission from Client " + clientId);
                        }

                        Object[] response = FeedbackActions.handleFeedbackSubmission(f, clientId);
                        out.writeObject(response);
                        out.flush();
                    }
                    case String s -> {
                        if ("GET_FEEDBACKS".equals(s)) {
                            if(CONFIG_DEBUG){
                                System.out.println("Received GET_FEEDBACKS request from Client " + clientId);
                            }
                            
                            Object[] response = FeedbackActions.getAllFeedbacks();
                            out.writeObject(response);
                            out.flush();
                        } else {
                            System.out.println("Received unknown string command: " + s);
                        }
                    }
                    case null -> System.out.println("Received a null object");
                    default -> System.out.println("Received unknown type: " + received.getClass().getName());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client " + clientId + " disconnected");
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
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
