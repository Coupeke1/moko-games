package be.kdg.team22.sessionservice.domain.lobby.settings;

import java.util.Map;

public record LobbySettings(
        int maxPlayers,
        Map<String, Object> gameSettings
) {
    public LobbySettings {
        gameSettings = (gameSettings == null) ? Map.of() : Map.copyOf(gameSettings);
    }
}