package server;
import common.Users;
import org.xml.sax.SAXException;
import server.components.XMLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, InterruptedException {

        int CurrentID;
        //Sample implementation
        XMLHandler<Users> xml = new XMLHandler<>(Users.class, "res/database.xml", "user");
        CurrentID = xml.getCurrentLength("user");

        //Create
        xml.Store(new Users(CurrentID, "Gereme", "Mary Heights"));

        //Read
        System.out.println("Created: " + xml.Read("" + CurrentID));

        //Update
        xml.Update(String.valueOf(CurrentID), new Users(CurrentID, "Merege", "Heights"));
        System.out.println("Updated: " + xml.Read("" + CurrentID));

        Thread.sleep(6000);

        //Delete
        xml.Delete(String.valueOf(CurrentID));
        System.out.println("Deleted: " + xml.Read("" + CurrentID));
    }
}