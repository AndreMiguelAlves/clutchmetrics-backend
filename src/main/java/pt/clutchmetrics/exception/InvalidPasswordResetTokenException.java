package pt.clutchmetrics.exception;

public class InvalidPasswordResetTokenException extends RuntimeException {

    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
}