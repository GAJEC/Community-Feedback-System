package client.controller;

import client.model.BrgyOfficialDashboardModel;
import client.model.ClientConnection;
import client.view.admin.AdminDashboard;
import client.view.brgyofficials.BrgyOfficialDashboard;
import client.view.common.LogoutPopUp;
import client.view.common.UserProfileDialog;
import common.BroadcastMessage;
import common.Feedbacks;
import common.User;

import javax.swing.*;

public class BrgyOfficialDashboardController implements BrgyOfficialDashboard.BrgyOfficialDashboardListener,
    AdminDashboard.AdminDashboardListener, LogoutPopUp.LogoutListener {

    private final ClientConnection connection;
    private final BrgyOfficialDashboardModel model;
    private final BrgyOfficialDashboard view;
    private User[] cachedUsers = new User[0];
    private Feedbacks[] cachedFeedbacks = new Feedbacks[0];
    private PostAnnouncementController postAnnouncementController;

    public BrgyOfficialDashboardController(ClientConnection connection, User user) {
        this.connection = connection;
        this.model = new BrgyOfficialDashboardModel(connection);
        this.view = new BrgyOfficialDashboard(user, this);

        if (!this.connection.isConnected()) {
            this.connection.connect();
        }
        this.connection.setBroadcastListener(this::handleBroadcast);

        refreshData();
    }

    public void show() {
        view.setVisible(true);
    }

    @Override
    public void onRefreshRequested() {
        refreshData();
    }

    @Override
    public Object[] updateFeedbackStatus(int feedbackId, String status) {
        Object[] response = model.updateFeedbackStatus(feedbackId, status);
        if ("200".equals(String.valueOf(response[0]))) {
            refreshData();
        }
        return response;
    }

    @Override
    public Object[] updateFeedbackPriority(int feedbackId, String priority) {
        Object[] response = model.updateFeedbackPriority(feedbackId, priority);
        if ("200".equals(String.valueOf(response[0]))) {
            refreshData();
        }
        return response;
    }

    @Override
    public Object[] updateFeedbackInternalNote(int feedbackId, String internalNote) {
        Object[] response = model.updateFeedbackInternalNote(feedbackId, internalNote);
        if ("200".equals(String.valueOf(response[0]))) {
            refreshData();
        }
        return response;
    }

    @Override
    public Object[] updateUserStatus(int userId, String status) {
        Object[] response = model.updateUserStatus(userId, status);
        if ("200".equals(String.valueOf(response[0]))) {
            refreshData();
        }
        return response;
    }

    @Override
    public Object[] deleteUser(int userId) {
        Object[] response = model.deleteUser(userId);
        if ("200".equals(String.valueOf(response[0]))) {
            refreshData();
        }
        return response;
    }

    @Override
    public void onLogoutRequested(JFrame parent) {
        LogoutPopUp dialog = new LogoutPopUp(parent, this);
        dialog.setVisible(true);
    }

    @Override
    public void onPostAnnouncementRequested(JFrame parent, String userName, int userId) {
        postAnnouncementController = new PostAnnouncementController(connection, parent, userName, userId);
        postAnnouncementController.show();
    }

    @Override
    public void onProfileRequested(JFrame parent, User user) {
        UserProfileDialog dialog = new UserProfileDialog(parent, user);
        dialog.setVisible(true);
    }

    @Override
    public AdminDashboard.AdminConsoleView createUsersPanel(Runnable onDataChanged) {
        return new AdminDashboard.AdminConsoleView(this, onDataChanged);
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

    private void refreshData() {
        cachedFeedbacks = model.fetchFeedbacks();
        cachedUsers = model.fetchUsers();
        view.updateData(cachedFeedbacks, cachedUsers);
    }

    private void handleBroadcast(BroadcastMessage message) {
        SwingUtilities.invokeLater(() -> {
            BrgyOfficialDashboardModel.BroadcastUiAction action = model.routeBroadcast(message);

            if (action == BrgyOfficialDashboardModel.BroadcastUiAction.FEEDBACKS_UPDATED && message.getData() instanceof Feedbacks feedback) {
                cachedFeedbacks = model.applyFeedbackBroadcast(cachedFeedbacks, feedback);
                view.onFeedbacksUpdated(cachedFeedbacks);
                return;
            }

            if (action == BrgyOfficialDashboardModel.BroadcastUiAction.USERS_UPDATED && message.getData() instanceof User newUser) {
                cachedUsers = model.applyUserBroadcast(cachedUsers, newUser);
                view.onUsersUpdated(cachedUsers);
            }
        });
    }
}
