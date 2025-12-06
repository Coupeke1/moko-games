package be.kdg.team22.communicationservice.infrastructure.messaging.events.social;

import java.util.UUID;

public record FriendRequestReceivedEvent(
        UUID senderId,
        String senderName,
        UUID targetUserId
) {
}