package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class CantCreateBotException extends RuntimeException {
    public CantCreateBotException() {
        super("Could't create a bot");
    }
}
