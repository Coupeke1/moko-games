package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class BotMessageInLobbyException extends RuntimeException {
    public BotMessageInLobbyException(String message) {
        super(message);
    }
}
