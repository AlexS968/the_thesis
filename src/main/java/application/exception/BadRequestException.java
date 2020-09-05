package application.exception;

import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;

public class BadRequestException extends RuntimeException {

    private ApiError error;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(ApiError error, String message) {
        super(message);
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }
}
