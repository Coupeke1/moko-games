package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class BotServiceNotReachableException extends RuntimeException {
    public BotServiceNotReachableException(String url) {
        super(String.format("Bot-service is not reachable at '%s'", url));
    }
}
