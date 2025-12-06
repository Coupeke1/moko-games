package be.kdg.team22.communicationservice.infrastructure.messaging.events.social;

public record FriendRequestAcceptedEvent(
        String senderId,
        String senderName,
        String targetUserId
) {
}