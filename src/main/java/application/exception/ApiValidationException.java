package application.exception;

import application.exception.apierror.ApiValidationError;

public class ApiValidationException extends RuntimeException {

    private ApiValidationError errors;

    public ApiValidationException(ApiValidationError errors) {
        super("");
        this.errors = errors;
    }

    public ApiValidationError getErrors() {
        return errors;
    }

    public void setErrors(ApiValidationError errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ApiValidationException{" +
                "errors=" + errors +
                '}';
    }
}
