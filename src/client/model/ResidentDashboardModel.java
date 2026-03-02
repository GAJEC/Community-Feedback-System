package client.model;

import common.Announcement;
import common.Feedbacks;
import common.VoteRequest;

public class ResidentDashboardModel {
    private final FeedbackModel feedbackModel;
    private final BrgyOfficialFeedbackModel announcementModel;

    public ResidentDashboardModel(ClientConnection connection) {
        this.feedbackModel = new FeedbackModel(connection);
        this.announcementModel = new BrgyOfficialFeedbackModel(connection);
    }

    public Object[] submitFeedback(int userId, String category, String title, String location, String content) {
        return feedbackModel.submitFeedback(userId, category, title, location, content);
    }

    public Object[] getAllFeedbacks() {
        return feedbackModel.getAllFeedbacks();
    }

    public Object[] vote(int feedbackId, int userId, VoteRequest.VoteType voteType) {
        return feedbackModel.vote(feedbackId, userId, voteType);
    }

    public Object[] getAnnouncements() {
        return announcementModel.getAnnouncements();
    }
}
