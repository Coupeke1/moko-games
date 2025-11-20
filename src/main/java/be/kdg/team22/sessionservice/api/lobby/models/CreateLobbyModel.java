package be.kdg.team22.sessionservice.api.lobby.models;

import java.util.UUID;

public record CreateLobbyModel(
        UUID gameId,
        Integer maxPlayers,
        Integer boardSize
) {
}