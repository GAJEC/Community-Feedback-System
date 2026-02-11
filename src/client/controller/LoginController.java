package client.controller;

import client.model.ClientConnection;
import client.model.LoginModel;
import common.User;

public class LoginController {
    private final ClientConnection connection;
    private final LoginModel loginModel;

    public LoginController(ClientConnection connection) {
        this.connection = connection;
        this.loginModel = new LoginModel();
    }

    public Object[] handleLogin(String username, String password) {
        loginModel.setUsername(username);
        loginModel.setPassword(password);

        if (!loginModel.isValid()) {
            return new Object[]{"400", "Missing Fields!", null};
        }

        if (!connection.isConnected()) {
            if (!connection.connect()) {
                return new Object[]{"500", "Connection Failed!", null};
            }
        }

        User user = new User(username, password);
        connection.sendUser(user);

        Object response = connection.receive();

        if (response instanceof Object[]) {
            return (Object[]) response;
        }

        if (response instanceof String[]) {
            String[] status = (String[]) response;
            return new Object[]{status[0], status.length > 1 ? status[1] : "Unknown error", null};
        }

        return new Object[]{"500", "Unknown Response", null};
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

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public void disconnect() {
        connection.disconnect();
    }
}
