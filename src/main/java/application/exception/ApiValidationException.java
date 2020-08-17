package application.exception;

import application.exception.apierror.ApiValidationError;

public class ApiValidationException extends RuntimeException {

    ApiValidationError errors;

    public ApiValidationException(String message) {
        super(message);
    }

    public ApiValidationException(ApiValidationError errors, String message) {
        super(message);
        this.errors = errors;
    }

    public ApiValidationError getErrors() {
        return errors;
    }

    public void setErrors(ApiValidationError errors) {
        this.errors = errors;
    }
}
