package application.exception.apierror;

import com.fasterxml.jackson.annotation.JsonInclude;


public class ApiError {

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ApiValidationError errors;

    public ApiError() {
    }

    public ApiError(ApiValidationError errors) {
        this.errors = errors;
    }

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(boolean result,ApiValidationError errors) {
        this.result = result;
        this.errors = errors;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ApiValidationError getErrors() {
        return errors;
    }

    public void setErrors(ApiValidationError errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                '}';
    }
}
