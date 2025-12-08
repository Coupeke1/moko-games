package be.kdg.team22.checkersservice.domain.player.exceptions;

public class PlayerIdentityMismatchException extends RuntimeException {
    public PlayerIdentityMismatchException() {
        super("Provided Jwt token does not match Player in Model");
    }
}