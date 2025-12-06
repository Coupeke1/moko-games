package be.kdg.team22.communicationservice.infrastructure.messaging.events.session;

public record LobbyInviteEvent(
        String lobbyId,
        String inviterId,
        String inviterName,
        String targetUserId,
        String gameName
) {
}