package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooManyPlayersException extends RuntimeException {
    public TooManyPlayersException() {
        super("There are too many players in this lobby");
    }
}