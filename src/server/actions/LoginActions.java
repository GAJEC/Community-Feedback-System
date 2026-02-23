package server.actions;

import common.RegisterRequest;
import common.User;
import common.UserIdImage;
import org.xml.sax.SAXException;
import server.Server;
import server.components.XMLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoginActions {
    public static Object[] handleLogin(User user, int clientId) {
        try {
            XMLHandler<User> loginHandler = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");

            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Login Failed", "Username not provided");
                return new Object[]{"400", "Username is not given!", null};
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Login Failed", "Password not provided");
                return new Object[]{"400", "Password is not given!", null};
            }

            User resultUser = loginHandler.Read("username", user.getUsername().trim());

            if (resultUser == null) {
                Server.logEvent("Client " + clientId, "Login Failed", "Account not found for username: " + user.getUsername());
                return new Object[]{"400", "Account not found", null};
            }

            if (resultUser.getPassword().equals(user.getPassword())) {
                Server.logEvent("Client " + clientId, "Login", "User " + user.getUsername() + " logged in");
                return new Object[]{"200", "Login successful", resultUser};
            } else {
                Server.logEvent("Client " + clientId, "Login Failed", "Wrong password for user " + user.getUsername());
                return new Object[]{"400", "Wrong password", null};
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent("Client " + clientId, "Login Exception", e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] handleRegister(RegisterRequest request, int clientId) {
        try {
            if (request == null) {
                Server.logEvent("Client " + clientId, "Register Failed", "Invalid registration request");
                return new Object[]{"400", "Invalid registration request", null};
            }

            XMLHandler<User> loginHandler = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            XMLHandler<UserIdImage> idHandler = new XMLHandler<>(UserIdImage.class, "src/server/data/id_images.xml", "useridimage");

            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Register Failed", "Full name is required");
                return new Object[]{"400", "Full name is required", null};
            }
            if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Register Failed", "Location is required");
                return new Object[]{"400", "Location is required", null};
            }
            if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Register Failed", "Phone is required");
                return new Object[]{"400", "Phone is required", null};
            }
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Register Failed", "Username is required");
                return new Object[]{"400", "Username is required", null};
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Register Failed", "Password is required");
                return new Object[]{"400", "Password is required", null};
            }
            if (request.getIdFileName() == null || request.getIdFileName().trim().isEmpty() ||
                    request.getIdFileBase64() == null || request.getIdFileBase64().trim().isEmpty()) {
                Server.logEvent("Client " + clientId, "Register Failed", "Valid ID file required");
                return new Object[]{"400", "Valid ID file is required", null};
            }

            User existing = loginHandler.Read("username", request.getUsername().trim());
            if (existing != null) {
                Server.logEvent("Client " + clientId, "Register Failed", "Username already exists: " + request.getUsername());
                return new Object[]{"409", "Username already exists", null};
            }

            int newId = 1;
            User[] users = loginHandler.ReadAll();
            if (users != null) {
                for (User u : users) if (u != null && u.getAttr_id() >= newId) newId = u.getAttr_id() + 1;
            }

            int newImageId = 1;
            UserIdImage[] images = idHandler.ReadAll();
            if (images != null) {
                for (UserIdImage img : images) if (img != null && img.getAttr_id() >= newImageId) newImageId = img.getAttr_id() + 1;
            }

            UserIdImage userIdImage = new UserIdImage(
                    newImageId,
                    newId,
                    request.getIdFileName().trim(),
                    request.getIdFileBase64().trim()
            );
            idHandler.Store(userIdImage);

            User newUser = new User(
                    newId,
                    2,
                    request.getUsername().trim(),
                    request.getPassword(),
                    request.getFullName().trim(),
                    request.getLocation().trim(),
                    new String[]{request.getPhone().trim()},
                    "PENDING",
                    newImageId
            );
            loginHandler.Store(newUser);

            Server.logEvent("Client " + clientId, "Register", "User " + newUser.getUsername() + " registered");

            return new Object[]{"200", "Registration successful. Awaiting admin verification.", newUser};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent("Client " + clientId, "Register Exception", e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] getAllUsers() {
        try {
            XMLHandler<User> loginHandler = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            User[] users = loginHandler.ReadAll();
            Server.logEvent("System", "Retrieve Users", "Retrieved " + (users != null ? users.length : 0) + " users");
            return new Object[]{"200", "Users retrieved successfully", users};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent("System", "Retrieve Users Exception", e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] updateUserStatus(int userId, String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return new Object[]{"400", "Status is required", null};
            }

            String normalized = status.trim().toUpperCase();
            if (!"PENDING".equals(normalized) && !"APPROVED".equals(normalized) && !"ACTIVE".equals(normalized)) {
                return new Object[]{"400", "Invalid status", null};
            }
            if ("ACTIVE".equals(normalized)) normalized = "APPROVED";

            XMLHandler<User> loginHandler = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            User user = loginHandler.Read("id", userId);
            if (user == null) {
                Server.logEvent("System", "Update User Status Failed", "User ID " + userId + " not found");
                return new Object[]{"404", "User not found", null};
            }

            user.setVerify_status(normalized);
            boolean updated = loginHandler.Update("id", userId, user);
            if (!updated) {
                Server.logEvent("System", "Update User Status Failed", "Failed to update user ID " + userId);
                return new Object[]{"500", "Failed to update user status", null};
            }

            Server.logEvent("System", "Update User Status", "User ID " + userId + " status updated to " + normalized);
            return new Object[]{"200", "User status updated", user};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent("System", "Update User Status Exception", e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] deleteUser(int userId) {
        try {
            XMLHandler<User> loginHandler = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            boolean deleted = loginHandler.Delete("id", userId);
            if (!deleted) {
                Server.logEvent("System", "Delete User Failed", "User ID " + userId + " not found");
                return new Object[]{"404", "User not found", null};
            }
            Server.logEvent("System", "Delete User", "User ID " + userId + " deleted");
            return new Object[]{"200", "User deleted", null};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent("System", "Delete User Exception", e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }
}