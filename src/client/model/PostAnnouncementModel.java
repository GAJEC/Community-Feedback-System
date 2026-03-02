package client.model;

import common.Announcement;

public class PostAnnouncementModel {
    public Object[] postAnnouncement(ClientConnection connection, int userId, String userName, String subject, String details) {
        if (connection == null) {
            return new Object[]{"500", "No connection available", null};
        }

        if (!connection.isConnected() && !connection.connect()) {
            return new Object[]{"500", "Unable to connect to server", null};
        }

        Announcement announcement = new Announcement(0, userId, userName, subject, details, new java.util.Date());
        connection.sendRequest(announcement);
        Object response = connection.receive();
        if (response instanceof Object[]) {
            return (Object[]) response;
        }
        return new Object[]{"500", "No valid response from server", null};
    }
}
