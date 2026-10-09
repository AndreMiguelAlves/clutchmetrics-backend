package pt.clutchmetrics.exception;

public class InvalidCurrentPasswordException extends RuntimeException {

    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}