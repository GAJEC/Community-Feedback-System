package client.model;

import common.FeedbackStatusUpdateRequest;
import common.FeedbackPriorityUpdateRequest;
import common.FeedbackInternalNoteUpdateRequest;

public class AdminFeedbackModel {
    private final ClientConnection connection;

    public AdminFeedbackModel(ClientConnection connection) {
        this.connection = connection;
    }

    public Object[] getAllFeedbacks() {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendRequest("GET_FEEDBACKS");
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error fetching feedbacks: " + e.getMessage(), null};
        }
    }

    public Object[] updateFeedbackStatus(int feedbackId, String newStatus) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            return new Object[]{"400", "Status cannot be empty", null};
        }

        try {
            FeedbackStatusUpdateRequest request = new FeedbackStatusUpdateRequest(feedbackId, newStatus);
            connection.sendRequest(request);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error updating status: " + e.getMessage(), null};
        }
    }

    public Object[] updateFeedbackPriority(int feedbackId, String newPriority) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        if (newPriority == null || newPriority.trim().isEmpty()) {
            return new Object[]{"400", "Priority cannot be empty", null};
        }

        try {
            FeedbackPriorityUpdateRequest request = new FeedbackPriorityUpdateRequest(feedbackId, newPriority);
            connection.sendRequest(request);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error updating priority: " + e.getMessage(), null};
        }
    }

    public Object[] updateInternalNote(int feedbackId, String note) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        if (note == null) {
            note = "";
        }

        try {
            FeedbackInternalNoteUpdateRequest request = new FeedbackInternalNoteUpdateRequest(feedbackId, note);
            connection.sendRequest(request);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error updating internal note: " + e.getMessage(), null};
        }
    }

    public Object[] getFeedbacksByStatus(String status) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendRequest("GET_FEEDBACKS_BY_STATUS:" + status);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error filtering feedbacks: " + e.getMessage(), null};
        }
    }

    public Object[] searchFeedbacks(String keyword) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllFeedbacks();
        }

        try {
            connection.sendRequest("SEARCH_FEEDBACKS:" + keyword);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error searching feedbacks: " + e.getMessage(), null};
        }
    }
}
