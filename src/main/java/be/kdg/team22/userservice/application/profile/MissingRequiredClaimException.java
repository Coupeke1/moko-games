package be.kdg.team22.userservice.application.profile;

public class MissingRequiredClaimException extends IllegalStateException {
    public MissingRequiredClaimException(String message) {
        super(message);
    }
}
