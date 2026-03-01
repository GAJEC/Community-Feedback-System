package server.model;

public interface LogListener {
    void onLog(String timestamp, String user, String transaction, String dataAffected);
}