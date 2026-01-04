package be.kdg.team22.communicationservice.domain.chat.message;

import java.util.UUID;

public record MessageId(UUID value) {
    public static MessageId create() {
        return new MessageId(UUID.randomUUID());
    }

    public static MessageId from(UUID value) {
        return new MessageId(value);
    }
}