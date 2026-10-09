package pt.clutchmetrics.exception;

public class InvalidVerificationTokenException extends RuntimeException {

    public InvalidVerificationTokenException(String message) {
        super(message);
    }
}