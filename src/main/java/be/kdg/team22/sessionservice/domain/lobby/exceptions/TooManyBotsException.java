package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooManyBotsException extends RuntimeException {
    public TooManyBotsException() {
        super("This lobby only supports one bot");
    }
}