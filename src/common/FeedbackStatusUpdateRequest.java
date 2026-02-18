package common;

import java.io.Serializable;

public class FeedbackStatusUpdateRequest implements Serializable {
    private final int feedbackId;
    private final String status;

    public FeedbackStatusUpdateRequest(int feedbackId, String status) {
        this.feedbackId = feedbackId;
        this.status = status;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public String getStatus() {
        return status;
    }
}
