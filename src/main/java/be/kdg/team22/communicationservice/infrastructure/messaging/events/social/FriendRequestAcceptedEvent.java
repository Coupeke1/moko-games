package be.kdg.team22.communicationservice.infrastructure.messaging.events.social;

import java.util.UUID;

public record FriendRequestAcceptedEvent(
        UUID senderId,
        String senderName,
        UUID targetUserId
) {
}