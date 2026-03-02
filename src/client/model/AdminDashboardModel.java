package client.model;

import common.BroadcastMessage;
import common.DeleteUserRequest;
import common.FeedbackInternalNoteUpdateRequest;
import common.FeedbackPriorityUpdateRequest;
import common.FeedbackStatusUpdateRequest;
import common.Feedbacks;
import common.User;
import common.UserStatusUpdateRequest;

public class AdminDashboardModel {
    public enum BroadcastUiAction {
        NONE,
        USERS_UPDATED,
        FEEDBACKS_UPDATED
    }

    private final ClientConnection connection;

    public AdminDashboardModel(ClientConnection connection) {
        this.connection = connection;
    }

    public User[] fetchUsers() {
        ensureConnected();
        connection.sendRequest("GET_USERS");
        Object response = connection.receive();
        if (response instanceof Object[] arr && arr.length >= 3 && "200".equals(arr[0]) && arr[2] instanceof User[]) {
            return (User[]) arr[2];
        }
        return new User[0];
    }

    public Feedbacks[] fetchFeedbacks() {
        ensureConnected();
        connection.sendRequest("GET_FEEDBACKS");
        Object response = connection.receive();
        if (response instanceof Object[] arr && arr.length >= 3 && "200".equals(arr[0]) && arr[2] instanceof Feedbacks[]) {
            return (Feedbacks[]) arr[2];
        }
        return new Feedbacks[0];
    }

    public Object[] updateFeedbackStatus(int feedbackId, String status) {
        ensureConnected();
        connection.sendRequest(new FeedbackStatusUpdateRequest(feedbackId, status));
        return normalizeResponse(connection.receive());
    }

    public Object[] updateFeedbackPriority(int feedbackId, String priority) {
        ensureConnected();
        connection.sendRequest(new FeedbackPriorityUpdateRequest(feedbackId, priority));
        return normalizeResponse(connection.receive());
    }

    public Object[] updateFeedbackInternalNote(int feedbackId, String internalNote) {
        ensureConnected();
        connection.sendRequest(new FeedbackInternalNoteUpdateRequest(feedbackId, internalNote));
        return normalizeResponse(connection.receive());
    }

    public Object[] updateUserStatus(int userId, String status) {
        ensureConnected();
        connection.sendRequest(new UserStatusUpdateRequest(userId, status));
        return normalizeResponse(connection.receive());
    }

    public Object[] deleteUser(int userId) {
        ensureConnected();
        connection.sendRequest(new DeleteUserRequest(userId));
        return normalizeResponse(connection.receive());
    }

    public BroadcastUiAction routeBroadcast(BroadcastMessage message) {
        if (message == null || message.getType() == null) {
            return BroadcastUiAction.NONE;
        }

        return switch (message.getType()) {
            case NEW_USER -> BroadcastUiAction.USERS_UPDATED;
            case NEW_FEEDBACK, VOTE_UPDATE, STATUS_UPDATE, INTERNAL_NOTE_UPDATE -> BroadcastUiAction.FEEDBACKS_UPDATED;
            default -> BroadcastUiAction.NONE;
        };
    }

    public User[] applyUserBroadcast(User[] cachedUsers, User incoming) {
        if (incoming == null) {
            return cachedUsers != null ? cachedUsers : new User[0];
        }
        return upsertUser(cachedUsers, incoming);
    }

    public Feedbacks[] applyFeedbackBroadcast(Feedbacks[] cachedFeedbacks, Feedbacks incoming) {
        if (incoming == null) {
            return cachedFeedbacks != null ? cachedFeedbacks : new Feedbacks[0];
        }
        return upsertFeedback(cachedFeedbacks, incoming);
    }

    private Object[] normalizeResponse(Object response) {
        if (response instanceof Object[]) {
            return (Object[]) response;
        }
        return new Object[]{"500", "Invalid server response", null};
    }

    private void ensureConnected() {
        if (!connection.isConnected()) {
            connection.connect();
        }
    }

    private User[] upsertUser(User[] cachedUsers, User incoming) {
        User[] base = cachedUsers != null ? cachedUsers : new User[0];
        for (int i = 0; i < base.length; i++) {
            User existing = base[i];
            if (existing != null && existing.getAttr_id() == incoming.getAttr_id()) {
                base[i] = incoming;
                return base;
            }
        }

        User[] expanded = new User[base.length + 1];
        System.arraycopy(base, 0, expanded, 0, base.length);
        expanded[base.length] = incoming;
        return expanded;
    }

    private Feedbacks[] upsertFeedback(Feedbacks[] cachedFeedbacks, Feedbacks incoming) {
        Feedbacks[] base = cachedFeedbacks != null ? cachedFeedbacks : new Feedbacks[0];
        for (int i = 0; i < base.length; i++) {
            Feedbacks existing = base[i];
            if (existing != null && existing.getAttr_id() == incoming.getAttr_id()) {
                base[i] = incoming;
                return base;
            }
        }

        Feedbacks[] expanded = new Feedbacks[base.length + 1];
        System.arraycopy(base, 0, expanded, 0, base.length);
        expanded[base.length] = incoming;
        return expanded;
    }
}
