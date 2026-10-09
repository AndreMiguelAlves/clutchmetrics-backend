package pt.clutchmetrics.exception;

public class ExpiredVerificationTokenException extends RuntimeException {

    public ExpiredVerificationTokenException(String message) {
        super(message);
    }
}