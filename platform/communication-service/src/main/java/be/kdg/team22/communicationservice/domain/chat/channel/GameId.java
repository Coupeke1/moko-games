package be.kdg.team22.communicationservice.domain.chat.channel;

import java.util.Objects;
import java.util.UUID;

public record GameId(UUID value) {
    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId from(UUID value) {
        return new GameId(Objects.requireNonNull(value));
    }
}