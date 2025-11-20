package be.kdg.team22.sessionservice.domain.lobby.settings;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record TicTacToeSettings(int boardSize) implements GameSettings {

    public TicTacToeSettings {
        if (boardSize < 3) {
            throw new IllegalArgumentException("boardSize must be >= 3");
        }
    }
}