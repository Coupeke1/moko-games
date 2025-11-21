package be.kdg.team22.sessionservice.api.lobby.models;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record LobbyInviteModel(
        UUID lobbyId,
        UUID gameId,
        String gameName,
        UUID ownerId,
        String ownerUsername,
        Set<PlayerSummaryModel> players,
        int maxPlayers,
        String status,
        Instant createdAt
) {
}
