package be.kdg.team22.sessionservice.infrastructure.messaging.events;

import java.util.UUID;

public record LobbyInviteEvent(
        String gameName,
        UUID targetUserId,
        String inviterName,
        UUID inviterId,
        UUID lobbyId
) {
}