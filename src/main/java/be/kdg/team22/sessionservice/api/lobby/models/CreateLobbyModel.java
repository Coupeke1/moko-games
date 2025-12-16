package be.kdg.team22.sessionservice.api.lobby.models;

import java.util.Map;
import java.util.UUID;

public record CreateLobbyModel(UUID gameId,
                               Integer maxPlayers,
                               Map<String, Object> settings) {
}