package common;

import java.io.Serializable;

public class DeleteUserRequest implements Serializable {
    private final int userId;

    public DeleteUserRequest(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
