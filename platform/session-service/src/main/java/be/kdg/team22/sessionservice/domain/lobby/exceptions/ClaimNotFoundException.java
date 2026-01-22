package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String claim) {
        super(String.format("Claim '%s' was not found", claim));
    }
}
