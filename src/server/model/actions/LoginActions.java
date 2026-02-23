package server.model.actions;

import common.RegisterRequest;
import common.User;
import common.UserIdImage;
import org.xml.sax.SAXException;
import server.components.XMLHandler;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoginActions {

    public static Object[] handleLogin(User user, int clientId) throws ParserConfigurationException, IOException, SAXException {
        XMLHandler<User> Login = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");

        if (user.getUsername().isEmpty()) {
            return new Object[]{"400", "Username is not given!", null};
        }
        if (user.getPassword().isEmpty()) {
            return new Object[]{"400", "Password is not given!", null};
        }
        User ResultUser = Login.Read("username", user.getUsername());

        if(ResultUser == null){
            return new Object[]{"400", "Account not found", null};
        }

        if(ResultUser.getPassword().equals(user.getPassword())){
            return new Object[]{"200", "Login successful", ResultUser};
        }else{
            return new Object[]{"400", "Wrong password", null};
        }
    }

    public static Object[] handleRegister(RegisterRequest request, int clientId) throws ParserConfigurationException, IOException, SAXException {
        XMLHandler<User> login = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
        XMLHandler<UserIdImage> idImages = new XMLHandler<>(UserIdImage.class, "src/server/data/id_images.xml", "useridimage");

        if (request == null) {
            return new Object[]{"400", "Invalid registration request", null};
        }

        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            return new Object[]{"400", "Full name is required", null};
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            return new Object[]{"400", "Location is required", null};
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return new Object[]{"400", "Phone is required", null};
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return new Object[]{"400", "Username is required", null};
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return new Object[]{"400", "Password is required", null};
        }
        if (request.getIdFileName() == null || request.getIdFileName().trim().isEmpty()) {
            return new Object[]{"400", "Valid ID file is required", null};
        }
        if (request.getIdFileBase64() == null || request.getIdFileBase64().trim().isEmpty()) {
            return new Object[]{"400", "Valid ID content is required", null};
        }

        User existing = login.Read("username", request.getUsername().trim());
        if (existing != null) {
            return new Object[]{"409", "Username already exists", null};
        }

        int maxId = 0;
        User[] users = login.ReadAll();
        if (users != null) {
            for (User user : users) {
                if (user != null && user.getAttr_id() > maxId) {
                    maxId = user.getAttr_id();
                }
            }
        }
        int newId = maxId + 1;

        int maxImageId = 0;
        UserIdImage[] images = idImages.ReadAll();
        if (images != null) {
            for (UserIdImage image : images) {
                if (image != null && image.getAttr_id() > maxImageId) {
                    maxImageId = image.getAttr_id();
                }
            }
        }
        int newImageId = maxImageId + 1;

        UserIdImage userIdImage = new UserIdImage(
                newImageId,
                newId,
                request.getIdFileName().trim(),
                request.getIdFileBase64().trim()
        );

        idImages.Store(userIdImage);

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

        login.Store(newUser);

        return new Object[]{"200", "Registration successful. Awaiting admin verification.", newUser};
    }
    
    public static boolean validateCredentials(String username, String password) {
        return username != null && !username.isEmpty() 
            && password != null && !password.isEmpty();
    }

    public static Object[] getAllUsers() {
        try {
            XMLHandler<User> login = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            User[] users = login.ReadAll();
            return new Object[]{"200", "Users retrieved successfully", users};
        } catch (ParserConfigurationException | IOException | SAXException e) {
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
            if ("ACTIVE".equals(normalized)) {
                normalized = "APPROVED";
            }

            XMLHandler<User> login = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            User user = login.Read("id", userId);
            if (user == null) {
                return new Object[]{"404", "User not found", null};
            }

            user.setVerify_status(normalized);
            boolean updated = login.Update("id", userId, user);
            if (!updated) {
                return new Object[]{"500", "Failed to update user status", null};
            }

            return new Object[]{"200", "User status updated", user};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] deleteUser(int userId) {
        try {
            XMLHandler<User> login = new XMLHandler<>(User.class, "src/server/data/database.xml", "user");
            boolean deleted = login.Delete("id", userId);
            if (!deleted) {
                return new Object[]{"404", "User not found", null};
            }
            return new Object[]{"200", "User deleted", null};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }
}
