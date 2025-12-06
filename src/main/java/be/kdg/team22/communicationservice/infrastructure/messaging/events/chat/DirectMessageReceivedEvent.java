package be.kdg.team22.communicationservice.infrastructure.messaging.events.chat;

public record DirectMessageReceivedEvent(
        String senderId,
        String senderName,
        String recipientId,
        String messagePreview,
        String channelId
) {
}