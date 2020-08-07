package application.dto.response;

public class LoginResponse {

    private boolean result;
    private UserByLoginResponse user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserByLoginResponse getUser() {
        return user;
    }

    public void setUser(UserByLoginResponse user) {
        this.user = user;
    }
}
