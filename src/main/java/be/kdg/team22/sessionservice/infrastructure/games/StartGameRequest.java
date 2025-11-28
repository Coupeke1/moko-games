package be.kdg.team22.sessionservice.infrastructure.games;

import be.kdg.team22.sessionservice.domain.lobby.settings.GameSettings;

import java.util.UUID;

public record StartGameRequest(
        UUID lobbyId,
        UUID gameId,
        java.util.List<UUID> players,
        GameSettings settings,
        boolean aiPlayer
) {
}