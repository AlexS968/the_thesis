package application.exception;

public class EntNotFoundException extends RuntimeException {

    public EntNotFoundException() {
        super();
    }

    public EntNotFoundException(String message) {
        super(message);
    }
}
