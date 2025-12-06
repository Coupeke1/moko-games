package be.kdg.team22.communicationservice.infrastructure.messaging.events.chat;

import java.util.UUID;

public record DirectMessageReceivedEvent(
        UUID senderId,
        String senderName,
        UUID recipientId,
        String messagePreview,
        UUID channelId
) {
}