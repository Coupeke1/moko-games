package be.kdg.team22.socialservice.infrastructure.messaging.events;

import java.util.UUID;

public record FriendRequestAcceptedEvent(
        UUID senderId,
        String senderName,
        UUID targetUserId
) {
}

