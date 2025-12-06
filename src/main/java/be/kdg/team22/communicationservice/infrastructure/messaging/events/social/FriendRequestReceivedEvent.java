package be.kdg.team22.communicationservice.infrastructure.messaging.events.social;

public record FriendRequestReceivedEvent(
        String senderId,
        String senderName,
        String targetUserId
) {
}