package server.actions;

import common.User;
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
        User ResultUser = Login.ReadbyUsername(user.getUsername());

        if(ResultUser == null){
            return new Object[]{"400", "Account not found", null};
        }

        if(ResultUser.getPassword().equals(user.getPassword())){
            return new Object[]{"200", "Login successful", ResultUser};
        }else{
            return new Object[]{"400", "Wrong password", null};
        }
    }
    
    public static boolean validateCredentials(String username, String password) {
        return username != null && !username.isEmpty() 
            && password != null && !password.isEmpty();
    }
}
