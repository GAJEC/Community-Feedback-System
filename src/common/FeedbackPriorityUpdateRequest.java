package common;

import java.io.Serializable;

public class FeedbackPriorityUpdateRequest implements Serializable {
    private final int feedbackId;
    private final String priority;

    public FeedbackPriorityUpdateRequest(int feedbackId, String priority) {
        this.feedbackId = feedbackId;
        this.priority = priority;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public String getPriority() {
        return priority;
    }
}
