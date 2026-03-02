package client.model;

import common.User;

public class LoadingModel {
    private final AuthScreenModel authScreenModel;

    public LoadingModel(ClientConnection connection) {
        this.authScreenModel = new AuthScreenModel(connection);
    }

    public User tryAutoLogin(String[] credentials) {
        if (credentials == null || credentials.length != 2) {
            return null;
        }

        Object[] loginResult = authScreenModel.handleLogin(credentials[0], credentials[1]);
        if (loginResult != null && loginResult.length >= 3) {
            String statusCode = (String) loginResult[0];
            if ("200".equals(statusCode) && loginResult[2] instanceof User) {
                return (User) loginResult[2];
            }
        }
        return null;
    }
}
