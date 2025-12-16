package be.kdg.team22.userservice.domain.achievement.exceptions;

public class GameIdNullException extends RuntimeException {
    public GameIdNullException() {
        super("GameId cannot be null");
    }
}