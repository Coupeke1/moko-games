package be.kdg.team22.gamesservice.domain.game.exceptions;

public class DuplicateGameNameException extends RuntimeException {
    public DuplicateGameNameException(String name) {
        super(String.format("A game with name '%s' already exists", name));
    }
}
