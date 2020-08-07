package application.dto.response;

public class PasswordRestoreResponse {

    private boolean result;

    public PasswordRestoreResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
