package client.model;

import common.BroadcastMessage;
import common.Feedbacks;
import common.RegisterRequest;
import common.User;
import common.VoteRequest;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static common.Config.CONFIG_DEBUG;
import static common.Config.SOCKET_ADDRESS;
import static common.Config.SOCKET_PORT;

public class ClientConnection {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected = false;
    private ConnectionListener listener;
    private BroadcastListener broadcastListener;
    private Thread listenerThread;
    private final BlockingQueue<Object> responseQueue = new LinkedBlockingQueue<>();

    public interface BroadcastListener {
        void onBroadcastReceived(BroadcastMessage message);
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public void setBroadcastListener(BroadcastListener broadcastListener) {
        this.broadcastListener = broadcastListener;
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
            
            startBroadcastListener();
            
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    private void startBroadcastListener() {
        listenerThread = new Thread(() -> {
            while (connected) {
                try {
                    Object received = in.readObject();
                    
                    if (received instanceof BroadcastMessage) {
                        BroadcastMessage broadcast = (BroadcastMessage) received;
                        if (CONFIG_DEBUG) {
                            System.out.println("Received broadcast: " + broadcast.getType());
                        }
                        if (broadcastListener != null) {
                            broadcastListener.onBroadcastReceived(broadcast);
                        }
                    } else {
                        responseQueue.offer(received);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (connected) {
                        System.err.println("Error in broadcast listener: " + e.getMessage());
                    }
                    break;
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendUser(User user) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            synchronized (out) {
                out.writeObject(user);
                out.flush();
            }
            if(CONFIG_DEBUG){
                System.out.println("Sent user: " + user);
            }
        } catch (IOException e) {
            System.err.println("Error sending user: " + e.getMessage());
        }
    }

    public void sendRegisterRequest(RegisterRequest registerRequest) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }

        try {
            synchronized (out) {
                out.writeObject(registerRequest);
                out.flush();
            }
            if (CONFIG_DEBUG) {
                System.out.println("Sent register request for username: " + registerRequest.getUsername());
            }
        } catch (IOException e) {
            System.err.println("Error sending register request: " + e.getMessage());
        }
    }

    public void sendFeedback(Feedbacks feedback) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            synchronized (out) {
                out.writeObject(feedback);
                out.flush();
            }
            if(CONFIG_DEBUG){
                System.out.println("Sent feedback: " + feedback);
            }
        } catch (IOException e) {
            System.err.println("Error sending feedback: " + e.getMessage());
        }
    }

    public void sendVoteRequest(VoteRequest voteRequest) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            synchronized (out) {
                out.writeObject(voteRequest);
                out.flush();
            }
            if(CONFIG_DEBUG){
                System.out.println("Sent vote request: " + voteRequest);
            }
        } catch (IOException e) {
            System.err.println("Error sending vote request: " + e.getMessage());
        }
    }

    public void sendRequest(Object request) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        
        try {
            synchronized (out) {
                out.writeObject(request);
                out.flush();
            }
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
            Object response = responseQueue.poll(10, TimeUnit.SECONDS);
            if (response == null) {
                System.err.println("Timeout waiting for server response");
            }
            return response;
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for response: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void disconnect() {
        try {
            connected = false;
            if (out != null) {
                synchronized (out) {
                    out.writeObject("DISCONNECT");
                    out.flush();
                }
            }
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            responseQueue.clear();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
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
