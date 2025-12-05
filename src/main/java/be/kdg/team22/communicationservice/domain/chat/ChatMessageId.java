package be.kdg.team22.communicationservice.domain.chat;

import java.util.Objects;
import java.util.UUID;

public record ChatMessageId(UUID value) {
    public static ChatMessageId create() {
        return new ChatMessageId(UUID.randomUUID());
    }

    public static ChatMessageId from(UUID value) {
        return new ChatMessageId(Objects.requireNonNull(value));
    }
}