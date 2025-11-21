package be.kdg.team22.sessionservice.domain.lobby.settings;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record CheckersSettings(int boardSize, boolean flyingKings)
        implements GameSettings {

    public CheckersSettings {
        if (boardSize < 8) {
            throw new IllegalArgumentException("boardSize must be >= 8 for checkers");
        }
    }
}