package client.controller;

import client.model.AdminDashboardModel;
import client.model.ClientConnection;
import client.view.admin.AdminDashboard;
import client.view.common.LogoutPopUp;
import common.BroadcastMessage;
import common.Feedbacks;
import common.User;

import javax.swing.*;

public class AdminDashboardController implements AdminDashboard.AdminDashboardListener, LogoutPopUp.LogoutListener {
    private final ClientConnection connection;
    private final AdminDashboardModel model;
    private final AdminDashboard view;
    private User[] cachedUsers = new User[0];
    private Feedbacks[] cachedFeedbacks = new Feedbacks[0];

    public AdminDashboardController(ClientConnection connection) {
        this.connection = connection;
        this.model = new AdminDashboardModel(connection);
        this.view = new AdminDashboard(this);

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
        cachedUsers = model.fetchUsers();
        cachedFeedbacks = model.fetchFeedbacks();
        view.updateData(cachedUsers, cachedFeedbacks);
    }

    private void handleBroadcast(BroadcastMessage message) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboardModel.BroadcastUiAction action = model.routeBroadcast(message);

            if (action == AdminDashboardModel.BroadcastUiAction.USERS_UPDATED && message.getData() instanceof User newUser) {
                cachedUsers = model.applyUserBroadcast(cachedUsers, newUser);
                view.onUsersUpdated(cachedUsers);
                return;
            }

            if (action == AdminDashboardModel.BroadcastUiAction.FEEDBACKS_UPDATED && message.getData() instanceof Feedbacks updated) {
                cachedFeedbacks = model.applyFeedbackBroadcast(cachedFeedbacks, updated);
                view.onFeedbacksUpdated(cachedFeedbacks);
            }
        });
    }
}
