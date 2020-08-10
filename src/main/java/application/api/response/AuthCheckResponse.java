package application.api.response;

import application.model.User;

public class AuthCheckResponse {

    private Boolean result;
    private User user;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
