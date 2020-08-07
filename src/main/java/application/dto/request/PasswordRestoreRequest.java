package application.dto.request;

public class PasswordRestoreRequest {

    private String email;

    public PasswordRestoreRequest() {
        super();
    }

    public PasswordRestoreRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
