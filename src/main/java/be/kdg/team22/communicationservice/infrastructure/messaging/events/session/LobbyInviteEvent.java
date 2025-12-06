package be.kdg.team22.communicationservice.infrastructure.messaging.events.session;

import java.util.UUID;

public record LobbyInviteEvent(
        UUID lobbyId,
        UUID inviterId,
        String inviterName,
        UUID targetUserId,
        String gameName
) {
}