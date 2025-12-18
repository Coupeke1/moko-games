package be.kdg.team22.checkersservice.domain.game.exceptions;

public class BotMoveRequestFailedException extends RuntimeException {
    public BotMoveRequestFailedException(String client, String message) {
        super(client + " bot-service error: " + message);
    }
}
