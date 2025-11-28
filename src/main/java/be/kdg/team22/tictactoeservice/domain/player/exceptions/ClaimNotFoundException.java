package be.kdg.team22.tictactoeservice.domain.player.exceptions;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String claim) {
        super(String.format("Claim '%s' was not found", claim));
    }

    public static ClaimNotFoundException sub() {
        return new ClaimNotFoundException("sub");
    }
}