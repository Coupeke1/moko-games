package be.kdg.team22.sessionservice.api.lobby.models;

import java.util.Map;

public record UpdateLobbySettingsModel(
        Integer maxPlayers,
        Map<String, Object> settings
) {
}