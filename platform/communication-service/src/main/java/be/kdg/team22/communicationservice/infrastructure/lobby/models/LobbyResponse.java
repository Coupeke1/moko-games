package be.kdg.team22.communicationservice.infrastructure.lobby.models;

import java.util.Set;
import java.util.UUID;

public record LobbyResponse(
        UUID id,
        UUID ownerId,
        Set<PlayerModel> players
) {
}