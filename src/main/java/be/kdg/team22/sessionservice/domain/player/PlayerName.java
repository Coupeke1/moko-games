package be.kdg.team22.sessionservice.domain.player;

import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record PlayerName(String value) {
    public static PlayerName create(String value) {
        return new PlayerName(value.trim());
    }

    public static PlayerName from(String value) {
        return new PlayerName(value);
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.trim();
    }
}