package client.controller;

import client.model.ClientConnection;
import client.model.Login;
import common.User;

import javax.swing.*;

public class LoginController {
    private final ClientConnection connection;
    private final Login loginModel;

    public LoginController(ClientConnection connection) {
        this.connection = connection;
        this.loginModel = new Login();
    }

// Chill first
    public boolean handleLogin(String username, String password) {
        loginModel.setUsername(username);
        loginModel.setPassword(password);

        if (!loginModel.isValid()) {
            return false;
        }

        if (!connection.isConnected()) {
            if (!connection.connect()) {
                return false;
            }
        }

        // Check if meron karga fields
        if (!loginModel.isValid()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields", "Login Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        User user = new User(0, username, password, username, "");
        connection.sendUser(user);

        Object response = connection.receive();
        return response instanceof User;
    }

    public User getResponseUser(String username, String password) {
        loginModel.setUsername(username);
        loginModel.setPassword(password);

        if (!loginModel.isValid()) {
            return null;
        }

        if (!connection.isConnected()) {
            if (!connection.connect()) {
                return null;
            }
        }

        User user = new User(username, password);
        connection.sendUser(user);

        Object response = connection.receive();
        if (response instanceof User) {
            return (User) response;
        }
        return null;
    }

    public Login getLoginModel() {
        return loginModel;
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public void disconnect() {
        connection.disconnect();
    }
}
