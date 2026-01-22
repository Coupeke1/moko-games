package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.game.exceptions.GameIdException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameId(UUID value) {
    public GameId {
        if (value == null)
            throw new GameIdException();
    }

    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId fromString(String id) {
        return new GameId(UUID.fromString(id));
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }
}