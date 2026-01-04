package be.kdg.team22.communicationservice.api.chat.models.message;

import be.kdg.team22.communicationservice.domain.chat.message.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageModel(UUID id, UUID senderId,
                           String content,
                           Instant timestamp) {
    public static MessageModel from(Message message) {
        return new MessageModel(message.id().value(), message.senderId().value(), message.content(), message.timestamp());
    }
}