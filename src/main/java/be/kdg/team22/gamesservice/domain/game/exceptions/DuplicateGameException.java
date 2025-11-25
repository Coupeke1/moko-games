package be.kdg.team22.gamesservice.domain.game.exceptions;

import be.kdg.team22.gamesservice.domain.game.GameId;

public class DuplicateGameException extends RuntimeException {
    public DuplicateGameException(GameId id) {
        super("A game with id '%s' already exists".formatted(id.value()));
    }
}
