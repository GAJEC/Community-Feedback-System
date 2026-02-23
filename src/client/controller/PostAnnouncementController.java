package client.controller;

import client.model.ClientConnection;
import common.Announcement;

public class PostAnnouncementController {

    public Object[] postAnnouncementWithFallback(ClientConnection primaryConnection, int userId, String userName, String subject, String details) {
        Announcement announcement = new Announcement(0, userId, userName, subject, details, new java.util.Date());

        Object[] primary = sendAnnouncement(primaryConnection, announcement, false);
        if (primary != null && "200".equals(String.valueOf(primary[0]))) {
            return primary;
        }

        if (primary != null && !"500".equals(String.valueOf(primary[0]))) {
            return primary;
        }

        ClientConnection dedicatedConnection = new ClientConnection();
        try {
            return sendAnnouncement(dedicatedConnection, announcement, true);
        } finally {
            if (dedicatedConnection.isConnected()) {
                dedicatedConnection.disconnect();
            }
        }
    }

    public Object[] sendAnnouncement(ClientConnection targetConnection, Announcement announcement, boolean forceConnect) {
        if (targetConnection == null) {
            return new Object[]{"500", "No connection available", null};
        }

        if ((forceConnect || !targetConnection.isConnected()) && !targetConnection.connect()) {
            return new Object[]{"500", "Unable to connect to server", null};
        }

        targetConnection.sendRequest(announcement);
        Object response = targetConnection.receive();
        if (response instanceof Object[]) {
            return (Object[]) response;
        }
        return new Object[]{"500", "No valid response from server", null};
    }
}
