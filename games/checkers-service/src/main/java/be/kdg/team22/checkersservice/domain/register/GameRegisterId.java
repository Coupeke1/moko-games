package be.kdg.team22.checkersservice.domain.register;

import be.kdg.team22.checkersservice.domain.game.exceptions.GameIdException;
import be.kdg.team22.checkersservice.domain.game.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameRegisterId(UUID value) {
    public GameRegisterId {
        if (value == null)
            throw new GameIdException();
    }

    public static GameRegisterId create() {
        return new GameRegisterId(UUID.randomUUID());
    }

    public static GameRegisterId fromString(String id) {
        return new GameRegisterId(UUID.fromString(id));
    }
    public static GameRegisterId fromUuid(UUID uuid) {
        return new GameRegisterId(uuid);
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }
}