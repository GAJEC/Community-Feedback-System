package client.model;

import common.Feedbacks;
import common.VoteRequest;

import java.util.Date;

public class FeedbackModel {
    private final ClientConnection connection;

    public FeedbackModel(ClientConnection connection) {
        this.connection = connection;
    }

    public Object[] submitFeedback(int userId, String category, String title, String location, String content) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            Feedbacks feedback = new Feedbacks(
                0,
                userId,
                0,
                new String[]{"Pending"},
                new String[]{category},
                title,
                "No priority set",
                "",
                location,
                content,
                new Date()
            );

            connection.sendFeedback(feedback);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error submitting feedback: " + e.getMessage(), null};
        }
    }

    public Object[] submitFeedback(Feedbacks feedback) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendFeedback(feedback);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error submitting feedback: " + e.getMessage(), null};
        }
    }

    public Object[] deleteFeedback(int feedbackId) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendRequest("DELETE_FEEDBACK:" + feedbackId);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            System.out.println("DEBUG: Server sent " + (response == null ? "null" : response.getClass().getName()));
            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error deleting feedback: " + e.getMessage(), null};
        }
    }


    public Object[] getAllFeedbacks() {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendRequest("GET_FEEDBACKS");
            Object response = connection.receive();

            if (response instanceof Object[]) {
                Object[] responseArray = (Object[]) response;
                String statusCode = (String) responseArray[0];
                String message = (String) responseArray[1];
                Object data = responseArray[2];

                if ("200".equals(statusCode) && data instanceof Feedbacks[]) {
                    return new Object[]{statusCode, message, data};
                }
                return new Object[]{statusCode, message, null};
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error retrieving feedbacks: " + e.getMessage(), null};
        }
    }

    public Object[] getFeedbackById(int feedbackId) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendRequest("GET_FEEDBACK:" + feedbackId);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error retrieving feedback: " + e.getMessage(), null};
        }
    }

    public Object[] vote(int feedbackId, int userId, VoteRequest.VoteType voteType) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            VoteRequest voteRequest = new VoteRequest(feedbackId, userId, voteType);
            connection.sendVoteRequest(voteRequest);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error processing vote: " + e.getMessage(), null};
        }
    }

    public Object[] vote(VoteRequest voteRequest) {
        if (!connection.isConnected()) {
            return new Object[]{"400", "Not connected to server", null};
        }

        try {
            connection.sendVoteRequest(voteRequest);
            Object response = connection.receive();

            if (response instanceof Object[]) {
                return (Object[]) response;
            }

            return new Object[]{"500", "Invalid server response", null};
        } catch (Exception e) {
            return new Object[]{"500", "Error processing vote: " + e.getMessage(), null};
        }
    }
}
