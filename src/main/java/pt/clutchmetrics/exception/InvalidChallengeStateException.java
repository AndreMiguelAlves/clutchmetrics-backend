package pt.clutchmetrics.exception;

public class InvalidChallengeStateException
        extends RuntimeException {

    public InvalidChallengeStateException(
            String message) {

        super(message);
    }
}