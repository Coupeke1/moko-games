package be.kdg.team22.userservice.domain.profile.exceptions;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String claim) {
        super(String.format("Claim '%s' was not found", claim));
    }

    public static ClaimNotFoundException sub() {
        return new ClaimNotFoundException("sub");
    }

    public static ClaimNotFoundException username() {
        return new ClaimNotFoundException("username");
    }
}