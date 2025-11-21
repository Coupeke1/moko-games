package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class PlayerRolesException extends RuntimeException {
    public PlayerRolesException() {
        super("All players should have a different role");
    }
}