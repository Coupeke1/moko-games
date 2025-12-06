package be.kdg.team22.communicationservice.infrastructure.messaging.events.session;

public record PlayerJoinedLobbyEvent(
        String lobbyId,
        String playerId,
        String playerName,
        String hostUserId
) {
}