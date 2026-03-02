package client.controller;

import client.model.ClientConnection;
import client.model.CredentialManager;
import client.model.LoadingModel;
import client.view.auth.Loading;
import common.User;

import javax.swing.*;
import java.awt.EventQueue;

import static common.Config.CONFIG_DEBUG;

public class LoadingController {
    private final ClientConnection connection;
    private final LoadingModel model;
    private final Loading view;

    public LoadingController(ClientConnection connection) {
        this.connection = connection;
        this.model = new LoadingModel(connection);
        this.view = new Loading();
    }

    public void show() {
        view.showLoading();
        startAutoLogin();
    }

    private void startAutoLogin() {
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                String[] credentials = CredentialManager.loadCredentials();
                User loggedInUser = model.tryAutoLogin(credentials);

                if (loggedInUser == null) {
                    CredentialManager.deleteCredentials();
                }

                Thread.sleep(3200);
                return loggedInUser;
            }

            @Override
            protected void done() {
                view.startFadeOut(() -> EventQueue.invokeLater(() -> {
                    try {
                        User loggedInUser = get();

                        if (loggedInUser != null) {
                            if ("PENDING".equalsIgnoreCase(loggedInUser.getVerify_status())) {
                                CredentialManager.deleteCredentials();
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Your account is still pending admin approval.",
                                        "Account Pending",
                                        JOptionPane.WARNING_MESSAGE
                                );
                                new AuthScreenController(connection).show();
                                return;
                            }

                            switch (loggedInUser.getAttr_type()) {
                                case 0 -> new AdminDashboardController(connection).show();
                                case 1 -> new BrgyOfficialDashboardController(connection, loggedInUser).show();
                                default -> new ResidentDashboardController(connection, loggedInUser, true).show();
                            }
                        } else {
                            new AuthScreenController(connection).show();
                        }
                    } catch (Exception e) {
                        if (CONFIG_DEBUG) {
                            e.printStackTrace();
                        }
                        new AuthScreenController(connection).show();
                    }
                }));
            }
        };

        worker.execute();
    }
}
