package common;

import java.io.Serializable;

public class VoteRequest implements Serializable {
    private int feedbackId;
    private int userId;
    private VoteType voteType;

    public enum VoteType {
        UPVOTE,
        DOWNVOTE,
        REMOVE_UPVOTE,
        REMOVE_DOWNVOTE
    }

    public VoteRequest(int feedbackId, int userId, VoteType voteType) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.voteType = voteType;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public int getUserId() {
        return userId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    @Override
    public String toString() {
        return "VoteRequest{" +
                "feedbackId=" + feedbackId +
                ", userId=" + userId +
                ", voteType=" + voteType +
                '}';
    }
}
