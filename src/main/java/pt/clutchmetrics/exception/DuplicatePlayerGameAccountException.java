package pt.clutchmetrics.exception;

public class DuplicatePlayerGameAccountException extends RuntimeException {

    public DuplicatePlayerGameAccountException(String message) {
        super(message);
    }
}