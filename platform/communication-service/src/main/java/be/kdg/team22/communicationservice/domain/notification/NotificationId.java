package be.kdg.team22.communicationservice.domain.notification;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record NotificationId(UUID value) {
    public NotificationId(UUID value) {
        this.value = value;
    }

    public static NotificationId create() {
        return new NotificationId(UUID.randomUUID());
    }

    public static NotificationId from(final UUID value) {
        return new NotificationId(value);
    }
}