package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameStartEndpointInvalidException extends RuntimeException {
    public GameStartEndpointInvalidException() {
        super("Start endpoint cannot be empty");
    }
}