package client.controller;

import client.model.ClientConnection;
import client.model.ResidentDashboardModel;
import client.view.common.LogoutPopUp;
import client.view.common.UserProfileDialog;
import client.view.residents.ResidentDashboard;
import common.Feedbacks;
import common.User;
import common.VoteRequest;
import common.Announcement;
import common.BroadcastMessage;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.IntFunction;

public class ResidentDashboardController implements ResidentDashboard.ResidentDashboardListener, LogoutPopUp.LogoutListener {

    private ResidentDashboardController feedbackController;

    public enum BroadcastUiAction {
        NONE,
        APPLY_FILTERS,
        REFRESH_FEEDBACKS,
        REFRESH_ANNOUNCEMENTS
    }

    private final ResidentDashboardModel model;
    private final User user;
    private final ResidentDashboard view;
    private final ClientConnection connection;

    private Feedbacks[] allFeedbacks = new Feedbacks[0];
    private String feedbackLoadErrorMessage;

    public ResidentDashboardController(ClientConnection connection, User user, boolean viewMode) {
        this.connection = connection;
        this.model = new ResidentDashboardModel(connection);
        this.user = user;
        this.view = new ResidentDashboard(viewMode, user, this);

        if (!this.connection.isConnected()) {
            this.connection.connect();
        }
        this.connection.setBroadcastListener(this::handleBroadcast);
    }


    public void show() {
        view.setVisible(true);
    }

    public Object[] submitFeedback(String title, String category, String location, String description) {
        if (title == null || title.trim().isEmpty()) {
            return new Object[]{"400", "Please enter a title for your feedback.", null};
        }

        if (location == null || location.trim().isEmpty()) {
            return new Object[]{"400", "Please enter a location.", null};
        }

        if (description == null || description.trim().isEmpty()) {
            return new Object[]{"400", "Please provide a detailed description.", null};
        }

        return model.submitFeedback(
                user.getAttr_id(),
                category,
                title.trim(),
                location.trim(),
                description.trim()
        );
    }

    public Object[] deleteFeedback(int feedbackId) {
        return feedbackController.deleteFeedback(feedbackId);
    }

    public void loadFeedbacks() {
        Object[] response = model.getAllFeedbacks();
        String statusCode = (String) response[0];
        String message = (String) response[1];

        if ("200".equals(statusCode) && response[2] != null) {
            allFeedbacks = (Feedbacks[]) response[2];
            feedbackLoadErrorMessage = null;
        } else {
            allFeedbacks = new Feedbacks[0];
            feedbackLoadErrorMessage = message;
        }
    }

    @Override
    public void onLoadFeedbacks() {
        loadFeedbacks();
    }

    public void updateFeedbackVotes(Feedbacks updatedFeedback) {
        if (updatedFeedback == null || allFeedbacks == null) {
            return;
        }

        for (Feedbacks cachedFeedback : allFeedbacks) {
            if (cachedFeedback != null && cachedFeedback.getAttr_id() == updatedFeedback.getAttr_id()) {
                cachedFeedback.setAttr_upvote(updatedFeedback.getAttr_upvote());
                cachedFeedback.setAttr_downvote(updatedFeedback.getAttr_downvote());
                break;
            }
        }
    }

    public Object[] vote(int feedbackId, int userId, VoteRequest.VoteType voteType) {
        return model.vote(feedbackId, userId, voteType);
    }

    public BroadcastUiAction routeBroadcast(BroadcastMessage message, boolean isViewMode) {
        if (message == null || message.getType() == null || !isViewMode) {
            return BroadcastUiAction.NONE;
        }

        switch (message.getType()) {
            case VOTE_UPDATE:
                if (message.getData() instanceof Feedbacks updatedFeedback) {
                    updateFeedbackVotes(updatedFeedback);
                    return BroadcastUiAction.APPLY_FILTERS;
                }
                return BroadcastUiAction.NONE;
            case NEW_FEEDBACK:
            case STATUS_UPDATE:
            case PRIORITY_UPDATE:
            case INTERNAL_NOTE_UPDATE:
                return BroadcastUiAction.REFRESH_FEEDBACKS;
            case NEW_ANNOUNCEMENT:
                return BroadcastUiAction.REFRESH_ANNOUNCEMENTS;
            default:
                return BroadcastUiAction.NONE;
        }
    }

