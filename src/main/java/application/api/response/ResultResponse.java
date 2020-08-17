package application.api.response;

public class ResultResponse {

    boolean result;

    public ResultResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
