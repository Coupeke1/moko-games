package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class CannotCreateBotException extends RuntimeException {
    public CannotCreateBotException() {
        super("Could not create a bot");
    }
}
