package be.kdg.team22.gamesservice.domain.game.exceptions;

import be.kdg.team22.gamesservice.domain.game.GameId;

public class DuplicateGameException extends RuntimeException {
    public DuplicateGameException(GameId id) {
        super(String.format("A game with id '%s' already exists", id.value()));
    }
}
