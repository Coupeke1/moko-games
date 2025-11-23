package be.kdg.team22.sessionservice.domain.player;

import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record PlayerName(String value) {

    public PlayerName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PlayerName cannot be null or blank");
        }
        value = value.trim();
    }

    public static PlayerName create(String value) {
        return new PlayerName(value);
    }

    public static PlayerName from(String value) {
        return new PlayerName(value);
    }

    public PlayerNotFoundException notFound() {
        throw new PlayerNotFoundException(value);
    }

    @Override
    public String toString() {
        return value;
    }
}