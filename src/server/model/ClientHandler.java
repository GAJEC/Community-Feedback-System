package server.model;

import common.Announcement;
import common.BroadcastMessage;
import common.DeleteUserRequest;
import common.FeedbackInternalNoteUpdateRequest;
import common.FeedbackPriorityUpdateRequest;
import common.FeedbackStatusUpdateRequest;
import common.Feedbacks;
import common.RegisterRequest;
import common.User;
import common.UserStatusUpdateRequest;
import common.VoteRequest;
import common.XmlMessageCodec;
import server.model.Server;
import server.model.actions.AnnouncementActions;
import server.model.actions.AnnouncementActions;
import common.XmlMessageCodec;
import common.XmlMessageCodec;
import server.model.actions.AnnouncementActions;
import org.xml.sax.SAXException;
import server.model.actions.FeedbackActions;
import server.model.actions.LoginActions;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;

import static common.Config.CONFIG_DEBUG;

public class ClientHandler extends Thread {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int clientId;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    // Para makapagsend tayo ng broadcast. Returns false if may error sa pag send (e.g. client disconnected)
    public boolean sendBroadcast(BroadcastMessage message) {
        try {
            synchronized (out) {
                out.writeObject(message);
                out.flush();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error broadcasting to client " + clientId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Add naten yung client sa active clients list, para makareceive siya ng broadcast messages
            Server.addClient(this);

            System.out.println("Client " + clientId + " connected from " + socket.getInetAddress());

            while (running) {
                Object received = in.readObject();

                switch (received) {
                    case User u -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received login request from Client " + clientId);
                        }

                        Object[] response = LoginActions.handleLogin(u, clientId);

                        // Gumamit tayo ng synchronized block para sure na walang sabayang access sa output stream
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }
                    }
                    case RegisterRequest rr -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received register request from Client " + clientId);
                        }

                        Object[] response = LoginActions.handleRegister(rr, clientId);

                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0]) && response[2] != null) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                    BroadcastMessage.BroadcastType.NEW_USER,
                                    response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case Feedbacks f -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received feedback submission from Client " + clientId);
                        }

                        Object[] response = FeedbackActions.handleFeedbackSubmission(f, clientId);
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0])) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                BroadcastMessage.BroadcastType.NEW_FEEDBACK,
                                response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case Announcement announcement -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received announcement post request from Client " + clientId);
                        }

                        Object[] response = AnnouncementActions.postAnnouncement(announcement, clientId);
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0]) && response[2] != null) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                    BroadcastMessage.BroadcastType.NEW_ANNOUNCEMENT,
                                    response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case VoteRequest vr -> {
                        if(CONFIG_DEBUG){
                            System.out.println("Received vote request from Client " + clientId + ": " + vr);
                        }
                        
                        Object[] response = FeedbackActions.handleVote(vr, clientId);
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0]) && response[2] != null) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                BroadcastMessage.BroadcastType.VOTE_UPDATE,
                                response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case UserStatusUpdateRequest usr -> {
                        if (CONFIG_DEBUG) {
                            System.out.println("Received user status update request from Client " + clientId);
                        }
                        Object[] response = LoginActions.updateUserStatus(usr.getUserId(), usr.getStatus());
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }
                    }
                    case DeleteUserRequest dur -> {
                        if (CONFIG_DEBUG) {
                            System.out.println("Received delete user request from Client " + clientId);
                        }
                        Object[] response = LoginActions.deleteUser(dur.getUserId());
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }
                    }
                    case FeedbackStatusUpdateRequest fsr -> {
                        if (CONFIG_DEBUG) {
                            System.out.println("Received feedback status update request from Client " + clientId);
                        }
                        Object[] response = FeedbackActions.updateFeedbackStatus(fsr.getFeedbackId(), fsr.getStatus());
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0]) && response[2] != null) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                    BroadcastMessage.BroadcastType.STATUS_UPDATE,
                                    response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case FeedbackPriorityUpdateRequest fpr -> {
                        if (CONFIG_DEBUG) {
                            System.out.println("Received feedback priority update request from Client " + clientId);
                        }
                        Object[] response = FeedbackActions.updateFeedbackPriority(fpr.getFeedbackId(), fpr.getPriority());
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0]) && response[2] != null) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                    BroadcastMessage.BroadcastType.PRIORITY_UPDATE,
                                    response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case FeedbackInternalNoteUpdateRequest fnr -> {
                        if (CONFIG_DEBUG) {
                            System.out.println("Received feedback internal note update request from Client " + clientId);
                        }
                        Object[] response = FeedbackActions.updateFeedbackInternalNote(fnr.getFeedbackId(), fnr.getInternalNote());
                        synchronized (out) {
                            out.writeObject(response);
                            out.flush();
                        }

                        if ("200".equals(response[0]) && response[2] != null) {
                            BroadcastMessage broadcast = new BroadcastMessage(
                                    BroadcastMessage.BroadcastType.INTERNAL_NOTE_UPDATE,
                                    response[2]
                            );
                            Server.broadcast(broadcast, this);
                        }
                    }
                    case String s -> {
                        if ("GET_FEEDBACKS".equals(s)) {
                            if(CONFIG_DEBUG){
                                System.out.println("Received GET_FEEDBACKS request from Client " + clientId);
                            }
                            
                            Object[] response = FeedbackActions.getAllFeedbacks();
                            synchronized (out) {
                                out.writeObject(response);
                                out.flush();
                            }
                        } else if ("GET_USERS".equals(s)) {
                            if (CONFIG_DEBUG) {
                                System.out.println("Received GET_USERS request from Client " + clientId);
                            }

                            Object[] response = LoginActions.getAllUsers();
                            synchronized (out) {
                                out.writeObject(response);
                                out.flush();
                            }
                        } else if ("GET_ANNOUNCEMENTS".equals(s)) {
                            if (CONFIG_DEBUG) {
                                System.out.println("Received GET_ANNOUNCEMENTS request from Client " + clientId);
                            }

                            Object[] response = AnnouncementActions.getAllAnnouncements();
                            synchronized (out) {
                                out.writeObject(response);
                                out.flush();
                            }
                        } else if ("DISCONNECT".equals(s)) {
                            System.out.println("Client " + clientId + " requested disconnect");
                            running = false;
                        } else {
                            System.out.println("Received unknown string command: " + s);
                        }
                    }
                    case null -> System.out.println("Received a null object");
                    default -> System.out.println("Received unknown type: " + received.getClass().getName());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client " + clientId + " disconnected: " + e.getMessage());
        } catch (ParserConfigurationException | SAXException e) {
            System.err.println("Error processing request: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    // Para sa cleanup kapag nagdisconnect ang client o nageerror, para maayos na ma-release lahat ng resources
    private void cleanup() {
        running = false;
        Server.removeClient(this);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
        System.out.println("Client " + clientId + " handler terminated");
    }
}
