package be.kdg.team22.gamesservice.domain.game.exceptions;

public class EngineNotReachableException extends RuntimeException {
    public EngineNotReachableException(String url) {
        super("External game engine at '%s' is unreachable".formatted(url));
    }
}