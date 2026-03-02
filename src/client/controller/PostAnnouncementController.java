package client.controller;

import client.model.ClientConnection;
import client.model.PostAnnouncementModel;
import client.view.brgyofficials.PostAnnouncementDialog;

import javax.swing.*;

public class PostAnnouncementController implements PostAnnouncementDialog.PostAnnouncementListener {
    private final ClientConnection connection;
    private final PostAnnouncementModel model;
    private final PostAnnouncementDialog view;

    public PostAnnouncementController(ClientConnection connection, JFrame parent, String userName, int userId) {
        this.connection = connection;
        this.model = new PostAnnouncementModel();
        this.view = new PostAnnouncementDialog(parent, userName, userId, this);
    }

    public void show() {
        view.setVisible(true);
    }

    @Override
    public void onSubmit(int userId, String userName, String subject, String details) {
        if (subject == null || subject.trim().isEmpty()) {
            view.showMessage("Validation", "Please enter an announcement subject.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (details == null || details.trim().isEmpty()) {
            view.showMessage("Validation", "Please enter announcement details.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.setBusy(true);
        SwingWorker<Object[], Void> worker = new SwingWorker<>() {
            @Override
            protected Object[] doInBackground() {
                return model.postAnnouncement(connection, userId, userName, subject, details);
            }

            @Override
            protected void done() {
                view.setBusy(false);
                try {
                    Object[] result = get();
                    if (result != null && result.length >= 2 && "200".equals(String.valueOf(result[0]))) {
                        view.showMessage("Success", "Announcement posted.", JOptionPane.INFORMATION_MESSAGE);
                        view.closeDialog();
                        return;
                    }

                    String message = result != null && result.length > 1 && result[1] != null
                            ? String.valueOf(result[1])
                            : "Failed to post announcement.";
                    view.showMessage("Post Failed", message, JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    view.showMessage("Post Failed", "Failed to post announcement: " + ex.getMessage(), JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    @Override
    public void onCancel() {
        // No-op
    }
}
