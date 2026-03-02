package client.model;

import common.RegisterRequest;
import common.User;

public class AuthScreenModel {
    private final ClientConnection connection;
    private final LoginModel loginModel;

    public AuthScreenModel(ClientConnection connection) {
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

    public Object[] handleRegister(String fullName, String location, String phone, String username, String password, String idFileName, String idFileBase64) {
        if (fullName == null || fullName.trim().isEmpty() ||
                location == null || location.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                idFileName == null || idFileName.trim().isEmpty() ||
                idFileBase64 == null || idFileBase64.trim().isEmpty()) {
            return new Object[]{"400", "Missing registration fields", null};
        }

        if (!connection.isConnected()) {
            if (!connection.connect()) {
                return new Object[]{"500", "Connection Failed!", null};
            }
        }

        RegisterRequest request = new RegisterRequest(
                fullName.trim(),
                location.trim(),
                phone.trim(),
                username.trim(),
                password,
                idFileName.trim(),
                idFileBase64.trim()
        );

        connection.sendRegisterRequest(request);
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
}
