package server.actions;

import common.User;

public class Login {
    
    public static User handleLogin(User user, int clientId) {
        System.out.println("Received" + user.getUsername());
        System.out.println("Client ID: " + clientId);

        return user;
    }
    
    public static boolean validateCredentials(String username, String password) {
        return username != null && !username.isEmpty() 
            && password != null && !password.isEmpty();
    }
}
