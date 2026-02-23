package server.actions;

import common.Feedbacks;
import common.VoteRequest;
import org.xml.sax.SAXException;
import server.Server;
import server.components.XMLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FeedbackActions {
    public static Object[] handleFeedbackSubmission(Feedbacks feedback, int clientId) {
        try {
            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                    Feedbacks.class,
                    "src/server/data/database.xml",
                    "feedback"
            );

            if (feedback.getContent() == null || feedback.getContent().trim().isEmpty()) {
                Server.logEvent(
                        "Client " + clientId,
                        "Submit Feedback Failed",
                        "Feedback content is required"
                );
                return new Object[]{"400", "Feedback content is required", null};
            }
            if (feedback.getTitle() == null || feedback.getTitle().trim().isEmpty()) {
                Server.logEvent(
                        "Client " + clientId,
                        "Submit Feedback Failed",
                        "Feedback title is required"
                );
                return new Object[]{"400", "Feedback title is required", null};
            }
            if (feedback.getLocation() == null || feedback.getLocation().trim().isEmpty()) {
                Server.logEvent(
                        "Client " + clientId,
                        "Submit Feedback Failed",
                        "Location is required"
                );
                return new Object[]{"400", "Location is required", null};
            }

            if (feedback.getPriority() == null || feedback.getPriority().trim().isEmpty()) {
                feedback.setPriority("No priority set");
            }
            if (feedback.getInternal_note() == null) {
                feedback.setInternal_note("");
            }

            int newId = feedbackHandler.getCurrentLength("feedback") + 1;
            feedback.setAttr_id(newId);

            feedbackHandler.Store("feedback", feedback);
            feedbackHandler.saveToXML();

            Server.logEvent(
                    "Client " + clientId,
                    "Submit Feedback",
                    "Feedback #" + newId + " submitted"
            );

            return new Object[]{"200", "Feedback submitted successfully", feedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent(
                    "Client " + clientId,
                    "Submit Feedback Exception",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
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

            Server.logEvent(
                    "System",
                    "Retrieve Feedbacks",
                    "Retrieved " + (feedbacks != null ? feedbacks.length : 0) + " feedbacks"
            );

            return new Object[]{"200", "Feedbacks retrieved successfully", feedbacks};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent(
                    "System",
                    "Retrieve Feedbacks Exception",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] handleVote(VoteRequest voteRequest, int clientId) {
        try {
            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                    Feedbacks.class,
                    "src/server/data/database.xml",
                    "feedback"
            );

            Feedbacks[] allFeedbacks = feedbackHandler.ReadAll();
            Feedbacks targetFeedback = null;

            for (Feedbacks fb : allFeedbacks) {
                if (fb.getAttr_id() == voteRequest.getFeedbackId()) {
                    targetFeedback = fb;
                    break;
                }
            }

            if (targetFeedback == null) {
                Server.logEvent(
                        "Client " + clientId,
                        "Vote Feedback Failed",
                        "Feedback #" + voteRequest.getFeedbackId() + " not found"
                );
                return new Object[]{"404", "Feedback not found", null};
            }

            Set<String> upvoteSet = new HashSet<>();
            Set<String> downvoteSet = new HashSet<>();

            if (targetFeedback.getAttr_upvote() != null) {
                for (String id : targetFeedback.getAttr_upvote()) if (id != null && !id.isEmpty()) upvoteSet.add(id);
            }
            if (targetFeedback.getAttr_downvote() != null) {
                for (String id : targetFeedback.getAttr_downvote()) if (id != null && !id.isEmpty()) downvoteSet.add(id);
            }

            String userId = String.valueOf(voteRequest.getUserId());

            switch (voteRequest.getVoteType()) {
                case UPVOTE -> {
                    if (upvoteSet.contains(userId)) upvoteSet.remove(userId);
                    else { downvoteSet.remove(userId); upvoteSet.add(userId); }
                }
                case DOWNVOTE -> {
                    if (downvoteSet.contains(userId)) downvoteSet.remove(userId);
                    else { upvoteSet.remove(userId); downvoteSet.add(userId); }
                }
                case REMOVE_UPVOTE -> { upvoteSet.remove(userId); downvoteSet.remove(userId); }
                case REMOVE_DOWNVOTE -> { downvoteSet.remove(userId); upvoteSet.remove(userId); }
            }

            targetFeedback.setAttr_upvote(upvoteSet.toArray(new String[0]));
            targetFeedback.setAttr_downvote(downvoteSet.toArray(new String[0]));

            boolean updated = feedbackHandler.Update("id", voteRequest.getFeedbackId(), targetFeedback);

            if (!updated) {
                Server.logEvent(
                        "Client " + clientId,
                        "Vote Feedback Failed",
                        "Failed to update Feedback #" + voteRequest.getFeedbackId()
                );
                return new Object[]{"500", "Failed to update feedback in database", null};
            }

            Server.logEvent(
                    "Client " + clientId,
                    "Vote Feedback",
                    "Feedback #" + voteRequest.getFeedbackId() + " (" + voteRequest.getVoteType() + ")"
            );

            return new Object[]{"200", "Vote recorded successfully", targetFeedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent(
                    "Client " + clientId,
                    "Vote Feedback Exception",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] updateFeedbackStatus(int feedbackId, String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                Server.logEvent(
                        "System",
                        "Update Feedback Status Failed",
                        "Feedback #" + feedbackId + " missing status"
                );
                return new Object[]{"400", "Status is required", null};
            }

            String normalized = status.trim();
            if (!("Pending".equalsIgnoreCase(normalized) ||
                    "In Progress".equalsIgnoreCase(normalized) ||
                    "Resolved".equalsIgnoreCase(normalized) ||
                    "Archived".equalsIgnoreCase(normalized))) {
                Server.logEvent(
                        "System",
                        "Update Feedback Status Failed",
                        "Feedback #" + feedbackId + " invalid status: " + normalized
                );
                return new Object[]{"400", "Invalid status", null};
            }

            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                    Feedbacks.class,
                    "src/server/data/database.xml",
                    "feedback"
            );

            Feedbacks feedback = feedbackHandler.Read("id", feedbackId);
            if (feedback == null) {
                Server.logEvent(
                        "System",
                        "Update Feedback Status Failed",
                        "Feedback #" + feedbackId + " not found"
                );
                return new Object[]{"404", "Feedback not found", null};
            }

            feedback.setAttr_status(new String[]{normalized});
            boolean updated = feedbackHandler.Update("id", feedbackId, feedback);

            if (!updated) {
                Server.logEvent(
                        "System",
                        "Update Feedback Status Failed",
                        "Failed to update Feedback #" + feedbackId
                );
                return new Object[]{"500", "Failed to update feedback status", null};
            }

            Server.logEvent(
                    "System",
                    "Update Feedback Status",
                    "Feedback #" + feedbackId + " status updated to " + normalized
            );

            return new Object[]{"200", "Feedback status updated", feedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent(
                    "System",
                    "Update Feedback Status Exception",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] updateFeedbackPriority(int feedbackId, String priority) {
        try {
            if (priority == null || priority.trim().isEmpty()) {
                Server.logEvent(
                        "System",
                        "Update Feedback Priority Failed",
                        "Feedback #" + feedbackId + " missing priority"
                );
                return new Object[]{"400", "Priority is required", null};
            }

            String normalized = priority.trim();
            if (!("Urgent".equalsIgnoreCase(normalized) ||
                    "High".equalsIgnoreCase(normalized) ||
                    "Medium".equalsIgnoreCase(normalized) ||
                    "Low".equalsIgnoreCase(normalized) ||
                    "No priority set".equalsIgnoreCase(normalized))) {
                Server.logEvent(
                        "System",
                        "Update Feedback Priority Failed",
                        "Feedback #" + feedbackId + " invalid priority: " + normalized
                );
                return new Object[]{"400", "Invalid priority", null};
            }

            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                    Feedbacks.class,
                    "src/server/data/database.xml",
                    "feedback"
            );

            Feedbacks feedback = feedbackHandler.Read("id", feedbackId);
            if (feedback == null) {
                Server.logEvent(
                        "System",
                        "Update Feedback Priority Failed",
                        "Feedback #" + feedbackId + " not found"
                );
                return new Object[]{"404", "Feedback not found", null};
            }

            feedback.setPriority(normalized);
            boolean updated = feedbackHandler.Update("id", feedbackId, feedback);

            if (!updated) {
                Server.logEvent(
                        "System",
                        "Update Feedback Priority Failed",
                        "Failed to update Feedback #" + feedbackId
                );
                return new Object[]{"500", "Failed to update feedback priority", null};
            }

            Server.logEvent(
                    "System",
                    "Update Feedback Priority",
                    "Feedback #" + feedbackId + " priority updated to " + normalized
            );

            return new Object[]{"200", "Feedback priority updated", feedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent(
                    "System",
                    "Update Feedback Priority Exception",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }

    public static Object[] updateFeedbackInternalNote(int feedbackId, String internalNote) {
        try {
            String normalized = internalNote == null ? "" : internalNote.trim();

            XMLHandler<Feedbacks> feedbackHandler = new XMLHandler<>(
                    Feedbacks.class,
                    "src/server/data/database.xml",
                    "feedback"
            );

            Feedbacks feedback = feedbackHandler.Read("id", feedbackId);
            if (feedback == null) {
                Server.logEvent(
                        "System",
                        "Update Feedback Internal Note Failed",
                        "Feedback #" + feedbackId + " not found"
                );
                return new Object[]{"404", "Feedback not found", null};
            }

            feedback.setInternal_note(normalized);
            boolean updated = feedbackHandler.Update("id", feedbackId, feedback);

            if (!updated) {
                Server.logEvent(
                        "System",
                        "Update Feedback Internal Note Failed",
                        "Failed to update Feedback #" + feedbackId
                );
                return new Object[]{"500", "Failed to update feedback internal note", null};
            }

            Server.logEvent(
                    "System",
                    "Update Feedback Internal Note",
                    "Feedback #" + feedbackId + " internal note updated"
            );

            return new Object[]{"200", "Feedback internal note updated", feedback};

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Server.logEvent(
                    "System",
                    "Update Feedback Internal Note Exception",
                    e.getClass().getSimpleName() + ": " + e.getMessage()
            );
            return new Object[]{"500", "Server error: " + e.getMessage(), null};
        }
    }
}