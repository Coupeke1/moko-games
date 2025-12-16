package be.kdg.team22.sessionservice.api.lobby.models;

import java.util.Map;

public record UpdateLobbySettingsModel(
        int maxPlayers,
        Map<String, Object> settings) {
}