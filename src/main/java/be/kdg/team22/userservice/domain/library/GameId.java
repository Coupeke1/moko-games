package be.kdg.team22.userservice.domain.library;

import be.kdg.team22.userservice.domain.library.exceptions.GameNotFoundException;

import java.util.UUID;

public record GameId(UUID value) {
    public static GameId create(String value) {
        return new GameId(UUID.fromString(value));
    }

    public static GameId from(UUID value) {
        return new GameId(value);
    }

    public GameNotFoundException notFound() {
        throw new GameNotFoundException(this);
    }
}