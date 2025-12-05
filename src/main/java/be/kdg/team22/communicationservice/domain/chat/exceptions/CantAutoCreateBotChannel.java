package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class CantAutoCreateBotChannel extends RuntimeException {
    public CantAutoCreateBotChannel(String message) {
        super(message);
    }
}
