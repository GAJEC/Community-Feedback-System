package client.controller;

import client.model.ClientConnection;
import client.model.FeedbackModel;
import common.Feedbacks;
import common.VoteRequest;

import java.util.Date;

public class FeedbackController {
    private final FeedbackModel feedbackModel;
    private final ClientConnection connection;

    public FeedbackController(ClientConnection connection) {
        this.connection = connection;
        this.feedbackModel = new FeedbackModel(connection);
    }

    public Object[] submitFeedback(int userId, String category, String title, String location, String content) {
        if (category == null || category.trim().isEmpty()) {
            return new Object[]{"400", "Category is required", null};
        }
        if (title == null || title.trim().isEmpty()) {
            return new Object[]{"400", "Title is required", null};
        }
        if (location == null || location.trim().isEmpty()) {
            return new Object[]{"400", "Location is required", null};
        }
        if (content == null || content.trim().isEmpty()) {
            return new Object[]{"400", "Description is required", null};
        }

        String[] initialStatus = {"Pending"};
        String[] categories = {category};
        Date currentDate = new Date();
        
        Feedbacks feedback = new Feedbacks(0, userId, 0, initialStatus, categories, title, location, content, currentDate);

        return feedbackModel.submitFeedback(feedback);
    }

    public Object[] getAllFeedbacks() {
        return feedbackModel.getAllFeedbacks();
    }

    public Object[] vote(int feedbackId, int userId, VoteRequest.VoteType voteType) {
        VoteRequest voteRequest = new VoteRequest(feedbackId, userId, voteType);
        return feedbackModel.vote(voteRequest);
    }

    public boolean isConnected() {
        return connection.isConnected();
    }
}
