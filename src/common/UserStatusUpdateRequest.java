package common;

import java.io.Serializable;

public class UserStatusUpdateRequest implements Serializable {
    private final int userId;
    private final String status;

    public UserStatusUpdateRequest(int userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }
}
