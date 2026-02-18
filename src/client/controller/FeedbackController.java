package client.controller;

import client.model.ClientConnection;
import client.model.FeedbackModel;
import client.model.AdminFeedbackModel;
import client.model.BrgyOfficialFeedbackModel;
import common.VoteRequest;

public class FeedbackController {
    private final FeedbackModel feedbackModel;
    private final AdminFeedbackModel adminFeedbackModel;
    private final BrgyOfficialFeedbackModel brgyOfficialFeedbackModel;
    private final ClientConnection connection;

    public FeedbackController(ClientConnection connection) {
        this.connection = connection;
        this.feedbackModel = new FeedbackModel(connection);
        this.adminFeedbackModel = new AdminFeedbackModel(connection);
        this.brgyOfficialFeedbackModel = new BrgyOfficialFeedbackModel(connection);
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

        return feedbackModel.submitFeedback(userId, category, title, location, content);
    }

    public Object[] getAllFeedbacks() {
        return feedbackModel.getAllFeedbacks();
    }

    public Object[] upvoteFeedback(int feedbackId, int userId) {
        return feedbackModel.vote(feedbackId, userId, VoteRequest.VoteType.UPVOTE);
    }

    public Object[] downvoteFeedback(int feedbackId, int userId) {
        return feedbackModel.vote(feedbackId, userId, VoteRequest.VoteType.DOWNVOTE);
    }

    public Object[] vote(int feedbackId, int userId, VoteRequest.VoteType voteType) {
        return feedbackModel.vote(feedbackId, userId, voteType);
    }

    public Object[] getFeedbackDetail(int feedbackId) {
        return feedbackModel.getFeedbackById(feedbackId);
    }

    public Object[] adminGetAllFeedbacks() {
        return adminFeedbackModel.getAllFeedbacks();
    }

    public Object[] updateFeedbackStatus(int feedbackId, String newStatus) {
        return adminFeedbackModel.updateFeedbackStatus(feedbackId, newStatus);
    }

    public Object[] updateFeedbackPriority(int feedbackId, String newPriority) {
        return adminFeedbackModel.updateFeedbackPriority(feedbackId, newPriority);
    }

    public Object[] addInternalNote(int feedbackId, String note) {
        return adminFeedbackModel.updateInternalNote(feedbackId, note);
    }

    public Object[] filterByStatus(String status) {
        return adminFeedbackModel.getFeedbacksByStatus(status);
    }

    public Object[] searchFeedbacks(String keyword) {
        return adminFeedbackModel.searchFeedbacks(keyword);
    }

    public Object[] brgyGetAllFeedbacks() {
        return brgyOfficialFeedbackModel.getAllFeedbacks();
    }

    public Object[] getFeedbackStatistics() {
        return brgyOfficialFeedbackModel.getFeedbackStatistics();
    }

    public Object[] brgyUpdateFeedbackStatus(int feedbackId, String newStatus) {
        return brgyOfficialFeedbackModel.updateFeedbackStatus(feedbackId, newStatus);
    }

    public Object[] brgyUpdateFeedbackPriority(int feedbackId, String newPriority) {
        return brgyOfficialFeedbackModel.updateFeedbackPriority(feedbackId, newPriority);
    }

    public Object[] brgyAddInternalNote(int feedbackId, String note) {
        return brgyOfficialFeedbackModel.updateInternalNote(feedbackId, note);
    }

    public Object[] brgyFilterByStatus(String status) {
        return brgyOfficialFeedbackModel.getFeedbacksByStatus(status);
    }

    public Object[] filterByCategory(String category) {
        return brgyOfficialFeedbackModel.getFeedbacksByCategory(category);
    }

    public Object[] postAnnouncement(int postedBy, String postedByName, String title, String content) {
        return brgyOfficialFeedbackModel.postAnnouncement(postedBy, postedByName, title, content);
    }

    public Object[] getAnnouncements() {
        return brgyOfficialFeedbackModel.getAnnouncements();
    }

    public boolean isConnected() {
        return connection.isConnected();
    }
}
