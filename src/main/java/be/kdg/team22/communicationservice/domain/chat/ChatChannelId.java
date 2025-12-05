package be.kdg.team22.communicationservice.domain.chat;

import java.util.Objects;
import java.util.UUID;

public record ChatChannelId(UUID value) {
    public static ChatChannelId create() {
        return new ChatChannelId(UUID.randomUUID());
    }

    public static ChatChannelId from(UUID value) {
        return new ChatChannelId(Objects.requireNonNull(value));
    }
}