package application.exception;

import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;

public class BadRequestException extends RuntimeException {

    private ApiError errors;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(ApiValidationError error) {
        super("");
        this.errors = new ApiError(error);
    }

    public ApiError getErrors() {
        return errors;
    }

    public void setErrors(ApiError errors) {
        this.errors = errors;
    }
}
