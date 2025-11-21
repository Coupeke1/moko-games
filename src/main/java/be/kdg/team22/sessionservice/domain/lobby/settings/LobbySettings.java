package be.kdg.team22.sessionservice.domain.lobby.settings;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record LobbySettings(GameSettings gameSettings, int maxPlayers) {

    public LobbySettings {
        if (maxPlayers <= 0)
            throw new IllegalArgumentException("maxPlayers > 0");

        if (gameSettings == null)
            throw new IllegalArgumentException("gameSettings cannot be null");
    }
}