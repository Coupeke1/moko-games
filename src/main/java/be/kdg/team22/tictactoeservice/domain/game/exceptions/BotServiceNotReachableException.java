package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class BotServiceNotReachableException extends RuntimeException {
    public BotServiceNotReachableException(String client) {
        super(client + " bot-service not reachable");
    }
}
