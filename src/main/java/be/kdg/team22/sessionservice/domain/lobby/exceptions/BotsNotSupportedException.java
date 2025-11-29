package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class BotsNotSupportedException extends RuntimeException {
    public BotsNotSupportedException() {
        super("Bots are not supported in this game");
    }
}
