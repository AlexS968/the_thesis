package application.exception;

import application.exception.apierror.ApiError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger =
            LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ApiError> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest req) {
        logger.error(ex.getMessage());
        return handleExceptionInternal(ex, null, HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(value = {UserUnauthorizedException.class})
    protected ResponseEntity<ApiError> handleUserNotAuthorizedException(
            UserUnauthorizedException ex, WebRequest req) {
        logger.error(ex.getMessage());
        return handleExceptionInternal(ex, null, HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(value = {ApiValidationException.class})
    protected ResponseEntity<ApiError> handleApiValidationException(
            ApiValidationException ex, WebRequest req) {
        logger.warn(ex.getErrors().toString() + ", " + ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(ex.getErrors()), HttpStatus.OK, req);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<ApiError> handleBadRequestException(
            BadRequestException ex, WebRequest req) {
        logger.error(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(ex.getMessage()), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ApiError> handleGenericException(
            Exception ex, WebRequest req) {
        logger.error(ex.getMessage());
        return handleExceptionInternal(ex, null, HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    protected ResponseEntity<ApiError> handleExceptionInternal(
            Exception ex, ApiError body, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, null, status);
    }
}
