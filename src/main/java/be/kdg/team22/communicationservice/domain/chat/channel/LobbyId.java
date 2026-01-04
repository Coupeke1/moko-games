package be.kdg.team22.communicationservice.domain.chat.channel;

import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.ChannelNotFoundException;

import java.util.Objects;
import java.util.UUID;

public record LobbyId(UUID value) {
    public static LobbyId create() {
        return new LobbyId(UUID.randomUUID());
    }

    public static LobbyId from(UUID value) {
        return new LobbyId(Objects.requireNonNull(value));
    }

    public ChannelNotFoundException notFound() {
        return new ChannelNotFoundException(ChannelId.from(this.value));
    }
}