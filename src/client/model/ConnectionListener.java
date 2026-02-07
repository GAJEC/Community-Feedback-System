package client.model;

public interface ConnectionListener {
    void onConnected();
    void onDisconnected();
    void onConnectionFailed(String error);
}
