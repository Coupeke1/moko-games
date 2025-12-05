package be.kdg.team22.communicationservice.api.chat.models;

import be.kdg.team22.communicationservice.domain.chat.ChatMessage;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        String senderId,
        String content,
        Instant timestamp
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId().value(),
                message.getSenderId(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}