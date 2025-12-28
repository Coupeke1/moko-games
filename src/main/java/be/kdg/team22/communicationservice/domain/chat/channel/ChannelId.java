package be.kdg.team22.communicationservice.domain.chat.channel;

import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.ChannelNotFoundException;

import java.util.Objects;
import java.util.UUID;

public record ChannelId(UUID value) {
    public static ChannelId create() {
        return new ChannelId(UUID.randomUUID());
    }

    public static ChannelId from(UUID value) {
        return new ChannelId(Objects.requireNonNull(value));
    }

    public ChannelNotFoundException notFound() {
        return new ChannelNotFoundException(this);
    }
}