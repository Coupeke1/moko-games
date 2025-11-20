package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record LobbyResponseModel(UUID id,
                                 UUID gameId,
                                 UUID ownerId,
                                 Set<UUID> players,
                                 LobbyStatus status,
                                 Instant createdAt) {
}