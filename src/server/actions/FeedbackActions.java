package server.actions;

import common.Feedbacks;
import org.xml.sax.SAXException;
import server.components.XMLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class FeedbackActions {

    public static Object[] handleFeedbackSubmission(Feedbacks feedback, int clientId) {
        try {
            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                Feedbacks.class, 
                "src/server/data/database.xml", 
                "feedback"
            );

            if (feedback.getContent() == null || feedback.getContent().trim().isEmpty()) {
                return new Object[]{"400", "Feedback content is required", null};
            }
            if (feedback.getLocation() == null || feedback.getLocation().trim().isEmpty()) {
                return new Object[]{"400", "Location is required", null};
            }

            int newId = feedbackHandler.getCurrentLength("feedback") + 1;
            feedback.setAttr_id(newId);

            feedbackHandler.Store("feedback", feedback);
            feedbackHandler.saveToXML();

            System.out.println("Feedback #" + newId + " saved from Client " + clientId);

            return new Object[]{"200", "Feedback submitted successfully", feedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Error handling feedback submission: " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] getAllFeedbacks() {
        try {
            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                Feedbacks.class, 
                "src/server/data/database.xml", 
                "feedback"
            );

            Feedbacks[] feedbacks = feedbackHandler.ReadAll();
            
            System.out.println("Retrieved " + feedbacks.length + " feedbacks from database");
            
            return new Object[]{"200", "Feedbacks retrieved successfully", feedbacks};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Error retrieving feedbacks: " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }
}
