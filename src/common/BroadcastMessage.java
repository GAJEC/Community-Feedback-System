package common;

import java.io.Serializable;

/**
 * Para sa pag broadcast ng updates sa clients. May type para malaman kung anong klaseng update at data para sa specific info (e.g. updated feedback object)
 */
public class BroadcastMessage implements Serializable {
    private BroadcastType type;
    private Object data;

    public enum BroadcastType {
        VOTE_UPDATE,
        NEW_FEEDBACK,
        STATUS_UPDATE,
        NEW_USER
    }

    public BroadcastMessage(BroadcastType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public BroadcastType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BroadcastMessage{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
