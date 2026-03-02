package client.controller;

import client.model.AuthScreenModel;
import client.model.ClientConnection;
import client.model.CredentialManager;
import client.view.auth.AuthScreen;
import common.User;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class AuthScreenController implements AuthScreen.AuthScreenListener {
    private final ClientConnection connection;
    private final AuthScreenModel model;
    private final AuthScreen view;

    public AuthScreenController(ClientConnection connection) {
        this.connection = connection;
        this.model = new AuthScreenModel(connection);
        this.view = new AuthScreen(this);
    }

    public void show() {
        view.setVisible(true);
    }

    @Override
    public void onLogin(AuthScreen.LoginData data) {
        if (data == null) {
            return;
        }

        Object[] result = model.handleLogin(data.getUsername(), data.getPassword());
        boolean success = result != null && result.length >= 3 && "200".equals(String.valueOf(result[0]));
        User loggedInUser = success && result[2] instanceof User ? (User) result[2] : null;

        if (success && loggedInUser != null) {
            if ("PENDING".equalsIgnoreCase(loggedInUser.getVerify_status())) {
                CredentialManager.deleteCredentials();
                view.showMessage("Account Pending", "Your account is still pending admin approval.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (data.isRememberMe()) {
                CredentialManager.saveCredentials(data.getUsername(), data.getPassword());
            } else {
                CredentialManager.deleteCredentials();
            }

            view.close();
            routeUser(loggedInUser);
            return;
        }

        String error = result != null && result.length > 1 ? String.valueOf(result[1]) : "Login failed.";
        view.showMessage("Login Error", "Login failed: " + error, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onRegister(AuthScreen.RegisterData data) {
        if (data == null) {
            return;
        }

        if (isBlank(data.getFullName()) || isBlank(data.getLocation()) || isBlank(data.getPhone())
                || isBlank(data.getUsername()) || isBlank(data.getPassword())
                || isBlank(data.getConfirmPassword()) || data.getIdFile() == null) {
            view.showMessage("Validation Error", "Please complete all fields and upload a valid ID.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!data.getPassword().equals(data.getConfirmPassword())) {
            view.showMessage("Validation Error", "Password and Confirm Password do not match.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idBase64 = encodeFileToBase64(data.getIdFile());
        if (idBase64.isEmpty()) {
            view.showMessage("File Error", "Failed to read the selected ID image.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] result = model.handleRegister(
                data.getFullName(),
                data.getLocation(),
                data.getPhone(),
                data.getUsername(),
                data.getPassword(),
                data.getIdFile().getName(),
                idBase64
        );

        if (result != null && "200".equals(String.valueOf(result[0]))) {
            view.showMessage("Registration Complete", String.valueOf(result[1]), JOptionPane.INFORMATION_MESSAGE);
            view.showLoginPanel();
        } else {
            String msg = result != null && result.length > 1 ? String.valueOf(result[1]) : "Registration failed.";
            view.showMessage("Registration Error", msg, JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onRequestShowRegister() {
        view.showRegisterPanel();
    }

    @Override
    public void onRequestShowLogin() {
        view.showLoginPanel();
    }

    private void routeUser(User user) {
        if (user.getAttr_type() == 0) {
            AdminDashboardController controller = new AdminDashboardController(connection);
            controller.show();
        } else if (user.getAttr_type() == 1) {
            BrgyOfficialDashboardController controller = new BrgyOfficialDashboardController(connection, user);
            controller.show();
        } else {
            ResidentDashboardController controller = new ResidentDashboardController(connection, user, true);
            controller.show();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String encodeFileToBase64(File file) {
        try {
            if (file == null || !file.exists() || !file.isFile()) {
                return "";
            }
            byte[] bytes = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return "";
        }
    }
}
