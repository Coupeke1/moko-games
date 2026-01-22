package be.kdg.team22.socialservice.infrastructure.messaging.events;

import java.util.UUID;

public record FriendRequestReceivedEvent(
        UUID senderId,
        String senderName,
        UUID targetUserId
) {
}

