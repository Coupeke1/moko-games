package be.kdg.team22.userservice.domain.achievement.exceptions;

import be.kdg.team22.userservice.domain.library.GameId;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(GameId id) {
        super(String.format("Game with id '%s' was not found", id.value()));
    }

    public GameNotFoundException(String name) {
        super(String.format("Game with name '%s' was not found", name));
    }
}