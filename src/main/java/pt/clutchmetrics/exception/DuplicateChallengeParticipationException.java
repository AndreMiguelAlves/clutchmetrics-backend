package pt.clutchmetrics.exception;

public class DuplicateChallengeParticipationException
        extends RuntimeException {

    public DuplicateChallengeParticipationException(
            String message) {

        super(message);
    }
}