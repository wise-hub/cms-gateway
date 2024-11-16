package bg.fibank.cmsgateway.model;

import java.util.List;

public class UserMetadata {
    private final int userId;
    private final List<String> userRoles;

    public UserMetadata(int userId, List<String> userRoles) {
        this.userId = userId;
        this.userRoles = userRoles;
    }

    public int getUserId() {
        return userId;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }
}