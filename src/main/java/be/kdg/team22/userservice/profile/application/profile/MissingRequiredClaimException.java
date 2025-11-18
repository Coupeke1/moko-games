package be.kdg.team22.userservice.profile.application.profile;

public class MissingRequiredClaimException extends IllegalStateException {
    public MissingRequiredClaimException(String message) {
        super(message);
    }
}
