package server.actions;

import common.Feedbacks;
import common.VoteRequest;
import org.xml.sax.SAXException;
import server.components.XMLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FeedbackActions {

    // Pang handle ng feedback submission. Returns new feedback object if successful, or error message if failed
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
            if (feedback.getTitle() == null || feedback.getTitle().trim().isEmpty()) {
                return new Object[]{"400", "Feedback title is required", null};
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

    // Para makuha naten lahat ng feedbacks. Returns array of feedback objects, or error message if may problema
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

    // Pang handle ng vote request. Returns updated feedback object if successful, or error message if failed
    public static Object[] handleVote(VoteRequest voteRequest, int clientId) {
        try {
            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                Feedbacks.class,
                "src/server/data/database.xml",
                "feedback"
            );

            Feedbacks[] allFeedbacks = feedbackHandler.ReadAll();
            Feedbacks targetFeedback = null;

            for (int i = 0; i < allFeedbacks.length; i++) {
                if (allFeedbacks[i].getAttr_id() == voteRequest.getFeedbackId()) {
                    targetFeedback = allFeedbacks[i];
                    break;
                }
            }

            if (targetFeedback == null) {
                return new Object[]{"404", "Feedback not found", null};
            }

            String[] currentUpvotes = targetFeedback.getAttr_upvote();
            String[] currentDownvotes = targetFeedback.getAttr_downvote();

            Set<String> upvoteSet = new HashSet<>();
            Set<String> downvoteSet = new HashSet<>();

            if (currentUpvotes != null) {
                for (String id : currentUpvotes) {
                    if (id != null && !id.trim().isEmpty()) {
                        upvoteSet.add(id.trim());
                    }
                }
            }

            if (currentDownvotes != null) {
                for (String id : currentDownvotes) {
                    if (id != null && !id.trim().isEmpty()) {
                        downvoteSet.add(id.trim());
                    }
                }
            }

            String userId = String.valueOf(voteRequest.getUserId());

            switch (voteRequest.getVoteType()) {
                case UPVOTE:
                    if (upvoteSet.contains(userId)) {
                        upvoteSet.remove(userId);
                    } else {
                        downvoteSet.remove(userId);
                        upvoteSet.add(userId);
                    }
                    break;
                case DOWNVOTE:
                    if (downvoteSet.contains(userId)) {
                        downvoteSet.remove(userId);
                    } else {
                        upvoteSet.remove(userId);
                        downvoteSet.add(userId);
                    }
                    break;
                case REMOVE_UPVOTE:
                    upvoteSet.remove(userId);
                    downvoteSet.remove(userId);
                    break;
                case REMOVE_DOWNVOTE:
                    downvoteSet.remove(userId);
                    upvoteSet.remove(userId);
                    break;
            }

            String[] newUpvotes = upvoteSet.toArray(new String[0]);
            String[] newDownvotes = downvoteSet.toArray(new String[0]);

            targetFeedback.setAttr_upvote(newUpvotes);
            targetFeedback.setAttr_downvote(newDownvotes);

            boolean updated = feedbackHandler.Update("id", voteRequest.getFeedbackId(), targetFeedback);
            
            if (!updated) {
                return new Object[]{"500", "Failed to update feedback in database", null};
            }

            System.out.println("Vote processed for Feedback #" + voteRequest.getFeedbackId() + 
                             " by User #" + userId + " (" + voteRequest.getVoteType() + ")");

            return new Object[]{"200", "Vote recorded successfully", targetFeedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Error handling vote: " + e.getMessage());
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] updateFeedbackStatus(int feedbackId, String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return new Object[]{"400", "Status is required", null};
            }

            String normalized = status.trim();
            if (!("Pending".equalsIgnoreCase(normalized)
                    || "In Progress".equalsIgnoreCase(normalized)
                    || "Resolved".equalsIgnoreCase(normalized)
                    || "Archived".equalsIgnoreCase(normalized))) {
                return new Object[]{"400", "Invalid status", null};
            }

            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                    Feedbacks.class,
                    "src/server/data/database.xml",
                    "feedback"
            );

            Feedbacks feedback = feedbackHandler.Read("id", feedbackId);
            if (feedback == null) {
                return new Object[]{"404", "Feedback not found", null};
            }

            feedback.setAttr_status(new String[]{normalized});
            boolean updated = feedbackHandler.Update("id", feedbackId, feedback);
            if (!updated) {
                return new Object[]{"500", "Failed to update feedback status", null};
            }

            return new Object[]{"200", "Feedback status updated", feedback};
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }
}
