package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.domain.player.PlayerId;

import java.util.UUID;

public record LobbyMessage(UUID userId,
                           String queue,
                           LobbyModel payload,
                           String reason) {

    public static LobbyMessage of(PlayerId playerId, LobbyModel payload) {
        return new LobbyMessage(playerId.value(), "lobby", payload, null);
    }

    public static LobbyMessage withReason(PlayerId playerId, RemovalReason reason) {
        return new LobbyMessage(playerId.value(), "lobby", null, reason.value());
    }
}
