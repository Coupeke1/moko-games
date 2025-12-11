package be.kdg.team22.tictactoeservice.domain.player.exceptions;

public class PlayerNotInThisGameException extends RuntimeException {
    public PlayerNotInThisGameException() {
        super("Provided player is not a participant in this game");
    }
}