    public Announcement[] getLatestAnnouncements(int limit) {
        Object[] response = model.getAnnouncements();
        if (response == null || response.length < 3 || !"200".equals(String.valueOf(response[0])) || !(response[2] instanceof Announcement[] announcements)) {
            return new Announcement[0];
        }

        Arrays.sort(announcements, (a, b) -> {
            Date da = a != null ? a.getPosted_at() : null;
            Date db = b != null ? b.getPosted_at() : null;
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        int safeLimit = Math.max(0, Math.min(limit, announcements.length));
        Announcement[] result = new Announcement[safeLimit];
        System.arraycopy(announcements, 0, result, 0, safeLimit);
        return result;
    }

    public String formatTimeAgo(Date date) {
        if (date == null) {
            return "-";
        }

        long diffMs = Math.max(0L, System.currentTimeMillis() - date.getTime());
        long minutes = diffMs / (60 * 1000);

        if (minutes < 1) {
            return "just now";
        }
        if (minutes < 60) {
            return minutes + "m ago";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "h ago";
        }

        long days = hours / 24;
        return days + "d ago";
    }

    public List<Feedbacks> getFilteredAndSortedFeedbacks(
            String selectedCategory,
            String selectedStatus,
            String query,
            IntFunction<String> userNameProvider
    ) {
        List<Feedbacks> filteredFeedbacks = new ArrayList<>();

        if (allFeedbacks == null || allFeedbacks.length == 0) {
            return filteredFeedbacks;
        }

        String normalizedCategory = selectedCategory == null ? "All Categories" : selectedCategory.trim();
        String normalizedStatus = selectedStatus == null ? "All Status" : selectedStatus.trim();
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();

        for (Feedbacks feedback : allFeedbacks) {
            if (matchesFilters(feedback, normalizedCategory, normalizedStatus, normalizedQuery, userNameProvider)) {
                filteredFeedbacks.add(feedback);
            }
        }

        filteredFeedbacks.sort((a, b) -> Integer.compare(voteScore(b), voteScore(a)));
        return filteredFeedbacks;
    }

    public String getFeedbackLoadErrorMessage() {
        return feedbackLoadErrorMessage;
    }

    public boolean hasFeedbacks() {
        return allFeedbacks != null && allFeedbacks.length > 0;
    }

    @Override
    public void onLogoutRequested(JFrame parent) {
        LogoutPopUp dialog = new LogoutPopUp(parent, this);
        dialog.setVisible(true);
    }

    @Override
    public void onProfileRequested(JFrame parent, User user) {
        UserProfileDialog dialog = new UserProfileDialog(parent, user);
        dialog.setVisible(true);
    }

    @Override
    public void onConfirm(JFrame parent) {
        if (parent != null) {
            parent.dispose();
        }
        new AuthScreenController(connection).show();
    }

    @Override
    public void onCancel() {
        // No-op
    }

    private boolean matchesFilters(
            Feedbacks feedback,
            String selectedCategory,
            String selectedStatus,
            String query,
            IntFunction<String> userNameProvider
    ) {
        if (feedback == null) {
            return false;
        }

        String category = firstNonEmpty(feedback.getCategories(), "General");
        String status = firstNonEmpty(feedback.getAttr_status(), "Pending");
        String title = feedback.getTitle() != null ? feedback.getTitle() : "";
        String content = feedback.getContent() != null ? feedback.getContent() : "";
        String location = feedback.getLocation() != null ? feedback.getLocation() : "";
        String author = userNameProvider != null ? userNameProvider.apply(feedback.getIssued_by()) : "";

        boolean categoryMatches = "All Categories".equalsIgnoreCase(selectedCategory)
                || category.equalsIgnoreCase(selectedCategory);
        boolean statusMatches = "All Status".equalsIgnoreCase(selectedStatus)
                || status.equalsIgnoreCase(selectedStatus);

        if (!categoryMatches || !statusMatches) {
            return false;
        }

        if (query.isEmpty()) {
            return true;
        }

        String searchable = (title + " " + content + " " + location + " " + category + " " + status + " " + author).toLowerCase();
        return searchable.contains(query);
    }

    private int voteScore(Feedbacks feedback) {
        int upvotes = feedback.getAttr_upvote() != null ? feedback.getAttr_upvote().length : 0;
        int downvotes = feedback.getAttr_downvote() != null ? feedback.getAttr_downvote().length : 0;
        return upvotes - downvotes;
    }

    private String firstNonEmpty(String[] values, String fallback) {
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return fallback;
    }


    private void handleBroadcast(BroadcastMessage message) {
        SwingUtilities.invokeLater(() -> {
            BroadcastUiAction action = routeBroadcast(message, view.isViewMode());
            switch (action) {
                case APPLY_FILTERS -> view.onApplyFiltersRequested();
                case REFRESH_FEEDBACKS -> view.onRefreshFeedbacksRequested();
                case REFRESH_ANNOUNCEMENTS -> view.onRefreshAnnouncementsRequested();
                default -> {
                }
            }
        });
    }
}
