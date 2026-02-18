package common;

import java.io.Serializable;

public class FeedbackInternalNoteUpdateRequest implements Serializable {
    private final int feedbackId;
    private final String internalNote;

    public FeedbackInternalNoteUpdateRequest(int feedbackId, String internalNote) {
        this.feedbackId = feedbackId;
        this.internalNote = internalNote;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public String getInternalNote() {
        return internalNote;
    }
}
