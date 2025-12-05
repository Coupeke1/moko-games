package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class AiMessageInLobbyException extends RuntimeException {
    public AiMessageInLobbyException(String message) {
        super(message);
    }
}
