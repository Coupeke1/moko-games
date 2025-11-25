package be.kdg.team22.gamesservice.domain.game.exceptions;

public class PlayersListEmptyException extends RuntimeException {
    public PlayersListEmptyException() {
        super("Players list cannot be empty");
    }
}
