package pt.clutchmetrics.exception;

public class ExpiredPasswordResetTokenException extends RuntimeException {

    public ExpiredPasswordResetTokenException(String message) {
        super(message);
    }
}