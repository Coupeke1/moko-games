package be.kdg.team22.communicationservice.domain.chat;

import java.util.UUID;

public record ReferenceId(String value) {
    public static ReferenceId create() {
        return new ReferenceId(UUID.randomUUID().toString());
    }

    public static ReferenceId from(UUID value) {
        return new ReferenceId(value.toString());
    }

    public static ReferenceId from(UserId id) {
        return new ReferenceId(id.value().toString());
    }
}