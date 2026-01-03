package be.kdg.team22.userservice.domain.profile;

import java.util.UUID;

public record BotId(UUID value) {
    public static BotId create(String value) {
        return new BotId(UUID.fromString(value));
    }

    public static BotId create() {
        return new BotId(UUID.randomUUID());
    }

    public static BotId from(UUID value) {
        return new BotId(value);
    }
}