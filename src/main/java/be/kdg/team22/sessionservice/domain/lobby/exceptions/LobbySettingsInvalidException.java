package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class LobbySettingsInvalidException extends RuntimeException {
    public LobbySettingsInvalidException() {
        super("Lobby settings cannot be null");
    }

    public LobbySettingsInvalidException(String message) {
        super(message);
    }
}
